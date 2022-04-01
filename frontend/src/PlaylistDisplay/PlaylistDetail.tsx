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

    return(
        <div>
            {errorMessage && <div>{errorMessage}</div>}
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