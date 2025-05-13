CREATE TABLE IF NOT EXISTS reclamation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    description TEXT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
); 

UPDATE user SET role = 'USER' WHERE role = 'EMPLOYEE'; 