# SQL 脚本说明

该目录用于保存 OpenNovel 当前版本的数据库表结构。

当前 SQL 文件只包含表结构，不包含真实业务数据、测试数据、账号密码、日志记录或小说正文。

使用方式：

```bash
mysql -u root -p 数据库名 < init.sql