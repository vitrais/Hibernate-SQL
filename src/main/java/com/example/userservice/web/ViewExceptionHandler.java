package com.example.userservice.web;

import com.example.userservice.exception.ConflictEx;
import com.example.userservice.exception.NotFoundEx;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ViewExceptionHandler {

    @ExceptionHandler(NotFoundEx.class)
    public String notFound(NotFoundEx ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler({ConflictEx.class, DataIntegrityViolationException.class, IllegalArgumentException.class})
    public String business(Exception ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}