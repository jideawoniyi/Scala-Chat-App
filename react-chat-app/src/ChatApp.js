import React, { useState, useEffect } from 'react';
import { TextField, Button, Container, List, ListItem, ListItemText } from '@mui/material';
import './index.css'; // Make sure this path is correct

function ChatApp() {
  const [message, setMessage] = useState('');
  const [chat, setChat] = useState([]);
  const socket = useWebSocket('ws://localhost:8080/chat'); // Update with your server's WebSocket URL

  const sendMessage = () => {
    if (socket && message) {
      socket.send(JSON.stringify({ text: message }));
      setMessage('');
    }
  };

  useEffect(() => {
    if (socket) {
      socket.onmessage = (event) => {
        const newMessage = JSON.parse(event.data);
        setChat(prevChat => [...prevChat, newMessage]);
      };
    }
  }, [socket]);

  return (
    <Container maxWidth="sm" className="Container">
      <List className="List">
        {chat.map((msg, index) => (
          <ListItem key={index} className="ListItem">
            <ListItemText primary={msg.text} />
          </ListItem>
        ))}
      </List>
      <div className="MessageInput">
        <TextField 
          label="Message" 
          fullWidth 
          value={message} 
          onChange={(e) => setMessage(e.target.value)} 
          onKeyPress={(e) => e.key === 'Enter' && sendMessage()}
          margin="normal"
          className="TextField"
        />
        <Button 
          variant="contained" 
          color="primary" 
          onClick={sendMessage}
          className="Button"
        >
          Send
        </Button>
      </div>
    </Container>
  );
}

function useWebSocket(url) {
  const [socket, setSocket] = useState(null);

  useEffect(() => {
    const ws = new WebSocket(url);

    ws.onopen = () => {
      console.log("Connected to WebSocket");
    };

    ws.onclose = () => {
      console.log("Disconnected from WebSocket, attempting to reconnect...");
      setTimeout(() => {
        setSocket(null); // Trigger reconnection
      }, 3000);
    };

    setSocket(ws);

    return () => {
      ws.close();
    };
  }, [url, socket]);

  return socket;
}

export default ChatApp;
