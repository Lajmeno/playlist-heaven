import { useEffect, useState } from "react";
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


    return(
        <div>
            
            {errorMessage && <div>{errorMessage}</div>}
            
            <div>{readyToRender 
            && <div>
            <button onClick={() => downloadCSV()}>Download Playlist</button>
            <button onClick={() => deleteFromDB()}>Delete From your Collection</button>
            <h3>{playlist.name}</h3>
            <div> {playlist
                .tracks
                .map((item : PlaylistTrack) => <div><TrackItem title={item.title} key={item.spotifyUri} artists={item.artists}
                 album={item.album} albumReleaseDate={item.albumReleaseDate} /></div>)}
            </div></div>}
            </div>
        </div>
    )
}