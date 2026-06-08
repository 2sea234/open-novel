package com.kxhy.client;

import com.kxhy.config.AdminSystemProperties;
import com.opennovel.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.*;
import java.util.Collections;
import java.util.List;

/**
 * 根据 adminId 去 admin-system 查询权限码。
 */
@Component
@RequiredArgsConstructor
public class AdminSystemPropertiesClient {

    private final RestTemplate restTemplate;
    private final AdminSystemProperties adminSystemProperties;

    public List<String> queryPermissionCodesByAdminId(Long adminId) {
        if (adminId == null) {
            return Collections.emptyList();
        }

        String url = adminSystemProperties.getBaseUrl() + "/adminSystem/user/permission";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Admin-Id", String.valueOf(adminId));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Result<List<String>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Result<List<String>>>() {}
        );

        Result<List<String>> body = response.getBody();

        if (body == null|| body.getCode() == null || body.getCode() != 200 || body.getData() == null) {
            return Collections.emptyList();
        }

        return body.getData();

    }

}
