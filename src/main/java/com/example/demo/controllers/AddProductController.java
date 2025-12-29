package com.example.demo.controllers;

import com.example.demo.domain.Part;
import com.example.demo.domain.Product;
import com.example.demo.service.PartService;
import com.example.demo.service.PartServiceImpl;
import com.example.demo.service.ProductService;
import com.example.demo.service.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 *
 *
 */
@Controller
public class AddProductController {
    @Autowired
    private ApplicationContext context;
    private PartService partService;
    private List<Part> theParts;
    private static Product product1;
    private Product product;

    @GetMapping("/showFormAddProduct")
    public String showFormAddPart(Model theModel) {
        theModel.addAttribute("parts", partService.findAll());
        product = new Product();
        product1=product;
        theModel.addAttribute("product", product);

        List<Part>availParts=new ArrayList<>();
        for(Part p: partService.findAll()){
            if(!product.getParts().contains(p))availParts.add(p);
        }
        theModel.addAttribute("availparts",availParts);
        theModel.addAttribute("assparts",product.getParts());
        return "productForm";
    }

    @PostMapping("/showFormAddProduct")
    public String submitForm(@Valid @ModelAttribute("product") Product product, BindingResult bindingResult, Model theModel) {

        // 1. Send the product back to the model immediately (in case of error)
        theModel.addAttribute("product", product);

        // 2. Fetch the "Real" Product from DB to get the Parts List
        ProductService repo = context.getBean(ProductServiceImpl.class);
        Product productFromDB = repo.findById((int) product.getId());

        if (productFromDB != null) {
            product.setParts(productFromDB.getParts());
        }

        // 3. LOGIC CHECK: Do we have enough parts?

        int productDelta = 0;
        if (productFromDB != null) {
            productDelta = product.getInv() - productFromDB.getInv();
        } else {
            productDelta = product.getInv(); // New product
        }

        if (productDelta > 0) {
            PartService partService = context.getBean(PartServiceImpl.class);

            // Count required parts
            Map<Integer, Integer> partCounts = new HashMap<>();
            for (Part p : product.getParts()) {
                partCounts.put((int)p.getId(), partCounts.getOrDefault((int)p.getId(), 0) + 1);
            }

            // Check stock for each part
            for (Map.Entry<Integer, Integer> entry : partCounts.entrySet()) {
                int partId = entry.getKey();
                int qtyNeededPerProduct = entry.getValue();
                int totalNeededForUpdate = productDelta * qtyNeededPerProduct;

                Part p = partService.findById(partId);

                if (p.getInv() < totalNeededForUpdate) {
                    // FAILURE: Add error message and STOP
                    bindingResult.rejectValue("inv", "error.inv",
                            "Not enough stock! You need " + totalNeededForUpdate + " " + p.getName() +
                                    "(s), but have " + p.getInv());
                }
            }
        }

        // 4. FINAL ERROR CHECK (Standard Validation + Stock Check)
        if (bindingResult.hasErrors()) {
            // RE-POPULATE THE MODEL
            theModel.addAttribute("parts", context.getBean(PartServiceImpl.class).findAll());

            List<Part> availParts = new ArrayList<>();
            for (Part p : context.getBean(PartServiceImpl.class).findAll()) {
                availParts.add(p);
            }
            theModel.addAttribute("availparts", availParts);

            // Calculate counts for display again
            Map<Part, Integer> partWithCounts = new HashMap<>();
            for (Part p : product.getParts()) {
                partWithCounts.put(p, partWithCounts.getOrDefault(p, 0) + 1);
            }
            theModel.addAttribute("assparts", partWithCounts.keySet());
            theModel.addAttribute("partCounts", partWithCounts);

            return "productForm"; // Go back to form with error message
        }

        // 5. SUCCESS: Deduct Inventory and Save
        else {
            PartService partService = context.getBean(PartServiceImpl.class);

            // We already calculated Delta above, but let's redo the math to be safe
            if (productDelta > 0) {
                Map<Integer, Integer> partCounts = new HashMap<>();
                for (Part p : product.getParts()) {
                    partCounts.put((int)p.getId(), partCounts.getOrDefault((int)p.getId(), 0) + 1);
                }

                for (Map.Entry<Integer, Integer> entry : partCounts.entrySet()) {
                    int partId = entry.getKey();
                    int qtyNeededPerProduct = entry.getValue();

                    Part p = partService.findById(partId);
                    p.setInv(p.getInv() - (productDelta * qtyNeededPerProduct));
                    partService.save(p);
                }
            }

            repo.save(product);
            return "confirmationaddproduct";
        }
    }

    @GetMapping("/showProductFormForUpdate")
    public String showProductFormForUpdate(@RequestParam("productID") int theId, Model theModel) {
        // 1. Load Data
        theModel.addAttribute("parts", partService.findAll());
        ProductService repo = context.getBean(ProductServiceImpl.class);
        Product theProduct = repo.findById(theId);
        // 2. Set Static Variable
        product1 = theProduct;

        // 3. Set Product in Model
        theModel.addAttribute("product", theProduct);

        // 4. PREPARE AVAILABLE PARTS
        // Allow ALL parts to be shown, so a 2nd or 3rd copy of the same part may be added.
        List<Part> availParts = new ArrayList<>();
        for(Part p: partService.findAll()){
            availParts.add(p);
        }
        theModel.addAttribute("availparts", availParts);


        // 5. PREPARE ASSOCIATED PARTS
        // Count the parts:
        Map<Part, Integer> partWithCounts = new HashMap<>();
        for (Part p : theProduct.getParts()) {
            partWithCounts.put(p, partWithCounts.getOrDefault(p, 0) + 1);
        }

        // Send UNIQUE parts for the rows
        theModel.addAttribute("assparts", partWithCounts.keySet());
        // Send the COUNTS to display the quantity
        theModel.addAttribute("partCounts", partWithCounts);

        return "productForm";
    }

    @GetMapping("/deleteproduct")
    public String deleteProduct(@RequestParam("productID") int theId, Model theModel) {
        ProductService productService = context.getBean(ProductServiceImpl.class);
        Product product2=productService.findById(theId);
        for(Part part:product2.getParts()){
            part.getProducts().remove(product2);
            partService.save(part);
        }
        product2.getParts().removeAll(product2.getParts());
        productService.save(product2);
        productService.deleteById(theId);

        return "confirmationdeleteproduct";
    }

    public AddProductController(PartService partService) {
        this.partService = partService;
    }
// make the add and remove buttons work

    @GetMapping("/associatepart")
    public String associatePart(@Valid @RequestParam("partID") int theID,
                                @RequestParam("amount") int amount,
                                Model theModel){

        // 1. Safety Check: If app restarted, go back to main screen
        if (product1 == null) {
            return "redirect:/mainscreen";
        }

        // 2. Safety Check: Invalid Amount
        if (amount <= 0) {
            return "redirect:/showProductFormForUpdate?productID=" + product1.getId();
        }

        // 3.Fetch a FRESH copy of the product from the database.
        ProductService productService = context.getBean(ProductServiceImpl.class);
        Product productToSave = productService.findById((int)product1.getId());

        // 4. Fetch the Part
        PartService partService = context.getBean(PartServiceImpl.class);
        Part thePart = partService.findById(theID);

        // 5. Add the Part (Loop for duplicates)
        for (int i = 0; i < amount; i++) {
            productToSave.getParts().add(thePart);
        }

        // 6. Save the FRESH product
        productService.save(productToSave);

        // 7. Update the static variable so the View sees the new data
        product1 = productToSave;

        // 8. Rebuild the Model for the View
        theModel.addAttribute("product", product1);

        // Show ALL parts (Can add duplicates)
        List<Part> availParts = new ArrayList<>();
        for (Part p : partService.findAll()) {
            availParts.add(p);
        }
        theModel.addAttribute("availparts", availParts);

        // Count the parts for display
        Map<Part, Integer> partWithCounts = new HashMap<>();
        for (Part p : product1.getParts()) {
            partWithCounts.put(p, partWithCounts.getOrDefault(p, 0) + 1);
        }

        theModel.addAttribute("assparts", partWithCounts.keySet());
        theModel.addAttribute("partCounts", partWithCounts);

        return "productForm";
    }

    @GetMapping("/removepart")
    public String removePart(@RequestParam("partID") int theID, Model theModel){

        // 1. Safety Check
        if (product1 == null) {
            return "redirect:/mainscreen";
        }

        // 2. Fetch Fresh Product (Prevents "Multiple Representations" Error)
        ProductService productService = context.getBean(ProductServiceImpl.class);
        Product productToSave = productService.findById((int)product1.getId());

        // 3. Fetch the Part to remove
        PartService partService = context.getBean(PartServiceImpl.class);
        Part partToRemove = partService.findById(theID);

        // 4. Remove ONE instance of the part
        if(productToSave.getParts().contains(partToRemove)){
            productToSave.getParts().remove(partToRemove);
        }

        // 5. Save
        productService.save(productToSave);

        // 6. Update Static Variable
        product1 = productToSave;

        // 7. Rebuild Model
        theModel.addAttribute("product", product1);

        // Show ALL Parts (Don't hide the part)
        List<Part> availParts = new ArrayList<>();
        for (Part p : partService.findAll()) {
            availParts.add(p);
        }
        theModel.addAttribute("availparts", availParts);

        // Count the parts
        Map<Part, Integer> partWithCounts = new HashMap<>();
        for (Part p : product1.getParts()) {
            partWithCounts.put(p, partWithCounts.getOrDefault(p, 0) + 1);
        }
        theModel.addAttribute("assparts", partWithCounts.keySet());
        theModel.addAttribute("partCounts", partWithCounts);

        return "productForm";
    }
}
