package com.kxhy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kxhy.config.ComfyuiProperties;
import com.kxhy.domain.dto.CoverGenerateResponse;
import com.kxhy.domain.dto.CoverGenerateRequest;
import com.kxhy.support.ByteArrayMultipartFile;
import com.opennovel.common.domain.vo.FileUploadVO;
import com.opennovel.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ComfyuiCoverService {

    private final ComfyuiProperties properties;
    private final ObjectMapper objectMapper;
    private final MinioImageClient minioImageClient;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public CoverGenerateResponse generateCover(CoverGenerateRequest request) {
        try {
            ObjectNode workflow = loadWorkflow();

            String positivePrompt = buildPositivePrompt(request);
            String negativePrompt = buildNegativePrompt(request);

            applyWorkflowParams(workflow, positivePrompt, negativePrompt);

            String promptId = submitPrompt(workflow);

            ImageInfo imageInfo = waitForImage(promptId);

            byte[] imageBytes = downloadImageBytes(imageInfo);

            MultipartFile file = new ByteArrayMultipartFile(
                    "file",
                    imageInfo.filename(),
                    "image/png",
                    imageBytes
            );

            FileUploadVO uploadVO = minioImageClient.uploadImage(file);

            return new CoverGenerateResponse(
                    promptId,
                    imageInfo.filename(),
                    imageInfo.subfolder(),
                    imageInfo.type(),
                    uploadVO.getUrl()
            );
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(500, "调用 ComfyUI 生成封面失败：" + e.getMessage());
        }
    }

    private ObjectNode loadWorkflow() throws Exception {
        ClassPathResource resource = new ClassPathResource(properties.getWorkflowPath());

        if (!resource.exists()) {
            throw new BizException(500, "ComfyUI 工作流文件不存在：" + properties.getWorkflowPath());
        }

        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode jsonNode = objectMapper.readTree(inputStream);
            if (!(jsonNode instanceof ObjectNode objectNode)) {
                throw new BizException(500, "ComfyUI 工作流 JSON 格式错误");
            }
            return objectNode.deepCopy();
        }
    }

    private String buildPositivePrompt(CoverGenerateRequest request) {
        if (request != null && request.getPrompt() != null && !request.getPrompt().isBlank()) {
            return request.getPrompt();
        }

        String title = request == null ? "" : nullToEmpty(request.getTitle());
        String summary = request == null ? "" : nullToEmpty(request.getSummary());

        return """
                cinematic fantasy novel cover, vertical book cover, no text, no logo,
                highly detailed, realistic, atmospheric, dramatic volumetric lighting,
                beautiful composition, professional cover art,
                novel title theme: %s,
                story summary: %s
                """.formatted(title, summary);
    }

    private String buildNegativePrompt(CoverGenerateRequest request) {
        if (request != null && request.getNegativePrompt() != null && !request.getNegativePrompt().isBlank()) {
            return request.getNegativePrompt();
        }

        return "text, logo, watermark, blurry, low quality, bad anatomy, bad hands, extra fingers, deformed face, ugly";
    }

    private void applyWorkflowParams(ObjectNode workflow, String positivePrompt, String negativePrompt) {
        ObjectNode checkpointNode = findFirstNodeByClassType(workflow, "CheckpointLoaderSimple");
        ObjectNode latentNode = findFirstNodeByClassType(workflow, "EmptyLatentImage");
        ObjectNode samplerNode = findFirstNodeByClassType(workflow, "KSampler");

        if (checkpointNode == null) {
            throw new BizException(500, "工作流缺少 CheckpointLoaderSimple 节点");
        }
        if (latentNode == null) {
            throw new BizException(500, "工作流缺少 EmptyLatentImage 节点");
        }
        if (samplerNode == null) {
            throw new BizException(500, "工作流缺少 KSampler 节点");
        }

        ObjectNode checkpointInputs = getInputs(checkpointNode);
        checkpointInputs.put("ckpt_name", properties.getCheckpointName());

        ObjectNode latentInputs = getInputs(latentNode);
        latentInputs.put("width", properties.getWidth());
        latentInputs.put("height", properties.getHeight());
        latentInputs.put("batch_size", 1);

        ObjectNode samplerInputs = getInputs(samplerNode);
        samplerInputs.put("seed", ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE));
        samplerInputs.put("steps", properties.getSteps());
        samplerInputs.put("cfg", properties.getCfg());
        samplerInputs.put("sampler_name", properties.getSamplerName());
        samplerInputs.put("scheduler", properties.getScheduler());
        samplerInputs.put("denoise", 1.0);

        applyPromptText(workflow, positivePrompt, negativePrompt);
    }

    private void applyPromptText(ObjectNode workflow, String positivePrompt, String negativePrompt) {
        ObjectNode firstClipTextNode = null;
        ObjectNode secondClipTextNode = null;

        Iterator<Map.Entry<String, JsonNode>> fields = workflow.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            JsonNode node = entry.getValue();

            if (!node.isObject()) {
                continue;
            }

            String classType = node.path("class_type").asText();

            if ("CLIPTextEncode".equals(classType)) {
                if (firstClipTextNode == null) {
                    firstClipTextNode = (ObjectNode) node;
                } else if (secondClipTextNode == null) {
                    secondClipTextNode = (ObjectNode) node;
                }
            }
        }

        if (firstClipTextNode == null || secondClipTextNode == null) {
            throw new BizException(500, "工作流缺少正向或负向 CLIPTextEncode 节点");
        }

        ObjectNode firstInputs = getInputs(firstClipTextNode);
        ObjectNode secondInputs = getInputs(secondClipTextNode);

        String firstText = firstInputs.path("text").asText("");
        String secondText = secondInputs.path("text").asText("");

        boolean firstLooksNegative = looksLikeNegativePrompt(firstText);
        boolean secondLooksNegative = looksLikeNegativePrompt(secondText);

        if (firstLooksNegative && !secondLooksNegative) {
            firstInputs.put("text", negativePrompt);
            secondInputs.put("text", positivePrompt);
        } else {
            firstInputs.put("text", positivePrompt);
            secondInputs.put("text", negativePrompt);
        }
    }

    private boolean looksLikeNegativePrompt(String text) {
        if (text == null) {
            return false;
        }

        String lowerText = text.toLowerCase();

        return lowerText.contains("watermark")
                || lowerText.contains("bad anatomy")
                || lowerText.contains("bad hands")
                || lowerText.contains("extra fingers")
                || lowerText.contains("low quality")
                || lowerText.contains("deformed");
    }

    private ObjectNode findFirstNodeByClassType(ObjectNode workflow, String classType) {
        Iterator<Map.Entry<String, JsonNode>> fields = workflow.fields();

        while (fields.hasNext()) {
            JsonNode node = fields.next().getValue();

            if (!node.isObject()) {
                continue;
            }

            if (classType.equals(node.path("class_type").asText())) {
                return (ObjectNode) node;
            }
        }

        return null;
    }

    private ObjectNode getInputs(ObjectNode node) {
        JsonNode inputsNode = node.get("inputs");

        if (!(inputsNode instanceof ObjectNode inputs)) {
            throw new BizException(500, "ComfyUI 节点 inputs 格式错误");
        }

        return inputs;
    }

    private String submitPrompt(ObjectNode workflow) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.set("prompt", workflow);
        body.put("client_id", UUID.randomUUID().toString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(normalizeBaseUrl() + "/prompt"))
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new BizException(500, "提交 ComfyUI 任务失败：" + response.body());
        }

        JsonNode responseJson = objectMapper.readTree(response.body());
        String promptId = responseJson.path("prompt_id").asText();

        if (promptId == null || promptId.isBlank()) {
            throw new BizException(500, "ComfyUI 未返回 prompt_id：" + response.body());
        }

        return promptId;
    }

    private ImageInfo waitForImage(String promptId) throws Exception {
        long deadline = System.currentTimeMillis() + properties.getTimeoutSeconds() * 1000;

        while (System.currentTimeMillis() < deadline) {
            ImageInfo imageInfo = queryImageFromHistory(promptId);

            if (imageInfo != null) {
                return imageInfo;
            }

            Thread.sleep(properties.getPollIntervalMillis());
        }

        throw new BizException(504, "ComfyUI 生成封面超时");
    }

    private ImageInfo queryImageFromHistory(String promptId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(normalizeBaseUrl() + "/history/" + promptId))
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            return null;
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode promptHistory = root.get(promptId);

        if (promptHistory == null || promptHistory.isMissingNode()) {
            return null;
        }

        JsonNode outputs = promptHistory.get("outputs");

        if (outputs == null || !outputs.isObject()) {
            return null;
        }

        Iterator<Map.Entry<String, JsonNode>> outputFields = outputs.fields();

        while (outputFields.hasNext()) {
            JsonNode output = outputFields.next().getValue();
            JsonNode images = output.get("images");

            if (images != null && images.isArray() && !images.isEmpty()) {
                JsonNode firstImage = images.get(0);

                String filename = firstImage.path("filename").asText();
                String subfolder = firstImage.path("subfolder").asText("");
                String type = firstImage.path("type").asText("output");

                if (filename != null && !filename.isBlank()) {
                    return new ImageInfo(filename, subfolder, type);
                }
            }
        }

        return null;
    }

    private String buildImageUrl(ImageInfo imageInfo) {
        String filename = URLEncoder.encode(imageInfo.filename(), StandardCharsets.UTF_8);
        String subfolder = URLEncoder.encode(imageInfo.subfolder(), StandardCharsets.UTF_8);
        String type = URLEncoder.encode(imageInfo.type(), StandardCharsets.UTF_8);

        return normalizeBaseUrl()
                + "/view?filename=" + filename
                + "&subfolder=" + subfolder
                + "&type=" + type;
    }

    private String normalizeBaseUrl() {
        String baseUrl = properties.getBaseUrl();

        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }

        return baseUrl;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private record ImageInfo(String filename, String subfolder, String type) {
    }

    private byte[] downloadImageBytes(ImageInfo imageInfo) throws Exception {
        String imageUrl = buildImageUrl(imageInfo);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(imageUrl))
                .timeout(Duration.ofSeconds(60))
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofByteArray()
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new BizException(500, "下载 ComfyUI 生成图片失败");
        }

        byte[] body = response.body();

        if (body == null || body.length == 0) {
            throw new BizException(500, "ComfyUI 生成图片为空");
        }

        return body;
    }

}
