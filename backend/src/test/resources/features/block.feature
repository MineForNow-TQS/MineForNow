# language: pt
@SCRUM-78
Funcionalidade: Bloqueio de Utilizadores pelo Administrador

  Cenário: Bloquear um utilizador e validar impedimento de acesso
    Dado que existe um utilizador comum com email "cliente@teste.com" e password "Senha123"
    E eu estou autenticado no sistema como administrador com "admin@tqs.com" e "admin123"
    Quando eu navego para a página de gestão de utilizadores
    E eu clico no botão de bloquear para o utilizador "cliente@teste.com"
    Então o botão deve mudar para "Ativar"
    E ao tentar fazer login com "cliente@teste.com" e "Senha123", devo ver a mensagem "A sua conta está bloqueada"