package com.example.demo.controllers;

import com.example.demo.DTO.NameForm;
import com.example.demo.domain.UserEntity;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FormController {

    @Autowired // Inject the repository
    private UserRepository userRepository;

    // 1. Show the form
    @GetMapping("/settings")
    public String showForm(Model model) {
        // We create an empty object to hold the data
        model.addAttribute("nameFormData", new NameForm());
        return "Settings";
    }

    // 2. Handle the submit
    @PostMapping("/save-name")
    public String saveName(@ModelAttribute NameForm nameFormData, RedirectAttributes theRa) {
        userRepository.deleteAll();

        // The 'nameFormData' now contains what the user typed!
        UserEntity newUser = new UserEntity();
        newUser.setName(nameFormData.getUserName());

        // 2. SAVE IT! (This writes to the DB)
        userRepository.save(newUser);
        theRa.addFlashAttribute("message", "Food Truck named " + nameFormData.getUserName() + " successfully saved!");
        return "redirect:/mainscreen";
    }
}