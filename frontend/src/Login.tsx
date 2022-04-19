import { Button, Col, Container, Figure, Row } from "react-bootstrap";
import './Login.css'


export default function Login(){


    return (
        <div className="login-header">
        <div className="login">
            <div className="login-logo">
            <Figure>
                <Figure.Image
                    width={371}
                    height={380}
                    alt="171x180"
                    src={require("./images/logo.png")}
                />
            </Figure>
            <Container>
                <Row>
                    <a href={`https://accounts.spotify.com/authorize?response_type=code&client_id=80640b8612764947a6329d3103743e02&scope=user-read-private user-read-email playlist-read-private playlist-read-collaborative playlist-modify-public playlist-modify-private&redirect_uri=${process.env.REACT_APP_CALLBACK_URL}`}><Button >Spotify-Login</Button></a>
                </Row>
             <Row >
                <a href="/overview"><Button>Home</Button></a>
            </Row>
            </Container>
            </div>

        </div>
        
        </div>
    );
}