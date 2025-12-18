import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery } from 'react-query';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Car, Calendar, Crown, MapPin, Clock } from 'lucide-react';
import { useAuth } from '@/contexts/AuthContext';
import { ownerRequestService } from '@/services/ownerRequestService';
import { bookingService } from '@/services/bookingService';
import { carService } from '@/services/carService';

export default function RentalDashboard() {
    const navigate = useNavigate();
    const { user } = useAuth();

    // Fetch user bookings
    const { data: bookings = [], isLoading } = useQuery(
        ['myBookings', user?.email],
        () => bookingService.getMyBookings(),
        {
            enabled: !!user,
            refetchInterval: 30000 // Refresh every 30 seconds
        }
    );

    // Check if user has a pending owner request
    const { data: existingRequests = [] } = useQuery(
        ['ownerRequests', user?.email],
        () => ownerRequestService.list().then(res => res.data),
        {
            enabled: !!user
        }
    );

    const hasPendingRequest = existingRequests.some(
        req => req.user_email === user?.email && req.status === 'pending'
    );

    // Calculate stats
    const stats = {
        total: bookings.length,
        pending: bookings.filter(b => b.status === 'WAITING_PAYMENT').length,
        confirmed: bookings.filter(b => b.status === 'CONFIRMED').length,
        active: bookings.filter(b => {
            const now = new Date();
            const start = new Date(b.pickupDate);
            const end = new Date(b.returnDate);
            return b.status === 'CONFIRMED' && start <= now && now <= end;
        }).length
    };

    const getStatusBadge = (status) => {
        const badges = {
            'WAITING_PAYMENT': { text: 'Aguarda Pagamento', class: 'bg-orange-100 text-orange-700' },
            'CONFIRMED': { text: 'Confirmada', class: 'bg-green-100 text-green-700' },
            'CANCELLED': { text: 'Cancelada', class: 'bg-gray-100 text-gray-700' }
        };
        const badge = badges[status] || { text: status, class: 'bg-gray-100 text-gray-700' };
        return (
            <span className={`px-2 py-1 rounded-full text-xs font-medium ${badge.class}`}>
                {badge.text}
            </span>
        );
    };

    return (
        <>
            {/* Stats Cards */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-slate-900 mb-1">{stats.total}</div>
                    <div className="text-sm text-slate-500">Total Reservas</div>
                </Card>
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-orange-500 mb-1">{stats.pending}</div>
                    <div className="text-sm text-slate-500">Pendentes</div>
                </Card>
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-green-500 mb-1">{stats.confirmed}</div>
                    <div className="text-sm text-slate-500">Confirmadas</div>
                </Card>
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-blue-500 mb-1">{stats.active}</div>
                    <div className="text-sm text-slate-500">Em Curso</div>
                </Card>
            </div>

            {/* Active Reservations */}
            <h2 className="text-xl font-bold text-slate-900 mb-4">Minhas Reservas</h2>

            {isLoading ? (
                <Card className="p-12 text-center border border-slate-200 mb-8">
                    <p className="text-slate-500">A carregar reservas...</p>
                </Card>
            ) : bookings.length === 0 ? (
                <Card className="p-12 text-center border border-slate-200 mb-8">
                    <div className="flex flex-col items-center">
                        <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mb-4">
                            <Calendar className="w-8 h-8 text-slate-400" />
                        </div>
                        <h3 className="text-lg font-semibold text-slate-900 mb-2">
                            Sem reservas ativas
                        </h3>
                        <p className="text-slate-500 mb-6">
                            Encontre o carro perfeito para a sua próxima viagem
                        </p>
                        <Button
                            onClick={() => navigate('/cars')}
                            className="bg-slate-900 hover:bg-slate-800 text-white"
                        >
                            Pesquisar Carros
                        </Button>
                    </div>
                </Card>
            ) : (
                <div className="space-y-4 mb-8">
                    {bookings.map((booking) => (
                        <BookingCard key={booking.id} booking={booking} getStatusBadge={getStatusBadge} />
                    ))}
                </div>
            )}

            {/* Become Owner Banner */}
            <Card className="p-6 bg-gradient-to-r from-amber-50 to-orange-50 border border-amber-200">
                <div className="flex items-center justify-between">
                    <div className="flex items-center gap-4">
                        <div className="w-12 h-12 bg-amber-100 rounded-full flex items-center justify-center">
                            <Crown className="w-6 h-6 text-amber-600" />
                        </div>
                        <div>
                            <h3 className="font-semibold text-slate-900 mb-1">
                                Quer rentabilizar o seu carro?
                            </h3>
                            <p className="text-sm text-slate-600">
                                {hasPendingRequest
                                    ? 'O seu pedido está a ser analisado pela nossa equipa'
                                    : 'Torne-se Owner e comece a ganhar dinheiro'
                                }
                            </p>
                        </div>
                    </div>
                    <Button
                        onClick={() => navigate('/become-owner')}
                        disabled={hasPendingRequest}
                        className="bg-orange-500 hover:bg-orange-600 text-white disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {hasPendingRequest ? 'Em Análise' : 'Candidatar-me'}
                    </Button>
                </div>
            </Card>
        </>
    );
}

function BookingCard({ booking, getStatusBadge }) {
    const navigate = useNavigate();

    // Fetch vehicle details
    const { data: vehicle } = useQuery(
        ['vehicle', booking.vehicleId],
        () => carService.getById(booking.vehicleId),
        {
            enabled: !!booking.vehicleId
        }
    );

    const handlePayment = () => {
        navigate(`/payment?bookingId=${booking.id}&carId=${booking.vehicleId}&start=${booking.pickupDate}&end=${booking.returnDate}`);
    };

    return (
        <Card className="p-6 border border-slate-200 hover:shadow-md transition-shadow">
            <div className="flex items-start justify-between mb-4">
                <div className="flex items-start gap-4 flex-1">
                    {vehicle && (
                        <div className="w-24 h-24 bg-slate-100 rounded-lg overflow-hidden flex-shrink-0">
                            <img
                                src={vehicle.images?.[0] || vehicle.image_url || '/placeholder-car.jpg'}
                                alt={`${vehicle.brand} ${vehicle.model}`}
                                className="w-full h-full object-cover"
                            />
                        </div>
                    )}
                    <div className="flex-1">
                        <div className="flex items-start justify-between mb-2">
                            <div>
                                <h3 className="font-semibold text-slate-900 text-lg">
                                    {vehicle ? `${vehicle.brand} ${vehicle.model}` : 'A carregar...'}
                                </h3>
                                {vehicle && (
                                    <div className="flex items-center gap-2 text-slate-500 text-sm mt-1">
                                        <MapPin className="w-4 h-4" />
                                        <span>{vehicle.city}</span>
                                    </div>
                                )}
                            </div>
                            {getStatusBadge(booking.status)}
                        </div>

                        <div className="flex items-center gap-4 text-sm text-slate-600 mt-3">
                            <div className="flex items-center gap-2">
                                <Calendar className="w-4 h-4" />
                                <span>{booking.pickupDate}</span>
                            </div>
                            <span>→</span>
                            <div className="flex items-center gap-2">
                                <Calendar className="w-4 h-4" />
                                <span>{booking.returnDate}</span>
                            </div>
                        </div>

                        <div className="mt-3 text-lg font-semibold text-slate-900">
                            €{booking.totalPrice?.toFixed(2)}
                        </div>
                    </div>
                </div>
            </div>

            {booking.status === 'WAITING_PAYMENT' && (
                <div className="flex gap-3 mt-4 pt-4 border-t border-slate-200">
                    <Button
                        onClick={handlePayment}
                        className="flex-1 bg-indigo-600 hover:bg-indigo-700 text-white"
                    >
                        Pagar Agora
                    </Button>
                </div>
            )}
        </Card>
    );
}
