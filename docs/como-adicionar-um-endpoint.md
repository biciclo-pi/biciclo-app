# Como adicionar um endpoint (receita)

Use esta receita para implementar qualquer novo domínio/rota seguindo o padrão.
Antes de começar, leia [`padroes-de-codigo.md`](padroes-de-codigo.md) e estude os
domínios de referência `auth` e `pontointeresse`.

## Passo a passo

### 1. Entidade (Entity)

Crie `domain/<dominio>/<Nome>.java` mapeando a tabela com JPA:

```java
@Entity
@Table(name = "nome_da_tabela")
@Getter @Setter @NoArgsConstructor
public class Nome {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // ...
}
```

- Use `@Enumerated(EnumType.STRING)` para enums (nunca `ORDINAL`).
- Campos geométricos: `@JdbcTypeCode(SqlTypes.GEOMETRY)` +
  `@Column(columnDefinition = "geometry(Point, 4326)")`.

### 2. Migração Flyway

Crie `src/main/resources/db/migration/V<próximo>__cria_tabela.sql` com o DDL
(ou edite via nova migração se a tabela já existe). Adicione índices (inclusive
GIST para colunas geométricas usadas em busca).

### 3. Repositório

```java
public interface NomeRepository extends JpaRepository<Nome, Long> {
    // consultas derivadas ou @Query (nativas para PostGIS)
}
```

### 4. DTOs

- `dto/<X>Request.java` — entrada com Bean Validation.
- `dto/<X>Response.java` — `record` com `static from(Entity)`.

### 5. Service

```java
@Service
public class NomeService {
    // regras de negócio aqui; @Transactional quando altera múltiplas entidades
}
```

### 6. Controller

```java
@RestController
@RequestMapping("/api/v1/<rota>")
public class NomeController {
    // @GetMapping / @PostMapping / ... delegando ao Service
}
```

### 7. Segurança

- Se o endpoint é público, adicione a regra em `SecurityConfig`.
- Se é restrito a um perfil, use `@PreAuthorize("hasRole('PARTNER')")`.

### 8. Teste

Adicione um teste de unidade para o Service.

## Checklist de revisão

- [ ] Entity não é exposta no controller (só DTOs).
- [ ] Entradas validadas com Bean Validation.
- [ ] Erros lançados via `BusinessException` / `ResourceNotFoundException`.
- [ ] Operações multi-entidade marcadas com `@Transactional`.
- [ ] Consultas espaciais usam índice GIST e retornam em < 200 ms.
- [ ] Migração Flyway criada (e não editada uma antiga).
- [ ] Regras de RBAC aplicadas.
- [ ] Contrato documentado em `docs/api-contrato.md`.
