# language: pt
@SCRUM-10
Funcionalidade: Criação de Veículos
  Como um proprietário registado na plataforma MineForNow
  Quero adicionar os meus veículos à plataforma
  Para poder alugá-los a outros utilizadores

  Contexto:
    Dado que existe um utilizador owner registado com email "owner@minefornow.com" e password "password123"

  Cenário: Criar um veículo com todos os campos preenchidos
    Dado que estou autenticado como owner com email "owner@minefornow.com" e password "password123"
    Quando navego para a página de adicionar carro
    E preencho os dados do veículo:
      | brand        | Carro         |
      | model        | Veloz         |
      | year         | 2000          |
      | mileage      | 2000          |
      | type         | desportivo    |
      | licensePlate | BB-22-BB      |
      | fuelType     | diesel        |
      | transmission | manual        |
      | seats        | 6             |
      | doors        | 4             |
      | hasAC        | true          |
      | hasGPS       | true          |
      | city         | Porto         |
      | exactLocation| Porto         |
      | pricePerDay  | 35            |
      | description  | Muito rápido  |
    E submeto o formulário de criação de veículo
    Então devo ver o veículo "Carro Veloz" na lista de veículos

  Cenário: Criar veículo sem preencher campos obrigatórios
    Dado que estou autenticado como owner com email "owner@minefornow.com" e password "password123"
    Quando navego para a página de adicionar carro
    E clico no botão "Adicionar Carro" sem preencher os dados
    Então devo ver uma mensagem de erro de validação

  Cenário: Renter não pode criar veículos
    Dado que existe um utilizador renter registado com email "renter@minefornow.com" e password "password123"
    E que estou autenticado como renter com email "renter@minefornow.com" e password "password123"
    Então não devo ver o botão "Adicionar Carro"
