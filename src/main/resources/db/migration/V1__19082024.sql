CREATE TABLE user
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(2048) NOT NULL,
    status VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE file
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE event
(
    id     INT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    user_id INT,
    file_id INT,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (file_id) REFERENCES file(id)
);