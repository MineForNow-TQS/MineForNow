@SCRUM-30
Feature: Visualização de Classificações e Comentários
  Como Renter
  Quero visualizar a classificação média e os comentários de um veículo
  Para tomar uma decisão informada antes de reservar

  Scenario: Ver reviews de um veículo com avaliações
    Given existe um veículo com id 4
    When acedo ao endpoint "/api/vehicles/4/reviews"
    Then devo receber status 200
    And a média de rating deve ser aproximadamente 4.67
    And devem existir 3 reviews na resposta
    And a primeira review deve ter rating 5

  Scenario: Ver reviews de um veículo sem avaliações
    Given existe um veículo com id 1
    When acedo ao endpoint "/api/vehicles/1/reviews"
    Then devo receber status 200
    And a média de rating deve ser 0.0
    And devem existir 0 reviews na resposta

  Scenario: Tentar ver reviews de um veículo inexistente
    When acedo ao endpoint "/api/vehicles/999/reviews"
    Then devo receber status 404
