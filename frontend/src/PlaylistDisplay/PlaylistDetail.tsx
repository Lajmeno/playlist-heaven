import { useEffect, useState } from "react";
import { useParams } from "react-router-dom"
import { PlaylistsResponse, PlaylistTrack } from "./PlaylistModel";
import TrackItem from "./TrackItem";


export default function PlaylistDetail(){

    const params = useParams();

    const [playlist, setPlaylist] = useState({} as PlaylistsResponse);

    const[readyToRender, setReadyToRender] = useState(false);

    const [errorMessage, setErrorMessage] = useState("");

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
        .then(responseBody => {setPlaylist(responseBody); setReadyToRender(true);})
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


    return(
        <div>
            
            {errorMessage && <div>{errorMessage}</div>}
            
            <div>{readyToRender 
            && <div>
            <button onClick={() => downloadCSV()}>Download Playlist</button>
            <h3>{playlist.name}</h3>
            <div> {playlist
                .tracks
                .map((item : PlaylistTrack) => <div><TrackItem title={item.title} artists={item.artists}
                 album={item.album} albumReleaseDate={item.albumReleaseDate} /></div>)}
            </div></div>}
            </div>
        </div>
    )
}