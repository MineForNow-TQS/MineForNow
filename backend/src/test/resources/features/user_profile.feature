# language: pt
@SCRUM-46
Funcionalidade: Perfil do Utilizador
  Como um utilizador autenticado da plataforma MineForNow
  Quero visualizar e editar o meu perfil
  Para manter os meus dados atualizados

  Cenário: Criar conta e atualizar informações do perfil
    Dado que não tenho conta no sistema
    Quando me registo com o nome "Joao" e email "joao@gmail.com"
    E acedo ao painel de definições
    E preencho o telefone com "+ 351 924424439"
    E preencho a carta de condução com "AA123456"
    E clico em "Guardar Alterações"
    Então devo ver a mensagem de sucesso "Alterações guardadas com sucesso!"

  Cenário: Aceder ao perfil após login
    Dado que existe um utilizador "joao@gmail.com" no sistema
    Quando faço login com "joao@gmail.com" e "Aa123456"
    E acedo ao painel de definições
    Então devo ver o título "Informação Pessoal"
    E devo ver os campos de telefone e carta de condução

  Cenário: Atualizar apenas o telefone
    Dado que estou autenticado como "joao@gmail.com"
    E estou na página de definições
    Quando preencho o telefone com "912345678"
    E clico em "Guardar Alterações"
    Então devo ver a mensagem de sucesso "Alterações guardadas com sucesso!"

  Cenário: Atualizar apenas a carta de condução
    Dado que estou autenticado como "joao@gmail.com"
    E estou na página de definições
    Quando preencho a carta de condução com "BB654321"
    E clico em "Guardar Alterações"
    Então devo ver a mensagem de sucesso "Alterações guardadas com sucesso!"

  Cenário: Ver opção de terminar sessão
    Dado que estou autenticado como "joao@gmail.com"
    E estou na página de definições
    Então devo ver o texto "Terminar Sessão"
    E devo ver o botão de sair "Sair"
