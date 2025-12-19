import React, { useState } from 'react'; // 1. Adicionado useState
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { carService } from '@/services/carService';
import { userService } from '@/services/userService';
import { ownerRequestService } from '@/services/ownerRequestService';
import { adminService } from '@/services/adminService';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input'; // Certifica-te que este componente existe
import { Users, Crown, Clock, Shield, Ban, Check, X, Search, CheckCircle } from 'lucide-react';

export default function AdminStatsDashboard() {
    const queryClient = useQueryClient();
    
    // 2. Estado para a pesquisa
    const [searchTerm, setSearchTerm] = useState('');

    // 3. Fetch de utilizadores com suporte a pesquisa
    const { data: users = [] } = useQuery(
        ['allUsers', searchTerm], 
        () => userService.list(searchTerm).then(res => res.data),
        { keepPreviousData: true }
    );

    const { data: stats } = useQuery('adminStats', () => adminService.getDashboardStats());
    const { data: cars = [] } = useQuery('allCars', () => carService.searchCars({}).then(res => res.data));
    const { data: ownerRequests = [] } = useQuery('allOwnerRequests', () => ownerRequestService.list().then(res => res.data));

    // MUTAÇÕES
    const approveRequestMutation = useMutation(userId => ownerRequestService.approve(userId), {
        onSuccess: () => {
            queryClient.invalidateQueries('allOwnerRequests');
            queryClient.invalidateQueries('allUsers');
        }
    });

    const rejectRequestMutation = useMutation(userId => ownerRequestService.reject(userId), {
        onSuccess: () => queryClient.invalidateQueries('allOwnerRequests')
    });

    const blockUserMutation = useMutation(userId => userService.toggleStatus(userId), {
        onSuccess: () => queryClient.invalidateQueries('allUsers')
    });

    // HANDLERS
    const handleBlockUser = (userId, userName, isActive) => {
        const action = isActive ? 'BLOQUEAR' : 'DESBLOQUEAR';
        if (window.confirm(`Tem certeza que deseja ${action} o utilizador "${userName}"?`)) {
            blockUserMutation.mutate(userId);
        }
    };

    const getRoleBadgeColor = (role) => {
        switch (role?.toLowerCase()) {
            case 'admin': return 'bg-purple-100 text-purple-700';
            case 'owner': return 'bg-amber-100 text-amber-700';
            default: return 'bg-blue-100 text-blue-700';
        }
    };

    return (
        <div className="p-6">
            {/* Stats Cards (Mantém-se igual) */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
                <Card className="p-6 text-center border">
                    <div className="text-3xl font-bold">{stats?.totalUsers || 0}</div>
                    <div className="text-sm text-slate-500">Utilizadores</div>
                </Card>
                <Card className="p-6 text-center border">
                    <div className="text-3xl font-bold text-blue-600">{stats?.totalCars || 0}</div>
                    <div className="text-sm text-slate-500">Carros</div>
                </Card>
                <Card className="p-6 text-center border">
                    <div className="text-3xl font-bold text-amber-600">{stats?.totalBookings || 0}</div>
                    <div className="text-sm text-slate-500">Reservas</div>
                </Card>
                <Card className="p-6 text-center border">
                    <div className="text-3xl font-bold text-emerald-600">
                        {new Intl.NumberFormat('pt-PT', { style: 'currency', currency: 'EUR' }).format(stats?.totalRevenue || 0)}
                    </div>
                    <div className="text-sm text-slate-500">Receita Total</div>
                </Card>
            </div>

            {/* Gestão de Pedidos (Owner Requests) */}
            <h2 className="text-xl font-bold mb-4">Pedidos Owner Pendentes</h2>
            <div className="space-y-3 mb-8">
                {ownerRequests.filter(r => r.status === 'pending').map(request => (
                    <Card key={request.id} className="p-4 border">
                        <div className="flex items-center justify-between">
                            <div className="flex items-center gap-4">
                                <div className="w-10 h-10 bg-amber-100 rounded-full flex items-center justify-center">
                                    <Crown className="w-5 h-5 text-amber-600" />
                                </div>
                                <div>
                                    <h3 className="font-semibold">{request.user_name || request.fullName}</h3>
                                    <p className="text-xs text-slate-500">Email: {request.user_email || request.email}</p>
                                </div>
                            </div>
                            <div className="flex gap-2">
                                <Button size="sm" className="bg-green-600" onClick={() => approveRequestMutation.mutate(request.id)}>
                                    <Check className="w-4 h-4 mr-1" /> Aprovar
                                </Button>
                                <Button size="sm" variant="outline" className="text-red-600" onClick={() => rejectRequestMutation.mutate(request.id)}>
                                    <X className="w-4 h-4 mr-1" /> Rejeitar
                                </Button>
                            </div>
                        </div>
                    </Card>
                ))}
            </div>

            {/* --- PESQUISA E GESTÃO DE UTILIZADORES --- */}
            <div className="flex items-center justify-between mb-4">
                <h2 className="text-xl font-bold">Gerir Utilizadores</h2>
                <div className="relative w-64">
                    <Search className="absolute left-2 top-2.5 h-4 w-4 text-slate-400" />
                    <Input 
                        placeholder="Pesquisar nome ou email..." 
                        className="pl-8"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>
            </div>

            <div className="space-y-3">
                {users.map((user) => (
                    <Card key={user.id} className={`p-4 border ${!user.active ? 'bg-red-50' : ''}`}>
                        <div className="flex items-center justify-between">
                            <div className="flex items-center gap-4">
                                <div className={`w-12 h-12 rounded-full flex items-center justify-center ${!user.active ? 'bg-red-200' : 'bg-slate-200'}`}>
                                    <span className="font-bold">{(user.fullName || '?')[0]}</span>
                                </div>
                                <div>
                                    <div className="flex items-center gap-2">
                                        <h3 className="font-semibold">{user.fullName}</h3>
                                        {!user.active && <span className="text-[10px] bg-red-600 text-white px-1.5 rounded">BLOQUEADO</span>}
                                    </div>
                                    <p className="text-sm text-slate-500">{user.email}</p>
                                </div>
                            </div>

                            <div className="flex items-center gap-3">
                                <span className={`px-2 py-1 rounded text-xs font-bold uppercase ${getRoleBadgeColor(user.role)}`}>
                                    {user.role}
                                </span>
                                {user.email !== 'admin@minefornow.com' && (
                                    <Button
                                        onClick={() => handleBlockUser(user.id, user.fullName, user.active)}
                                        variant="outline"
                                        size="sm"
                                        className={user.active ? "text-red-600 border-red-200" : "text-green-600 border-green-200"}
                                    >
                                        {user.active ? <><Ban className="w-4 h-4 mr-1" /> Bloquear</> : <><CheckCircle className="w-4 h-4 mr-1" /> Ativar</>}
                                    </Button>
                                )}
                            </div>
                        </div>
                    </Card>
                ))}
            </div>
        </div>
    );
}