USE study;


DROP TABLE IF EXISTS tbl_member_role;
DROP TABLE IF EXISTS tbl_member;
CREATE TABLE IF NOT EXISTS tbl_member (
	c_uuid              BINARY(16)      NOT NULL,
	c_user_id           VARCHAR(50)     NOT NULL,
	c_password          VARCHAR(100)    NOT NULL,
	c_enabled           BIT             NOT NULL,
	c_create_date       DATETIME        NOT NULL DEFAULT(NOW()),
	c_update_date       DATETIME        NOT NULL DEFAULT(NOW()),
	
	PRIMARY KEY (c_uuid),
	UNIQUE KEY (c_user_id)
) COMMENT 'jwt study user';

CREATE TABLE IF NOT EXISTS tbl_member_role (
	c_member_uuid      BINARY(16)     NOT NULL,
	c_value            VARCHAR(10)    NOT NULL,
	
	FOREIGN KEY (c_member_uuid) REFERENCES tbl_member (c_uuid)
) COMMENT 'jwt study user roles';
