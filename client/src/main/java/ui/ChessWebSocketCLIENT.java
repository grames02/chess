package ui;

import com.google.gson.Gson;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class ChessWebSocketCLIENT {

    private Session session;
    private final URI serverUri;

    public ChessWebSocketCLIENT(String serverUrl) throws Exception {
        this.serverUri = new URI(serverUrl);
        connect();
    }

    private void connect() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, serverUri);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("WebSocket connection opened.");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received from server: " + message);
        Gson gson = new Gson();
        ServerMessage baseMessage = gson.fromJson(message, ServerMessage.class);
        if (baseMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {

        }

    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket closed: " + closeReason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    public void send(String message) {
        try {
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(message);
            } else {
                System.err.println("WebSocket is not connected.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }

    public boolean isOpen() {
        return session != null && session.isOpen();
    }

    public void close() {
        try {
            if (session != null) session.close();
        } catch (Exception e) {
            System.err.println("Error closing WebSocket: " + e.getMessage());
        }
    }
}
