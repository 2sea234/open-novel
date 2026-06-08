package com.kxhy.novel.controller;

import cn.dev33.satoken.exception.NotLoginException;
import com.kxhy.novel.annotation.OperationLog;
import com.kxhy.novel.common.converter.ApiResult;
import com.kxhy.novel.common.util.NetworkUtil;
import com.kxhy.novel.common.util.ApiResponseBuilder;
import com.kxhy.novel.domain.dto.UserDTO;
import com.kxhy.novel.domain.dto.UserRegisterDTO;
import com.kxhy.novel.domain.vo.UserRegisterResult;
import com.kxhy.novel.exception.BusinessException;
import com.kxhy.novel.service.UserService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Log4j2
@Tag(name = "认证管理", description = "用户认证相关接口")
@Validated
@RestController
@RequestMapping("user")
@RequiredArgsConstructor // 自动生成构造方法 (自动注入UserService)
public class LoginController {

    // http://localhost:8081/swagger-ui/index.html#/ 接口地址

    private final UserService userService;
    private final NetworkUtil networkUtil;
    private final ApiResponseBuilder apiResponseBuilder;




    /**
     * <span style="color: #e84393;">注册</span>
     * @param userDTO 用户信息
     * @return 注册结果
     */

    @Operation(summary = "用户注册", description = "注册新用户，成功后自动登录并返回访问令牌")
    @PostMapping("/register")
    @RateLimiter(name = "user-register", fallbackMethod = "registerRateLimitFallback")
    @OperationLog(module = "用户认证", type = "注册", description = "用户注册", recordParams = true, recordResult = true, recordTime = true)
    public ResponseEntity<ApiResult<UserRegisterResult>> register(@Valid @RequestBody UserRegisterDTO userDTO, HttpServletRequest request) {
        ResponseEntity<ApiResult<UserRegisterResult>> validateRegistration = validateRegistration(userDTO);

        String clientIP = networkUtil.getClientIP(request);
        log.info("用户注册请求 - 用户信息：{}", userDTO.getCode());
        log.info("用户注册请求 - 用户名：{}，IP：{}",userDTO.getUsername(), clientIP);

        // 判断用户是否存在
        if (validateRegistration != null) {
            return validateRegistration;
        }

        try {
            // 注册
            UserRegisterResult register = userService.register(userDTO);
            log.info("用户注册成功 - 用户名：{}", register.getUsername());
            // 返回结果
            return apiResponseBuilder.buildSuccessResponse(HttpStatus.OK, "注册成功", register);
        } catch (BusinessException e) {
            // 业务异常
            log.warn("用户注册业务异常 - 用户名：{}, 原因：{}", userDTO.getUsername(), e.getMessage());
            // 返回结果
            return apiResponseBuilder.buildErrorResponse(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户注册业务异常 - 用户名：{}", userDTO.getUsername(), e);
            return apiResponseBuilder.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "服务器异常");
        }
    }




    /**
     * <span style="color: #e84393;">登录</span>
     * @param userDTO 用户信息
     * @return 登录结果
     */
    @Operation(summary = "用户登录", description = "用户登录接口，验证用户名和密码后返回访问令牌")
    @PostMapping("/login")
    @RateLimiter(name = "user-login", fallbackMethod = "loginRateLimitFallback")
    @OperationLog(module = "用户认证", type = "登录", description = "用户登录", recordParams = true, recordResult = true, recordTime = true)
    public ResponseEntity<ApiResult<UserRegisterResult>> login(@Valid @RequestBody UserRegisterDTO userDTO, HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        if (userDTO.getUsername() != null) {
            log.info("用户登录请求 - 路径：{}，用户名：{}", requestPath, userDTO.getUsername());
        }
        log.info("用户登录请求 - 路径：{}，邮箱：{}", requestPath, userDTO.getEmail());
        try {
            UserRegisterResult longin = userService.login(userDTO);
            return apiResponseBuilder.buildSuccessResponse(HttpStatus.OK, "登录成功", longin);
        } catch (BusinessException e) {
            log.warn("用户登陆失败 - 用户名：{}， 原因：{}", userDTO.getUsername(), e.getMessage());
            return apiResponseBuilder.buildErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    /**
     * <span style="color: #e84393;">更新用户信息</span>
     * @param userDTO 用户信息
     * @return 响应
     */
    @Operation(summary = "更新用户信息", description = "更新当前用户的用户名")
    @PostMapping("/updateUserName")
    @OperationLog(module = "用户管理", type = "更新", description = "更新用户信息", recordParams = true, recordResult = true)
    public ResponseEntity<ApiResult<Object>> updateUserInfo(@Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO result = userService.updateUserInfo(userDTO);

            if (result != null) {
                log.info("更新用户信息成功 - 用户ID：{}，新用户名：{}",
                        result.getUserId(), result.getUsername());
                return apiResponseBuilder.buildSuccessResponse(
                        HttpStatus.OK, "更新成功", result);
            } else {
                return apiResponseBuilder.buildErrorResponse(
                        HttpStatus.BAD_REQUEST, "更新失败");
            }

        } catch (BusinessException e) {
            log.warn("更新用户信息业务异常 - 原因：{}", e.getMessage());
            return apiResponseBuilder.buildErrorResponse(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新用户信息异常", e);
            return apiResponseBuilder.buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR, "系统繁忙，请稍后重试");
        }
    }

    /**
     * 获取当前用户信息
     * @return 当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前已登录用户的详细信息")
    @GetMapping("/current-user")
    @OperationLog(module = "用户管理", type = "查询", description = "获取当前登录用户信息", recordParams = false, recordResult = true)
    public ResponseEntity<ApiResult<Object>> getCurrentUser() {
        try {

            Object currentUserInfo = userService.getCurrentUserInfo();
            log.info("获取当前用户信息成功");
            return apiResponseBuilder.buildSuccessResponse(HttpStatus.OK, "当前用户信息", currentUserInfo);
        } catch (BusinessException e) {

            log.warn("获取当前用户信息失败 - 原因：{}", e.getMessage());
//            HttpStatus status = e.getCode() == 401 ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR;
            return apiResponseBuilder.buildErrorResponse(e.getCode(), e.getMessage());
        } catch (Exception e) {

            log.error("获取当前用户异常", e);
            return apiResponseBuilder.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "系统繁忙，请稍后重试");
        }
    }

    /**
     * 退出登录
     * @return 退出结果
     */
    @Operation(summary = "用户退出登录", description = "退出当前登录用户，使token失效并记录退出时间")
    @PostMapping("/logout")
    @OperationLog(module = "用户认证", type = "登出", description = "用户退出登录", recordParams = false, recordResult = true)
    public ResponseEntity<ApiResult<Void>> logout() {
        try {

            Boolean result = userService.logout();
            if (result) {

                log.info("用户退出登录成功");
                return apiResponseBuilder.buildSuccessResponse(HttpStatus.OK, "退出成功", null);
            } else {

                log.warn("用户退出登录失败");
                return apiResponseBuilder.buildErrorResponse(HttpStatus.BAD_REQUEST, "退出失败");
            }
        } catch (NotLoginException e) {

            log.warn("用户退出登录失败 - 原因：{}", e.getMessage());
            return apiResponseBuilder.buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("用户退出登录异常", e);
            return apiResponseBuilder.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "系统繁忙，请稍后重试");
        }
    }


    /**
     * 注册限流
     * @param userDTO 用户信息
     * @param ex 限流异常
     * @return 响应
     */
    public ResponseEntity<ApiResult<UserRegisterResult>> registerRateLimitFallback(UserRegisterDTO userDTO, RequestNotPermitted ex) {
        log.warn("注册接口限流触发 - 用户名：{}", userDTO.getUsername());
        return apiResponseBuilder.buildRateLimitResponse();
    }

    /**
     * 登录限流
     * @param userDTO 用户信息
     * @param ex 限流异常
     * @return 响应
     */
    public ResponseEntity<ApiResult<UserRegisterResult>> loginRateLimitFallback(UserRegisterDTO userDTO, RequestNotPermitted ex) {
        String identifier = userDTO.getUsername() != null ? userDTO.getUsername() : userDTO.getEmail();
        log.warn("登录接口限流触发 - 用户标识：{}", identifier);
        return apiResponseBuilder.buildRateLimitResponse();
    }

    /**
     * 验证用户注册信息
     * @param userDTO 用户信息
     * @return 验证结果
     */
    private ResponseEntity<ApiResult<UserRegisterResult>> validateRegistration(UserRegisterDTO userDTO) {
        // 判断用户名是否已存在
        if (userService.isUsernameExists(userDTO.getUsername())) {
            log.warn("用户注册失败 - 用户名已存在：{}", userDTO.getUsername());
            // 判断邮箱是否已存在
            if (userService.isUserEmailExists(userDTO.getEmail())) {
                log.warn("用户注册失败 - 邮箱已被注册：{}", userDTO.getEmail());
                return apiResponseBuilder.buildErrorResponse(HttpStatus.CONFLICT, "邮箱已被注册");
            }
            return apiResponseBuilder.buildErrorResponse(HttpStatus.CONFLICT, "用户名已存在");
        }
        return null;
    }


}
