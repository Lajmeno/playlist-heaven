import { Suspense } from "react";
import { Outlet } from "react-router-dom";
import Header from "./Header";
import NavigationBar from "./NavigationBar";


function App() {

    return (
        <Suspense fallback="loading..">
                <Header />
                <NavigationBar />
                <Outlet />
        </Suspense>
    );
}

export default App;
