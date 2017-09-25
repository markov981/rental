DROP TABLE IF EXISTS users;

CREATE TABLE users
(
  id bigserial PRIMARY KEY NOT NULL,
  email varchar(255) UNIQUE NOT NULL,
  password varchar(255) NOT NULL,
  first_name varchar(255) NOT NULL,
  last_name varchar(255) NOT NULL
);