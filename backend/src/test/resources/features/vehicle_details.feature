# language: pt
@SCRUM-12
Funcionalidade: Visualização de Detalhes do Veículo
  Como um utilizador da plataforma MineForNow
  Quero visualizar todos os detalhes de um veículo específico
  Para tomar uma decisão informada antes de efetuar uma reserva

  Contexto:
    Dado que o sistema tem veículos cadastrados
    E o veículo com ID 1 é um "Fiat 500" de "2023"
    E o proprietário do veículo é "Admin MineForNow"

  @wip
  Cenário: Visualizar detalhes completos de um veículo
    Dado que estou na página de detalhes do veículo com ID 1
    Então devo ver o nome do veículo "Fiat 500"
    E devo ver o ano "2023"
    E devo ver o preço por dia
    E devo ver o tipo de combustível "Gasolina"
    E devo ver a transmissão "Automática"
    E devo ver o número de lugares "4"
    E devo ver o número de portas "3"

  @wip
  Cenário: Visualizar características adicionais do veículo
    Dado que estou na página de detalhes do veículo com ID 1
    Então devo ver a característica "Ar Condicionado"
    E devo ver a característica "Bluetooth"

  @wip
  Cenário: Visualizar localização do veículo
    Dado que estou na página de detalhes do veículo com ID 1
    Então devo ver a cidade "Cascais"
    E devo ver o local "Estação de Comboios"

  @wip
  Cenário: Visualizar descrição do veículo
    Dado que estou na página de detalhes do veículo com ID 1
    Então devo ver uma descrição do veículo
    E a descrição deve conter "charmoso"

  @wip
  Cenário: Visualizar botão de reserva
    Dado que estou na página de detalhes do veículo com ID 1
    Então devo ver o botão "Reservar Agora"

  @wip
  Cenário: Navegar de volta para a lista de veículos
    Dado que estou na página de detalhes do veículo com ID 1
    Quando clico no botão "Voltar"
    Então devo ser redirecionado para a página de pesquisa

  @wip
  Cenário: Acessar veículo inexistente
    Dado que estou na página de detalhes do veículo com ID 99999
    Então devo ver uma mensagem de erro ou ser redirecionado

  @wip
  Cenário: Verificar formatação do preço
    Dado que estou na página de detalhes do veículo com ID 1
    Então o preço deve estar no formato correto com "€/dia"

  @wip
  Cenário: Visualizar galeria de imagens
    Dado que estou na página de detalhes do veículo com ID 1
    Então devo ver pelo menos uma imagem do veículo

  @wip
  Cenário: Visualizar mapa com localização
    Dado que estou na página de detalhes do veículo com ID 1
    Então devo ver um mapa integrado com a localização

  @wip
  Cenário: Visualizar classificação do veículo
    Dado que estou na página de detalhes do veículo com ID 1
    Então devo ver a classificação por estrelas se disponível
