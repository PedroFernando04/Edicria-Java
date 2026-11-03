# 📚 Edicria

Aplicação web para organizar leituras: catálogo de livros, autores e editoras, e uma **estante pessoal** para cada usuário acompanhar o que está lendo.

> Projeto de estudo com duas abordagens de propósito: o **backend foi escrito 100% à mão, sem IA**, para exercitar o fundamento. O **frontend foi construído com apoio de IA**, para praticar o fluxo de trabalho moderno.

## ✨ Funcionalidades

- Cadastro e login de usuários (sessão com Spring Security)
- Catálogo de livros com página de detalhes
- Cadastro de autores e editoras
- Estante pessoal: cada usuário acompanha suas leituras
- Perfil do usuário
- Documentação da API com Swagger (springdoc)

## 🧰 Tecnologias

| Camada | Stack |
|---|---|
| Backend | Java 21, Spring Boot, Spring Security, Spring Data JPA, Bean Validation, Lombok |
| Banco | PostgreSQL |
| Frontend | Angular 21 |
| Infra | Docker, Docker Compose, nginx, Render |

## 🗂️ Estrutura

```
edicria/
├── backend/             API REST (Spring Boot)
├── frontend/            Interface (Angular)
├── docker/              nginx.conf.template e entrypoint.sh
├── Dockerfile           imagem única (nginx + Spring Boot)
├── docker-compose.yml   app + PostgreSQL para uso local
├── render.yaml          Blueprint de deploy no Render
└── README-DEPLOY.md     guia detalhado de execução e deploy
```

O backend segue a organização em camadas: `controllers` → `services` → `repositories`, com `entities`, `dtos` (request/response), `mappers` e `exceptions` próprias, além de testes unitários dos serviços.

## 🚀 Como rodar

### Com Docker (recomendado)

Requisito: Docker instalado.

```bash
git clone -b projeto-completo https://github.com/PedroFernando04/Edicria-Java.git
cd Edicria-Java
docker compose up --build
```

Abra **http://localhost:8081**, crie uma conta em "Criar conta" e entre.

Verificações rápidas:

```bash
curl http://localhost:8081/healthz        # -> ok
curl -i http://localhost:8081/api/books   # -> 401 (sem login, é o esperado)
```

Para parar: `docker compose down` (use `-v` para apagar também o banco).

### Sem Docker (desenvolvimento)

Requisitos: Java 21, Node 22+ e PostgreSQL rodando em `localhost:5432` com um banco chamado `edicria`.

```bash
# Terminal 1 - backend (http://localhost:8080)
cd backend
./mvnw spring-boot:run

# Terminal 2 - frontend (http://localhost:4200, com proxy de /api para o backend)
cd frontend
npm ci
npm start
```

As credenciais do banco ficam em `backend/src/main/resources/application.properties`.

## ☁️ Deploy no Render

1. Suba o repositório (esta branch) para o GitHub.
2. No Render: **New → Blueprint** e selecione o repositório. O `render.yaml` cria o banco e o web service.
3. Aguarde o build e acesse a URL gerada.

Detalhes, variáveis de ambiente e solução de problemas em [README-DEPLOY.md](README-DEPLOY.md).

## 🔌 API

Base: `/api`

| Recurso | Rota |
|---|---|
| Usuários (login/cadastro) | `/api/users` |
| Livros | `/api/books` |
| Autores | `/api/authors` |
| Editoras | `/api/publishers` |
| Estante pessoal | `/api/book-user` |

Apenas `/api/users/**` é público; as demais rotas exigem login.

## ⚠️ Observações

- O `application.properties` usa `ddl-auto=create-drop` (recria o banco a cada execução). No Docker/Render isso é sobrescrito para `update`.
- No plano gratuito do Render o serviço "dorme" após um período sem uso, e o banco gratuito é temporário.

## 🧪 Testes

```bash
cd backend
./mvnw test
```

## 👤 Autor

**Pedro Fernando** — [GitHub](https://github.com/PedroFernando04)
