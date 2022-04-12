import { useEffect, useState } from "react"
import PlaylistItem from "./PlaylistItem"
import { PlaylistsResponse } from "./PlaylistModel"




export default function PlaylistOverview() {

    const [playlists, setPlaylists] = useState([] as Array<PlaylistsResponse>);

    const[errorMessage, setErrorMessage] = useState("");

    useEffect(() => {
        fetchAll();
    }, [])

    const fetchAll = () => {
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
        .then(requestBody => setPlaylists(requestBody))
        .catch(e => setErrorMessage(e.message));
    }

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

    return(
        <div>
            <h2>Your Spotify Playlists</h2>
            <div><button onClick={() => reloadPlaylists()}>Reload Your Playlists from Spotify</button></div>
            {errorMessage && {errorMessage}}
            <div>
                {playlists.length > 1 && 
                playlists
                .map(item => <PlaylistItem name={item.name} key={item.spotifyId} images={item.images} spotifyId={item.spotifyId}/>)}
            </div>
        </div>
    )
}