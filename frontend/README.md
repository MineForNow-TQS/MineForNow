# MineForNow - Car Rental Platform

Platform for peer-to-peer car rental built with React. Allows users to rent cars and owners to list their vehicles.

## Prerequisites

- Node.js 16.x or higher
- npm or yarn package manager

## Installation

```bash
# Clone the repository
git clone <repository-url>

# Navigate to project directory
cd FrontendReact

# Install dependencies
npm install
```

## Running the Application

### Development Server

```bash
npm run dev
```

Application will be available at `http://localhost:3000`

### Production Build

```bash
# Create optimized build
npm run build

# Preview production build
npm run preview
```

## Project Structure

```
src/
├── components/
│   ├── ui/              # Reusable UI components
│   ├── home/            # Home page components
│   ├── car/             # Car-related components
│   └── dashboard/       # Dashboard components
├── pages/               # Application pages
├── services/            # API services (mock data)
├── contexts/            # React contexts
└── utils/               # Utility functions
```

## User Roles

### Rental (Customer)
- Search and filter cars
- Make reservations
- View booking history
- Apply to become an owner

### Owner
- All rental permissions
- Add and manage cars
- View reservation statistics
- Toggle car availability

### Admin
- All system permissions
- Manage users and roles
- Approve owner applications
- View platform statistics

## Application Routes

```
/                  Home page
/cars              Car listing with filters
/cars/:id          Car details
/login             User login
/register          User registration
/dashboard         User dashboard
/become-owner      Owner application
/add-car           Add new car (Owner only)
/edit-car/:id      Edit car (Owner only)
/checkout/:id      Reservation checkout
```

## Key Features

- User authentication system with role-based access
- Advanced car search with multiple filters
- Responsive design using Tailwind CSS
- Mock services for development (localStorage persistence)
- Owner application workflow with admin approval
- Car management system for owners

## Technology Stack

- React 18.2.0
- Vite 5.4.21
- React Router 6.20.0
- Tailwind CSS 3.3.6
- React Query 3.39.3
- Lucide React (icons)

## Default Accounts

The application includes pre-configured accounts for testing:

```
Admin:
  Email: admin@minefornow.com
  
Owner:
  Email: owner@minefornow.com
  
Rental:
  Email: rental@minefornow.com
```

Note: Password validation is not enforced in the mock authentication system.

## Development Notes

- Currently using localStorage for mock data persistence
- Backend integration planned for future user story implementation
- This frontend will be connected to a Spring Boot backend with comprehensive test coverage
- Images stored in `public/Images/`

## Project Status

This is the initial frontend implementation. The backend with proper API integration and testing infrastructure is under development. All mock services will be replaced with real API calls once the backend user stories are implemented.

## Project Information

Academic project developed for TQS (Software Testing and Quality) course at University of Aveiro, 2024/2025.