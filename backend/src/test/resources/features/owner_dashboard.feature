# language: pt
@SCRUM-24
Funcionalidade: Dashboard de Gestão do Proprietário
  Como Owner
  Quero visualizar métricas do meu negócio
  Para acompanhar o desempenho dos meus alugueres

  Contexto:
    Dado que existe um utilizador owner com email "owner@dashboard.com" e password "password123"
    E que o owner está autenticado

  Cenário: Owner visualiza dashboard com veículos e reservas
    Dado que o owner tem 2 veículos cadastrados
    E que existem 3 reservas confirmadas com valor total de 1500 euros
    E que existem 2 reservas pendentes
    Quando acedo ao dashboard do owner
    Então devo ver o total de ganhos de "1500.00 €"
    E devo ver "2" veículos ativos
    E devo ver "2" reservas pendentes
    E devo ver "3" reservas completadas

  Cenário: Owner visualiza dashboard sem veículos
    Dado que o owner não tem veículos cadastrados
    Quando acedo ao dashboard do owner
    Então devo ver o total de ganhos de "0.00 €"
    E devo ver "0" veículos ativos
    E devo ver "0" reservas pendentes
    E devo ver "0" reservas completadas
    E devo ver a mensagem "Nenhum carro adicionado"

  Cenário: Owner visualiza dashboard com veículos mas sem reservas
    Dado que o owner tem 3 veículos cadastrados
    E que não existem reservas
    Quando acedo ao dashboard do owner
    Então devo ver o total de ganhos de "0.00 €"
    E devo ver "3" veículos ativos
    E devo ver "0" reservas pendentes
    E devo ver "0" reservas completadas

  Cenário: Verificar cálculo correto de métricas com múltiplas reservas
    Dado que o owner tem 1 veículo cadastrado
    E que existem as seguintes reservas:
      | status          | valor |
      | CONFIRMED       | 500   |
      | CONFIRMED       | 700   |
      | WAITING_PAYMENT | 300   |
      | WAITING_PAYMENT | 200   |
      | CANCELLED       | 400   |
    Quando acedo ao dashboard do owner
    Então devo ver o total de ganhos de "1200.00 €"
    E devo ver "1" veículos ativos
    E devo ver "2" reservas pendentes
    E devo ver "2" reservas completadas
