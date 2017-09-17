CREATE TABLE IF NOT EXISTS `servers` (
  server_id   INT                  AUTO_INCREMENT,
  server_name VARCHAR(30) NOT NULL,
  server_type VARCHAR(15) NOT NULL,
  server_ip   VARCHAR(16) NOT NULL,
  server_port INT(16)     NOT NULL DEFAULT 25565,
  PRIMARY KEY (server_id)
);