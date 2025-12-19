import React from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { carService } from '@/services/carService';
import { userService } from '@/services/userService';
import { adminService } from '@/services/adminService';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Users, Crown, Clock, Shield, Ban, Loader2 } from 'lucide-react';

export default function AdminStatsDashboard() {
    const queryClient = useQueryClient();

    const { data: users = [], isLoading: loadingUsers } = useQuery(
        'allUsers',
        () => userService.list().then(res => res.data)
    );


    const { data: cars = [] } = useQuery(
        'allCars',
        () => carService.searchCars({}).then(res => res.data)
    );


    const { data: ownerRequests = [] } = useQuery(
        'allOwnerRequests',
        () => adminService.getPendingRequests()
    );

    // Mutaciones
    const changeRoleMutation = useMutation(
        ({ userId, newRole }) => userService.updateRole(userId, newRole),
        { onSuccess: () => queryClient.invalidateQueries('allUsers') }
    );

    const blockUserMutation = useMutation(
        (userId) => userService.toggleStatus(userId),
        { onSuccess: () => queryClient.invalidateQueries('allUsers') }
    );

    const totalUsers = users.length;
    const totalOwners = users.filter(u => u.role?.toUpperCase() === 'OWNER' || u.role?.toUpperCase() === 'ADMIN').length;
    const totalCars = cars.length;
    // adminService ya filtra por pendientes, usamos el length directamente
    const pendingRequestsCount = ownerRequests.length;

    const handleChangeRole = (userId, currentRole) => {
        const roles = ['RENTER', 'OWNER', 'ADMIN'];
        const currentIndex = roles.indexOf(currentRole?.toUpperCase());
        const nextRole = roles[(currentIndex + 1) % roles.length];
        
        if (window.confirm(`Alterar role para "${nextRole}"?`)) {
            changeRoleMutation.mutate({ userId, newRole: nextRole });
        }
    };

    const getRoleBadgeColor = (role) => {
        switch (role?.toUpperCase()) {
            case 'ADMIN': return 'bg-purple-100 text-purple-700';
            case 'OWNER': return 'bg-amber-100 text-amber-700';
            case 'RENTER': return 'bg-blue-100 text-blue-700';
            default: return 'bg-slate-100 text-slate-700';
        }
    };

    if (loadingUsers) return <div className="flex justify-center p-10"><Loader2 className="animate-spin" /></div>;

    return (
        <>
            {/* Stats Cards */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-slate-900 mb-1">{totalUsers}</div>
                    <div className="text-sm text-slate-500">Utilizadores</div>
                </Card>
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-amber-600 mb-1">{totalOwners}</div>
                    <div className="text-sm text-slate-500">Owners</div>
                </Card>
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-blue-600 mb-1">{totalCars}</div>
                    <div className="text-sm text-slate-500">Carros</div>
                </Card>
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-orange-500 mb-1">{pendingRequestsCount}</div>
                    <div className="text-sm text-slate-500">Pedidos Pendentes</div>
                </Card>
            </div>

            {/* Listado de Pedidos Pendientes */}
            <h2 className="text-xl font-bold text-slate-900 mb-4">Pedidos Owner Pendentes</h2>
            {pendingRequestsCount === 0 ? (
                <Card className="p-8 text-center border border-slate-200 mb-8">
                    <p className="text-slate-500">Nenhum pedido pendente.</p>
                </Card>
            ) : (
                <div className="space-y-3 mb-8">
                    {ownerRequests.map((request) => (
                        <Card key={request.id} className="p-4 border border-slate-200">
                            <div className="flex items-center justify-between">
                                <div className="flex items-center gap-4">
                                    <div className="w-12 h-12 bg-amber-100 rounded-full flex items-center justify-center">
                                        <Crown className="w-6 h-6 text-amber-600" />
                                    </div>
                                    <div>
                                        <h3 className="font-semibold text-slate-900">{request.fullName}</h3>
                                        <p className="text-sm text-slate-600">{request.email}</p>
                                        <p className="text-xs text-slate-400 mt-1">CC: {request.citizenCardNumber}</p>
                                    </div>
                                </div>
                                <div className="text-right">
                                    <span className="text-xs bg-orange-100 text-orange-700 px-2 py-1 rounded">Aguardando</span>
                                </div>
                            </div>
                        </Card>
                    ))}
                </div>
            )}

            {/* Gestión de Usuarios */}
            <h2 className="text-xl font-bold text-slate-900 mb-4">Gerir Utilizadores</h2>
            <div className="space-y-3">
                {users.map((user) => (
                    <Card key={user.id} className="p-4 border border-slate-200">
                        <div className="flex items-center justify-between">
                            <div className="flex items-center gap-4">
                                <div className="w-10 h-10 bg-slate-100 rounded-full flex items-center justify-center">
                                    <Users className="w-5 h-5 text-slate-400" />
                                </div>
                                <div>
                                    <h3 className="font-semibold text-slate-900">{user.fullName || user.full_name}</h3>
                                    <p className="text-sm text-slate-500">{user.email}</p>
                                </div>
                            </div>

                            <div className="flex items-center gap-3">
                                <span className={`px-3 py-1 rounded-md text-xs font-bold uppercase ${getRoleBadgeColor(user.role)}`}>
                                    {user.role}
                                </span>
                                
                                {user.email !== 'admin@gmail.com' && (
                                    <div className="flex gap-2">
                                        <Button
                                            onClick={() => handleChangeRole(user.id, user.role)}
                                            variant="outline" size="sm"
                                        >
                                            <Shield className="w-4 h-4 mr-1" /> Role
                                        </Button>
                                        <Button
                                            onClick={() => handleBlockUser(user.id, user.fullName)}
                                            variant="outline" size="sm"
                                            className="text-red-600 border-red-200 hover:bg-red-50"
                                        >
                                            <Ban className="w-4 h-4 mr-1" /> Status
                                        </Button>
                                    </div>
                                )}
                            </div>
                        </div>
                    </Card>
                ))}
            </div>
        </>
    );
}