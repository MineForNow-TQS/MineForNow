import React, { useState } from 'react';
import ContactForm from './support/ContactForm';
import InfoModal from './support/InfoModal';

export default function Footer() {
    const [contactOpen, setContactOpen] = useState(false);
    const [infoModalData, setInfoModalData] = useState({ isOpen: false, title: '', content: '' });

    const openContact = (e) => {
        e.preventDefault();
        setContactOpen(true);
    };

    const openInfo = (e, type) => {
        e.preventDefault();
        if (type === 'help') {
            setInfoModalData({
                isOpen: true,
                title: 'Centro de Ajuda',
                content: (
                    <>
                        <h4 className="font-semibold text-slate-800 mb-2">Como Alugar?</h4>
                        <p className="mb-4">Para alugar um carro, basta pesquisar pelas datas e local desejados, escolher o veículo e enviar um pedido de reserva.</p>

                        <h4 className="font-semibold text-slate-800 mb-2">Cancelamentos</h4>
                        <p className="mb-4">Pode cancelar a sua reserva gratuitamente até 48h antes do início do aluguer.</p>

                        <h4 className="font-semibold text-slate-800 mb-2">Pagamentos</h4>
                        <p>Aceitamos cartões de crédito/débito e MB Way. O pagamento é retido pela plataforma até o final do aluguer.</p>
                    </>
                )
            });
        } else if (type === 'terms') {
            setInfoModalData({
                isOpen: true,
                title: 'Termos e Condições',
                content: (
                    <>
                        <p className="mb-4">Ao utilizar a plataforma MineForNow, concorda com os seguintes termos:</p>
                        <ul className="list-disc pl-5 space-y-2">
                            <li>Deve possuir carta de condução válida há pelo menos 2 anos.</li>
                            <li>O veículo deve ser devolvido nas mesmas condições de limpeza e combustível.</li>
                            <li>A MineForNow atua apenas como intermediária entre Owner e Renter.</li>
                            <li>Qualquer dano não reportado será da responsabilidade do condutor.</li>
                        </ul>
                    </>
                )
            });
        }
    };

    return (
        <>
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
                                <li><a href="#" className="hover:text-white transition-colors">Como funciona</a></li>
                                <li><a href="#" className="hover:text-white transition-colors">Pesquisar carros</a></li>
                                <li><a href="#" className="hover:text-white transition-colors">Seguros</a></li>
                            </ul>
                        </div>
                        <div>
                            <h4 className="text-white font-semibold mb-4">Para Owners</h4>
                            <ul className="space-y-2 text-sm">
                                <li><a href="#" className="hover:text-white transition-colors">Listar carro</a></li>
                                <li><a href="#" className="hover:text-white transition-colors">Preços</a></li>
                                <li><a href="#" className="hover:text-white transition-colors">FAQ</a></li>
                            </ul>
                        </div>
                        <div>
                            <h4 className="text-white font-semibold mb-4">Suporte</h4>
                            <ul className="space-y-2 text-sm">
                                <li>
                                    <button onClick={openContact} className="hover:text-white transition-colors text-left w-full">
                                        Contactos
                                    </button>
                                </li>
                                <li>
                                    <button onClick={(e) => openInfo(e, 'help')} className="hover:text-white transition-colors text-left w-full">
                                        Ajuda
                                    </button>
                                </li>
                                <li>
                                    <button onClick={(e) => openInfo(e, 'terms')} className="hover:text-white transition-colors text-left w-full">
                                        Termos
                                    </button>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <div className="border-t border-slate-800 pt-8 text-center text-sm">
                        <p>© 2024 MineForNow. Todos os direitos reservados.</p>
                    </div>
                </div>
            </footer>

            {/* Modals */}
            <ContactForm
                isOpen={contactOpen}
                onClose={() => setContactOpen(false)}
            />

            <InfoModal
                isOpen={infoModalData.isOpen}
                onClose={() => setInfoModalData({ ...infoModalData, isOpen: false })}
                title={infoModalData.title}
                content={infoModalData.content}
            />
        </>
    );
}
