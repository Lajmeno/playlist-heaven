

export default function Login(){
    return (
        <div>
            <a href="https://accounts.spotify.com/authorize?response_type=code&client_id=80640b8612764947a6329d3103743e02&scope=user-read-private user-read-email playlist-read-private&redirect_uri=http://localhost:8080/api/callback"><button >Spotify-Login</button></a>
        </div>
    );
}