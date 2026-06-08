# OpenNovel

OpenNovel 是一个基于 Java / Spring Boot 的小说平台后端项目，采用多模块结构进行拆分，主要包含后台权限系统、小说后台管理、图片上传服务、AI 辅助服务、前台用户服务等模块。

当前仓库主要用于保存 OpenNovel 后端代码与项目结构，部分本地配置、数据库脚本和仍在重构中的模块暂未提交。

---

## 项目简介

OpenNovel 当前主要围绕后台管理、权限控制、小说内容导入、图片存储和用户基础能力进行开发。当前仓库包含 `gateway`、`common`、`admin-system`、`novel-admin`、`minio-image`、`novel-AI`、`novel-user` 等模块。

当前已实现或基本完成的能力包括：

* 后台管理员登录与 JWT 鉴权
* Gateway 统一入口、后台接口保护与登录人信息透传
* RBAC 权限模型，包含用户、角色、菜单、权限码及关系表
* 基于 `@RequirePermission + Spring AOP` 的后台接口级权限控制
* 管理员、角色、菜单、权限分配等后台管理功能
* 登录日志、操作日志、通知公告、个人中心等后台基础能力
* 小说分类、小说信息、章节、段落等后台管理功能
* TXT 小说导入、章节识别、段落拆分与锚点数据生成
* 小说封面上传、删除与 MinIO 对象存储封装
* 前台用户注册、登录、邮箱验证码等基础能力
* novel-AI 模块提供小说信息生成等 AI 辅助能力的基础调用与后续扩展预留

当前未提交或仍在重构中的模块：

* `novel-metadata`
* `novel-novel`

---

## 技术栈

| 分类 | 技术 |
|---|---|
| 后端框架 | Java 17、Spring Boot、Spring MVC、Spring AOP |
| 网关 | Spring Cloud Gateway |
| 持久层 | MyBatis、MyBatis XML、PageHelper |
| 数据库 | MySQL |
| 配置 / 注册 | Nacos |
| 对象存储 | MinIO |
| 认证授权 | JWT、BCrypt、RBAC、Sa-Token |
| 工具 | Maven、Git、Postman、Docker、IDEA |

---

## 模块结构

| 模块 | 默认端口 | 说明 | 配置文件 |
|---|---:|---|---|
| `common` | 无 | 公共返回、业务异常、JWT 工具、权限注解、全局异常处理 | 无 |
| `gateway` | 8010 | 统一入口、路由转发、后台 JWT 校验、登录人信息透传 | `gateway/src/main/resources/application.yaml` |
| `admin-system` | 8006 | 后台管理员、角色、菜单、权限、日志、公告、个人中心 | `admin-system/src/main/resources/application.yaml` |
| `novel-admin` | 8000 | 小说分类、小说信息、章节、段落、TXT 导入、封面管理 | `novel-admin/src/main/resources/application.yaml` |
| `minio-image` | 8001 | 图片上传、删除、MinIO 对象存储封装 | `minio-image/src/main/resources/application.yaml` |
| `novel-AI` | 8002 | 小说信息生成等 AI 辅助能力 | `novel-AI/src/main/resources/application.yaml` |
| `novel-user` | 8003 | 前台用户注册、登录、邮箱验证码、用户基础信息 | `novel-user/src/main/resources/application.yml` |

---

## 文档

* [架构说明](docs/architecture.md)
* [配置说明](docs/configuration.md)

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

### 编译项目

```bash
mvn clean install
```

如果只编译某个模块：

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

## License

本项目使用 MIT License，详见 [LICENSE](LICENSE)。
