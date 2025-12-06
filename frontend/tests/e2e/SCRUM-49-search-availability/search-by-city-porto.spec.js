import { test, expect } from '@playwright/test';

test.describe('Pesquisa de Veículos - Cidade Porto', () => {
  test('deve mostrar 1 veículo ao pesquisar por Porto', async ({ page }) => {
    // Navegar para a homepage
    await page.goto('/');
    
    // Preencher cidade Porto
    await page.getByRole('textbox', { name: 'Lisboa, Porto, Faro...' }).click();
    await page.getByRole('textbox', { name: 'Lisboa, Porto, Faro...' }).fill('Porto');
    
    // Clicar em pesquisar
    await page.getByRole('button', { name: 'Pesquisar Carros' }).click();
    
    // Aguardar navegação e carregamento
    await page.waitForLoadState('networkidle');
    
    // Verificar que aparece 1 carro no Porto
    await expect(page.getByText(/1 carro/i)).toBeVisible();
  });
});