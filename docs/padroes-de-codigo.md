# Padrões de Código — Biciclo API

Este documento define as convenções que **todos os contribuidores** devem seguir.
Os domínios `auth` e `pontointeresse` são as referências vivas destes padrões.

## 1. Organização por domínio

O código é organizado por **domínio de negócio** (não por camada técnica),
mantendo a divisão em camadas *dentro* de cada domínio:

```
com.biciclo.domain.<dominio>/
├── <Entidade>.java            # Entity JPA
├── <Entidade>Repository.java  # Spring Data JPA
├── <Entidade>Service.java     # Regras de negócio + transações
├── <Entidade>Controller.java  # Endpoints REST
└── dto/
    ├── <X>Request.java        # Entrada validada
    └── <X>Response.java       # Saída (nunca uma entity)
```

Regras:

- **Entities nunca vazam para fora do domínio.** A camada web só recebe/retorna DTOs.
- **Controllers não contêm lógica de negócio.** Apenas delegam ao Service.
- **Repositories** contêm apenas acesso a dados (JPQL/SQL), nunca regras.

## 2. Nomenclatura

- Entidades/classes de domínio: **Português** (`Usuario`, `PontoInteresse`, `Trajeto`).
- Camadas técnicas: **Inglês** (`UsuarioController`, `UsuarioService`, `UsuarioRepository`).
- Colunas de tabela: **snake_case** (mapeadas via `@Column(name = "...")`).
- Constantes: `UPPER_SNAKE_CASE`. Métodos/variáveis: `camelCase`.

## 3. DTOs

- DTOs de **entrada** são classes mutáveis com `@Getter`/`@Setter` + Bean Validation
  (`@NotBlank`, `@Email`, `@Size`, `@NotNull`...).
- DTOs de **saída** são `record` imutáveis com um factory `static <Nome> from(Entity e)`.
- Não usar `@Builder` indiscriminadamente; preferir construtores explícitos.

## 4. Tratamento de erros

- Nunca capturar exceções dentro de Controller para montar resposta manualmente.
- Lance uma das exceções de domínio (tratadas pelo `GlobalExceptionHandler`):

| Situação | Exceção | HTTP |
|---|---|---|
| Regra de negócio violada | `BusinessException(status, msg)` | configurável |
| Recurso não encontrado | `ResourceNotFoundException(msg)` | 404 |
| Erro de validação de payload | (automático via Bean Validation) | 400 |

- A resposta de erro é sempre o `ApiError` padronizado
  (`timestamp`, `status`, `error`, `message`).

## 5. Transações e concorrência

- Operações que alteram mais de uma entidade **devem** usar `@Transactional`.
- Débito de pontos, reserva de estoque e baixa de cupom devem ser **atómicas**
  (uma única transação) — ver RNF01.
- Para atualizações condicionais use **lock pessimista** (`@Lock(PESSIMISTIC_WRITE)`)
  ou SQL otimista (`UPDATE ... WHERE estoque > 0`), nunca "ler e depois gravar".

## 6. Geoespacial (PostGIS)

- Tipos geométricos mapeados com `@JdbcTypeCode(SqlTypes.GEOMETRY)` +
  `@Column(columnDefinition = "geometry(<Tipo>, 4326)")`.
- Coordenadas sempre em **SRID 4326** (WGS84).
- Distâncias em **metros** via cast para `::geography` (`ST_DWithin`, `ST_Distance`).
- Todo campo geométrico usado em busca deve ter **índice GIST** (ver `V1__init.sql`).
- Ordem dos parâmetros em `ST_MakePoint`: `(longitude, latitude)`.

## 7. Segurança (RBAC)

- Endpoints protegidos por padrão (`anyRequest().authenticated()`).
- Restrições por perfil via `@PreAuthorize("hasRole('PARTNER')")` no controller/service.
- Perfis disponíveis: `CYCLIST`, `PARTNER`, `ADMIN` (autoridade = `ROLE_<PERFIL>`).
- O principal autenticado é `UserPrincipal` (id, email, role) vindo dos claims do JWT.

## 8. Migrações (Flyway)

- O schema pertence ao Flyway (`ddl-auto: none`). **Não** gerar schema via Hibernate.
- Nomeação: `V<n>__descricao.sql`. Nunca editar migração já aplicada.
- Toda nova tabela/coluna vai numa nova migração `V2__`, `V3__`, etc.

## 9. Testes

- Testes de unidade para Services (com Mockito) e para componentes puros (ex.: `JwtService`).
- Nome de teste em português com sufixo `Test`.
- Ver exemplo em `src/test/java/com/biciclo/security/JwtServiceTest.java`.
