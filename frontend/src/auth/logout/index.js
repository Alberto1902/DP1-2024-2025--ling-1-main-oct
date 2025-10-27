import React from "react";
import { Link } from "react-router-dom";
import "../../static/css/auth/authButton.css";
import "../../static/css/auth/authPage.css";
import tokenService from "../../services/token.service";

const jwt = tokenService.getLocalAccessToken();
const user = tokenService.getUser();

const Logout = ({ toggleLogoutModal }) => {
  function sendLogoutRequest() {
    fetch(`/api/v1/users/makeOffline?username=${user.username}`, {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
    })

    if (jwt || typeof jwt === "undefined") {
      tokenService.removeUser();
      window.location.href = "/";
    } else {
      alert("There is no user logged in");
    }
  }

  return (

    <>
      <h2 className="text-center text-md">
        Are you sure you want to log out?
      </h2>
      <div className="options-row">
        <Link className="auth-button" onClick={toggleLogoutModal}
        >
          No
        </Link>
        <button className="auth-button" onClick={() => sendLogoutRequest()}>
          Yes
        </button>
      </div>
    </>
  );
};

export default Logout;
