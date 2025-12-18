import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { CreditCard, Calendar, Car, CheckCircle, AlertCircle } from "lucide-react";
import paymentService from '../services/paymentService';

export default function Payment() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const bookingId = searchParams.get('bookingId');
    const carId = searchParams.get('carId');
    const startDate = searchParams.get('start');
    const endDate = searchParams.get('end');

    const [formData, setFormData] = useState({
        cardNumber: '',
        cardHolderName: '',
        expiryDate: '',
        cvv: ''
    });

    const [errors, setErrors] = useState({});
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitSuccess, setSubmitSuccess] = useState(false);
    const [submitError, setSubmitError] = useState('');

    // Redirect if no bookingId
    useEffect(() => {
        if (!bookingId) {
            navigate('/cars');
        }
    }, [bookingId, navigate]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        let formattedValue = value;

        // Format card number (only last 4 digits)
        if (name === 'cardNumber') {
            formattedValue = value.replace(/\D/g, '').slice(0, 4);
        }

        // Format expiry date (MM/YY)
        if (name === 'expiryDate') {
            formattedValue = value.replace(/\D/g, '');
            if (formattedValue.length >= 2) {
                formattedValue = formattedValue.slice(0, 2) + '/' + formattedValue.slice(2, 4);
            }
        }

        // Format CVV (3 digits)
        if (name === 'cvv') {
            formattedValue = value.replace(/\D/g, '').slice(0, 3);
        }

        setFormData(prev => ({
            ...prev,
            [name]: formattedValue
        }));

        // Clear error for this field
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    const validateForm = () => {
        const newErrors = {};

        if (!formData.cardNumber || formData.cardNumber.length !== 4) {
            newErrors.cardNumber = 'Insira os últimos 4 dígitos do cartão';
        }

        if (!formData.cardHolderName.trim()) {
            newErrors.cardHolderName = 'Nome do titular é obrigatório';
        }

        if (!formData.expiryDate || !/^\d{2}\/\d{2}$/.test(formData.expiryDate)) {
            newErrors.expiryDate = 'Formato inválido (MM/YY)';
        }

        if (!formData.cvv || formData.cvv.length !== 3) {
            newErrors.cvv = 'CVV deve ter 3 dígitos';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        setIsSubmitting(true);
        setSubmitError('');

        try {
            await paymentService.confirmPayment(bookingId, formData);
            setSubmitSuccess(true);

            // Redirect to dashboard after 2 seconds
            setTimeout(() => {
                navigate('/dashboard');
            }, 2000);
        } catch (error) {
            console.error('Payment error:', error);
            // Show the specific backend error message
            const errorMessage = error.response?.data?.message ||
                error.response?.data ||
                'Erro ao processar pagamento. Tente novamente.';
            setSubmitError(errorMessage);
        } finally {
            setIsSubmitting(false);
        }
    };

    if (submitSuccess) {
        return (
            <div className="min-h-screen bg-slate-50 py-8">
                <div className="max-w-2xl mx-auto px-4 sm:px-6">
                    <Card className="p-8 text-center">
                        <CheckCircle className="w-16 h-16 text-green-500 mx-auto mb-4" />
                        <h2 className="text-2xl font-bold text-slate-900 mb-2">
                            Pagamento Confirmado!
                        </h2>
                        <p className="text-slate-600 mb-4">
                            A sua reserva foi confirmada com sucesso.
                        </p>
                        <p className="text-sm text-slate-500">
                            A redirecionar para o dashboard...
                        </p>
                    </Card>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-slate-50 py-8">
            <div className="max-w-2xl mx-auto px-4 sm:px-6">
                <h1 className="text-3xl font-bold text-slate-900 mb-8">Pagamento</h1>

                {/* Booking Details */}
                <Card className="p-6 mb-6">
                    <div className="flex items-center gap-3 mb-6 pb-6 border-b">
                        <CreditCard className="w-8 h-8 text-indigo-600" />
                        <div>
                            <h2 className="text-xl font-semibold text-slate-900">Detalhes da Reserva</h2>
                            <p className="text-sm text-slate-500">Reserva #{bookingId}</p>
                        </div>
                    </div>

                    <div className="space-y-4">
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
                </Card>

                {/* Payment Form */}
                <Card className="p-6">
                    <h2 className="text-xl font-semibold text-slate-900 mb-6">
                        Dados de Pagamento (Simulação)
                    </h2>

                    {submitError && (
                        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start gap-3">
                            <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
                            <p className="text-sm text-red-800">{submitError}</p>
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-6">
                        {/* Card Number (last 4 digits) */}
                        <div>
                            <Label htmlFor="cardNumber">Últimos 4 Dígitos do Cartão *</Label>
                            <Input
                                id="cardNumber"
                                name="cardNumber"
                                type="text"
                                placeholder="1234"
                                value={formData.cardNumber}
                                onChange={handleInputChange}
                                className={errors.cardNumber ? 'border-red-500' : ''}
                                maxLength={4}
                            />
                            {errors.cardNumber && (
                                <p className="text-sm text-red-600 mt-1">{errors.cardNumber}</p>
                            )}
                        </div>

                        {/* Card Holder Name */}
                        <div>
                            <Label htmlFor="cardHolderName">Nome do Titular *</Label>
                            <Input
                                id="cardHolderName"
                                name="cardHolderName"
                                type="text"
                                placeholder="João Silva"
                                value={formData.cardHolderName}
                                onChange={handleInputChange}
                                className={errors.cardHolderName ? 'border-red-500' : ''}
                            />
                            {errors.cardHolderName && (
                                <p className="text-sm text-red-600 mt-1">{errors.cardHolderName}</p>
                            )}
                        </div>

                        {/* Expiry Date and CVV */}
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <Label htmlFor="expiryDate">Validade (MM/YY) *</Label>
                                <Input
                                    id="expiryDate"
                                    name="expiryDate"
                                    type="text"
                                    placeholder="12/25"
                                    value={formData.expiryDate}
                                    onChange={handleInputChange}
                                    className={errors.expiryDate ? 'border-red-500' : ''}
                                    maxLength={5}
                                />
                                {errors.expiryDate && (
                                    <p className="text-sm text-red-600 mt-1">{errors.expiryDate}</p>
                                )}
                            </div>

                            <div>
                                <Label htmlFor="cvv">CVV *</Label>
                                <Input
                                    id="cvv"
                                    name="cvv"
                                    type="text"
                                    placeholder="123"
                                    value={formData.cvv}
                                    onChange={handleInputChange}
                                    className={errors.cvv ? 'border-red-500' : ''}
                                    maxLength={3}
                                />
                                {errors.cvv && (
                                    <p className="text-sm text-red-600 mt-1">{errors.cvv}</p>
                                )}
                            </div>
                        </div>

                        {/* Info Note */}
                        <div className="bg-blue-50 p-4 rounded-lg">
                            <p className="text-sm text-blue-800">
                                <strong>Nota:</strong> Esta é uma simulação de pagamento.
                                Nenhum pagamento real será processado.
                            </p>
                        </div>

                        {/* Action Buttons */}
                        <div className="flex gap-3 pt-4">
                            <Button
                                type="button"
                                variant="outline"
                                className="flex-1"
                                onClick={() => navigate(-1)}
                                disabled={isSubmitting}
                            >
                                Voltar
                            </Button>
                            <Button
                                type="submit"
                                className="flex-1"
                                disabled={isSubmitting}
                            >
                                {isSubmitting ? 'A processar...' : 'Confirmar Pagamento'}
                            </Button>
                        </div>
                    </form>
                </Card>
            </div>
        </div>
    );
}
