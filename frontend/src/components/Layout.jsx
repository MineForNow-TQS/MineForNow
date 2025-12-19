import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { createPageUrl } from '@/utils';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from "@/components/ui/button";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { 
    Menu, Car, User, LogIn, ChevronDown, Crown, Shield, 
    LayoutDashboard, LogOut, Search 
} from "lucide-react";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

export default function Layout({ children, currentPageName }) {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [isScrolled, setIsScrolled] = useState(false);
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

    useEffect(() => {
        const handleScroll = () => {
            setIsScrolled(window.scrollY > 20);
        };
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    const isHome = currentPageName === 'Home';
    const headerBg = isHome && !isScrolled 
        ? 'bg-transparent' 
        : 'bg-white/95 backdrop-blur-md shadow-sm border-b border-slate-200';
    const textColor = isHome && !isScrolled ? 'text-white' : 'text-slate-900';

    const handleLogin = () => {
        navigate('/login');
    };

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    const isOwner = user?.role === 'OWNER' || user?.user_role === 'ADMIN';
    const isAdmin = user?.role === 'ADMIN';

    return (
        <div className="min-h-screen bg-white">
            {/* Header */}
            <header className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${headerBg}`}>
                <div className="max-w-7xl mx-auto px-4 sm:px-6">
                    <div className="flex items-center justify-between h-16">
                        {/* Logo */}
                        <Link to="/" className="flex items-center gap-2">
                            <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-indigo-600 to-blue-600 flex items-center justify-center">
                                <Car className="w-5 h-5 text-white" />
                            </div>
                            <span className={`font-bold text-xl ${textColor}`}>
                                MineForNow
                            </span>
                        </Link>

                        {/* Desktop Navigation */}
                        <nav className="hidden md:flex items-center gap-6">
                            <Link 
                                to="/cars"
                                className={`flex items-center gap-2 ${textColor} hover:text-indigo-600 transition-colors`}
                            >
                                <Search className="w-4 h-4" />
                                Pesquisar
                            </Link>

                            {user ? (
                                <DropdownMenu>
                                    <DropdownMenuTrigger asChild>
                                        <Button variant="ghost" className={`${textColor} hover:bg-white/10`}>
                                            <div className="w-8 h-8 rounded-full bg-indigo-100 flex items-center justify-center mr-2">
                                                <span className="text-indigo-600 font-medium">
                                                    {user.full_name?.[0]?.toUpperCase() || 'U'}
                                                </span>
                                            </div>
                                            {user.full_name?.split(' ')[0]}
                                            <ChevronDown className="w-4 h-4 ml-1" />
                                        </Button>
                                    </DropdownMenuTrigger>
                                    <DropdownMenuContent align="end" className="w-56">
                                        <div className="px-2 py-1.5">
                                            <p className="text-sm font-medium">{user.full_name}</p>
                                            <p className="text-xs text-slate-500">{user.email}</p>
                                            <div className="flex items-center gap-1 mt-1">
                                                {isAdmin ? (
                                                    <span className="text-xs px-2 py-0.5 rounded-full bg-red-100 text-red-700">Admin</span>
                                                ) : isOwner ? (
                                                    <span className="text-xs px-2 py-0.5 rounded-full bg-amber-100 text-amber-700">Owner</span>
                                                ) : (
                                                    <span className="text-xs px-2 py-0.5 rounded-full bg-blue-100 text-blue-700">Rental</span>
                                                )}
                                            </div>
                                        </div>
                                        <DropdownMenuSeparator />
                                        <DropdownMenuItem className="cursor-pointer" onClick={() => navigate('/dashboard')}>
                                            <LayoutDashboard className="w-4 h-4 mr-2" />
                                            Painel
                                        </DropdownMenuItem>
                                        {!isOwner && (
                                            <DropdownMenuItem className="cursor-pointer" onClick={() => navigate('/become-owner')}>
                                                <Crown className="w-4 h-4 mr-2" />
                                                Tornar-me Owner
                                            </DropdownMenuItem>
                                        )}
                                        <DropdownMenuSeparator />
                                        <DropdownMenuItem onClick={handleLogout} className="cursor-pointer text-red-600">
                                            <LogOut className="w-4 h-4 mr-2" />
                                            Sair
                                        </DropdownMenuItem>
                                    </DropdownMenuContent>
                                </DropdownMenu>
                            ) : (
                                <Button 
                                    onClick={handleLogin}
                                    className="bg-indigo-600 hover:bg-indigo-700 text-white"
                                >
                                    <LogIn className="w-4 h-4 mr-2" />
                                    Entrar
                                </Button>
                            )}
                        </nav>

                        {/* Mobile Menu Trigger */}
                        <Button 
                            variant="ghost" 
                            size="icon" 
                            className={`md:hidden ${textColor}`}
                            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                        >
                            <Menu className="w-6 h-6" />
                        </Button>
                    </div>
                </div>
            </header>

            {/* Main Content */}
            <main className={currentPageName === 'Home' ? '' : 'pt-16'}>
                {children}
            </main>
        </div>
    );
}
