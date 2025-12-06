# Playwright E2E Tests

Testes end-to-end organizados por funcionalidade.

## Estrutura de Testes

- `search-without-filters.spec.js` - Pesquisa sem filtros (deve retornar 6 veículos)
- `search-by-city-lisboa.spec.js` - Pesquisa por cidade Lisboa (2 veículos)
- `search-by-city-porto.spec.js` - Pesquisa por cidade Porto (1 veículo)
- `search-by-dates-with-reservation.spec.js` - Pesquisa com datas 16-21 Dez (exclui Mercedes reservado)
- `search-by-dates-no-conflict.spec.js` - Pesquisa com datas 22-25 Dez (todos disponíveis)
- `search-dates-only-with-reservation.spec.js` - Pesquisa apenas por datas sem cidade (16-21 → 5 carros)
- `search-dates-only-no-conflict.spec.js` - Pesquisa apenas por datas sem cidade (25-28 → 6 carros)

## Como gravar novos testes

```bash
# Gravar teste interativamente
npx playwright codegen http://localhost:3000

# O código gerado deve ser guardado em tests/e2e/
```

## Como executar os testes

```bash
# Rodar todos os testes
npm test

# Rodar testes em modo UI
npx playwright test --ui

# Rodar teste específico
npx playwright test search-without-filters.spec.js

# Ver relatório
npx playwright show-report
```
