-- ============================================================================
-- Spring Authorization Server 0.3.1 -> 1.3.7 数据库结构迁移（MySQL）
--
-- 依据 SAS 1.3.7 jar 内置的官方 DDL（与本仓库 pom 中 Boot 3.3.13 管理的 SAS 版本一致）：
--   org/springframework/security/oauth2/server/authorization/oauth2-authorization-schema.sql
--   org/springframework/security/oauth2/server/authorization/client/oauth2-registered-client-schema.sql
--   org/springframework/security/oauth2/server/authorization/oauth2-authorization-consent-schema.sql
--
-- 0.3 -> 1.3 的变化只有“新增列”，没有改类型或删列。
-- 必须执行：JdbcOAuth2AuthorizationService / JdbcRegisteredClientRepository 的
-- INSERT 会显式列出这些列，缺列会直接抛 "Unknown column"。
--
-- 执行前请备份；若某列已存在会报 Duplicate column name，跳过该语句即可。
-- ============================================================================

-- oauth2_authorization：新增 authorized_scopes（0.4+）与 user_code_*/device_code_*（设备码流程，1.1+）
ALTER TABLE oauth2_authorization
    ADD COLUMN authorized_scopes varchar(1000) DEFAULT NULL AFTER authorization_grant_type,
    ADD COLUMN user_code_value blob DEFAULT NULL AFTER refresh_token_metadata,
    ADD COLUMN user_code_issued_at timestamp DEFAULT NULL AFTER user_code_value,
    ADD COLUMN user_code_expires_at timestamp DEFAULT NULL AFTER user_code_issued_at,
    ADD COLUMN user_code_metadata blob DEFAULT NULL AFTER user_code_expires_at,
    ADD COLUMN device_code_value blob DEFAULT NULL AFTER user_code_metadata,
    ADD COLUMN device_code_issued_at timestamp DEFAULT NULL AFTER device_code_value,
    ADD COLUMN device_code_expires_at timestamp DEFAULT NULL AFTER device_code_issued_at,
    ADD COLUMN device_code_metadata blob DEFAULT NULL AFTER device_code_expires_at;

-- oauth2_registered_client：新增 post_logout_redirect_uris
ALTER TABLE oauth2_registered_client
    ADD COLUMN post_logout_redirect_uris varchar(1000) DEFAULT NULL AFTER redirect_uris;

-- oauth2_authorization_consent 在 0.3 -> 1.3 之间结构无变化，无需处理。

-- ----------------------------------------------------------------------------
-- 可选清理：SAS 从未实现 password grant（0.3 亦如此），
-- 存量客户端的 authorization_grant_types 里可能残留 "password"。
-- 1.3 仍能解析该值（只是请求时会返回 unsupported_grant_type），
-- 为保持与 RegisteredClientServiceImpl 新写入的数据一致，可按需执行：
-- ----------------------------------------------------------------------------
-- UPDATE oauth2_registered_client
--    SET authorization_grant_types = TRIM(BOTH ',' FROM REPLACE(CONCAT(',', authorization_grant_types, ','), ',password,', ','))
--  WHERE authorization_grant_types LIKE '%password%';

-- 注意：oauth2_registered_client.token_settings / client_settings 是 JSON 文本，
-- 1.3 读取旧 JSON 时缺失的键会走默认值，不需要 DDL 变更。
