# Feature Map

## Objetivo do sistema

Sistema de agendamento de serviços para uma barbearia.

## Features

### Feature 001 — Cadastro de Serviços [x]
Permitir cadastrar e gerenciar os serviços oferecidos pela barbearia.

### Feature 002 — Ajustes de Infra [x]

### Feature 003 — Cadastro de Profissionais [x]
Permitir cadastrar e gerenciar os profissionais que realizam os serviços.

### Feature 004 — Disponibilidade dos Profissionais [x]
Permitir definir e consultar os horários em que cada profissional pode atender.

### Feature 005 — Associação de serviços a Profissionais [...]
Cria a associação entre profissionais e serviços.

### Feature 006 — Consulta de Horários Disponíveis []
Calcular os horários disponíveis para determinado serviço e profissional.

### Feature 007 — Criação de Agendamento []
Permitir que um cliente reserve um horário.

### Feature 008 — Cancelamento de Agendamento []
Permitir cancelar um agendamento existente.

### Feature 009 — Notificações de Agendamento []
Publicar eventos de criação e cancelamento no RabbitMQ.

### Feature 010 — Envio de E-mails []
Consumir eventos do RabbitMQ e enviar e-mails de confirmação/cancelamento.

### Feature 011 — Lembretes []
Executar job diário para enviar lembretes dos agendamentos do dia seguinte.

### Feature 012 — Observabilidade []
Adicionar métricas, logs, health checks e dashboards.

-----

001 Serviços
↓
002 Profissionais
↓
003 Disponibilidade
↓
004 Horários disponíveis
↓
005 Agendamento
↓
006 Cancelamento
↓
007 Eventos/RabbitMQ
↓
008 E-mails
↓
009 Jobs de lembrete