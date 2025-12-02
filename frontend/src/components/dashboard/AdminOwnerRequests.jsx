import React from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { ownerRequestService } from '@/services/ownerRequestService';
import { userService } from '@/services/userService';
import { useAuth } from '@/contexts/AuthContext';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Check, X, Clock, Crown, Mail, CreditCard } from 'lucide-react';

export default function AdminOwnerRequests() {
    const { user } = useAuth();
    const queryClient = useQueryClient();

    // Fetch owner requests
    const { data: requests = [], isLoading } = useQuery(
        'allOwnerRequests',
        () => ownerRequestService.list().then(res => res.data)
    );

    // Mutation to approve request
    const approveMutation = useMutation(
        async (request) => {
            // Update request status
            await ownerRequestService.update(request.id, { status: 'approved' });
            
            // Update user role to owner
            try {
                const userResponse = await userService.getByEmail(request.user_email);
                await userService.updateRole(userResponse.data.id, 'owner');
            } catch (error) {
                console.error('Error updating user role:', error);
            }
        },
        {
            onSuccess: () => {
                queryClient.invalidateQueries('allOwnerRequests');
                queryClient.invalidateQueries('allUsers');
            }
        }
    );

    // Mutation to reject request
    const rejectMutation = useMutation(
        (requestId) => ownerRequestService.update(requestId, { status: 'rejected' }),
        {
            onSuccess: () => {
                queryClient.invalidateQueries('allOwnerRequests');
            }
        }
    );

    const handleApprove = (request) => {
        if (window.confirm('Tem certeza que deseja aprovar esta candidatura?')) {
            approveMutation.mutate(request);
        }
    };

    const handleReject = (requestId) => {
        if (window.confirm('Tem certeza que deseja recusar esta candidatura?')) {
            rejectMutation.mutate(requestId);
        }
    };

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString('pt-PT', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const getStatusBadge = (status) => {
        const badges = {
            pending: { color: 'bg-yellow-100 text-yellow-800', icon: Clock, label: 'Pendente' },
            approved: { color: 'bg-green-100 text-green-800', icon: Check, label: 'Aprovado' },
            rejected: { color: 'bg-red-100 text-red-800', icon: X, label: 'Recusado' }
        };
        const badge = badges[status] || badges.pending;
        const Icon = badge.icon;
        
        return (
            <span className={`inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-medium ${badge.color}`}>
                <Icon className="w-3 h-3" />
                {badge.label}
            </span>
        );
    };

    const pendingRequests = requests.filter(req => req.status === 'pending');
    const reviewedRequests = requests.filter(req => req.status !== 'pending');

    if (isLoading) {
        return (
            <div className="flex items-center justify-center py-12">
                <div className="text-slate-600">A carregar pedidos...</div>
            </div>
        );
    }

    return (
        <div className="space-y-6">
            {/* Stats */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <Card className="p-4 border border-slate-200">
                    <div className="flex items-center gap-3">
                        <div className="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center">
                            <Clock className="w-6 h-6 text-yellow-600" />
                        </div>
                        <div>
                            <div className="text-2xl font-bold text-slate-900">{pendingRequests.length}</div>
                            <div className="text-sm text-slate-500">Pendentes</div>
                        </div>
                    </div>
                </Card>
                <Card className="p-4 border border-slate-200">
                    <div className="flex items-center gap-3">
                        <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                            <Check className="w-6 h-6 text-green-600" />
                        </div>
                        <div>
                            <div className="text-2xl font-bold text-slate-900">
                                {requests.filter(r => r.status === 'approved').length}
                            </div>
                            <div className="text-sm text-slate-500">Aprovados</div>
                        </div>
                    </div>
                </Card>
                <Card className="p-4 border border-slate-200">
                    <div className="flex items-center gap-3">
                        <div className="w-12 h-12 bg-red-100 rounded-lg flex items-center justify-center">
                            <X className="w-6 h-6 text-red-600" />
                        </div>
                        <div>
                            <div className="text-2xl font-bold text-slate-900">
                                {requests.filter(r => r.status === 'rejected').length}
                            </div>
                            <div className="text-sm text-slate-500">Recusados</div>
                        </div>
                    </div>
                </Card>
            </div>

            {/* Pending Requests */}
            {pendingRequests.length > 0 && (
                <div>
                    <h2 className="text-xl font-bold text-slate-900 mb-4">Pedidos Pendentes</h2>
                    <div className="space-y-4">
                        {pendingRequests.map((request) => (
                            <Card key={request.id} className="p-6 border border-yellow-200 bg-yellow-50">
                                <div className="flex items-start justify-between mb-4">
                                    <div className="flex items-center gap-3">
                                        <div className="w-12 h-12 bg-indigo-100 rounded-full flex items-center justify-center">
                                            <Crown className="w-6 h-6 text-indigo-600" />
                                        </div>
                                        <div>
                                            <h3 className="font-semibold text-slate-900">{request.user_name}</h3>
                                            <div className="flex items-center gap-4 text-sm text-slate-600 mt-1">
                                                <span className="flex items-center gap-1">
                                                    <Mail className="w-4 h-4" />
                                                    {request.user_email}
                                                </span>
                                                <span className="flex items-center gap-1">
                                                    <CreditCard className="w-4 h-4" />
                                                    {request.driving_license}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                    {getStatusBadge(request.status)}
                                </div>

                                {request.reason && (
                                    <div className="mb-4 p-4 bg-white rounded-lg border border-slate-200">
                                        <p className="text-sm font-medium text-slate-700 mb-1">Motivo:</p>
                                        <p className="text-sm text-slate-600">{request.reason}</p>
                                    </div>
                                )}

                                <div className="flex items-center justify-between pt-4 border-t border-yellow-200">
                                    <span className="text-xs text-slate-500">
                                        Submetido em {formatDate(request.created_at)}
                                    </span>
                                    <div className="flex gap-2">
                                        <Button
                                            onClick={() => handleReject(request.id)}
                                            variant="outline"
                                            className="border-red-300 text-red-600 hover:bg-red-50"
                                            disabled={rejectMutation.isLoading}
                                        >
                                            <X className="w-4 h-4 mr-1" />
                                            Recusar
                                        </Button>
                                        <Button
                                            onClick={() => handleApprove(request)}
                                            className="bg-green-600 hover:bg-green-700 text-white"
                                            disabled={approveMutation.isLoading}
                                        >
                                            <Check className="w-4 h-4 mr-1" />
                                            Aprovar
                                        </Button>
                                    </div>
                                </div>
                            </Card>
                        ))}
                    </div>
                </div>
            )}

            {/* Reviewed Requests */}
            {reviewedRequests.length > 0 && (
                <div>
                    <h2 className="text-xl font-bold text-slate-900 mb-4">Histórico de Pedidos</h2>
                    <div className="space-y-4">
                        {reviewedRequests.map((request) => (
                            <Card key={request.id} className="p-6 border border-slate-200">
                                <div className="flex items-start justify-between mb-3">
                                    <div className="flex items-center gap-3">
                                        <div className="w-10 h-10 bg-slate-100 rounded-full flex items-center justify-center">
                                            <Crown className="w-5 h-5 text-slate-600" />
                                        </div>
                                        <div>
                                            <h3 className="font-semibold text-slate-900">{request.user_name}</h3>
                                            <div className="flex items-center gap-3 text-xs text-slate-500 mt-1">
                                                <span>{request.user_email}</span>
                                                <span>•</span>
                                                <span>{request.driving_license}</span>
                                            </div>
                                        </div>
                                    </div>
                                    {getStatusBadge(request.status)}
                                </div>
                                
                                <div className="text-xs text-slate-500 pt-3 border-t border-slate-200">
                                    Submetido em {formatDate(request.created_at)}
                                </div>
                            </Card>
                        ))}
                    </div>
                </div>
            )}

            {requests.length === 0 && (
                <Card className="p-12 text-center border border-slate-200">
                    <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <Crown className="w-8 h-8 text-slate-400" />
                    </div>
                    <h3 className="text-lg font-semibold text-slate-900 mb-2">
                        Sem pedidos
                    </h3>
                    <p className="text-slate-500">
                        Ainda não há candidaturas de Owner para rever.
                    </p>
                </Card>
            )}
        </div>
    );
}
