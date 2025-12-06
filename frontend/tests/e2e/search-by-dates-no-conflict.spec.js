import { test, expect } from '@playwright/test';

test.describe('Pesquisa de Veículos - Datas sem Conflito', () => {
  test('deve mostrar 2 veículos em Lisboa para datas 22-25 Dez (sem reservas)', async ({ page }) => {
    // Navegar para a homepage
    await page.goto('/');
    
    // Preencher cidade Lisboa
    await page.getByRole('textbox', { name: 'Lisboa, Porto, Faro...' }).click();
    await page.getByRole('textbox', { name: 'Lisboa, Porto, Faro...' }).fill('Lisboa');
    
    // Preencher datas (22-25 Dezembro - sem reservas)
    await page.getByRole('textbox').nth(1).fill('2025-12-22');
    await page.getByRole('textbox').nth(2).fill('2025-12-25');
    
    // Clicar em pesquisar
    await page.getByRole('button', { name: 'Pesquisar Carros' }).click();
    
    // Aguardar navegação e carregamento
    await page.waitForLoadState('networkidle');
    
    // Verificar que aparecem 2 carros (Ferrari e Mercedes, ambos disponíveis)
    await expect(page.getByText(/2 carros encontrados/i)).toBeVisible();
  });
});