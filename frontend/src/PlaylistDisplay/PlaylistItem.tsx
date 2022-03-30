
interface PlaylistItemImage{
    url: string
}

interface PlaylistItemProps{
    name: string,
    images : Array<PlaylistItemImage> 
}

export default function PlaylistItem(props:PlaylistItemProps) {

    return(
        <div>
            
            <div><img src={props.images[1].url} alt="">
                </img></div>
                <h3>{props.name}</h3>
        </div>
    )
    
}