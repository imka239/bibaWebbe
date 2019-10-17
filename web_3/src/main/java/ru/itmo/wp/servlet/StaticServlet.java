package ru.itmo.wp.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StaticServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestURI = request.getRequestURI();
        String[] uriAll = requestURI.split("\\+");
        boolean first = true;
        OutputStream outputStream = response.getOutputStream();
        for (String uri : uriAll) {
            if (uri == null) {
                continue;
            }
            File file = new File(getServletContext().getRealPath("."), Paths.get("../../src/main/webapp/static/", uri).toString());
            if (file.isFile()) {
                first = isFirst(response, first, outputStream, uri, file);
            } else {
                file = new File(getServletContext().getRealPath("/static/" + uri));
                if (file.isFile()) {
                    first = isFirst(response, first, outputStream, uri, file);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        }
    }

    private boolean isFirst(HttpServletResponse response, boolean first, OutputStream outputStream, String uri, File file) throws IOException {
        if (first) {
            response.setContentType(getContentTypeFromName(uri));
        }
        Files.copy(file.toPath(), outputStream);
        outputStream.flush();
        return false;
    }

    private String getContentTypeFromName(String name) {
        name = name.toLowerCase();

        if (name.endsWith(".png")) {
            return "image/png";
        }

        if (name.endsWith(".jpg")) {
            return "image/jpeg";
        }

        if (name.endsWith(".html")) {
            return "text/html";
        }

        if (name.endsWith(".css")) {
            return "text/css";
        }

        if (name.endsWith(".js")) {
            return "application/javascript";
        }

        throw new IllegalArgumentException("Can't find content type for '" + name + "'.");
    }
}
