import React from 'react';

export const Separator = ({ 
  orientation = 'horizontal', 
  className = '' 
}) => {
  return (
    <div
      className={`
        ${orientation === 'horizontal' ? 'h-px w-full' : 'w-px h-full'}
        bg-slate-200
        ${className}
      `}
    />
  );
};

Separator.displayName = 'Separator';
