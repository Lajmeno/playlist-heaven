import axios from "axios";
import { useState } from "react";



export default function CreateSpotifyPlaylist() {


    const [playlistTitle, setPlaylistTitle] = useState('');
    const [file, setFile] = useState({} as File)
    const [errorMessage, setErrorMessage] = useState('');
    
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
            //props.onItemCreate()
        })
        .catch(error => {
            if (error.response.status === 422) {
                setErrorMessage('Nicht alle Einträge konnten importiert weren. Guck ins Log!!!');
            } else if (error.response.status === 400) {
                setErrorMessage('Die Einträge wurde nicht importiert. Guck ins Log!!!');
            }
        })
    };

    return(
        <div>
            <h2>Create Spotify Playlist from CSV</h2>

            <input type="text" placeholder="Playlist Title" value={playlistTitle} onChange={ev => setPlaylistTitle(ev.target.value)}  />

            <div>
                <input type="file" onChange={ev => setFile(ev.target.files![0])} />
            </div>
            <div><button onClick={() => performFileUpload(file)}>Add to Collection</button></div>
            {errorMessage && {errorMessage}}
        </div>
    )
}