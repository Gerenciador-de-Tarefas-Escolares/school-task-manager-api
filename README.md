# ⚙️ Back-end — API Gerenciador de Tarefas e Planejador Acadêmico

API RESTful desenvolvida em **Java 21** e **Spring Boot 3** para o gerenciamento de tarefas escolares, matérias dinâmicas, calendário acadêmico, avaliações com notas e autenticação segura via **JWT** e **Google OAuth2**.

---

## 🌐 Acesso Online (Deploy em Produção)

* **Documentação Interativa (Swagger UI):** https://school-task-manager-api.onrender.com/swagger-ui/index.html
* **Especificação OpenAPI 3 (JSON):** https://school-task-manager-api.onrender.com/v3/api-docs
* **Aplicação Web (Front-end):** https://school-task-manager-front.vercel.app
* **Repositório do Banco de Dados (PostgreSQL + Docker):** https://github.com/Gerenciador-de-Tarefas-Escolares/Gerenciador-tarefa-banco.git

> **Nota para avaliação:** O servidor está hospedado em plano gratuito (Render) e entra em modo de suspensão após 15 minutos sem uso. Caso a primeira requisição demore cerca de 30 a 50 segundos, aguarde o carregamento inicial.

---

## 🏗️ Arquitetura da Aplicação

O sistema adota uma arquitetura desacoplada em três camadas (Cliente-Servidor-Banco), separadas em repositórios independentes e conteinerizados via Docker:

1. **Camada de Apresentação (Front-end — React + Vite):** Interface SPA hospedada no **Vercel** que consome os endpoints REST autenticados via Bearer Token JWT.
2. **Camada de Negócio e Segurança (Back-end — Spring Boot 3):** API RESTful *Stateless* hospedada no **Render**, estruturada em camadas (`Controller` → `Service` → `Repository` → `Model`) com filtro de segurança JWT (`JwtAuthenticationFilter`), criptografia **BCrypt** e isolamento estrito de dados por `usuarioId`.
3. **Camada de Persistência (Banco de Dados — PostgreSQL 16):** Esquema relacional normalizado (`cadastro`, `materia`, `tarefa`, `avaliacao` e view `calendario`), utilizando `sequences` com incremento padronizado para o Hibernate e integridade referencial com exclusão em cascata.

---

## ✨ Detalhamento das Funcionalidades Implementadas

* **Autenticação e Conta:** Registro de estudante com verificação de unicidade de e-mail (*case-insensitive*), login tradicional com emissão de token JWT, autenticação via **Google OAuth2**, recuperação/redefinição de senha com token temporário e exclusão definitiva de conta.
* **Gestão de Tarefas (`/api/tarefas`):** CRUD completo com suporte a prioridades, status de andamento, prazos de entrega e criação dinâmica de matérias escolares sob demanda.
* **Consulta de Matérias (`/api/materias`):** Listagem de disciplinas vinculadas ao estudante autenticado.
* **Gestão de Avaliações (`/api/avaliacoes`):** CRUD completo de provas, testes, exames e trabalhos com validação de notas e atualização rápida para cálculo de média no front-end.

---

## 🛠️ Tecnologias e Bibliotecas

* **Java 21** & **Spring Boot 3**
* **Spring Security** & **JWT (JSON Web Token)**
* **Spring Data JPA / Hibernate**
* **PostgreSQL** (Banco de dados relacional)
* **Springdoc OpenAPI** (`v3/api-docs` e `swagger-ui`)
* **JUnit 5**, **Mockito** & **JaCoCo** (Testes unitários e relatório de cobertura)
* **Docker**

---

## 📂 Estrutura de Pacotes (`com.escolar.gerenciadortarefas`)

* `config/` — Configurações de segurança (`SecurityConfig`), CORS dinâmico, OpenAPI/Swagger, chaves JWT, OAuth2 e tratamento global de exceções (`GlobalExceptionHandler`).
* `controller/` — Endpoints REST padronizados com anotações OpenAPI (`UsuarioController`, `ContaController`, `TarefaController`, `MateriaController`, `AvaliacaoController`).
* `dto/` — Objetos de transferência e validação de entrada/saída (`RegistroDTO`, `LoginDTO`, `TarefaDTO`, `AvaliacaoDTO`, `RecuperarSenhaDTO`, `TokenResponseDTO`).
* `model/` — Entidades JPA mapeadas no PostgreSQL (`Usuario`, `Tarefa`, `Materia`, `Avaliacao` com geração automática de ID via `@PrePersist` compatível com o esquema original do banco).
* `repository/` — Interfaces Spring Data JPA com isolamento de consultas por `usuarioId`.
* `security/` — Filtro de autenticação JWT (`JwtAuthenticationFilter`), `UsuarioUserDetails` e serviços de token.
* `service/` — Regras de negócio e validações (`AutentificacaoService`, `ContaService`, `TarefaService`, `AvaliacaoService`, `JwtService`).

---

## 📡 Endpoints Principais

Documentação interativa e especificação OpenAPI 3 disponíveis tanto online quanto localmente:
* **OpenAPI JSON (para Bruno / Postman):** `http://localhost:8080/v3/api-docs`
* **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`

### Autenticação e Conta (`/api/auth` e `/api/conta`)
* `POST /api/auth/registro` — Cadastro de novo estudante (Pública).
* `POST /api/auth/login` — Login tradicional com retorno de Bearer Token JWT (Pública).
* `POST /api/auth/google` — Login via Google OAuth2 (Pública).
* `POST /api/auth/esqueceu-senha` — Geração de token temporário para redefinição de senha (Pública).
* `POST /api/auth/redefinir-senha` — Atualização de senha usando o token de recuperação (Pública).
* `DELETE /api/conta` — Exclusão definitiva da conta do usuário autenticado (Protegida).

### Tarefas (`/api/tarefas`) — *Todas Protegidas (Bearer JWT)*
* `GET /api/tarefas` — Lista todas as tarefas do usuário logado.
* `POST /api/tarefas` — Cria uma nova tarefa (suporta criação dinâmica de matéria via `novaMateriaNome`).
* `PUT /api/tarefas/{id}` — Atualiza título, descrição, prazo, prioridade, status ou matéria.
* `DELETE /api/tarefas/{id}` — Exclui uma tarefa pertencente ao usuário logado.

### Matérias (`/api/materias`) — *Protegida (Bearer JWT)*
* `GET /api/materias` — Lista as matérias vinculadas ao usuário logado.

### Avaliações (`/api/avaliacoes`) — *Todas Protegidas (Bearer JWT)*
* `GET /api/avaliacoes` — Lista provas, trabalhos e seminários ordenados por data.
* `POST /api/avaliacoes` — Cadastra uma nova avaliação e nota opcional (com suporte a criação dinâmica de matéria).
* `PUT /api/avaliacoes/{id}` — Atualiza dados ou lança/edita a nota da avaliação.
* `DELETE /api/avaliacoes/{id}` — Remove uma avaliação do usuário logado.

---

## ⚙️ Como Executar

### 1. Configuração de Ambiente
Suba o banco PostgreSQL usando o repositório oficial (`Gerenciador-tarefa-banco`) e crie um arquivo `.env` na raiz deste repositório baseado no `.env.example` (ou ajuste `src/main/resources/application.properties`) informando:
* URL, usuário e senha do **PostgreSQL** (`ddl-auto=none`, compatível diretamente com o script `01_criar_tabelas.sql`).
* Chave secreta **JWT**.
* Client ID do **Google OAuth2** (opcional para login social).

### 2. Execução Local (Maven)
```bash
./mvnw spring-boot:run
```
A API iniciará em **`http://localhost:8080`**.

### 3. Execução via Docker
```bash
docker build -t gerenciador-tarefas-backend .
docker run -p 8080:8080 --env-file .env gerenciador-tarefas-backend
```

---

## 🧪 Testes Automatizados e Cobertura (JaCoCo)

Para executar todos os testes unitários e gerar o relatório de cobertura em `target/site/jacoco/index.html`:

```bash
./mvnw test
```

* **Cobertura Global:** **89%**
* **Camada Controller:** **100%**
* **Camada Security:** **99%**
* **Camada Service:** **85%+**

---

## 💡 Aspectos Relevantes, Desafios Técnicos e Decisões de Design

* **Compatibilidade Estrita com o Esquema SQL (`ddl-auto=none`):** Normalização automática de campos opcionais (`telefone`, `dataNascimento`, matéria padrão e tipos de avaliação) na camada de serviço (`AutentificacaoService` e `AvaliacaoService`) para atender às restrições `NOT NULL` e `CHECK` do PostgreSQL inclusive em cadastros via Google OAuth2.
* **CORS Dinâmico Multiambiente:** Configuração centralizada no `SecurityConfig` via variável de ambiente `CORS_ALLOWED_ORIGINS`, permitindo alternar entre ambiente local (`localhost:5173`), Docker e produção (`vercel.app`) sem alterações no código-fonte.
* **Qualidade e Rastreabilidade:** Cobertura de testes de **89%** com **JUnit 5**, **Mockito** e **JaCoCo**, além de documentação interativa **OpenAPI 3 / Swagger UI** habilitada em produção.
