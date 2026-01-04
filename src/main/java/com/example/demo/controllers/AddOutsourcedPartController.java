package com.example.demo.controllers;

import com.example.demo.domain.OutsourcedPart;
import com.example.demo.domain.Part;
import com.example.demo.repositories.PartRepository;
import com.example.demo.service.OutsourcedPartService;
import com.example.demo.service.OutsourcedPartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class AddOutsourcedPartController {
    @Autowired
    private ApplicationContext context;
    @Autowired
    private PartRepository partRepository;

    @GetMapping("/showFormAddOutPart")
    public String showFormAddOutsourcedPart(Model theModel){
        Part part=new OutsourcedPart();
        theModel.addAttribute("part",part);
        return "OutsourcedPartForm";
    }

    @PostMapping("/showFormAddOutPart")
    public String submitForm(@Valid @ModelAttribute("part") OutsourcedPart part, BindingResult bindingResult,
                             RedirectAttributes theRa){

        // 1. CUSTOM VALIDATION - Call Helper Method
        inventoryCheck(part, bindingResult);

        // 2. STANDARD CHECK
        if(bindingResult.hasErrors()){
            return "OutsourcedPartForm";
        }
        else {
            OutsourcedPartService repo = context.getBean(OutsourcedPartServiceImpl.class);
            OutsourcedPart op = repo.findById((int) part.getId());
            if (op != null) {
                part.setProducts(op.getProducts());
            }

            repo.save(part);

            theRa.addFlashAttribute("message", "Success! " + part.getName() + " inventory updated.");

            return "redirect:/mainscreen";
        }
    }

    private void inventoryCheck(Part part, BindingResult bindingResult) {

        // CHECK 1: Is Max less than Min?
        if (part.getMax() < part.getMin()) {
            bindingResult.rejectValue("max", "error.max", "Max must be greater than or equal to Min");
        }
        // CHECK 2: Is Inventory less than Min?
        else if (part.getInv() < part.getMin()) {
            bindingResult.rejectValue("inv", "error.inv", "Inventory cannot be less than Min");
        }
        // CHECK 3: Is Inventory greater than Max?
        else if (part.getInv() > part.getMax()) {
            bindingResult.rejectValue("inv", "error.inv", "Inventory cannot be greater than Max");
        }
        // --- Min - Max Logic Validation Ends ---

        //Use the PARENT repository to check if this name exists anywhere
        Part existingPart = partRepository.findByName(part.getName());

        // If found, and it's different ID (checking for updates vs. new)
        if (existingPart != null && existingPart.getId() != part.getId()) {
            bindingResult.rejectValue("name", "error.name",
                    "Name already exists in inventory! Try a different name, ex. " + part.getName() + " multi-pack.");
        }
    }
}
