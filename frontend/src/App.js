import React, { useEffect, useState } from "react";
import { Route, Routes } from "react-router-dom";
import jwt_decode from "jwt-decode";
import { ErrorBoundary } from "react-error-boundary";
import AppNavbar from "./AppNavbar";
import Home from "./home";
import PrivateRoute from "./privateRoute";
import tokenService from "./services/token.service";
import UserListAdmin from "./admin/users/UserListAdmin";
import UserEditAdmin from "./admin/users/UserEditAdmin";
import GameSessionsList from "./gameSessions/GameSessionsList";
import GameSessionCreate from "./gameSessions/GameSessionCreate";
import AchievementList from "./achievements/achievementList";
import AchievementEdit from "./achievements/achievementEdit";
import UserProfileScreen from "./profile/UserProfileScreen.js";
import Game from "./gameSessions/Game.js";
import GameSessionsListAdminFinished from "./gameSessionsAdmin/gameSessionListAdminFinished.js";
import GameSessionsListAdminCurrent from "./gameSessionsAdmin/gameSessionListAdminCurrent.js";
import AchievementListAdmin from "./achievements/achievementListAdmin.js";
import MyGamesList from "./gameSessions/myGames/MyGamesList.js";
import StatisticsList from "./statistics/statisticsList.js";
import Social from "./friendships/Social.js";
import Chat from "./gameSessions/ChatWS.js";
import SoundController from './SoundController.js';
import "./static/css/app/app.css";
import CombinedRanking from "./ranking/index.js";

function ErrorFallback({ error, resetErrorBoundary }) {
  return (
    <div role="alert">
      <p>Something went wrong:</p>
      <pre>{error.message}</pre>
      <button onClick={resetErrorBoundary}>Try again</button>
    </div>
  );
}

function App() {
  const [easterEggText, setEasterEggText] = useState("");
  const jwt = tokenService.getLocalAccessToken();
  const [onlineNotifications, setOnlineNotifications] = useState([]);
  const [user, setUser] = useState({});
  const [authorities, setAuthorities] = useState([]);
  const [friends, setFriends] = useState([]);
  const role = authorities[0];

  console.log(onlineNotifications)
  function showNotification(message) {
    const id = Date.now();
    setOnlineNotifications(prevNotifications => [...prevNotifications, { id, message }]);

    setTimeout(() => {
      setOnlineNotifications(prevNotifications => prevNotifications.filter(notif => notif.id !== id));
    }, 5000);
  }
  function checkFriendsOnline(friendsList) {
    setOnlineNotifications(prevNotifications => {
      const currentMessages = prevNotifications.map(notif => notif.message);

      friendsList.forEach(friend => {
        const message = `${friend.username} is connected`;
        if (friend.online && !currentMessages.includes(message)) {
          const id = Date.now();
          prevNotifications = [...prevNotifications, { id, message }];

          setTimeout(() => {
            setOnlineNotifications(prev =>
              prev.filter(notif => notif.id !== id)
            );
          }, 5000);
        }
      });

      return prevNotifications;
    });
  }

  useEffect(() => {
    if (user && user.username) {
      fetchFriends();
    }
  }, [user]);

  function fetchFriends() {
    role === "PLAYER" &&
      fetch(`/api/v1/friendship/friends?username=${user.username}`, {
        headers: { Authorization: `Bearer ${jwt}` },
      })
        .then(response => response.json())
        .then(data => {
          setFriends(data);
          checkFriendsOnline(data);
        })
        .catch(error =>
          console.error("Error fetching friends data:", error)
        );
  }


  useEffect(() => {
    if (jwt) {
      const decodedToken = jwt_decode(jwt);
      if (decodedToken.authorities) {
        setAuthorities(decodedToken.authorities);
      }

      fetch(`/api/v1/users/${decodedToken.sub}`, {
        headers: { 'Authorization': `Bearer ${jwt}` }
      })
        .then(response => response.json())
        .then(data => { setUser(data); })
        .catch(error => { console.error("There was an error fetching the user data!", error); });
    }
  }, []);
  let roles = [];
  if (jwt) {
    roles = getRolesFromJWT(jwt);
  } function getRolesFromJWT(token) {
    return jwt_decode(token).authorities;
  }


  let adminRoutes = <></>;
  let playerRoutes = <></>;

  roles.forEach((role) => {
    if (role === "ADMIN") {
      adminRoutes = (
        <>
          <Route path="/users" element={<PrivateRoute><UserListAdmin /></PrivateRoute>} />
          <Route path="/users/:username" element={<PrivateRoute><UserEditAdmin /></PrivateRoute>} />
          <Route path="/gamesessions/:id" element={<PrivateRoute><GameSessionsList /></PrivateRoute>} />
          <Route path="/gamesessions/finishedGames" element={<PrivateRoute><GameSessionsListAdminFinished /></PrivateRoute>} />
          <Route path="/gamesessions/currentGames" element={<PrivateRoute><GameSessionsListAdminCurrent /></PrivateRoute>} />
          <Route path="/achievementsAdmin" element={<PrivateRoute><AchievementListAdmin /></PrivateRoute>} />
          <Route path="/achievements/:achievementId" element={<PrivateRoute><AchievementEdit /></PrivateRoute>} />

        </>
      );
    }
    if (role === "PLAYER") {
      playerRoutes = (
        <>
          <Route path="/gamesessions" element={<PrivateRoute><GameSessionsList /></PrivateRoute>} />
          <Route path="/mygames" element={<PrivateRoute><MyGamesList /></PrivateRoute>} />
          <Route path="/gamesessions/:id" element={<PrivateRoute><Game /></PrivateRoute>} />
          <Route path="/users" element={<PrivateRoute><UserListAdmin /></PrivateRoute>} />
          <Route path="/gamesessions/new" element={<PrivateRoute><GameSessionCreate /></PrivateRoute>} />
          <Route path="/achievements" element={<PrivateRoute><AchievementList /></PrivateRoute>} />
          <Route path="/social" element={<PrivateRoute><Social /></PrivateRoute>} />
          <Route path="/achievements/:achievementId" element={<PrivateRoute><AchievementEdit /></PrivateRoute>} />
          <Route path="/statistics" element={<PrivateRoute><StatisticsList /></PrivateRoute>} />
          <Route path="/ranking" element={<PrivateRoute><CombinedRanking /></PrivateRoute>} />
          <Route path="/profile" element={<PrivateRoute><UserProfileScreen /></PrivateRoute>} />
          <Route path="/profile/:username" element={<UserProfileScreen />} />
          <Route path="/chat" element={<Chat />} />
        </>
      );
    }
  });


  return (
    <div>
      <div className="notification-container">
        {onlineNotifications.map((notif) => (
          <div key={notif.id} className="notification">
            {notif.message}
          </div>
        ))}
      </div>
      <video
        autoPlay
        muted
        loop
        className="background-video"
      >
        <source src={require('./static/images/0103.mp4')} type="video/mp4" />
        Your browser does not support the video tag.
      </video>

      <ErrorBoundary FallbackComponent={ErrorFallback}>
        <AppNavbar />
        <Routes>
          <Route path="/" element={<Home />} />
          {adminRoutes}
          {playerRoutes}
        </Routes>
      </ErrorBoundary>

      <SoundController />
      {easterEggText && (
        <div className="easter-egg-text">
          {easterEggText}
        </div>
      )}
    </div>
  );

}

export default App;
