import { Container, Nav, Navbar} from "react-bootstrap";
import { useLocation} from "react-router-dom";
import './NavigationBar.css';


export default function NavigationBar(){

  const logout = () => {
    localStorage.removeItem("jwt");
  }

  const location = useLocation();
  const { pathname } = location;
  const splitLocation = pathname.split("/");

  return(
      
      <Navbar bg="dark" variant="dark">
          <Container>
              <Navbar.Brand className="" href="/">PlaylistHeaven</Navbar.Brand>
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
          </Container>
      </Navbar>
    
);
}