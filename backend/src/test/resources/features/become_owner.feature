# language: pt
@SCRUM-65
Funcionalidade: Solicitação de Upgrade para Owner

  Como utilizador comum
  Quero submeter uma candidatura para me tornar Owner
  Para poder disponibilizar veículos na plataforma

  Contexto:
    Dado que eu estou autenticado como um utilizador comum

  Cenário: Submissão de candidatura com sucesso
    E que eu acedo à página "Torne-se Owner"
    Quando eu preencho o formulário de candidatura
    E eu clico no botão "Submeter Candidatura"
    Então eu devo ver a mensagem "Enviado com Sucesso!"
    E o meu estado na base de dados deve ser "PENDING_OWNER"

  Cenário: Impedir múltiplas submissões
    Dado que eu já submeti uma candidatura anteriormente
    E que eu acedo à página "Torne-se Owner"
    Então eu não devo ver o formulário de candidatura
    E eu devo ver a mensagem "Candidatura em Análise"
