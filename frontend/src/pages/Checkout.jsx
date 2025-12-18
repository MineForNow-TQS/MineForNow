import React, { useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useQuery } from 'react-query';
import { carService } from '@/services/carService';
import { bookingService } from '@/services/bookingService';
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Loader2, Calendar, MapPin, AlertCircle, CheckCircle } from "lucide-react";
import { formatCurrency } from '@/utils';
import { useAuth } from '@/contexts/AuthContext';

export default function Checkout() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { user } = useAuth();

    const carId = searchParams.get('carId');
    const startDate = searchParams.get('start');
    const endDate = searchParams.get('end');

    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const { data: car, isLoading } = useQuery(['car', carId], async () => {
        if (!carId) return null;
        const result = await carService.get(carId);
        return result.data;
    }, {
        enabled: !!carId
    });

    if (isLoading) {
        return (
            <div className="flex justify-center items-center min-h-screen">
                <Loader2 className="w-8 h-8 animate-spin text-indigo-600" />
            </div>
        );
    }

    if (!car || !startDate || !endDate) {
        return (
            <div className="min-h-screen bg-slate-50 py-8 px-4 flex justify-center">
                <Card className="p-6 max-w-md w-full text-center">
                    <AlertCircle className="w-12 h-12 text-red-500 mx-auto mb-4" />
                    <h2 className="text-xl font-bold text-slate-900 mb-2">Dados em falta</h2>
                    <p className="text-slate-600 mb-4">Parâmetros de reserva inválidos.</p>
                    <Button onClick={() => navigate('/cars')}>Voltar aos Carros</Button>
                </Card>
            </div>
        );
    }

    // Calculation (Frontend estimate, backend validates)
    const start = new Date(startDate);
    const end = new Date(endDate);
    const dayDiff = Math.max(1, Math.ceil((end - start) / (1000 * 60 * 60 * 24)));
    const totalPrice = dayDiff * car.price_per_day;

    const handleConfirmBooking = async () => {
        setError('');
        setSuccess('');
        setIsSubmitting(true);

        if (!user) {
            setError("Login necessário. Redirecionando...");
            setTimeout(() => {
                navigate('/login', { state: { from: `/checkout?${searchParams.toString()}` } });
            }, 1500);
            setIsSubmitting(false);
            return;
        }

        try {
            // Create booking via API
            const bookingData = {
                vehicleId: parseInt(carId),
                startDate: startDate,
                endDate: endDate,
                renterId: user.id
            };

            const createdBooking = await bookingService.create(bookingData);

            setSuccess("Reserva criada com sucesso!");
            setTimeout(() => {
                navigate(`/payment?bookingId=${createdBooking.id}&carId=${carId}&start=${startDate}&end=${endDate}`);
            }, 1500);
        } catch (err) {
            console.error('Erro ao criar reserva:', err);
            setError(err.message || 'Erro ao criar reserva. Tente novamente.');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="min-h-screen bg-slate-50 py-8">
            <div className="max-w-3xl mx-auto px-4 sm:px-6">
                <h1 className="text-3xl font-bold text-slate-900 mb-8">Confirmar Reserva</h1>

                <div className="grid md:grid-cols-2 gap-6">
                    {/* Car Summary */}
                    <Card className="overflow-hidden">
                        <div className="h-48 bg-slate-200">
                            <img
                                src={car.images?.[0] || car.image_url || '/placeholder-car.jpg'}
                                alt={`${car.brand} ${car.model}`}
                                className="w-full h-full object-cover"
                            />
                        </div>
                        <div className="p-6">
                            <h2 className="text-xl font-bold text-slate-900 mb-1">{car.brand} {car.model}</h2>
                            <div className="flex items-center gap-2 text-slate-500 mb-4">
                                <MapPin className="w-4 h-4" />
                                <span className="text-sm">{car.city}</span>
                            </div>
                        </div>
                    </Card>

                    {/* Booking Details */}
                    <Card className="p-6 h-fit">
                        <h3 className="font-semibold text-slate-900 mb-4 pb-2 border-b">Detalhes</h3>

                        <div className="space-y-4 mb-6">
                            <div className="flex items-start gap-3">
                                <Calendar className="w-5 h-5 text-indigo-600 mt-0.5" />
                                <div>
                                    <p className="text-sm text-slate-500">Recolha</p>
                                    <p className="font-medium">{new Date(startDate).toLocaleDateString('pt-PT')}</p>
                                </div>
                            </div>
                            <div className="flex items-start gap-3">
                                <Calendar className="w-5 h-5 text-indigo-600 mt-0.5" />
                                <div>
                                    <p className="text-sm text-slate-500">Entrega</p>
                                    <p className="font-medium">{new Date(endDate).toLocaleDateString('pt-PT')}</p>
                                </div>
                            </div>
                        </div>

                        <div className="space-y-2 pt-4 border-t mb-6">
                            <div className="flex justify-between text-slate-600">
                                <span>{dayDiff} dias x {formatCurrency(car.price_per_day)}</span>
                                <span>{formatCurrency(totalPrice)}</span>
                            </div>
                            <div className="flex justify-between font-bold text-lg text-slate-900 pt-2 border-t">
                                <span>Total</span>
                                <span>{formatCurrency(totalPrice)}</span>
                            </div>
                        </div>

                        {error && (
                            <div className="bg-red-50 text-red-600 p-3 rounded-md mb-4 flex items-center gap-2 text-sm">
                                <AlertCircle className="w-4 h-4" />
                                {error}
                            </div>
                        )}

                        {success && (
                            <div className="bg-green-50 text-green-600 p-3 rounded-md mb-4 flex items-center gap-2 text-sm">
                                <CheckCircle className="w-4 h-4" />
                                {success}
                            </div>
                        )}

                        <Button
                            className="w-full h-12 text-base"
                            onClick={handleConfirmBooking}
                            disabled={isSubmitting || success}
                        >
                            {isSubmitting ? (
                                <>
                                    <Loader2 className="mr-2 h-4 w-4 animate-spin" /> Processando...
                                </>
                            ) : (
                                'Confirmar e Pagar'
                            )}
                        </Button>
                    </Card>
                </div>
            </div>
        </div>
    );
}
