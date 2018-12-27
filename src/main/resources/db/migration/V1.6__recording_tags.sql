CREATE SEQUENCE RECORDING_TAGS_SEQ;
CREATE TABLE RECORDING_TAGS
(
  ID           BIGINT NOT NULL PRIMARY KEY,
  RECORDING_ID BIGINT NOT NULL REFERENCES RECORDINGS (ID),
  TAG_ID       BIGINT NOT NULL REFERENCES TAGS (ID)
);