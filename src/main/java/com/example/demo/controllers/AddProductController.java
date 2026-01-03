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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private static Product product1;
    private Product product;

    @GetMapping("/showFormAddProduct")
    public String showFormAddPart(Model theModel) {
        theModel.addAttribute("parts", partService.findAll());
        product = new Product();
        product1=product;
        theModel.addAttribute("product", product);

        List<Part> availParts = new ArrayList<>();
        for(Part p: partService.findAll()){
            if(!product.getParts().contains(p))availParts.add(p);
        }
        theModel.addAttribute("availparts",availParts);
        theModel.addAttribute("assparts",product.getParts());
        return "productForm";
    }

    @PostMapping("/showFormAddProduct")
    public String submitForm(@Valid @ModelAttribute("product") Product product, BindingResult bindingResult,
                             Model theModel, @RequestParam(value = "ignoreMin", required = false) boolean ignoreMin,
                             RedirectAttributes theRa) {

        theModel.addAttribute("product", product);

        // 1. Restore Parts if needed (Helper Method 1)
        restorePartsList(product);

        // 2. Custom Logic: Check Stock (Helper Method 2)
        // We pass 'bindingResult' so the helper can add the error message directly!
        checkInventoryStock(product, bindingResult);

        // 3. Final Check
        if (bindingResult.hasErrors()) {
            // Helper Method 3: Prepare the model for the error page
            setupModelForError(theModel, product);
            return "productForm";
        }

        if (!ignoreMin) {
            List<String> stockWarnings = checkPartMinLevels(product);
            if (!stockWarnings.isEmpty()) {
                System.out.println("LOG: Stopped by Stock Warnings!");
                // We found risks! Stop and show the form again.
                theModel.addAttribute("stockWarnings", stockWarnings);
                //Call helper to reload the lists (assparts/availparts)
                setupModelForError(theModel, product);

                return "productForm";
            }
        }

        // 4. Success: Save
        saveProductAndInventory(product); // Helper Method 4
        theRa.addFlashAttribute("message", "Successfully added product " + product.getName() + "!");
        return "redirect:/mainscreen";
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
        List<Part> availParts = new ArrayList<>(partService.findAll());
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
    public String deleteProduct(@RequestParam("productID") int theId, RedirectAttributes theRa) {
        ProductService productService = context.getBean(ProductServiceImpl.class);
        Product product2=productService.findById(theId);
        for(Part part:product2.getParts()){
            part.getProducts().remove(product2);
            partService.save(part);
        }
        product2.getParts().removeAll(product2.getParts());
        productService.save(product2);
        productService.deleteById(theId);

        theRa.addFlashAttribute("message", "Successfully deleted product " + product2.getName() + "!");

        return "redirect:/mainscreen";
    }

    public AddProductController(PartService partService) {
        this.partService = partService;
    }
// make the add and remove buttons work

    @GetMapping("/associatepart")
    public String associatePart(@Valid @RequestParam("partID") int theID,
                                @RequestParam("amount") int amount,
                                @RequestParam(value = "open", required = false) String open,
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
        for (int i = 0; i < amount; ++i) {
            productToSave.getParts().add(thePart);
        }

        // 6. Save the FRESH product
        productService.save(productToSave);

        // 7. Update the static variable so the View sees the new data
        product1 = productToSave;

        // 8. Rebuild the Model for the View
        theModel.addAttribute("product", product1);

        // Show ALL parts (Can add duplicates)
        List<Part> availParts = new ArrayList<>(partService.findAll());
        theModel.addAttribute("availparts", availParts);

        // Count the parts for display
        Map<Part, Integer> partWithCounts = new HashMap<>();
        for (Part p : product1.getParts()) {
            partWithCounts.put(p, partWithCounts.getOrDefault(p, 0) + 1);
        }

        theModel.addAttribute("assparts", partWithCounts.keySet());
        theModel.addAttribute("partCounts", partWithCounts);
        if (open != null) {
            theModel.addAttribute("open", true);
        }

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
        List<Part> availParts = new ArrayList<>(partService.findAll());
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
    //Helper Methods!!!

    // Helper to check if we have enough parts
    private void checkInventoryStock(Product product, BindingResult bindingResult) {
        // If basic validation failed, don't bother checking complex stock logic
        if (bindingResult.hasErrors()) return;

        ProductService repo = context.getBean(ProductServiceImpl.class);
        Product productFromDB = (product.getId() != 0) ? repo.findById((int)product.getId()) : null;

        int productDelta = 0;
        if (productFromDB != null) {
            productDelta = product.getInv() - productFromDB.getInv();
        } else {
            productDelta = product.getInv();
        }

        // Only check if inventory is increasing
        if (productDelta > 0 && product.getParts() != null) {
            PartService partService = context.getBean(PartServiceImpl.class);

            // Count required parts
            Map<Integer, Integer> partCounts = new HashMap<>();
            for (Part p : product.getParts()) {
                partCounts.put((int)p.getId(), partCounts.getOrDefault((int)p.getId(), 0) + 1);
            }

            // Compare against DB
            for (Map.Entry<Integer, Integer> entry : partCounts.entrySet()) {
                Part p = partService.findById(entry.getKey());
                int totalNeeded = productDelta * entry.getValue();

                if (p.getInv() < totalNeeded) {
                    // Add the error directly to the binding result
                    bindingResult.rejectValue("inv", "error.inv",
                            "Not enough stock! You need " + totalNeeded + " " + p.getName() +
                                    "(s), but only have " + p.getInv());
                }
            }
        }
    }
    private void restorePartsList(Product product) {
        // Only look up if ID is not 0 (Prevent "Product #0 not found" crash)
        if (product.getId() != 0) {
            try {
                ProductService repo = context.getBean(ProductServiceImpl.class);
                Product productFromDB = repo.findById((int) product.getId());

                // If the form didn't send parts (because it's an update), keep the old ones
                if (productFromDB != null && (product.getParts() == null || product.getParts().isEmpty())) {
                    product.setParts(productFromDB.getParts());
                }
            } catch (Exception e) {
                // If lookup fails, just treat it as a new product
            }
        }
    }
    private void setupModelForError(Model theModel, Product product) {
        PartService partService = context.getBean(PartServiceImpl.class);

        // 1. Available Parts (Top Table)
        List<Part> availParts = new ArrayList<>();
        for (Part p : partService.findAll()) {
            availParts.add(p);
        }
        theModel.addAttribute("availparts", availParts);
        theModel.addAttribute("parts", partService.findAll());

        // 2. Associated Parts (Bottom Table)
        // Count manually to handle the duplicates correctly
        Map<Part, Integer> partWithCounts = new HashMap<>();
        if (product.getParts() != null) {
            for (Part p : product.getParts()) {
                partWithCounts.put(p, partWithCounts.getOrDefault(p, 0) + 1);
            }
        }

        theModel.addAttribute("assparts", partWithCounts.keySet());
        theModel.addAttribute("partCounts", partWithCounts);
    }
    private void saveProductAndInventory(Product product) {
        ProductService repo = context.getBean(ProductServiceImpl.class);
        PartService partService = context.getBean(PartServiceImpl.class);

        // 1. Calculate the Inventory Change (Delta)
        int productDelta = 0;
        if (product.getId() != 0) {
            // Existing Product: Compare New vs Old
            Product productFromDB = repo.findById((int) product.getId());
            if (productFromDB != null) {
                productDelta = product.getInv() - productFromDB.getInv();
            }
        } else {
            // New Product: The whole inventory is new
            productDelta = product.getInv();
        }

        // 2. Deduct Stock (Only if inventory increased)
        if (productDelta > 0 && product.getParts() != null) {
            // Count how many of each part is needed per product
            Map<Integer, Integer> partCounts = new HashMap<>();
            for (Part p : product.getParts()) {
                partCounts.put((int)p.getId(), partCounts.getOrDefault((int)p.getId(), 0) + 1);
            }

            // Loop through and update each part
            for (Map.Entry<Integer, Integer> entry : partCounts.entrySet()) {
                int partId = entry.getKey();
                int qtyNeededPerProduct = entry.getValue();

                Part p = partService.findById(partId);
                int totalDeduction = productDelta * qtyNeededPerProduct;

                p.setInv(p.getInv() - totalDeduction);
                partService.save(p);
            }
        }

        // 3. Finally, Save the Product
        repo.save(product);
    }
    private List<String> checkPartMinLevels(Product product) {
        List<String> warnings = new ArrayList<>();

        // We need the old product to calculate the difference (Delta)
        ProductService repo = context.getBean(ProductServiceImpl.class);
        Product productFromDB = (product.getId() != 0) ? repo.findById((int)product.getId()) : null;

        int productDelta = product.getInv();
        if (productFromDB != null) {
            productDelta -= productFromDB.getInv();
        }

        // Only care if we are INCREASING inventory (building stuff)
        if (productDelta > 0 && product.getParts() != null) {
            PartService partService = context.getBean(PartServiceImpl.class);

            // Count parts needed
            Map<Integer, Integer> partCounts = new HashMap<>();
            for (Part p : product.getParts()) {
                partCounts.put((int)p.getId(), partCounts.getOrDefault((int)p.getId(), 0) + 1);
            }

            for (Map.Entry<Integer, Integer> entry : partCounts.entrySet()) {
                Part p = partService.findById(entry.getKey());
                int amountNeeded = productDelta * entry.getValue();
                int predictedInventory = p.getInv() - amountNeeded;

                // THE CHECK: Will it fall below Min?
                if (predictedInventory < p.getMin()) {
                    warnings.add("Part '" + p.getName() + "' will fall below minimum! (Current: "
                            + p.getInv() + ", After: " + predictedInventory + ", Min: " + p.getMin() + ")");
                }
            }
        }
        return warnings;
    }
}
