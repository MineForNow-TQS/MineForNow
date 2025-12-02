import React from 'react';
import HeroSearch from '@/components/home/HeroSearch';
import FeaturedCars from '@/components/home/FeaturedCars';
import HowItWorks from '@/components/home/HowItWorks';
import BecomeOwnerSection from '@/components/home/BecomeOwnerSection';

export default function Home() {
    return (
        <div className="min-h-screen bg-white">
            <HeroSearch />
            <FeaturedCars />
            <HowItWorks />
            <BecomeOwnerSection />
            
            {/* Footer */}
            <footer className="bg-slate-900 text-slate-400 py-12 px-4 sm:px-6">
                <div className="max-w-7xl mx-auto">
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-8 mb-8">
                        <div>
                            <h4 className="text-white font-semibold mb-4">MineForNow</h4>
                            <p className="text-sm">A plataforma de aluguer de carros entre particulares líder em Portugal.</p>
                        </div>
                        <div>
                            <h4 className="text-white font-semibold mb-4">Para Renters</h4>
                            <ul className="space-y-2 text-sm">
                                <li>Como funciona</li>
                                <li>Pesquisar carros</li>
                                <li>Seguros</li>
                            </ul>
                        </div>
                        <div>
                            <h4 className="text-white font-semibold mb-4">Para Owners</h4>
                            <ul className="space-y-2 text-sm">
                                <li>Listar carro</li>
                                <li>Preços</li>
                                <li>FAQ</li>
                            </ul>
                        </div>
                        <div>
                            <h4 className="text-white font-semibold mb-4">Suporte</h4>
                            <ul className="space-y-2 text-sm">
                                <li>Contactos</li>
                                <li>Ajuda</li>
                                <li>Termos</li>
                            </ul>
                        </div>
                    </div>
                    <div className="border-t border-slate-800 pt-8 text-center text-sm">
                        <p>© 2024 MineForNow. Todos os direitos reservados.</p>
                    </div>
                </div>
            </footer>
        </div>
    );
}
