import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery } from 'react-query';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Car, Calendar, Crown } from 'lucide-react';
import { useAuth } from '@/contexts/AuthContext';
import { ownerRequestService } from '@/services/ownerRequestService';

export default function RentalDashboard() {
    const navigate = useNavigate();
    const { user } = useAuth();

    // Check if user has a pending request
    const { data: existingRequests = [] } = useQuery(
        ['ownerRequests', user?.email],
        () => ownerRequestService.list().then(res => res.data),
        {
            enabled: !!user
        }
    );

    const hasPendingRequest = existingRequests.some(
        req => req.user_email === user?.email && req.status === 'pending'
    );

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
            <Card className="p-12 text-center border border-slate-200 mb-8">
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

            {/* Become Owner Banner */}
            <Card className="p-6 bg-gradient-to-r from-amber-50 to-orange-50 border border-amber-200">
                <div className="flex items-center justify-between">
                    <div className="flex items-center gap-4">
                        <div className="w-12 h-12 bg-amber-100 rounded-full flex items-center justify-center">
                            <Crown className="w-6 h-6 text-amber-600" />
                        </div>
                        <div>
                            <h3 className="font-semibold text-slate-900 mb-1">
                                Quer rentabilizar o seu carro?
                            </h3>
                            <p className="text-sm text-slate-600">
                                {hasPendingRequest 
                                    ? 'O seu pedido está a ser analisado pela nossa equipa'
                                    : 'Torne-se Owner e comece a ganhar dinheiro'
                                }
                            </p>
                        </div>
                    </div>
                    <Button 
                        onClick={() => navigate('/become-owner')}
                        disabled={hasPendingRequest}
                        className="bg-orange-500 hover:bg-orange-600 text-white disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {hasPendingRequest ? 'Em Análise' : 'Candidatar-me'}
                    </Button>
                </div>
            </Card>
        </>
    );
}
