import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { Checkbox } from "@/components/ui/checkbox";
import { Car, Mail, Lock, ArrowRight, Eye, EyeOff, Check } from "lucide-react";

export default function Login() {
    const navigate = useNavigate();
    const { login } = useAuth();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [rememberMe, setRememberMe] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const benefits = [
        "Acesso instantâneo a milhares de carros",
        "Reservas rápidas e seguras",
        "Suporte dedicado 24/7",
        "Cancelamento flexível"
    ];

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
    
        if (!email || !password) {
            setError('Por favor, preencha todos os campos');
            return;
        }
    
        setLoading(true);
        try {
            await login(email, password);
            navigate('/dashboard');
        } catch (err) {
            setError(err.message || 'Email ou password incorretos');
        } finally {
            setLoading(false);
        }
    };
    return (
        <div className="min-h-screen flex">
            {/* Left - Image */}
            <div className="hidden lg:block lg:flex-1 relative">
                <div className="absolute inset-0 bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
                    <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=1920')] bg-cover bg-center opacity-40" />
                    <div className="absolute inset-0 bg-gradient-to-l from-slate-900/80 to-transparent" />
                </div>
                
                <div className="relative z-10 flex flex-col justify-center h-full p-12">
                    <div className="transition-all duration-800">
                        <h2 className="text-4xl font-light text-white mb-8">
                            Bem-vindo de volta à
                            <span className="font-semibold block mt-2 bg-gradient-to-r from-indigo-400 to-blue-400 bg-clip-text text-transparent">
                                MineForNow
                            </span>
                        </h2>
                        
                        <div className="space-y-4">
                            {benefits.map((benefit, i) => (
                                <div
                                    key={i}
                                    className="flex items-center gap-3 transition-all duration-500"
                                    style={{ transitionDelay: `${300 + (i * 100)}ms` }}
                                >
                                    <div className="w-6 h-6 rounded-full bg-indigo-500/20 flex items-center justify-center">
                                        <Check className="w-4 h-4 text-indigo-400" />
                                    </div>
                                    <span className="text-slate-300">{benefit}</span>
                                </div>
                            ))}
                        </div>

                        <div className="mt-12 p-6 bg-white/5 backdrop-blur-sm rounded-2xl border border-white/10">
                            <p className="text-slate-300 text-sm leading-relaxed">
                                "O MineForNow transformou a forma como alugo carros. Simples, rápido e confiável!"
                            </p>
                            <div className="flex items-center gap-3 mt-4">
                                <div className="w-10 h-10 rounded-full bg-gradient-to-br from-indigo-500 to-blue-500 flex items-center justify-center">
                                    <span className="text-white text-sm font-medium">JM</span>
                                </div>
                                <div>
                                    <p className="text-white font-medium text-sm">João Mendes</p>
                                    <p className="text-slate-400 text-xs">Cliente desde 2023</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Right - Form */}
            <div className="flex-1 flex items-center justify-center p-6 sm:p-12">
                <div className="w-full max-w-md transition-all duration-500">
                    {/* Logo */}
                    <Link to="/" className="flex items-center gap-2 mb-12">
                        <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-indigo-600 to-blue-600 flex items-center justify-center">
                            <Car className="w-6 h-6 text-white" />
                        </div>
                        <span className="font-bold text-2xl text-slate-900">MineForNow</span>
                    </Link>

                    <div className="mb-8">
                        <h1 className="text-3xl font-bold text-slate-900 mb-2">Entrar na conta</h1>
                        <p className="text-slate-500">Continue a sua jornada connosco</p>
                    </div>

                    {error && (
                        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl text-red-600 text-sm">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div>
                            <Label className="text-slate-700">Email</Label>
                            <div className="relative mt-1">
                                <Mail className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                                <Input
                                    type="email"
                                    placeholder="seu@email.com"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    className="bg-white pl-12 h-12 rounded-xl border-slate-200 focus:border-indigo-500 focus:ring-indigo-500/20"
                                    required
                                />
                            </div>
                        </div>

                        <div>
                            <Label className="text-slate-700">Password</Label>
                            <div className="relative mt-1">
                                <Lock className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                                <Input
                                    type={showPassword ? "text" : "password"}
                                    placeholder="Digite a sua password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="bg-white pl-12 pr-12 h-12 rounded-xl border-slate-200 focus:border-indigo-500 focus:ring-indigo-500/20"
                                    required
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowPassword(!showPassword)}
                                    className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                                >
                                    {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                </button>
                            </div>
                        </div>

                        <div className="flex items-center justify-between">
                            <div className="flex items-center gap-2">
                                <Checkbox
                                    id="remember"
                                    checked={rememberMe}
                                    onCheckedChange={setRememberMe}
                                />
                                <Label htmlFor="remember" className="text-sm text-slate-600 cursor-pointer">
                                    Lembrar-me
                                </Label>
                            </div>
                            <a href="#" className="text-sm text-indigo-600 hover:text-indigo-700 hover:underline">
                                Esqueceu a password?
                            </a>
                        </div>

                        <Button 
                            type="submit"
                            disabled={loading}
                            className="w-full h-12 bg-gradient-to-r from-indigo-600 to-blue-600 hover:from-indigo-700 hover:to-blue-700 rounded-xl text-base font-medium shadow-lg shadow-indigo-500/25 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            {loading ? 'A entrar...' : 'Entrar'}
                            <ArrowRight className="w-5 h-5 ml-2" />
                        </Button>

                        <div className="relative my-6">
                            <Separator />
                            <span className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 bg-white px-4 text-sm text-slate-400">
                                ou
                            </span>
                        </div>

                        <Button 
                            type="button"
                            variant="outline" 
                            className="w-full h-12 rounded-xl border-slate-200 hover:bg-slate-50"
                        >
                            <img src="https://www.google.com/favicon.ico" alt="Google" className="w-5 h-5 mr-2" />
                            Entrar com Google
                        </Button>
                    </form>

                    <p className="text-center mt-8 text-slate-600">
                        Não tem conta?{' '}
                        <Link to="/register" className="text-indigo-600 font-medium hover:text-indigo-700">
                            Criar conta
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
}
