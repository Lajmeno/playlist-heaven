import { Suspense, useEffect } from "react";
import { Container } from "react-bootstrap";
import { Outlet, useNavigate } from "react-router-dom";
import Header from "./Header";
import NavigationBar from "./NavigationBar";


function App() {

    return (
        <Suspense fallback="loading..">
            <Container>
                <Header />
                <NavigationBar />
                <Outlet />
            </Container>
        </Suspense>
    );
}

export default App;
