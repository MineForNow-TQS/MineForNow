import React from 'react';
import { Check } from 'lucide-react';

export const Checkbox = ({ 
  id, 
  checked = false, 
  onCheckedChange, 
  className = '',
  disabled = false 
}) => {
  return (
    <button
      type="button"
      id={id}
      role="checkbox"
      aria-checked={checked}
      disabled={disabled}
      onClick={() => onCheckedChange && onCheckedChange(!checked)}
      className={`
        w-5 h-5 rounded border-2 flex items-center justify-center
        transition-all duration-200
        ${checked 
          ? 'bg-indigo-600 border-indigo-600' 
          : 'bg-white border-slate-300 hover:border-indigo-400'
        }
        ${disabled ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'}
        focus:outline-none focus:ring-2 focus:ring-indigo-500/30
        ${className}
      `}
    >
      {checked && <Check className="w-3.5 h-3.5 text-white" />}
    </button>
  );
};

Checkbox.displayName = 'Checkbox';
