# Feature Map

## Objetivo do sistema

Sistema de agendamento de serviços para uma barbearia.

## Features

### Feature 001 — Cadastro de Serviços
Permitir cadastrar e gerenciar os serviços oferecidos pela barbearia.

### Feature 002 — Cadastro de Profissionais
Permitir cadastrar e gerenciar os profissionais que realizam os serviços.

### Feature 003 — Disponibilidade dos Profissionais
Permitir definir e consultar os horários em que cada profissional pode atender.

### Feature 004 — Consulta de Horários Disponíveis
Calcular os horários disponíveis para determinado serviço e profissional.

### Feature 005 — Criação de Agendamento
Permitir que um cliente reserve um horário.

### Feature 006 — Cancelamento de Agendamento
Permitir cancelar um agendamento existente.

### Feature 007 — Notificações de Agendamento
Publicar eventos de criação e cancelamento no RabbitMQ.

### Feature 008 — Envio de E-mails
Consumir eventos do RabbitMQ e enviar e-mails de confirmação/cancelamento.

### Feature 009 — Lembretes
Executar job diário para enviar lembretes dos agendamentos do dia seguinte.

### Feature 010 — Observabilidade
Adicionar métricas, logs, health checks e dashboards.