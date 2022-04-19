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
            <Card className="ml-3" style={{ width: '14rem' }} bg="secondary" text="secondary">
                <Link className="link" to={`${props.spotifyId}`} style={{ textDecoration: 'none' }}>
                    <Card.Img variant="top" src={props.images.length > 0 ? (props.images.length > 1 ? props.images[1].url : props.images[0].url) :require('../images/default-image.png') } />
                    <Card.Body>
                        <Card.Title style={{fontSize:"18px"}}>{props.name}</Card.Title>
                    </Card.Body>
                </Link>
            </Card>
        </div>
        
    )
    
}