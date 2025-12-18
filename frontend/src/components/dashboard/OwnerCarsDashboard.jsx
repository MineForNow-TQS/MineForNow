import React from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { carService } from '@/services/carService';
import { dashboardService } from '@/services/dashboardService';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Car, Plus, Edit, Trash2, MapPin, Eye } from 'lucide-react';

export default function OwnerCarsDashboard() {
    const navigate = useNavigate();
    const queryClient = useQueryClient();

    // Fetch cars owned by this user (using JWT token)
    const { data: cars = [], isLoading: carsLoading } = useQuery(
        ['ownerCars'],
        () => carService.getCarsByOwner().then(res => res.data)
    );

    // Fetch dashboard statistics
    const { data: stats, isLoading: statsLoading, error: statsError } = useQuery(
        ['ownerStats'],
        () => dashboardService.getOwnerStats(),
        {
            retry: 1,
            onError: (error) => {
                console.error('Failed to fetch dashboard stats:', error);
            }
        }
    );

    // Fetch active bookings list (confirmed rentals)
    const { data: activeBookingsList = [], isLoading: activeLoading } = useQuery(
        ['ownerActiveBookings'],
        () => dashboardService.getActiveBookings(),
        {
            retry: 1,
            onError: (error) => {
                console.error('Failed to fetch active bookings:', error);
            }
        }
    );

    // Mutation to delete car
    const deleteMutation = useMutation(
        (carId) => carService.deleteCar(carId),
        {
            onSuccess: () => {
                queryClient.invalidateQueries(['ownerCars']);
                queryClient.invalidateQueries(['ownerStats']);
            }
        }
    );

    const handleDelete = (carId, carName) => {
        if (window.confirm(`Tem certeza que deseja eliminar o carro "${carName}"?`)) {
            deleteMutation.mutate(carId);
        }
    };

    // Use stats from API or fallback to defaults
    const totalCars = stats?.activeVehicles ?? cars.length;
    const pendingReservations = stats?.pendingBookings ?? 0;
    const completedReservations = stats?.completedBookings ?? 0;
    const totalEarnings = stats?.totalRevenue ?? 0;

    const isLoading = carsLoading || statsLoading;

    if (isLoading) {
        return <div className="text-center py-8">A carregar...</div>;
    }

    return (
        <>
            {/* Stats Cards */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-slate-900 mb-1">{totalCars}</div>
                    <div className="text-sm text-slate-500">Meus Carros</div>
                </Card>
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-orange-500 mb-1">{pendingReservations}</div>
                    <div className="text-sm text-slate-500">Pendentes</div>
                </Card>
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-green-500 mb-1">{completedReservations}</div>
                    <div className="text-sm text-slate-500">Pagas</div>
                </Card>
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-blue-500 mb-1">{totalEarnings.toFixed(2)} €</div>
                    <div className="text-sm text-slate-500">Ganhos Totais</div>
                </Card>
            </div>

            {/* Active Reservations */}
            <h2 className="text-xl font-bold text-slate-900 mb-4">Reservas em Curso</h2>
            {activeLoading ? (
                <Card className="p-8 text-center border border-slate-200 mb-8">
                    <p className="text-slate-500">A carregar reservas...</p>
                </Card>
            ) : activeBookingsList.length === 0 ? (
                <Card className="p-8 text-center border border-slate-200 mb-8">
                    <p className="text-slate-500">Nenhuma reserva em curso.</p>
                </Card>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
                    {activeBookingsList.map((booking) => {
                        const vehicle = cars.find(c => c.id === booking.vehicleId);
                        const today = new Date();
                        today.setHours(0, 0, 0, 0);
                        const pickupDate = new Date(booking.pickupDate);
                        const returnDate = new Date(booking.returnDate);

                        // Determine booking status based on dates
                        const isFuture = pickupDate > today;
                        const isCompleted = returnDate < today;
                        const isInProgress = !isFuture && !isCompleted;
                        const isPaid = booking.status === 'CONFIRMED';

                        let statusText = 'Em Curso';
                        let statusColor = 'bg-blue-500';
                        if (isFuture) {
                            statusText = 'Futura';
                            statusColor = 'bg-purple-500';
                        } else if (isCompleted) {
                            statusText = 'Completa';
                            statusColor = 'bg-gray-500';
                        }

                        return (
                            <Card key={booking.id} className={`p-4 border ${isPaid ? 'border-green-200 bg-green-50' : 'border-orange-200 bg-orange-50'}`}>
                                <div className="flex justify-between items-start mb-2">
                                    <div className="flex-1">
                                        <p className="font-semibold text-slate-900">
                                            {vehicle ? `${vehicle.brand} ${vehicle.model}` : `Veículo #${booking.vehicleId}`}
                                        </p>
                                        <p className="text-sm text-slate-600">
                                            {new Date(booking.pickupDate).toLocaleDateString('pt-PT')} - {new Date(booking.returnDate).toLocaleDateString('pt-PT')}
                                        </p>
                                        <p className="text-lg font-bold text-slate-900 mt-2">{booking.totalPrice.toFixed(2)} €</p>
                                    </div>
                                    <div className="flex flex-col gap-1 items-end">
                                        <span className={`px-2 py-1 text-xs rounded ${isPaid ? 'bg-green-500 text-white' : 'bg-orange-500 text-white'}`}>
                                            {isPaid ? 'Paga' : 'Pendente'}
                                        </span>
                                        <span className={`px-2 py-1 text-xs rounded ${statusColor} text-white`}>
                                            {statusText}
                                        </span>
                                    </div>
                                </div>
                            </Card>
                        );
                    })}
                </div>
            )}

            {/* My Cars Section */}
            <div className="flex items-center justify-between mb-4">
                <h2 className="text-xl font-bold text-slate-900">Meus Carros</h2>
                <Button
                    onClick={() => navigate('/add-car')}
                    className="bg-indigo-600 hover:bg-indigo-700 text-white"
                >
                    <Plus className="w-4 h-4 mr-2" />
                    Adicionar Carro
                </Button>
            </div>

            {cars.length === 0 ? (
                <Card className="p-12 text-center border border-slate-200">
                    <div className="flex flex-col items-center">
                        <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mb-4">
                            <Car className="w-8 h-8 text-slate-400" />
                        </div>
                        <h3 className="text-lg font-semibold text-slate-900 mb-2">
                            Nenhum carro adicionado
                        </h3>
                        <p className="text-slate-500 mb-6">
                            Adicione o seu primeiro carro para começar a ganhar dinheiro
                        </p>
                        <Button
                            onClick={() => navigate('/add-car')}
                            className="bg-indigo-600 hover:bg-indigo-700 text-white"
                        >
                            <Plus className="w-4 h-4 mr-2" />
                            Adicionar Carro
                        </Button>
                    </div>
                </Card>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {cars.map((car) => (
                        <Card key={car.id} className="overflow-hidden border border-slate-200">
                            {/* Car Image */}
                            <div className="relative h-48 bg-slate-200">
                                {car.images && car.images.length > 0 ? (
                                    <img
                                        src={car.images[0]}
                                        alt={`${car.brand} ${car.model}`}
                                        className="w-full h-full object-cover"
                                    />
                                ) : (
                                    <div className="w-full h-full flex items-center justify-center">
                                        <Car className="w-16 h-16 text-slate-400" />
                                    </div>
                                )}
                                <div className="absolute top-3 left-3 bg-white rounded-md px-2 py-1 text-xs font-medium">
                                    {car.brand}
                                </div>
                            </div>

                            {/* Car Info */}
                            <div className="p-4">
                                <h3 className="font-semibold text-slate-900 mb-2">
                                    {car.brand} {car.model} {car.year}
                                </h3>
                                <div className="flex items-center text-sm text-slate-500 mb-3">
                                    <MapPin className="w-4 h-4 mr-1" />
                                    {car.city || car.location}
                                </div>
                                <div className="text-lg font-bold text-slate-900 mb-4">
                                    {car.price_per_day.toFixed(2)} €/dia
                                </div>

                                {/* Action Row - View Button */}
                                <div className="flex items-center gap-2 mb-4 pb-4 border-b border-slate-200">
                                    <button
                                        onClick={() => navigate(`/cars/${car.id}`)}
                                        className="flex items-center gap-2 px-3 py-1.5 rounded-md text-sm font-medium transition-colors bg-indigo-100 text-indigo-700 hover:bg-indigo-200"
                                    >
                                        <Eye className="w-5 h-5" />
                                        Ver Detalhes
                                    </button>
                                </div>

                                {/* Action Buttons */}
                                <div className="flex gap-2">
                                    <Button
                                        onClick={() => navigate(`/edit-car/${car.id}`)}
                                        variant="outline"
                                        className="flex-1 border-slate-300 text-slate-700 hover:bg-slate-50"
                                    >
                                        <Edit className="w-4 h-4 mr-2" />
                                        Editar
                                    </Button>
                                    <Button
                                        onClick={() => handleDelete(car.id, `${car.brand} ${car.model}`)}
                                        variant="outline"
                                        className="flex-1 border-red-300 text-red-600 hover:bg-red-50"
                                    >
                                        <Trash2 className="w-4 h-4 mr-2" />
                                        Eliminar
                                    </Button>
                                </div>
                            </div>
                        </Card>
                    ))}
                </div>
            )}
        </>
    );
}
