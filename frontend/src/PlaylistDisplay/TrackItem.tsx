import './Playlists.css'


interface DisplayArtist {
    name: string
}
interface TrackItemProps{
    title : string,
    artists : Array<DisplayArtist>,
    album: string,
    albumReleaseDate: string,
    index: number
}

export default function TrackItem(props:TrackItemProps){


    
    const displayArtistsNames= (items: Array<DisplayArtist>) => {
        let n = items.length;
        return(<>
            {items.map(item => {
                if(n > 1){
                    n-=1
                    return item.name + ", "
                }
                return item.name
            })}
            </>
        )
    }
    
    
    return(
        <tr>
            <td>{props.index + 1}</td>
            <td>{props.title}</td>
            <td>{displayArtistsNames(props.artists)}</td>
            <td>{props.album}</td>
            <td>{props.albumReleaseDate}</td>
        </tr>
    );
}