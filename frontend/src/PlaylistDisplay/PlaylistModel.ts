
interface PlaylistArtist{
    name : string
}

interface PlaylistTrack{
    title: string,
    artists : Array<PlaylistArtist>,
    album: string,
    albumReleaseDate : string
}

interface PlaylistImage{
    url: string
}

export interface PlaylistsResponse{
    name : string,
    tracks : Array<PlaylistTrack>,
    images : Array<PlaylistImage>,
    spotifyId: string

}