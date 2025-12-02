import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { useQuery } from 'react-query';
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Crown, Check, CheckCircle, Clock, ArrowLeft } from "lucide-react";
import { ownerRequestService } from '@/services/ownerRequestService';

export default function BecomeOwner() {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        fullName: user?.full_name || '',
        email: user?.email || '',
        drivingLicense: '',
        reason: ''
    });
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [showSuccess, setShowSuccess] = useState(false);

    // Check if user already has a pending request
    const { data: existingRequests = [] } = useQuery(
        ['ownerRequests', user?.email],
        () => ownerRequestService.list().then(res => res.data),
        {
            enabled: !!user
        }
    );

    // Check if user is already owner or admin
    const isOwner = user?.user_role === 'owner' || user?.user_role === 'admin';
    
    // Check if user has a pending request
    const hasPendingRequest = existingRequests.some(
        req => req.user_email === user?.email && req.status === 'pending'
    );

    // Check if form is valid
    const isFormValid = formData.drivingLicense.trim().length > 0;

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!formData.drivingLicense.trim()) {
            alert('Por favor, preencha o ID da Carta de Condução');
            return;
        }

        setIsSubmitting(true);
        
        try {
            await ownerRequestService.create({
                user_id: user?.id || 'user_' + Date.now(),
                user_name: formData.fullName,
                user_email: formData.email,
                driving_license: formData.drivingLicense,
                reason: formData.reason
            });
            
            // Clear form
            setFormData({
                fullName: user?.full_name || '',
                email: user?.email || '',
                drivingLicense: '',
                reason: ''
            });
            
            // Show success message
            setShowSuccess(true);
            
            // Redirect to dashboard after 5 seconds
            setTimeout(() => {
                navigate('/dashboard');
            }, 5000);
        } catch (error) {
            console.error('Erro ao submeter candidatura:', error);
            alert('Erro ao submeter candidatura. Por favor, tente novamente.');
            setIsSubmitting(false);
        }
    };

    // Show success message
    if (showSuccess) {
        return (
            <div className="min-h-screen bg-white py-12 flex items-center justify-center">
                <div className="max-w-md mx-auto px-4 sm:px-6 text-center">
                    <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
                        <CheckCircle className="w-12 h-12 text-green-600" />
                    </div>
                    <h1 className="text-3xl font-bold text-slate-900 mb-3">
                        Candidatura Enviada!
                    </h1>
                    <p className="text-slate-600 mb-4">
                        A sua candidatura foi submetida com sucesso! Receberá uma resposta em breve depois de uma detalhada análise da nossa equipa.
                    </p>
                    <p className="text-sm text-slate-500 mb-6">
                        Será redirecionado para o painel em instantes...
                    </p>
                    <Button 
                        onClick={() => navigate('/dashboard')}
                        className="bg-indigo-600 hover:bg-indigo-700 text-white"
                    >
                        Ir para o Painel
                    </Button>
                </div>
            </div>
        );
    }

    // Show pending request message
    if (hasPendingRequest) {
        return (
            <div className="min-h-screen bg-white py-12 flex items-center justify-center">
                <div className="max-w-md mx-auto px-4 sm:px-6">
                    <button
                        onClick={() => navigate('/dashboard')}
                        className="flex items-center gap-2 text-slate-600 hover:text-slate-900 mb-6"
                    >
                        <ArrowLeft className="w-4 h-4" />
                        Voltar
                    </button>
                    
                    <Card className="p-8 text-center border border-slate-200">
                        <div className="w-20 h-20 bg-amber-100 rounded-full flex items-center justify-center mx-auto mb-6">
                            <Clock className="w-12 h-12 text-amber-600" />
                        </div>
                        <h1 className="text-2xl font-bold text-slate-900 mb-3">
                            Pedido em Análise
                        </h1>
                        <p className="text-slate-600 mb-6">
                            O seu pedido foi submetido e está a ser analisado pela nossa equipa. Receberá uma notificação quando for aprovado.
                        </p>
                        <Button 
                            onClick={() => navigate('/dashboard')}
                            className="bg-slate-900 hover:bg-slate-800 text-white"
                        >
                            Voltar ao Painel
                        </Button>
                    </Card>
                </div>
            </div>
        );
    }

    // Redirect owners/admins to dashboard
    if (isOwner) {
        navigate('/dashboard');
        return null;
    }

    return (
        <div className="min-h-screen bg-white py-12">
            <div className="max-w-2xl mx-auto px-4 sm:px-6">
                {/* Header */}
                <div className="text-center mb-8">
                    <div className="w-16 h-16 bg-amber-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <Crown className="w-8 h-8 text-amber-600" />
                    </div>
                    <h1 className="text-3xl font-bold text-slate-900 mb-3">
                        Torne-se Owner
                    </h1>
                    <p className="text-slate-600">
                        Comece a rentabilizar o seu carro e ganhe dinheiro extra
                    </p>
                </div>

                {/* Form */}
                <Card className="p-8 border border-slate-200 mb-8">
                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div>
                            <label className="block text-sm font-medium text-slate-900 mb-2">
                                Nome Completo
                            </label>
                            <Input
                                value={formData.fullName}
                                onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                                placeholder="NADA DE NADA"
                                className="bg-white border-slate-300"
                                disabled={!!user?.full_name}
                            />
                            {user?.full_name && (
                                <p className="text-xs text-slate-500 mt-1">O nome não pode ser alterado</p>
                            )}
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-slate-900 mb-2">
                                Email
                            </label>
                            <Input
                                type="email"
                                value={formData.email}
                                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                                placeholder="anacristina11371137113711371@gmail.com"
                                className="bg-white border-slate-300"
                                disabled={!!user?.email}
                            />
                            {user?.email && (
                                <p className="text-xs text-slate-500 mt-1">O email não pode ser alterado</p>
                            )}
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-slate-900 mb-2">
                                ID da Carta de Condução <span className="text-red-500">*</span>
                            </label>
                            <Input
                                value={formData.drivingLicense}
                                onChange={(e) => setFormData({ ...formData, drivingLicense: e.target.value })}
                                placeholder="Ex: AB123456"
                                className="bg-white border-slate-300"
                                required
                            />
                            <p className="text-xs text-slate-500 mt-1">Necessário para verificação</p>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-slate-900 mb-2">
                                Porque quer ser Owner?
                            </label>
                            <Textarea
                                value={formData.reason}
                                onChange={(e) => setFormData({ ...formData, reason: e.target.value })}
                                placeholder="Conte-nos um pouco sobre si e porque quer partilhar o seu carro..."
                                className="bg-white border-slate-300 min-h-[120px] resize-none"
                                rows={5}
                            />
                        </div>

                        <Button 
                            type="submit"
                            disabled={isSubmitting || !isFormValid}
                            className="w-full bg-orange-500 hover:bg-orange-600 text-white py-6 text-base font-medium disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            <Crown className="w-5 h-5 mr-2" />
                            {isSubmitting ? 'A submeter...' : 'Submeter Candidatura'}
                        </Button>
                    </form>
                </Card>

                {/* What Happens Next */}
                <Card className="p-8 bg-gradient-to-br from-amber-50 to-orange-50 border border-amber-200">
                    <h2 className="text-lg font-semibold text-slate-900 mb-4">
                        O que acontece a seguir:
                    </h2>
                    <div className="space-y-3">
                        <div className="flex items-start gap-3">
                            <div className="w-5 h-5 bg-green-500 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5">
                                <Check className="w-3 h-3 text-white" />
                            </div>
                            <p className="text-slate-700">
                                A nossa equipa irá analisar o seu pedido
                            </p>
                        </div>
                        <div className="flex items-start gap-3">
                            <div className="w-5 h-5 bg-green-500 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5">
                                <Check className="w-3 h-3 text-white" />
                            </div>
                            <p className="text-slate-700">
                                Receberá uma notificação com a decisão
                            </p>
                        </div>
                        <div className="flex items-start gap-3">
                            <div className="w-5 h-5 bg-green-500 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5">
                                <Check className="w-3 h-3 text-white" />
                            </div>
                            <p className="text-slate-700">
                                Após aprovação, poderá adicionar carros
                            </p>
                        </div>
                    </div>
                </Card>
            </div>
        </div>
    );
}
