CREATE TABLE usuario (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  senha VARCHAR(255) NOT NULL,
  plano_ativo BOOLEAN DEFAULT FALSE
);
CREATE TABLE site (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  profissao VARCHAR(255),
  descricao TEXT,
  instagram VARCHAR(255),
  whatsapp VARCHAR(100),
  template VARCHAR(50),
  slug VARCHAR(255) UNIQUE,
  usuario_id BIGINT,
  CONSTRAINT fk_usuario FOREIGN KEY(usuario_id) REFERENCES usuario(id)
);
