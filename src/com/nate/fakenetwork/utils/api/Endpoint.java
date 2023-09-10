package com.nate.fakenetwork.utils.api;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Endpoint extends AbstractHandler {

    private Server server;

    public Endpoint(int port) {
        this.server = new Server(port);
        this.server.setHandler(this);
    }

    public void startAPI() throws Exception {
        server.start();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if ("/report".equals(target) && baseRequest.getMethod().equals("POST")) {
            // Handle the /report POST request
            String reported = request.getParameter("reported");
            String reporter = request.getParameter("reporter");
            String reason = request.getParameter("reason");

            // Process the reported information here
            // You can use Bukkit/Spigot methods to handle this data

            // Send a response
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Report received successfully!");
            baseRequest.setHandled(true);
        }
    }

    public void stopAPI() throws Exception {
        server.stop();
    }
}
