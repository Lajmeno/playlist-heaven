import axios from "axios";
import { useState } from "react";
import { Button, Col, Container, FormControl, InputGroup, Row } from "react-bootstrap";
import { useNavigate } from "react-router-dom";



export default function CreateSpotifyPlaylist() {


    const [playlistTitle, setPlaylistTitle] = useState('');
    const [file, setFile] = useState({} as File)
    const [errorMessage, setErrorMessage] = useState('');

    const navigate = useNavigate();
    
    const performFileUpload = (file: File | null) => {
        const fileData = new FormData();
        fileData.append('csv', file!);

        axios.post(`${process.env.REACT_APP_BASE_URL}/api/spotify/${playlistTitle}`, fileData, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem("jwt")}`,
                'Content-Type': 'multipart/form-data'
            },
            onUploadProgress:progressEvent => {
                console.log("Uploading : " + ((progressEvent.loaded / progressEvent.total) * 100).toString() + "%")
            }
        })
        .then(() => {
            setErrorMessage('');
            navigate('/overview')
        })
        .catch(error => {
            if (error.response.status === 400) {
                setErrorMessage('Playlist could not be uploaded. Please check the Log.');
            }
        })
    };

    return(
        <Container>
            <Row md="auto" className="justify-content-center">
            <h2>Create New Spotify Playlist from a .csv-File</h2>
            </Row>
            <Row md="auto" className="justify-content-center">
                <Col  md={{ span: 3, offset: 0 }}>
                <InputGroup className="mb-3" >
                                <FormControl
                                className="bg-light text-black"
                                value={playlistTitle}
                                onChange={ev => setPlaylistTitle(ev.target.value)}
                                placeholder="Playlist Title"
                                aria-label="Search"
                                aria-describedby="inputGroup-sizing-default"
                                />
                            </InputGroup>
                        </Col>
            </Row>
            <Row lg="auto" className="justify-content-center">
                <Col  md={{ span: 2, offset: 2 }}>
                    <input type="file" onChange={ev => setFile(ev.target.files![0])} />
                </Col>
            </Row>
            <Row md="auto" className="justify-content-center">
                <Button onClick={() => performFileUpload(file)}>Add to Collection</Button>
                {errorMessage && {errorMessage}}
            </Row>
        </Container>
    )
}