import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { Button } from "@/components/ui/button";
import { TrendingUp, Clock, Shield, ArrowRight } from "lucide-react";

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

const benefits = [
    {
        icon: TrendingUp,
        title: "Rendimento Extra",
        description: "Ganhe até €500/mês rentabilizando o seu carro parado"
    },
    {
        icon: Clock,
        title: "Flexibilidade Total",
        description: "Defina a sua disponibilidade e preços como quiser"
    },
    {
        icon: Shield,
        title: "Seguro Incluído",
        description: "Proteção completa para si e para o seu veículo"
    }
];

export default function BecomeOwnerSection() {
    const [contentRef, contentInView] = useInView();
    const [imageRef, imageInView] = useInView();

    return (
        <section className="py-20 px-4 sm:px-6 bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 relative overflow-hidden">
            <div className="absolute inset-0">
                <div className="absolute top-0 right-0 w-96 h-96 bg-indigo-500/10 rounded-full blur-3xl" />
                <div className="absolute bottom-0 left-0 w-96 h-96 bg-blue-500/10 rounded-full blur-3xl" />
            </div>

            <div className="max-w-7xl mx-auto relative z-10">
                <div className="grid lg:grid-cols-2 gap-12 items-center">
                    <div 
                        ref={contentRef}
                        className={`transition-all duration-1000 ${
                            contentInView ? 'opacity-100 translate-x-0' : 'opacity-0 -translate-x-10'
                        }`}
                    >
                        <h2 className="text-3xl sm:text-4xl md:text-5xl font-normal text-white mb-3">
                            Tem um carro parado?
                        </h2>
                        <h2 className="text-3xl sm:text-4xl md:text-5xl font-bold mb-6">
                            <span className="bg-gradient-to-r from-indigo-400 via-blue-400 to-indigo-500 bg-clip-text text-transparent">
                                Comece a rentabilizar.
                            </span>
                        </h2>
                        <p className="text-base sm:text-lg text-slate-300 mb-10 leading-relaxed">
                            Junte-se a centenas de proprietários que já estão a ganhar dinheiro extra 
                            partilhando os seus veículos com a comunidade MineForNow.
                        </p>

                        <div className="space-y-6 mb-10">
                            {benefits.map((benefit, index) => (
                                <div 
                                    key={index}
                                    className={`flex items-start gap-4 transition-all duration-700 ${
                                        contentInView ? 'opacity-100 translate-x-0' : 'opacity-0 -translate-x-10'
                                    }`}
                                    style={{ transitionDelay: `${(index + 1) * 150}ms` }}
                                >
                                    <div className="w-14 h-14 rounded-xl bg-white/10 backdrop-blur-sm flex items-center justify-center flex-shrink-0 group-hover:bg-white/20 transition-colors">
                                        <benefit.icon className="w-7 h-7 text-indigo-400" strokeWidth={1.5} />
                                    </div>
                                    <div>
                                        <h3 className="text-white font-bold text-lg mb-1">{benefit.title}</h3>
                                        <p className="text-slate-300 text-sm">{benefit.description}</p>
                                    </div>
                                </div>
                            ))}
                        </div>

                        <Link to="/become-owner">
                            <Button className="bg-gradient-to-r from-indigo-600 to-blue-600 hover:from-indigo-700 hover:to-blue-700 text-white px-8 py-6 text-base rounded-xl shadow-lg hover:shadow-xl hover:scale-105 transition-all">
                                Tornar-me Owner
                                <ArrowRight className="ml-2 w-5 h-5" />
                            </Button>
                        </Link>
                    </div>

                    <div 
                        ref={imageRef}
                        className={`relative transition-all duration-1000 ${
                            imageInView ? 'opacity-100 translate-x-0' : 'opacity-0 translate-x-10'
                        }`}
                    >
                        <div className="aspect-square rounded-3xl overflow-hidden shadow-2xl">
                            <img
                                src="/Images/photo-1449965408869-eaa3f722e40d.jpeg"
                                alt="Proprietário com carro"
                                className="w-full h-full object-cover hover:scale-105 transition-transform duration-700"
                            />
                        </div>
                        <div className="absolute -bottom-8 -left-8 bg-white rounded-2xl p-6 shadow-2xl border border-slate-100">
                            <div className="text-4xl font-bold bg-gradient-to-r from-indigo-600 to-blue-600 bg-clip-text text-transparent mb-1">€2.500+</div>
                            <div className="text-slate-600 text-sm font-medium">Ganhos médios/mês dos top owners</div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}
