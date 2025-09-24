# Sistema BiblioLobato
 
Este repositório contém a aplicação web "BiblioLobato": um sistema simples de gerenciamento de biblioteca construído com Spring Boot 3, Thymeleaf e MongoDB.
 
Este README explica como iniciar o projeto localmente, descreve a estrutura de diretórios e resume a função dos principais arquivos.
 
# Sistema BiblioLobato
 
Este repositório contém a aplicação web "BiblioLobato": um sistema simples de gerenciamento de biblioteca construído com Spring Boot 3, Thymeleaf e MongoDB.
 
Este README explica como iniciar o projeto localmente, descreve a estrutura de diretórios e resume a função dos principais arquivos.
 
## Requisitos
 
- Java 21 (compatível com a propriedade `java.version` do `pom.xml`).
- Maven (ou use o wrapper `./mvnw` / `mvnw.cmd` já incluído).
- Uma instância MongoDB acessível (Atlas ou local). O projeto espera uma URI em `spring.data.mongodb.uri`.
 
Recomenda-se ter também um IDE (IntelliJ IDEA, VS Code) e o Git.
 
## Como rodar (modo rápido)
 
1. Ajuste a configuração do MongoDB:
 
   - Abra `src/main/resources/application.properties` e atualize `spring.data.mongodb.uri` com a sua conexão. O repositório contém um exemplo de string de conexão; substitua por credenciais seguras.
   - Alternativamente, configure a variável de ambiente `SPRING_DATA_MONGODB_URI` ou use perfis externos para não colocar credenciais no repositório.
 
2. Build e execução usando o wrapper (Windows PowerShell):
 
```powershell
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```
 
Ou usando o Maven instalado:
 
```powershell
mvn clean package
mvn spring-boot:run
```
 
3. Acesse a aplicação em http://localhost:8080
 
Observações:
- Para executar o jar após build: `java -jar target\lobato-0.0.1-SNAPSHOT.jar`.
- O endpoint de login é `/login`. Há endpoints de debug úteis (ver seção "Endpoints úteis para desenvolvimento").
 
## Endpoints úteis para desenvolvimento
 
- GET `/create-simple-user` — cria um usuário simples (username: `simpleuser`, senha: `password123`). Útil para testes locais.
- GET `/list-all-users` — lista usuários armazenados no MongoDB (apenas para debug).
- GET `/create-sample-book` — cria um livro de exemplo.
- GET `/list-all-books` — lista livros no banco.
- GET `/clean-duplicates` — remove usuários duplicados por `username`.
 
Use esses endpoints apenas em ambiente de desenvolvimento!
 
## Estrutura do projeto
 
Raiz do projeto (resumo):
 
- `pom.xml` — dependências e configuração do Maven. Define Java 21 e inclui Spring Boot, Spring Security, Thymeleaf e Spring Data MongoDB.
- `mvnw`, `mvnw.cmd` — Maven Wrapper para executar Maven sem instalá-lo globalmente.
- `src/main/java/com/lobato/lobato/` — código-fonte Java da aplicação.
- `src/main/resources/` — recursos (templates Thymeleaf, `application.properties`).
- `src/test/` — testes unitários (ex.: `LobatoApplicationTests`).
 
### Pacotes principais (src/main/java/com/lobato/lobato)
 
- `LobatoApplication.java` — classe principal com o método `main` que inicia a aplicação Spring Boot.
 
- `config/SecurityConfig.java` — configuração do Spring Security. Define:
  - PasswordEncoder (BCrypt).
  - AuthenticationManager.
  - Regras de autorização e formulário de login (custom login page `/login`, URL de processamento `/perform_login`, comportamento de logout).
 
- `controller/` — controladores MVC (Thymeleaf):
  - `AuthController.java` — tratamento de rotas de autenticação e páginas principais (login, profile, users, settings). Contém endpoints de auxílio para criação/listagem de usuários, upload de comprovantes (comprovante de residência e documento) e visualização desses arquivos.
  - `BookController.java` — rotas para CRUD de livros (listar, cadastrar, atualizar, excluir) e endpoints API para testes (`/api/book/{id}`, `/create-sample-book`, `/list-all-books`).
 
- `model/` — classes de domínio mapeadas para coleções MongoDB:
  - `User.java` — representa usuários (implementa `UserDetails`). Contém campos pessoais, dados de endereço, campos binários para comprovantes (arquivos) e roles.
  - `Book.java` — representa livros com validações (título, autor, editora, quantidade, ano).
 
- `repository/` — interfaces Spring Data MongoDB:
  - `UserRepository.java` — métodos para buscar por username/email e operações auxiliares.
  - `BookRepository.java` — busca por título, autor, editora e métodos `existsBy...`.
 
- `service/` — lógica de negócio mínima/wrappers sobre repositories:
  - `CustomUserDetailsService.java` — implementa `UserDetailsService` para o Spring Security e expõe métodos para salvar usuários (encodificando a senha) e checagens de existência.
  - `BookService.java` — operações CRUD para `Book` delegando ao `BookRepository`.
 
### Recursos (src/main/resources)
 
- `application.properties` — configurações da aplicação (nome, `spring.data.mongodb.uri`, Thymeleaf, limite de upload multipart, níveis de log). Substitua a URI do MongoDB por credenciais seguras.
- `templates/` — views Thymeleaf usadas pela aplicação:
  - `login.html` — página de login customizada.
  - `books.html` — página de listagem e gerenciamento de livros (formulários para cadastro/edição).
  - `users.html`, `profile.html`, `settings.html`, `reports.html` — outras views apresentadas pela aplicação.
 
## Entidades
 
Esta seção descreve as entidades centrais do sistema: `User` e `Book`. Aponto os campos principais, validações aplicadas, e como cada entidade é usada por controllers e serviços.
 
### User
 
- Coleção MongoDB: `users` (annotação `@Document(collection = "users")`).
- Classe: `src/main/java/com/lobato/lobato/model/User.java` (implementa `UserDetails`).
 
Campos principais:
- `id` (String) — identificador gerado pelo MongoDB (`@Id`).
- `username` (String) — obrigatório, único (`@Indexed(unique = true)`), tamanho entre 3 e 20.
- `email` (String) — obrigatório, formatado como e-mail (`@Email`), único (`@Indexed(unique = true)`).
- `password` (String) — obrigatório, mínimo 6 caracteres. As senhas são armazenadas codificadas (BCrypt) pelo `CustomUserDetailsService` ao salvar o usuário.
- `nome`, `telefone`, `cpf`, `rg` — campos pessoais opcionais.
- Campos de endereço: `logradouro`, `cep`, `numero`, `complemento`, `bairro`, `cidade`, `estado`.
- Campos para documentos (arquivos):
  - `comprovanteResidencia` (byte[]) — conteúdo binário do arquivo de comprovante de residência.
  - `comprovanteResidenciaNome` (String) — nome original do arquivo.
  - `comprovanteResidenciaContentType` (String) — tipo MIME do arquivo.
  - `comprovanteDocumento` / `comprovanteDocumentoNome` / `comprovanteDocumentoContentType` — mesmos propósitos para comprovante de documento pessoal.
- `roles` (Set<String>) — papéis do usuário (ex.: `ROLE_USER`).
- Flags de segurança: `enabled`, `accountNonExpired`, `accountNonLocked`, `credentialsNonExpired`.
 
Validações e comportamento:
- Campos críticos (`username`, `email`, `password`) possuem validações via Jakarta Validation e índices únicos no MongoDB.
- A senha é codificada com BCrypt antes de armazenar (veja `CustomUserDetailsService.saveUser`).
- Implementando `UserDetails`, a entidade é usada diretamente pelo Spring Security para autenticação/autorizações (método `getAuthorities()` converte `roles` em `GrantedAuthority`).
 
Como é usada no app:
- `AuthController` e `CustomUserDetailsService` são os pontos principais de interação:
  - `CustomUserDetailsService.loadUserByUsername` carrega o usuário para autenticação.
  - `AuthController` expõe endpoints para criar usuários de teste (`/create-simple-user`), registrar usuários via formulário (`/register-user-complete`), listar, excluir e limpar duplicatas.
  - Uploads de comprovantes são aceitos no endpoint de registro (`/register-user-complete`) e gravados dentro do documento `User` como bytes + metadata (nome, contentType).
  - Há endpoints para recuperar arquivos: `/user/{userId}/comprovante-residencia` e `/user/{userId}/comprovante-documento` que retornam o conteúdo com o tipo MIME correto.
 
Observações de segurança:
- Nunca exponha endpoints de criação/listagem de usuários em produção sem proteção adequada. Os endpoints de debug existentes são úteis apenas em ambiente local.
- Prefira armazenar arquivos maiores em um storage dedicado (ex.: S3, GridFS) em vez de dentro do documento MongoDB se os arquivos crescerem em tamanho.
 
### Book
 
- Coleção MongoDB: `books` (annotação `@Document(collection = "books")`).
- Classe: `src/main/java/com/lobato/lobato/model/Book.java`.
 
Campos principais e validações:
- `id` (String) — identificador do MongoDB.
- `title` (String) — obrigatório, tamanho entre 3 e 150 (`@NotBlank`, `@Size`).
- `author` (String) — obrigatório, tamanho entre 3 e 150.
- `publisher` (String) — obrigatório, tamanho entre 3 e 150.
- `amount` (Integer) — obrigatório, mínimo 1 (`@NotNull`, `@Min(1)`), representa a quantidade disponível.
- `year` (Integer) — obrigatório, mínimo 1000 (`@NotNull`, `@Min(1000)`).
 
Validações e comportamento:
- Validações são feitas pelo Bean Validation (usando `@Valid` nos controllers). Se houver erros de validação, o `BookController` trata e redireciona com mensagens de erro.
 
Como é usada no app:
- `BookService` encapsula o acesso ao `BookRepository` e fornece métodos como `findAll()`, `findById()`, `save()`, `existsByTitle()` e `deleteAll()`.
- `BookController` usa `BookService` para:
  - Mostrar a lista de livros (`/books`).
  - Criar/atualizar livros (`/register-book`, `/update-book`) — verifica validações e unicidade de título.
  - Excluir livros (`/delete-book`) — o controller trata duplicatas por título e pode excluir todos os registros com o mesmo título se necessário.
  - Endpoints de API/depuração: `/api/book/{id}`, `/create-sample-book`, `/list-all-books`.
 
Observações:
- Atualmente o projeto não impõe título único por banco (há método `existsByTitle` que valida antes de inserir), mas o repositório permite múltiplos registros com o mesmo título (existem páginas que tratam duplicatas). Se desejar, um índice único pode ser criado para `title` conforme a regra de negócio.
 
## Como configurar o banco (MongoDB)
 
1. Se for usar MongoDB Atlas, crie um cluster e um usuário com acesso.
2. Copie a connection string (no formato `mongodb+srv://<user>:<password>@cluster.../dbname`) para `spring.data.mongodb.uri` em `application.properties` ou passe-a via variável de ambiente.
 
Segurança: nunca comite credenciais em repositórios públicos. Prefira variáveis de ambiente, arquivos de configuração externos ou serviços de secrets.
 
## Credenciais iniciais e uso
 
- Para testes locais, acesse `GET /create-simple-user` para criar um usuário de teste (`simpleuser` / `password123`). Depois, acesse `/login` com essas credenciais.
