import { Link } from "react-router-dom"

interface PlaylistItemImage{
    url: string
}

interface PlaylistItemProps{
    name: string,
    images : Array<PlaylistItemImage>,
    spotifyId: string
}

export default function PlaylistItem(props:PlaylistItemProps) {

    return(
        <div>
            
            <div>
                <Link to={`${props.spotifyId}`}>
                <img src={props.images[1].url} alt=""></img>
                </Link>
                </div>
                <h3>{props.name}</h3>
        </div>
    )
    
}