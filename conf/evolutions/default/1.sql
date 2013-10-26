# --- !Ups

CREATE SEQUENCE user_id_seq;
CREATE TABLE users (
		user_id    integer DEFAULT nextval('user_id_seq') PRIMARY KEY ,
		email      varchar(255) NOT NULL UNIQUE,
		name       varchar(255) NOT NULL,
		password   varchar(255) NOT NULL,
		);

# --- !Downs

DROP TABLE if exists users;
DROP SEQUENCE if exists user_id_seq;
