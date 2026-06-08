package com.kxhy.novel.service.impl;

import com.kxhy.novel.common.converter.ShortIdGenerator;
import com.kxhy.novel.constant.NovelPermissionConstants;
import com.kxhy.novel.domain.dto.UserDTO;
import com.kxhy.novel.domain.dto.UserRegisterDTO;
import com.kxhy.novel.domain.enm.Status;
import com.kxhy.novel.domain.po.User;
import com.kxhy.novel.domain.vo.CurrentUserVo;
import com.kxhy.novel.domain.vo.UserRegisterResult;
import com.kxhy.novel.domain.vo.UsernameCheckResult;
import com.kxhy.novel.exception.BusinessException;
import com.kxhy.novel.mapper.NovelRoleMapper;
import com.kxhy.novel.mapper.UserMapper;
import com.kxhy.novel.service.EmailService;
import com.kxhy.novel.service.TokenService;
import com.kxhy.novel.service.UserService;
import com.kxhy.novel.utils.UsernameValidator;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private EmailService emailService;

    @Resource
    private UserMapper userMapper;
    @Resource
    private NovelRoleMapper novelRoleMapper;
    @Resource
    private BCryptPasswordEncoder  passwordEncoder;
    private final TokenService tokenService; // 令牌服务

    @Autowired
    private UsernameValidator usernameValidator;


    /**
     * <span style="color: #3498db;">判断用户名是否存在</span>
     * @param username 用户名
     * @return true:存在 false:不存在
     */
    @Override
    public Boolean isUsernameExists(String username) {
        return userMapper.selectByUserName(username) != null;
    }

    /**
     * <span style="color: teal;">验证用户邮箱是否存在</span>
     * @param email 邮箱
     * @return true:存在 false:不存在
     */
    @Override
    public Boolean isUserEmailExists(String email) {
        return userMapper.selectByEmail(email) != null;
    }


    private void assignDefaultRole(Long userId) {
        try {
            Long readerRoleID = novelRoleMapper.selectRoleIdByCode(NovelPermissionConstants.ROLE_READER);
            log.info("查询到的读者角色ID：{}", readerRoleID);

            if (readerRoleID == null) {
                log.error("读者角色不存在，请检查数据库配置");
                throw new BusinessException("系统配置错误");
            }

            int result = novelRoleMapper.assignRoleToUser(userId, readerRoleID);
            if (result>0) {
                log.info("分配默认角色成功 - 用户ID：{}，角色ID：{}", userId, readerRoleID);
            } else {
                log.error("分配默认主角失败 - 用户ID：{}， 角色ID：{}", userId, readerRoleID);
            }

        } catch (Exception e) {
            log.warn("分配默认用户角色异常 - 用户ID：{}", userId, e);
            throw new BusinessException("角色分配异常");
        }
    }

    /**
     * <span style="color: #e84393">用户注册</span>
     * @param userDTO 用户信息
     * @return 注册结果
     */
    @Transactional
    @Override
    public UserRegisterResult register(UserRegisterDTO userDTO) {


        boolean result = emailService.verifyCode(userDTO.getEmail(), userDTO.getCode());
        if (!result) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 检查邮箱格式
        if (!isValidEmail(userDTO.getEmail())) {
            throw new BusinessException("邮箱格式不正确");
        }

        try {
            if (isUsernameExists(userDTO.getUsername())) {
                throw new BusinessException("用户名已存在，请换一个");
            }

            if (isUserEmailExists(userDTO.getEmail())) {
                throw new BusinessException("该邮箱已注册，请直接登录");
            }
            User user = buildUserEntity(userDTO);
            if (userMapper.insetUser(user) <= 0) {
                throw new BusinessException("用户注册失败");
            }

            // 分配默认角色
            assignDefaultRole(user.getUserId());

            // 使用tokenService 生成token
            String token = tokenService.generateToken(user.getUserId());

            // 注册日志
            logUserRegisterEvent(user);

            // 返回注册结果
            return new UserRegisterResult(
                    user.getUserId(),
                    user.getUsername(),
                    token,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    user.getStatus());

        }catch (Exception e) {
            // 捕获数据库唯一约束异常，重新检查并提示
            log.warn("注册时发生唯一约束冲突: {}", e.getMessage());

            // 重新检查，确定是哪个字段冲突
            if (isUsernameExists(userDTO.getUsername())) {
                throw new BusinessException("用户名已被占用，请换一个");
            }
            if (isUserEmailExists(userDTO.getEmail())) {
                throw new BusinessException("该邮箱已注册，请直接登录");
            }

            throw new BusinessException("注册失败，请稍后重试");
        }
    }


    /**
     * <span style="color: teal">验证密码强度</span>
     */
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        return hasLetter && hasDigit;
    }

    /**
     * <span style="color: teal">验证邮箱格式</span>
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.length() > 254) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * <span style="color: teal">构建用户实体</span>
     * @param userDTO 用户信息
     * @return 用户实体
     */
    private User buildUserEntity(UserRegisterDTO userDTO) {
        User user = new User();
        user.setUserId(new ShortIdGenerator().generateNumericId());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPhone(userDTO.getMobile());
        user.setEmail(userDTO.getEmail());
        user.setLastLoginTime(null);
        user.setCreateTime(LocalDateTime.now());
        user.setStatus(Status.NORMAL.getValue());
        return user;
    }

    /**
     * 通过SaToken登录返回token
     * @param id 用户id
     * @return token
     */
    private String generateAndSaveToken(Long id) {
        return tokenService.generateToken(id);
    }

    /**
     * 记录用户注册日志
     * @param user 用户
     */
    private void logUserRegisterEvent(User user) {
        log.info("用户注册成功 - 用户ID：{}, 用户名：{}，注册时间：{}",
                user.getUserId(), user.getUsername(), user.getCreateTime());
    }

    /**
     * <span style="color: #e84393">用户登录</span>
     * @param userDTO 用户信息
     * @return 用户实体
     */
    @Override
    public UserRegisterResult login(UserRegisterDTO userDTO) {

        // 根据用户登陆方式获取用户
        User user = this.ifLoginMethod(userDTO);
        // 验证该用户是否存在
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "密码错误");
        }

        // 检查用户状态
        if (Status.LOCK.getValue().equals(user.getStatus())) {
            throw new BusinessException("账户已被锁定，请联系管理员");
        }

        // 使用tokenService生成token
        String token = tokenService.generateToken(user.getUserId());

        return new UserRegisterResult(
                user.getUserId(),
                user.getUsername(),
                token,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                user.getStatus()
        );
    }

    /**
     * <span style="color: #2980b9; background: linear-gradient(90deg, #2980b9, #3498db); padding: 2px 5px; border-radius: 3px;">判断用户登陆方式</span>
     * @param userDTO 用户信息
     * @return 用户实体
     */
    public User ifLoginMethod(UserRegisterDTO userDTO) {

        // 判断用户昵称是否存在
        if (userDTO.getUsername() != null) {
            return userMapper.selectByUserName(userDTO.getUsername());
        }

        // 判断用户邮箱是否存在
        if (userDTO.getEmail() != null) {
            return userMapper.selectByEmail(userDTO.getEmail());
        }
        return null;
    }



    /**
     * <span style="color: #e84393;">获取当前用户信息</span>
     * @return 当前用户信息
     */
    @Override
    public Object getCurrentUserInfo() {
        if (!tokenService.isLogin()) {
            throw new BusinessException(401, "未登录，请先登录");
        }
        try {
            Object loginId = tokenService.getLoginId();
            // 获取当前登录用户的token
//            String token = tokenService.getTokenValue();
//            if ( token == null)  {
//                throw new BusinessException("用户未登录");
//            }



            log.info("获取当前用户信息成功 - 用户ID：{}", loginId);
            User user = userMapper.selectByUserId((String) loginId);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            return convertToSafeVo(user);
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            throw new BusinessException("获取当前用户信息失败");
        }
    }

    /**
     * 检查用户名是否可用 --> <span style="color: red" >未启用  有待考虑<span/>
     */
    @Override
    public UsernameCheckResult checkUsernameAvailability(String username) {
        try {
            // 1. 格式验证
            UsernameValidator.ValidationResult validationResult = usernameValidator.validate(username);
            if (!validationResult.isValid()) {
                return UsernameCheckResult.unavailable(username, validationResult.getMessage());
            }

            // 2. 检查是否已存在
            if (isUsernameExists(username)) {
                String suggestion = generateUsernameSuggestion(username);
                return UsernameCheckResult.unavailable(
                        username,
                        "用户名已存在，请换一个",
                        suggestion
                );
            }

            // 3. 检查是否有类似用户名
            if (hasSimilarUsername(username)) {
                String suggestion = generateUsernameSuggestion(username);
                return UsernameCheckResult.unavailable(
                        username,
                        "该用户名与现有用户名相似，请换一个",
                        suggestion
                );
            }

            return UsernameCheckResult.available(username);

        } catch (Exception e) {
            log.error("检查用户名可用性失败: {}", e.getMessage(), e);
            return UsernameCheckResult.unavailable(username, "系统繁忙，请稍后重试");
        }
    }

    /**
     * <span  style="color: #e84393;">更新用户信息</span> <span style="color: red;">其实就只是修改了个昵称邮箱修改后期看情况再加</span>
     * @param userDTO 用户信息
     * @return 是否成功
     */
    @Transactional
    @Override
    public UserDTO updateUserInfo(UserDTO userDTO) {
        log.info("开始更新用户信息 - 用户ID：{}，新用户名：{}",
                userDTO.getUserId(), userDTO.getUsername());

        try {
            // 1. 验证用户是否存在
            User existingUser = userMapper.selectByUserId(String.valueOf(userDTO.getUserId()));
            if (existingUser == null) {
                throw new BusinessException("用户不存在");
            }

            // 2. 验证新用户名是否已存在（如果是修改用户名）
            if (userDTO.getUsername() != null &&
                    !existingUser.getUsername().equals(userDTO.getUsername())) {
                if (isUsernameExists(userDTO.getUsername())) {
                    throw new BusinessException("用户名已存在");
                }
            }

            // 3. 执行更新
            int updateCount = userMapper.updateUserSelective(
                    userDTO.getUserId(),
                    userDTO.getUsername()
            );

            if (updateCount > 0) {
                log.info("更新用户信息成功 - 用户ID：{}", userDTO.getUserId());
                // 查询更新后的用户信息
                User updatedUser = userMapper.selectByUserId(String.valueOf(userDTO.getUserId()));
                return new UserDTO(updatedUser.getUserId(), updatedUser.getUsername());
            } else {
                log.warn("更新用户信息失败 - 未找到用户或数据未变化");
                throw new BusinessException("更新失败，请稍后重试");
            }

        } catch (BusinessException e) {
            throw e; // 重新抛出业务异常
        } catch (Exception e) {
            log.error("更新用户信息异常 - 用户ID：{}", userDTO.getUserId(), e);
            throw new BusinessException("更新用户信息失败");
        }
    }


    /**
     * 将拿到的用户信息转换为安全VO
     * @param user 用户
     * @return 安全VO
     */
    private CurrentUserVo convertToSafeVo(User user) {
        CurrentUserVo vo = new CurrentUserVo();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
//        vo.setPhone(user.getPhone());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }


    /**
     * <span style="color: #e84393">登出</span>
     * @return 是否成功
     */
    @Transactional
    @Override
    public Boolean logout() {
        try {
            // 检查是否登录
            if (!tokenService.isLogin()) {
                log.warn("退出登陆失败 - 用户未登录");
                return false;
            }

            // 获取当前登录用户名
            Object loginId = tokenService.getLoginId();
            String userId = (String) loginId;
            // 使用tokenService退出登录
            tokenService.logout();

            // 更新用户退出时间
            if (userMapper.updateEndTimeById(LocalDateTime.now(), userId) > 0) {
                log.info("记录退出日志成功 - 用户名：{}", loginId);
                return true;
            } else {
                log.warn("记录退出日志失败 - 用户名：{}", loginId);
                return false;
            }
        } catch (Exception e) {
            log.error("退出登录失败", e);
            return false;
        }
    }


    /**
     * 生成用户名建议
     */
    private String generateUsernameSuggestion(String baseUsername) {
        Random random = new Random();
        int attempts = 0;

        while (attempts < 10) {
            String suggestion;

            switch (attempts % 4) {
                case 0:
                    suggestion = baseUsername + random.nextInt(1000);
                    break;
                case 1:
                    suggestion = baseUsername + "_" + random.nextInt(100);
                    break;
                case 2:
                    suggestion = baseUsername + "User" + random.nextInt(100);
                    break;
                default:
                    suggestion = baseUsername + (char)('A' + random.nextInt(26));
                    break;
            }

            if (!isUsernameExists(suggestion)) {
                return suggestion;
            }

            attempts++;
        }

        return null;
    }

    /**
     * 检查是否有相似用户名
     */
    private boolean hasSimilarUsername(String username) {
        // 这里可以添加更复杂的相似度算法
        // 简单实现：检查是否有相同前缀的用户名
        String prefix = username.length() > 3 ? username.substring(0, 3) : username;
        Long count = userMapper.countSimilarUsernames(prefix + "%");
        return count != null && count > 0;
    }

}
