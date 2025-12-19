# language: pt
@SCRUM-28
Funcionalidade: Submissão de Avaliação (UI)
  Como Renter
  Quero avaliar uma experiência de aluguer terminada
  Para partilhar a minha opinião com outros utilizadores

  Cenário: Visitante não vê botão de avaliação na página do carro sem login
    Dado que estou na página do veículo com id 1
    E que não estou logado
    Então não devo ver o botão de criar review
