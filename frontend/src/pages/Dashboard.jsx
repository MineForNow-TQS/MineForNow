import React, { useState } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Crown, Car, Calendar, Settings, BarChart3 } from "lucide-react";
import ProfileSettings from '@/components/dashboard/ProfileSettings';
import RentalDashboard from '@/components/dashboard/RentalDashboard';
import OwnerReservationsDashboard from '@/components/dashboard/OwnerReservationsDashboard';
import OwnerCarsDashboard from '@/components/dashboard/OwnerCarsDashboard';
import AdminStatsDashboard from '@/components/dashboard/AdminStatsDashboard';
import AdminOwnerRequests from '@/components/dashboard/AdminOwnerRequests';

export default function Dashboard() {
    const { user } = useAuth();
    const navigate = useNavigate();
    const isAdmin = user?.role === 'admin';
    const isOwner = user?.role === 'owner' || user?.role === 'admin';

    // Set default tab based on role
    const getDefaultTab = () => {
        if (isAdmin) return 'estatisticas';
        if (isOwner) return 'carros';
        return 'reservas';
    };

    const [activeTab, setActiveTab] = useState(getDefaultTab());

    if (!user) {
        return (
            <div className="min-h-screen bg-slate-50 flex items-center justify-center">
                <Card className="p-8 text-center">
                    <h2 className="text-xl font-semibold mb-4">Acesso Restrito</h2>
                    <p className="text-slate-600 mb-4">Precisa fazer login para aceder ao painel</p>
                </Card>
            </div>
        );
    }


    return (
        <div className="min-h-screen bg-slate-50">
            {/* Hero Section */}
            <div className="bg-slate-900 text-white py-8">
                <div className="max-w-7xl mx-auto px-4 sm:px-6">
                    <div className="flex items-center justify-between mb-6">
                        <div>
                            <h1 className="text-3xl font-bold mb-2">Olá, {user.fullName || user.full_name}</h1>
                            <span className="inline-block px-3 py-1 bg-indigo-600 text-white text-sm rounded-md">
                                {isAdmin ? 'Admin' : isOwner ? 'Owner' : 'Rental'}
                            </span>
                        </div>
                        <Button
                            onClick={() => navigate('/cars')}
                            className="bg-slate-800 hover:bg-slate-700 text-white border border-slate-700"
                        >
                            <Car className="w-4 h-4 mr-2" />
                            Ver Carros
                        </Button>
                    </div>
                </div>
            </div>

            {/* Main Content */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
                {/* Tabs - Different for each role */}
                <div className="flex gap-4 mb-6 border-b border-slate-200">
                    {/* RENTAL: Reservas + Definições */}
                    {!isOwner && (
                        <>
                            <button
                                onClick={() => setActiveTab('reservas')}
                                className={`pb-3 px-2 font-medium transition-colors relative ${activeTab === 'reservas'
                                    ? 'text-slate-900'
                                    : 'text-slate-500 hover:text-slate-700'
                                    }`}
                            >
                                <Calendar className="w-4 h-4 inline-block mr-2" />
                                Minhas Reservas
                                {activeTab === 'reservas' && (
                                    <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-slate-900"></div>
                                )}
                            </button>
                            <button
                                onClick={() => setActiveTab('definicoes')}
                                className={`pb-3 px-2 font-medium transition-colors relative ${activeTab === 'definicoes'
                                    ? 'text-slate-900'
                                    : 'text-slate-500 hover:text-slate-700'
                                    }`}
                            >
                                <Settings className="w-4 h-4 inline-block mr-2" />
                                Definições
                                {activeTab === 'definicoes' && (
                                    <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-slate-900"></div>
                                )}
                            </button>
                        </>
                    )}

                    {/* OWNER (não admin): Reservas + Meus Carros + Definições */}
                    {isOwner && !isAdmin && (
                        <>
                            <button
                                onClick={() => setActiveTab('reservas')}
                                className={`pb-3 px-2 font-medium transition-colors relative ${activeTab === 'reservas'
                                    ? 'text-slate-900'
                                    : 'text-slate-500 hover:text-slate-700'
                                    }`}
                            >
                                <Calendar className="w-4 h-4 inline-block mr-2" />
                                Reservas
                                {activeTab === 'reservas' && (
                                    <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-slate-900"></div>
                                )}
                            </button>
                            <button
                                onClick={() => setActiveTab('carros')}
                                className={`pb-3 px-2 font-medium transition-colors relative ${activeTab === 'carros'
                                    ? 'text-slate-900'
                                    : 'text-slate-500 hover:text-slate-700'
                                    }`}
                            >
                                <Car className="w-4 h-4 inline-block mr-2" />
                                Meus Carros
                                {activeTab === 'carros' && (
                                    <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-slate-900"></div>
                                )}
                            </button>
                            <button
                                onClick={() => setActiveTab('definicoes')}
                                className={`pb-3 px-2 font-medium transition-colors relative ${activeTab === 'definicoes'
                                    ? 'text-slate-900'
                                    : 'text-slate-500 hover:text-slate-700'
                                    }`}
                            >
                                <Settings className="w-4 h-4 inline-block mr-2" />
                                Definições
                                {activeTab === 'definicoes' && (
                                    <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-slate-900"></div>
                                )}
                            </button>
                        </>
                    )}

                    {/* ADMIN: Estatísticas + Pedidos Owner + Meus Carros + Definições */}
                    {isAdmin && (
                        <>
                            <button
                                onClick={() => setActiveTab('estatisticas')}
                                className={`pb-3 px-2 font-medium transition-colors relative ${activeTab === 'estatisticas'
                                    ? 'text-slate-900'
                                    : 'text-slate-500 hover:text-slate-700'
                                    }`}
                            >
                                <BarChart3 className="w-4 h-4 inline-block mr-2" />
                                Estatísticas
                                {activeTab === 'estatisticas' && (
                                    <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-slate-900"></div>
                                )}
                            </button>
                            <button
                                onClick={() => setActiveTab('pedidos')}
                                className={`pb-3 px-2 font-medium transition-colors relative ${activeTab === 'pedidos'
                                    ? 'text-slate-900'
                                    : 'text-slate-500 hover:text-slate-700'
                                    }`}
                            >
                                <Crown className="w-4 h-4 inline-block mr-2" />
                                Pedidos Owner
                                {activeTab === 'pedidos' && (
                                    <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-slate-900"></div>
                                )}
                            </button>
                            <button
                                onClick={() => setActiveTab('carros')}
                                className={`pb-3 px-2 font-medium transition-colors relative ${activeTab === 'carros'
                                    ? 'text-slate-900'
                                    : 'text-slate-500 hover:text-slate-700'
                                    }`}
                            >
                                <Car className="w-4 h-4 inline-block mr-2" />
                                Meus Carros
                                {activeTab === 'carros' && (
                                    <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-slate-900"></div>
                                )}
                            </button>
                            <button
                                onClick={() => setActiveTab('definicoes')}
                                className={`pb-3 px-2 font-medium transition-colors relative ${activeTab === 'definicoes'
                                    ? 'text-slate-900'
                                    : 'text-slate-500 hover:text-slate-700'
                                    }`}
                            >
                                <Settings className="w-4 h-4 inline-block mr-2" />
                                Definições
                                {activeTab === 'definicoes' && (
                                    <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-slate-900"></div>
                                )}
                            </button>
                        </>
                    )}
                </div>

                {/* Tab Content */}
                {activeTab === 'reservas' && !isOwner && <RentalDashboard />}
                {activeTab === 'reservas' && isOwner && <OwnerReservationsDashboard />}
                {activeTab === 'carros' && isOwner && <OwnerCarsDashboard />}
                {activeTab === 'estatisticas' && isAdmin && <AdminStatsDashboard />}
                {activeTab === 'pedidos' && isAdmin && <AdminOwnerRequests />}
                {activeTab === 'definicoes' && <ProfileSettings />}
            </div>
        </div>
    );
}
