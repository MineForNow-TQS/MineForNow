import React from 'react';
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

export default function Checkout() {
    return (
        <div className="min-h-screen bg-slate-50 py-8">
            <div className="max-w-3xl mx-auto px-4 sm:px-6">
                <h1 className="text-3xl font-bold text-slate-900 mb-8">Checkout</h1>
                
                <Card className="p-6">
                    <p className="text-slate-600">Funcionalidade de checkout em desenvolvimento...</p>
                    <Button className="mt-4">Confirmar Reserva</Button>
                </Card>
            </div>
        </div>
    );
}
