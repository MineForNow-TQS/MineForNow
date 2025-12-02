import React from 'react';

export const Sheet = ({ children }) => {
  return <>{children}</>;
};

export const SheetTrigger = React.forwardRef(({ children, asChild, ...props }, ref) => {
  return React.cloneElement(children, { ref, ...props });
});

export const SheetContent = ({ children, side = 'right', className = '', onClose }) => {
  const [isOpen, setIsOpen] = React.useState(true);
  
  React.useEffect(() => {
    const handleEscape = (e) => {
      if (e.key === 'Escape') {
        setIsOpen(false);
        onClose?.();
      }
    };
    
    document.addEventListener('keydown', handleEscape);
    return () => document.removeEventListener('keydown', handleEscape);
  }, [onClose]);

  if (!isOpen) return null;

  const sideStyles = {
    right: 'right-0 top-0 h-full',
    left: 'left-0 top-0 h-full',
    top: 'top-0 left-0 w-full',
    bottom: 'bottom-0 left-0 w-full',
  };

  return (
    <>
      <div 
        className="fixed inset-0 z-50 bg-black/50"
        onClick={() => {
          setIsOpen(false);
          onClose?.();
        }}
      />
      <div 
        className={`fixed z-50 bg-white shadow-lg p-6 ${sideStyles[side]} ${className}`}
      >
        {children}
      </div>
    </>
  );
};

Sheet.displayName = 'Sheet';
SheetTrigger.displayName = 'SheetTrigger';
SheetContent.displayName = 'SheetContent';
