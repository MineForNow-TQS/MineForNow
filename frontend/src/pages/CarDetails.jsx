import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from 'react-query';
import { carService } from '@/services/carService';
import { reviewService } from '@/services/reviewService';
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Star, MapPin, Fuel, Users, Calendar, Shield, Check, ArrowLeft, ChevronLeft, ChevronRight, Car, Snowflake, Bluetooth as BluetoothIcon, Navigation2 } from "lucide-react";
import { formatCurrency } from '@/utils';

export default function CarDetails() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');

    const { data, isLoading } = useQuery(['car', id], async () => {
        const result = await carService.get(id);
        return result.data;
    });

    const { data: reviewsData } = useQuery(['reviews', id], async () => {
        const response = await fetch(`http://localhost:8080/api/vehicles/${id}/reviews`);
        if (!response.ok) {
            if (response.status === 404) return { averageRating: 0, totalReviews: 0, reviews: [] };
            throw new Error('Failed to fetch reviews');
        }
        return response.json();
    });

    if (isLoading) {
        return (
            <div className="min-h-screen bg-slate-50 py-8">
                <div className="max-w-7xl mx-auto px-4 sm:px-6">
                    <div className="animate-pulse">
                        <div className="h-96 bg-slate-200 rounded-xl mb-8" />
                        <div className="h-8 bg-slate-200 rounded w-1/2 mb-4" />
                        <div className="h-4 bg-slate-200 rounded w-3/4" />
                    </div>
                </div>
            </div>
        );
    }

    if (!data) {
        return (
            <div className="min-h-screen bg-slate-50 py-8">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 text-center">
                    <h1 className="text-2xl font-bold text-slate-900">Carro não encontrado</h1>
                </div>
            </div>
        );
    }

    const car = data;
    // O adapter já garante que car.images[0] tem uma URL válida (imagem real ou placeholder)
    const baseImage = car.images?.[0] || car.image_url || '/Images/photo-1494976388531-d1058494cdd8.jpeg';
    const images = [baseImage, baseImage, baseImage];

    // Reviews data from backend API
    const reviews = reviewsData?.reviews || [];
    const averageRating = reviewsData?.averageRating || 0;
    const totalReviews = reviewsData?.totalReviews || 0;

    const nextImage = () => {
        setCurrentImageIndex((prev) => (prev + 1) % images.length);
    };

    const prevImage = () => {
        setCurrentImageIndex((prev) => (prev - 1 + images.length) % images.length);
    };

    const canReserve = startDate && endDate;

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('pt-PT', { year: 'numeric', month: 'long', day: 'numeric' });
    };

    // Formata a matrícula para o formato AA-00-BB
    const formatLicensePlate = (plate) => {
        if (!plate) return null;
        const cleaned = plate.replace(/[^A-Za-z0-9]/g, '').toUpperCase();
        if (cleaned.length !== 6) return plate; // Se não tiver 6 caracteres, retorna como está
        return `${cleaned.slice(0, 2)}-${cleaned.slice(2, 4)}-${cleaned.slice(4, 6)}`;
    };

    return (
        <div className="min-h-screen bg-slate-50">
            {/* Header with Back Button */}
            <div className="bg-white border-b border-slate-200">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 py-4">
                    <button
                        onClick={() => navigate(-1)}
                        className="flex items-center gap-2 text-slate-600 hover:text-slate-900"
                    >
                        <ArrowLeft className="w-5 h-5" />
                        Voltar
                    </button>
                </div>
            </div>

            <div className="max-w-7xl mx-auto px-4 sm:px-6 py-6">
                <div className="grid lg:grid-cols-3 gap-6">
                    {/* Left Column - Image and Details */}
                    <div className="lg:col-span-2 space-y-4">
                        {/* Image Gallery */}
                        <div className="relative">
                            <div className="relative h-80 md:h-96 rounded-2xl overflow-hidden bg-slate-900">
                                <img
                                    src={images[currentImageIndex]}
                                    alt={`${car.brand} ${car.model}`}
                                    className="w-full h-full object-cover"
                                />

                                {images.length > 1 && (
                                    <>
                                        <button
                                            onClick={prevImage}
                                            className="absolute left-4 top-1/2 -translate-y-1/2 bg-white/90 hover:bg-white p-2 rounded-full shadow-lg transition-all"
                                        >
                                            <ChevronLeft className="w-6 h-6 text-slate-900" />
                                        </button>
                                        <button
                                            onClick={nextImage}
                                            className="absolute right-4 top-1/2 -translate-y-1/2 bg-white/90 hover:bg-white p-2 rounded-full shadow-lg transition-all"
                                        >
                                            <ChevronRight className="w-6 h-6 text-slate-900" />
                                        </button>
                                    </>
                                )}
                            </div>

                            {/* Thumbnail Preview */}
                            {images.length > 1 && (
                                <div className="flex gap-2 mt-3">
                                    {images.map((img, idx) => (
                                        <button
                                            key={idx}
                                            onClick={() => setCurrentImageIndex(idx)}
                                            className={`w-16 h-16 rounded-lg overflow-hidden border-2 transition-all ${idx === currentImageIndex ? 'border-slate-900' : 'border-slate-200'
                                                }`}
                                        >
                                            <img src={img} alt="" className="w-full h-full object-cover" />
                                        </button>
                                    ))}
                                </div>
                            )}
                        </div>

                        {/* Title and Badge */}
                        <div>
                            <span className="inline-block bg-slate-900 text-white px-3 py-1 rounded-lg text-xs font-semibold mb-3 capitalize">
                                {car.type === 'citadino' ? 'Citadino' :
                                    car.type === 'suv' ? 'SUV' :
                                        car.type === 'hatchback' ? 'Hatchback' :
                                            car.type === 'desportivo' ? 'Desportivo' : 'Sedan'}
                            </span>
                            <h1 className="text-3xl font-bold text-slate-900 mb-1">
                                {car.brand} {car.model}
                            </h1>
                            <p className="text-slate-500 mb-1">{car.year}</p>
                            {car.license_plate && (
                                <p className="text-slate-400 text-sm mb-3">{formatLicensePlate(car.license_plate)}</p>
                            )}

                            <div className="flex items-center gap-2 text-slate-600">
                                <MapPin className="w-4 h-4" />
                                <span>{car.location ? `${car.location}, ${car.city}` : car.city}</span>
                            </div>
                        </div>

                        {/* Specs Grid */}
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                            <div className="bg-white rounded-xl p-4 border border-slate-200 text-center">
                                <Fuel className="w-6 h-6 text-indigo-600 mx-auto mb-2" />
                                <p className="text-xs text-slate-500 mb-1">Combustível</p>
                                <p className="font-semibold text-slate-900 text-sm">
                                    {car.fuel_type === 'gasoline' ? 'Gasolina' :
                                        car.fuel_type === 'diesel' ? 'Diesel' :
                                            car.fuel_type === 'electric' ? 'Elétrico' :
                                                car.fuel_type === 'hybrid' ? 'Híbrido' : car.fuel_type}
                                </p>
                            </div>
                            <div className="bg-white rounded-xl p-4 border border-slate-200 text-center">
                                <Car className="w-6 h-6 text-indigo-600 mx-auto mb-2" />
                                <p className="text-xs text-slate-500 mb-1">Transmissão</p>
                                <p className="font-semibold text-slate-900 text-sm">{car.transmission === 'manual' ? 'Manual' : 'Automática'}</p>
                            </div>
                            <div className="bg-white rounded-xl p-4 border border-slate-200 text-center">
                                <Users className="w-6 h-6 text-indigo-600 mx-auto mb-2" />
                                <p className="text-xs text-slate-500 mb-1">Lugares</p>
                                <p className="font-semibold text-slate-900 text-sm">{car.seats}</p>
                            </div>
                            <div className="bg-white rounded-xl p-4 border border-slate-200 text-center">
                                <Calendar className="w-6 h-6 text-indigo-600 mx-auto mb-2" />
                                <p className="text-xs text-slate-500 mb-1">Portas</p>
                                <p className="font-semibold text-slate-900 text-sm">{car.doors || 4}</p>
                            </div>
                        </div>

                        {/* Características */}
                        <div className="bg-white rounded-xl p-6 border border-slate-200">
                            <h2 className="text-lg font-bold text-slate-900 mb-4">Características</h2>
                            <div className="flex flex-wrap gap-3">
                                {car.air_conditioning && (
                                    <div className="flex items-center gap-2 px-4 py-2 bg-white rounded-lg text-sm border border-slate-200">
                                        <Snowflake className="w-5 h-5 text-slate-700" />
                                        <span className="text-slate-900">Ar Condicionado</span>
                                    </div>
                                )}
                                {car.gps && (
                                    <div className="flex items-center gap-2 px-4 py-2 bg-white rounded-lg text-sm border border-slate-200">
                                        <Navigation2 className="w-5 h-5 text-slate-700" />
                                        <span className="text-slate-900">GPS</span>
                                    </div>
                                )}
                                {car.bluetooth && (
                                    <div className="flex items-center gap-2 px-4 py-2 bg-white rounded-lg text-sm border border-slate-200">
                                        <BluetoothIcon className="w-5 h-5 text-slate-700" />
                                        <span className="text-slate-900">Bluetooth</span>
                                    </div>
                                )}
                            </div>
                        </div>

                        {/* Descrição */}
                        <div className="bg-white rounded-xl p-6 border border-slate-200">
                            <h2 className="text-lg font-bold text-slate-900 mb-3">Descrição</h2>
                            <p className="text-slate-600 leading-relaxed">{car.description}</p>
                        </div>
                    </div>

                    {/* Right Column - Booking Card */}
                    <div className="lg:col-span-1">
                        <div className="bg-white rounded-xl p-6 border border-slate-200 sticky top-24 shadow-sm">
                            <div className="mb-6">
                                <div className="flex items-baseline gap-1">
                                    <span className="text-4xl font-bold text-slate-900">
                                        {formatCurrency(car.price_per_day).replace(',00', '')}
                                    </span>
                                    <span className="text-slate-500">/dia</span>
                                </div>
                                {averageRating > 0 && (
                                    <div className="flex items-center gap-1 mt-2">
                                        <Star className="w-5 h-5 fill-amber-400 text-amber-400" />
                                        <span className="font-semibold text-slate-900">{averageRating.toFixed(1)}</span>
                                        <span className="text-sm text-slate-500">({totalReviews} {totalReviews === 1 ? 'avaliação' : 'avaliações'})</span>
                                    </div>
                                )}
                            </div>

                            <div className="space-y-3 mb-6">
                                <div>
                                    <label className="block text-sm font-medium text-slate-700 mb-2">
                                        Data de Recolha
                                    </label>
                                    <Input
                                        type="date"
                                        value={startDate}
                                        onChange={(e) => setStartDate(e.target.value)}
                                        className="w-full px-4 py-3 bg-white border border-slate-200 rounded-lg"
                                    />
                                </div>

                                <div>
                                    <label className="block text-sm font-medium text-slate-700 mb-2">
                                        Data de Entrega
                                    </label>
                                    <Input
                                        type="date"
                                        value={endDate}
                                        onChange={(e) => setEndDate(e.target.value)}
                                        className="w-full px-4 py-3 bg-white border border-slate-200 rounded-lg"
                                    />
                                </div>
                            </div>

                            <Button
                                className="w-full h-12 text-base font-semibold mb-4 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed"
                                style={{ backgroundColor: canReserve ? '#6366f1' : '#9ca3af' }}
                                onClick={() => canReserve && navigate(`/checkout?carId=${car.id}&start=${startDate}&end=${endDate}`)}
                                disabled={!canReserve}
                            >
                                Reservar Agora
                            </Button>

                            <div className="space-y-2.5 text-sm">
                                <div className="flex items-center gap-2 text-slate-700">
                                    <Shield className="w-5 h-5 text-green-600" />
                                    <span>Seguro incluído</span>
                                </div>
                                <div className="flex items-center gap-2 text-slate-700">
                                    <Check className="w-5 h-5 text-green-600" />
                                    <span>Cancelamento gratuito até 24h antes</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Reviews Section - Full Width at Bottom */}
                <div className="mt-8 lg:col-span-2">
                    <div className="bg-white rounded-xl p-6 border border-slate-200">
                        <div className="flex items-center justify-between mb-6">
                            <div className="flex items-center gap-2">
                                <div className="w-10 h-10 bg-slate-100 rounded-full flex items-center justify-center">
                                    <Star className="w-5 h-5 text-slate-400" />
                                </div>
                                <h2 className="text-xl font-bold text-slate-900">
                                    Avaliações ({reviews.length})
                                </h2>
                            </div>
                            <Button className="bg-indigo-600 hover:bg-indigo-700 text-white">
                                Escrever Avaliação
                            </Button>
                        </div>

                        {reviews.length > 0 ? (
                            <div className="space-y-4">
                                {reviews.map((review) => (
                                    <div key={review.id} className="pb-4 border-b border-slate-200 last:border-0 last:pb-0">
                                        <div className="flex items-start justify-between mb-2">
                                            <div>
                                                <h3 className="font-semibold text-slate-900">{review.reviewerName}</h3>
                                                <p className="text-sm text-slate-500">{formatDate(review.createdAt)}</p>
                                            </div>
                                            <div className="flex items-center gap-0.5">
                                                {[...Array(5)].map((_, i) => (
                                                    <Star
                                                        key={i}
                                                        className={`w-4 h-4 ${i < review.rating
                                                            ? 'fill-amber-400 text-amber-400'
                                                            : 'text-slate-300'
                                                            }`}
                                                    />
                                                ))}
                                            </div>
                                        </div>
                                        <p className="text-slate-600 leading-relaxed">{review.comment}</p>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="text-center py-12">
                                <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-4">
                                    <Star className="w-8 h-8 text-slate-300" />
                                </div>
                                <p className="text-slate-500 mb-1">Ainda não há avaliações</p>
                                <p className="text-sm text-slate-400">Seja o primeiro a avaliar este carro!</p>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}
