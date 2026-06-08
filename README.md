# OpenNovel

OpenNovel 是一个基于 Java / Spring Boot 的小说平台后端项目。仓库现在主要放后端多模块代码，包含后台权限、小说后台管理、图片上传、AI 辅助、前台用户等服务。

这个项目还在开发中。公开仓库不会提交真实本地配置、数据库密码、对象存储密钥和邮箱授权码，下面的配置示例都用占位符，方便本地拉起来之后按自己的环境改。

## 技术栈

| 分类            | 技术                                         |
| --------------- | -------------------------------------------- |
| 后端            | Java 17、Spring Boot、Spring MVC、Spring AOP |
| 网关            | Spring Cloud Gateway                         |
| 服务注册 / 配置 | Nacos                                        |
| 持久层          | MyBatis、MyBatis XML、PageHelper             |
| 数据库          | MySQL                                        |
| 缓存            | Redis                                        |
| 对象存储        | MinIO                                        |
| 认证授权        | JWT、BCrypt、RBAC、Sa-Token                  |
| AI              | Ollama、Spring AI                            |
| 构建            | Maven                                        |

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

| 模块           | 默认端口 | 说明                                               | 配置文件                                           |
| -------------- | -------- | -------------------------------------------------- | -------------------------------------------------- |
| `common`       | 无       | 公共返回体、异常、JWT、权限注解和通用 DTO / VO     | 无独立启动配置                                     |
| `gateway`      | `8010`   | 统一入口、路由转发、后台 JWT 校验和登录人信息透传  | `gateway/src/main/resources/application.yaml`      |
| `admin-system` | `8006`   | 后台管理员、角色、菜单、权限、日志、公告、个人中心 | `admin-system/src/main/resources/application.yaml` |
| `novel-admin`  | `8000`   | 小说分类、小说信息、章节、段落、TXT 导入、封面管理 | `novel-admin/src/main/resources/application.yaml`  |
| `minio-image`  | `8001`   | 图片上传、删除、MinIO 对象存储封装                 | `minio-image/src/main/resources/application.yaml`  |
| `novel-AI`     | `8002`   | 小说信息生成等 AI 辅助能力                         | `novel-AI/src/main/resources/application.yaml`     |
| `novel-user`   | `8003`   | 前台用户注册、登录、邮箱验证码、用户基础信息       | `novel-user/src/main/resources/application.yml`    |

更多模块说明可以看 `docs/architecture.md`。

## 当前进度

已经基本完成的部分：

* 后台管理员登录和 JWT 鉴权
* Gateway 统一入口、后台接口保护和请求头透传
* RBAC 权限模型，包含用户、角色、菜单、权限码和关系表
* `@RequirePermission` + Spring AOP 的接口级权限控制
* 管理员、角色、菜单、权限分配、登录日志、操作日志、通知公告、个人中心
* 小说分类、小说信息、章节、段落、回收站、TXT 导入
* 小说封面上传、删除和 MinIO 存储
* 前台用户注册、登录、邮箱验证码
* 基于 Ollama 的小说元信息生成接口

还在补的部分：

* 数据库初始化 SQL
* 接口文档和 Postman Collection
* Docker Compose 一键启动环境
* 前台小说展示和小说元数据相关模块
* 权限缓存、token 失效机制、服务间鉴权
* 单元测试和集成测试

## 核心设计

后台请求统一从 `gateway` 进入。当前网关里登录白名单是 `/api/adminSystem/adminLogin`，后台受保护路径主要是 `/api/adminSystem/**`。

```text
客户端请求
  -> Gateway 判断是否白名单
  -> 校验 JWT
  -> 解析 adminId / username / loginType
  -> 写入 X-Admin-Id / X-Admin-Username
  -> 转发到下游服务
```

后台权限使用 RBAC。菜单控制页面可见性，权限码控制具体操作，接口上通过类似下面的注解做权限校验：

```java
@RequirePermission("admin:user:delete")
```

TXT 导入会把非结构化文本拆成小说、章节、段落和锚点数据。图片上传成功后返回访问 URL 和 `objectName`，其中 `objectName` 用于后续删除 MinIO 对象。

## 本地运行

### 环境要求

* JDK 17+
* Maven 3.8+
* MySQL 8.x
* Redis
* Nacos
* MinIO
* Ollama，只有运行 `novel-AI` 时需要

### 克隆和编译

```bash
git clone https://github.com/2sea234/open-novel.git
cd open-novel
mvn clean install
```

只编译某个模块时可以这样写：

```bash
mvn clean package -pl admin-system -am
```

### 启动顺序

建议先启动基础服务：

```text
MySQL
Redis
Nacos
MinIO
Ollama
```

再启动后端模块：

```text
admin-system
novel-admin
minio-image
novel-AI
novel-user
gateway
```

单个模块本地启动示例：

```bash
mvn spring-boot:run -pl admin-system -am
mvn spring-boot:run -pl gateway -am
```

## 配置文件示例

下面这些示例是给本地开发用的。真实密码、密钥、邮箱授权码不要写死在仓库里，建议用环境变量、Nacos 配置中心或本地未提交的 `*-local.yaml` 管理。

### 1. gateway

文件：`gateway/src/main/resources/application.yaml`

```yaml
server:
  port: 8010

spring:
  application:
    name: gateway
  profiles:
    active: local
  config:
    import:
      - optional:nacos:novel-gateway-local.yaml?group=DEFAULT_GROUP&refresh-enabled=true
  cloud:
    nacos:
      server-addr: ${NACOS_SERVER_ADDR:127.0.0.1:8848}
      discovery:
        namespace: ${NACOS_NAMESPACE:}
        group: DEFAULT_GROUP
      config:
        namespace: ${NACOS_NAMESPACE:}
        group: DEFAULT_GROUP
        file-extension: yaml
```

Nacos Data ID：`novel-gateway-local.yaml`

```yaml
jwt:
  secret: ${JWT_SECRET}
  expire-time: ${JWT_EXPIRE_TIME:86400000}

spring:
  cloud:
    gateway:
      routes:
        - id: admin-system
          uri: lb://admin-system-service
          predicates:
            - Path=/api/adminSystem/**
          filters:
            - StripPrefix=1
        - id: novel-admin
          uri: lb://novel-admin
          predicates:
            - Path=/api/admin/**
          filters:
            - StripPrefix=1
        - id: minio-image
          uri: lb://minio-image
          predicates:
            - Path=/api/image/**
          filters:
            - StripPrefix=1
        - id: novel-ai
          uri: lb://novel-ai
          predicates:
            - Path=/api/ai/**
          filters:
            - StripPrefix=1
        - id: novel-user
          uri: lb://novel-user
          predicates:
            - Path=/api/user/**,/api/email/**
          filters:
            - StripPrefix=1
```

### 2. admin-system

文件：`admin-system/src/main/resources/application.yaml`

```yaml
server:
  port: 8006

spring:
  application:
    name: admin-system-service
  profiles:
    active: local
  config:
    import:
      - optional:nacos:admin-system-service-local.yaml?group=DEFAULT_GROUP&refresh-enabled=true
  cloud:
    nacos:
      server-addr: ${NACOS_SERVER_ADDR:127.0.0.1:8848}
      discovery:
        namespace: ${NACOS_NAMESPACE:}
        group: DEFAULT_GROUP
      config:
        namespace: ${NACOS_NAMESPACE:}
        group: DEFAULT_GROUP
        file-extension: yaml
```

Nacos Data ID：`admin-system-service-local.yaml`

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${MYSQL_HOST:127.0.0.1}:${MYSQL_PORT:3306}/open_novel_admin?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: ${MYSQL_USERNAME}
    password: ${MYSQL_PASSWORD}

mybatis:
  mapper-locations: classpath:Mapper/*.xml
  type-aliases-package: com.kxhy.admin.domain
  configuration:
    map-underscore-to-camel-case: true

pagehelper:
  helper-dialect: mysql
  reasonable: true
  support-methods-arguments: true

jwt:
  secret: ${JWT_SECRET}
  expire-time: ${JWT_EXPIRE_TIME:86400000}

admin:
  user:
    default-password: ${ADMIN_DEFAULT_PASSWORD:123456}
```

### 3. novel-admin

文件：`novel-admin/src/main/resources/application.yaml`

```yaml
server:
  port: 8000

spring:
  application:
    name: novel-admin
  profiles:
    active: local
  config:
    import:
      - optional:nacos:novel-admin-local.yaml?group=DEFAULT_GROUP&refresh-enabled=true
  cloud:
    nacos:
      server-addr: ${NACOS_SERVER_ADDR:127.0.0.1:8848}
      discovery:
        namespace: ${NACOS_NAMESPACE:}
        group: DEFAULT_GROUP
      config:
        namespace: ${NACOS_NAMESPACE:}
        group: DEFAULT_GROUP
        file-extension: yaml
```

Nacos Data ID：`novel-admin-local.yaml`

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${MYSQL_HOST:127.0.0.1}:${MYSQL_PORT:3306}/open_novel_content?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: ${MYSQL_USERNAME}
    password: ${MYSQL_PASSWORD}

mybatis:
  mapper-locations: classpath:Mapper/*.xml
  type-aliases-package: com.kxhy.domain
  configuration:
    map-underscore-to-camel-case: true

pagehelper:
  helper-dialect: mysql
  reasonable: true
  support-methods-arguments: true

jwt:
  secret: ${JWT_SECRET}
  expire-time: ${JWT_EXPIRE_TIME:86400000}

admin-system:
  base-url: http://admin-system-service

comfyui:
  base-url: ${COMFYUI_BASE_URL:http://127.0.0.1:8188}
  workflow-path: ${COMFYUI_WORKFLOW_PATH:}
  checkpoint-name: ${COMFYUI_CHECKPOINT_NAME:}
  width: 832
  height: 1216
  steps: 30
  cfg: 5.0
  sampler-name: dpm_2
  scheduler: normal
  timeout-seconds: 300
  poll-interval-millis: 1000
```

### 4. minio-image

文件：`minio-image/src/main/resources/application.yaml`

```yaml
server:
  port: 8001

spring:
  application:
    name: minio-image
  profiles:
    active: local
  config:
    import:
      - optional:nacos:novel-minio-local.yaml?group=DEFAULT_GROUP&refresh-enabled=true
  cloud:
    nacos:
      server-addr: ${NACOS_SERVER_ADDR:127.0.0.1:8848}
      discovery:
        namespace: ${NACOS_NAMESPACE:}
        group: DEFAULT_GROUP
      config:
        namespace: ${NACOS_NAMESPACE:}
        group: DEFAULT_GROUP
        file-extension: yaml
```

Nacos Data ID：`novel-minio-local.yaml`

```yaml
minio:
  endpoint: ${MINIO_ENDPOINT:http://127.0.0.1:9000}
  access-key: ${MINIO_ACCESS_KEY}
  secret-key: ${MINIO_SECRET_KEY}
  bucket-name: ${MINIO_BUCKET_NAME:open-novel}

spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```

### 5. novel-AI

文件：`novel-AI/src/main/resources/application.yaml`

```yaml
server:
  port: 8002

spring:
  application:
    name: novel-ai
  profiles:
    active: local
  config:
    import:
      - optional:nacos:novel-ai-local.yaml?group=DEFAULT_GROUP&refresh-enabled=true
  cloud:
    nacos:
      server-addr: ${NACOS_SERVER_ADDR:127.0.0.1:8848}
      discovery:
        namespace: ${NACOS_NAMESPACE:}
        group: DEFAULT_GROUP
      config:
        namespace: ${NACOS_NAMESPACE:}
        group: DEFAULT_GROUP
        file-extension: yaml
```

Nacos Data ID：`novel-ai-local.yaml`

```yaml
ollama:
  base-url: ${OLLAMA_BASE_URL:http://127.0.0.1:11434}
  model: ${OLLAMA_MODEL:qwen2.5:7b}
```

### 6. novel-user

文件：`novel-user/src/main/resources/application.yml`

```yaml
server:
  port: 8003

spring:
  application:
    name: novel-user
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${MYSQL_HOST:127.0.0.1}:${MYSQL_PORT:3306}/novel_user?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: ${MYSQL_USERNAME}
    password: ${MYSQL_PASSWORD}
  data:
    redis:
      host: ${REDIS_HOST:127.0.0.1}
      port: ${REDIS_PORT:6379}
      database: ${REDIS_DATABASE:0}
      timeout: 5000
  cloud:
    nacos:
      server-addr: ${NACOS_SERVER_ADDR:127.0.0.1:8848}

sa-token:
  token-name: satoken
  timeout: 2592000
  active-timeout: -1
  is-concurrent: true
  is-share: false
  token-style: uuid
  is-log: true

app:
  email:
    smtp:
      host: ${EMAIL_SMTP_HOST}
      port: ${EMAIL_SMTP_PORT:465}
      ssl-enabled: ${EMAIL_SMTP_SSL_ENABLED:true}
      from-email: ${APP_EMAIL_FROM}
      auth-code: ${APP_EMAIL_AUTH_CODE}
      connection-timeout: 10000
      timeout: 15000
    pool:
      max-total: 10
      max-idle: 5
      min-idle: 2
      max-wait-millis: 3000
      test-on-borrow: true
      test-while-idle: true
    retry:
      max-attempts: 3
      backoff-delay: 1000
      multiplier: 2.0
    template:
      verification-code: verification-email.ftl
    monitor:
      enabled: true
      metrics-prefix: email

mybatis:
  type-aliases-package: com.kxhy.novel.domain.po
  mapper-locations: classpath:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true

resilience4j:
  ratelimiter:
    instances:
      user-register:
        limit-for-period: 5
        limit-refresh-period: 10s
        timeout-duration: 0
        register-health-indicator: true
      user-login:
        limit-for-period: 10
        limit-refresh-period: 60s
        timeout-duration: 0
        register-health-indicator: true

management:
  endpoints:
    web:
      exposure:
        include: health,ratelimiters
  endpoint:
    health:
      show-details: always

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
    enabled: true
    try-it-out-enabled: true
  packages-to-scan: com.kxh.novel.controller
  paths-to-match: /**

logging:
  pattern:
    level: "%5p [%X{requestId}]"
  file:
    name: logs/novel-app.log
```

### 环境变量参考

| 变量                                                         | 用途                                  |
| ------------------------------------------------------------ | ------------------------------------- |
| `NACOS_SERVER_ADDR`                                          | Nacos 地址，例如 `127.0.0.1:8848`     |
| `NACOS_NAMESPACE`                                            | Nacos namespace，本地不分环境可以留空 |
| `MYSQL_HOST` / `MYSQL_PORT`                                  | MySQL 地址和端口                      |
| `MYSQL_USERNAME` / `MYSQL_PASSWORD`                          | MySQL 账号和密码                      |
| `REDIS_HOST` / `REDIS_PORT` / `REDIS_DATABASE`               | Redis 配置                            |
| `JWT_SECRET` / `JWT_EXPIRE_TIME`                             | 后台 JWT 密钥和过期时间               |
| `MINIO_ENDPOINT` / `MINIO_ACCESS_KEY` / `MINIO_SECRET_KEY` / `MINIO_BUCKET_NAME` | MinIO 配置                            |
| `OLLAMA_BASE_URL` / `OLLAMA_MODEL`                           | Ollama 地址和模型                     |
| `EMAIL_SMTP_HOST` / `APP_EMAIL_FROM` / `APP_EMAIL_AUTH_CODE` | 邮箱服务配置                          |

## 安全说明

仓库里不要提交这些内容：

* 本地配置文件和 `.env`
* MySQL、Redis、MinIO、邮箱、JWT 的真实密钥
* 日志文件、上传文件、缓存目录
* `target/`、`build/`、IDE 配置和临时文件
* 数据库备份和压缩包

`.gitignore` 已经覆盖了常见本地配置和构建产物。如果要分享配置，优先写成脱敏后的示例。

## 暂未提交的目录

当前公开仓库没有提交下面两个规划中或重构中的模块：

```text
novel-metadata
novel-novel
```

## 后续计划

* 补数据库初始化 SQL
* 补接口文档和 Postman Collection
* 补 Docker Compose 本地启动环境
* 完善前台小说展示和小说元数据服务
* 增加权限缓存、token 失效机制和服务间鉴权
* 增加测试用例

## License

暂未指定开源许可证。
