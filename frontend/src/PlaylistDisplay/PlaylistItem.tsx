import { Card } from "react-bootstrap"
import { Link } from "react-router-dom"

import './Playlists.css'

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
                <Card style={{ width: '18rem' }} bg="dark" text="secondary">
                <Link className="link" to={`${props.spotifyId}`} style={{ textDecoration: 'none' }}>
                <Card.Img variant="top" src={props.images.length > 0 ? (props.images.length > 1 ? props.images[1].url : props.images[0].url) :"holder.js/100px180?text=Image cap" } />
                <Card.Body>
                    <Card.Title>{props.name}</Card.Title>
                </Card.Body>
                </Link>
                </Card>
        </div>
        
    )
    
}