import React from 'react';

const Footer = ({ user, genre }) => {
    return (
        <footer>
            <div id="root" data-user-id={user.providerId} data-genre={genre}></div>

            {/* React and ReactDOM from CDN */}
            <script src="https://unpkg.com/react@17/umd/react.production.min.js" crossorigin></script>
            <script src="https://unpkg.com/react-dom@17/umd/react-dom.production.min.js" crossorigin></script>
            {/* Babel for JSX support */}
            <script src="https://unpkg.com/babel-standalone@6/babel.min.js"></script>
            {/* Spotify Player Script */}
            <script src="https://sdk.scdn.co/spotify-player.js"></script>
            {/* Link the external JS file */}
            <script src="../app.js" type="text/babel"></script>
        </footer>
    );
};

export default Footer;