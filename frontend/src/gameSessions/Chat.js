import React, { useState } from 'react';
import '../static/css/game/chat.css';

export default function Chat({ onSendMessage, messages }) {
    const [message, setMessage] = useState('');

    const handleKeyPress = (event) => {
        if (event.key === 'Enter' && message.trim()) {
            onSendMessage(message.trim());
            setMessage('');
        }
    };

    return (
        <div className="chat-container">
            <div className="messages-container">
                {Array.isArray(messages) && messages.map((msg, index) => (
                    <div key={index} className="message-item">
                        <strong>{msg.senderUsername}:</strong> {msg.message}
                    </div>
                ))}
            </div>

            <input
                type="text"
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="Send a message..."
                className="chat-input"
            />
        </div>
    );
}
