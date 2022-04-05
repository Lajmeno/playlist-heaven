import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { PlaylistsResponse, PlaylistTrack } from "../PlaylistDisplay/PlaylistModel";
import TrackItem from "../PlaylistDisplay/TrackItem";


export default function SearchPlaylistDetail(){


    const params = useParams();

    const [playlist, setPlaylist] = useState({} as PlaylistsResponse);

    const[readyToRender, setReadyToRender] = useState("");

    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() => {
        fetch(`${process.env.REACT_APP_BASE_URL}/api/spotify/${params.id}`, {
            headers:{
                "Authorization": "Bearer"+ localStorage.getItem("jwt")
            }})
        .then(response => {
            if(!(response.status === 404)){
                return response.json()
            }
            throw new Error("There is no Playlist with the requested id");
        })
        .then(responseBody => {setPlaylist(responseBody); setReadyToRender("yes");})
        .catch((e) => {setErrorMessage(e.message)})

    }, [params.id]);

    const addToCollectio = () => {
        //here next issue
        
    }



    return(
        <div>
            
            {errorMessage && <div>{errorMessage}</div>}
            
            <div>{readyToRender 
            && <div>
            <button onClick={() => addToCollectio()}>Add to Collection</button>
            <h3>{playlist.name}</h3>
            {playlist
                .tracks
                .map((item : PlaylistTrack) => <div><TrackItem title={item.title} artists={item.artists}
                 album={item.album} albumReleaseDate={item.albumReleaseDate} /></div>)}
            </div>}
            </div>
        </div>
    );
}