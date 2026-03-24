-- V7__add_module_id_to_quizzes.sql
-- Replace course_id with module_id in quizzes table to match the Quiz entity

-- Step 1: Add the new module_id column (nullable first to avoid constraint issues)
ALTER TABLE quizzes ADD COLUMN module_id BIGINT;

-- Step 2: Fill module_id with the first module of each course (best-effort migration)
UPDATE quizzes q
SET module_id = (
    SELECT m.id
    FROM modules m
    WHERE m.course_id = q.course_id
    ORDER BY m.order_index ASC
    LIMIT 1
);

-- Step 3: Add the foreign key constraint
ALTER TABLE quizzes
    ADD CONSTRAINT fk_quizzes_module
    FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE CASCADE;

-- Step 4: Make module_id NOT NULL
ALTER TABLE quizzes ALTER COLUMN module_id SET NOT NULL;

-- Step 5: Drop the old course_id column and its foreign key
ALTER TABLE quizzes DROP COLUMN course_id;

-- Step 6: Add index on module_id
CREATE INDEX idx_quizzes_module_id ON quizzes(module_id);
