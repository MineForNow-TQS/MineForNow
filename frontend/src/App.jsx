import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import { AuthProvider } from './contexts/AuthContext';
import Layout from './components/Layout';

// Pages
import Home from './pages/Home';
import Cars from './pages/Cars';
import CarDetails from './pages/CarDetails';
import Checkout from './pages/Checkout';
import Payment from './pages/Payment';
import Dashboard from './pages/Dashboard';
import AddCar from './pages/AddCar';
import EditCar from './pages/EditCar';
import BecomeOwner from './pages/BecomeOwner';
import Register from './pages/Register';
import Login from './pages/Login';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <Router>
          <Routes>
            <Route path="/" element={<Layout currentPageName="Home"><Home /></Layout>} />
            <Route path="/cars" element={<Layout currentPageName="Cars"><Cars /></Layout>} />
            <Route path="/cars/:id" element={<Layout currentPageName="CarDetails"><CarDetails /></Layout>} />
            <Route path="/checkout" element={<Layout currentPageName="Checkout"><Checkout /></Layout>} />
            <Route path="/payment" element={<Layout currentPageName="Payment"><Payment /></Layout>} />
            <Route path="/dashboard" element={<Layout currentPageName="Dashboard"><Dashboard /></Layout>} />
            <Route path="/add-car" element={<Layout currentPageName="AddCar"><AddCar /></Layout>} />
            <Route path="/edit-car/:id" element={<Layout currentPageName="EditCar"><EditCar /></Layout>} />
            <Route path="/become-owner" element={<Layout currentPageName="BecomeOwner"><BecomeOwner /></Layout>} />
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />
          </Routes>
        </Router>
      </AuthProvider>
    </QueryClientProvider>
  );
}

export default App;
