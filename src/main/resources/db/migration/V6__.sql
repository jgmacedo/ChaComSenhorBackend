ALTER TABLE "user"
    DROP COLUMN token;

ALTER TABLE "user"
    DROP COLUMN role;

ALTER TABLE "user"
    ADD role SMALLINT;