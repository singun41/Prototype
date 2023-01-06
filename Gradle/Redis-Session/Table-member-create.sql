-- mariaDB 10.6

CREATE TABLE if NOT EXISTS member (
	id          BIGINT         AUTO_INCREMENT PRIMARY KEY NOT NULL,
	user_id     VARCHAR(30)    NOT NULL,
   `password`  VARCHAR(70)    NOT NULL,
   role        VARCHAR(10)    NOT NULL,
   auths       VARCHAR(100)   NOT NULL,
   enabled     bit            NOT NULL DEFAULT FALSE 
);
CREATE UNIQUE INDEX if NOT EXISTS member_UK ON member(user_id);


INSERT INTO member (user_id, `password`, role, auths, enabled)
VALUES ('dev-ops', '$2a$10$l9f/VpJ1fb6uWIwXSgwvUe6mO2qPvwGEFjXK4tsfwh7jfZuUGORVe', 'ADMIN', 'ALL', TRUE);

INSERT INTO member (user_id, `password`, role, auths, enabled)
VALUES ('user1', '$2a$10$l9f/VpJ1fb6uWIwXSgwvUe6mO2qPvwGEFjXK4tsfwh7jfZuUGORVe', 'MANAGER', 'RND,SALES,HR', TRUE);

INSERT INTO member (user_id, `password`, role, auths, enabled)
VALUES ('user2', '$2a$10$l9f/VpJ1fb6uWIwXSgwvUe6mO2qPvwGEFjXK4tsfwh7jfZuUGORVe', 'USER', 'SALES', TRUE);

INSERT INTO member (user_id, `password`, role, auths, enabled)
VALUES ('user3', '$2a$10$l9f/VpJ1fb6uWIwXSgwvUe6mO2qPvwGEFjXK4tsfwh7jfZuUGORVe', 'USER', 'RND', FALSE);

-- encrypted string by Bcryopt. asdf123!
-- $2a$10$l9f/VpJ1fb6uWIwXSgwvUe6mO2qPvwGEFjXK4tsfwh7jfZuUGORVe


SELECT * FROM member;
DESCRIBE member;

-- DROP TABLE member;
