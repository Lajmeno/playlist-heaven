import { useEffect, useState } from "react"
import { Button, Col, Container, Pagination, Row } from "react-bootstrap";
import PlaylistItem from "./PlaylistItem"
import { PlaylistsResponse } from "./PlaylistModel"




export default function PlaylistOverview() {

    const [playlists, setPlaylists] = useState([] as Array<PlaylistsResponse>);

    const[errorMessage, setErrorMessage] = useState("");

    const [page, setPage] = useState(1);

    const [paginationAmount, setPaginationAmount] = useState(1);

    const [amountItemsOnPage, setAmountItemsOnPage] = useState(12);

    useEffect(() => {
        fetchAll();
    }, [])

    const fetchAll = () => {
        fetch(`${process.env.REACT_APP_BASE_URL}/api/playlists`, {
            method: 'GET',
            headers:{
                "Authorization": `Bearer ${localStorage.getItem("jwt")}`
            } 
        })
        .then(request => {
            if(request.ok){
                return request.json();
            }
            throw new Error("Could not get Playlists from Backend")
        })
        .then(requestBody => {
            setPlaylists(requestBody);
            setPaginationAmount(Math.ceil(requestBody.length / amountItemsOnPage));
        })
        .catch(e => setErrorMessage(e.message));
    }

    const reloadPlaylists = () => {
        fetch(`${process.env.REACT_APP_BASE_URL}/api/spotify`, {
            method: 'GET',
            headers:{
                "Authorization": `Bearer ${localStorage.getItem("jwt")}`
            } 
        })
        .then(response => {
            if(response.ok){
                fetchAll();
                setErrorMessage("");
            }else {
                throw new Error("Coud not reload Playlists from Spotify")
            }
        })
        .catch(e => setErrorMessage(e.message));

    }


   
    let items = [];
    for (let number = 1; number <= paginationAmount; number++) {
        items.push(
        <Pagination.Item onClick={() => setPage(number)} key={number} active={number === page}>
        {number}
        </Pagination.Item>
        );
    };

    const paginationBasic = (
        <div>
            <Pagination>
                <Pagination.Prev onClick={() => page === 1 ? "" : setPage(page-1)}/>
                {items}
                <Pagination.Next onClick={() => (page < (paginationAmount - 1) ? setPage(page+1): "")}/>
            </Pagination> 
        </div>
    );


    return(
        <div>
            
            {errorMessage && {errorMessage}}
            <div>
                <Container>
                    <Row  md="auto" className="justify-content-md-center"><Button onClick={() => reloadPlaylists()}>Reload Your Playlists from Spotify</Button></Row>
                    <Row md="auto" className="justify-content-center">{paginationBasic}</Row>
                    <Row>
                        {playlists.length > 1 && 
                        playlists
                        .map((item, index) => {{
                            if(index < (page * amountItemsOnPage) && index >= ((page - 1) * amountItemsOnPage)){
                                return <Col><PlaylistItem name={item.name} key={`$(item.spotifyId}-${index}`} images={item.images} spotifyId={item.spotifyId}/></Col>
                            }
                        }})}
                     </Row>
             </Container>
             
            </div>
           
        </div>
    )
}