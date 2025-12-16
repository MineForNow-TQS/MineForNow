Database initialization scripts for PostgreSQL

Este diretório contém os scripts SQL usados para inicializar o banco de dados PostgreSQL do projeto.

- Execução automática: os scripts em `db/init` são executados automaticamente pelo entrypoint do container PostgreSQL na PRIMEIRA inicialização do volume (quando o container é criado via `docker-compose up`).
- Script principal: `001_schema.sql` — contém a DDL (tabelas, índices e constraints) necessária para a aplicação.

Observações importantes
- A aplicação também usa Flyway para migrações em tempo de execução; os scripts de migração da aplicação ficam em `src/main/resources/db/migration`.
- Esses scripts em `db/init` destinam-se a preparar um banco vazio em ambientes de desenvolvimento/CI e não devem ser aplicados manualmente em bases de produção sem revisão.

Como forçar reexecução dos scripts (cuidado: apaga dados)
1. Pare o compose
```bash
docker compose down
```
2. Remova o volume que persiste os dados (o nome do volume está em `docker-compose.yml`, por padrão `pgdata`)
```bash
docker volume rm minefornow_pgdata
```
3. Suba novamente o serviço de banco
```bash
docker compose up -d db
```

Ver logs do container
```bash
docker compose logs -f db
```

Credenciais padrão (definidas em `docker-compose.yml`)
- Database: `minefornow`
- User: `minefornow`
- Password: `minefornow_pwd`

Aviso: use estes comandos com cuidado em ambientes onde existam dados importantes.
