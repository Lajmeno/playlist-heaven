import { useEffect, useState } from "react";
import { Button, Col, Container, Figure, Row, Table } from "react-bootstrap";
import { ArrowClockwise} from "react-bootstrap-icons";
import { useNavigate, useParams } from "react-router-dom"
import { PlaylistsResponse, PlaylistTrack } from "./PlaylistModel";
import TrackItem from "./TrackItem";


export default function PlaylistDetail(){

    const params = useParams();

    const [playlist, setPlaylist] = useState({} as PlaylistsResponse);

    const[readyToRender, setReadyToRender] = useState(false);

    const [errorMessage, setErrorMessage] = useState("");

    const nav = useNavigate();

    useEffect(() => {
        fetch(`${process.env.REACT_APP_BASE_URL}/api/playlists/${params.id}`, {
            headers:{
                "Authorization": "Bearer"+ localStorage.getItem("jwt")
            }})
        .then(response => {
            if(!(response.status === 404)){
                return response.json()
            }
            throw new Error("There is no Playlist with the requested id");
        })
        .then(responseBody => {
            setPlaylist(responseBody); 
            setReadyToRender(true);
        })
        .catch((e) => {setErrorMessage(e.message)})

    }, [params.id]);

    const downloadCSV = () => {
        fetch(`${process.env.REACT_APP_BASE_URL}/api/csv`, {
            method: "POST",
            headers:{
                'Content-Type': 'application/json',
                "Authorization": "Bearer"+ localStorage.getItem("jwt")
            },
            body: JSON.stringify(playlist)
        })
        .then(resp => resp.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.style.display = "none";
            a.href = url;
            a.download = `${playlist.name}_${playlist.spotifyId}.csv`;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            alert("Your Playlist has been downloaded!");
        }) 
    }

    const deleteFromDB = () => {
        fetch(`${process.env.REACT_APP_BASE_URL}/api/playlists/${params.id}`, {
            method: "DELETE",
            headers:{
                'Content-Type': 'application/json',
                "Authorization": "Bearer"+ localStorage.getItem("jwt")
            }
        })
        .then(response => {
            if(!(response.status === 404)){
                return response;
            }
            throw new Error("Playlist could not be deleted");
        })
        .then(() => {
            nav('/overview'); 
        })
        .catch((e) => {setErrorMessage(e.message)})
    } 

    const reloadPlaylist = () => {
        fetch(`${process.env.REACT_APP_BASE_URL}/api/spotify/${params.id}`, {
            method: "PUT",
            headers:{
                "Authorization": "Bearer"+ localStorage.getItem("jwt")
            }
        })
        .then(response => {
            if(!(response.status === 400)){
                return response.json();
            }
            throw new Error("Playlist could not be reloaded from Spotify");
        })
        .then(responseBody => setPlaylist(responseBody))
        .catch((e) => {setErrorMessage(e.message)})
    }


    return(
        <div>
            
            {errorMessage && <div>{errorMessage}</div>}     
            {readyToRender 
            && <Container>
                <Row></Row>
                <Row className="row-no-margin">
                    <Col md={{ span: 4, offset: 0 }}>
                        <Figure>
                            <Figure.Image
                                width={321}
                                height={340}
                                alt="playlist-image"
                                src={playlist.images.length > 0 ? (playlist.images.length > 1 ? playlist.images[1].url : playlist.images[0].url) :require('../images/default-image.png') }
                            />
                        </Figure>
                    </Col>
                    <Col md={{ span: 5, offset: 0 }}><h3>{playlist.name}</h3></Col>
                    <Col></Col>
                    <Col xs={{ order: 'last' }}>
                        <Row><Button onClick={() => downloadCSV()}>Download</Button></Row>
                        <Row><Button onClick={() => deleteFromDB()}>Delete</Button></Row>
                    </Col>
                    
                </Row>   
                <Row className="row-little-margin">
                    <Col xl={{ span: 2, offset: 4, order: 'last' }}>
                        <Button variant="warning" onClick={() => reloadPlaylist()}><ArrowClockwise /> Reload</Button>
                    </Col>
                    
                </Row>     
                <Table striped bordered hover variant="dark" className="table-no-margin" >
                    <thead>
                        <tr> 
                        <th>#</th>
                        <th>Title</th>
                        <th>Artists</th>
                        <th>Album</th>
                        <th>Release Date</th>
                        </tr>
                    </thead>
                    <tbody>
                    {playlist
                    .tracks
                    .map((item : PlaylistTrack, index) => <TrackItem index={index} title={item.title} key={item.spotifyUri} artists={item.artists}
                    album={item.album} albumReleaseDate={item.albumReleaseDate} />)}
                        </tbody>
                </Table>

            
            </Container>}
        </div>
    )
}