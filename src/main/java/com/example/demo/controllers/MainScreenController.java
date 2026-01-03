package com.example.demo.controllers;

import com.example.demo.domain.Part;
import com.example.demo.domain.Product;
import com.example.demo.domain.UserEntity;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.PartService;
import com.example.demo.service.ProductService;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MainScreenController { // Fixed double 'r' typo

    // 1. Define dependencies as final (Best Practice)
    private final PartService partService;
    private final ProductService productService;
    private final UserRepository userRepository;

    // 2. Single Constructor for ALL injections (Cleaner than mixing @Autowired)
    public MainScreenController(PartService partService, ProductService productService, UserRepository userRepository) {
        this.partService = partService;
        this.productService = productService;
        this.userRepository = userRepository;
    }

    @GetMapping("/mainscreen")
    public String listPartsAndProducts(Model theModel,
                                       @Param("partkeyword") String partkeyword,
                                       @Param("productkeyword") String productkeyword) {

        // 1. Get Lists (with Search logic)
        List<Part> partList = partService.listAll(partkeyword);
        List<Product> productList = productService.listAll(productkeyword);

        // 2. Add to Model
        theModel.addAttribute("parts", partList);
        theModel.addAttribute("partkeyword", partkeyword);
        theModel.addAttribute("products", productList);
        theModel.addAttribute("productkeyword", productkeyword);

        // 3. User Logic (Refactored to helper method)
        theModel.addAttribute("myUserName", fetchStoreOwnerName());

        return "mainscreen";
    }

    @GetMapping("/buyproduct")
    public String buyProduct(@RequestParam("productID") int theId) {

        //Use the existing service!
        Product product = productService.findById(theId);

        if (product != null && product.getInv() > 0) {
            product.setInv(product.getInv() - 1);
            productService.save(product);
            return "redirect:/mainscreen?success=true";
        } else {
            return "redirect:/mainscreen?error=true";
        }
    }
    // --- Private Helper Methods ---
    /**
     * Extracts the name of the first user in the DB to display as store owner.
     */
    private String fetchStoreOwnerName() {
        List<UserEntity> allUsers = userRepository.findAll();
        if (allUsers.isEmpty()) {
            return "No User Found";
        } else {
            return allUsers.get(0).getName();
        }
    }
}