import React from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { carService } from '@/services/carService';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Car, Plus, Edit, Trash2, MapPin, Eye } from 'lucide-react';

export default function OwnerCarsDashboard() {
    const navigate = useNavigate();
    const queryClient = useQueryClient();

    // Fetch cars owned by this user (using JWT token)
    const { data: cars = [], isLoading } = useQuery(
        ['ownerCars'],
        () => carService.getCarsByOwner().then(res => res.data)
    );

    // Mutation to delete car
    const deleteMutation = useMutation(
        (carId) => carService.deleteCar(carId),
        {
            onSuccess: () => {
                queryClient.invalidateQueries(['ownerCars']);
            }
        }
    );

    const handleDelete = (carId, carName) => {
        if (window.confirm(`Tem certeza que deseja eliminar o carro "${carName}"?`)) {
            deleteMutation.mutate(carId);
        }
    };

    // Calculate stats
    const totalCars = cars.length;
    const pendingReservations = 0; // TODO: Get from reservations
    const completedReservations = 0; // TODO: Get from reservations
    const totalEarnings = 0; // TODO: Calculate from reservations

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
                    <div className="text-sm text-slate-500">Completadas</div>
                </Card>
                <Card className="p-6 text-center border border-slate-200">
                    <div className="text-3xl font-bold text-blue-500 mb-1">{totalEarnings.toFixed(2)} €</div>
                    <div className="text-sm text-slate-500">Ganhos Totais</div>
                </Card>
            </div>

            {/* Pending Reservations */}
            <h2 className="text-xl font-bold text-slate-900 mb-4">Reservas Pendentes</h2>
            <Card className="p-8 text-center border border-slate-200 mb-8">
                <p className="text-slate-500">Nenhuma reserva pendente.</p>
            </Card>

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
