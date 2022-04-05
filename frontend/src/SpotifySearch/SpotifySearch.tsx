import { useState } from "react"
import PlaylistItem from "../PlaylistDisplay/PlaylistItem";
import { PlaylistsResponse } from "../PlaylistDisplay/PlaylistModel";


export default function SpotifySearch(){


    const [searchItem, SetSearchItem] = useState("");

    const [playlists, setPlaylists] = useState([] as Array<PlaylistsResponse>)
    const [errorMessage, setErrorMessage] = useState("");

    const searchSpotify = () =>{
        fetch(`${process.env.REACT_APP_BASE_URL}/api/spotify/search/${searchItem}`, {
            method: "GET",
            headers:{
                'Content-Type': 'application/json',
                "Authorization": "Bearer"+ localStorage.getItem("jwt")
            }
        })
        .then(response => {return response.json()})
        .then(responseBody  => {
            if(responseBody.length > 1){
                return responseBody;   
            }
            throw new Error("Nothing found..");
         })
        .then(responseBody => {setPlaylists(responseBody); setErrorMessage("");})
        .catch((e:Error) => {setErrorMessage(e.message)})
    }
    
    return (
        <div>
            
            <input type="text" placeholder="Search for Playlists on Spotify" value={searchItem} onChange = {v => SetSearchItem(v.target.value)}/>
            <button onClick={() => searchSpotify()}>Search Playlist</button>

            <div>
            <h2>Your Playlists</h2>
            {errorMessage && <div>{errorMessage}</div>}
            {playlists.length > 1 && playlists
            .map(item => <PlaylistItem name={item.name} key={item.spotifyId} images={item.images} spotifyId={item.spotifyId}/>)}
        </div>
        </div>
    )
}