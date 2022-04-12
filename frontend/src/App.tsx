import { Suspense } from "react";
import { Outlet } from "react-router-dom";
import Header from "./Header";


function App() {

    return (
        <Suspense fallback="loading..">
        <div>
        <Header />
        <Outlet />
        </div>
        </Suspense>
    );
}

export default App;
