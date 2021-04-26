CREATE TABLE if not exists dentist (
  id            IDENTITY     NOT NULL,
  dentist_name  VARCHAR(50)  UNIQUE NOT NULL,

  CONSTRAINT dentist_pk PRIMARY KEY (id)
);

CREATE TABLE if not exists dentist_visit (
  id            IDENTITY     NOT NULL,
  dentist_id    BIGINT       NOT NULL,
  visit_date    DATE         NOT NULL,
  visit_time    VARCHAR(10)  NOT NULL,

  CONSTRAINT dentist_visit_pk PRIMARY KEY (id),
  FOREIGN KEY (dentist_id) REFERENCES dentist (id)
);

INSERT INTO dentist(dentist_name) VALUES ('Ants');
INSERT INTO dentist(dentist_name) VALUES ('Peeter');

INSERT INTO dentist_visit(dentist_id, visit_date, visit_time) VALUES (1, '2020-10-10', '10:22')
