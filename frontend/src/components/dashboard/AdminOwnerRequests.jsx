import React from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { ownerRequestService } from '@/services/ownerRequestService';

import { useAuth } from '@/contexts/AuthContext';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Check, X, Clock, Crown, Mail, CreditCard, Users } from 'lucide-react';

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
            await ownerRequestService.approve(request.id);
        },
        {
            onSuccess: () => {
                queryClient.invalidateQueries('allOwnerRequests');
                queryClient.invalidateQueries('allUsers');
            },
            onError: (error) => {
                console.error('Error approving request:', error);
                alert('Erro ao aprovar candidatura.');
            }
        }
    );

    // Mutation to reject request
    // Mutation to reject request
    const rejectMutation = useMutation(
        (requestId) => ownerRequestService.reject(requestId),
        {
            onSuccess: () => {
                queryClient.invalidateQueries('allOwnerRequests');
            },
            onError: (error) => {
                console.error('Error rejecting request:', error);
                alert('Erro ao rejeitar candidatura.');
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
                        <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                            <Users className="w-6 h-6 text-blue-600" />
                        </div>
                        <div>
                            <div className="text-2xl font-bold text-slate-900">{requests.length}</div>
                            <div className="text-sm text-slate-500">Total</div>
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
                                            <div className="grid grid-cols-2 gap-x-6 gap-y-1 mt-2 text-sm text-slate-600">
                                                <span className="flex items-center gap-2">
                                                    <Mail className="w-4 h-4 text-slate-400" />
                                                    {request.user_email}
                                                </span>
                                                <span className="flex items-center gap-2">
                                                    <CreditCard className="w-4 h-4 text-slate-400" />
                                                    Carta: {request.drivingLicense}
                                                </span>
                                                <span className="flex items-center gap-2">
                                                    <CreditCard className="w-4 h-4 text-slate-400" />
                                                    NIF/CC: {request.citizenCardNumber || 'N/A'}
                                                </span>
                                                <span className="flex items-center gap-2">
                                                    <Users className="w-4 h-4 text-slate-400" />
                                                    {request.phone || 'Sem telefone'}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                    {getStatusBadge(request.status)}
                                </div>

                                {request.motivation && (
                                    <div className="mb-4 p-4 bg-white rounded-lg border border-slate-200">
                                        <p className="text-sm font-medium text-slate-700 mb-1">Motivação:</p>
                                        <p className="text-sm text-slate-600 italic">"{request.motivation}"</p>
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

            {/* All Requests List */}
            {requests.length > 0 && (
                <div>
                    <h2 className="text-xl font-bold text-slate-900 mb-4">Todos os Pedidos</h2>
                    <div className="space-y-4">
                        {requests.map((request) => (
                            <Card key={request.id} className="p-6 border border-slate-200">
                                <div className="flex items-start justify-between mb-3">
                                    <div className="flex items-center gap-3">
                                        <div className="w-10 h-10 bg-slate-100 rounded-full flex items-center justify-center">
                                            <Crown className="w-5 h-5 text-slate-600" />
                                        </div>
                                        <div>
                                            <h3 className="font-semibold text-slate-900">{request.user_name}</h3>
                                            <div className="flex flex-wrap gap-x-4 gap-y-1 text-xs text-slate-500 mt-1">
                                                <span>{request.user_email}</span>
                                                <span>•</span>
                                                <span>{request.drivingLicense}</span>
                                                <span>•</span>
                                                <span>{request.phone || 'N/A'}</span>
                                            </div>
                                        </div>
                                    </div>
                                    {getStatusBadge(request.status)}
                                </div>

                                <div className="mt-3 text-sm text-slate-600 bg-slate-50 p-3 rounded-md">
                                    <p className="text-xs font-semibold text-slate-500 mb-1">Motivação:</p>
                                    <p className="italic">"{request.motivation || 'Sem motivação'}"</p>
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
