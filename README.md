# 📚 EDICRIA — Registrador de Leituras

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge\&logo=openjdk\&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?style=for-the-badge\&logo=springboot\&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge\&logo=springsecurity\&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge\&logo=postgresql\&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge\&logo=apachemaven\&logoColor=white)
![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow?style=for-the-badge)

---

## 📖 Sobre o Projeto

O **Edicria** é uma API REST desenvolvida em **Java com Spring Boot**, criada como um projeto pessoal para funcionar como um **registrador de leituras**, inspirado em plataformas como o [Letterboxd](https://letterboxd.com/), mas voltado para livros.

A proposta é permitir que usuários mantenham sua própria biblioteca de leituras, registrando quais livros estão lendo ou já leram, além de informações como **nota, resenha, idioma da leitura e período de leitura**.

O projeto também busca colocar em prática conceitos de desenvolvimento backend, como:

* Arquitetura em camadas
* API REST
* Spring Data JPA
* Spring Security
* DTOs
* Mappers
* Validação de dados
* Tratamento de exceções
* Testes unitários
* Modelagem de relacionamentos entre entidades

O projeto atualmente é focado exclusivamente no **backend**, não possuindo uma aplicação frontend integrada.

---

## 🚀 Funcionalidades

### 👤 Usuários

* Cadastro de usuários
* Consulta de usuários
* Consulta de usuário por ID
* Atualização dos próprios dados
* Autenticação por login
* Logout
* Criptografia de senhas utilizando BCrypt
* Controle de acesso através do Spring Security

### 📚 Livros

* Cadastro de livros
* Atualização de livros
* Consulta de livro por ID
* Consulta de livro pelo título
* Listagem de livros
* Associação entre livro, autor e editora
* Definição de categoria e formato do livro

### ✍️ Autores

* Cadastro de autores
* Atualização de autores
* Consulta por ID
* Listagem de autores
* Registro do país de origem e gênero

### 🏢 Editoras

* Cadastro de editoras
* Atualização de editoras
* Consulta por ID
* Listagem de editoras
* Registro do país de origem

### 📖 Biblioteca pessoal

A entidade `LivroCliente` representa a relação entre um usuário e um livro.

Por meio dela, o usuário pode registrar:

* 📌 Status da leitura
* 📅 Data de início
* 📅 Data de término
* ⭐ Nota
* 📝 Resenha
* 🌎 Idioma em que o livro foi lido

Isso permite separar os dados gerais de um livro dos dados específicos da experiência de cada usuário com aquele livro.

---

## 🧠 Como o sistema funciona

O funcionamento do Edicria é baseado na relação entre **livros cadastrados no sistema** e a **biblioteca individual de cada usuário**.

O fluxo principal pode ser representado da seguinte maneira:

```text
                    ┌──────────────┐
                    │    Usuário   │
                    └──────┬───────┘
                           │
                           │ possui
                           ▼
                    ┌──────────────┐
                    │ LivroCliente │
                    └──────┬───────┘
                           │
                           │ referencia
                           ▼
                    ┌──────────────┐
                    │    Livro     │
                    └──────┬───────┘
                           │
                    ┌──────┴───────┐
                    ▼              ▼
              ┌──────────┐   ┌──────────┐
              │  Autor   │   │  Editora │
              └──────────┘   └──────────┘
```

### Fluxo de utilização

1. Um usuário cria sua conta.
2. O usuário realiza login na API.
3. Livros são cadastrados no sistema juntamente com seus autores e editoras.
4. O usuário adiciona um livro à sua biblioteca.
5. O usuário pode definir o status da leitura.
6. Durante ou após a leitura, pode registrar:

   * Nota
   * Resenha
   * Idioma
   * Datas de início e término
7. Essas informações ficam associadas especificamente àquele usuário e àquele livro.

Dessa forma, **um mesmo livro pode fazer parte da biblioteca de vários usuários**, mas cada usuário pode possuir sua própria nota, resenha e histórico de leitura.

---

## 🛠️ Tecnologias utilizadas

### Backend

* ☕ **Java 21**
* 🚀 **Spring Boot 4.1.1**
* 🔐 **Spring Security**
* 🗄️ **Spring Data JPA**
* 🐘 **PostgreSQL**
* 📦 **Maven**
* 🧩 **Hibernate**
* 📝 **Bean Validation**
* 🗺️ **DTOs e Mappers**
* 🔒 **BCrypt**
* 📚 **SpringDoc OpenAPI / Swagger**
* 🧪 **JUnit 5**
* 🎭 **Mockito**
* 🛠️ **Lombok**

---

## 🏗️ Arquitetura

O projeto utiliza uma arquitetura organizada em camadas, separando as responsabilidades de cada parte da aplicação.

```text
src/main/java/com/qqd/edicria/

├── configs/
│   └── SecurityConfig.java
│
├── controllers/
│   ├── tabelasPrincipais/
│   │   ├── AutorController.java
│   │   ├── EditoraController.java
│   │   ├── LivroController.java
│   │   └── UsuarioController.java
│   │
│   └── tabelasAuxiliares/
│       └── LivroClienteController.java
│
├── dtos/
│   ├── request/
│   └── response/
│
├── entities/
│   ├── enums/
│   ├── tabelasPrincipais/
│   │   ├── Autor.java
│   │   ├── Editora.java
│   │   ├── Livro.java
│   │   └── Usuario.java
│   │
│   └── tabelasAuxiliares/
│       └── LivroCliente.java
│
├── exceptions/
│
├── mappers/
│
├── repositories/
│
└── services/
```

### Responsabilidade das camadas

**Controllers**

Responsáveis por receber as requisições HTTP e retornar as respostas da API.

**Services**

Concentram as regras de negócio da aplicação.

**Repositories**

Responsáveis pela comunicação com o banco de dados através do Spring Data JPA.

**Entities**

Representam as entidades persistidas no banco de dados.

**DTOs**

Controlam os dados recebidos e enviados pela API, evitando expor diretamente as entidades.

**Mappers**

Realizam a conversão entre entidades e DTOs.

**Exceptions**

Centralizam as exceções específicas das regras de negócio, como registros duplicados ou entidades inexistentes.

---

## 🔐 Autenticação

O Edicria utiliza **Spring Security** para controlar o acesso aos endpoints.

A autenticação utiliza uma abordagem baseada em **sessão**, permitindo que o usuário realize login e mantenha sua autenticação enquanto utiliza a API.

As senhas dos usuários não são armazenadas diretamente. Antes de serem persistidas, elas passam pelo `BCryptPasswordEncoder`.

Exemplo do fluxo:

```text
POST /api/users/login
        │
        ▼
Spring Security
        │
        ▼
Verificação do email
        │
        ▼
Comparação da senha com BCrypt
        │
        ▼
Usuário autenticado
        │
        ▼
Sessão HTTP
```

Endpoints relacionados:

```http
POST /api/users
POST /api/users/login
POST /api/users/logout
PUT  /api/users/me
```

Os endpoints protegidos exigem autenticação, enquanto os endpoints necessários para criação e autenticação do usuário permanecem públicos.

---

## 🌐 Principais endpoints

### Usuários

| Método | Endpoint            | Descrição                 |
| ------ | ------------------- | ------------------------- |
| `POST` | `/api/users`        | Cadastrar usuário         |
| `GET`  | `/api/users`        | Listar usuários           |
| `GET`  | `/api/users/{id}`   | Buscar usuário            |
| `PUT`  | `/api/users/me`     | Atualizar próprio usuário |
| `POST` | `/api/users/login`  | Realizar login            |
| `POST` | `/api/users/logout` | Realizar logout           |

### Livros

| Método | Endpoint            | Descrição               |
| ------ | ------------------- | ----------------------- |
| `POST` | `/api/books`        | Cadastrar livro         |
| `GET`  | `/api/books`        | Listar livros           |
| `GET`  | `/api/books/{id}`   | Buscar livro            |
| `GET`  | `/api/books/titulo` | Buscar livro por título |
| `PUT`  | `/api/books/{id}`   | Atualizar livro         |

### Autores

| Método | Endpoint            | Descrição       |
| ------ | ------------------- | --------------- |
| `POST` | `/api/authors`      | Cadastrar autor |
| `GET`  | `/api/authors`      | Listar autores  |
| `GET`  | `/api/authors/{id}` | Buscar autor    |
| `PUT`  | `/api/authors/{id}` | Atualizar autor |

### Editoras

| Método | Endpoint               | Descrição         |
| ------ | ---------------------- | ----------------- |
| `POST` | `/api/publishers`      | Cadastrar editora |
| `GET`  | `/api/publishers`      | Listar editoras   |
| `GET`  | `/api/publishers/{id}` | Buscar editora    |
| `PUT`  | `/api/publishers/{id}` | Atualizar editora |

### Biblioteca do usuário

| Método   | Endpoint                               | Descrição                     |
| -------- | -------------------------------------- | ----------------------------- |
| `POST`   | `/api/book-user`                       | Adicionar livro à biblioteca  |
| `PUT`    | `/api/book-user/{idLivro}/{idUsuario}` | Atualizar registro de leitura |
| `GET`    | `/api/book-user/{id}`                  | Buscar registro               |
| `GET`    | `/api/book-user/{idLivro}/{idUsuario}` | Buscar livro de um usuário    |
| `GET`    | `/api/book-user/all/{id}`              | Listar livros de um usuário   |
| `DELETE` | `/api/book-user/{id}`                  | Remover livro da biblioteca   |

---

## 🗃️ Modelo de dados

O domínio principal do sistema é formado pelas seguintes entidades:

### `Usuario`

Representa o usuário da plataforma.

```text
Usuario
├── id
├── nome
├── email
├── senha
├── genero
├── paisOrigem
├── dataNascimento
└── adm
```

### `Livro`

Representa um livro disponível na plataforma.

```text
Livro
├── id
├── titulo
├── autor
├── editora
├── dataLancamento
├── categoria
└── formato
```

### `Autor`

Representa o autor de uma obra.

```text
Autor
├── id
├── nome
├── paisOrigem
└── genero
```

### `Editora`

Representa a editora responsável pela publicação.

```text
Editora
├── id
├── nome
└── paisOrigem
```

### `LivroCliente`

Representa a experiência de um usuário com determinado livro.

```text
LivroCliente
├── id
├── usuario
├── livro
├── status
├── dataInicioLeitura
├── dataTerminoLeitura
├── nota
├── resenha
└── linguaLida
```

---

## 🧪 Testes

O projeto possui testes unitários utilizando **JUnit 5 e Mockito**, principalmente para a camada de serviços.

Entre os cenários testados estão:

* Cadastro de usuários
* Validação de email já cadastrado
* Validação de nome já cadastrado
* Criptografia de senha
* Atualização de usuários
* Cadastro de livros
* Validação de título duplicado
* Autor ou editora inexistente
* Operações relacionadas à biblioteca do usuário
* Autenticação

Os testes utilizam mocks para isolar os serviços de seus repositories e demais dependências.

Exemplo:

```java
verify(usuarioRepository, never()).save(any());
```

Esse tipo de verificação garante que uma operação de persistência não seja executada quando uma regra de negócio impede o cadastro.

---

## 📚 Documentação da API

O projeto utiliza **SpringDoc OpenAPI**, permitindo gerar a documentação da API e facilitar os testes dos endpoints.

A especificação OpenAPI está disponível através de:

```text
/api-docs
```

A interface do Swagger pode ser utilizada para visualizar e testar os endpoints da aplicação.

---

## ⚙️ Como executar o projeto

### 🔽 1. Clonar o repositório

```bash
git clone https://github.com/PedroFernando04/Edicria-Java.git
```

```bash
cd Edicria-Java
```

---

### 🐘 2. Configurar o PostgreSQL

Crie um banco de dados PostgreSQL chamado:

```text
edicria
```

Por padrão, o projeto utiliza:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/edicria
spring.datasource.username=postgres
spring.datasource.password=123456
```

Caso suas credenciais sejam diferentes, altere o arquivo:

```text
src/main/resources/application.properties
```

> ⚠️ As credenciais presentes no arquivo de configuração são destinadas ao ambiente local de desenvolvimento. Em produção, recomenda-se utilizar variáveis de ambiente ou outro mecanismo seguro de gerenciamento de credenciais.

---

### ▶️ 3. Executar o projeto

Utilizando o Maven Wrapper:

**Windows:**

```bash
mvnw.cmd spring-boot:run
```

**Linux/macOS:**

```bash
./mvnw spring-boot:run
```

Ou execute a classe:

```text
EdicriaApplication.java
```

A API estará disponível em:

```text
http://localhost:8080
```

---

### 🧪 4. Executar os testes

```bash
mvnw.cmd test
```

No Linux/macOS:

```bash
./mvnw test
```

---

## 🔧 Configuração do banco

Atualmente, o projeto está configurado para recriar as tabelas automaticamente durante a execução:

```properties
spring.jpa.hibernate.ddl-auto=create-drop
```

Essa configuração é adequada para desenvolvimento e testes iniciais, mas **não deve ser utilizada em um ambiente de produção**, pois as tabelas podem ser recriadas quando a aplicação é iniciada.

---

## 🚧 Melhorias futuras

O Edicria ainda está em desenvolvimento e possui espaço para evoluir.

Algumas possibilidades futuras:

* 🎨 Desenvolvimento de um frontend para consumo da API
* 👤 Perfil público dos usuários
* 📚 Página de detalhes dos livros
* ⭐ Sistema de avaliações e médias
* 📊 Estatísticas pessoais de leitura
* 📅 Histórico anual de leituras
* 🏆 Metas de leitura
* 🔎 Busca e filtros avançados
* 📖 Listas personalizadas de livros
* 👥 Sistema de seguidores
* 💬 Interação entre usuários
* ❤️ Curtidas em resenhas
* 📈 Dashboard de estatísticas
* ☁️ Deploy da aplicação em ambiente de nuvem

---

## 🎯 Objetivo do projeto

O Edicria foi criado como um **projeto pessoal de desenvolvimento backend**, com o objetivo de construir uma aplicação que seja interessante do ponto de vista de domínio e, ao mesmo tempo, permita aprofundar conhecimentos em desenvolvimento de APIs utilizando Java e Spring.

O projeto também serve como espaço para praticar conceitos como:

* Desenvolvimento de APIs REST
* Spring Boot
* Spring Security
* Autenticação baseada em sessão
* Persistência com JPA/Hibernate
* Modelagem de relacionamentos
* DTOs
* Mappers
* Validação
* Tratamento de exceções
* Testes unitários com JUnit e Mockito
* Documentação de APIs com OpenAPI

---

## 👨‍💻 Autor

**Pedro Fernando**

Desenvolvedor focado em desenvolvimento backend com Java e Spring.

* GitHub: [PedroFernando04](https://github.com/PedroFernando04)

---

## 📄 Licença

Este projeto é de uso pessoal e está disponível para fins de estudo e desenvolvimento.

Não há uma licença open source definida atualmente.
