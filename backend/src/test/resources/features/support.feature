# language: pt
@SCRUM-27
Funcionalidade: Centro de Suporte e Reporte de Incidentes
  Como um utilizador da plataforma MineForNow
  Quero aceder rapidamente a formas de contacto e informação de ajuda no rodapé
  Para esclarecer dúvidas ou resolver problemas

  Contexto:
    Dado que estou na homepage

  Cenário: Enviar mensagem de contacto
    Quando clico no link de contactos no rodapé
    Então devo ver o modal de contactos abrir
    Quando preencho o formulário de contacto
    E envio a mensagem
    Então devo ver uma mensagem de sucesso

  Cenário: Consultar Ajuda
    Quando clico no link de ajuda no rodapé
    Então devo ver o modal de ajuda abrir
    E devo ver informações sobre como alugar

  Cenário: Consultar Termos e Condições
    Quando clico no link de termos no rodapé
    Então devo ver o modal de termos abrir
    E devo ver tópicos sobre carta de condução
