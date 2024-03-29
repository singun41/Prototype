USE study;

-- Jasypt : asdf
-- INSERT INTO tbl_member (c_uuid, c_user_id, c_password, c_enabled)
-- VALUES (ordered_uuid(), 'admin', 'SYWGhrR/Hx0jPABjKfi6Dw==', 1);

-- Bcrypt : asdf
INSERT INTO tbl_member (c_uuid, c_user_id, c_password, c_enabled)
VALUES (ordered_uuid(), 'admin', '$2a$10$eEVWAZ7A7aXmT4EZtKam3.KrVqjDJC3XpylLq6hp/QK7wFjfX7CoG', 1);

INSERT INTO tbl_member_role
SELECT a.c_uuid, 'ADMIN' 
FROM tbl_member a
WHERE a.c_user_id = 'admin';

INSERT INTO tbl_member_role
SELECT a.c_uuid, 'USER' 
FROM tbl_member a
WHERE a.c_user_id = 'admin';



-- normal user
INSERT INTO tbl_member (c_uuid, c_user_id, c_password, c_enabled)
VALUES (ordered_uuid(), 'user1', '$2a$10$eEVWAZ7A7aXmT4EZtKam3.KrVqjDJC3XpylLq6hp/QK7wFjfX7CoG', 1);

INSERT INTO tbl_member_role
SELECT a.c_uuid, 'USER' 
FROM tbl_member a
WHERE a.c_user_id = 'user1';



-- disabled user
INSERT INTO tbl_member (c_uuid, c_user_id, c_password, c_enabled)
VALUES (ordered_uuid(), 'user2', '$2a$10$eEVWAZ7A7aXmT4EZtKam3.KrVqjDJC3XpylLq6hp/QK7wFjfX7CoG', 0);

INSERT INTO tbl_member_role
SELECT a.c_uuid, 'USER' 
FROM tbl_member a
WHERE a.c_user_id = 'user2';



-- manager
INSERT INTO tbl_member (c_uuid, c_user_id, c_password, c_enabled)
VALUES (ordered_uuid(), 'manager', '$2a$10$eEVWAZ7A7aXmT4EZtKam3.KrVqjDJC3XpylLq6hp/QK7wFjfX7CoG', 1);

INSERT INTO tbl_member_role
SELECT a.c_uuid, 'MANAGER' 
FROM tbl_member a
WHERE a.c_user_id = 'manager';
