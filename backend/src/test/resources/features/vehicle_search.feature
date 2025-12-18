# language: pt
@SCRUM-49
Funcionalidade: Pesquisa de Veículos por Disponibilidade
  Como um utilizador da plataforma MineForNow
  Quero pesquisar veículos por cidade e datas
  Para encontrar carros disponíveis para alugar

  Contexto:
    Dado que o sistema tem 6 veículos cadastrados
    E o veículo "Mercedes-Benz AMG GT" em "Lisboa" está reservado de "2026-01-16" até "2026-01-21"

  @smoke
  Cenário: Pesquisar veículos por cidade
    Dado que estou na página de pesquisa
    Quando pesquiso por veículos em "Lisboa"
    Então devo ver 2 veículos na lista
    E devo ver o veículo "Mercedes-Benz AMG GT"
    E devo ver o veículo "Ferrari Roma"

  Cenário: Pesquisar veículos por cidade diferente
    Dado que estou na página de pesquisa
    Quando pesquiso por veículos em "Porto"
    Então devo ver 1 veículo na lista
    E devo ver o veículo "Mercedes-Benz AMG GT R"

  Cenário: Pesquisar veículos disponíveis em datas específicas
    Dado que estou na página de pesquisa
    Quando pesquiso por veículos em "Lisboa"
    E seleciono a data de levantamento "2026-01-16"
    E seleciono a data de devolução "2026-01-21"
    Então devo ver 1 veículo na lista
    E devo ver o veículo "Ferrari Roma"
    E não devo ver o veículo "Mercedes-Benz AMG GT"

  Cenário: Pesquisar veículos fora do período de reserva
    Dado que estou na página de pesquisa
    Quando pesquiso por veículos em "Lisboa"
    E seleciono a data de levantamento "2026-01-23"
    E seleciono a data de devolução "2026-01-26"
    Então devo ver 2 veículos na lista
    E devo ver o veículo "Mercedes-Benz AMG GT"
    E devo ver o veículo "Ferrari Roma"

  Cenário: Pesquisar em cidade sem veículos
    Dado que estou na página de pesquisa
    Quando pesquiso por veículos em "Braga"
    Então devo ver 0 veículos na lista
    E devo ver a mensagem "0 carros encontrados"

  Cenário: Pesquisar apenas por datas com veículos reservados
    Dado que estou na página de pesquisa
    Quando seleciono a data de levantamento "2026-01-16"
    E seleciono a data de devolução "2026-01-21"
    Então devo ver 5 veículos na lista
    E não devo ver o veículo "Mercedes-Benz AMG GT"

  Cenário: Pesquisar apenas por datas sem conflitos de reserva
    Dado que estou na página de pesquisa
    Quando seleciono a data de levantamento "2026-01-25"
    E seleciono a data de devolução "2026-01-28"
    Então devo ver 6 veículos na lista
