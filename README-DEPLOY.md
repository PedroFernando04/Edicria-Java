# Edicria — frontend (Angular) + backend (Spring Boot) em um só projeto

```
edicria/
├── frontend/            Angular 21
├── backend/             Spring Boot 4 / Java 21 / PostgreSQL
├── docker/              nginx.conf.template + entrypoint.sh
├── Dockerfile           imagem única (nginx + Spring Boot)
├── docker-compose.yml   app + Postgres para teste local
└── render.yaml          Blueprint do Render (app + banco)
```

Como funciona: o contêiner roda o **nginx** na porta `$PORT` (serve o Angular e
repassa `/api/*` ao Spring Boot na 8080). Para o navegador é tudo uma origem só,
então o cookie de sessão funciona e não precisa de CORS.

## 1. Testar localmente com Docker

Requisito: Docker Desktop (ou Docker Engine + compose).

```bash
docker compose up --build
```

A primeira vez demora alguns minutos (baixa dependências). Depois abra
**http://localhost:8081**, crie uma conta em "Criar conta" e entre.

Checagens rápidas:
```bash
curl http://localhost:8081/healthz                 # -> ok
curl -i http://localhost:8081/api/books            # -> 401 (sem login: é o esperado)
docker compose logs -f app                         # logs do Spring
```
Parar: `docker compose down` (use `-v` para apagar também o banco).

### Só a imagem, com seu próprio Postgres
```bash
docker build -t edicria .
docker run --rm -p 8081:10000 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/edicria \
  -e SPRING_DATASOURCE_USERNAME=postgres -e SPRING_DATASOURCE_PASSWORD=123456 \
  edicria
```

### Sem Docker (desenvolvimento)
```bash
# terminal 1 — backend (precisa de Postgres local em localhost:5432/edicria)
cd backend && ./mvnw spring-boot:run
# terminal 2 — frontend (proxy /api -> localhost:8080 já configurado)
cd frontend && npm ci && npm start     # http://localhost:4200
```

## 2. Publicar no Render

1. Suba esta pasta para um repositório no GitHub (a raiz do repo = a pasta `edicria/`).
2. No Render: **New → Blueprint** → escolha o repositório. Ele lê o `render.yaml`
   e cria o banco `edicria-db` e o web service `edicria`.
   (Alternativa manual: crie um **PostgreSQL**, depois um **Web Service** com
   *Runtime: Docker* e as variáveis abaixo.)
3. Aguarde o build (5–10 min na primeira vez) e abra a URL `https://edicria.onrender.com`.

Variáveis de ambiente (já definidas no `render.yaml`):

| Variável | Valor |
|---|---|
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` | dados do banco (use o host **Internal**) |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` |
| `JAVA_OPTS` | limita a memória da JVM |

Se criar tudo manualmente, `SPRING_DATASOURCE_URL` também funciona, mas precisa
estar no formato JDBC: `jdbc:postgresql://HOST:5432/BANCO`
(a URL `postgres://...` do Render **não** serve direto).

### Testar no Render
```bash
curl https://SEU-APP.onrender.com/healthz          # ok
curl -i https://SEU-APP.onrender.com/api/books     # 401
```
Depois abra a URL no navegador, crie uma conta e cadastre um livro.

## Pontos de atenção

- **`ddl-auto=create-drop`**: no `application.properties` original isso **apaga o
  banco a cada reinício**. O Docker já sobrescreve para `update`; o arquivo não foi alterado.
- **Plano free do Render**: o serviço "dorme" após ~15 min sem uso (primeiro acesso
  leva ~1 min), tem 512 MB de RAM (Spring + nginx ficam justos; se der
  `Out of memory`, use o plano Starter) e o Postgres free expira em ~30 dias.
- Senha do banco no compose (`123456`) é só para uso local.
- Os testes do backend foram pulados no build Docker (`-DskipTests`), pois o
  `EdicriaApplicationTests` precisa de banco.
