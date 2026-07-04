-- Script de criação do banco de dados MySQL para DOOH-Signage

-- Tabela Empresa (Cliente/Estabelecimento)
CREATE TABLE IF NOT EXISTS empresas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    razao_social VARCHAR(255) NOT NULL,
    nome_fantasia VARCHAR(255) NOT NULL,
    responsavel VARCHAR(100),
    telefone VARCHAR(20),
    whatsapp VARCHAR(20),
    email VARCHAR(100) NOT NULL UNIQUE,
    endereco VARCHAR(255),
    cidade VARCHAR(100),
    estado VARCHAR(2) NOT NULL,
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    url_logo VARCHAR(500),
    status VARCHAR(20) DEFAULT 'ATIVO',
    observacoes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabela Tela
CREATE TABLE IF NOT EXISTS telas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    codigo_unico VARCHAR(36) NOT NULL UNIQUE,
    local VARCHAR(150),
    empresa_id BIGINT,
    cidade VARCHAR(100),
    endereco VARCHAR(255),
    orientacao VARCHAR(20) NOT NULL,
    polegadas VARCHAR(20) DEFAULT 'LIVRE',
    resolucao VARCHAR(50) DEFAULT '1920x1080',
    status VARCHAR(20) DEFAULT 'OFFLINE',
    ultima_conexao TIMESTAMP NULL DEFAULT NULL,
    ip VARCHAR(45),
    observacoes TEXT,
    -- Telemetria (Espaço livre, Memória livre/total, CPU em %)
    telemetria_espaco_livre_mb BIGINT,
    telemetria_memoria_uso_mb BIGINT,
    telemetria_cpu_uso_porcento DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (empresa_id) REFERENCES empresas(id) ON DELETE CASCADE
);

-- Tabela Campanha
CREATE TABLE IF NOT EXISTS campanhas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    empresa_id BIGINT,
    url_midia VARCHAR(500) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    dias_semana VARCHAR(50) NOT NULL, -- Exemplo: "1,2,3,4,5" (dias da semana ativos)
    hora_inicio TIME DEFAULT '00:00:00',
    hora_fim TIME DEFAULT '23:59:59',
    tempo_exibicao INTEGER NOT NULL DEFAULT 15, -- Segundos por exibição
    prioridade INTEGER NOT NULL DEFAULT 1, -- Ordem de relevância ou peso
    status VARCHAR(20) DEFAULT 'ATIVA',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (empresa_id) REFERENCES empresas(id) ON DELETE CASCADE
);

-- Tabela de Playlist (Intermediária para associar telas e campanhas de forma ordenada)
CREATE TABLE IF NOT EXISTS playlists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tela_id BIGINT NOT NULL,
    nome VARCHAR(100) DEFAULT 'Playlist Principal',
    ativa BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tela_id) REFERENCES telas(id) ON DELETE CASCADE
);

-- Tabela de Itens de Playlist (Relacionamento N:M ordenado entre Playlist e Campanha)
CREATE TABLE IF NOT EXISTS playlist_itens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    playlist_id BIGINT NOT NULL,
    campanha_id BIGINT NOT NULL,
    ordem INTEGER NOT NULL,
    CONSTRAINT unique_playlist_ordem UNIQUE (playlist_id, ordem),
    FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE,
    FOREIGN KEY (campanha_id) REFERENCES campanhas(id) ON DELETE CASCADE
);

-- Índices de otimização para buscas do Player Web e Telemetria
-- Nota: Índices normais no MySQL não suportam IF NOT EXISTS em versões antigas.
-- Recomenda-se criar os índices diretamente. Eles serão criados se a tabela for recém criada.
CREATE INDEX idx_telas_codigo_unico ON telas(codigo_unico);
CREATE INDEX idx_playlist_itens_playlist ON playlist_itens(playlist_id);
