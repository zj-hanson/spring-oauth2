-- ============================================================================
-- Spring Authorization Server 1.3.7 + 本项目 完整建库脚本（MySQL 8.x / 5.7）
--
-- 适用场景：全新的 easy_oauth2 库（本机没有历史数据时直接跑这个脚本）。
--   已有 0.3 版本旧库的升级场景请改用 sql/upgrade-sas-0.3-to-1.3.sql。
--
-- 前 3 张表来自 SAS 1.3.7 jar 内置官方 DDL（只加了 IF NOT EXISTS 与引擎/字符集后缀）：
--   org/springframework/security/oauth2/server/authorization/oauth2-authorization-schema.sql
--   org/springframework/security/oauth2/server/authorization/client/oauth2-registered-client-schema.sql
--   org/springframework/security/oauth2/server/authorization/oauth2-authorization-consent-schema.sql
-- 第 4 张表对应本项目实体 com.lightshell.oauth2.entity.User（@Table(name = "user")），
-- 供 UserDetailsServiceImpl 做表单登录认证。
-- ============================================================================

CREATE DATABASE IF NOT EXISTS easy_oauth2
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE easy_oauth2;

-- ----------------------------------------------------------------------------
-- 1. oauth2_registered_client：注册客户端（RegisteredClientServiceImpl 写入）
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS oauth2_registered_client (
    id varchar(100) NOT NULL,
    client_id varchar(100) NOT NULL,
    client_id_issued_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret varchar(200) DEFAULT NULL,
    client_secret_expires_at timestamp DEFAULT NULL,
    client_name varchar(200) NOT NULL,
    client_authentication_methods varchar(1000) NOT NULL,
    authorization_grant_types varchar(1000) NOT NULL,
    redirect_uris varchar(1000) DEFAULT NULL,
    post_logout_redirect_uris varchar(1000) DEFAULT NULL,
    scopes varchar(1000) NOT NULL,
    client_settings varchar(2000) NOT NULL,
    token_settings varchar(2000) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------------------
-- 2. oauth2_authorization：授权记录与令牌（JdbcOAuth2AuthorizationService 写入）
--    varchar(1000) 的 scopes 列与 64KB 的 blob 列按官方定义；若 access_token_metadata /
--    attributes 可能超过 64KB，可自行改为 mediumblob。
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS oauth2_authorization (
    id varchar(100) NOT NULL,
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorization_grant_type varchar(100) NOT NULL,
    authorized_scopes varchar(1000) DEFAULT NULL,
    attributes blob DEFAULT NULL,
    state varchar(500) DEFAULT NULL,
    authorization_code_value blob DEFAULT NULL,
    authorization_code_issued_at timestamp DEFAULT NULL,
    authorization_code_expires_at timestamp DEFAULT NULL,
    authorization_code_metadata blob DEFAULT NULL,
    access_token_value blob DEFAULT NULL,
    access_token_issued_at timestamp DEFAULT NULL,
    access_token_expires_at timestamp DEFAULT NULL,
    access_token_metadata blob DEFAULT NULL,
    access_token_type varchar(100) DEFAULT NULL,
    access_token_scopes varchar(1000) DEFAULT NULL,
    oidc_id_token_value blob DEFAULT NULL,
    oidc_id_token_issued_at timestamp DEFAULT NULL,
    oidc_id_token_expires_at timestamp DEFAULT NULL,
    oidc_id_token_metadata blob DEFAULT NULL,
    refresh_token_value blob DEFAULT NULL,
    refresh_token_issued_at timestamp DEFAULT NULL,
    refresh_token_expires_at timestamp DEFAULT NULL,
    refresh_token_metadata blob DEFAULT NULL,
    user_code_value blob DEFAULT NULL,
    user_code_issued_at timestamp DEFAULT NULL,
    user_code_expires_at timestamp DEFAULT NULL,
    user_code_metadata blob DEFAULT NULL,
    device_code_value blob DEFAULT NULL,
    device_code_issued_at timestamp DEFAULT NULL,
    device_code_expires_at timestamp DEFAULT NULL,
    device_code_metadata blob DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------------------
-- 3. oauth2_authorization_consent：授权确认（JdbcOAuth2AuthorizationConsentService）
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS oauth2_authorization_consent (
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorities varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------------------
-- 4. user：本项目登录用户表
--    注意：password 列用 varchar(100)，因为 BCryptPasswordEncoder 生成的哈希是 60 字符，
--    而实体 User.java 上的 length = 45 存不下（ddl-auto=none，运行时以库里的列宽为准）。
--    如需注解与列宽一致，可把实体的 length 改成 100。
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
    id int NOT NULL AUTO_INCREMENT,
    username varchar(45) NOT NULL,
    password varchar(100) DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 示例用户：用户名 admin，密码 666666
-- 哈希由 BCryptPasswordEncoder 生成（可见 src/test/java/SpringOauth2Test.java 的打印结果）。
-- 生产环境请替换为你自己的用户名/密码，或登录后立即修改。
INSERT INTO `user` (username, password)
SELECT 'admin', '$2a$10$2Y13OwtBi9pbjN7BbPZv8uMAIELLfzKLDuqfuy3SR50owt8Z1miGq'
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE username = 'admin');

-- ============================================================================
-- 客户端注册方式（无需手工 INSERT，走接口即可）：
--   curl -X POST http://localhost:9000/easy-oauth2/register-client/register \
--        -H 'Content-Type: application/json' \
--        -d '{"clientId":"demo-client","pwd":"secret123","code":"f78bcc2aa9b245349999b229c509253c"}'
-- 其中 code 对应 application-dev.yml 的 oauth2.register-client.secret-code。
-- 取令牌：
--   curl -u demo-client:secret123 -X POST \
--        -d 'grant_type=client_credentials&scope=all' \
--        http://localhost:9000/easy-oauth2/oauth2/token
-- ============================================================================
