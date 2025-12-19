@SCRUM-30
Funcionalidade: Visualização de Classificações e Comentários
  Como Renter
  Quero visualizar a classificação média e os comentários de um veículo
  Para tomar uma decisão informada antes de reservar

  Cenário: Ver reviews de um veículo com avaliações
    Dado que existe um veículo com id 4
    Quando acedo ao endpoint "/api/vehicles/4/reviews"
    Então devo receber status 200
    E a média de rating deve ser aproximadamente 4.67
    E devem existir 3 reviews na resposta
    E a primeira review deve ter rating 5

  Cenário: Ver reviews de um veículo sem avaliações
    Dado que existe um veículo com id 1
    Quando acedo ao endpoint "/api/vehicles/1/reviews"
    Então devo receber status 200
    E a média de rating deve ser 0.0
    E devem existir 0 reviews na resposta

  Cenário: Tentar ver reviews de um veículo inexistente
    Quando acedo ao endpoint "/api/vehicles/999/reviews"
    Então devo receber status 404
