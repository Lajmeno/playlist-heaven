import { Suspense } from "react";
import { Outlet } from "react-router-dom";
import Header from "./Header";
import Navigation from "./Navigation";


function App() {

    return (
        <Suspense fallback="loading..">
        <div>
        <Header />
        <Navigation />
        <Outlet />
        </div>
        </Suspense>
    );
}

export default App;
