package ru.itmo.wp.servlet;

import ru.itmo.wp.util.ImageUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class CaptchaFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpSession httpSession = request.getSession();
        Object answer = "CaptchaDone";

        if ("GET".equals(request.getMethod())) {
            if (!"CaptchaDone".equalsIgnoreCase((String) httpSession.getAttribute("captchaDone"))) {
                int number = SendCaptcha(httpSession, response, request);
                httpSession.setAttribute("captchaNumber", Integer.toString(number));
            } else {
                chain.doFilter(request, response);
            }
        } else {
            String ans = (String) request.getParameter("captcha");
            if (httpSession.getAttribute("captchaNumber").equals(ans)) {
                httpSession.setAttribute("captchaDone", answer);
                System.out.print("kek");
                response.sendRedirect((String) httpSession.getAttribute("extendedUri"));
            } else {
                int number = SendCaptcha(httpSession, response, request);
                httpSession.setAttribute("captchaNumber", Integer.toString(number));
            }
        }
    }

    private int SendCaptcha(HttpSession httpSession, HttpServletResponse response, HttpServletRequest request) throws IOException {
        int number = (int) (Math.random() * 900) + 100;
        if (httpSession.getAttribute("extendedUri") == null) {
            httpSession.setAttribute("extendedUri", request.getRequestURI());
        }
        String s = Base64.getEncoder().encodeToString(ImageUtils.toPng(String.valueOf(number)));
        byte[] sByte = Files.readAllBytes(Paths.get(getServletContext().getRealPath("/static/captcha.html")));
        response.setContentType("text/html");
        String s1 = new String(sByte, StandardCharsets.UTF_8);
        PrintWriter printWriter = response.getWriter();
        printWriter.write(String.format(s1, s));
        printWriter.flush();
        return number;
    }
}
