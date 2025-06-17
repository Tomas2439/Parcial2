CREATE TABLE Personas (
    id INT PRIMARY KEY,
    nombre VARCHAR(100)NOT NULL,
    fnacim DATE,
    DNI VARCHAR(50) UNIQUE
);

CREATE TABLE usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(150) NOT NULL
); -- Tabla usuarios


CREATE VIEW vewPersonasUsuarios 
AS
SELECT p.id, p.nombre, u.username, p.dni, u.email
FROM usuarios u LEFT JOIN personas p on u.id = p.id;
