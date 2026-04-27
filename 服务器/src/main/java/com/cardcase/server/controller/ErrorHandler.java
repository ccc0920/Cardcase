package com.cardcase.server.controller;

import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public String handleAllExceptions(Exception ex) {
        return "{\"success\":false,\"message\":\"" + ex.getMessage() + "\"}";
    }
}
