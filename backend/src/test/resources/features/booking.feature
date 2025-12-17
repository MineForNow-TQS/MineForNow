# language: pt
@SCRUM-15
Funcionalidade: Intenção de Reserva (Booking Intent)
  Como um utilizador "Renter"
  Quero iniciar o processo de reserva de um veículo
  Para que possa alugar o carro nas datas desejadas

  Contexto:
    Dado que existe um veículo disponível para aluguer com ID 1

  Cenário: Tentar reservar sem estar autenticado
    Dado que sou um utilizador não autenticado
    E estou a visualizar os detalhes do veículo com ID 1
    Quando preencho as datas de reserva "2025-12-19" a "2025-12-23"
    E aciono o botão "Reservar Agora"
    E aciono o botão "Confirmar e Pagar"
    Então devo ver uma mensagem de erro "Login necessário"
    E devo ser redirecionado para a página de login

  @wip
  Cenário: Iniciar uma reserva com sucesso
    Dado que sou um utilizador do tipo "Renter" autenticado
    E estou a visualizar os detalhes do veículo com ID 1
    Quando seleciono a data de levantamento para "2025-12-20"
    E seleciono a data de devolução para "2025-12-25"
    E clico no botão "Reservar Agora"
    Então devo ser redirecionado para a página de checkout
    E devo ver o preço total calculado para 5 dias
    Quando clico no botão "Confirmar e Pagar"
    Então devo ser redirecionado para a página de pagamento
    E devo ver os detalhes da reserva para pagamento
