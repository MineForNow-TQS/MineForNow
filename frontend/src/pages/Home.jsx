import React from 'react';
import HeroSearch from '@/components/home/HeroSearch';
import FeaturedCars from '@/components/home/FeaturedCars';
import HowItWorks from '@/components/home/HowItWorks';
import BecomeOwnerSection from '@/components/home/BecomeOwnerSection';

import Footer from '@/components/Footer';

export default function Home() {
    return (
        <div className="min-h-screen bg-white">
            <HeroSearch />
            <FeaturedCars />
            <HowItWorks />
            <BecomeOwnerSection />
            <Footer />
        </div>
    );
}
