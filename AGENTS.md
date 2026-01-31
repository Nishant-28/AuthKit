# AuthKit - Agent Guidelines

## Build Commands

```bash
# Build the project
./mvnw clean compile

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=JwtServiceTest

# Run a single test method
./mvnw test -Dtest=JwtServiceTest#testGenerateToken

# Package the application
./mvnw clean package

# Run the application
./mvnw spring-boot:run

# Skip tests during build
./mvnw clean package -DskipTests
```

## Infrastructure

```bash
# Start PostgreSQL and Redis
sudo docker-compose up -d

# Stop infrastructure
sudo docker-compose down

# View logs
sudo docker-compose logs -f
```

## Code Style Guidelines

### Project Structure
- **Base package**: `com.nishant.AuthKit`
- **Entities**: `entity/` - JPA entities with Lombok
- **DTOs**: `dto/` - Data transfer objects with validation annotations
- **Repositories**: `repository/` - Spring Data JPA interfaces
- **Services**: `service/` - Business logic
- **Config**: `config/` - Spring configuration classes
- **Security**: `security/` - JWT filters and security components

### Naming Conventions
- **Classes**: PascalCase (e.g., `UserService`, `JwtAuthenticationFilter`)
- **Methods**: camelCase (e.g., `generateToken`, `extractUsername`)
- **Variables**: camelCase (e.g., `userDetails`, `authHeader`)
- **Constants**: UPPER_SNAKE_CASE
- **Packages**: lowercase with dots (e.g., `com.nishant.AuthKit.service`)
- **DTOs**: Suffix with `DTO` (e.g., `UserRegistrationRequestDTO`)
- **Repositories**: Suffix with `Repository` (e.g., `UserRepository`)

### Imports & Dependencies
- Use **Lombok** annotations: `@Data`, `@Builder`, `@RequiredArgsConstructor`
- Use **Spring Boot 3.x** with Jakarta EE (not `javax`)
- Use **Java 17** features (var where appropriate)
- Group imports: java.*, jakarta.*, org.springframework.*, project-specific
- No wildcard imports

### Code Patterns
- Use constructor injection with `@RequiredArgsConstructor`
- Use `var` for local variables when type is obvious
- Use `@Slf4j` for logging
- Use `Optional` for repository methods that may return null
- Use streams and functional style where readable

### Error Handling
- Use `UsernameNotFoundException` for authentication failures
- Log errors with context: `log.error("Message: {}", variable)`
- Validate inputs with Jakarta Validation annotations
- Return meaningful error messages in validation annotations

### Testing
- Use JUnit 5 with `@SpringBootTest`
- Use `@DataJpaTest` for repository tests
- Use `@WebMvcTest` for controller tests
- Use `@MockBean` for mocking dependencies
- Test naming: `should<ExpectedBehavior>When<Condition>`

### Database & Security
- PostgreSQL 15 for persistence
- Redis 7 for caching/sessions
- JWT tokens with JJWT library
- BCrypt for password hashing
- Role-based access control (ADMIN, USER)

### Configuration
- Environment variables for secrets (JWT_SECRET, JWT_EXPIRATION)
- `application.properties` for non-sensitive config
- Docker Compose for local development infrastructure
