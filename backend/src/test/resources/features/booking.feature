# language: pt
@SCRUM-15
Funcionalidade: Intenção de Reserva (Booking Intent)
  Como um utilizador "Renter"
  Quero iniciar o processo de reserva de um veículo
  Para que possa alugar o carro nas datas desejadas

  Contexto:
    Dado que sou um utilizador do tipo "Renter" autenticado
    E existe um veículo disponível para aluguer com ID 1

  @wip
  Cenário: Iniciar uma reserva com sucesso
    Dado que estou a visualizar os detalhes do veículo com ID 1
    Quando seleciono a data de levantamento para "2025-12-20"
    E seleciono a data de devolução para "2025-12-25"
    E clico no botão "Reservar Agora"
    Então devo ser redirecionado para a página de checkout
    E devo ver o preço total calculado para 5 dias
    Quando clico no botão "Confirmar e Pagar"
    Então devo ver uma mensagem de sucesso "Reserva Iniciada com sucesso!"
    E devo ser redirecionado para o dashboard
