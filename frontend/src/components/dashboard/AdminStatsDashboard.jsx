import React from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { carService } from '@/services/carService';
import { userService } from '@/services/userService';
import { ownerRequestService } from '@/services/ownerRequestService';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Users, Crown, Car as CarIcon, Clock, Shield, Ban } from 'lucide-react';

export default function AdminStatsDashboard() {
    const queryClient = useQueryClient();

    // Fetch all users
    const { data: users = [] } = useQuery(
        'allUsers',
        () => userService.list().then(res => res.data)
    );

    // Fetch all cars
    const { data: cars = [] } = useQuery(
        'allCars',
        () => carService.searchCars({}).then(res => res.data)
    );

    // Fetch owner requests (pending)
    const { data: ownerRequests = [] } = useQuery(
        'allOwnerRequests',
        () => ownerRequestService.list().then(res => res.data)
    );

    // Mutation to change user role
    const changeRoleMutation = useMutation(
        ({ userId, newRole }) => userService.updateRole(userId, newRole),
        {
            onSuccess: () => {
                queryClient.invalidateQueries('allUsers');
            }
        }
    );

    // Mutation to block user
    const blockUserMutation = useMutation(
        (userId) => userService.toggleStatus(userId),
        {
            onSuccess: () => {
                queryClient.invalidateQueries('allUsers');
            }
        }
    );

    // Calculate stats
    const totalUsers = users.length;
    const totalOwners = users.filter(u => u.role === 'owner' || u.role === 'admin').length;
    const totalCars = cars.length;
    const pendingRequestsCount = ownerRequests.filter(r => r.status === 'pending').length;

    const handleChangeRole = (userId, currentRole) => {
        const roles = ['rental', 'owner', 'admin'];
        const currentIndex = roles.indexOf(currentRole);
        const nextRole = roles[(currentIndex + 1) % roles.length];
        
        if (window.confirm(`Alterar role para "${nextRole}"?`)) {
            changeRoleMutation.mutate({ userId, newRole: nextRole });
        }
    };

    const handleBlockUser = (userId, userName) => {
        if (window.confirm(`Tem certeza que deseja bloquear/desbloquear "${userName}"?`)) {
            blockUserMutation.mutate(userId);
        }
    };

    const getRoleBadgeColor = (role) => {
        switch (role) {
            case 'admin':
                return 'bg-purple-100 text-purple-700';
            case 'owner':
                return 'bg-amber-100 text-amber-700';
            case 'rental':
                return 'bg-blue-100 text-blue-700';
            default:
                return 'bg-slate-100 text-slate-700';
        }
    };

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

            {/* Pending Owner Requests */}
            <h2 className="text-xl font-bold text-slate-900 mb-4">Pedidos Owner Pendentes</h2>
            {pendingRequestsCount === 0 ? (
                <Card className="p-8 text-center border border-slate-200 mb-8">
                    <p className="text-slate-500">Nenhum pedido pendente.</p>
                </Card>
            ) : (
                <div className="space-y-3 mb-8">
                    {ownerRequests
                        .filter(req => req.status === 'pending')
                        .map((request) => (
                            <Card key={request.id} className="p-4 border border-slate-200">
                                <div className="flex items-center justify-between">
                                    <div className="flex items-center gap-4">
                                        <div className="w-12 h-12 bg-amber-100 rounded-full flex items-center justify-center">
                                            <Crown className="w-6 h-6 text-amber-600" />
                                        </div>
                                        <div>
                                            <h3 className="font-semibold text-slate-900">{request.user_name}</h3>
                                            <p className="text-sm text-slate-600">{request.user_email}</p>
                                            <p className="text-xs text-slate-500 mt-1">Carta: {request.drivingLicense}</p>
                                        </div>
                                    </div>
                                    <div className="flex items-center gap-2">
                                        <Clock className="w-4 h-4 text-slate-400" />
                                        <span className="text-sm text-slate-500">
                                            {new Date(request.created_at).toLocaleDateString('pt-PT')}
                                        </span>
                                    </div>
                                </div>
                            </Card>
                        ))}
                </div>
            )}

            {/* User Management */}
            <h2 className="text-xl font-bold text-slate-900 mb-4">Gerir Utilizadores</h2>
            <div className="space-y-3">
                {users.map((user) => (
                    <Card key={user.id} className="p-4 border border-slate-200">
                        <div className="flex items-center justify-between">
                            <div className="flex items-center gap-4">
                                {/* User Avatar */}
                                <div className="w-12 h-12 bg-slate-200 rounded-full flex items-center justify-center">
                                    <Users className="w-6 h-6 text-slate-500" />
                                </div>
                                
                                {/* User Info */}
                                <div>
                                    <div className="flex items-center gap-2 mb-1">
                                        <h3 className="font-semibold text-slate-900">{user.full_name || user.name}</h3>
                                        {user.email === 'admin@minefornow.com' && (
                                            <span className="text-xs text-slate-500 bg-slate-100 px-2 py-0.5 rounded">
                                                (Você)
                                            </span>
                                        )}
                                    </div>
                                    <p className="text-sm text-slate-600">{user.email}</p>
                                </div>
                            </div>

                            {/* Role and Actions */}
                            <div className="flex items-center gap-3">
                                <span className={`px-3 py-1 rounded-md text-sm font-medium ${getRoleBadgeColor(user.role)}`}>
                                    {user.role}
                                </span>
                                
                                {user.email !== 'admin@minefornow.com' && ( // Don't show actions for current admin
                                    <div className="flex gap-2">
                                        <Button
                                            onClick={() => handleChangeRole(user.id, user.role)}
                                            variant="outline"
                                            size="sm"
                                            className="border-slate-300 text-slate-700 hover:bg-slate-50"
                                        >
                                            <Shield className="w-4 h-4 mr-1" />
                                            Alterar Role
                                        </Button>
                                        <Button
                                            onClick={() => handleBlockUser(user.id, user.full_name || user.name)}
                                            variant="outline"
                                            size="sm"
                                            className="border-red-300 text-red-600 hover:bg-red-50"
                                        >
                                            <Ban className="w-4 h-4 mr-1" />
                                            {user.status === 'active' ? 'Bloquear' : 'Desbloquear'}
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
