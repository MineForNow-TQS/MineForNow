import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { MapPin, Calendar as CalendarIcon, Search, User } from "lucide-react";

export default function HeroSearch() {
    const navigate = useNavigate();
    const [location, setLocation] = useState("");
    const [pickupDate, setPickupDate] = useState("");
    const [returnDate, setReturnDate] = useState("");
    const [age, setAge] = useState("18");
    const [visible, setVisible] = useState(false);
    const [pickupFocused, setPickupFocused] = useState(false);
    const [returnFocused, setReturnFocused] = useState(false);

    const handleAgeChange = (e) => {
        const value = e.target.value;
        if (value === "" || parseInt(value) >= 18) {
            setAge(value);
        }
    };

    useEffect(() => {
        setVisible(true);
    }, []);

    const handleSearch = () => {
        const params = new URLSearchParams();
        if (location) params.set('city', location);
        if (pickupDate) params.set('pickup', pickupDate);
        if (returnDate) params.set('return', returnDate);
        
        navigate('/cars?' + params.toString());
    };

    return (
        <div className="relative min-h-[90vh] flex flex-col items-center justify-center overflow-hidden bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
            {/* Background Image */}
            <div className="absolute inset-0">
                <div 
                    className="absolute inset-0 bg-cover bg-center opacity-30" 
                    style={{backgroundImage: 'url(/Images/photo-1503376780353-7e6692767b70.jpeg)'}} 
                />
                <div className="absolute inset-0 bg-gradient-to-t from-slate-900/80 via-slate-900/40 to-slate-900/60" />
            </div>

            <div className={`relative z-10 w-full max-w-6xl mx-auto px-4 sm:px-6 transition-all duration-1000 ${visible ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'}`}>
                <div className="text-center mb-8">
                    <h1 className="text-5xl sm:text-6xl md:text-7xl font-normal text-white mb-4 tracking-tight">
                        O seu carro.
                    </h1>
                    <h1 className="text-5xl sm:text-6xl md:text-7xl font-semibold mb-6 tracking-tight">
                        <span className="bg-gradient-to-r from-indigo-400 via-blue-400 to-indigo-500 bg-clip-text text-transparent">
                            A rentabilizar.
                        </span>
                    </h1>
                    <p className="text-base sm:text-lg text-slate-300 max-w-2xl mx-auto font-light">
                        Alugue carros diretamente de proprietários locais. Mais escolha, melhores preços, experiências únicas.
                    </p>
                </div>

                {/* Search Box */}
                <div className="bg-slate-800/60 backdrop-blur-xl rounded-2xl p-6 border border-white/10 shadow-2xl max-w-5xl mx-auto">
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                        {/* Location */}
                        <div className="space-y-2">
                            <label className="text-slate-300 text-xs font-medium flex items-center gap-1.5">
                                <MapPin className="w-4 h-4" />
                                Localização
                            </label>
                            <Input
                                placeholder="Lisboa, Porto, Faro..."
                                value={location}
                                onChange={(e) => setLocation(e.target.value)}
                                className="bg-slate-700/50 border-slate-600/50 text-white placeholder:text-slate-400 h-11 rounded-lg focus:bg-slate-700 focus:border-indigo-500 transition-all"
                            />
                        </div>

                        {/* Pickup Date */}
                        <div className="space-y-2">
                            <label className="text-slate-300 text-xs font-medium flex items-center gap-1.5">
                                <CalendarIcon className="w-4 h-4" />
                                Recolha
                            </label>
                            <div className="relative">
                                <Input
                                    type="date"
                                    value={pickupDate}
                                    onChange={(e) => setPickupDate(e.target.value)}
                                    min={new Date().toISOString().split('T')[0]}
                                    className="bg-slate-700/50 border-slate-600/50 h-11 rounded-lg focus:bg-slate-700 focus:border-indigo-500 transition-all pr-10 [&::-webkit-calendar-picker-indicator]:brightness-200 [&::-webkit-calendar-picker-indicator]:cursor-pointer text-transparent"
                                    style={{
                                        colorScheme: 'dark'
                                    }}
                                    onFocus={(e) => {
                                        setPickupFocused(true);
                                        e.target.showPicker();
                                    }}
                                    onBlur={() => setPickupFocused(false)}
                                />
                                {!pickupDate && !pickupFocused && (
                                    <span className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none text-sm">
                                        Selecionar data
                                    </span>
                                )}
                                {pickupDate && (
                                    <span className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-200 pointer-events-none text-sm">
                                        {new Date(pickupDate + 'T00:00:00').toLocaleDateString('pt-PT')}
                                    </span>
                                )}
                            </div>
                        </div>

                        {/* Return Date */}
                        <div className="space-y-2">
                            <label className="text-slate-300 text-xs font-medium flex items-center gap-1.5">
                                <CalendarIcon className="w-4 h-4" />
                                Entrega
                            </label>
                            <div className="relative">
                                <Input
                                    type="date"
                                    value={returnDate}
                                    onChange={(e) => setReturnDate(e.target.value)}
                                    min={pickupDate || new Date().toISOString().split('T')[0]}
                                    className="bg-slate-700/50 border-slate-600/50 h-11 rounded-lg focus:bg-slate-700 focus:border-indigo-500 transition-all pr-10 [&::-webkit-calendar-picker-indicator]:brightness-200 [&::-webkit-calendar-picker-indicator]:cursor-pointer text-transparent"
                                    style={{
                                        colorScheme: 'dark'
                                    }}
                                    onFocus={(e) => {
                                        setReturnFocused(true);
                                        e.target.showPicker();
                                    }}
                                    onBlur={() => setReturnFocused(false)}
                                />
                                {!returnDate && !returnFocused && (
                                    <span className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none text-sm">
                                        Selecionar data
                                    </span>
                                )}
                                {returnDate && (
                                    <span className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-200 pointer-events-none text-sm">
                                        {new Date(returnDate + 'T00:00:00').toLocaleDateString('pt-PT')}
                                    </span>
                                )}
                            </div>
                        </div>

                        {/* Age */}
                        <div className="space-y-2">
                            <label className="text-slate-300 text-xs font-medium flex items-center gap-1.5">
                                <User className="w-4 h-4" />
                                Idade do Condutor
                            </label>
                            <Input
                                type="number"
                                min="18"
                                value={age}
                                onChange={handleAgeChange}
                                className="bg-slate-700/50 border-slate-600/50 text-slate-200 h-11 rounded-lg focus:bg-slate-700 focus:border-indigo-500 transition-all"
                            />
                        </div>
                    </div>

                    {/* Search Button */}
                    <div className="mt-6">
                        <Button 
                            onClick={handleSearch}
                            className="w-full h-12 bg-gradient-to-r from-indigo-600 to-blue-600 hover:from-indigo-700 hover:to-blue-700 text-white rounded-lg font-medium text-base shadow-lg hover:shadow-xl transition-all"
                        >
                            <Search className="w-5 h-5 mr-2" />
                            Pesquisar Carros
                        </Button>
                    </div>
                </div>

                {/* Stats Section */}
                <div className="mt-16 grid grid-cols-3 gap-8 max-w-3xl mx-auto">
                    <div className="text-center">
                        <div className="text-3xl md:text-4xl font-bold text-white mb-1">500+</div>
                        <div className="text-sm text-slate-400">Carros Disponíveis</div>
                    </div>
                    <div className="text-center">
                        <div className="text-3xl md:text-4xl font-bold text-white mb-1">50+</div>
                        <div className="text-sm text-slate-400">Cidades</div>
                    </div>
                    <div className="text-center">
                        <div className="text-3xl md:text-4xl font-bold text-white mb-1">10K+</div>
                        <div className="text-sm text-slate-400">Clientes Satisfeitos</div>
                    </div>
                </div>
            </div>
        </div>
    );
}
