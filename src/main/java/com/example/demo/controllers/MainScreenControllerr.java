package com.example.demo.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.domain.UserEntity;
import com.example.demo.repositories.UserRepository;
import com.example.demo.domain.Part;
import com.example.demo.domain.Product;

import com.example.demo.service.PartService;
import com.example.demo.service.ProductService;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 *
 *
 *
 *
 */

@Controller
public class MainScreenControllerr {
    // private final PartRepository partRepository;
    // private final ProductRepository productRepository;'

    private PartService partService;
    private ProductService productService;
    @Autowired
    private UserRepository userRepository;

    private List<Part> theParts;
    private List<Product> theProducts;

 /*   public MainScreenControllerr(PartRepository partRepository, ProductRepository productRepository) {
        this.partRepository = partRepository;
        this.productRepository = productRepository;
    }*/

    public MainScreenControllerr(PartService partService, ProductService productService) {
        this.partService = partService;
        this.productService = productService;
    }
    @GetMapping("/mainscreen")
    public String listPartsandProducts(Model theModel, @Param("partkeyword") String partkeyword, @Param("productkeyword") String productkeyword) {
        //add to the sprig model
        List<Part> partList = partService.listAll(partkeyword);
        theModel.addAttribute("parts", partList);
        theModel.addAttribute("partkeyword", partkeyword);
        //    theModel.addAttribute("products",productService.findAll());
        List<Product> productList = productService.listAll(productkeyword);
        theModel.addAttribute("products", productList);
        theModel.addAttribute("productkeyword", productkeyword);

        List<UserEntity> allUsers = userRepository.findAll();

        if (allUsers.isEmpty()) {
            theModel.addAttribute("myUserName", "No User Found");
        } else {
            // 3. Get the FIRST user from the list (Index 0)
            UserEntity firstUser = allUsers.get(0);

            // 4. Extract ONLY the name string
            theModel.addAttribute("myUserName", firstUser.getName());
        }
        return "mainscreen";
    }
}