-- V2__create_course_tables.sql

-- Create categories table
CREATE TABLE categories (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255)  NOT NULL UNIQUE,
    slug          VARCHAR(255)  NOT NULL UNIQUE,
    description   TEXT
);

-- Create courses table
CREATE TABLE courses (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(255)  NOT NULL,
    description     TEXT,
    price           NUMERIC(10, 2),
    status          VARCHAR(20)   NOT NULL DEFAULT 'BROUILLON',
    level           VARCHAR(50)   NOT NULL,
    category_id     BIGINT        NOT NULL,
    instructor_id   BIGINT        NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (instructor_id) REFERENCES users(id)
);

-- Create modules table
CREATE TABLE modules (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255)  NOT NULL,
    description TEXT,
    "order"     INTEGER       NOT NULL,
    course_id   BIGINT        NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Create lessons table
CREATE TABLE lessons (
    id                  BIGSERIAL PRIMARY KEY,
    title               VARCHAR(255)  NOT NULL,
    type                VARCHAR(50)   NOT NULL,
    content             TEXT,
    duration_minutes    INTEGER,
    "order"             INTEGER       NOT NULL,
    is_free             BOOLEAN       DEFAULT TRUE,
    module_id           BIGINT        NOT NULL,
    FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE CASCADE
);

-- Create enrollments table
CREATE TABLE enrollments (
    id                  BIGSERIAL PRIMARY KEY,
    status              VARCHAR(20)   NOT NULL DEFAULT 'EN_ATTENTE',
    enrolled_at         TIMESTAMP     NOT NULL DEFAULT NOW(),
    progress_percentage INTEGER       DEFAULT 0,
    student_id          BIGINT        NOT NULL,
    course_id           BIGINT        NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    UNIQUE(student_id, course_id)
);

-- Create quizzes table
CREATE TABLE quizzes (
    id                  BIGSERIAL PRIMARY KEY,
    title               VARCHAR(255)  NOT NULL,
    description         TEXT,
    time_limit_minutes  INTEGER,
    pass_mark           INTEGER       DEFAULT 60,
    max_attempts        INTEGER       DEFAULT 3,
    course_id           BIGINT        NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Create questions table
CREATE TABLE questions (
    id              BIGSERIAL PRIMARY KEY,
    text            TEXT          NOT NULL,
    type            VARCHAR(50)   NOT NULL,
    points          INTEGER       NOT NULL,
    correct_answer  TEXT,
    quiz_id         BIGINT        NOT NULL,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- Create quiz_attempts table
CREATE TABLE quiz_attempts (
    id            BIGSERIAL PRIMARY KEY,
    score         INTEGER,
    status        VARCHAR(20)   NOT NULL DEFAULT 'EN_COURS',
    started_at    TIMESTAMP     NOT NULL DEFAULT NOW(),
    submitted_at  TIMESTAMP,
    passed        BOOLEAN,
    student_id    BIGINT        NOT NULL,
    quiz_id       BIGINT        NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);

-- Create certificates table
CREATE TABLE certificates (
    id              BIGSERIAL PRIMARY KEY,
    unique_code     VARCHAR(255)  NOT NULL UNIQUE,
    issued_at       TIMESTAMP     NOT NULL DEFAULT NOW(),
    certificate_url TEXT,
    student_id      BIGINT        NOT NULL,
    course_id       BIGINT        NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- Create progress table
CREATE TABLE progress (
    id                  BIGSERIAL PRIMARY KEY,
    completed_at        TIMESTAMP,
    time_spent_minutes  INTEGER,
    student_id          BIGINT        NOT NULL,
    lesson_id           BIGINT        NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE,
    UNIQUE(student_id, lesson_id)
);

-- Create payments table
CREATE TABLE payments (
    id              BIGSERIAL PRIMARY KEY,
    amount          NUMERIC(10, 2)  NOT NULL,
    currency        VARCHAR(3)      NOT NULL,
    method          VARCHAR(50)     NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    transaction_id  VARCHAR(255),
    paid_at         TIMESTAMP,
    enrollment_id   BIGINT          NOT NULL,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(id)
);

-- Create indexes
CREATE INDEX idx_courses_status ON courses(status);
CREATE INDEX idx_courses_category_id ON courses(category_id);
CREATE INDEX idx_courses_instructor_id ON courses(instructor_id);
CREATE INDEX idx_modules_course_id ON modules(course_id);
CREATE INDEX idx_lessons_module_id ON lessons(module_id);
CREATE INDEX idx_enrollments_student_id ON enrollments(student_id);
CREATE INDEX idx_enrollments_course_id ON enrollments(course_id);
CREATE INDEX idx_enrollments_status ON enrollments(status);
CREATE INDEX idx_quizzes_course_id ON quizzes(course_id);
CREATE INDEX idx_questions_quiz_id ON questions(quiz_id);
CREATE INDEX idx_quiz_attempts_student_id ON quiz_attempts(student_id);
CREATE INDEX idx_quiz_attempts_quiz_id ON quiz_attempts(quiz_id);
CREATE INDEX idx_certificates_student_id ON certificates(student_id);
CREATE INDEX idx_certificates_course_id ON certificates(course_id);
CREATE INDEX idx_progress_student_id ON progress(student_id);
CREATE INDEX idx_progress_lesson_id ON progress(lesson_id);
CREATE INDEX idx_payments_enrollment_id ON payments(enrollment_id);
CREATE INDEX idx_payments_status ON payments(status);
