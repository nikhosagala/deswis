# --- !Ups

CREATE TABLE document (
  id          INTEGER NOT NULL,
  name        VARCHAR(255),
  url         VARCHAR(255),
  uploaded_at TIMESTAMPTZ,
  CONSTRAINT pk_document PRIMARY KEY (id)
);

CREATE TABLE destination (
  id              INTEGER NOT NULL,
  name            VARCHAR(255),
  address         VARCHAR(255),
  phone           VARCHAR(255),
  description     TEXT,
  distance        NUMERIC,
  duration        NUMERIC,
  length_of_visit NUMERIC,
  rating          NUMERIC DEFAULT 0,
  tariff          NUMERIC,
  url_source      VARCHAR(255),
  monday          VARCHAR(50),
  tuesday         VARCHAR(50),
  wednesday       VARCHAR(50),
  thursday        VARCHAR(50),
  friday          VARCHAR(50),
  saturday        VARCHAR(50),
  sunday          VARCHAR(50),
  categories      VARCHAR(100),
  google_place_id INTEGER DEFAULT 1,
  created_at      TIMESTAMPTZ,
  updated_at      TIMESTAMPTZ,
  CONSTRAINT pk_destination PRIMARY KEY (id)
);

CREATE TABLE category (
  id         INTEGER NOT NULL,
  name       VARCHAR(255),
  children   VARCHAR(255),
  hint       TEXT,
  created_at TIMESTAMPTZ,
  updated_at TIMESTAMPTZ,
  CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE google_place (
  id           INTEGER NOT NULL,
  name         VARCHAR(255),
  lat          NUMERIC,
  lng          NUMERIC,
  place_id     VARCHAR(100),
  content_type VARCHAR(20),
  types        VARCHAR(100),
  image_data   BYTEA,
  created_at   TIMESTAMPTZ,
  updated_at   TIMESTAMPTZ,
  CONSTRAINT pk_google_place PRIMARY KEY (id)
);

CREATE TABLE recommendation (
  id              INTEGER NOT NULL,
  distance_weight NUMERIC,
  duration_weight NUMERIC,
  interest_weight NUMERIC,
  rating_weight   NUMERIC,
  lat             NUMERIC,
  lng             NUMERIC,
  name            VARCHAR(50),
  process_time    NUMERIC,
  rating          NUMERIC DEFAULT 0,
  created_at      TIMESTAMPTZ,
  updated_at      TIMESTAMPTZ,
  CONSTRAINT pk_recommendation PRIMARY KEY (id)
);

CREATE TABLE recommendation_detail (
  id                INTEGER NOT NULL,
  category_id       INTEGER,
  recommendation_id INTEGER,
  interest_value    NUMERIC,
  created_at        TIMESTAMPTZ,
  updated_at        TIMESTAMPTZ,
  CONSTRAINT pk_recommendation_detail PRIMARY KEY (id)
);

CREATE TABLE recommendation_result (
  id                INTEGER NOT NULL,
  destination_id    INTEGER,
  recommendation_id INTEGER,
  interest_value    NUMERIC,
  created_at        TIMESTAMPTZ,
  updated_at        TIMESTAMPTZ,
  CONSTRAINT pk_recommendation_result PRIMARY KEY (id)
);

CREATE TABLE survey (
  id                INTEGER NOT NULL,
  name              VARCHAR(50),
  age               NUMERIC,
  familiar          VARCHAR(20),
  pu1               VARCHAR(3),
  pu2               VARCHAR(3),
  pu3               VARCHAR(3),
  pu4               VARCHAR(3),
  pu5               VARCHAR(3),
  pu6               VARCHAR(3),
  pu7               VARCHAR(3),
  eou1              VARCHAR(3),
  eou2              VARCHAR(3),
  eou3              VARCHAR(3),
  eou4              VARCHAR(3),
  eou5              VARCHAR(3),
  tr1               VARCHAR(3),
  tr2               VARCHAR(3),
  tr3               VARCHAR(3),
  tr4               VARCHAR(3),
  pe1               VARCHAR(3),
  pe2               VARCHAR(3),
  pe3               VARCHAR(3),
  pe4               VARCHAR(3),
  bi1               VARCHAR(3),
  bi2               VARCHAR(3),
  bi3               VARCHAR(3),
  comment           TEXT,
  recommendation_id INTEGER,
  created_at        TIMESTAMPTZ,
  updated_at        TIMESTAMPTZ,
  CONSTRAINT pk_survey PRIMARY KEY (id)
);

CREATE SEQUENCE document_seq;
CREATE SEQUENCE destination_seq;
CREATE SEQUENCE category_seq;
CREATE SEQUENCE google_place_seq;
CREATE SEQUENCE recommendation_seq;
CREATE SEQUENCE recommendation_detail_seq;
CREATE SEQUENCE recommendation_result_seq;
CREATE SEQUENCE survey_seq;

ALTER TABLE destination
  ADD CONSTRAINT fk_destination_google_place_1 FOREIGN KEY (google_place_id) REFERENCES google_place (id);
CREATE INDEX ix_destination_google_place_1
  ON destination (google_place_id);

ALTER TABLE recommendation_detail
  ADD CONSTRAINT fk_recommendation_detail_recommendation_1 FOREIGN KEY (recommendation_id) REFERENCES recommendation (id);
CREATE INDEX ix_recommendation_detail_recommendation_1
  ON recommendation_detail (recommendation_id);

ALTER TABLE recommendation_detail
  ADD CONSTRAINT fk_recommendation_detail_category_1 FOREIGN KEY (category_id) REFERENCES category (id);
CREATE INDEX ix_recommendation_detail_category_1
  ON recommendation_detail (category_id);

ALTER TABLE recommendation_result
  ADD CONSTRAINT fk_recommendation_result_destination_1 FOREIGN KEY (destination_id) REFERENCES destination (id);
CREATE INDEX ix_recommendation_result_destination_1
  ON recommendation_result (destination_id);

ALTER TABLE recommendation_result
  ADD CONSTRAINT fk_recommendation_result_recommendation_1 FOREIGN KEY (recommendation_id) REFERENCES recommendation (id);
CREATE INDEX ix_recommendation_result_recommendation_1
  ON recommendation_result (recommendation_id);

# --- !Downs

DROP TABLE IF EXISTS document CASCADE;

DROP TABLE IF EXISTS destination CASCADE;

DROP TABLE IF EXISTS category CASCADE;

DROP TABLE IF EXISTS google_place CASCADE;

DROP TABLE IF EXISTS recommendation CASCADE;

DROP TABLE IF EXISTS recommendation_detail CASCADE;

DROP TABLE IF EXISTS recommendation_result CASCADE;

DROP TABLE IF EXISTS survey CASCADE;

DROP SEQUENCE IF EXISTS document_seq;

DROP SEQUENCE IF EXISTS destination_seq;

DROP SEQUENCE IF EXISTS category_seq;

DROP SEQUENCE IF EXISTS google_place_seq;

DROP SEQUENCE IF EXISTS recommendation_seq;

DROP SEQUENCE IF EXISTS recommendation_detail_seq;

DROP SEQUENCE IF EXISTS recommendation_result_seq;

DROP SEQUENCE IF EXISTS survey_seq;