import React, { useState } from 'react';
import { useQuery } from 'react-query';
import { useSearchParams, Link } from 'react-router-dom';
import { carService } from '@/services/carService';
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Star, MapPin, Fuel, Users, Filter, ArrowUpDown, ArrowRight, ChevronDown, ChevronUp } from "lucide-react";
import { formatCurrency } from '@/utils';

export default function Cars() {
    const [searchParams] = useSearchParams();
    const [sortBy, setSortBy] = useState('price_asc');
    const [filters, setFilters] = useState({
        city: searchParams.get('city') || '',
        pickupDate: searchParams.get('pickup') || '',
        returnDate: searchParams.get('dropoff') || '',
        type: '',
        fuel_type: '',
        transmission: '',
        minPrice: '',
        maxPrice: '',
        minRating: '',
        seats: '',
    });
    
    // Estado para expandir/colapsar categorias
    const [expandedSections, setExpandedSections] = useState({
        price: true,
        type: true,
        fuel: true,
        transmission: true,
        marca: true,
        characteristics: true,
        seats: true,
        rating: true,
    });
    
    const toggleSection = (section) => {
        setExpandedSections(prev => ({
            ...prev,
            [section]: !prev[section]
        }));
    };

    const { data, isLoading } = useQuery(['cars', filters.city, filters.pickupDate, filters.returnDate, filters.type, filters.fuel_type, filters.transmission, filters.seats, filters.minRating], async () => {
        // Enviar filtros sem minPrice e maxPrice para o backend
        const backendFilters = {
            city: filters.city,
            pickupDate: filters.pickupDate,
            returnDate: filters.returnDate,
            type: filters.type,
            fuel_type: filters.fuel_type,
            transmission: filters.transmission,
            seats: filters.seats,
            minRating: filters.minRating,
        };
        const result = await carService.list(backendFilters);
        return result.data;
    });

    let cars = data || [];
    
    // Definir range de preços fixo
    const minCarPrice = 0;
    const maxCarPrice = 500;
    
    // Aplicar filtros de preço localmente (se maxPrice = 500, não filtrar)
    if (filters.minPrice && filters.minPrice !== '') {
        cars = cars.filter(car => car.price_per_day >= parseInt(filters.minPrice));
    }
    if (filters.maxPrice && filters.maxPrice !== '' && parseInt(filters.maxPrice) < 500) {
        cars = cars.filter(car => car.price_per_day <= parseInt(filters.maxPrice));
    }
    
    // Ordenação
    if (sortBy === 'price_asc') {
        cars = [...cars].sort((a, b) => a.price_per_day - b.price_per_day);
    } else if (sortBy === 'price_desc') {
        cars = [...cars].sort((a, b) => b.price_per_day - a.price_per_day);
    } else if (sortBy === 'rating') {
        cars = [...cars].sort((a, b) => (b.average_rating || 0) - (a.average_rating || 0));
    } else if (sortBy === 'recent') {
        cars = [...cars].sort((a, b) => new Date(b.id) - new Date(a.id));
    }

    return (
        <div className="min-h-screen bg-slate-50">
            {/* Hero Section */}
            <div className="bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 text-white py-16">
                <div className="max-w-7xl mx-auto px-4 sm:px-6">
                    <h1 className="text-4xl md:text-5xl font-bold mb-4">
                        Encontre o seu <span className="bg-gradient-to-r from-indigo-400 to-blue-400 bg-clip-text text-transparent">carro ideal</span>
                    </h1>
                    <p className="text-slate-300 text-lg">
                        Pesquisar por cidade ou localização...
                    </p>
                    <div className="mt-6 flex gap-4">
                        <Input
                            placeholder="Lisboa, Porto, Faro..."
                            value={filters.city}
                            onChange={(e) => setFilters({ ...filters, city: e.target.value })}
                            className="bg-white/90 backdrop-blur-sm max-w-md h-12 text-slate-900 border-slate-300"
                        />
                        <select
                            className="h-12 rounded-md border border-slate-300 bg-white/90 backdrop-blur-sm px-4 text-sm min-w-[200px] text-slate-900"
                            value={sortBy}
                            onChange={(e) => setSortBy(e.target.value)}
                        >
                            <option value="price_asc">Preço: menor primeiro</option>
                            <option value="price_desc">Preço: maior primeiro</option>
                            <option value="rating">Melhor avaliação</option>
                            <option value="recent">Mais recentes</option>
                        </select>
                    </div>
                </div>
            </div>

            {/* Main Content */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
                <div className="flex flex-col lg:flex-row gap-8">
                    {/* Sidebar Filters */}
                    <aside className="lg:w-80 flex-shrink-0">
                        <div className="bg-white rounded-xl p-6 shadow-sm sticky top-24">
                            <div className="flex items-center justify-between mb-6">
                                <h2 className="text-lg font-bold text-slate-900">Filtros</h2>
                                <button
                                    onClick={() => setFilters({
                                        city: '',
                                        type: '',
                                        fuel_type: '',
                                        transmission: '',
                                        minPrice: '',
                                        maxPrice: '',
                                        minRating: '',
                                        seats: '',
                                    })}
                                    className="text-sm text-indigo-600 hover:text-indigo-700"
                                >
                                    Limpar
                                </button>
                            </div>

                            {/* Preço por dia */}
                            <div className="mb-6">
                                <button
                                    onClick={() => toggleSection('price')}
                                    className="flex items-center justify-between w-full text-sm font-semibold text-slate-900 mb-3 hover:text-indigo-600 transition-colors"
                                >
                                    <span>Preço por dia</span>
                                    {expandedSections.price ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
                                </button>
                                {expandedSections.price && (
                                    <div className="space-y-3">
                                        <div className="flex gap-2">
                                            <Input
                                                type="number"
                                                placeholder="€0"
                                                value={filters.minPrice}
                                                onChange={(e) => {
                                                    const value = e.target.value === '' ? '' : Math.max(0, parseInt(e.target.value));
                                                    setFilters({ ...filters, minPrice: value });
                                                }}
                                                className="bg-slate-50 border-slate-200"
                                                min="0"
                                            />
                                            <Input
                                                type="number"
                                                placeholder="€500+"
                                                value={filters.maxPrice}
                                                onChange={(e) => {
                                                    const value = e.target.value === '' ? '' : Math.max(0, parseInt(e.target.value));
                                                    setFilters({ ...filters, maxPrice: value });
                                                }}
                                                className="bg-slate-50 border-slate-200"
                                                min="0"
                                            />
                                        </div>
                                        <div className="space-y-1">
                                            <input
                                                type="range"
                                                min="0"
                                                max="500"
                                                step="5"
                                                value={filters.maxPrice || 500}
                                                onChange={(e) => setFilters({ ...filters, maxPrice: e.target.value })}
                                                className="w-full h-2 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-indigo-600"
                                            />
                                            <div className="flex justify-between text-xs text-slate-500">
                                                <span>€0</span>
                                                <span>€500+</span>
                                            </div>
                                        </div>
                                    </div>
                                )}
                            </div>

                            {/* Tipo de Carro */}
                            <div className="mb-6">
                                <button
                                    onClick={() => toggleSection('type')}
                                    className="flex items-center justify-between w-full text-sm font-semibold text-slate-900 mb-3 hover:text-indigo-600 transition-colors"
                                >
                                    <span>Tipo de Carro</span>
                                    {expandedSections.type ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
                                </button>
                                {expandedSections.type && (
                                <div className="space-y-2">
                                    {['sedan', 'suv', 'hatchback', 'citadino', 'desportivo', 'monovolume', 'coupé'].map((type) => (
                                        <label key={type} className="flex items-center cursor-pointer">
                                            <input
                                                type="checkbox"
                                                checked={filters.type === type}
                                                onChange={(e) => setFilters({ ...filters, type: e.target.checked ? type : '' })}
                                                className="w-4 h-4 text-indigo-600 rounded"
                                            />
                                            <span className="ml-2 text-sm text-slate-700 capitalize">{
                                                type === 'sedan' ? 'Sedan' :
                                                type === 'suv' ? 'SUV' :
                                                type === 'hatchback' ? 'Hatchback' :
                                                type === 'citadino' ? 'Citadino' :
                                                type === 'desportivo' ? 'Desportivo' :
                                                type === 'monovolume' ? 'Monovolume' : 'Coupé'
                                            }</span>
                                        </label>
                                    ))}
                                </div>
                                )}
                            </div>

                            {/* Combustível */}
                            <div className="mb-6">
                                <button
                                    onClick={() => toggleSection('fuel')}
                                    className="flex items-center justify-between w-full text-sm font-semibold text-slate-900 mb-3 hover:text-indigo-600 transition-colors"
                                >
                                    <span>Combustível</span>
                                    {expandedSections.fuel ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
                                </button>
                                {expandedSections.fuel && (
                                <div className="space-y-2">
                                    {['gasoline', 'diesel', 'electric', 'hybrid', 'gpl'].map((fuel) => (
                                        <label key={fuel} className="flex items-center cursor-pointer">
                                            <input
                                                type="checkbox"
                                                checked={filters.fuel_type === fuel}
                                                onChange={(e) => setFilters({ ...filters, fuel_type: e.target.checked ? fuel : '' })}
                                                className="w-4 h-4 text-indigo-600 rounded"
                                            />
                                            <span className="ml-2 text-sm text-slate-700 capitalize">{
                                                fuel === 'gasoline' ? 'Gasolina' :
                                                fuel === 'diesel' ? 'Diesel' :
                                                fuel === 'electric' ? 'Elétrico' :
                                                fuel === 'hybrid' ? 'Híbrido' : 'GPL'
                                            }</span>
                                        </label>
                                    ))}
                                </div>
                                )}
                            </div>

                            {/* Transmissão */}
                            <div className="mb-6">
                                <button
                                    onClick={() => toggleSection('transmission')}
                                    className="flex items-center justify-between w-full text-sm font-semibold text-slate-900 mb-3 hover:text-indigo-600 transition-colors"
                                >
                                    <span>Transmissão</span>
                                    {expandedSections.transmission ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
                                </button>
                                {expandedSections.transmission && (
                                <div className="space-y-2">
                                    {['manual', 'automatic'].map((trans) => (
                                        <label key={trans} className="flex items-center cursor-pointer">
                                            <input
                                                type="radio"
                                                name="transmission"
                                                checked={filters.transmission === trans}
                                                onChange={() => setFilters({ ...filters, transmission: trans })}
                                                className="w-4 h-4 text-indigo-600"
                                            />
                                            <span className="ml-2 text-sm text-slate-700">{trans === 'manual' ? 'Manual' : 'Automática'}</span>
                                        </label>
                                    ))}
                                </div>
                                )}
                            </div>

                            {/* Características */}
                            <div className="mb-6">
                                <button
                                    onClick={() => toggleSection('characteristics')}
                                    className="flex items-center justify-between w-full text-sm font-semibold text-slate-900 mb-3 hover:text-indigo-600 transition-colors"
                                >
                                    <span>Características</span>
                                    {expandedSections.characteristics ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
                                </button>
                                {expandedSections.characteristics && (
                                <div className="space-y-2 text-sm">
                                    <label className="flex items-center cursor-pointer">
                                        <input type="checkbox" className="w-4 h-4 text-indigo-600 rounded" />
                                        <span className="ml-2 text-slate-700">Ar Condicionado</span>
                                    </label>
                                    <label className="flex items-center cursor-pointer">
                                        <input type="checkbox" className="w-4 h-4 text-indigo-600 rounded" />
                                        <span className="ml-2 text-slate-700">GPS</span>
                                    </label>
                                    <label className="flex items-center cursor-pointer">
                                        <input type="checkbox" className="w-4 h-4 text-indigo-600 rounded" />
                                        <span className="ml-2 text-slate-700">Bluetooth</span>
                                    </label>
                                </div>
                                )}
                            </div>

                            {/* Nº de Lugares */}
                            <div className="mb-6">
                                <button
                                    onClick={() => toggleSection('seats')}
                                    className="flex items-center justify-between w-full text-sm font-semibold text-slate-900 mb-3 hover:text-indigo-600 transition-colors"
                                >
                                    <span>Nº de Lugares</span>
                                    {expandedSections.seats ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
                                </button>
                                {expandedSections.seats && (
                                <div className="flex gap-2">
                                    {['2+', '4+', '5+', '7+', '9+'].map((seat) => (
                                        <button
                                            key={seat}
                                            onClick={() => setFilters({ ...filters, seats: seat })}
                                            className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-colors ${
                                                filters.seats === seat
                                                    ? 'bg-indigo-600 text-white'
                                                    : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                                            }`}
                                        >
                                            {seat}
                                        </button>
                                    ))}
                                </div>
                                )}
                            </div>

                            {/* Avaliação Mínima */}
                            <div>
                                <button
                                    onClick={() => toggleSection('rating')}
                                    className="flex items-center justify-between w-full text-sm font-semibold text-slate-900 mb-3 hover:text-indigo-600 transition-colors"
                                >
                                    <span>Avaliação Mínima</span>
                                    {expandedSections.rating ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
                                </button>
                                {expandedSections.rating && (
                                <div className="flex gap-2">
                                    {['3+', '4+', '4.5+'].map((rating) => (
                                        <button
                                            key={rating}
                                            onClick={() => setFilters({ ...filters, minRating: rating })}
                                            className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-colors ${
                                                filters.minRating === rating
                                                    ? 'bg-indigo-600 text-white'
                                                    : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                                            }`}
                                        >
                                            {rating} ★
                                        </button>
                                    ))}
                                </div>
                                )}
                            </div>
                        </div>
                    </aside>

                    {/* Results */}
                    <div className="flex-1">
                        <div className="mb-6">
                            <p className="text-slate-600 font-medium">{cars.length} carros encontrados</p>
                        </div>

                {isLoading ? (
                    <div className="space-y-4">
                        {[1, 2, 3, 4].map((i) => (
                            <Card key={i} className="overflow-hidden animate-pulse">
                                <div className="flex flex-col md:flex-row">
                                    <div className="w-full md:w-80 h-48 bg-slate-200" />
                                    <div className="flex-1 p-6 space-y-3">
                                        <div className="h-6 bg-slate-200 rounded w-1/3" />
                                        <div className="h-4 bg-slate-200 rounded w-1/4" />
                                        <div className="h-4 bg-slate-200 rounded w-1/2" />
                                    </div>
                                </div>
                            </Card>
                        ))}
                    </div>
                ) : (
                    <div className="space-y-4">
                        {cars.map((car) => (
                            <Link key={car.id} to={`/cars/${car.id}`}>
                                <Card className="group overflow-hidden border border-slate-200 hover:border-indigo-200 shadow-sm hover:shadow-lg transition-all duration-300 bg-white">
                                    <div className="flex flex-col md:flex-row">
                                        {/* Image */}
                                        <div className="relative w-full md:w-80 h-48 overflow-hidden flex-shrink-0">
                                            <img
                                                src={car.images?.[0] || '/Images/photo-1494976388531-d1058494cdd8.jpeg'}
                                                alt={`${car.brand} ${car.model}`}
                                                className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                                            />
                                            <div className="absolute inset-0 bg-gradient-to-r from-black/40 via-black/10 to-transparent" />
                                            
                                            {/* Badge */}
                                            <span className="absolute top-4 left-4 bg-white text-slate-900 px-3 py-1.5 rounded-lg text-xs font-semibold shadow-lg capitalize">
                                                {car.type === 'citadino' ? 'Citadino' : 
                                                 car.type === 'suv' ? 'SUV' : 
                                                 car.type === 'hatchback' ? 'Hatchback' : 
                                                 car.type === 'desportivo' ? 'Desportivo' : 'Sedan'}
                                            </span>
                                        </div>
                                        
                                        {/* Content */}
                                        <div className="flex-1 p-6 flex flex-col justify-between">
                                            <div>
                                                <div className="flex items-start justify-between mb-2">
                                                    <div>
                                                        <h3 className="text-xl font-bold text-slate-900">
                                                            {car.brand} {car.model}
                                                        </h3>
                                                        <p className="text-sm text-slate-500">{car.year}</p>
                                                    </div>
                                                    {/* Rating */}
                                                    {car.average_rating > 0 && (
                                                        <div className="flex items-center gap-1">
                                                            <Star className="w-4 h-4 fill-amber-400 text-amber-400" />
                                                            <span className="text-sm font-bold text-slate-900">{car.average_rating.toFixed(1)}</span>
                                                            <span className="text-xs text-slate-500">({car.total_reviews})</span>
                                                        </div>
                                                    )}
                                                </div>
                                                
                                                <div className="flex items-center gap-4 text-sm text-slate-500 mb-4">
                                                    <span className="flex items-center gap-1.5">
                                                        <MapPin className="w-4 h-4" />
                                                        {car.city}
                                                    </span>
                                                    <span className="flex items-center gap-1.5">
                                                        <Fuel className="w-4 h-4" />
                                                        {car.fuel_type === 'gasoline' ? 'Gasolina' : 
                                                         car.fuel_type === 'diesel' ? 'Diesel' : 
                                                         car.fuel_type === 'electric' ? 'Elétrico' : 
                                                         car.fuel_type === 'hybrid' ? 'Híbrido' : car.fuel_type}
                                                    </span>
                                                    <span className="flex items-center gap-1.5">
                                                        <Users className="w-4 h-4" />
                                                        {car.seats} lugares
                                                    </span>
                                                    <span className="flex items-center gap-1.5">
                                                        {car.transmission === 'manual' ? 'Manual' : 'Automática'}
                                                    </span>
                                                </div>
                                            </div>
                                            
                                            <div className="flex items-center justify-between pt-4 border-t border-slate-100">
                                                <div>
                                                    <span className="text-3xl font-bold text-slate-900">
                                                        {formatCurrency(car.price_per_day).replace(',00', '')}
                                                    </span>
                                                    <span className="text-slate-500 text-sm">/dia</span>
                                                </div>
                                                <Button className="bg-indigo-600 hover:bg-indigo-700 text-white">
                                                    Ver Detalhes
                                                    <ArrowRight className="w-4 h-4 ml-2 group-hover:translate-x-1 transition-transform" />
                                                </Button>
                                            </div>
                                        </div>
                                    </div>
                                </Card>
                            </Link>
                        ))}
                    </div>
                )}
                    </div>
                </div>
            </div>
        </div>
    );
}
