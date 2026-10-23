# NIR-CMI-Java

Versao **Java (Spring Boot)** do projeto [NIR-CMI](https://github.com/Gbrcamilo/NIR-CMI), originalmente escrito em Node.js/Express + Vue.js.

Painel interativo em tempo real de pacientes internados na unidade CMI, com geracao de etiquetas ZPL para pulseiras hospitalares (impressoras Zebra), integrado ao Oracle DB.

## Por que uma versao Java?

Este repositorio e um port funcional-equivalente do projeto original em Node.js, reescrito com o ecossistema Java/Spring, mantendo a mesma logica de negocio (consulta SQL Oracle, geracao de ZPL, atualizacao em tempo real).

## Stack

| Camada | Node.js (original) | Java (este repo) |
|---|---|---|
| Servidor HTTP | Express | Spring Boot (Spring MVC) |
| Tempo real | Socket.IO | WebSocket + STOMP (SockJS) |
| Acesso a Oracle | node-oracledb (Thin) | JDBC (ojdbc11) + HikariCP |
| Validacao | zod | Bean Validation (jakarta.validation) |
| Front-end | Vue 3 + Vite | Front-end web mantido separado (ver `docs/frontend.md`) |
| Documentacao API | - | springdoc-openapi (Swagger UI em `/docs`) |

## Estrutura do projeto

```
nir-cmi-java/
├── pom.xml
├── src/main/java/br/com/nircmi/
│   ├── NirCmiApplication.java
│   ├── config/            # DataSource Oracle, WebSocket (STOMP), CORS
│   ├── controller/        # REST: /api/cmi/internacoes, /api/zebra/wristband
│   ├── dto/                # DTOs de requisicao (pulseira/paciente/settings)
│   ├── model/              # Entidade Internacao (equivalente as linhas do SQL)
│   ├── repository/         # Query SQL de internacoes CMI (JdbcTemplate)
│   ├── service/            # Polling periodico e broadcast via WebSocket
│   └── zebra/              # Gerador de ZPL para pulseira hospitalar Zebra
└── src/main/resources/application.yml
```

## Equivalencia de arquivos (Node.js -> Java)

| Original (Node.js) | Porte (Java) |
|---|---|
| `server/index.js` | `NirCmiApplication.java` + `controller/*` + `config/WebSocketConfig.java` |
| `server/oraclePool.js` | `config/OracleDataSourceConfig.java` |
| `server/queries.js` | `repository/InternacaoRepository.java` |
| `server/zebra/wristband.js` | `zebra/WristbandLabelBuilder.java` |

## Configuracao (variaveis de ambiente)

| Variavel | Descricao | Default |
|---|---|---|
| `PORT` | Porta HTTP do servidor | `3012` |
| `REFRESH_MINUTES` | Intervalo do polling (minutos) | `1` |
| `DB_USER` | Usuario Oracle | - |
| `DB_PASSWORD` | Senha Oracle | - |
| `DB_CONNECT_STRING` | Connect string Oracle (host:porta/servico) | - |
| `DB_POOL_MIN` / `DB_POOL_MAX` | Tamanho minimo/maximo do pool | `1` / `10` |

## Como executar

```bash
# build
mvn clean package

# executar
java -jar target/nir-cmi-java-0.1.0.jar

# ou em modo dev
mvn spring-boot:run
```

Documentacao interativa da API disponivel em `http://localhost:3012/docs` (Swagger UI), gerada automaticamente pelo springdoc-openapi.

## Endpoints

- `GET /api/cmi/internacoes` — lista as internacoes ativas na unidade CMI (equivalente ao endpoint original).
- `POST /api/zebra/wristband` — recebe dados do paciente/settings e retorna o ZPL da pulseira (`text/plain`).
- `WS /ws` (SockJS/STOMP) — assinar `/topic/cmi-internacoes` para receber atualizacoes em tempo real (substitui o evento `cmi_internacoes` do Socket.IO original).

## Documentacao adicional

Veja a pasta [`docs/`](docs) para detalhes do fluxo de pulseira segura e notas de migracao Node.js -> Java.

## Projeto original

[Gbrcamilo/NIR-CMI](https://github.com/Gbrcamilo/NIR-CMI) — versao Node.js/Vue.js.
