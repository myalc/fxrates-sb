package com.myalc.fxrates.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myalc.fxrates.dto.CustomAppError;

import org.apache.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        CustomAppError message = new CustomAppError(String.valueOf(HttpStatus.SC_UNAUTHORIZED), accessDeniedException.getMessage());
        accessDeniedException.printStackTrace();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper objectMapper = new ObjectMapper();
        PrintWriter printWriter = response.getWriter();
        printWriter.print(objectMapper.writeValueAsString(message));
        printWriter.flush();
        printWriter.close();
    }
}