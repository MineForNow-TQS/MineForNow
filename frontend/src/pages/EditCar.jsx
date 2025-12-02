import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { useAuth } from '@/contexts/AuthContext';
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { carService } from '@/services/carService';

export default function EditCar() {
    const { id } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();
    const queryClient = useQueryClient();
    const [formData, setFormData] = useState(null);

    // Fetch car data
    const { data: car, isLoading } = useQuery(
        ['car', id],
        () => carService.get(id).then(res => res.data)
    );

    // Update mutation
    const updateMutation = useMutation(
        (data) => carService.update(id, data),
        {
            onSuccess: () => {
                queryClient.invalidateQueries(['car', id]);
                queryClient.invalidateQueries(['ownerCars', user?.id]);
                alert('Carro atualizado com sucesso!');
                navigate('/dashboard');
            },
            onError: (error) => {
                console.error('Erro ao atualizar carro:', error);
                alert('Erro ao atualizar carro. Por favor, tente novamente.');
            }
        }
    );

    // Initialize form when car data loads
    useEffect(() => {
        if (car) {
            setFormData({
                brand: car.brand || '',
                model: car.model || '',
                year: car.year?.toString() || '',
                type: car.type || 'citadino',
                mileage: car.mileage?.toString() || '',
                fuel_type: car.fuel_type || 'gasoline',
                transmission: car.transmission || 'automatic',
                seats: car.seats?.toString() || '',
                doors: car.doors?.toString() || '',
                air_conditioning: car.air_conditioning || false,
                gps: car.gps || false,
                bluetooth: car.bluetooth || false,
                city: car.city || '',
                location: car.location || '',
                price_per_day: car.price_per_day?.toString() || '',
                description: car.description || ''
            });
        }
    }, [car]);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        updateMutation.mutate({
            ...formData,
            year: parseInt(formData.year),
            mileage: parseInt(formData.mileage),
            seats: parseInt(formData.seats),
            doors: parseInt(formData.doors),
            price_per_day: parseFloat(formData.price_per_day)
        });
    };

    if (isLoading || !formData) {
        return (
            <div className="min-h-screen bg-white py-12 flex items-center justify-center">
                <div className="text-center">
                    <p className="text-slate-600">A carregar...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-white py-12">
            <div className="max-w-3xl mx-auto px-4 sm:px-6">
                <h1 className="text-3xl font-bold text-slate-900 mb-8">Editar Carro</h1>
                
                <form onSubmit={handleSubmit}>
                    {/* Informações Básicas */}
                    <Card className="p-6 mb-6 border border-slate-200">
                        <h2 className="text-lg font-semibold text-slate-900 mb-4">Informações Básicas</h2>
                        
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Marca <span className="text-red-500">*</span>
                                </label>
                                <Input
                                    name="brand"
                                    value={formData.brand}
                                    onChange={handleChange}
                                    required
                                    className="bg-white border-slate-300"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Modelo <span className="text-red-500">*</span>
                                </label>
                                <Input
                                    name="model"
                                    value={formData.model}
                                    onChange={handleChange}
                                    required
                                    className="bg-white border-slate-300"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Ano <span className="text-red-500">*</span>
                                </label>
                                <Input
                                    name="year"
                                    type="number"
                                    value={formData.year}
                                    onChange={handleChange}
                                    required
                                    className="bg-white border-slate-300"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Tipo <span className="text-red-500">*</span>
                                </label>
                                <select
                                    name="type"
                                    value={formData.type}
                                    onChange={handleChange}
                                    required
                                    className="w-full px-3 py-2 border border-slate-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                >
                                    <option value="citadino">Citadino</option>
                                    <option value="sedan">Sedan</option>
                                    <option value="suv">SUV</option>
                                    <option value="desportivo">Desportivo</option>
                                    <option value="carrinha">Carrinha</option>
                                </select>
                            </div>

                            <div className="col-span-2">
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Quilometragem (km) <span className="text-red-500">*</span>
                                </label>
                                <Input
                                    name="mileage"
                                    type="number"
                                    value={formData.mileage}
                                    onChange={handleChange}
                                    required
                                    className="bg-white border-slate-300"
                                />
                            </div>
                        </div>
                    </Card>

                    {/* Especificações */}
                    <Card className="p-6 mb-6 border border-slate-200">
                        <h2 className="text-lg font-semibold text-slate-900 mb-4">Especificações</h2>
                        
                        <div className="grid grid-cols-2 gap-4 mb-4">
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Combustível <span className="text-red-500">*</span>
                                </label>
                                <select
                                    name="fuel_type"
                                    value={formData.fuel_type}
                                    onChange={handleChange}
                                    required
                                    className="w-full px-3 py-2 border border-slate-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                >
                                    <option value="gasoline">Gasolina</option>
                                    <option value="diesel">Diesel</option>
                                    <option value="electric">Elétrico</option>
                                    <option value="hybrid">Híbrido</option>
                                </select>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Transmissão <span className="text-red-500">*</span>
                                </label>
                                <select
                                    name="transmission"
                                    value={formData.transmission}
                                    onChange={handleChange}
                                    required
                                    className="w-full px-3 py-2 border border-slate-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                >
                                    <option value="manual">Manual</option>
                                    <option value="automatic">Automática</option>
                                </select>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Lugares <span className="text-red-500">*</span>
                                </label>
                                <Input
                                    name="seats"
                                    type="number"
                                    value={formData.seats}
                                    onChange={handleChange}
                                    required
                                    className="bg-white border-slate-300"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Portas <span className="text-red-500">*</span>
                                </label>
                                <Input
                                    name="doors"
                                    type="number"
                                    value={formData.doors}
                                    onChange={handleChange}
                                    required
                                    className="bg-white border-slate-300"
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-2">
                                Características
                            </label>
                            <div className="space-y-2">
                                <label className="flex items-center">
                                    <input
                                        type="checkbox"
                                        name="air_conditioning"
                                        checked={formData.air_conditioning}
                                        onChange={handleChange}
                                        className="w-4 h-4 text-indigo-600 border-slate-300 rounded focus:ring-indigo-500"
                                    />
                                    <span className="ml-2 text-sm text-slate-700">Ar Condicionado</span>
                                </label>
                                <label className="flex items-center">
                                    <input
                                        type="checkbox"
                                        name="gps"
                                        checked={formData.gps}
                                        onChange={handleChange}
                                        className="w-4 h-4 text-indigo-600 border-slate-300 rounded focus:ring-indigo-500"
                                    />
                                    <span className="ml-2 text-sm text-slate-700">GPS</span>
                                </label>
                                <label className="flex items-center">
                                    <input
                                        type="checkbox"
                                        name="bluetooth"
                                        checked={formData.bluetooth}
                                        onChange={handleChange}
                                        className="w-4 h-4 text-indigo-600 border-slate-300 rounded focus:ring-indigo-500"
                                    />
                                    <span className="ml-2 text-sm text-slate-700">Bluetooth</span>
                                </label>
                            </div>
                        </div>
                    </Card>

                    {/* Localização e Preço */}
                    <Card className="p-6 mb-6 border border-slate-200">
                        <h2 className="text-lg font-semibold text-slate-900 mb-4">Localização e Preço</h2>
                        
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Cidade <span className="text-red-500">*</span>
                                </label>
                                <Input
                                    name="city"
                                    value={formData.city}
                                    onChange={handleChange}
                                    required
                                    className="bg-white border-slate-300"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Localização Exata <span className="text-red-500">*</span>
                                </label>
                                <Input
                                    name="location"
                                    value={formData.location}
                                    onChange={handleChange}
                                    placeholder="Ex: Av. da Liberdade"
                                    required
                                    className="bg-white border-slate-300"
                                />
                            </div>

                            <div className="col-span-2">
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Preço por Dia (€) <span className="text-red-500">*</span>
                                </label>
                                <Input
                                    name="price_per_day"
                                    type="number"
                                    step="0.01"
                                    value={formData.price_per_day}
                                    onChange={handleChange}
                                    required
                                    className="bg-white border-slate-300"
                                />
                            </div>

                            <div className="col-span-2">
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Descrição <span className="text-red-500">*</span>
                                </label>
                                <Textarea
                                    name="description"
                                    value={formData.description}
                                    onChange={handleChange}
                                    placeholder="Descreva o seu carro..."
                                    required
                                    rows={4}
                                    className="bg-white border-slate-300 resize-none"
                                />
                            </div>
                        </div>
                    </Card>

                    {/* Buttons */}
                    <div className="flex gap-3">
                        <Button 
                            type="submit"
                            disabled={updateMutation.isLoading}
                            className="bg-indigo-600 hover:bg-indigo-700 text-white disabled:opacity-50"
                        >
                            {updateMutation.isLoading ? 'A guardar...' : 'Guardar Alterações'}
                        </Button>
                        <Button 
                            type="button"
                            onClick={() => navigate('/dashboard')}
                            variant="outline"
                            className="border-slate-300 text-slate-700"
                        >
                            Cancelar
                        </Button>
                    </div>
                </form>
            </div>
        </div>
    );
}
