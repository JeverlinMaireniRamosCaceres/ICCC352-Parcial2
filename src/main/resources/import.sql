INSERT INTO Usuario (nombre, email, contrasena, rol, bloqueado) VALUES ('Administrador', 'admin@eventos.com', 'admin', 'ADMIN', false);

-- Articulos dummy
INSERT INTO evento (titulo, descripcion, fecha, hora, lugar, cupoMaximo, estado) VALUES ('Charla de Inteligencia Artificial', 'Introducción a la inteligencia artificial y sus aplicaciones.', '2026-03-10', '18:00:00', 'Auditorio A', 150, 'PUBLICADO');
INSERT INTO evento (titulo, descripcion, fecha, hora, lugar, cupoMaximo, estado) VALUES ('Taller de Programación Web', 'Aprende a desarrollar aplicaciones web modernas.', '2026-03-12', '16:00:00', 'Laboratorio 3', 80, 'PUBLICADO');
INSERT INTO evento (titulo, descripcion, fecha, hora, lugar, cupoMaximo, estado) VALUES ('Seminario de Ciberseguridad', 'Conceptos fundamentales de seguridad informática.', '2026-03-15', '17:30:00', 'Auditorio B', 120, 'CANCELADO');
INSERT INTO evento (titulo, descripcion, fecha, hora, lugar, cupoMaximo, estado) VALUES ('Conferencia de Innovación Tecnológica', 'Tendencias actuales en innovación y tecnología.', '2026-03-18', '19:00:00', 'Salón de Conferencias', 200, 'PUBLICADO');
INSERT INTO evento (titulo, descripcion, fecha, hora, lugar, cupoMaximo, estado) VALUES ('Workshop de Desarrollo Backend', 'Construcción de APIs modernas.', '2026-03-20', '15:00:00', 'Laboratorio 1', 60, 'PUBLICADO');
INSERT INTO evento (titulo, descripcion, fecha, hora, lugar, cupoMaximo, estado) VALUES ('Panel de Startups Tecnológicas', 'Experiencias de emprendedores tecnológicos.', '2026-03-25', '18:30:00', 'Auditorio Principal', 140, 'CANCELADO');
INSERT INTO evento (titulo, descripcion, fecha, hora, lugar, cupoMaximo, estado) VALUES ('Bootcamp de Python', 'Aprende Python desde cero.', '2026-03-28', '14:00:00', 'Laboratorio 2', 90, 'PUBLICADO');
INSERT INTO evento (titulo, descripcion, fecha, hora, lugar, cupoMaximo, estado) VALUES ('Introducción a Machine Learning', 'Conceptos básicos de aprendizaje automático.', '2026-04-02', '17:00:00', 'Auditorio A', 130, 'PUBLICADO');
INSERT INTO evento (titulo, descripcion, fecha, hora, lugar, cupoMaximo, estado) VALUES ('Charla de Desarrollo Mobile', 'Desarrollo de aplicaciones móviles modernas.', '2026-04-05', '16:30:00', 'Laboratorio 4', 100, 'PUBLICADO');
INSERT INTO evento (titulo, descripcion, fecha, hora, lugar, cupoMaximo, estado) VALUES ('Taller de Git y Control de Versiones', 'Buenas prácticas usando Git.', '2026-04-08', '15:30:00', 'Laboratorio 5', 70, 'CANCELADO');
INSERT INTO evento (titulo, descripcion, fecha, hora, lugar, cupoMaximo, estado) VALUES ('Charla de UX/UI', 'Principios de diseño de interfaces.', '2026-04-12', '18:00:00', 'Auditorio C', 110, 'PUBLICADO');
INSERT INTO evento (titulo, descripcion, fecha, hora, lugar, cupoMaximo, estado) VALUES ('Seminario de Cloud Computing', 'Introducción a servicios en la nube.', '2026-04-15', '17:00:00', 'Salón 204', 120, 'PUBLICADO');

