import React, { useState, useEffect } from 'react';
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { 
    Check, X, User, FileText, Phone, Mail, 
    Loader2, AlertCircle, ExternalLink 
} from "lucide-react";
import { adminService } from '@/services/adminService';

export default function AdminOwnerRequests() {
    const [requests, setRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [actionLoading, setActionLoading] = useState(null); // ID del usuario procesando

    useEffect(() => {
        fetchRequests();
    }, []);

    const fetchRequests = async () => {
        try {
            setLoading(true);
            const data = await adminService.getPendingRequests();
            setRequests(data);
        } catch (error) {
            console.error("Erro:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleDecision = async (userId, action) => {
        setActionLoading(userId);
        try {
            if (action === 'approve') {
                await adminService.approveRequest(userId);
            } else {
                await adminService.rejectRequest(userId);
            }
            // Eliminar de la lista local tras éxito
            setRequests(prev => prev.filter(req => req.id !== userId));
        } catch (error) {
            alert(error.message);
        } finally {
            setActionLoading(null);
        }
    };

    if (loading) {
        return (
            <div className="flex flex-col items-center justify-center p-20 space-y-4">
                <Loader2 className="w-8 h-8 animate-spin text-indigo-600" />
                <p className="text-slate-500 animate-pulse">A carregar candidaturas...</p>
            </div>
        );
    }

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h2 className="text-2xl font-bold text-slate-900">Pedidos de Upgrade</h2>
                <span className="bg-amber-100 text-amber-700 px-3 py-1 rounded-full text-sm font-medium">
                    {requests.length} pendentes
                </span>
            </div>

            {requests.length === 0 ? (
                <Card className="p-12 text-center border-dashed">
                    <div className="bg-slate-50 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                        <Check className="text-slate-400 w-8 h-8" />
                    </div>
                    <h3 className="text-lg font-medium text-slate-900">Tudo em dia!</h3>
                    <p className="text-slate-500">Não existem pedidos de owner por processar.</p>
                </Card>
            ) : (
                <div className="grid gap-4">
                    {requests.map((request) => (
                        <Card key={request.id} className="p-6 transition-all hover:shadow-md border-l-4 border-l-indigo-500">
                            <div className="flex flex-col lg:flex-row gap-6">
                                {/* Info del Usuario */}
                                <div className="flex-1 space-y-4">
                                    <div className="flex items-center gap-3">
                                        <div className="bg-indigo-100 p-2 rounded-lg">
                                            <User className="w-5 h-5 text-indigo-600" />
                                        </div>
                                        <div>
                                            <h3 className="font-bold text-slate-900">{request.fullName}</h3>
                                            <p className="text-xs text-slate-500">ID Utilizador: #{request.id}</p>
                                        </div>
                                    </div>

                                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-y-3 gap-x-6">
                                        <div className="flex items-center gap-2 text-sm text-slate-600">
                                            <Mail className="w-4 h-4 text-slate-400" /> {request.email}
                                        </div>
                                        <div className="flex items-center gap-2 text-sm text-slate-600">
                                            <Phone className="w-4 h-4 text-slate-400" /> {request.phone}
                                        </div>
                                        <div className="flex items-center gap-2 text-sm text-slate-600">
                                            <FileText className="w-4 h-4 text-slate-400" /> CC: {request.citizenCardNumber}
                                        </div>
                                        <div className="flex items-center gap-2 text-sm text-slate-600">
                                            <ExternalLink className="w-4 h-4 text-slate-400" /> Carta: {request.drivingLicense}
                                        </div>
                                    </div>

                                    <div className="bg-slate-50 p-4 rounded-lg border border-slate-100">
                                        <label className="text-[10px] uppercase font-bold text-slate-400 tracking-wider">Motivação para ser Owner</label>
                                        <p className="text-sm text-slate-700 leading-relaxed mt-1">
                                            "{request.motivation}"
                                        </p>
                                    </div>
                                </div>

                                {/* Acciones */}
                                <div className="flex lg:flex-col items-center justify-center gap-3 border-t lg:border-t-0 lg:border-l pt-4 lg:pt-0 lg:pl-6 min-w-[150px]">
                                    <Button 
                                        onClick={() => handleDecision(request.id, 'approve')}
                                        disabled={actionLoading === request.id}
                                        className="w-full bg-green-600 hover:bg-green-700 text-white shadow-sm"
                                    >
                                        {actionLoading === request.id ? <Loader2 className="animate-spin w-4 h-4" /> : <><Check className="w-4 h-4 mr-2" /> Aprovar</>}
                                    </Button>
                                    
                                    <Button 
                                        onClick={() => handleDecision(request.id, 'reject')}
                                        disabled={actionLoading === request.id}
                                        variant="outline"
                                        className="w-full border-red-200 text-red-600 hover:bg-red-50"
                                    >
                                        <X className="w-4 h-4 mr-2" /> Rejeitar
                                    </Button>
                                </div>
                            </div>
                        </Card>
                    ))}
                </div>
            )}
        </div>
    );
}