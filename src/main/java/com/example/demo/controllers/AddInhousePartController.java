package com.example.demo.controllers;

import com.example.demo.domain.InhousePart;
import com.example.demo.domain.Part;
import com.example.demo.service.InhousePartService;
import com.example.demo.service.InhousePartServiceImpl;
import com.example.demo.service.PartService;
import com.example.demo.service.PartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 *
 *
 *
 *
 */
@Controller
public class AddInhousePartController{
    @Autowired
    private ApplicationContext context;

    @GetMapping("/showFormAddInPart")
    public String showFormAddInhousePart(Model theModel){
        InhousePart inhousepart=new InhousePart();
        theModel.addAttribute("inhousepart",inhousepart);
        return "InhousePartForm";
    }

    @PostMapping("/showFormAddInPart")
    public String submitForm(@Valid @ModelAttribute("inhousepart") InhousePart part, BindingResult theBindingResult, Model theModel){

        // CHECK 1: Is Max less than Min?
        if (part.getMax() < part.getMin()) {
            theBindingResult.rejectValue("max", "error.max", "Max must be greater than or equal to Min");
        }
        // CHECK 2: Is Inventory less than Min?
        else if (part.getInv() < part.getMin()) {
            theBindingResult.rejectValue("inv", "error.inv", "Inventory cannot be less than Min");
        }
        // CHECK 3: Is Inventory greater than Max?
        else if (part.getInv() > part.getMax()) {
            theBindingResult.rejectValue("inv", "error.inv", "Inventory cannot be greater than Max");
        }
        // --- VALIDATION LOGIC END ---


        // 2. STANDARD CHECK
        if(theBindingResult.hasErrors()){
            return "InhousePartForm";
        }
        else{
            InhousePartService repo = context.getBean(InhousePartServiceImpl.class);
            InhousePart ip = repo.findById((int)part.getId());
            if(ip != null) {
                part.setProducts(ip.getProducts());
            }

            repo.save(part);
            return "confirmationaddpart";
        }
    }

}
