package ru.itmo.wp.servlet;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;

public class MessageServlet extends HttpServlet {
    private ArrayList<MyPair> messages = new ArrayList<>();

    private class MyPair {
        String user;
        String text;
        MyPair(String a1, String a2) {
            user = a1;
            text = a2;
        }
    };

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("windows-1251");
        request.setCharacterEncoding("windows-1251");
        HttpSession httpSession = request.getSession();
        String uri = request.getRequestURI();
        if (uri.equals("/message/auth")) {
            String user = request.getParameter("user");
            if (user != null) {
                httpSession.setAttribute("user", user);
            } else {
                user = (String) httpSession.getAttribute("user");
            }
            String json = new Gson().toJson(user);
            response.getWriter().print(json);
            response.setContentType("application/json");
            response.getWriter().close();
        }
        if (uri.equals("/message/findAll")) {
            String json = new Gson().toJson(messages);
            System.out.print(messages);
            response.getWriter().print(json);
            response.getWriter().flush();
        }
        if (uri.equals("/message/add")) {
            String user = request.getParameter("user");
            String text = request.getParameter("text");
            if (user != null) {
                httpSession.setAttribute("user", user);
            } else {
                user = (String) httpSession.getAttribute("user");
            }
            if (user == null) {
                return;
            }
            messages.add(new MyPair(user, text));
        }
    }
}
