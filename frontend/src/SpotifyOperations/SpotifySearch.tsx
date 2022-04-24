import { useState } from "react"
import { Button, Col, Container, FormControl, InputGroup, Row } from "react-bootstrap";
import PaginationBasic from "../PaginationBasic";
import PlaylistItem from "../PlaylistDisplay/PlaylistItem";
import { PlaylistsResponse } from "../PlaylistDisplay/PlaylistModel";


export default function SpotifySearch(){


    const [searchValue, setSearchValue] = useState("");

    const [playlists, setPlaylists] = useState([] as Array<PlaylistsResponse>)
    const [errorMessage, setErrorMessage] = useState("");

    const [page, setPage] = useState(1);

    const [paginationAmount, setPaginationAmount] = useState(0);

    const amountItemsOnPage = 15;

    const searchSpotify = () =>{
        fetch(`${process.env.REACT_APP_BASE_URL}/api/spotify/search/${searchValue}`, {
            method: "GET",
            headers:{
                'Content-Type': 'application/json',
                "Authorization": "Bearer"+ localStorage.getItem("jwt")
            }
        })
        .then(response => {return response.json()})
        .then(responseBody  => {
            if(responseBody.length > 1){
                return responseBody;   
            }
            throw new Error("Nothing found..");
         })
        .then(responseBody => {
            setPlaylists(responseBody); 
            setPaginationAmount(Math.ceil(responseBody.length / amountItemsOnPage));
            setPage(1);
            setErrorMessage("");
        })
        .catch((e:Error) => {setErrorMessage(e.message)})
    }

    
    return (
        <Container>
            <Row md="auto" className="justify-content-center"><h3>Search Playlists on Spotify</h3></Row>
            <Row></Row>
            <Row className="search-collection" >
                    <Col md={{ span: 4, offset: 4 }}>
                        <InputGroup className="mb-3" >
                        <InputGroup.Text className="text-white bg-primary" id="inputGroup-sizing-default" >Search</InputGroup.Text>
                            <FormControl
                            className="bg-light text-black"
                            value={searchValue}
                            onChange={v => {
                                setSearchValue(v.target.value);
                                }}
                            aria-label="Search"
                            aria-describedby="inputGroup-sizing-default"
                            />
                        </InputGroup>
                    </Col>
                    <Col md={{ span: 2, offset: 0 }}><Button onClick={() => searchSpotify()}>Start Search</Button></Col>
            </Row>
            
            <Row md="auto" className="justify-content-center">{<PaginationBasic amount={paginationAmount} page={page} setPage={setPage}/>}</Row>
                   
            
            <Row>{errorMessage && <div>{errorMessage}</div>}
                {playlists.length > 1 && playlists
                .map((item, index) => {
                    if(index < (page * amountItemsOnPage) && index >= ((page - 1) * amountItemsOnPage)){
                        return <Col><PlaylistItem name={item.name} key={`$(item.spotifyId}-${index}`} images={item.images} spotifyId={item.spotifyId}/></Col>;
                    }
                    return <></>;
                    })
                }
            </Row>
        </Container>
    )
}