import { useEffect } from "react"

export default function PlaylistOverview() {


    useEffect(() => {
        fetch('http://localhost:8080/api/playlists', {
            method: 'GET'
        })
    })

    return(
        <div>Hallo World!</div>
    )
}