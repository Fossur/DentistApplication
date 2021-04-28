CREATE TABLE if not exists dentist (
  id                IDENTITY     NOT NULL,
  dentist_name      VARCHAR(50)  UNIQUE NOT NULL,
  session_length    INT          NOT NULL default 30,

  CONSTRAINT dentist_pk PRIMARY KEY (id)
);

CREATE TABLE if not exists dentist_visit (
  id                IDENTITY     NOT NULL,
  dentist_id        BIGINT       NOT NULL,
  visit_date        DATE         NOT NULL,
  visit_start_time  VARCHAR(10)  NOT NULL,
  visit_end_time    VARCHAR(10)  NOT NULL,
  visitor_name      VARCHAR(50)  NOT NULL,
  visitor_email     VARCHAR(50)  NOT NULL,

  CONSTRAINT dentist_visit_pk PRIMARY KEY (id),
  FOREIGN KEY (dentist_id) REFERENCES dentist (id)
);

INSERT INTO dentist(dentist_name, session_length) VALUES ('Ants', 30);
INSERT INTO dentist(dentist_name, session_length) VALUES ('Peeter', 45);

INSERT INTO dentist_visit(dentist_id, visit_date, visit_start_time, visit_end_time, visitor_name, visitor_email) VALUES (1, '2020-10-10', '10:30', '11:00', 'Test', 'foo@bar.com')
