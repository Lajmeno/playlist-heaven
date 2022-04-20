import { useEffect, useState } from "react";
import { Button, Col, Container, Figure, Row, Table } from "react-bootstrap";
import { useParams } from "react-router-dom";
import { PlaylistsResponse, PlaylistTrack } from "../PlaylistDisplay/PlaylistModel";
import TrackItem from "../PlaylistDisplay/TrackItem";


export default function SearchPlaylistDetail(){


    const params = useParams();

    const [playlist, setPlaylist] = useState({} as PlaylistsResponse);

    const[readyToRender, setReadyToRender] = useState(false);

    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() => {
        fetch(`${process.env.REACT_APP_BASE_URL}/api/spotify/${params.id}`, {
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
            setErrorMessage("");
        })
        .catch((e) => {setErrorMessage(e.message)})

    }, [params.id]);

    const addToCollectio = () => {
        fetch(`${process.env.REACT_APP_BASE_URL}/api/playlists`, {
            method: "POST",
            headers:{
                'Content-Type': 'application/json',
                "Authorization": "Bearer"+ localStorage.getItem("jwt")
            },
            body: JSON.stringify(playlist)
        })
        .then(response => {
            if((response.status === 400)){
                throw new Error("Playlist already exists in Collection");
            }
            setErrorMessage("");
        })
        .catch((e) => {setErrorMessage(e.message)})
    }



    return(
        <div>
            
            {errorMessage && <div>{errorMessage}</div>}
            
            <Container>{readyToRender 
            && <Container>
            <Row></Row>
                <Row className="mb-0" xs="auto" md={4} lg={12}>
                    <Col xl={{ span: 3, offset: 0 }}>
                        <Figure>
                            <Figure.Image
                                width={321}
                                height={340}
                                alt="playlist-image"
                                src={playlist.images.length > 0 ? (playlist.images.length > 1 ? playlist.images[1].url : playlist.images[0].url) :require('../images/default-image.png') }
                            />
                        </Figure>
                    </Col>
                    <Col lg={{ span: 4, offset: 0 }}>
                        <Row><h2>{playlist.name}</h2></Row>
                        <Row><a href={`https://open.spotify.com/playlist/${playlist.spotifyId}`} target="_blank" rel="noreferrer noopener" ><Button>Open in Spotify</Button></a></Row>
                    </Col>
                    <Col xl={{ span: "auto", offset: 3 }}>
                        <Row><Button onClick={() => addToCollectio()}>Add to Collection</Button></Row>
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
                .map((item : PlaylistTrack, index) => <TrackItem index={index} title={item.title} artists={item.artists}
                 album={item.album} albumReleaseDate={item.albumReleaseDate} />)}
                 </tbody>
                 </Table>
            </Container>}
            </Container>
        </div>
    );
}