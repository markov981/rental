DROP TABLE IF EXISTS apartments;

CREATE TABLE apartments
(
  id bigserial PRIMARY KEY NOT NULL,
  rent integer NOT NULL,
  number_of_bedrooms integer NOT NULL,
  number_of_bathrooms numeric(4, 2) NOT NULL,
  square_footage integer NOT NULL,
  address varchar(255),
  city varchar(255),
  state varchar(20),
  zip_code varchar(10),
  user_id bigint,
  is_active boolean not null default false
);
