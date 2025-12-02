import React, { useState } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { User } from 'lucide-react';

export default function ProfileSettings() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        fullName: user?.full_name || '',
        email: user?.email || '',
        phone: user?.phone || '',
        drivingLicense: user?.driving_license || ''
    });

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log('Guardar alterações:', formData);
        // TODO: Implement save logic
    };

    const handleLogout = () => {
        if (window.confirm('Tem certeza que deseja sair da sua conta?')) {
            logout();
            navigate('/');
        }
    };

    return (
        <div className="space-y-6">
            {/* Personal Information Card */}
            <Card className="p-6 border border-slate-200">
                <div className="flex items-center gap-2 mb-6">
                    <User className="w-5 h-5 text-slate-600" />
                    <h2 className="text-lg font-semibold text-slate-900">Informação Pessoal</h2>
                </div>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-slate-900 mb-2">
                            Nome Completo
                        </label>
                        <Input
                            value={formData.fullName}
                            onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                            className="bg-white border-slate-300"
                            disabled
                        />
                        <p className="text-xs text-slate-500 mt-1">O nome não pode ser alterado</p>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-slate-900 mb-2">
                            Email
                        </label>
                        <Input
                            type="email"
                            value={formData.email}
                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                            className="bg-white border-slate-300"
                            disabled
                        />
                        <p className="text-xs text-slate-500 mt-1">O email não pode ser alterado</p>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-slate-900 mb-2">
                            Telefone
                        </label>
                        <Input
                            type="tel"
                            value={formData.phone}
                            onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                            placeholder="+351 912 345 678"
                            className="bg-white border-slate-300"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-slate-900 mb-2">
                            ID da Carta de Condução
                        </label>
                        <Input
                            value={formData.drivingLicense}
                            onChange={(e) => setFormData({ ...formData, drivingLicense: e.target.value })}
                            placeholder="AB123456"
                            className="bg-white border-slate-300"
                        />
                    </div>

                    <Button 
                        type="submit"
                        className="bg-indigo-600 hover:bg-indigo-700 text-white"
                    >
                        Guardar Alterações
                    </Button>
                </form>
            </Card>

            {/* Logout Section */}
            <Card className="p-6 bg-red-50 border border-red-200">
                <div>
                    <h3 className="text-base font-semibold text-red-900 mb-1">
                        Terminar Sessão
                    </h3>
                    <p className="text-sm text-red-700 mb-4">
                        Sair da sua conta MineForNow
                    </p>
                    <Button 
                        onClick={handleLogout}
                        variant="outline"
                        className="border-red-300 text-red-600 hover:bg-red-100"
                    >
                        Sair
                    </Button>
                </div>
            </Card>
        </div>
    );
}
