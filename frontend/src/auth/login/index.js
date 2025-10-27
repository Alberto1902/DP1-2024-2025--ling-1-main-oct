import React, { useState } from "react";
import { Alert } from "reactstrap";
import FormGenerator from "../../components/formGenerator/formGenerator";
import tokenService from "../../services/token.service";
import "../../static/css/auth/authButton.css";
import { loginFormInputs } from "./form/loginFormInputs";

export default function Login() {
  const [message, setMessage] = useState(null);
  const loginFormRef = React.createRef();

  async function handleSubmit({ values }) {
    const reqBody = values;
    setMessage(null);

    // Realizar la petición para iniciar sesión
    await fetch("/api/v1/auth/signin", {
      headers: { "Content-Type": "application/json" },
      method: "POST",
      body: JSON.stringify(reqBody),
    })
      .then(function (response) {
        if (response.status === 200) {
          return response.json();
        } else {
          return Promise.reject("Invalid login attempt");
        }
      })
      .then(async function (data) {
        // Guardar el token y los datos del usuario
        tokenService.setUser(data);
        tokenService.updateLocalAccessToken(data.token);

        // Hacer la llamada para cambiar el estado a 'online' (o 'offline', si es necesario)
        const username = data.username; // Suponiendo que el nombre de usuario esté en los datos de la respuesta

        // Llamada a la API para actualizar el estado a 'online'
        await fetch(`/api/v1/users/makeOnline?username=${username}`, {
          method: 'PUT',
          headers: {
            Authorization: `Bearer ${data.token}`,
            Accept: 'application/json',
            'Content-Type': 'application/json',
          },
        })
          .then((response) => {
            if (response.status === 200) {
              // Redirigir a la página de inicio después de un login exitoso
              window.location.href = "/";
            } else {
              return Promise.reject("Error updating user status");
            }
          })
          .catch((error) => {
            setMessage("Failed to update user status: " + error);
          });
      })
      .catch((error) => {
        setMessage(error);
      });
  }

  return (
      <>
        {message && <Alert color="primary">{message}</Alert>}
          <FormGenerator
            ref={loginFormRef}
            inputs={loginFormInputs}
            onSubmit={handleSubmit}
            numberOfColumns={1}
            listenEnterKey
            buttonText="Login"
            buttonClassName="auth-button"
          />
      </>
    );
}
