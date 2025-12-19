# language: pt
@SCRUM-24
Funcionalidade: Dashboard de Gestão do Proprietário
  Como Owner
  Quero visualizar métricas do meu negócio
  Para acompanhar o desempenho dos meus alugueres
  
  Cenário: Owner visualiza dashboard com métricas corretas
    Dado que existe um owner autenticado com email "owner@minefornow.com"
    E que o owner tem 3 veículos cadastrados
    E que existem 3 reservas:
      | pickup     | return     | status          | valor |
      | 2025-12-22 | 2025-12-25 | CONFIRMED       | 850   |
      | 2025-12-26 | 2025-12-27 | CONFIRMED       | 1700  |
      | 2025-12-28 | 2025-12-30 | WAITING_PAYMENT | 1100  |
    Quando acedo ao endpoint "/api/dashboard/owner"
    Então devo receber status 200
    E o total de ganhos deve ser "2550.0"
    E o número de veículos ativos deve ser "3"
    E o número de reservas pendentes deve ser "1"
    E o número de reservas pagas deve ser "2"
