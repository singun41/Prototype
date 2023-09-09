USE study;

-- Jasypt
-- INSERT INTO tbl_member (c_uuid, c_user_id, c_password, c_enabled)
-- VALUES (ordered_uuid(), 'developer', 'SYWGhrR/Hx0jPABjKfi6Dw==', 1);

-- Bcrypt
INSERT INTO tbl_member (c_uuid, c_user_id, c_password, c_enabled)
VALUES (ordered_uuid(), 'developer', '$2a$10$eEVWAZ7A7aXmT4EZtKam3.KrVqjDJC3XpylLq6hp/QK7wFjfX7CoG', 1);

INSERT INTO tbl_member_role
SELECT a.c_uuid, 'ADMIN' 
FROM tbl_member a;

INSERT INTO tbl_member_role
SELECT a.c_uuid, 'USER' 
FROM tbl_member a;

-- UPDATE tbl_member
-- SET c_password = '$2a$10$eEVWAZ7A7aXmT4EZtKam3.KrVqjDJC3XpylLq6hp/QK7wFjfX7CoG'
-- WHERE c_uuid = 0x11ee39c4d6e38ed0084212f9527f3a31;

-- UPDATE tbl_member
-- SET c_password = 'SYWGhrR/Hx0jPABjKfi6Dw=='
-- WHERE c_uuid = 0x11ee39c4d6e38ed0084212f9527f3a31;
