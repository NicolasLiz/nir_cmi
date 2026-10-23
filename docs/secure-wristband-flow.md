Escopo do Sistema de Gestão de Leitos da Maternidade

1. Visão geral

O Sistema de Gestão de Leitos da Maternidade terá como objetivo centralizar e facilitar o controle dos leitos disponíveis, ocupados, reservados, bloqueados e em manutenção, permitindo que a equipe acompanhe a situação da maternidade de forma rápida e organizada.

O sistema deverá registrar as movimentações das pacientes, permitindo acompanhar processos de internação, alocação, transferência e alta, mantendo histórico das operações realizadas.

⸻

2. Objetivo geral

Desenvolver um sistema informatizado para gerenciamento dos leitos da maternidade, proporcionando maior controle sobre a ocupação hospitalar, disponibilidade de leitos e movimentação das pacientes.

⸻

3. Objetivos específicos

* Controlar os leitos existentes na maternidade.
* Visualizar a disponibilidade dos leitos em tempo real.
* Registrar internações.
* Associar pacientes aos respectivos leitos.
* Registrar transferências entre leitos e setores.
* Registrar altas hospitalares.
* Controlar leitos bloqueados ou indisponíveis.
* Manter histórico das movimentações.
* Permitir diferentes níveis de acesso aos usuários.
* Gerar relatórios gerenciais.
* Disponibilizar indicadores de ocupação.
* Reduzir controles manuais e informações descentralizadas.

⸻

4. Usuários do sistema

4.1 Administrador

Responsável pela administração geral do sistema.

Permissões:

* Cadastrar usuários.
* Alterar usuários.
* Inativar usuários.
* Definir perfis de acesso.
* Cadastrar setores.
* Cadastrar quartos.
* Cadastrar leitos.
* Configurar parâmetros do sistema.
* Consultar registros de auditoria.

4.2 Gestão/Coordenação

Responsável pelo acompanhamento da ocupação e dos indicadores.

Permissões:

* Visualizar todos os setores.
* Consultar disponibilidade.
* Consultar ocupação.
* Consultar pacientes internadas.
* Consultar movimentações.
* Gerar relatórios.
* Visualizar indicadores.

4.3 Recepção/Internação

Responsável pelo registro das pacientes e internações.

Permissões:

* Cadastrar pacientes.
* Localizar pacientes.
* Registrar internações.
* Solicitar/alocar leitos disponíveis.
* Consultar situação dos leitos.
* Registrar alta conforme permissão definida.

4.4 Enfermagem

Responsável pelo acompanhamento da ocupação e movimentação dos leitos.

Permissões:

* Consultar leitos.
* Visualizar pacientes vinculadas aos leitos.
* Solicitar transferência.
* Registrar movimentações permitidas.
* Atualizar situação operacional do leito.

4.5 Médico

Permissões de consulta, conforme definido pela instituição:

* Consultar pacientes.
* Consultar setor e leito.
* Consultar informações relacionadas à internação.
* Consultar movimentações.

⸻

5. Estrutura física

O sistema deverá representar a estrutura física da maternidade.

A hierarquia será:

Unidade → Setor → Quarto → Leito

Exemplo:

Maternidade

* Setor: Pré-parto
    * Quarto 01
        * Leito 01
        * Leito 02
    * Quarto 02
        * Leito 03
        * Leito 04

⸻

6. Cadastro de leitos

Cada leito deverá possuir, no mínimo:

* Código/identificação;
* Número do leito;
* Setor;
* Quarto;
* Tipo de leito;
* Status;
* Observações;
* Data de cadastro;
* Situação de funcionamento.

Status possíveis

* Disponível;
* Ocupado;
* Reservado;
* Em higienização;
* Em manutenção;
* Bloqueado/interditado.

O sistema deverá registrar o histórico das alterações de status.

⸻

7. Cadastro de pacientes

O sistema deverá permitir o cadastro e identificação das pacientes.

Dados sugeridos:

* Nome completo;
* Data de nascimento;
* CPF/documento, quando aplicável;
* Número do prontuário;
* Dados de contato, quando necessários;
* Informações administrativas da internação.

Os dados clínicos detalhados deverão permanecer no sistema assistencial/prontuário apropriado, caso exista integração.

⸻

8. Internação

O processo de internação deverá permitir:

1. Localizar ou cadastrar a paciente.
2. Registrar a internação.
3. Identificar o setor de destino.
4. Consultar os leitos disponíveis.
5. Selecionar o leito.
6. Associar a paciente ao leito.
7. Alterar o status do leito para “Ocupado”.
8. Registrar data e hora da ocupação.
9. Registrar o usuário responsável pela operação.

Fluxo

Paciente → Internação → Seleção do leito → Ocupação → Acompanhamento

⸻

9. Transferência

O sistema deverá permitir a transferência da paciente entre leitos e/ou setores.

Exemplo:

Leito 203 → Leito 105

O sistema deverá registrar:

* Paciente;
* Leito de origem;
* Leito de destino;
* Data e hora;
* Usuário responsável;
* Motivo da transferência, quando aplicável.

Após a transferência:

* O leito de origem deverá ficar disponível ou assumir outro status definido;
* O leito de destino deverá ficar ocupado;
* A movimentação deverá ser registrada no histórico.

⸻

10. Alta

Ao realizar uma alta, o sistema deverá:

1. Localizar a paciente.
2. Registrar a alta.
3. Registrar data e hora.
4. Liberar o leito.
5. Alterar o status do leito.
6. Registrar o usuário responsável.

Fluxo:

Paciente internada → Alta → Liberação do leito → Higienização → Disponível

A etapa de higienização poderá ser obrigatória antes que o leito volte a ser disponibilizado, conforme a regra definida pela maternidade.

⸻

11. Bloqueio e manutenção de leitos

O sistema deverá permitir bloquear um leito quando ele não puder ser utilizado.

Motivos possíveis:

* Manutenção;
* Problema estrutural;
* Higienização;
* Interdição;
* Reserva operacional;
* Outros motivos configurados.

O sistema deverá registrar:

* Leito;
* Motivo;
* Data/hora de início;
* Usuário responsável;
* Previsão de liberação, quando aplicável;
* Data/hora de liberação.

⸻

12. Painel de gestão de leitos

O sistema deverá possuir um painel visual para acompanhamento da situação dos leitos.

Exemplo:

Maternidade

Total: 40 leitos
Ocupados: 30
Disponíveis: 6
Reservados: 2
Indisponíveis: 2

O usuário poderá visualizar os leitos por setor e quarto.

Exemplo:

Quarto 01

* Leito 01 — 🟢 Disponível
* Leito 02 — 🔴 Ocupado

Quarto 02

* Leito 03 — 🔴 Ocupado
* Leito 04 — 🟡 Reservado

⸻

13. Indicadores

O sistema deverá apresentar indicadores para auxiliar a gestão.

Indicadores sugeridos:

* Total de leitos;
* Total de leitos ocupados;
* Total de leitos disponíveis;
* Total de leitos bloqueados;
* Taxa de ocupação;
* Quantidade de internações;
* Quantidade de altas;
* Quantidade de transferências;
* Tempo médio de permanência;
* Ocupação por setor.

Taxa de ocupação

A taxa de ocupação poderá ser calculada por:

Taxa de ocupação = (Leitos ocupados / Leitos disponíveis para uso) × 100

⸻

14. Relatórios

O sistema deverá permitir a geração de relatórios, conforme perfil de acesso.

Relatórios iniciais:

* Relatório de ocupação;
* Relatório de disponibilidade;
* Relatório de internações;
* Relatório de altas;
* Relatório de transferências;
* Histórico de ocupação dos leitos;
* Relatório por setor;
* Relatório de leitos bloqueados;
* Relatório de tempo de permanência.

Os relatórios poderão possuir filtros por:

* Período;
* Setor;
* Leito;
* Status;
* Paciente;
* Tipo de movimentação.

⸻

15. Histórico e auditoria

Todas as operações relevantes deverão possuir registro de auditoria.

Exemplo:

Data: 24/08/2026
Hora: 14:32
Usuário: Usuário X
Leito: 203
Ação: Alteração de status
Anterior: Ocupado
Novo: Disponível

O histórico não deverá ser apagado pelo usuário comum.

⸻

16. Requisitos funcionais

RF01 — Autenticação

O sistema deverá permitir que usuários autorizados acessem o sistema por meio de login e senha.

RF02 — Controle de usuários

O administrador deverá poder cadastrar, alterar, bloquear e inativar usuários.

RF03 — Controle de permissões

O sistema deverá controlar o acesso às funcionalidades conforme o perfil do usuário.

RF04 — Cadastro de setores

O sistema deverá permitir cadastrar e alterar os setores da maternidade.

RF05 — Cadastro de quartos

O sistema deverá permitir cadastrar os quartos vinculados aos setores.

RF06 — Cadastro de leitos

O sistema deverá permitir cadastrar os leitos e suas respectivas características.

RF07 — Consulta de disponibilidade

O sistema deverá permitir consultar os leitos disponíveis.

RF08 — Cadastro de paciente

O sistema deverá permitir cadastrar e localizar pacientes.

RF09 — Registro de internação

O sistema deverá permitir registrar a internação e vincular a paciente a um leito.

RF10 — Transferência

O sistema deverá permitir transferir uma paciente para outro leito.

RF11 — Alta

O sistema deverá permitir registrar a alta e iniciar o processo de liberação do leito.

RF12 — Bloqueio de leito

O sistema deverá permitir bloquear um leito e informar o motivo.

RF13 — Histórico

O sistema deverá armazenar o histórico de movimentações.

RF14 — Painel

O sistema deverá apresentar um painel com a situação atual dos leitos.

RF15 — Relatórios

O sistema deverá permitir gerar relatórios de gestão.

RF16 — Indicadores

O sistema deverá apresentar indicadores relacionados à ocupação dos leitos.

RF17 — Auditoria

O sistema deverá registrar operações relevantes realizadas pelos usuários.

⸻

17. Requisitos não funcionais

RNF01 — Segurança

O sistema deverá possuir autenticação e controle de acesso.

RNF02 — Disponibilidade

O sistema deverá estar disponível para os usuários autorizados durante o funcionamento da maternidade.

RNF03 — Desempenho

As principais telas de consulta deverão apresentar os dados em tempo adequado para utilização operacional.

RNF04 — Backup

Deverá existir uma política de backup dos dados.

RNF05 — Rastreabilidade

As operações relevantes deverão possuir identificação do usuário, data e hora.

RNF06 — Usabilidade

A interface deverá ser simples e permitir que a situação dos leitos seja identificada rapidamente.

RNF07 — Privacidade

O sistema deverá aplicar controles para proteger informações pessoais e sensíveis, observando as regras internas da instituição e a legislação aplicável.

⸻

18. Regras de negócio

RN01

Um leito ocupado não poderá ser atribuído simultaneamente a outra paciente.

RN02

Uma paciente não poderá possuir duas internações ativas no mesmo contexto assistencial, salvo situações expressamente previstas pelo sistema.

RN03

Um leito bloqueado não poderá ser utilizado para uma nova internação.

RN04

Um leito em manutenção não deverá aparecer como disponível para ocupação.

RN05

Uma transferência deverá possuir leito de origem e leito de destino.

RN06

O leito de destino deverá estar disponível ou em condição previamente autorizada para ocupação.

RN07

Toda alteração de status deverá ser registrada no histórico.

RN08

A liberação definitiva do leito poderá depender da conclusão da etapa de higienização.

RN09

Somente usuários com permissão poderão realizar alterações na situação dos leitos.

RN10

O sistema deverá impedir conflitos de ocupação.

⸻

19. Sugestão de banco de dados

Uma estrutura inicial poderia conter as seguintes tabelas:

USUARIO

* id_usuario
* nome
* login
* senha_hash
* perfil
* ativo
* data_cadastro

SETOR

* id_setor
* nome
* descrição
* ativo

QUARTO

* id_quarto
* id_setor
* identificação
* tipo
* ativo

LEITO

* id_leito
* id_quarto
* identificação
* tipo
* status
* observação
* ativo

PACIENTE

* id_paciente
* nome
* data_nascimento
* documento
* prontuario
* data_cadastro

INTERNACAO

* id_internacao
* id_paciente
* data_entrada
* data_alta
* status
* observação

OCUPACAO_LEITO

* id_ocupacao
* id_internacao
* id_leito
* data_entrada
* data_saida
* ativo

TRANSFERENCIA

* id_transferencia
* id_internacao
* leito_origem
* leito_destino
* data_hora
* motivo
* id_usuario

BLOQUEIO_LEITO

* id_bloqueio
* id_leito
* motivo
* data_inicio
* data_fim
* id_usuario

AUDITORIA

* id_auditoria
* id_usuario
* data_hora
* ação
* tabela
* registro
* valor_anterior
* valor_novo

⸻

20. Fluxo principal do sistema

PACIENTE
   ↓
INTERNAÇÃO
   ↓
CONSULTA DE LEITOS
   ↓
LEITO DISPONÍVEL?
   ↓
SIM
   ↓
ALOCAÇÃO
   ↓
LEITO OCUPADO
   ↓
┌───────────────┬───────────────┐
↓               ↓               ↓
TRANSFERÊNCIA   ALTA            OUTRA AÇÃO
↓               ↓
NOVO LEITO      LIBERAÇÃO
                ↓
          HIGIENIZAÇÃO
                ↓
           DISPONÍVEL

⸻

21. Escopo do MVP

A primeira versão do sistema deverá contemplar:

* Login e autenticação.
* Controle de usuários.
* Perfis e permissões.
* Cadastro de setores.
* Cadastro de quartos.
* Cadastro de leitos.
* Cadastro/localização de pacientes.
* Registro de internação.
* Alocação de leitos.
* Transferência.
* Alta.
* Bloqueio de leitos.
* Painel de ocupação.
* Histórico de movimentações.
* Relatório de ocupação.
* Auditoria.

⸻

22. Funcionalidades futuras

Após a primeira versão, poderão ser implementadas:

* Integração com prontuário eletrônico;
* Integração com sistema de internação;
* Notificações automáticas;
* Painel de TV para acompanhamento dos leitos;
* Aplicativo móvel;
* Dashboard gerencial avançado;
* Previsão de disponibilidade de leitos;
* Integração com sistemas externos por API;
* Indicadores avançados;
* Automatização do processo de higienização.

⸻

23. Fora do escopo inicial

Para evitar que o projeto cresça excessivamente, a primeira versão não deverá contemplar:

* Prontuário médico completo;
* Prescrição médica;
* Controle completo de medicamentos;
* Gestão financeira;
* Faturamento hospitalar;
* Controle completo de estoque;
* Diagnóstico médico;
* Substituição de sistemas assistenciais já existentes.

Essas funcionalidades poderão ser avaliadas para futuras versões ou integrações.

⸻

24. Critérios de aceite

O sistema será considerado apto para a primeira versão quando:

1. Um usuário autorizado conseguir acessar o sistema.
2. O administrador conseguir cadastrar setores, quartos e leitos.
3. O sistema apresentar corretamente a situação dos leitos.
4. Uma paciente puder ser vinculada a um leito disponível.
5. O sistema impedir a ocupação simultânea do mesmo leito.
6. Uma paciente puder ser transferida para outro leito.
7. Uma alta puder liberar o leito.
8. Leitos bloqueados não puderem ser utilizados.
9. Todas as movimentações relevantes forem registradas.
10. A gestão conseguir consultar os principais indicadores de ocupação.
11. Os relatórios apresentarem informações consistentes com os registros do sistema.
12. O acesso às informações respeitar as permissões de cada usuário.

⸻

25. Resultado esperado

Ao final do projeto, a maternidade deverá possuir uma ferramenta centralizada para acompanhamento dos leitos, permitindo identificar rapidamente:

Quantos leitos existem → Quantos estão ocupados → Quantos estão disponíveis → Onde estão as pacientes → Quais leitos estão bloqueados → Quais movimentações ocorreram.

O sistema deverá proporcionar maior organização, rastreabilidade e agilidade na gestão dos leitos, servindo como ferramenta de apoio à tomada de decisão da equipe responsável pela gestão da maternidade.


# Fluxo de pulseira segura (porte do original)

Este documento reproduz e adapta a logica descrita em `docs/secure-wristband-flow.md` do projeto original [NIR-CMI](https://github.com/Gbrcamilo/NIR-CMI), agora referente a implementacao Java.

## Fluxo

1. O front-end (painel Vue, mantido como estava ou a ser portado) solicita a geracao da pulseira via `POST /api/zebra/wristband`, enviando os dados do paciente e as configuracoes de layout (`WristbandSettings`).
2. O backend Java (`WristbandController` -> `WristbandLabelBuilder`) monta a string ZPL a partir dos dados recebidos, sanitizando caracteres especiais (`^`, `~`) que quebrariam o protocolo ZPL.
3. A resposta (`text/plain`) contendo o ZPL e enviada ao cliente, que e responsavel por envia-la a impressora Zebra (via spooler local, agente de impressao ou biblioteca de comunicacao com a impressora).
4. Nenhuma integracao direta com a impressora fisica e feita pelo backend — a impressao permanece responsabilidade da camada cliente, assim como no projeto original.

## Diferencas em relacao ao original

- A geracao de ZPL foi portada 1:1 de `server/zebra/wristband.js` para `zebra/WristbandLabelBuilder.java`, preservando formulas de conversao mm->dots e a estrutura das tags ZPL (`^XA`, `^FO`, `^A`, `^BY`, `^BC`, `^XZ`).
- Validacao de entrada pode ser reforcada com Bean Validation (`jakarta.validation`) nos DTOs, algo que o projeto Node.js fazia com `zod`.

## arquivos

config/CorsConfig.java? configura o CORS pra permitir GET e POST em "api/*"
config/SqliteDataSourceConfig.java: configura a conexão com o banco sqlite
config/WebSocketConfig.java: configura a web socket

controller/InteracaoController.java: api que retorna dados de pacientes
controller/WristbandController.java: api que imprime a pulseira

dto/PacienteWritsbandRequest.java: informaçoes do paciente(não sei)
dto/WristbandSettings.java: Configuração padrao da etiqueta

model/Internacao.java: modelo de dados do DB

repository/InternacaoRepository.java: query de db

service/InternacaoPollingService.java: polling periodico do banco de dados e manda pra websocket

zebra/WristbandLabelBuilder.java: cria o texto pra imprimir na etiqueta

NirCmiApplication.java: entypoint
