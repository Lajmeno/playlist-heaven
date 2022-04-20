import { Col, Container, Figure, Nav, Navbar, Row} from "react-bootstrap";
import { useLocation} from "react-router-dom";


export default function NavigationBar(){

  const logout = () => {
    localStorage.removeItem("jwt");
  }

  const location = useLocation();
  const { pathname } = location;
  const splitLocation = pathname.split("/");

  return(
    <Container>
            <Row>
              <Col xl={{span: 2, offset: 0}}>
                <Figure>
                  <Figure.Image
                      width={171}
                      height={180}
                      alt="171x180"
                      src={require("./images/logo.png")}
                  />
                </Figure>
              </Col>
              <Col xl={{span: 0, offset: 0}}>
                <Row></Row>
                <Row></Row>
                <Row></Row>
                <Row>
              <Navbar bg="dark" variant="dark">
                  <Navbar.Brand className="" href="/">
                  </Navbar.Brand>
              
              <Nav className="me-auto">
              <Nav.Link className={splitLocation[1] === "overview" ? "active" : ""} href="/overview">Overview</Nav.Link>
              <Nav.Link  className={splitLocation[1] === "search" ? "active" : ""} href="/search">Search</Nav.Link>
              <Nav.Link className={splitLocation[1] === "create" ? "active" : ""} href="/create">Create</Nav.Link>
              </Nav>
              <Navbar.Collapse className="justify-content-end">
            <Nav>
              <Nav.Link onClick={() => logout()} href="/login">Logout</Nav.Link>
              <Nav.Link href="/login">Login</Nav.Link>
            </Nav>
          </Navbar.Collapse>
          </Navbar>
          </Row>
          </Col>
          </Row>
      </Container> 
  );
}