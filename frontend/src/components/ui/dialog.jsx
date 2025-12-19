import React, { useEffect } from 'react';
import { cn } from "@/lib/utils";
import { X } from "lucide-react";

export const Dialog = ({ children, open, onOpenChange }) => {
    useEffect(() => {
        const handleEscape = (e) => {
            if (e.key === 'Escape' && open) {
                onOpenChange(false);
            }
        };

        if (open) {
            document.body.style.overflow = 'hidden';
            document.addEventListener('keydown', handleEscape);
        } else {
            document.body.style.overflow = 'unset';
            document.removeEventListener('keydown', handleEscape);
        }

        return () => {
            document.body.style.overflow = 'unset';
            document.removeEventListener('keydown', handleEscape);
        };
    }, [open, onOpenChange]);

    if (!open) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
            {/* Backdrop */}
            <div
                className="fixed inset-0 bg-black/50 backdrop-blur-sm transition-opacity"
                onClick={() => onOpenChange(false)}
            />

            {/* Dialog Container to handle positioning if needed more robust */}
            {/* Passing open state down is managed by conditional rendering here for simplicity */}
            {children}
        </div>
    );
};

export const DialogContent = ({ children, className }) => {
    return (
        <div className={cn(
            "z-50 w-full max-w-lg rounded-lg border bg-white p-6 shadow-lg animate-in fade-in-0 zoom-in-95 data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=closed]:zoom-out-95",
            className
        )}>
            {children}
        </div>
    );
};

export const DialogHeader = ({ children, className }) => (
    <div className={cn("flex flex-col space-y-1.5 text-center sm:text-left", className)}>
        {children}
    </div>
);

export const DialogTitle = ({ children, className }) => (
    <h2 className={cn("text-lg font-semibold leading-none tracking-tight", className)}>
        {children}
    </h2>
);

export const DialogFooter = ({ children, className }) => (
    <div className={cn("flex flex-col-reverse sm:flex-row sm:justify-end sm:space-x-2", className)}>
        {children}
    </div>
);
