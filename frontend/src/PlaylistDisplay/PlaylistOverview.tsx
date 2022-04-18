import { useCallback, useEffect, useState } from "react"
import { Button, Col, Container, FormControl, InputGroup, Row } from "react-bootstrap";
import PaginationBasic from "../PaginationBasic";
import PlaylistItem from "./PlaylistItem"
import { PlaylistsResponse } from "./PlaylistModel"
import './Playlists.css'


export default function PlaylistOverview() {

    const [playlists, setPlaylists] = useState([] as Array<PlaylistsResponse>);

    const [errorMessage, setErrorMessage] = useState("");

    const [page, setPage] = useState(1);

    const [paginationAmount, setPaginationAmount] = useState(1);

    const amountItemsOnPage = 15;

    const [searchOn, setSearchOn] = useState(false);
    const [searchValue, setSearchValue] = useState("");

    const fetchAll = useCallback(() => {
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
            calcItemsAmount(requestBody, searchValue);
        })
        .catch(e => setErrorMessage(e.message));
    }, [searchValue]);

     useEffect(() => {
        fetchAll();
    }, [fetchAll])


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

    const calcItemsAmount = (items:Array<PlaylistsResponse>, searchValue:string) => {
        const numberOfItems = items.filter(ele => ele.name.toLowerCase().includes(searchValue.toLowerCase())).length;
        setPaginationAmount(Math.ceil(numberOfItems / amountItemsOnPage));
    }
   
    return(
        <div>
            {errorMessage && {errorMessage}}
            <div>
                <Container>
                    <Row></Row>
                    <Row>
                        <Col xl={{ span: 2, offset: 0 }}><Button className="custom-btn" onClick={() => {searchOn ? setSearchOn(false) : setSearchOn(true)}}>Search Collection</Button></Col>
                        <Col xl={{ span: 2, offset: 8 }}>
                            <Button onClick={() => reloadPlaylists()}>Reload your Spotify-Playlists</Button>
                        </Col>
                    </Row>
                    <Row className="search-collection" style={searchOn ? {} : {display:"none"} }>
                            <Col md={{ span: 4, offset: 0 }}>
                                <InputGroup className="mb-3" >
                                    <InputGroup.Text id="inputGroup-sizing-default" >Search</InputGroup.Text>
                                    <FormControl
                                    value={searchValue}
                                    onChange={v => {
                                        setSearchValue(v.target.value);
                                        calcItemsAmount(playlists, v.target.value);
                                        setPage(1);
                                        }}
                                    aria-label="Search"
                                    aria-describedby="inputGroup-sizing-default"
                                    />
                                </InputGroup>
                            </Col>
                    </Row>
                    <Row md="auto" className="justify-content-center">{<PaginationBasic amount={paginationAmount} page={page} setPage={setPage}/>}</Row>
                    <Row>
                        {playlists.length > 1 &&   
                        playlists
                        .filter(ele => ele.name.toLowerCase().includes(searchValue.toLowerCase()))
                        .map((item, index) => {
                            if(index < (page * amountItemsOnPage) && index >= ((page - 1) * amountItemsOnPage)){
                                return <Col><PlaylistItem name={item.name} key={`$(item.spotifyId}-${index}`} images={item.images} spotifyId={item.spotifyId}/></Col>;
                            }
                            return <></>;
                            })
                        }
                     </Row>
             </Container>
             
            </div>
           
        </div>
    )
}