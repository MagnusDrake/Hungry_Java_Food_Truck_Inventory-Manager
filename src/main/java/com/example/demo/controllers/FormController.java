package com.example.demo.controllers;

import com.example.demo.DTO.NameForm;
import com.example.demo.domain.UserEntity;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

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
    // 2. Handle the submit
    @PostMapping("/save-name")
    public String saveName(@Valid @ModelAttribute("nameFormData") NameForm nameFormData,
                           BindingResult bindingResult,
                           RedirectAttributes theRa) {

        // 1. Check for errors FIRST.
        // If the form is bad, stop here. Do not touch the database.
        if (bindingResult.hasErrors()) {
            return "Settings";
        }

        // --- SUCCESS BLOCK ---

        // 2. NOW it is safe to wipe the old data
        userRepository.deleteAll();

        // 3. Map the DTO to the Entity
        UserEntity newUser = new UserEntity();
        newUser.setName(nameFormData.getUserName());

        // 4. Save the new user
        userRepository.save(newUser);

        theRa.addFlashAttribute("message", "Food Truck named " + nameFormData.getUserName() + " successfully saved!");
        return "redirect:/mainscreen";
    }
}