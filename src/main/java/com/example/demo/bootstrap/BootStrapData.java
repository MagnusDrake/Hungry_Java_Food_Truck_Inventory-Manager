package com.example.demo.bootstrap;

import com.example.demo.domain.InhousePart;
import com.example.demo.domain.OutsourcedPart;
import com.example.demo.domain.Product;
import com.example.demo.repositories.OutsourcedPartRepository;
import com.example.demo.repositories.PartRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.demo.domain.UserEntity;

@Component
public class BootStrapData implements CommandLineRunner {

    private final PartRepository partRepository;
    private final ProductRepository productRepository;
    private final OutsourcedPartRepository outsourcedPartRepository;
    private final UserRepository userRepository; // <--- New Dependency

    public BootStrapData(PartRepository partRepository, ProductRepository productRepository, OutsourcedPartRepository outsourcedPartRepository, UserRepository userRepository) {
        this.partRepository = partRepository;
        this.productRepository = productRepository;
        this.outsourcedPartRepository=outsourcedPartRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Requirement 1: Only load data if BOTH lists are empty
        if (partRepository.count() == 0 && productRepository.count() == 0) {

            if (userRepository.count() == 0) {
                UserEntity user = new UserEntity();

                user.setName("The Hungry Java Food Truck");

                userRepository.save(user);
            }

            System.out.println("No data found. Loading sample inventory...");
            // =================================================================
            // 1. LOCALLY SOURCED INGREDIENTS
            // =================================================================
            // We create the object AND update the variable with the saved version

            InhousePart beef = new InhousePart(0,"Local Grass-Fed Beef", 2.50, 50, 10, 100, 1001);
            beef = partRepository.save(beef); // <--- CRITICAL UPDATE

            InhousePart bun = new InhousePart(0,"Artisan Brioche Bun", 0.75, 60, 20, 120, 1002);
            bun = partRepository.save(bun);

            InhousePart lettuce = new InhousePart(0,"Organic Butter Lettuce", 0.30, 40, 5, 80, 1003);
            lettuce = partRepository.save(lettuce);

            InhousePart cheese = new InhousePart(0,"Aged Cheddar Slice", 0.60, 75, 15, 150, 1004);
            cheese = partRepository.save(cheese);

            InhousePart sauce = new InhousePart(0,"Truck's Secret Sauce", 0.20, 100, 20, 200, 1005);
            sauce = partRepository.save(sauce);


            // =================================================================
            // 2. WHOLESALE INGREDIENTS
            // =================================================================

            OutsourcedPart fries = new OutsourcedPart(0,"Frozen Crinkle Cut Fries", 3.50, 30, 5, 50, "Sysco");
            fries = outsourcedPartRepository.save(fries); // <--- CRITICAL UPDATE

            OutsourcedPart tortilla = new OutsourcedPart(0,"Flour Tortilla", 0.15, 200, 50, 500, "US Foods");
            tortilla = outsourcedPartRepository.save(tortilla);

            OutsourcedPart chicken = new OutsourcedPart(0,"Breaded Chicken Tenders", 1.25, 60, 10, 120, "Perdue");
            chicken = outsourcedPartRepository.save(chicken);

            OutsourcedPart hotSauce = new OutsourcedPart(0,"Bulk Hot Sauce per oz.", 0.20, 1000, 200, 2000, "Texas Pete");
            hotSauce = outsourcedPartRepository.save(hotSauce);

            OutsourcedPart paperBoat = new OutsourcedPart(0,"Red Food Boat", 0.05, 500, 100, 1000, "Costco");
            paperBoat = outsourcedPartRepository.save(paperBoat);


            // =================================================================
            // 3. PRODUCTS
            // =================================================================

            Product burger = new Product(0,"The 'Main St.' Burger", 12.99, 15, 3.50);
            Product basket = new Product(0,"Crispy Chicken Basket", 10.50, 20, 2.00);
            Product taco = new Product(0,"Spicy Chicken Taco", 4.50, 40, 1.25);
            Product loadedFries = new Product(0,"Cheesy Loaded Fries", 8.99, 25, 1.50);
            Product wrap = new Product(0,"Fresh Garden Wrap", 9.50, 10, 2.50);

            // Associate the Parts
            // Now 'beef', 'bun', etc. are holding the MANAGED instances with real IDs

            burger.getParts().add(beef);
            burger.getParts().add(bun);
            burger.getParts().add(bun);
            burger.getParts().add(cheese);
            burger.getParts().add(cheese);
            burger.getParts().add(lettuce);
            burger.getParts().add(sauce);

            basket.getParts().add(chicken);
            basket.getParts().add(chicken);
            basket.getParts().add(chicken);
            basket.getParts().add(fries);
            basket.getParts().add(paperBoat);

            taco.getParts().add(chicken);
            taco.getParts().add(chicken);
            taco.getParts().add(tortilla);
            taco.getParts().add(hotSauce);

            loadedFries.getParts().add(fries);
            loadedFries.getParts().add(cheese);
            loadedFries.getParts().add(sauce);
            loadedFries.getParts().add(paperBoat);

            wrap.getParts().add(tortilla);
            wrap.getParts().add(lettuce);
            wrap.getParts().add(lettuce);
            wrap.getParts().add(cheese);
            wrap.getParts().add(cheese);
            wrap.getParts().add(cheese);
            wrap.getParts().add(sauce);

            // Save Products
            productRepository.save(burger);
            productRepository.save(basket);
            productRepository.save(taco);
            productRepository.save(loadedFries);
            productRepository.save(wrap);

            System.out.println("Sample Inventory Loaded Successfully!");
            System.out.println("Number of Parts: " + partRepository.count());
            System.out.println("Number of Products: " + productRepository.count());
        }

    }
}
