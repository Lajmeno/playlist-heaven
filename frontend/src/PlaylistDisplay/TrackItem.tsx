import { useEffect, useState } from "react";

interface DisplayArtist {
    name: string
}
interface TrackItemProps{
    title : string,
    artists : Array<DisplayArtist>,
    album: string,
    albumReleaseDate: string
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
        <div>{props.title} || {displayArtistsNames(props.artists)}  || {props.album} || {props.albumReleaseDate}</div>
    );
}