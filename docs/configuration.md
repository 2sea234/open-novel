# OpenNovel 配置说明

本项目不提交真实本地配置和密钥。运行项目前，需要自行准备 MySQL、Nacos、MinIO、JWT、邮箱、AI 服务等相关配置。

推荐使用以下方式管理配置：

* 环境变量
* Nacos 配置中心
* 本地外置配置文件
* `application-*.example.yaml` 示例配置

不要将真实密码、密钥、token、邮箱授权码提交到 GitHub。

---

## 1. 通用配置项

常见配置包括：

| 配置项 | 说明 |
|---|---|
| `MYSQL_USERNAME` | MySQL 用户名 |
| `MYSQL_PASSWORD` | MySQL 密码 |
| `JWT_SECRET` | JWT 签名密钥 |
| `MINIO_ENDPOINT` | MinIO 服务地址 |
| `MINIO_ACCESS_KEY` | MinIO Access Key |
| `MINIO_SECRET_KEY` | MinIO Secret Key |
| `APP_EMAIL_AUTH_CODE` | 邮箱授权码 |
| `OLLAMA_BASE_URL` | Ollama / AI 服务地址 |

---

## 2. MySQL 示例

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/opennovel
    username: ${MYSQL_USERNAME}
    password: ${MYSQL_PASSWORD}
```

---

## 3. JWT 示例

```yaml
jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000
```

---

## 4. MinIO 示例

```yaml
minio:
  endpoint: ${MINIO_ENDPOINT}
  access-key: ${MINIO_ACCESS_KEY}
  secret-key: ${MINIO_SECRET_KEY}
  bucket-name: novel-image
```

---

## 5. Nacos 示例

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
        file-extension: yaml
```

---

## 6. 邮箱配置示例

```yaml
spring:
  mail:
    host: smtp.qq.com
    username: ${APP_EMAIL_USERNAME}
    password: ${APP_EMAIL_AUTH_CODE}
```

---

## 7. AI 服务配置示例

```yaml
ollama:
  base-url: ${OLLAMA_BASE_URL}
  model: qwen3:4b
```

---

## 8. 注意事项

* 不要提交真实的 `application-local.yaml`
* 不要提交 `.env`
* 不要提交数据库真实密码
* 不要提交 MinIO accessKey / secretKey
* 不要提交 JWT secret
* 不要提交邮箱授权码
* 不要提交包含真实业务数据的 SQL dump

如需提供示例配置，建议使用：

```text
application-example.yaml
application-local.example.yaml
```

并在文件中使用占位符。
