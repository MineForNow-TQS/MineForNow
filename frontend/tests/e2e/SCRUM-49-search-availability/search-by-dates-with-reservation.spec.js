import { test, expect } from '@playwright/test';

test.describe('Pesquisa de Veículos - Datas com Reserva', () => {
  test('deve mostrar apenas 1 veículo em Lisboa para datas 16-21 Dez (Mercedes reservado)', async ({ page }) => {
    // Navegar para a homepage
    await page.goto('/');
    
    // Preencher cidade Lisboa
    await page.getByRole('textbox', { name: 'Lisboa, Porto, Faro...' }).click();
    await page.getByRole('textbox', { name: 'Lisboa, Porto, Faro...' }).fill('Lisboa');
    
    // Preencher datas (16-21 Dezembro - período com reserva do Mercedes)
    await page.getByRole('textbox').nth(1).fill('2025-12-16');
    await page.getByRole('textbox').nth(2).fill('2025-12-21');
    
    // Clicar em pesquisar
    await page.getByRole('button', { name: 'Pesquisar Carros' }).click();
    
    // Aguardar navegação e carregamento
    await page.waitForLoadState('networkidle');
    
    // Verificar que aparece apenas 1 carro (Ferrari, Mercedes excluído por estar reservado)
    await expect(page.getByText(/1 carro/i)).toBeVisible();
  });
});