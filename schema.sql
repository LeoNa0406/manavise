DROP DATABASE IF EXISTS manavise_db;

CREATE DATABASE manavise_db;
+++++

USE manavise_db;

DROP TABLE IF EXISTS choices;
DROP TABLE IF EXISTS answer_histories;
DROP TABLE IF EXISTS quiz_histories;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;


CREATE TABLE roles (
  role_id   INT PRIMARY KEY AUTO_INCREMENT,
  role_name VARCHAR(50) NOT NULL
);

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(100) NOT NULL,
    login_id VARCHAR(100) NOT NULL UNIQUE,
    login_password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE categories (
  category_id   INT PRIMARY KEY AUTO_INCREMENT,
  category_name VARCHAR(100) NOT NULL
);

CREATE TABLE questions (
  question_id      INT PRIMARY KEY AUTO_INCREMENT,
  question_number  INT NOT NULL,
  question_content TEXT NOT NULL,
  answer           VARCHAR(255) NOT NULL,
  category_id      INT NOT NULL,
  question_type TINYINT NOT NULL DEFAULT 1,
  deleted          BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

CREATE TABLE choices (
  choice_id    INT PRIMARY KEY AUTO_INCREMENT,
  question_id  INT          NOT NULL,
  choice_text  VARCHAR(255) NOT NULL,
  is_correct   BOOLEAN      NOT NULL DEFAULT FALSE,
  choice_order TINYINT      NOT NULL DEFAULT 1,  -- 表示順 (1〜4)
  FOREIGN KEY (question_id) REFERENCES questions(question_id)
);

CREATE TABLE quiz_histories (
  execute_id   INT PRIMARY KEY AUTO_INCREMENT,
  execute_user INT NOT NULL,
  executed_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (execute_user) REFERENCES users(user_id)
);

CREATE TABLE answer_histories (
  answer_id   INT PRIMARY KEY AUTO_INCREMENT,
  execute_id  INT NOT NULL,
  question_id INT NOT NULL,
  user_answer VARCHAR(255) NOT NULL,
  correct     BOOLEAN NOT NULL,
  FOREIGN KEY (execute_id)  REFERENCES quiz_histories(execute_id),
  FOREIGN KEY (question_id) REFERENCES questions(question_id)
);



-- 初期データ
INSERT INTO roles (role_name) VALUES ('管理者'), ('一般ユーザー');

INSERT INTO users (user_name, login_id, login_password, role_id) VALUES 
('管理者', 'admin01', 'password', 1),
('佐藤 健太郎', 'user01', 'password', 2),
('鈴木 結衣', 'user02', 'password', 2),
('高橋 浩二', 'user03', 'password', 2),
('田中 美咲', 'user04', 'password', 2);

INSERT INTO categories (category_name) VALUES 
('英語'), ('数学'), ('国語'),('理科'), ('社会');

INSERT INTO questions (question_number, question_content, answer, category_id, question_type) VALUES 
(1, '「りんご」を英語で言うと？', 'apple', 1, 2),
(2, '15 + 27 は？', '42', 2, 1),
(3, '「吾輩は猫である」の著者は？', '夏目漱石', 3, 2),
(4, '光合成に必要な気体は？', '二酸化炭素', 4, 1),
(5, '日本の初代内閣総理大臣は？', '伊藤博文', 5, 1),
(6, '「走れメロス」の著者は？', '太宰治', 3, 1),
(7, '三角形の内角の和は何度？', '180', 2, 1);

INSERT INTO choices (question_id, choice_text, is_correct, choice_order) VALUES 
(1, 'apple', TRUE, 1), (1, 'orange', FALSE, 2), (1, 'grape', FALSE, 3), (1, 'banana', FALSE, 4),
(3, '夏目漱石', TRUE, 1), (3, '芥川龍之介', FALSE, 2), (3, '太宰治', FALSE, 3), (3, '森鴎外', FALSE, 4);

INSERT INTO quiz_histories (execute_user) VALUES 
(2), (2), (2), (3), (3), (4), (4), (4), (4);

INSERT INTO answer_histories (execute_id, question_id, user_answer, correct) VALUES 
(1, 1, 'apple', TRUE),
(2, 2, '42', TRUE),
(3, 3, '芥川龍之介', FALSE),
(4, 4, '二酸化炭素', TRUE),
(5, 5, '板垣退助', FALSE),
(6, 6, '太宰治', TRUE),
(7, 7, '180', TRUE),
(8, 1, 'apple', TRUE),
(9, 3, '夏目漱石', TRUE);
