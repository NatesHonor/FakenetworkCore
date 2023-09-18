package com.nate.fakenetwork.utils.api.WebSocket;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.nate.fakenetwork.utils.Functions.SendToReportChat;

@WebSocket
public class WebSocketServer {
    private static Server server;

    public WebSocketServer(int port) {
        startWebSocketServer(port);
    }

    private void startWebSocketServer(int port) {
        server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(), "/websocket/*"); // No need to pass any arguments

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket Connected: " + session.getRemoteAddress());
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("WebSocket Closed: " + reason);
        stopWebSocketServer();
    }

    @OnWebSocketMessage
    public void onMessage(String playerAndMessage) {
        System.out.println("Received message: " + playerAndMessage);

        String[] parts = playerAndMessage.split(" ", 2);

        if (parts.length == 2) {
            String player = parts[0];
            String message = parts[1];
            SendToReportChat.createReportMessage(player, message);
        } else {
            System.out.println("Invalid message format: " + playerAndMessage);
        }
    }

    public void stopWebSocketServer() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
