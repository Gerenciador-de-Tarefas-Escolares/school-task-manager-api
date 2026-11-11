# 📚 Gerenciador de Tarefas Escolares — Backend (API REST)

API RESTful desenvolvida em **Java 21** e **Spring Boot** para gerenciamento de tarefas escolares, matérias e autenticação de estudantes (Projeto Final — **Trilha Dev. Full Stack +praTi & Codifica**).

---

## 1. Arquitetura do Sistema

O projeto segue o padrão arquitetural em camadas (**MVC / RESTful**):

```text
src/main/java/com/escolar/gerenciadortarefas/
├── config/          # Configurações de Segurança (CORS, JWT, OpenAPI/Swagger, Beans)
├── controller/      # Controladores REST (/api/auth, /api/tarefas, /api/materias, /api/conta)
├── dto/             # Objetos de Transferência de Dados e validações (@Valid)
├── model/           # Entidades JPA mapeadas para o PostgreSQL (Usuario, Materia, Tarefa)
├── repository/      # Interfaces Spring Data JPA
├── security/        # Filtro JWT, Proxy de revogação de tokens e UserDetails
└── service/         # Regras de negócio (Autenticação, Tarefas, Conta e JWT)
```

### Banco de Dados (PostgreSQL)
* **`cadastro` (`Usuario`)**: Dados do aluno (`id`, `nome`, `email`, `senha`, `telefone`, `data_nascimento`).
* **`materia` (`Materia`)**: Disciplinas vinculadas ao usuário (`id`, `nome`, `cor`, `cadastro_id` com `ON DELETE CASCADE`).
* **`tarefa` (`Tarefa`)**: Atividades escolares (`id`, `titulo`, `descricao`, `data_entrega`, `status`, `prioridade`, `data_conclusao`, `materia_id`, `cadastro_id` com `ON DELETE CASCADE`).

---

## 2. Funcionalidades Implementadas

* **Autenticação e Segurança:** Cadastro e login tradicional com **BCrypt** + **JWT**, autenticação social via **Google OAuth 2.0**, busca de e-mail *case-insensitive*, recuperação de senha com token temporário (15 min) e exclusão de conta em cascata.
* **CRUD de Tarefas e Matérias:** Criação, listagem por usuário autenticado, conclusão/edição e exclusão de tarefas, com associação ou criação dinâmica de matérias.
* **Documentação da API:** Swagger UI integrado em `/swagger-ui.html`.

---

## 3. Como Executar Localmente

### Passo 1 — Subir o Banco de Dados (PostgreSQL 16)
Utilize o repositório oficial do banco de dados do projeto com Docker:

```bash
git clone https://github.com
cd Gerenciador-tarefa-banco
cp .env.example .env
docker compose up -d
```
*(Alternativamente, crie o banco `gerenciador_tarefas` localmente no PostgreSQL e execute o script `01_criar_tabelas.sql`).*

### Passo 2 — Configurar o `.env` do Backend
Copie o modelo `.env.example` para `.env` na raiz do backend (`cp .env.example .env`). **Importante:** os valores de banco, usuário e senha abaixo devem ser os mesmos definidos no `.env` do repositório de banco (`DB_NOME`, `DB_USUARIO` e `DB_SENHA`):

```env
DB_URL=jdbc:postgresql://localhost:5432/gerenciador_tarefas
DB_USER=gerenciador
DB_PASSWORD=troque_esta_senha

JWT_SECRET=sua_chave_secreta_base64_256_bits
JWT_EXPIRATION=86400000

GOOGLE_CLIENT_ID=seu_client_://googleusercontent.com
GOOGLE_CLIENT_SECRET=seu_client_secret
```

### Passo 3 — Iniciar a API

**Opção A — Via Maven (Java 21):**
```bash
mvn clean spring-boot:run
```

**Opção B — Via Docker:**
```bash
docker build -t gerenciadortarefas-api .
docker run -d -p 8080:8080 --env-file .env --name gerenciadortarefas-api gerenciadortarefas-api
```
*(Se o banco estiver rodando no host fora da rede Docker, utilize `host.docker.internal` no lugar de `localhost` na `DB_URL`).*

A API estará disponível em `http://localhost:8080` e o Swagger em `http://localhost:8080/swagger-ui.html`.

---

## 4. Testes Automatizados e Cobertura

O backend utiliza **JUnit 5**, **Mockito**, **H2 Database** e **JaCoCo**, atingindo **87% de cobertura** (acima da meta mínima de 70%):

```bash
mvn clean test
```
O relatório visual é gerado em `target/site/jacoco/index.html`.

---

## 5. Decisões Técnicas

* **Controle de Schema pelo Banco (`ddl-auto=none`):** O schema é gerenciado exclusivamente pelos scripts SQL do repositório de banco de dados, evitando alterações automáticas pelo Hibernate.
* **Normalização de E-mail:** Todos os cadastros e autenticações normalizam o e-mail em minúsculas e consultam via `findByEmailIgnoreCase`.
* **Isolamento por Usuário e Proxy JWT:** Todas as consultas validam o usuário autenticado no `SecurityContextHolder`, e o `SecureJwtProxyService` adiciona suporte à revogação de tokens em memória.
