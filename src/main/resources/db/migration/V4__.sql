ALTER TABLE devotionals
    ADD ethical_alignment VARCHAR(500);

ALTER TABLE devotionals
    ADD practical_application VARCHAR(1000);

ALTER TABLE devotionals
    ADD prayer VARCHAR(500);

ALTER TABLE devotionals
    ADD reflection VARCHAR(3000);

ALTER TABLE devotionals
    ADD validation_sources VARCHAR(1000);

ALTER TABLE devotionals
    DROP COLUMN content;