import React from 'react';

export const Button = React.forwardRef(({ 
  className = '', 
  variant = 'default', 
  size = 'default',
  children, 
  ...props 
}, ref) => {
  const baseStyles = 'inline-flex items-center justify-center rounded-lg font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50';
  
  const variants = {
    default: 'bg-indigo-600 text-white hover:bg-indigo-700 focus-visible:ring-indigo-600',
    outline: 'border border-slate-300 bg-white hover:bg-slate-50 text-slate-900',
    ghost: 'hover:bg-slate-100 text-slate-900',
    link: 'text-indigo-600 underline-offset-4 hover:underline',
  };
  
  const sizes = {
    default: 'h-10 px-4 py-2',
    sm: 'h-9 rounded-md px-3',
    lg: 'h-11 rounded-md px-8',
    icon: 'h-10 w-10',
  };
  
  return (
    <button
      ref={ref}
      className={`${baseStyles} ${variants[variant]} ${sizes[size]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
});

Button.displayName = 'Button';
