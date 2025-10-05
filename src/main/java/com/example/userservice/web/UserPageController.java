package com.example.userservice.web;

import com.example.userservice.dto.CreateUpdateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/ui/users")
@RequiredArgsConstructor
public class UserPageController {

    private final UserService service;

    @GetMapping
    public String listPage(Model model) {
        List<UserDto> users = service.list();
        model.addAttribute("users", users);
        model.addAttribute("form", new CreateUpdateUserDto());
        return "users/list";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") CreateUpdateUserDto form,
                         BindingResult br,
                         Model model) {
        if (br.hasErrors()) {
            model.addAttribute("users", service.list());
            return "users/list";
        }
        service.create(form);
        return "redirect:/ui/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/ui/users";
    }
}
