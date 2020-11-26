CREATE USER IF NOT EXISTS ITAG_USER password 'password';
CREATE USER IF NOT EXISTS RESET_USER password 'password';
CREATE USER IF NOT EXISTS OLD_NOMIS_USER password 'password';
CREATE USER IF NOT EXISTS ITAG_USER_ADM password 'password123456';
CREATE USER IF NOT EXISTS CA_USER password 'password123456';
CREATE USER IF NOT EXISTS CA_USER_TEST password 'licences123456';
CREATE USER IF NOT EXISTS RESET_TEST_USER password 'password123456';
CREATE USER IF NOT EXISTS CA_USER_MULTI password 'password123456';
CREATE USER IF NOT EXISTS RO_USER password 'password123456';
CREATE USER IF NOT EXISTS RO_DEMO password 'password123456';
CREATE USER IF NOT EXISTS RO_USER_TEST password 'licences123456';
CREATE USER IF NOT EXISTS RO_USER_MULTI password 'password123456';
CREATE USER IF NOT EXISTS DM_USER password 'password123456';
CREATE USER IF NOT EXISTS DM_USER_TEST password 'licences123456';
CREATE USER IF NOT EXISTS DM_USER_MULTI password 'password123456';
CREATE USER IF NOT EXISTS NOMIS_BATCHLOAD password 'password123456';
CREATE USER IF NOT EXISTS LOCKED_USER password 'password123456';
CREATE USER IF NOT EXISTS EXPIRED_USER password 'password123456';
CREATE USER IF NOT EXISTS EXPIRED_TEST_USER password 'password123456';
CREATE USER IF NOT EXISTS EXPIRED_TEST2_USER password 'password123456';
CREATE USER IF NOT EXISTS EXPIRED_TEST3_USER password 'password123456';
CREATE USER IF NOT EXISTS OMIC_USER password 'password';
CREATE USER IF NOT EXISTS LAA_USER password 'password';
CREATE USER IF NOT EXISTS IEP_USER password 'password';
CREATE USER IF NOT EXISTS SECURE_CASENOTE_USER password 'password123456';
CREATE USER IF NOT EXISTS PPL_USER password 'password123456';
CREATE USER IF NOT EXISTS UOF_REVIEWER_USER password 'password123456';
CREATE USER IF NOT EXISTS UOF_COORDINATOR_USER password 'password123456';
CREATE USER IF NOT EXISTS RCTL_USER password 'password123456';
CREATE USER IF NOT EXISTS POM_USER password 'password123456';
CREATE USER IF NOT EXISTS PF_RO_USER password 'password123456';
CREATE USER IF NOT EXISTS NOMIS_NEVER_LOGGED_IN password 'password123456';
CREATE USER IF NOT EXISTS SOC_PRISON_LOCAL password 'password123456';
CREATE USER IF NOT EXISTS LICENCE_READONLY_TEST password 'password123456';
CREATE USER IF NOT EXISTS NOMIS_ENABLED_AUTH_DISABLED password 'password123456';
CREATE USER IF NOT EXISTS NOMIS_LOCKED_AUTH_DISABLED password 'password123456';
CREATE USER IF NOT EXISTS PPM_ANALYST_USER password 'password123456';
CREATE USER IF NOT EXISTS PRISON_COLLATOR_LOCAL password 'password123456';
CREATE USER IF NOT EXISTS PRISON_ANALYST_LOCAL password 'password123456';
CREATE USER IF NOT EXISTS NOMIS_EMAIL password 'password123456';
CREATE USER IF NOT EXISTS PF_LOCAL_READER_USER password 'password123456';
CREATE USER IF NOT EXISTS PF_NATIONAL_READER_USER password 'password123456';
CREATE USER IF NOT EXISTS PF_HQ_USER password 'password123456';

INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (1, 'ITAG', 'USER', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (2, 'OLD', 'NOMIS USER', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (3, 'Licence', 'Case Admin', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (4, 'Licence', 'Responsible Officer', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (5, 'Licence', 'Decision Maker', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (6, 'Licence', 'Batchloader', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (7, 'User', 'Locked', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (8, 'Catherine', 'Amos', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (9, 'Licence', 'Case Admin Multi', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (10, 'Licence', 'Responsible Officer Demo', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (11, 'Ryan', 'Orton', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (12, 'Licence', 'Responsible Officer Multi', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (13, 'Diane', 'Matthews', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (14, 'Licence', 'Decision Maker Multi', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (15, 'Omic', 'User', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (16, 'User', 'Expired', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (17, 'Change', 'Password', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (18, 'Change', 'Password2', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (19, 'Change', 'Password3', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (20, 'Local', 'Admin', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (21, 'Ca', 'UserTest', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (22, 'Iep', 'UserTest', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (23, 'Secure Case', 'Note', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (24, 'Prison', 'Prevent Lead', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (26, 'Use of Force', 'Reviewer', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (27, 'Pathfinder', 'RctlUser', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (29, 'Pom', 'User', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (30, 'Pathfinder', 'Read Only', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (31, 'Use of Force', 'Coordinator', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (32, 'User', 'Never', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (33, 'Licence', 'Read Only', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (34, 'Soc', 'Prison Local', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (35, 'nomis enabled', 'auth disabled', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (36, 'nomis disabled', 'auth disabled', 'INACT');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (37, 'Ppm', 'Analyst', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (38, 'Prison', 'Collator', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (39, 'Prison', 'Analyst', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (40, 'nomis', 'email', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS)
VALUES (41, 'Reset', 'User', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS) VALUES (42, 'PF local', 'Reader', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS) VALUES (43, 'PF national', 'Reader', 'ACTIVE');
INSERT INTO STAFF_MEMBERS (STAFF_ID, FIRST_NAME, LAST_NAME, STATUS) VALUES (44, 'PF HQ', 'User', 'ACTIVE');

INSERT INTO STAFF_USER_ACCOUNTS (username, staff_user_type, staff_id, working_caseload_id)
VALUES ('ITAG_USER', 'GENERAL', 1, 'MDI'),
  ('CA_USER', 'GENERAL', 3, 'BEL'),
  ('RO_USER', 'GENERAL', 4, 'BEL'),
  ('DM_USER', 'GENERAL', 5, 'BEL'),
  ('NOMIS_BATCHLOAD', 'GENERAL', 6, 'BEL'),
  ('CA_USER_TEST', 'GENERAL', 8, 'BEL'),
  ('CA_USER_MULTI', 'GENERAL', 9, 'BEL'),
  ('RO_DEMO', 'GENERAL', 10, 'BEL'),
  ('RO_USER_TEST', 'GENERAL', 11, 'BEL'),
  ('RO_USER_MULTI', 'GENERAL', 12, 'BEL'),
  ('DM_USER_TEST', 'GENERAL', 13, 'BEL'),
  ('DM_USER_MULTI', 'GENERAL', 14, 'BEL'),
  ('RESET_TEST_USER', 'GENERAL', 21, 'BEL'),
  ('IEP_USER', 'GENERAL', 22, 'MDI'),
  ('SECURE_CASENOTE_USER', 'GENERAL', 23, 'LEI'),
  ('PPL_USER', 'GENERAL', 24, 'BEL'),
  ('UOF_REVIEWER_USER', 'GENERAL', 26, 'BEL'),
  ('RCTL_USER', 'GENERAL', 27, 'BEL'),
  ('POM_USER', 'GENERAL', 29, 'BEL'),
  ('PF_RO_USER', 'GENERAL', 30, 'BEL'),
  ('UOF_COORDINATOR_USER', 'GENERAL', 31, 'BEL'),
  ('NOMIS_NEVER_LOGGED_IN', 'GENERAL', 32, 'BEL'),
  ('LICENCE_READONLY_TEST', 'GENERAL', 33, 'BEL'),
  ('SOC_PRISON_LOCAL', 'GENERAL', 34, 'BEL'),
  ('NOMIS_ENABLED_AUTH_DISABLED', 'GENERAL', 35, 'BEL'),
  ('NOMIS_LOCKED_AUTH_DISABLED', 'GENERAL', 36, 'BEL'),
  ('PPM_ANALYST_USER', 'GENERAL', 37, 'BEL'),
  ('PRISON_COLLATOR_LOCAL', 'GENERAL', 38, 'BEL'),
  ('PRISON_ANALYST_LOCAL', 'GENERAL', 39, 'BEL'),
  ('NOMIS_EMAIL', 'GENERAL', 40, 'BEL'),
  ('RESET_USER', 'GENERAL', 41, 'MDI'),
  ('PF_LOCAL_READER_USER', 'GENERAL', 42, 'MDI');

INSERT INTO STAFF_USER_ACCOUNTS (username, staff_user_type, staff_id)
VALUES ('ITAG_USER_ADM', 'ADMIN', 1),
  ('OLD_NOMIS_USER', 'GENERAL', 2),
  ('LOCKED_USER', 'GENERAL', 7),
  ('OMIC_USER', 'GENERAL', 15),
  ('EXPIRED_USER', 'GENERAL', 16),
  ('EXPIRED_TEST_USER', 'GENERAL', 17),
  ('EXPIRED_TEST2_USER', 'GENERAL', 18),
  ('EXPIRED_TEST3_USER', 'GENERAL', 19),
  ('LAA_USER', 'ADMIN', 20),
  ('PF_NATIONAL_READER_USER', 'GENERAL', 43),
  ('PF_HQ_USER', 'GENERAL', 44);

INSERT INTO DBA_USERS (username, account_status, profile)
VALUES ('ITAG_USER', 'OPEN', 'TAG_GENERAL'),
  ('RESET_USER', 'OPEN', 'TAG_GENERAL'),
  ('ITAG_USER_ADM', 'OPEN', 'TAG_ADMIN'),
  ('OLD_NOMIS_USER', 'OPEN', 'TAG_GENERAL'),
  ('CA_USER', 'OPEN', 'TAG_GENERAL'),
  ('RO_USER', 'OPEN', 'TAG_GENERAL'),
  ('DM_USER', 'OPEN', 'TAG_GENERAL'),
  ('NOMIS_BATCHLOAD', 'OPEN', 'TAG_GENERAL'),
  ('LOCKED_USER', 'LOCKED', 'TAG_GENERAL'),
  ('EXPIRED_USER', 'EXPIRED', 'TAG_GENERAL'),
  ('EXPIRED_TEST_USER', 'EXPIRED', 'TAG_GENERAL'),
  ('EXPIRED_TEST2_USER', 'EXPIRED', 'TAG_GENERAL'),
  ('EXPIRED_TEST3_USER', 'EXPIRED', 'TAG_GENERAL'),
  ('CA_USER_TEST', 'OPEN', 'TAG_GENERAL'),
  ('RESET_TEST_USER', 'OPEN', 'TAG_GENERAL'),
  ('CA_USER_MULTI', 'OPEN', 'TAG_GENERAL'),
  ('RO_DEMO', 'OPEN', 'TAG_GENERAL'),
  ('RO_USER_TEST', 'OPEN', 'TAG_GENERAL'),
  ('RO_USER_MULTI', 'OPEN', 'TAG_GENERAL'),
  ('DM_USER_TEST', 'OPEN', 'TAG_GENERAL'),
  ('DM_USER_MULTI', 'OPEN', 'TAG_GENERAL'),
  ('OMIC_USER', 'OPEN', 'TAG_GENERAL'),
  ('LAA_USER', 'OPEN', 'TAG_ADMIN'),
  ('IEP_USER', 'OPEN', 'TAG_GENERAL'),
  ('SECURE_CASENOTE_USER', 'OPEN', 'TAG_GENERAL'),
  ('PPL_USER', 'OPEN', 'TAG_GENERAL'),
  ('UOF_REVIEWER_USER', 'OPEN', 'TAG_GENERAL'),
  ('UOF_COORDINATOR_USER', 'OPEN', 'TAG_GENERAL'),
  ('RCTL_USER', 'OPEN', 'TAG_GENERAL'),
  ('POM_USER', 'OPEN', 'TAG_GENERAL'),
  ('PF_RO_USER', 'OPEN', 'TAG_GENERAL'),
  ('NOMIS_NEVER_LOGGED_IN', 'OPEN', 'TAG_GENERAL'),
  ('LICENCE_READONLY_TEST', 'OPEN', 'TAG_GENERAL'),
  ('SOC_PRISON_LOCAL', 'OPEN', 'TAG_GENERAL'),
  ('NOMIS_ENABLED_AUTH_DISABLED', 'OPEN', 'TAG_GENERAL'),
  ('NOMIS_LOCKED_AUTH_DISABLED', 'EXPIRED & LOCKED', 'TAG_GENERAL'),
  ('PPM_ANALYST_USER', 'OPEN', 'TAG_GENERAL'),
  ('PRISON_COLLATOR_LOCAL', 'OPEN', 'TAG_GENERAL'),
  ('PRISON_ANALYST_LOCAL', 'OPEN', 'TAG_GENERAL'),
  ('NOMIS_EMAIL', 'OPEN', 'TAG_GENERAL');

INSERT INTO SYS.USER$ (name, spare4)
VALUES ('ITAG_USER', '{bcrypt}$2a$10$9rVgms..dZ3gnPSt4JWPA.Oan4MrDHvcx1c.HuYqeMD5rVFmf0C3G'),
  ('RESET_USER', '{bcrypt}$2a$10$9rVgms..dZ3gnPSt4JWPA.Oan4MrDHvcx1c.HuYqeMD5rVFmf0C3G'),
  ('ITAG_USER_ADM', '{bcrypt}$2a$10$0zomTd5coSOKnSBMkCyEiei72HwBLJZrSpoqL1GVwr4LNp.KAq.FK'),
  ('OLD_NOMIS_USER', '{bcrypt}$2a$10$EaaM7jp4e/Y1q8zR..eEYOwJOnGJmHIItZfIxR6gVgUP4xT8qXVEm'),
  ('CA_USER', '{bcrypt}$2a$10$Fmcp2KUKRW53US3EJfsxkOh.ekZhqz5.Baheb9E98QLwEFLb9csxy'),
  ('RO_USER', '{bcrypt}$2a$10$ordeqG5gMJHWXm9SDsN0q.PgYbwRmC5idWoFTEWIe2hoIS8IKh3dK'),
  ('DM_USER', '{bcrypt}$2a$10$S0K9erqzMttMHhs5hdi8DuE8lk7F1ajsAD5pAMmgmJBc/8QsjzHzy'),
  ('NOMIS_BATCHLOAD', '{bcrypt}$2a$10$bAzE0xo8XsKMBchiKmnpEuPZqXV.0/RVVhya7v7kkGoRLTpt2Iwxa'),
  ('LOCKED_USER', '{bcrypt}$2a$10$BPCJlKWhaICns8ax5JHd8er8Ti6zy8VH3LiFKtt1M2A4iVyJv1NyW'),
  ('EXPIRED_USER', '{bcrypt}$2a$10$kJf5g6MQuORDouOnrlpoQOupk5ieUsgcp5v0TtHVzR3mn53b37vby'),
  ('EXPIRED_TEST_USER', '{bcrypt}$2a$10$kJf5g6MQuORDouOnrlpoQOupk5ieUsgcp5v0TtHVzR3mn53b37vby'),
  ('EXPIRED_TEST2_USER', '{bcrypt}$2a$10$kJf5g6MQuORDouOnrlpoQOupk5ieUsgcp5v0TtHVzR3mn53b37vby'),
  ('EXPIRED_TEST3_USER', '{bcrypt}$2a$10$kJf5g6MQuORDouOnrlpoQOupk5ieUsgcp5v0TtHVzR3mn53b37vby'),
  ('CA_USER_TEST', '{bcrypt}$2a$10$vgyF/EFvlol3pwwi0n6ZgeI6C90xY5exW6tETbdDBHB4xpDYzuV9q'),
  ('RESET_TEST_USER', '{bcrypt}$2a$10$Fmcp2KUKRW53US3EJfsxkOh.ekZhqz5.Baheb9E98QLwEFLb9csxy'),
  ('CA_USER_MULTI', '{bcrypt}$2a$10$.uXmUgIPCFv1c93IFzZ48.N3fUlY25YJcq74SPwvPzp93iwId4SzW'),
  ('RO_DEMO', '{bcrypt}$2a$10$Xtg49KDA5dBQ2kk3MqsG7erUsUV.vid.s9m2ikLMBWTGTynBG9v4K'),
  ('RO_USER_TEST', '{bcrypt}$2a$10$WIthoS9sIXg2mm3tXW6kd./w.xkmfwrSOIUDu.3KNLypCmIpltuvS'),
  ('RO_USER_MULTI', '{bcrypt}$2a$10$MGhfSUGrb9DbfMQ/RgxIpOepxw/.R53bZ.tgcTKrEgEHyOsL.VBj6'),
  ('DM_USER_TEST', '{bcrypt}$2a$10$3uLXHUVAN3TPp0l4FAlDXe/DTjxp9BsjkJ9WXnRAp2zQkbEVYmGuC'),
  ('DM_USER_MULTI', '{bcrypt}$2a$10$JPsL6m0wCXIq5Zdjo8yCx.kYUJBxkPb1gdsAsCqsqgMaJmqJpLiyS'),
  ('OMIC_USER', '{bcrypt}$2a$10$8lRtC2ndt.nb004kZTNm6O2DqJmakSwKfcmWXB5adKMb9NUWkA1Tm'),
  ('LAA_USER', '{bcrypt}$2a$10$Efc5x3nXUVBj84SZFdcCzuSiaoztJVJkqXFwgUq6xXRKb0dyUWlK.'),
  ('IEP_USER', '{bcrypt}$2a$10$Efc5x3nXUVBj84SZFdcCzuSiaoztJVJkqXFwgUq6xXRKb0dyUWlK.'),
  ('SECURE_CASENOTE_USER', '{bcrypt}$2a$10$kJf5g6MQuORDouOnrlpoQOupk5ieUsgcp5v0TtHVzR3mn53b37vby'),
  ('PPL_USER', '{bcrypt}$2a$10$Fmcp2KUKRW53US3EJfsxkOh.ekZhqz5.Baheb9E98QLwEFLb9csxy'),
  ('UOF_REVIEWER_USER', '{bcrypt}$2a$10$Fmcp2KUKRW53US3EJfsxkOh.ekZhqz5.Baheb9E98QLwEFLb9csxy'),
  ('UOF_COORDINATOR_USER', '{bcrypt}$2a$10$wtBtXQFeDjlnHD.V78q0Bu229Qw2m8CsTKEaHHNLLPBPmyMPppKwe'),
  ('RCTL_USER', '{bcrypt}$2a$10$Fmcp2KUKRW53US3EJfsxkOh.ekZhqz5.Baheb9E98QLwEFLb9csxy'),
  ('POM_USER', '{bcrypt}$2a$10$Fmcp2KUKRW53US3EJfsxkOh.ekZhqz5.Baheb9E98QLwEFLb9csxy'),
  ('PF_RO_USER', '{bcrypt}$2a$10$Fmcp2KUKRW53US3EJfsxkOh.ekZhqz5.Baheb9E98QLwEFLb9csxy'),
  ('NOMIS_NEVER_LOGGED_IN', '{bcrypt}$2a$10$Fmcp2KUKRW53US3EJfsxkOh.ekZhqz5.Baheb9E98QLwEFLb9csxy'),
  ('LICENCE_READONLY_TEST', '{bcrypt}$2a$10$Fmcp2KUKRW53US3EJfsxkOh.ekZhqz5.Baheb9E98QLwEFLb9csxy'),
  ('SOC_PRISON_LOCAL', '{bcrypt}$2a$10$Fmcp2KUKRW53US3EJfsxkOh.ekZhqz5.Baheb9E98QLwEFLb9csxy'),
  ('NOMIS_ENABLED_AUTH_DISABLED', '{bcrypt}$2a$10$Fmcp2KUKRW53US3EJfsxkOh.ekZhqz5.Baheb9E98QLwEFLb9csxy'),
  ('NOMIS_LOCKED_AUTH_DISABLED', '{bcrypt}$2a$10$Fmcp2KUKRW53US3EJfsxkOh.ekZhqz5.Baheb9E98QLwEFLb9csxy'),
  ('PPM_ANALYST_USER', '{bcrypt}$2a$10$Fmcp2KUKRW53US3EJfsxkOh.ekZhqz5.Baheb9E98QLwEFLb9csxy'),
  ('PRISON_COLLATOR_LOCAL', '{bcrypt}$2a$10$Fmcp2KUKRW53US3EJfsxkOh.ekZhqz5.Baheb9E98QLwEFLb9csxy'),
  ('PRISON_ANALYST_LOCAL', '{bcrypt}$2a$10$Fmcp2KUKRW53US3EJfsxkOh.ekZhqz5.Baheb9E98QLwEFLb9csxy'),
  ('NOMIS_EMAIL', '{bcrypt}$2a$10$Fmcp2KUKRW53US3EJfsxkOh.ekZhqz5.Baheb9E98QLwEFLb9csxy');

INSERT INTO PERSONNEL_IDENTIFICATIONS (STAFF_ID, IDENTIFICATION_TYPE, IDENTIFICATION_NUMBER) VALUES (1, 'YJAF', 'test@yjaf.gov.uk');
INSERT INTO PERSONNEL_IDENTIFICATIONS (STAFF_ID, IDENTIFICATION_TYPE, IDENTIFICATION_NUMBER) VALUES (2, 'YJAF', 'olduser@yjaf.gov.uk');

INSERT INTO CASELOADS (CASELOAD_ID, DESCRIPTION, CASELOAD_TYPE) VALUES ('NWEB', 'Magic API Caseload', 'APP');
INSERT INTO CASELOADS (CASELOAD_ID, DESCRIPTION, CASELOAD_TYPE, CASELOAD_FUNCTION) VALUES ('MDI', 'Moorlands', 'INST', 'GENERAL');
INSERT INTO CASELOADS (CASELOAD_ID, DESCRIPTION, CASELOAD_TYPE, CASELOAD_FUNCTION) VALUES ('CADM', 'Central Admin', 'INST', 'ADMIN');

INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'ITAG_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('MDI', 'ITAG_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('MDI', 'OLD_NOMIS_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('CADM', 'ITAG_USER_ADM', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'CA_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'RO_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'DM_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'NOMIS_BATCHLOAD', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'LOCKED_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'EXPIRED_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'EXPIRED_TEST_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'EXPIRED_TEST2_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'EXPIRED_TEST3_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'ITAG_USER_ADM', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'CA_USER_TEST', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'RESET_TEST_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'CA_USER_MULTI', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'RO_DEMO', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'RO_USER_TEST', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'RO_USER_MULTI', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'DM_USER_TEST', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'DM_USER_MULTI', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'OMIC_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'LAA_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('CADM', 'LAA_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'IEP_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'SECURE_CASENOTE_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'PPL_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'UOF_REVIEWER_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'UOF_COORDINATOR_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'RCTL_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'POM_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'PF_RO_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'NOMIS_NEVER_LOGGED_IN', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'LICENCE_READONLY_TEST', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'SOC_PRISON_LOCAL', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'NOMIS_ENABLED_AUTH_DISABLED', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'NOMIS_LOCKED_AUTH_DISABLED', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'PPM_ANALYST_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'PRISON_COLLATOR_LOCAL', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'PRISON_ANALYST_LOCAL', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'PF_LOCAL_READER_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'PF_NATIONAL_READER_USER', now());
INSERT INTO USER_ACCESSIBLE_CASELOADS (CASELOAD_ID, USERNAME, START_DATE) VALUES ('NWEB', 'PF_HQ_USER', now());

INSERT INTO OMS_ROLES (ROLE_ID, ROLE_CODE, ROLE_NAME, ROLE_SEQ, ROLE_TYPE, ROLE_FUNCTION)
VALUES (1, 'OMIC_ADMIN', 'Omic Administrator', 1, 'APP', 'GENERAL'),
  (-1, '900', 'Some Old Role', 99, 'INST', 'GENERAL'),
  (3, 'CENTRAL_ADMIN', 'All Powerful Admin', 1, 'INST', 'ADMIN'),
  (4, 'KW_MIGRATION', 'KW Migration', 1, 'APP', 'ADMIN'),
  (5, 'NOMIS_BATCHLOAD', 'Nomis BatchLoad', 1, 'APP', 'ADMIN'),
  (6, 'MAINTAIN_ACCESS_ROLES', 'Maintain Access Roles', 1, 'APP', 'ADMIN'),
  (7, 'MAINTAIN_ACCESS_ROLES_ADMIN', 'Maintain Access Roles Admin', 1, 'APP', 'ADMIN'),
  (8, 'GLOBAL_SEARCH', 'Global Search', 1, 'APP', 'ADMIN'),
  (11, 'LICENCE_CA', 'Licence Case Admin', 1, 'APP', 'GENERAL'),
  (12, 'LICENCE_RO', 'Licence Responsible Officer', 2, 'APP', 'GENERAL'),
  (13, 'LICENCE_DM', 'Licence Decision Maker', 3, 'APP', 'GENERAL'),
  (14, 'OAUTH_ADMIN', 'Oauth Admin', 99, 'APP', 'ADMIN'),
  (15, 'SYSTEM_READ_ONLY', 'System Read Only', 99, 'APP', 'ADMIN'),
  (16, 'INACTIVE_BOOKINGS', 'View Inactive Bookings', 99, 'APP', 'GENERAL'),
  (17, 'CREATE_CATEGORISATION', 'Create Category assessments', 99, 'APP', 'GENERAL'),
  (18, 'APPROVE_CATEGORISATION', 'Approve Category assessments', 99, 'APP', 'GENERAL'),
  (19, 'MAINTAIN_OAUTH_USERS', 'Maintain oauth users (admin)', 99, 'APP', 'ADMIN'),
  (20, 'CATEGORISATION_SECURITY', 'Security Cat tool role', 99, 'APP', 'GENERAL'),
  (21, 'MAINTAIN_IEP', 'Maintain IEP', 99, 'APP', 'GENERAL'),
  (22, 'VIEW_SENSITIVE_CASE_NOTES', 'View Secure Case Notes', 99, 'APP', 'GENERAL'),
  (23, 'ADD_SENSITIVE_CASE_NOTES', 'Add Secure Case Notes', 99, 'APP', 'GENERAL'),
  (24, 'PF_STD_PRISON', 'Pathfinder Standard Prison', 99, 'APP', 'GENERAL'),
  (26, 'USE_OF_FORCE_REVIEWER', 'Use of force reviewer', 99, 'APP', 'GENERAL'),
  (27, 'PF_APPROVAL', 'Pathfinder Approval', 99, 'APP', 'GENERAL'),
  (29, 'POM', 'Prisoner Offender Manager', 99, 'APP', 'GENERAL'),
  (30, 'PF_STD_PRISON_RO', 'Pathfinder Prison Read Only', 99, 'APP', 'GENERAL'),
  (31, 'USE_OF_FORCE_COORDINATOR', 'Use of force coordinator', 99, 'APP', 'ADMIN'),
  (32, 'LICENCE_READONLY', 'Licence read only', 100, 'APP', 'GENERAL'),
  (33, 'SOC_CUSTODY', 'SOCU Prison Role', 99, 'APP', 'GENERAL'),
  (34, 'PPM_ANALYST', 'PPM Analyst', 99, 'APP', 'GENERAL'),
  (35, 'ARTEMIS_USER', 'Artemis user', 99, 'APP', 'GENERAL'),
  (36, 'PF_LOCAL_READER', 'Pathfinder Local Reader', 99, 'APP', 'GENERAL'),
  (37, 'PF_NATIONAL_READER', 'Pathfinder National Reader', 99, 'APP', 'GENERAL'),
  (38, 'PF_HQ', 'Pathfinder HQ', 99, 'APP', 'GENERAL');

INSERT INTO USER_CASELOAD_ROLES (ROLE_ID, CASELOAD_ID, USERNAME)
VALUES (-1, 'MDI', 'ITAG_USER'),
  (-1, 'MDI', 'OLD_NOMIS_USER'),
  (1, 'NWEB', 'ITAG_USER'),
  (4, 'NWEB', 'ITAG_USER'),
  (4, 'NWEB', 'ITAG_USER_ADM'),
  (5, 'NWEB', 'NOMIS_BATCHLOAD'),
  (6, 'NWEB', 'ITAG_USER_ADM'),
  (7, 'NWEB', 'ITAG_USER'),
  (8, 'NWEB', 'ITAG_USER'),
  (17, 'NWEB', 'ITAG_USER'),
  (18, 'NWEB', 'ITAG_USER'),
  (20, 'NWEB', 'ITAG_USER'),
  (3, 'CADM', 'ITAG_USER_ADM'),
  (11, 'NWEB', 'CA_USER'),
  (12, 'NWEB', 'RO_USER'),
  (8, 'NWEB', 'RO_USER'),
  (16, 'NWEB', 'RO_USER'),
  (13, 'NWEB', 'DM_USER'),
  (14, 'NWEB', 'ITAG_USER_ADM'),
  (11, 'NWEB', 'CA_USER_TEST'),
  (11, 'NWEB', 'RESET_TEST_USER'),
  (11, 'NWEB', 'CA_USER_MULTI'),
  (12, 'NWEB', 'RO_DEMO'),
  (12, 'NWEB', 'RO_USER_TEST'),
  (12, 'NWEB', 'RO_USER_MULTI'),
  (13, 'NWEB', 'DM_USER_TEST'),
  (13, 'NWEB', 'DM_USER_MULTI'),
  (15, 'NWEB', 'OMIC_USER'),
  (6, 'NWEB', 'LAA_USER'),
  (19, 'NWEB', 'ITAG_USER_ADM'),
  (21, 'NWEB', 'IEP_USER'),
  (22, 'NWEB', 'SECURE_CASENOTE_USER'),
  (23, 'NWEB', 'SECURE_CASENOTE_USER'),
  (8, 'NWEB', 'PPL_USER'),
  (24, 'NWEB', 'PPL_USER'),
  (26, 'NWEB', 'UOF_REVIEWER_USER'),
  (8, 'NWEB', 'RCTL_USER'),
  (27, 'NWEB', 'RCTL_USER'),
  (29, 'NWEB', 'POM_USER'),
  (30, 'NWEB', 'PF_RO_USER'),
  (31, 'NWEB', 'UOF_COORDINATOR_USER'),
  (32, 'NWEB', 'LICENCE_READONLY_TEST'),
  (8, 'NWEB', 'SOC_PRISON_LOCAL'),
  (33, 'NWEB', 'SOC_PRISON_LOCAL'),
  (34, 'NWEB', 'PPM_ANALYST_USER'),
  (35, 'NWEB', 'PRISON_COLLATOR_LOCAL'),
  (35, 'NWEB', 'PRISON_ANALYST_LOCAL'),
  (36, 'NWEB', 'PF_LOCAL_READER_USER'),
  (37, 'NWEB', 'PF_NATIONAL_READER_USER'),
  (38, 'NWEB', 'PF_HQ_USER');

Insert into internet_addresses (INTERNET_ADDRESS_ID, OWNER_CLASS, OWNER_ID, OWNER_SEQ, OWNER_CODE,
                                INTERNET_ADDRESS_CLASS, INTERNET_ADDRESS, CREATE_DATETIME, CREATE_USER_ID,
                                MODIFY_DATETIME, MODIFY_USER_ID, AUDIT_TIMESTAMP, AUDIT_USER_ID, AUDIT_MODULE_NAME,
                                AUDIT_CLIENT_USER_ID, AUDIT_CLIENT_IP_ADDRESS, AUDIT_CLIENT_WORKSTATION_NAME,
                                AUDIT_ADDITIONAL_INFO)
VALUES (138250, 'STF', 4, null, null, 'EMAIL', 'phillips@bobjustice.gov.uk',
    to_timestamp('06-DEC-18 16.08.27.910960000', 'DD-MON-RR HH24.MI.SSXFF'), 'PPHILLIPS_ADM', null, null,
    to_timestamp('06-DEC-18 16.08.27.911332000', 'DD-MON-RR HH24.MI.SSXFF'), 'PPHILLIPS_ADM', 'OUMPERSO',
    'pphillips', '10.102.2.4', 'MGMRW0100', null),
  (138251, 'STF', 4, null, null, 'EMAIL', 'phillips@fredjustice.gov.uk',
    to_timestamp('06-DEC-18 16.08.43.742506000', 'DD-MON-RR HH24.MI.SSXFF'), 'PPHILLIPS_ADM', null, null,
    to_timestamp('06-DEC-18 16.08.43.742717000', 'DD-MON-RR HH24.MI.SSXFF'), 'PPHILLIPS_ADM', 'OUMPERSO',
    'pphillips', '10.102.2.4', 'MGMRW0100', null),
  (138252, 'STF', 10, null, null, 'EMAIL', 'ro_user@some.justice.gov.uk',
    to_timestamp('06-DEC-18 16.08.43.742506000', 'DD-MON-RR HH24.MI.SSXFF'), 'PPHILLIPS_ADM', null, null,
    to_timestamp('06-DEC-18 16.08.43.742717000', 'DD-MON-RR HH24.MI.SSXFF'), 'PPHILLIPS_ADM', 'OUMPERSO',
    'pphillips', '10.102.2.4', 'MGMRW0100', null),
    (138253, 'STF', 32, null, null, 'EMAIL', 'bob.smith.no@justice.gov.uk',
    to_timestamp('06-DEC-18 16.08.43.742506000', 'DD-MON-RR HH24.MI.SSXFF'), 'NOMIS_NEVER_LOGGED_IN', null, null,
    to_timestamp('06-DEC-18 16.08.43.742717000', 'DD-MON-RR HH24.MI.SSXFF'), 'NOMIS_NEVER_LOGGED_IN', 'OUMPERSO',
    'pphillips', '10.102.2.4', 'MGMRW0100', null),
    (138254, 'STF', 32, null, null, 'EMAIL', 'bob@hmps.gsi.gov.uk',
    to_timestamp('06-DEC-18 16.08.43.742506000', 'DD-MON-RR HH24.MI.SSXFF'), 'NOMIS_NEVER_LOGGED_IN', null, null,
    to_timestamp('06-DEC-18 16.08.43.742717000', 'DD-MON-RR HH24.MI.SSXFF'), 'NOMIS_NEVER_LOGGED_IN', 'OUMPERSO',
    'pphillips', '10.102.2.4', 'MGMRW0100', null),
    (138255, 'STF', 40, null, null, 'EMAIL', 'fred@some.justice.gov.uk',
    to_timestamp('06-DEC-18 16.08.43.742506000', 'DD-MON-RR HH24.MI.SSXFF'), 'PPHILLIPS_ADM', null, null,
    to_timestamp('06-DEC-18 16.08.43.742717000', 'DD-MON-RR HH24.MI.SSXFF'), 'PPHILLIPS_ADM', 'OUMPERSO',
    'pphillips', '10.102.2.4', 'MGMRW0100', null),
    (138256, 'STF', 40, null, null, 'EMAIL', 'multiple.user.test@digital.justice.gov.uk',
    to_timestamp('06-DEC-18 16.08.43.742506000', 'DD-MON-RR HH24.MI.SSXFF'), 'PPHILLIPS_ADM', null, null,
    to_timestamp('06-DEC-18 16.08.43.742717000', 'DD-MON-RR HH24.MI.SSXFF'), 'PPHILLIPS_ADM', 'OUMPERSO',
    'pphillips', '10.102.2.4', 'MGMRW0100', null);






