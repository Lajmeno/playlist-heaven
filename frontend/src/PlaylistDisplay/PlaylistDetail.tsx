import { useEffect, useState } from "react";
import { useParams } from "react-router-dom"
import { PlaylistsResponse, PlaylistTrack } from "./PlaylistModel";
import TrackItem from "./TrackItem";


export default function PlaylistDetail(){

    const params = useParams();

    const [playlist, setPlaylist] = useState({} as PlaylistsResponse);

    const[readyToRender, setReadyToRender] = useState("");

    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() => {
        fetch(`${process.env.REACT_APP_BASE_URL}/api/playlists/${params.id}`, {
            headers:{
                "Authorization": "Bearer"+ localStorage.getItem("jwt")
            }})
        .then(response => {return response.json()})
        .then(responseBody  => {
            if(responseBody){
                return responseBody;   
            }
            throw new Error("There is no ToDo with the requested id");
         })
        .then(responseBody => {setPlaylist(responseBody); setReadyToRender("yes");})
        .catch((e:Error) => {setErrorMessage(e.message)})

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
        // the filename you want
        a.download = `${playlist.name}_${playlist.spotifyId}.csv`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        alert("your file has downloaded!")}) // or you know, something with better UX...
    }

    const downloadCSV_2 = () => {
        fetch(`${process.env.REACT_APP_BASE_URL}/api/csv/download`, {
            method: "GET",
            headers:{
                "Authorization": "Bearer"+ localStorage.getItem("jwt")
            }
        }) 
    }

    const downloadCSV_3 = () => {
        fetch(`${process.env.REACT_APP_BASE_URL}/api/csv`, {
            method: "GET",
            headers:{
                "Authorization": "Bearer"+ localStorage.getItem("jwt")
            }
        }) 

    }

    return(
        <div>
            {errorMessage && <div>{errorMessage}</div>}
            <button onClick={() => downloadCSV()}>Download Playlist</button>
            <button onClick={() => downloadCSV_3()}>getPlaylistAsCSV</button>
            <button onClick={() => downloadCSV_2()}>download</button>
            <div>Playlist Details here</div>
            <div>{playlist.name}</div>
            <div>{readyToRender 
            && <div> {playlist
                .tracks
                .map((item : PlaylistTrack) => <div><TrackItem title={item.title} artists={item.artists}
                 album={item.album} albumReleaseDate={item.albumReleaseDate} /></div>)}
            </div>}
            </div>
        </div>
    )
}