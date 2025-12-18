# language: pt
@SCRUM-32
Funcionalidade: Autenticação de Utilizador
  Como utilizador
  Quero poder registar-me, entrar e sair
  Para que possa aceder à aplicação de forma segura

  Cenário: Registo com sucesso
    Dado que tenho um pedido de registo válido
    Quando submeto o pedido de registo
    Então o estado da resposta deve ser 200
    E devo conseguir aceder ao perfil de "joaosilva"

  Cenário: Registo sem sucesso com passwords diferentes
    Dado que tenho um pedido de registo com passwords diferentes
    Quando submeto o pedido de registo
    Então o estado da resposta deve ser 400
    E a resposta deve conter "As senhas não coincidem"

  Cenário: Login com sucesso
    Dado que tenho credenciais válidas
    Quando submeto o pedido de login
    Então o estado da resposta deve ser 200
    E devo conseguir aceder ao perfil de "joaosilva"

  Cenário: Login sem sucesso
    Dado que tenho credenciais inválidas
    Quando submeto o pedido de login
    Então o estado da resposta deve ser 401

  Cenário: Logout com sucesso
    Dado que estou autenticado
    Quando faço logout
    Então não devo conseguir aceder ao perfil de "joaosilva"
