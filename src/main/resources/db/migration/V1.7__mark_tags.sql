CREATE SEQUENCE MARK_TAGS_SEQ;
CREATE TABLE MARK_TAGS
(
  ID      BIGINT NOT NULL PRIMARY KEY,
  MARK_ID BIGINT NOT NULL REFERENCES MARKS (ID),
  TAG_ID  BIGINT NOT NULL REFERENCES TAGS (ID)
);
