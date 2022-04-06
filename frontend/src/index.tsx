import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import App from './App';
import Login from './Login';
import PlaylistDetail from './PlaylistDisplay/PlaylistDetail';
import PlaylistOverview from './PlaylistDisplay/PlaylistOverview';
import SpotifySearch from './SpotifyOperations/SpotifySearch';
import reportWebVitals from './reportWebVitals';
import SearchPlaylistDetail from './SpotifyOperations/SearchPlaylistDetail';
import CreateSpotifyPlaylist from './SpotifyOperations/CreateSpotifyPlaylist';

ReactDOM.render(
    <React.StrictMode>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<App />}>
            <Route path="/login" element={<Login/>} />
            <Route path="/overview" element={<PlaylistOverview/>} />
            <Route path="/overview/:id" element={<PlaylistDetail/>} />
            <Route path="/search" element={<SpotifySearch/>} />
            <Route path="/search/:id" element={<SearchPlaylistDetail/>} />
            <Route path="/create" element={<CreateSpotifyPlaylist/>}/>
          </Route>
        </Routes>
      </BrowserRouter>
    </React.StrictMode>,
  document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
