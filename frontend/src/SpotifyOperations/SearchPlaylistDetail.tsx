import { useEffect, useState } from "react";
import { Container, Row } from "react-bootstrap";
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
            <Row md="auto" className="justify-content-center"><button onClick={() => addToCollectio()}>Add to Collection</button>
            <h3>{playlist.name}</h3>
            </Row>
            <Row>
            {playlist
                .tracks
                .map((item : PlaylistTrack) => <div><TrackItem title={item.title} artists={item.artists}
                 album={item.album} albumReleaseDate={item.albumReleaseDate} /></div>)}
                 </Row>
            </Container>}
            </Container>
        </div>
    );
}