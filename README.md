# Secure File Management System 

This project is a secure multi-user file management backend built with Java (Spring Boot) and MySQL.
To keep the project **zero cost**, it uses:
- MySQL via Docker
- LocalStack (S3 emulator) via Docker
- AWS SDK v2 in the backend (so code is AWS-compatible)

## Requirements
- Java 17
- Maven
- Docker Desktop

## Start dependencies (MySQL + LocalStack)
```bash
docker compose up -d
