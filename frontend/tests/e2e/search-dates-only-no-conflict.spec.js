import { test, expect } from '@playwright/test';

test.describe('Pesquisa de Veículos - Apenas Datas sem Conflito', () => {
  test('deve mostrar todos os 6 veículos ao pesquisar apenas por datas 23-26 Dez (sem reservas)', async ({ page }) => {
    // Navegar para a homepage
    await page.goto('/');
    
    // Preencher apenas datas (23-26 Dezembro - sem reservas)
    // NÃO preencher cidade para testar filtro apenas por disponibilidade
    await page.getByRole('textbox').nth(1).fill('2025-12-23');
    await page.getByRole('textbox').nth(2).fill('2025-12-26');
    
    // Clicar em pesquisar
    await page.getByRole('button', { name: 'Pesquisar Carros' }).click();
    
    // Aguardar navegação e carregamento
    await page.waitForLoadState('networkidle');
    
    // Verificar que aparecem todos os 6 carros (nenhum reservado neste período)
    await expect(page.getByText(/6 carros encontrados/i)).toBeVisible();
  });
});