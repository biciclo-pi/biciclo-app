-- =====================================================================
-- Biciclo - Migração inicial do schema (PostgreSQL + PostGIS)
-- O Flyway executa este arquivo em ordem numérica. Nunca edite migrações
-- já aplicadas; crie uma nova (V2__, V3__, ...).
-- =====================================================================

CREATE EXTENSION IF NOT EXISTS postgis;

-- ---------------------------------------------------------------------
-- Usuários (base comum: ciclista, comerciante e administrador)
-- ---------------------------------------------------------------------
CREATE TABLE usuarios (
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(150) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    senha_hash    VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL CHECK (role IN ('CYCLIST', 'PARTNER', 'ADMIN')),
    saldo_pontos  INTEGER      NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_usuarios_email UNIQUE (email)
);

-- ---------------------------------------------------------------------
-- Parceiros (comerciantes). Endereço em lat/long separadas por decisão
-- de domínio (diferente de pontos_interesse, que usa geometry).
-- ---------------------------------------------------------------------
CREATE TABLE parceiros (
    id            BIGSERIAL PRIMARY KEY,
    usuario_id    BIGINT       NOT NULL,
    nome_fantasia VARCHAR(200) NOT NULL,
    cnpj          VARCHAR(18)  NOT NULL,
    latitude      DOUBLE PRECISION,
    longitude     DOUBLE PRECISION,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_parceiros_usuario UNIQUE (usuario_id),
    CONSTRAINT uq_parceiros_cnpj    UNIQUE (cnpj),
    CONSTRAINT fk_parceiros_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

-- ---------------------------------------------------------------------
-- Pontos de interesse geoespaciais
-- ---------------------------------------------------------------------
CREATE TABLE pontos_interesse (
    id          BIGSERIAL PRIMARY KEY,
    tipo        VARCHAR(30) NOT NULL CHECK (tipo IN ('BICICLETARIO', 'OFICINA', 'PONTO_APOIO', 'TERMINAL')),
    descricao   VARCHAR(255),
    geom        geometry(Point, 4326) NOT NULL,
    capacidade  INTEGER,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE
);

-- Índice GIST para consultas espaciais (ST_DWithin / ST_Distance).
-- O índice em geografia permite buscas por raio em METROS.
CREATE INDEX idx_pois_geom         ON pontos_interesse USING GIST (geom);
CREATE INDEX idx_pois_geom_geog    ON pontos_interesse USING GIST ((geom::geography));

-- ---------------------------------------------------------------------
-- Trajetos (série temporal de GPS agregada em LineString)
-- ---------------------------------------------------------------------
CREATE TABLE trajetos (
    id                 BIGSERIAL PRIMARY KEY,
    ciclista_id        BIGINT NOT NULL,
    geom               geometry(LineString, 4326) NOT NULL,
    distancia_metros   DOUBLE PRECISION,
    duracao_segundos   INTEGER,
    velocidade_media   DOUBLE PRECISION,
    pontos_ganhos      INTEGER,
    status_analise     VARCHAR(20) NOT NULL DEFAULT 'PENDENTE'
                         CHECK (status_analise IN ('PENDENTE', 'APROVADO', 'REJEITADO')),
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_trajetos_ciclista FOREIGN KEY (ciclista_id) REFERENCES usuarios (id)
);

CREATE INDEX idx_trajetos_geom      ON trajetos USING GIST (geom);
CREATE INDEX idx_trajetos_ciclista  ON trajetos (ciclista_id);

-- ---------------------------------------------------------------------
-- Recompensas (catálogo dos parceiros)
-- ---------------------------------------------------------------------
CREATE TABLE recompensas (
    id                  BIGSERIAL PRIMARY KEY,
    parceiro_id         BIGINT NOT NULL,
    titulo              VARCHAR(150) NOT NULL,
    descricao           TEXT,
    custo_pontos        INTEGER NOT NULL,
    quantidade_estoque  INTEGER NOT NULL DEFAULT 0,
    data_expiracao      DATE,
    ativa               BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_recompensas_parceiro FOREIGN KEY (parceiro_id) REFERENCES parceiros (id)
);

CREATE INDEX idx_recompensas_parceiro ON recompensas (parceiro_id);
CREATE INDEX idx_recompensas_ativa    ON recompensas (ativa);

-- ---------------------------------------------------------------------
-- Cupons (vouchers resgatados pelo ciclista)
-- ---------------------------------------------------------------------
CREATE TABLE cupons (
    id                BIGSERIAL PRIMARY KEY,
    codigo_validacao  VARCHAR(64) NOT NULL,
    recompensa_id     BIGINT NOT NULL,
    ciclista_id       BIGINT NOT NULL,
    status            VARCHAR(20) NOT NULL DEFAULT 'PENDENTE'
                        CHECK (status IN ('PENDENTE', 'UTILIZADO', 'EXPIRADO')),
    data_resgate      TIMESTAMPTZ NOT NULL DEFAULT now(),
    data_utilizacao   TIMESTAMPTZ,
    CONSTRAINT uq_cupons_codigo UNIQUE (codigo_validacao),
    CONSTRAINT fk_cupons_recompensa FOREIGN KEY (recompensa_id) REFERENCES recompensas (id),
    CONSTRAINT fk_cupons_ciclista   FOREIGN KEY (ciclista_id)   REFERENCES usuarios (id)
);

CREATE INDEX idx_cupons_ciclista   ON cupons (ciclista_id);
CREATE INDEX idx_cupons_recompensa ON cupons (recompensa_id);

-- ---------------------------------------------------------------------
-- Extrato de pontos (registro imutável / auditável)
-- ---------------------------------------------------------------------
CREATE TABLE extrato_pontos (
    id                BIGSERIAL PRIMARY KEY,
    ciclista_id       BIGINT NOT NULL,
    tipo_movimento    VARCHAR(10) NOT NULL CHECK (tipo_movimento IN ('ENTRADA', 'SAIDA')),
    quantidade        INTEGER NOT NULL,
    referencia_tipo   VARCHAR(20) NOT NULL CHECK (referencia_tipo IN ('TRAJETO', 'CUPOM')),
    referencia_id     BIGINT NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_extrato_ciclista FOREIGN KEY (ciclista_id) REFERENCES usuarios (id)
);

CREATE INDEX idx_extrato_ciclista ON extrato_pontos (ciclista_id);
