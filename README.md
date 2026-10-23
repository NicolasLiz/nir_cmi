# NIR-CMI-Java

Painel interativo em tempo real de pacientes internados na unidade CMI.

### ignorar por enquanto
<!-- ## Configuracao (variaveis de ambiente) -->
<!---->
<!-- | Variavel | Descricao | Default | -->
<!-- |---|---|---| -->
<!-- | `PORT` | Porta HTTP do servidor | `3012` | -->
<!-- | `REFRESH_MINUTES` | Intervalo do polling (minutos) | `1` | -->
<!-- | `DB_USER` | Usuario Oracle | - | -->
<!-- | `DB_PASSWORD` | Senha Oracle | - | -->
<!-- | `DB_CONNECT_STRING` | Connect string Oracle (host:porta/servico) | - | -->
<!-- | `DB_POOL_MIN` / `DB_POOL_MAX` | Tamanho minimo/maximo do pool | `1` / `10` | -->

## Como executar

```bash
# build
./mvnw clean package

# executar
java -jar target/nir-cmi-java-0.1.0.jar

# ou em modo dev
./mvnw spring-boot:run
```

Documentacao interativa da API disponivel em `http://localhost:3012/docs` (Swagger UI), gerada automaticamente pelo springdoc-openapi.

## Documentacao adicional

Veja a pasta [`docs/`](docs)
