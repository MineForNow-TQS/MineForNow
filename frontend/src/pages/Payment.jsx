import React, { useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { CreditCard, Calendar, Car, MapPin } from "lucide-react";

export default function Payment() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const carId = searchParams.get('carId');
    const startDate = searchParams.get('start');
    const endDate = searchParams.get('end');

    return (
        <div className="min-h-screen bg-slate-50 py-8">
            <div className="max-w-2xl mx-auto px-4 sm:px-6">
                <h1 className="text-3xl font-bold text-slate-900 mb-8">Pagamento</h1>

                <Card className="p-6 mb-6">
                    <div className="flex items-center gap-3 mb-6 pb-6 border-b">
                        <CreditCard className="w-8 h-8 text-indigo-600" />
                        <div>
                            <h2 className="text-xl font-semibold text-slate-900">Detalhes da Reserva</h2>
                            <p className="text-sm text-slate-500">Verifique os dados antes de pagar</p>
                        </div>
                    </div>

                    <div className="space-y-4 mb-6">
                        <div className="flex items-center gap-3 text-slate-700">
                            <Car className="w-5 h-5 text-slate-400" />
                            <div>
                                <p className="text-sm text-slate-500">Veículo</p>
                                <p className="font-medium">ID: {carId}</p>
                            </div>
                        </div>

                        <div className="flex items-center gap-3 text-slate-700">
                            <Calendar className="w-5 h-5 text-slate-400" />
                            <div>
                                <p className="text-sm text-slate-500">Período</p>
                                <p className="font-medium">{startDate} até {endDate}</p>
                            </div>
                        </div>
                    </div>

                    <div className="bg-slate-50 p-4 rounded-lg mb-6">
                        <p className="text-sm text-slate-600 mb-2">
                            <strong>Nota:</strong> Esta é uma página de demonstração.
                        </p>
                        <p className="text-sm text-slate-600">
                            O formulário de pagamento completo será implementado no SCRUM-16.
                        </p>
                    </div>

                    <div className="flex gap-3">
                        <Button
                            variant="outline"
                            className="flex-1"
                            onClick={() => navigate('/cars')}
                        >
                            Voltar
                        </Button>
                        <Button
                            className="flex-1"
                            onClick={() => navigate('/dashboard')}
                        >
                            Ir para Dashboard
                        </Button>
                    </div>
                </Card>
            </div>
        </div>
    );
}
