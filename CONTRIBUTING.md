# Contributing to E-commerce Microservices Platform

Thank you for your interest in contributing to our e-commerce microservices platform! This document provides guidelines and instructions for contributing to the project.

## Code Style Guidelines

### Java (User Service & Product Service)

- Follow standard Java naming conventions
- Use Spring Boot best practices
- Implement comprehensive unit tests using JUnit
- Document public APIs using Javadoc
- Use Lombok annotations to reduce boilerplate code

### Python (Order Service)

- Follow PEP 8 style guide
- Use type hints for function parameters and return types
- Write unit tests using pytest
- Document functions using docstrings
- Use Black for code formatting

### Node.js (Notification Service)

- Follow JavaScript Standard Style
- Use ES6+ features appropriately
- Write unit tests using Jest
- Use JSDoc for documentation
- Implement async/await pattern for asynchronous operations

## Git Workflow

### Branch Naming Convention

- Feature branches: `feature/service-name/description`
- Bug fixes: `fix/service-name/description`
- Documentation: `docs/description`
- Performance improvements: `perf/service-name/description`

### Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

Types:

- feat: new feature
- fix: bug fix
- docs: documentation changes
- style: formatting, missing semicolons, etc.
- refactor: code refactoring
- test: adding tests
- chore: maintenance tasks

### Pull Request Process

1. Create a new branch from `develop`
2. Make your changes following the code style guidelines
3. Write/update tests as needed
4. Update documentation if required
5. Ensure all tests pass
6. Submit PR against the `develop` branch
7. Wait for code review and address any feedback

## Testing Requirements

### Unit Tests

- All new features must include unit tests
- Maintain minimum 80% code coverage
- Tests should be meaningful and cover edge cases

### Integration Tests

- Write integration tests for API endpoints
- Test service interactions using mock services
- Verify database operations

## Documentation

### API Documentation

- Use OpenAPI/Swagger for REST API documentation
- Include request/response examples
- Document error responses

### Code Documentation

- Add comments for complex logic
- Update README files when adding new features
- Include setup instructions for new dependencies

## Development Setup

1. Fork the repository
2. Clone your fork
3. Install required dependencies:
   - Java 11 or higher
   - Python 3.9 or higher
   - Node.js 14 or higher
   - Docker and Docker Compose
4. Follow service-specific README for local setup

## Questions and Support

- Open an issue for bug reports or feature requests
- Join our community discussions
- Check existing issues and PRs before creating new ones

## Code of Conduct

Please note that this project follows our [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code.

## License

By contributing to this project, you agree that your contributions will be licensed under the project's MIT License.
