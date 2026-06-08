# OpenNovel

OpenNovel 是一个基于 Java / Spring Boot 的小说平台后端项目，采用多模块结构进行拆分，主要包含后台权限系统、小说后台管理、图片上传服务、AI 辅助服务、前台用户服务等模块。

当前仓库主要用于保存 OpenNovel 后端代码与项目结构，部分本地配置、数据库脚本、未完成模块暂未提交。

---

## 项目简介

OpenNovel 主要围绕小说平台的后台管理和用户服务进行开发，当前已实现或正在完善以下能力：

* 后台管理员登录与 JWT 鉴权
* RBAC 权限管理
* 用户、角色、菜单、权限码管理
* 后台接口级权限控制
* 小说分类、书籍、章节、段落管理
* TXT 小说导入与章节/段落解析
* 小说封面上传与删除
* 登录日志与操作日志记录
* 前台用户注册、登录、邮箱验证码等基础能力
* AI 辅助生成小说信息能力预留

---

## 技术栈

| 分类      | 技术                                     |
| ------- | -------------------------------------- |
| 后端框架    | Java、Spring Boot、Spring MVC、Spring AOP |
| 网关      | Spring Cloud Gateway                   |
| 持久层     | MyBatis、MyBatis XML、PageHelper         |
| 数据库     | MySQL                                  |
| 配置 / 注册 | Nacos                                  |
| 对象存储    | MinIO                                  |
| 认证授权    | JWT、BCrypt、RBAC                        |
| 其他      | Maven、Docker、Postman、Git               |

---

## 模块结构

```text
OpenNovel
├── common
├── gateway
├── admin-system
├── novel-admin
├── minio-image
├── novel-AI
└── novel-user
```

### common

公共模块，主要放置跨服务复用的基础能力，例如：

* 统一返回结果
* 业务异常
* 全局异常处理
* JWT 工具类
* JWT 配置类
* 权限注解
* 通用 DTO / VO

### gateway

网关模块，作为后台请求统一入口，主要负责：

* 请求路由
* 登录白名单放行
* JWT 校验
* 后台接口保护
* 解析登录用户信息
* 向下游服务透传当前管理员信息

### admin-system

后台系统模块，主要负责后台基础能力：

* 管理员登录
* 用户管理
* 角色管理
* 菜单管理
* 权限码管理
* 用户分配角色
* 角色分配菜单
* 角色分配权限
* 当前管理员菜单树
* 当前管理员权限码
* 登录日志
* 操作日志
* 通知公告
* 个人中心

### novel-admin

小说后台管理模块，主要负责小说内容相关管理：

* 分类管理
* 小说列表
* 小说详情
* 小说新增 / 修改
* 小说逻辑删除
* 批量删除
* 回收站
* 小说恢复
* TXT 导入
* 章节解析
* 段落解析
* 章节正文查询
* 封面管理

### minio-image

图片服务模块，主要负责：

* 图片上传
* 图片删除
* MinIO 对象存储封装
* 图片访问地址返回
* objectName 返回
* 图片文件类型校验

### novel-AI

AI 辅助服务模块，主要用于：

* 小说信息生成
* 小说分类 / 简介等 AI 能力预留
* 对接本地或外部 AI 服务

### novel-user

前台用户服务模块，主要负责：

* 用户注册
* 用户登录
* 邮箱验证码
* 用户基础信息
* 用户角色
* 用户权限切面
* 用户操作日志
* Redis / Sa-Token 等相关配置

---

## 当前功能进度

### 已完成或基本完成

* 后台管理员登录
* Gateway + JWT 后台鉴权
* RBAC 权限模型
* 后端接口级权限校验
* 用户管理
* 角色管理
* 菜单管理
* 权限分配
* 登录日志
* 操作日志
* 通知公告
* 个人中心
* 小说分类管理
* 小说管理
* TXT 小说导入
* 章节 / 段落解析
* 回收站与恢复
* MinIO 图片上传与删除
* 前台用户注册 / 登录基础能力

### 开发中 / 后续计划

* 完善数据库初始化脚本
* 完善接口文档
* 完善 Postman 测试集合
* 完善前台小说展示模块
* 完善小说元数据模块
* 增加 Redis 权限缓存
* 增加 token 黑名单或 tokenVersion 机制
* 增加服务间鉴权
* 增加单元测试和集成测试
* 完善 Docker Compose 一键启动环境

---

## 核心设计

### 1. Gateway + JWT 鉴权

后台接口统一经过 gateway 访问。

基本流程：

```text
客户端请求
    ↓
Gateway 判断是否白名单
    ↓
校验 JWT
    ↓
解析 adminId / username / loginType
    ↓
注入 X-Admin-Id / X-Admin-Username
    ↓
转发到下游服务
```

下游服务通过 gateway 透传的请求头获取当前管理员信息，用于业务处理、权限校验和操作日志记录。

---

### 2. RBAC 权限模型

后台权限采用 RBAC 模型，核心关系如下：

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

菜单主要控制页面可见性，权限码主要控制具体操作权限。

示例权限码：

```text
admin:user:list
admin:user:add
admin:user:update
admin:user:delete
admin:role:list
admin:role:assign-menu
admin:role:assign-permission
```

---

### 3. 接口级权限控制

项目通过自定义权限注解和 Spring AOP 实现接口级权限校验。

示例：

```java
@RequirePermission("admin:user:delete")
```

请求进入接口前，权限切面会读取当前管理员 ID，查询其拥有的权限码，并判断是否拥有当前接口所需权限。没有权限时返回 403。

---

### 4. 操作日志

后台关键操作会记录操作日志，包括：

* 操作人
* 操作模块
* 操作类型
* 操作描述
* 请求路径
* 请求方式
* IP
* User-Agent
* 操作状态
* 失败原因
* 操作时间

操作日志用于记录后台关键行为，方便后续排查和审计。

---

### 5. TXT 导入解析

TXT 小说导入流程大致如下：

```text
上传 TXT 文件
    ↓
读取文件内容
    ↓
解析文件名 / 正文头部
    ↓
识别小说标题和作者
    ↓
识别章节
    ↓
拆分段落
    ↓
保存文章
    ↓
保存章节
    ↓
保存段落
    ↓
生成锚点数据
    ↓
更新字数和章节数
```

通过该流程将非结构化 TXT 小说文本转换为结构化的文章、章节、段落数据。

---

### 6. MinIO 图片上传

图片上传服务基于 MinIO 实现。

上传成功后返回：

* 图片访问 URL
* objectName

其中 URL 用于前端展示，objectName 用于后续删除对象。

图片上传时会进行基础校验，包括：

* 文件后缀
* Content-Type
* 文件头 magic number

---

## 本地运行

### 环境要求

* JDK 17+
* Maven 3.8+
* MySQL 8.x
* Nacos
* MinIO
* Docker（可选）

### 克隆项目

```bash
git clone https://github.com/2sea234/open-novel.git
cd open-novel
```

### 配置说明

本仓库不提交真实本地配置和密钥，运行前需要准备以下配置：

* MySQL 地址、账号、密码
* Nacos 地址
* MinIO endpoint、accessKey、secretKey、bucket
* JWT secret
* 邮箱授权码
* AI 服务地址

建议通过环境变量、Nacos 配置中心或本地外置配置文件进行配置。

示例：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/opennovel
    username: ${MYSQL_USERNAME}
    password: ${MYSQL_PASSWORD}

jwt:
  secret: ${JWT_SECRET}

minio:
  endpoint: ${MINIO_ENDPOINT}
  access-key: ${MINIO_ACCESS_KEY}
  secret-key: ${MINIO_SECRET_KEY}
```

### 编译

```bash
mvn clean install
```

如果只编译指定模块：

```bash
mvn clean package -pl admin-system -am
```

### 启动顺序

建议先启动基础服务：

```text
MySQL
Nacos
MinIO
```

再启动后端模块：

```text
gateway
admin-system
novel-admin
minio-image
novel-AI
novel-user
```

具体端口和配置以各模块配置文件为准。

---

## 目录未提交说明

当前仓库暂未提交以下模块：

```text
novel-metadata
novel-novel
```

这两个模块属于后续规划或重构中模块，暂未纳入当前公开版本。

---

## 安全说明

仓库中不提交以下内容：

* 本地配置文件
* 数据库真实密码
* JWT secret
* MinIO 密钥
* 邮箱授权码
* 日志文件
* target 编译产物
* IDE 配置文件
* 本地上传文件

如果需要运行项目，请自行准备对应配置。

---

## 后续计划

* 补充数据库初始化 SQL
* 补充接口文档
* 补充 Postman Collection
* 完善 Docker Compose 启动方式
* 完善前台小说展示服务
* 完善小说元数据服务
* 增加权限缓存
* 增加 token 失效机制
* 增加服务间鉴权
* 增加测试用例

---

## License

暂未指定开源许可证。
