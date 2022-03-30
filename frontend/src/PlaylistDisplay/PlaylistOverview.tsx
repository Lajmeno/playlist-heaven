import { useEffect, useState } from "react"
import PlaylistItem from "./PlaylistItem"
import { PlaylistsResponse } from "./PlaylistModel"




export default function PlaylistOverview() {

    const [playlists, setPlaylists] = useState([] as Array<PlaylistsResponse>)

    useEffect(() => {
        fetch('http://localhost:8080/api/playlists', {
            method: 'GET'
        })
        .then(request => request.json())
        .then(requestBody => setPlaylists(requestBody))
    }, [])

    return(
        <div>
            <h2>Your Playlists</h2>
            {playlists
            .map(item => <PlaylistItem name={item.name} images={item.images}/>)}
        </div>
    )
}