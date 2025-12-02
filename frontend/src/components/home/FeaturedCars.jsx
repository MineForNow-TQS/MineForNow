import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { useQuery } from 'react-query';
import { carService } from '@/services/carService';
import { Card } from "@/components/ui/card";
import { Star, MapPin, Fuel, Users, ArrowRight } from "lucide-react";
import { formatCurrency } from '@/utils';

function useInView(options = {}) {
    const [isInView, setIsInView] = useState(false);
    const ref = useRef(null);

    useEffect(() => {
        const observer = new IntersectionObserver(([entry]) => {
            if (entry.isIntersecting) {
                setIsInView(true);
            }
        }, { threshold: 0.1, ...options });

        if (ref.current) {
            observer.observe(ref.current);
        }

        return () => {
            if (ref.current) {
                observer.unobserve(ref.current);
            }
        };
    }, []);

    return [ref, isInView];
}

export default function FeaturedCars() {
    const [titleRef, titleInView] = useInView(); 

    const { data, isLoading } = useQuery('featured-cars', async () => {
        const result = await carService.list({ status: 'available' });
        return result.data.slice(0, 6);
    });

    const cars = data || [];

    if (isLoading) {
        return (
            <section className="py-20 px-4 sm:px-6 bg-white">
                <div className="max-w-7xl mx-auto">
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {[1, 2, 3].map((i) => (
                            <Card key={i} className="overflow-hidden animate-pulse">
                                <div className="h-56 w-full bg-slate-200 rounded-t-xl" />
                                <div className="p-5 space-y-3">
                                    <div className="h-6 bg-slate-200 rounded w-3/4" />
                                    <div className="h-4 bg-slate-200 rounded w-1/2" />
                                    <div className="h-8 bg-slate-200 rounded w-1/3" />
                                </div>
                            </Card>
                        ))}
                    </div>
                </div>
            </section>
        );
    }

    return (
        <section className="py-20 px-4 sm:px-6 bg-white">
            <div className="max-w-7xl mx-auto">
                <div 
                    ref={titleRef}
                    className={`text-center mb-14 transition-all duration-1000 ${
                        titleInView ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'
                    }`}
                >
                    <h2 className="text-3xl sm:text-4xl md:text-5xl font-normal text-slate-900 mb-3">
                        Carros em <span className="font-bold">Destaque</span>
                    </h2>
                    <p className="text-slate-600 text-base max-w-2xl mx-auto">
                        Descubra os veículos mais populares da nossa comunidade
                    </p>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-10">
                    {cars.map((car, index) => (
                        <CarCard key={car.id} car={car} index={index} />
                    ))}
                </div>

                {/* View All Button */}
                <div className="text-center mt-12">
                    <Link to="/cars">
                        <button className="inline-flex items-center gap-2 text-indigo-600 hover:text-indigo-700 font-medium transition-colors group">
                            Ver todos os carros
                            <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
                        </button>
                    </Link>
                </div>
            </div>
        </section>
    );
}

function CarCard({ car, index }) {
    const [cardRef, cardInView] = useInView();

    return (
        <div
            ref={cardRef}
            className={`transition-all duration-700 ${
                cardInView ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'
            }`}
            style={{ transitionDelay: `${index * 100}ms` }}
        >
            <Link to={`/cars/${car.id}`}>
                <Card className="group overflow-hidden border border-slate-200 hover:border-indigo-200 shadow-sm hover:shadow-2xl transition-all duration-500 bg-white h-full">
                    <div className="relative h-56 overflow-hidden">
                        <img
                            src={car.images?.[0] || '/Images/photo-1494976388531-d1058494cdd8.jpeg'}
                            alt={`${car.brand} ${car.model}`}
                            className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700"
                        />
                        <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-black/20 to-transparent" />
                        
                        {/* Badge - Corrigindo para o português */}
                        <span className="absolute top-4 left-4 bg-white text-slate-900 px-3 py-1.5 rounded-lg text-xs font-semibold shadow-lg">
                            {car.type === 'citadino' ? 'Citadino' : 
                             car.type === 'suv' ? 'SUV' : 
                             car.type === 'hatchback' ? 'Hatchback' : 
                             car.type === 'desportivo' ? 'Desportivo' : 'Sedan'}
                        </span>
                        
                        {/* Rating */}
                        {car.average_rating > 0 && (
                            <div className="absolute top-4 right-4 flex items-center gap-1 bg-white px-2.5 py-1.5 rounded-lg shadow-lg">
                                <Star className="w-3.5 h-3.5 fill-amber-400 text-amber-400" />
                                <span className="text-sm font-bold text-slate-900">{car.average_rating.toFixed(1)}</span>
                            </div>
                        )}
                    </div>
                    
                    <div className="p-5">
                        <h3 className="text-xl font-bold text-slate-900 mb-3">
                            {car.brand} {car.model}
                        </h3>
                        
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
                                {car.seats}
                            </span>
                        </div>
                        
                        <div className="flex items-center justify-between pt-3 border-t border-slate-100">
                            <div>
                                <span className="text-2xl font-bold text-slate-900">
                                    {formatCurrency(car.price_per_day).replace(',00', '')}
                                </span>
                                <span className="text-slate-500 text-sm">/dia</span>
                            </div>
                            <ArrowRight className="w-5 h-5 text-indigo-600 group-hover:translate-x-1 transition-transform" />
                        </div>
                    </div>
                </Card>
            </Link>
        </div>
    );
}