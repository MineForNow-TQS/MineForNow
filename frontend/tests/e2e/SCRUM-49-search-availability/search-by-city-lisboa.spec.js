import { test, expect } from '@playwright/test';

test.describe('Pesquisa de Veículos - Cidade Lisboa', () => {
  test('deve mostrar 2 veículos ao pesquisar por Lisboa', async ({ page }) => {
    // Navegar para a homepage
    await page.goto('/');
    
    // Preencher cidade Lisboa
    await page.getByRole('textbox', { name: 'Lisboa, Porto, Faro...' }).click();
    await page.getByRole('textbox', { name: 'Lisboa, Porto, Faro...' }).fill('Lisboa');
    
    // Clicar em pesquisar
    await page.getByRole('button', { name: 'Pesquisar Carros' }).click();
    
    // Aguardar navegação e carregamento
    await page.waitForLoadState('networkidle');
    
    // Verificar que aparecem 2 carros em Lisboa
    await expect(page.getByText(/2 carros encontrados/i)).toBeVisible();
  });
});