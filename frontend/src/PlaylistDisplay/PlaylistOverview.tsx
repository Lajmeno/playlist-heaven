import { useEffect, useState } from "react"
import PlaylistItem from "./PlaylistItem"
import { PlaylistsResponse } from "./PlaylistModel"




export default function PlaylistOverview() {

    const [playlists, setPlaylists] = useState([] as Array<PlaylistsResponse>)

    useEffect(() => {
        fetch(`${process.env.REACT_APP_BASE_URL}/api/playlists`, {
            method: 'GET',
            headers:{
                "Authorization": `Bearer ${localStorage.getItem("jwt")}`
            } 
        })
        .then(request => request.json())
        .then(requestBody => setPlaylists(requestBody))
    }, [])

    return(
        <div>
            <h2>Your Playlists</h2>
            {playlists.length > 1 && 
            playlists
            .map(item => <PlaylistItem name={item.name} key={item.spotifyId} images={item.images} spotifyId={item.spotifyId}/>)}
        </div>
    )
}