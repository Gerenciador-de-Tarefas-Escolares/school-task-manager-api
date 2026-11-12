
# ⚙️ Back-end — API Gerenciador de Tarefas e Planejador Acadêmico

API RESTful desenvolvida em **Java 21** e **Spring Boot 3** para o gerenciamento de tarefas escolares, matérias dinâmicas, calendário acadêmico, avaliações com notas e autenticação segura via **JWT** e **Google OAuth2**.

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

* `config/` — Configurações de segurança (`SecurityConfig`), CORS, OpenAPI/Swagger, chaves JWT, OAuth2 e tratamento global de exceções (`GlobalExceptionHandler`).
* `controller/` — Endpoints REST padronizados com anotações OpenAPI (`UsuarioController`, `ContaController`, `TarefaController`, `MateriaController`, `AvaliacaoController`).
* `dto/` — Objetos de transferência e validação de entrada/saída (`RegistroDTO`, `LoginDTO`, `TarefaDTO`, `AvaliacaoDTO`, `RecuperarSenhaDTO`, `TokenResponseDTO`).
* `model/` — Entidades JPA mapeadas no PostgreSQL (`Usuario`, `Tarefa`, `Materia`, `Avaliacao` com geração automática de ID via `@PrePersist` compatível com o esquema original do banco).
* `repository/` — Interfaces Spring Data JPA com isolamento de consultas por `usuarioId`.
* `security/` — Filtro de autenticação JWT (`JwtAuthenticationFilter`), `UsuarioUserDetails` e serviços de token.
* `service/` — Regras de negócio e validações (`AutentificacaoService`, `ContaService`, `TarefaService`, `AvaliacaoService`, `JwtService`).

---

## 📡 Endpoints Principais

Documentação interativa e especificação OpenAPI 3 disponíveis com o servidor ativo:
* **OpenAPI JSON (para Bruno / Postman):** `http://localhost:8080/v3/api-docs`
* **Swagger UI:** `http://localhost:8080/swagger-ui.html`

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
Crie um arquivo `.env` na raiz da pasta `backend/` baseado no `.env.example` (ou ajuste `src/main/resources/application.properties`) informando:
* URL, usuário e senha do **PostgreSQL** (compatível diretamente com o esquema SQL original, sem necessidade de scripts de migração adicionais).
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
