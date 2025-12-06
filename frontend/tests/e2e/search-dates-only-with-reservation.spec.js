import { test, expect } from '@playwright/test';

test.describe('Pesquisa de Veículos - Apenas Datas com Reserva', () => {
  test('deve mostrar 5 veículos ao pesquisar apenas por datas 16-21 Dez (sem filtro de cidade)', async ({ page }) => {
    // Navegar para a homepage
    await page.goto('/');
    
    // Preencher apenas datas (16-21 Dezembro - Mercedes reservado)
    // NÃO preencher cidade para testar filtro apenas por disponibilidade
    await page.getByRole('textbox').nth(1).fill('2025-12-16');
    await page.getByRole('textbox').nth(2).fill('2025-12-21');
    
    // Clicar em pesquisar
    await page.getByRole('button', { name: 'Pesquisar Carros' }).click();
    
    // Aguardar navegação e carregamento
    await page.waitForLoadState('networkidle');
    
    // Verificar que aparecem 5 carros (6 total - 1 Mercedes reservado)
    await expect(page.getByText(/5 carros encontrados/i)).toBeVisible();
  });
});