import { ok } from "assert";
import axios from "axios";
import { useEffect, useState } from "react";
import { Button, Col, Container, Figure, Row, Table } from "react-bootstrap";
import { ArrowClockwise} from "react-bootstrap-icons";
import { useNavigate, useParams } from "react-router-dom"
import { PlaylistsResponse, PlaylistTrack } from "./PlaylistModel";
import TrackItem from "./TrackItem";


export default function PlaylistDetail(){

    const params = useParams();

    const [userId, setUserId] = useState("");

    const [playlist, setPlaylist] = useState({} as PlaylistsResponse);

    const [file, setFile] = useState({} as File)

    const [readyToRender, setReadyToRender] = useState(false);

    const [restoreWindow, setRestoreWindow] = useState(false);

    const [errorMessage, setErrorMessage] = useState("");

    const nav = useNavigate();

    useEffect(() => {
        let jwt = localStorage.getItem("jwt");
        if (jwt){
            const tokenDetails = JSON.parse(window.atob(jwt.split('.')[1]));
            setUserId(tokenDetails.sub);
        }
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

    const restoreFromCSV = (file: File | null) => {
        const fileData = new FormData();
        fileData.append('csv', file!);

        axios.patch(`${process.env.REACT_APP_BASE_URL}/api/spotify/${playlist.spotifyId}/${playlist.spotifyOwnerId}`, fileData, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem("jwt")}`,
                'Content-Type': 'multipart/form-data'
            },
            onUploadProgress:progressEvent => {
                console.log("Uploading : " + ((progressEvent.loaded / progressEvent.total) * 100).toString() + "%")
            }
        })
        .then(response => {
            if(response.status === 200){
                setPlaylist(response.data);
            }
        })
        .then(() => {
            setErrorMessage('');
        })
        .catch(error => {
            if (error.response.status === 400) {
                setErrorMessage('Playlist could not be restored. Please check the Log.');
            }
        })
    };


    return(
        <div>
            
            {errorMessage && <div>{errorMessage}</div>}     
            {readyToRender 
            && <Container className="p-0">
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
                        <Row className="mb-5"><h2>{playlist.name}</h2></Row>
                        <Row className="mb-5"><a href={`https://open.spotify.com/playlist/${playlist.spotifyId}`} target="_blank" rel="noreferrer noopener" ><Button>Open in Spotify</Button></a></Row>
                        <Row>{playlist.spotifyOwnerId === userId &&
                        <Col className="mb-10" md={{ span: 4, offset: 0 }}>
                            <Button className="mab-1" onClick={() => restoreWindow === true ? setRestoreWindow(false) : setRestoreWindow(true) }>Restore</Button>
                            </Col>}
                           </Row>
                           <Row className="bg-info">
                            {restoreWindow && <Row>
                                <Row></Row>
                            <Row><input type="file" onChange={ev => setFile(ev.target.files![0])} /></Row>
                            <Row><Col md={{ span: 2, offset: 0 }}><Button onClick={() => restoreFromCSV(file)}>Upload</Button></Col></Row></Row>}
                        </Row>
                    </Col>
                    <Col xl={{ span: "auto", offset: 4 }}>
                        <Row><Button onClick={() => downloadCSV()}>Download</Button></Row>
                        <Row><Button onClick={() => deleteFromDB()}>Delete</Button></Row>
                    </Col>
                    
                </Row>   
                <Row className="mb-2">
                    <Col></Col>
                    <Col xl={{ span: "auto", offset: 0 }}>
                        <Button variant="warning" onClick={() => reloadPlaylist()}><ArrowClockwise /> Reload</Button>
                    </Col>  
                </Row>     
                <Table striped bordered hover variant="dark" >
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