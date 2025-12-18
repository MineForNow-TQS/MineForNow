# Frontend Configuration

## API Configuration

O frontend está configurado para trabalhar tanto em desenvolvimento (`npm run dev`) como em produção (deploy).

### Desenvolvimento (npm run dev)

Em desenvolvimento, o frontend usa URLs relativas que são proxificadas para `http://localhost:8080`:

1. **Arquivo `.env.local`** - Define a URL da API para desenvolvimento:
   ```
   VITE_API_URL=http://localhost:8080
   ```

2. **Vite Proxy** - O Vite está configurado para fazer proxy de requisições `/api` para a URL configurada em `VITE_API_URL`

3. **Como funciona:**
   - Todos os pedidos à `/api/...` são proxificados para `http://localhost:8080/api/...`
   - Permite CORS sem problemas em desenvolvimento

### Produção (Deploy)

Em produção, o frontend usa URLs **relativas** (sem domínio):

1. **Arquivo `.env.production`** - Não define `VITE_API_URL`
   ```
   # Production configuration - uses relative URLs
   # VITE_API_URL is not set, so it defaults to relative URLs
   ```

2. **Como funciona:**
   - Todos os pedidos à `/api/...` são feitos para o mesmo domínio (`http://deti-tqs-18.ua.pt:8080/api/...`)
   - O Spring Boot serve o frontend estático na raiz `/`
   - O API está disponível em `/api`

## Estrutura de Configuração

### `src/config/api.js`

Este arquivo centraliza toda a lógica de configuração da API:

```javascript
// Em desenvolvimento (npm run dev)
// Usa: http://localhost:8080

// Em produção (build + deploy)
// Usa: '' (URLs relativas) -> http://deti-tqs-18.ua.pt:8080

export const API_BASE_URL = getApiBaseUrl();
export const getImageUrl = (imageUrl) => { ... };
```

### Todos os serviços (`src/services/*.js`)

Todos os serviços importam de `src/config/api.js`:

```javascript
import { API_BASE_URL, getImageUrl } from '../config/api';
```

## Como Usar

### 1. Para Desenvolvimento

```bash
cd frontend
npm install
npm run dev
```

- O frontend acessível em `http://localhost:3000`
- As requisições da API são proxificadas para `http://localhost:8080`

### 2. Para Build/Produção

```bash
cd frontend
npm install
npm run build
```

- Gera files em `frontend/dist`
- O deploy copia para `backend/src/main/resources/static`
- Em produção, usa URLs relativas automaticamente

## Troubleshooting

### Erro: "Cannot find module '../config/api'"

Certifique-se que o arquivo `src/config/api.js` existe e está na localização correta.

### Erro: "localhost:8080 refused to connect"

Em desenvolvimento, verifique se o backend está rodando em `http://localhost:8080`:

```bash
# Backend
cd backend
mvn spring-boot:run
```

### URLs incorretas em produção

Verifique se o arquivo `.env.production` não define `VITE_API_URL`. Deve estar vazio ou comentado.

## Variáveis de Ambiente

| Variável | Desenvolvimento | Produção | Descrição |
|----------|-----------------|----------|-----------|
| `VITE_API_URL` | `http://localhost:8080` | (não definido) | URL base da API |

