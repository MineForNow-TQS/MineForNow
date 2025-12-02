import React, { useState, useEffect, useRef } from 'react';
import { Search, CalendarCheck, Car, Shield } from "lucide-react";

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

const steps = [
    {
        icon: Search,
        title: "Pesquise",
        description: "Encontre o carro perfeito na sua área, com filtros por tipo, preço e características."
    },
    {
        icon: CalendarCheck,
        title: "Reserve",
        description: "Escolha as datas, contacte o proprietário e confirme a sua reserva em minutos."
    },
    {
        icon: Car,
        title: "Conduza",
        description: "Recolha o carro no local combinado e aproveite a sua viagem."
    },
    {
        icon: Shield,
        title: "Segurança",
        description: "Todos os alugueres incluem seguro e suporte 24/7 para a sua tranquilidade."
    }
];

export default function HowItWorks() {
    const [titleRef, titleInView] = useInView();

    return (
        <section className="py-20 px-4 sm:px-6 bg-slate-50">
            <div className="max-w-7xl mx-auto">
                <div 
                    ref={titleRef}
                    className={`text-center mb-16 transition-all duration-1000 ${
                        titleInView ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'
                    }`}
                >
                    <h2 className="text-3xl sm:text-4xl md:text-5xl font-normal text-slate-900 mb-3">
                        Como <span className="font-bold">Funciona</span>
                    </h2>
                    <p className="text-slate-600 text-base max-w-2xl mx-auto">
                        Alugar um carro nunca foi tão simples. Siga estes passos e comece já a sua aventura.
                    </p>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
                    {steps.map((step, index) => (
                        <StepCard key={index} step={step} index={index} />
                    ))}
                </div>
            </div>
        </section>
    );
}

function StepCard({ step, index }) {
    const [cardRef, cardInView] = useInView();

    return (
        <div
            ref={cardRef}
            className={`relative text-center group transition-all duration-700 ${
                cardInView ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'
            }`}
            style={{ transitionDelay: `${index * 150}ms` }}
        >
            {index < 3 && (
                <div className="hidden lg:block absolute top-14 left-1/2 w-full h-0.5 bg-gradient-to-r from-indigo-200 via-indigo-300 to-transparent" />
            )}
            <div className="relative inline-flex items-center justify-center w-28 h-28 rounded-2xl bg-gradient-to-br from-indigo-50 to-blue-50 mb-6 group-hover:from-indigo-100 group-hover:to-blue-100 group-hover:scale-110 transition-all duration-300 shadow-md">
                <step.icon className="w-12 h-12 text-indigo-600" strokeWidth={1.5} />
                <div className="absolute -top-3 -right-3 w-10 h-10 rounded-full bg-gradient-to-br from-indigo-600 to-blue-600 text-white text-lg font-bold flex items-center justify-center shadow-lg">
                    {index + 1}
                </div>
            </div>
            <h3 className="text-xl font-bold text-slate-900 mb-3">{step.title}</h3>
            <p className="text-slate-600 text-sm leading-relaxed">{step.description}</p>
        </div>
    );
}
