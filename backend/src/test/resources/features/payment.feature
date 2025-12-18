# language: pt
Funcionalidade: Simulação de Pagamento e Confirmação
  Como Renter
  Quero simular o pagamento da minha reserva
  Para confirmar definitivamente a reserva

  Contexto:
    Dado que existe um veículo disponível com ID 1
    E que existe uma reserva com ID 1 no estado "WAITING_PAYMENT"

  @SCRUM-16
  Cenário: Confirmar pagamento com sucesso
    Dado que sou um utilizador do tipo "Renter" autenticado
    E estou na página de pagamento da reserva com ID 1
    Quando preencho o campo "Últimos 4 Dígitos do Cartão" com "1234"
    E preencho o campo "Nome do Titular" com "Maria Silva"
    E preencho o campo "Validade (MM/YY)" com "12/25"
    E preencho o campo "CVV" com "123"
    E clico no botão "Confirmar Pagamento"
    Então devo ver a mensagem "Pagamento Confirmado!"
    E a reserva deve ter o estado "CONFIRMED"

  @SCRUM-16
  Cenário: Tentar confirmar pagamento de reserva já confirmada
    Dado que sou um utilizador do tipo "Renter" autenticado
    E que existe uma reserva com ID 2 no estado "CONFIRMED"
    E estou na página de pagamento da reserva com ID 2
    Quando preencho o campo "Últimos 4 Dígitos do Cartão" com "1234"
    E preencho o campo "Nome do Titular" com "Maria Silva"
    E preencho o campo "Validade (MM/YY)" com "12/25"
    E preencho o campo "CVV" com "123"
    E clico no botão "Confirmar Pagamento"
    Então devo ver uma mensagem de erro contendo "not waiting for payment"

  @SCRUM-16
  Cenário: Validação de formulário - campos obrigatórios
    Dado que sou um utilizador do tipo "Renter" autenticado
    E estou na página de pagamento da reserva com ID 1
    Quando clico no botão "Confirmar Pagamento"
    Então devo ver mensagens de erro de validação nos campos obrigatórios
