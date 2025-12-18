import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { 
    Crown, 
    CheckCircle, 
    Clock, 
    ArrowLeft, 
    Phone, 
    CreditCard, 
    FileBadge, 
    ShieldCheck,
    Loader2 
} from "lucide-react";
import { ownerRequestService } from '@/services/ownerRequestService';

export default function BecomeOwner() {
    const { user, refreshUser } = useAuth();
    const navigate = useNavigate();
    
    // Estados locales
    const [formData, setFormData] = useState({
        phone: '',
        citizenCardNumber: '',
        drivingLicense: '',
        motivation: ''
    });
    
    const [isInitialLoading, setIsInitialLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [showSuccess, setShowSuccess] = useState(false);

    // 1. Sincronizar estado con el backend al montar el componente
    useEffect(() => {
        const verifyStatus = async () => {
            setIsInitialLoading(true);
            await refreshUser(); // Actualiza el rol en el AuthContext
            setIsInitialLoading(false);
        };
        verifyStatus();
    }, [refreshUser]);

    // 2. Definir estados basados en el rol actualizado del backend
    const isPending = user?.role === 'PENDING_OWNER';
    const isAlreadyOwner = user?.role === 'OWNER' || user?.role === 'ADMIN';

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);
        
        try {
            await ownerRequestService.create(formData);
            
            // Forzamos actualización del contexto para que el rol cambie a PENDING_OWNER
            await refreshUser(); 
            
            setShowSuccess(true);
            // Redirigir al dashboard después de un tiempo
            setTimeout(() => navigate('/dashboard'), 4000);
        } catch (error) {
            console.error('Erro:', error);
            // Captura el mensaje de tu backend: "Pedido já submetido..."
            const errorMsg = error.message || 'Erro ao submeter candidatura.';
            alert(errorMsg);
            setIsSubmitting(false);
        }
    };

    // --- VISTAS CONDICIONALES ---

    // A. Pantalla de carga inicial (Evita el "flash" del formulario)
    if (isInitialLoading) {
        return (
            <div className="min-h-screen flex flex-col items-center justify-center bg-slate-50">
                <Loader2 className="w-10 h-10 text-orange-500 animate-spin mb-4" />
                <p className="text-slate-600 font-medium">A verificar o seu estado...</p>
            </div>
        );
    }

    // B. Pantalla si ya es OWNER
    if (isAlreadyOwner) {
        return (
            <div className="min-h-screen bg-slate-50 py-12 flex items-center justify-center px-4">
                <Card className="p-8 max-w-md text-center shadow-xl border-t-4 border-t-green-500">
                    <ShieldCheck className="w-16 h-16 text-green-500 mx-auto mb-4" />
                    <h1 className="text-2xl font-bold text-slate-900 mb-2">Já é um Owner!</h1>
                    <p className="text-slate-600 mb-6">
                        A sua conta já possui privilégios de proprietário. Pode começar a gerir os seus veículos no painel.
                    </p>
                    <Button onClick={() => navigate('/dashboard')} className="w-full bg-slate-900 hover:bg-slate-800">
                        Ir para o Painel
                    </Button>
                </Card>
            </div>
        );
    }

    // C. Pantalla si el pedido está PENDIENTE (Sincronizado con el 409 del backend)
    if (isPending || showSuccess) {
        return (
            <div className="min-h-screen bg-slate-50 py-12 flex items-center justify-center px-4">
                <Card className="p-8 max-w-md text-center shadow-xl border-t-4 border-t-amber-500">
                    {showSuccess ? (
                        <CheckCircle className="w-16 h-16 text-green-500 mx-auto mb-4 animate-bounce" />
                    ) : (
                        <Clock className="w-16 h-16 text-amber-500 mx-auto mb-4" />
                    )}
                    <h1 className="text-2xl font-bold text-slate-900 mb-2">
                        {showSuccess ? "Enviado com Sucesso!" : "Candidatura em Análise"}
                    </h1>
                    <p className="text-slate-600 mb-6">
                        O seu pedido está a ser validado pela nossa equipa. Receberá uma notificación assim que for aprovado.
                    </p>
                    <Button onClick={() => navigate('/dashboard')} variant="outline" className="w-full">
                        Voltar ao Dashboard
                    </Button>
                </Card>
            </div>
        );
    }

    // D. Formulario normal (Solo para RENTERs sin pedidos activos)
    return (
        <div className="min-h-screen bg-slate-50 py-12 px-4">
            <div className="max-w-2xl mx-auto">
                <button 
                    onClick={() => navigate(-1)} 
                    className="flex items-center text-slate-600 mb-6 hover:text-orange-600 transition-colors"
                >
                    <ArrowLeft className="w-4 h-4 mr-2" /> Voltar
                </button>

                <div className="text-center mb-8">
                    <div className="bg-amber-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                        <Crown className="w-8 h-8 text-amber-600" />
                    </div>
                    <h1 className="text-3xl font-bold text-slate-900">Torne-se Owner</h1>
                    <p className="text-slate-600 mt-2">Rentabilize o seu veículo e comece a ganhar hoje mesmo.</p>
                </div>

                <Card className="p-8 border-none shadow-lg bg-white">
                    <form onSubmit={handleSubmit} className="space-y-6">
                        
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div className="space-y-2">
                                <label className="text-sm font-semibold flex items-center gap-2 text-slate-700">
                                    <Phone className="w-4 h-4 text-orange-500" /> Telefone
                                </label>
                                <Input 
                                    required
                                    placeholder="912 345 678"
                                    value={formData.phone}
                                    onChange={(e) => setFormData({...formData, phone: e.target.value})}
                                />
                            </div>

                            <div className="space-y-2">
                                <label className="text-sm font-semibold flex items-center gap-2 text-slate-700">
                                    <CreditCard className="w-4 h-4 text-orange-500" /> Nº Cartão Cidadão
                                </label>
                                <Input 
                                    required
                                    placeholder="12345678"
                                    value={formData.citizenCardNumber}
                                    onChange={(e) => setFormData({...formData, citizenCardNumber: e.target.value})}
                                />
                            </div>
                        </div>

                        <div className="space-y-2">
                            <label className="text-sm font-semibold flex items-center gap-2 text-slate-700">
                                <FileBadge className="w-4 h-4 text-orange-500" /> Carta de Condução (Ex: PT123456)
                            </label>
                            <Input 
                                required
                                pattern="^[A-Z]{2}[0-9]{6}$"
                                placeholder="Duas letras e seis números"
                                value={formData.drivingLicense}
                                onChange={(e) => setFormData({...formData, drivingLicense: e.target.value})}
                                className="uppercase"
                            />
                        </div>

                        <div className="space-y-2">
                            <label className="text-sm font-semibold text-slate-700">Porque quer ser Owner?</label>
                            <Textarea 
                                required
                                placeholder="Conte-nos brevemente a sua motivação..."
                                rows={4}
                                value={formData.motivation}
                                onChange={(e) => setFormData({...formData, motivation: e.target.value})}
                                className="resize-none"
                            />
                        </div>

                        <Button 
                            type="submit" 
                            className="w-full bg-orange-500 hover:bg-orange-600 text-white h-12 text-lg font-bold shadow-md transition-all active:scale-[0.98]"
                            disabled={isSubmitting}
                        >
                            {isSubmitting ? (
                                <span className="flex items-center gap-2">
                                    <Loader2 className="w-5 h-5 animate-spin" /> A Processar...
                                </span>
                            ) : "Submeter Candidatura"}
                        </Button>
                    </form>
                </Card>
            </div>
        </div>
    );
}