import React, { useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import { Star, Loader2 } from 'lucide-react';

export function ReviewModal({ isOpen, onClose, onSubmit, isSubmitting }) {
    const [rating, setRating] = useState(0);
    const [hoveredRating, setHoveredRating] = useState(0);
    const [comment, setComment] = useState('');

    const handleSubmit = () => {
        if (rating === 0) return;
        onSubmit({ rating, comment });
    };

    const handleClose = () => {
        setRating(0);
        setHoveredRating(0);
        setComment('');
        onClose();
    };

    return (
        <Dialog open={isOpen} onOpenChange={(open) => !open && handleClose()}>
            <DialogContent className="sm:max-w-[500px]">
                <DialogHeader>
                    <DialogTitle className="text-2xl font-bold text-center">Avaliar Experiência</DialogTitle>
                </DialogHeader>

                <div className="flex flex-col items-center gap-6 py-4">
                    {/* Star Rating */}
                    <div className="flex gap-2">
                        {[1, 2, 3, 4, 5].map((star) => (
                            <button
                                key={star}
                                type="button"
                                className="focus:outline-none transition-transform hover:scale-110"
                                onClick={() => setRating(star)}
                                onMouseEnter={() => setHoveredRating(star)}
                                onMouseLeave={() => setHoveredRating(0)}
                            >
                                <Star
                                    className={`w-10 h-10 ${star <= (hoveredRating || rating)
                                            ? 'fill-yellow-400 text-yellow-400'
                                            : 'fill-transparent text-slate-300'
                                        } transition-colors duration-200`}
                                />
                            </button>
                        ))}
                    </div>
                    <p className="text-sm text-slate-500 font-medium">
                        {rating === 0 ? 'Selecione uma classificação' : `Você escolheu ${rating} estrela${rating > 1 ? 's' : ''}`}
                    </p>

                    {/* Comment Area */}
                    <Textarea
                        placeholder="Comentário (opcional)"
                        value={comment}
                        onChange={(e) => setComment(e.target.value)}
                        className="w-full min-h-[120px] resize-none"
                    />
                </div>

                <DialogFooter className="flex gap-2 sm:justify-center">
                    <Button variant="outline" onClick={handleClose} disabled={isSubmitting} className="w-full sm:w-auto">
                        Cancelar
                    </Button>
                    <Button
                        onClick={handleSubmit}
                        disabled={rating === 0 || isSubmitting}
                        className="w-full sm:w-auto bg-blue-600 hover:bg-blue-700"
                    >
                        {isSubmitting ? (
                            <>
                                <Loader2 className="mr-2 h-4 w-4 animate-spin" /> Submetendo...
                            </>
                        ) : (
                            'Submeter Avaliação'
                        )}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}
