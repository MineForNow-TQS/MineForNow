import React, { useState, useRef, useEffect, createContext, useContext } from 'react';

const DropdownContext = createContext();

export const DropdownMenu = ({ children }) => {
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setOpen(false);
      }
    };

    const handleEscape = (event) => {
      if (event.key === 'Escape') {
        setOpen(false);
      }
    };

    if (open) {
      document.addEventListener('mousedown', handleClickOutside);
      document.addEventListener('keydown', handleEscape);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
      document.removeEventListener('keydown', handleEscape);
    };
  }, [open]);

  return (
    <DropdownContext.Provider value={{ open, setOpen }}>
      <div ref={dropdownRef} className="relative inline-block">
        {children}
      </div>
    </DropdownContext.Provider>
  );
};

export const DropdownMenuTrigger = React.forwardRef(({ children, asChild, ...props }, ref) => {
  const { open, setOpen } = useContext(DropdownContext);

  const handleClick = (e) => {
    e.stopPropagation();
    setOpen(!open);
  };

  return React.cloneElement(children, { 
    ref, 
    onClick: handleClick,
    ...props 
  });
});

export const DropdownMenuContent = ({ children, align = 'center', className = '' }) => {
  const { open, setOpen } = useContext(DropdownContext);

  if (!open) return null;

  const alignStyles = {
    start: 'left-0',
    center: 'left-1/2 -translate-x-1/2',
    end: 'right-0',
  };

  const handleItemClick = (originalOnClick) => (e) => {
    if (originalOnClick) {
      originalOnClick(e);
    }
    setOpen(false);
  };

  return (
    <div 
      className={`absolute mt-2 ${alignStyles[align]} z-50 min-w-[8rem] rounded-md border border-slate-200 bg-white p-1 shadow-md ${className}`}
    >
      {React.Children.map(children, (child) => {
        if (!child) return null;
        
        // Check if it's a DropdownMenuItem by checking displayName
        if (child.type && child.type.displayName === 'DropdownMenuItem') {
          return React.cloneElement(child, {
            onClick: handleItemClick(child.props.onClick),
          });
        }
        
        return child;
      })}
    </div>
  );
};

export const DropdownMenuItem = ({ children, className = '', onClick, ...props }) => {
  return (
    <div
      className={`relative flex cursor-pointer select-none items-center rounded-sm px-2 py-1.5 text-sm outline-none transition-colors hover:bg-slate-100 focus:bg-slate-100 ${className}`}
      onClick={onClick}
      {...props}
    >
      {children}
    </div>
  );
};

export const DropdownMenuSeparator = ({ className = '' }) => {
  return <div className={`-mx-1 my-1 h-px bg-slate-200 ${className}`} />;
};

DropdownMenu.displayName = 'DropdownMenu';
DropdownMenuTrigger.displayName = 'DropdownMenuTrigger';
DropdownMenuContent.displayName = 'DropdownMenuContent';
DropdownMenuItem.displayName = 'DropdownMenuItem';
DropdownMenuSeparator.displayName = 'DropdownMenuSeparator';
