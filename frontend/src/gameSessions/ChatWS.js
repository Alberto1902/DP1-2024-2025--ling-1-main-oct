import React, { useEffect, useState, useRef } from "react";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import "../static/css/game/chatWS.css";
import tokenService from "../services/token.service";

const user = tokenService.getUser();
const jwt = tokenService.getLocalAccessToken();

const Chat = ({ chatRoomId = 1, isOpen, onClose }) => {
  const [stompClient, setStompClient] = useState(null);
  const [message, setMessage] = useState("");
  const [messages, setMessages] = useState([]);
  const nickname = user.username;
  const messagesEndRef = useRef(null);

  useEffect(() => {
    const loadPreviousMessages = async () => {
      try {
        const response = await fetch(`/api/v1/chat/messages?id=${chatRoomId}`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwt}`,
          },
        });

        if (response.ok) {
          const roomMessages = await response.json();
          setMessages(roomMessages.map((msg) => ({
            senderUsername: msg.senderUsername,
            message: msg.message,
          })));
        } else {
          console.error("Error while retrieving messages", response.statusText);
        }
      } catch (error) {
        console.error("Error while retrieving messages: ", error);
      }
    };

    const socket = new SockJS("http://localhost:8080/socket-chat");
    const client = Stomp.over(socket);

    client.connect({}, () => {
      client.subscribe(`/topic/chat/${chatRoomId}`, (response) => {
        const newMessage = JSON.parse(response.body);
        setMessages((prev) => [...prev, newMessage]);
      });
      setStompClient(client);
    });

    loadPreviousMessages();

    return () => {
      if (client) client.disconnect();
    };
  }, [chatRoomId]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const sendMessage = async () => {
    if (stompClient && message.trim()) {
      const chatMessage = {
        senderUsername: nickname,
        message: message.trim(),
        chatRoomId,
      };

      try {
        const response = await fetch(`/api/v1/messages?senderId=${user.id}&chatRoomId=${chatRoomId}`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwt}`,
          },
          body: chatMessage.message,
        });

        if (!response.ok) {
          console.error("Error while sending the message:", response.statusText);
          alert("Error while sending the message. Try it again.");
        } else {
          stompClient.send(`/app/chat/${chatRoomId}`, {}, JSON.stringify(chatMessage));
          setMessage("");
        }
      } catch (error) {
        console.error("Error while sending the message:", error);
      }
    } else {
      alert("Couldn't send the message. Make sure the message is not empty.");
    }
  };

  if (!isOpen) return null;

  return (
    <div className="chat-overlay">
      <div className="chat-container">
        <div className="chat-header">
          <h3>Chat</h3>
          <button className="close-btn" onClick={onClose}>
            ✖
          </button>
        </div>
        <div className="messages-container">
          {messages.map((msg, index) => (
            <div key={index} className="message">
              <strong>{msg.senderUsername}</strong>: {msg.message}
            </div>
          ))}
          <div ref={messagesEndRef} />
        </div>
        <div className="input-container">
          <input
            type="text"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            onKeyPress={(e) => e.key === "Enter" && sendMessage()}
            placeholder="Send a message..."
          />
          <button onClick={sendMessage}>Enviar</button>
        </div>
      </div>
    </div>
  );
};

export default Chat;
