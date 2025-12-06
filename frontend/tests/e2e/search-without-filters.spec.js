import { test, expect } from '@playwright/test';

test.describe('Pesquisa de Veículos - Sem Filtros', () => {
  test('deve mostrar todos os 6 veículos quando não há filtros aplicados', async ({ page }) => {
    // Navegar para a página de carros
    await page.goto('/cars');
    
    // Aguardar que a página carregue
    await page.waitForLoadState('networkidle');
    
    // Verificar que aparecem 6 carros
    await expect(page.getByText(/6 carros encontrados/i)).toBeVisible();
  });
});