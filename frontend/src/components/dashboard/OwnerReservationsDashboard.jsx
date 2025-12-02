import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Calendar } from 'lucide-react';

export default function OwnerReservationsDashboard() {
    const navigate = useNavigate();

    return (
        <>
            {/* Stats Cards */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-slate-900 mb-1">0</div>
                    <div className="text-sm text-slate-500">Total Reservas</div>
                </Card>
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-orange-500 mb-1">0</div>
                    <div className="text-sm text-slate-500">Pendentes</div>
                </Card>
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-green-500 mb-1">0</div>
                    <div className="text-sm text-slate-500">Confirmadas</div>
                </Card>
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-blue-500 mb-1">0</div>
                    <div className="text-sm text-slate-500">Em Curso</div>
                </Card>
            </div>

            {/* Active Reservations */}
            <h2 className="text-xl font-bold text-slate-900 mb-4">Reservas Ativas</h2>
            <Card className="p-12 text-center border border-slate-200">
                <div className="flex flex-col items-center">
                    <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mb-4">
                        <Calendar className="w-8 h-8 text-slate-400" />
                    </div>
                    <h3 className="text-lg font-semibold text-slate-900 mb-2">
                        Sem reservas ativas
                    </h3>
                    <p className="text-slate-500 mb-6">
                        Encontre o carro perfeito para a sua próxima viagem
                    </p>
                    <Button 
                        onClick={() => navigate('/cars')}
                        className="bg-slate-900 hover:bg-slate-800 text-white"
                    >
                        Pesquisar Carros
                    </Button>
                </div>
            </Card>
        </>
    );
}
