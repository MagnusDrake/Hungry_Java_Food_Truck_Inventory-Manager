package com.example.demo.controllers;

import com.example.demo.domain.UserEntity;
import com.example.demo.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AboutController {
    private final UserRepository userRepository;

    public AboutController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/about")
    public String showAbout(Model theModel) {

        theModel.addAttribute("myUserName", fetchStoreOwnerName());

        return "about";
    }
    private String fetchStoreOwnerName() {
        List<UserEntity> allUsers = userRepository.findAll();
        if (allUsers.isEmpty()) {
            return "No User Found";
        } else {
            return allUsers.get(0).getName();
        }
    }
}
