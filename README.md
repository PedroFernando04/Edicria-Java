# 📚 EDICRIA — Registrador de Leituras

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge\&logo=openjdk\&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?style=for-the-badge\&logo=springboot\&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge\&logo=springsecurity\&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge\&logo=spring\&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge\&logo=hibernate\&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge\&logo=postgresql\&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit%205-25A162?style=for-the-badge\&logo=junit5\&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-78C257?style=for-the-badge\&logo=mockito\&logoColor=white)
![OpenAPI](https://img.shields.io/badge/OpenAPI-6BA539?style=for-the-badge\&logo=openapiinitiative\&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge\&logo=apachemaven\&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-BC4521?style=for-the-badge\&logo=lombok\&logoColor=white)

![API REST](https://img.shields.io/badge/API-REST-005571?style=for-the-badge)
![Arquitetura](https://img.shields.io/badge/Architecture-Layered-8A2BE2?style=for-the-badge)
![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow?style=for-the-badge)

---

## 📖 Sobre o Projeto

O **Edicria** é uma **API REST backend** desenvolvida em **Java com Spring Boot**, criada como um projeto pessoal para funcionar como um **registrador de leituras**, inspirado em plataformas como o Letterboxd, mas voltado para livros.

A aplicação permite que usuários mantenham sua própria biblioteca de leituras, registrando quais livros estão lendo ou já leram, além de informações como **nota, resenha, idioma da leitura e período de leitura**.

O projeto também foi desenvolvido com foco em boas práticas de desenvolvimento backend, utilizando **Spring Security para autenticação, JPA/Hibernate para persistência, DTOs e Mappers para separação entre camadas e JUnit + Mockito para testes unitários**.

Atualmente, o projeto é focado exclusivamente no **backend**, não possuindo uma aplicação frontend integrada.

---

## 🛠️ Stack Tecnológica

### Backend

* ☕ **Java 21**
* 🚀 **Spring Boot**
* 🔐 **Spring Security**
* 🗄️ **Spring Data JPA**
* 🛢️ **Hibernate**
* 🐘 **PostgreSQL**
* 📦 **Maven**
* 🧩 **Lombok**

### Qualidade e testes

* 🧪 **JUnit 5**
* 🎭 **Mockito**
* ✅ **Testes unitários**
* 🔍 **Validação de regras de negócio**
* 🧱 **Testes isolados utilizando mocks**

### API

* 🌐 **REST API**
* 📋 **DTOs**
* 🔄 **Mappers**
* 📚 **OpenAPI / Swagger**
* ⚠️ **Tratamento global de exceções**
* ✅ **Bean Validation**

### Arquitetura

* 🏗️ **Arquitetura em camadas**
* 🎯 **Separação de responsabilidades**
* 📦 **Controllers**
* ⚙️ **Services**
* 🗃️ **Repositories**
* 🧱 **Entities**
* 📋 **DTOs**
* 🔄 **Mappers**

---

## 🧪 Testes Unitários

Uma das partes importantes do projeto é a utilização de **testes unitários com JUnit 5 e Mockito**.

Os testes são utilizados principalmente para validar as regras de negócio da camada de serviços sem depender diretamente do banco de dados.

O Mockito é utilizado para criar mocks dos repositories e demais dependências:

```java
@Mock
private UsuarioRepository usuarioRepository;
```

E verificar o comportamento esperado:

```java
verify(usuarioRepository).save(any(Usuario.class));
```

Também são testados cenários em que determinadas operações **não devem acontecer**:

```java
verify(usuarioRepository, never()).save(any());
```

Dessa forma, além de verificar o resultado de uma operação, os testes também verificam **como o serviço interage com suas dependências**.

---

## 🔐 Segurança

A autenticação da API é implementada utilizando **Spring Security** com autenticação baseada em sessão.

As senhas dos usuários são protegidas utilizando **BCrypt**, evitando o armazenamento de senhas em texto puro.

O projeto também separa os endpoints públicos dos endpoints que exigem um usuário autenticado.

```text
Cliente
   │
   │ Login
   ▼
Spring Security
   │
   ├── Busca usuário
   ├── Verifica senha (BCrypt)
   └── Cria sessão
          │
          ▼
   Endpoints protegidos
```
