# OpenNovel 架构说明

## 1. 项目结构

OpenNovel 当前采用多模块结构拆分，主要模块如下：

```text
OpenNovel
├── common          公共模块
├── gateway         网关模块
├── admin-system    后台系统模块
├── novel-admin     小说后台管理模块
├── minio-image     图片服务模块
├── novel-AI        AI 辅助服务模块
└── novel-user      前台用户服务模块
```

## 2. 模块职责

### common

公共基础模块，主要包含：

* 统一返回结构
* 业务异常
* 全局异常处理
* JWT 工具类
* JWT 配置类
* 权限注解
* 通用 DTO / VO

### gateway

统一网关模块，主要负责：

* 请求路由
* 登录接口白名单
* JWT 校验
* 后台接口保护
* 解析 token 中的登录人信息
* 向下游服务透传 `X-Admin-Id` 和 `X-Admin-Username`

### admin-system

后台系统模块，主要负责：

* 管理员登录
* 管理员用户管理
* 角色管理
* 菜单管理
* 权限码管理
* 登录日志
* 操作日志
* 通知公告
* 个人中心
* 当前管理员菜单树和权限码查询

### novel-admin

小说后台管理模块，主要负责：

* 小说分类管理
* 小说信息管理
* TXT 小说导入
* 章节解析
* 段落解析
* 章节正文查询
* 回收站与恢复
* 小说封面管理

### minio-image

图片服务模块，主要负责：

* 图片上传
* 图片删除
* MinIO 对象存储封装
* 图片文件类型校验

### novel-AI

AI 辅助模块，主要负责：

* 小说信息生成基础调用
* 后续 AI 能力扩展预留

### novel-user

前台用户模块，主要负责：

* 用户注册
* 用户登录
* 邮箱验证码
* 用户基础信息
* 用户角色与权限切面
* 用户操作日志

## 3. 后台请求链路

后台接口统一通过 gateway 访问：

```text
前端 / Postman
    ↓
gateway
    ↓
JWT 校验
    ↓
注入 X-Admin-Id / X-Admin-Username
    ↓
admin-system / novel-admin / minio-image / novel-AI
```

## 4. 权限控制链路

后台权限基于 RBAC 模型实现：

```text
管理员用户
    ↓
用户角色关系
    ↓
角色
    ↓
角色菜单关系 / 角色权限关系
    ↓
菜单 / 权限码
```

前端通过菜单树控制页面显示，通过权限码控制按钮显示。后端通过 `@RequirePermission` 注解和 Spring AOP 实现接口级权限校验。

## 5. TXT 导入链路

TXT 小说导入主要流程如下：

```text
上传 TXT
    ↓
文件校验
    ↓
读取文本
    ↓
解析文件名 / 正文头部
    ↓
识别标题和作者
    ↓
识别章节
    ↓
拆分段落
    ↓
保存文章、章节、段落
    ↓
生成段落 hash 和锚点数据
    ↓
更新字数和章节数
```

## 6. 图片上传链路

图片上传由 `minio-image` 模块处理：

```text
上传图片
    ↓
后缀校验
    ↓
Content-Type 校验
    ↓
magic number 文件头校验
    ↓
上传到 MinIO
    ↓
返回 URL 和 objectName
```

## 7. 当前边界

当前仓库暂未包含以下模块：

* novel-metadata
* novel-novel

这两个模块仍在规划或重构中，暂未纳入当前公开版本。
