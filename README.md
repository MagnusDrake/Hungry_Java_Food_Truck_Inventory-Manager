**------------------------------------------------------Task C-------------------------------------------------------------**

**C. Customize the HTML user interface for your customer’s application. The user interface should include the
    shop name, the product names, and the names of the parts.**

**||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||**

    mainscreen.html {

        line 3 to 10 - CLeaned up the header. Added a CSS link for my styles.css file.
            Changed the title text. (Store Inventory)

        lines 11 to 13 - added classes to tags body and divs. Leveraging the bootstrap framework.

        line 15 to 21 - Added a div tag. Encapsulating the H2 and H1 tag.
            H2 tag text became a variable. The store name can be changed and stored in the database.
            H1 tag text just add context about the app itself. Used classes from bootstrap.

        line 23 to 39 - Added context messages to the page. For success and error confirmations. The first two are for
            RedirectAttribute, after a successful or failed operation in the product or part forms. The other two are
            for success or failed operations after clicking the Buy Now button.By doing this I got rid of the pages
            dedicated to success and error messages.

        line 68, 77 - Added Min/Max column to the Parts table. Both numbers on the same column

        line 75 - The column Price of the Parts table now shows numbers in dollar format.

        line 84 to 87 - Refactored the delete part message to show the name of the part being deleted.

        line 131, 139, 140 - Added a Labor Cost column to the products table. Labor Cost and Price columns show numbers
        in dollar format.

        line 144 to 145 - Added Buy Now button to the products table.

        line 150 to 155 - Refactored the delete part message to show the name of the product being deleted.

        line 173 to 188 - Added a footer tag. Containing the links to the Home page and the About page.
                          Also contains the link to the Settings page, which allows the store name to be changed.

        line 192 to 197 - Moved the script for the clear search button to this area. Also added a script tag to src
                          bootstrap.bundle.min.js. to be able to close the success and error messages added on lines 23
                          to 39.
    }

    MainScreenController.java {

        Corrected spelling mistake in MainScreenController, it had two r's.
        Removed unused imports and code.

        lines 6, 7, 13 - Added imports:
                         import com.example.demo.domain.UserEntity;
                         import com.example.demo.repositories.UserRepository;
                         import org.springframework.web.bind.annotation.RequestParam;

        lines 21 to 23 -Defined dependencies as final, added the userRepository.

        lines 26 to 30 - Added a new parameter to the constructor. userRepository.

        lines 32 to 51 - Organized the code inside @GetMapping("/mainscreen").

        line 48 - Added the code to retrieve the store name from the database, using a helper method.

        line 53 to 66 - Added @GetMapping("/buyproduct") logic. After a successful or failed purchase, redirects to the
                    main screen with a success or error message.

        lines 71 to 79 - Private Helper Method to retrieve the store name from the database.
    }

**-----------------------------------------------------Task D-------------------------------------------------------------**

**D. Add an “About” page to the application to describe your chosen customer’s company to web viewers and include
        navigation to and from the “About” page and the main screen.**

**||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||**

        about.html {
            Whole page created from scratch.
            The page contains the store name from the database, using the thymeleaf variable.
        }
        AboutController.java {
            Whole class created from scratch.
            @GetMapping("/about") - Added the route to the page.
            Private Helper Method to fetch the store name from the database.
        }

**-----------------------------------------------------Task E-------------------------------------------------------------**

**E. Add a sample inventory appropriate for your chosen store to the application. You should have five parts and five
    products in your sample inventory and should not overwrite existing data in the database.**

        Note: Make sure the sample inventory is added only when both the part and product lists are empty.
              When adding the sample inventory appropriate for the store, the inventory is stored in a set so
              duplicate items cannot be added to your products. When duplicate items are added, make a
              “multi-pack” part.

**||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||**

        On the Note: I made changes to the code to allow duplicate parts to be added to a product
                     instead of adding a new part with the multi-pack label. Parts are added to a list,
                     not a set.

    AddPartController.java {
        Removed unused imports.

        line 12 - Added import RedirectAttributes.

        lines 38, 45 - Changed the name of the model attribute to part. Was planning on using only one page to enter the
                       part data, but I decided to keep the two pages at the end. Stayed withe the change on the
                       controller.

        lines 60, 63, 64 - Removed the pages for delete part success or error, and just redirected to the main screen,
                           after a successful or failed operation, with a flash message.
    }

    AddProductController.java {
        Removed unused imports.

        line 5 - Added import com.example.demo.repositories.ProductRepository;

        line 16 - Added import RedirectAttributes.

        lines 37, 38 - Added Autowired ProductRepository productRepository.

        lines 56 to 94 - Rewrite the logic in @PostMapping("/showFormAddProduct"). Coding to habilitate associating
                         more than one of the same part per product. Fixing errors caused by the new logic. Like losing
                         the association between the product and the part. Added logic to verify there's enough inventory
                         to add the product. Refactored the code into private helper methods, to keep the controller
                         clean. Added a flash message to show the success or error of the operation, after redirecting to
                         the main screen.

        lines 96 to 127 -  Rewrite the logic in @GetMapping("/showProductFormForUpdate"). Allowing all the parts to be
                           shown. Counting the associated parts per product to be shown.

        lines 130, 141, 143 - Removed the page for delete product success. Added a flash message to show the success of
                              the operation, using the RedirectAttributes.

        lines 151 to 206 - Rewrite @GetMapping("/associatepart") logic. Coding to habilitate associating more than
                           one part per product. Fixing errors caused by the new logic.

        lines 208 to 251 - Rewrite  @GetMapping("/removepart"). Had to do changes to fix logic errors, parts disappearing
                           from the available parts table.

        lines 255 to 416 - Helper methods to enhance readability and reusability.
            checkInventoryStock: It checks inventory levels to make sure after adding the product the inventory for the
                                 associated parts is not going to fall below zero. If it does, returns a binding result.

            restorePartsList: Deals with the problem I was getting into, where adding a product to inventory would
                              disassociate the parts already associated. Also makes sure if is a new part it would not
                              look in the database for parts, which was causing the program to crash.

            setupModelForError: This takes care of sending the information back to the page in case an error happens. I
                                was having issues where everytime an error happen tha app would crash.

            saveProductAndInventory: This just saves the product and inventory. Checks if there are changes on the
                                     inventory amount and takes care of reducing the parts by the specific amount.

            checkPartMinLevels: This checks minimum value associated with each part. If a product was to be added, and a
                                part associated was to fall below that minimum threshold, a message is sent back to the
                                user to make a choice, by acceding to the risk of making this new product while the part
                                falls below the minimum amount set.

            getPartCounts: This method returns a map with the part name as key and the amount of parts associated as a
                           value.
    }

    Part.java {

        line 7 - Added import javax.validation.constraints.NotBlank

        line 26 to 36 - Corrected the field declared variables, by adding private to them. Added two private integer
                        field variables, to capture the minimum and maximum inventory levels for each part. Added
                        @NotBlank to ensure the Name field is not empty. Added @MIN to ensure variables are 0 or
                        positive integers.

        line 38, 39 - Made changes so the product would be in control, to allow duplicate parts. Parts is now the passive
                      side of the relationship.

        line 44 to 59 - Updated both constructors to set the variables for the minimum and maximum inventory levels.

        line 125 to 128 - Changed part of the code, as was suggested by the IDE. This (int) (id ^ (id >>> 32)), became
                          this Long.hashCode(id).

        line 130 to 147 - added getters and setters for the min and max variables.
    }

    PartRepository.java {
        Removed unused imports.

        line 18 - Added method Part findByName(String name), to verify if a part name already exists in the database.

    }

    ProductRepository.java {

        line 18 - Added method Product findByName(String name), to verify if a product name already exists in the database.

    }

    InhousePart.java {

        line 11 to 18 - Added two constructors. One without id and the other with id.

    }

    OutsourcedPart.java {

        line 20 to 27 - Added two constructors. One without id and the other with id.

    }
    Product.java {

        line 8, 10, 11 - Switch imports, set and hashset, for arrays and list. Added @NotBlank to ensure the Name field
                         is not empty.

        line 21 - Added @NotBlank to ensure the Name field is not empty.

        line 27, 28 - Added a new variable to retain Labor Cost, per product. Added @Min annotation to ensure the value
                      is positive.

        lines 33 to 42 - Change the cascade definition, removed persist, this was needed to be able to associate parts
                         to the respective product from the BootStrapData.java file. Added @JoinTable here so Product
                         "Owns" the table. Changed from Set to List to allow duplicates. Added @OrderColumn(name =
                         "parts_order"), because I was getting an error when trying to add the extra same parts to
                         products.

        lines 47, 51, 54, 59 - Changed the constructors to set the Labor Cost variable.

        lines 119 to 125 - Added getters and setters for the Labor Cost variable.

        line 139 - Change a piece of code as suggested by the IDE.
                   From this (int) (id ^ (id >>> 32)), to this Long.hashCode(id).

    }

    BootStrapData.java {
        Removed unused imports.

        line 3, 9, 12 - Added import com.example.demo.domain.InhousePart,
                              import com.example.demo.repositories.UserRepository,
                              import com.example.demo.domain.UserEntity.

        line 20 - Added private final variable userRepository.

        line 22 to 26 - Updated Constructor to set the userRepository variable.

        line 33 to 139 - Added a conditional that checks for inventory to be empty before populating the database with
                         a sample inventory. Also adds a sample user to the database.

    }

**-----------------------------------------------------Task F-------------------------------------------------------------**

**F. Add a “Buy Now” button to your product list. Your “Buy Now” button must meet each of the following parameters:**

    • The “Buy Now” button must be next to the buttons that update and delete products.
    • The button should decrement the inventory of that product by one. It should not affect the inventory of any of the
      associated parts.
    • Display a message that indicates the success or failure of a purchase.

**||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||**

    mainscreen.html {

        line 23 to 39 - Added context messages to the page. For success and error confirmations. The first two are for
                        RedirectAttribute, after a successful or failed operation in the product or part forms. The other two are
                        for success or failed operations after clicking the Buy Now button.By doing this I got rid of the pages
                        dedicated to success and error messages.

        line 144 to 145 - Added Buy Now button to the products table.

        line 192 to 197 - Moved the script for the clear search button to this area. Also added a script tag to src
                            bootstrap.bundle.min.js. to be able to close the success and error messages added on lines 23 to 39.

    }

    MainScreenController.java {

        line 53 to 66 - Added @GetMapping("/buyproduct") logic. It looks up the product by ID. Verifies that there's
                        enough inventory, and after a successful or failed purchase, redirects to the main screen
                        with a success or error message.

    }

**-----------------------------------------------------Task G-------------------------------------------------------------**

**G. Modify the parts to track maximum and minimum inventory by doing the following:**

    • Add additional fields to the part entity for maximum and minimum inventory.
    • Modify the sample inventory to include the maximum and minimum fields.
    • Add to the InhousePartForm and OutsourcedPartForm forms additional text inputs for the inventory so the user can
      set the maximum and minimum values.
    • Rename the file the persistent storage is saved to.
    • Modify the code to enforce that the inventory is between or at the minimum and maximum value.

**||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||**

    InhousePartForm.html {

        line 70 to 79 - Added inputs for min and max inventory.
    }

    OutsourcedPartForm.html {

        line 67 to 74 - Added inputs for min and max inventory.

    }

    Part.java {

        line 33 to 36 - Added two private integer field variables, to capture the minimum and maximum inventory
                            levels for each part. Added @MIN to ensure variables are 0 or positive integers.

        line 44 to 59 - Updated both constructors to set the variables for the minimum and maximum inventory levels.

        line 130 to 147 - Added getters and setters for the min and max variables.

    }

    application.properties {

        line 5 - Renamed database to FoodTruckInventory.

    }

    AddInhousePartController.java {
        Removed unused imports.

        line 5 - Added import com.example.demo.repositories.PartRepository.

        line 16 - Added import RedirectAttributes.

        line 24, 25 - Added Autowired PartRepository partRepository.

        line 39 - @PostMapping("/showFormAddInPart") - Added a helper method to check if the part already exists in the
                  database, and to make sure minimum and maximum levels are set correctly.

        line 54 to 56 - Added a flash message to show the success of the operation, after redirecting to the
                        main screen.

        line 60 to 84 - Helper methods to enhance readability and reusability:
                        inventoryCheck: It validates the minimum and maximum levels set for the part, also validates
                        that the part name is not already in the database.

    }

    AddOutsourcedPartController.java {
        Removed unused imports.

        line 5, 16 - Added import com.example.demo.repositories.PartRepository
                     import org.springframework.web.servlet.mvc.support.RedirectAttributes

        line 24, 25 - Added Autowired PartRepository partRepository.

        line 39 - @PostMapping("/showFormAddInPart") - Added a helper method to check if the part already exists in the
                  database, and to make sure minimum and maximum levels are set correctly.

        line 54 to 56 - Added a flash message to show the success of the operation, after redirecting to the
                            main screen.

        line 60 to 84 - Helper methods to enhance readability and reusability:
                        inventoryCheck: It validates the minimum and maximum levels set for the part, also validates
                        that the part name is not already in the database.

    }

**-----------------------------------------------------Task H-------------------------------------------------------------**

**H. Add validation for between or at the maximum and minimum fields. The validation must include the following:**

    • Display error messages for low inventory when adding and updating parts if the inventory is less than the minimum
      number of parts.
    • Display error messages for low inventory when adding and updating products lowers the part inventory below the minimum.
    • Display error messages when adding and updating parts if the inventory is greater than the maximum.

**||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||**

    AddInhousePartController.java {
        Removed unused imports.

        line 5 - Added import com.example.demo.repositories.PartRepository.

        line 16 - Added import RedirectAttributes.

        line 24, 25 - Added Autowired PartRepository partRepository.

        line 39 - @PostMapping("/showFormAddInPart") - Added a helper method to check if the part already exists in the
                  database, and to make sure minimum and maximum levels are set correctly.

        line 54 to 56 - Added a flash message to show the success of the operation, after redirecting to the
                        main screen.

        line 60 to 84 - Helper methods to enhance readability and reusability:
                            inventoryCheck: It validates the minimum and maximum levels set for the part, also validates
                            that the part name is not already in the database.

    }

        AddOutsourcedPartController.java {
            Removed unused imports.

        line 5, 16 - Added import com.example.demo.repositories.PartRepository
                     import org.springframework.web.servlet.mvc.support.RedirectAttributes

        line 24, 25 - Added Autowired PartRepository partRepository.

        line 39 - @PostMapping("/showFormAddInPart") - Added a helper method to check if the part already exists in the
                  database, and to make sure minimum and maximum levels are set correctly.

        line 54 to 56 - Added a flash message to show the success of the operation, after redirecting to the
                        main screen.

        line 60 to 84 - Helper methods to enhance readability and reusability:
                        inventoryCheck: It validates the minimum and maximum levels set for the part, also validates
                        that the part name is not already in the database.

    }

    productForm.html {

        lines 51 to 53 - Added Labour Cost field.

        line 87 - Create a toggle button to show or hide the Available Parts table, since all parts are shown by
                  default now. But when a product already has parts associated, they are hidden by default.

        line 114 to 122 - Adding parts can be done by quantity.

        line 152 - Shows the quantity of parts associated with the product.

        line 191 to 207 - Script that controls the toggle button.

        line 208 to 230 - Script to show the modal window, and the function that allows a product to be created, even if
                          a part falls below the minimum inventory level.

        line 232 to 252 - Added a modal window, to ask the user if they want to create a new product, even if a new part
                          falls below the minimum inventory level.

    }

**-----------------------------------------------------Task I-------------------------------------------------------------**

**I. Add at least two unit tests for the maximum and minimum fields to the PartTest class in the test package**

**||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||**

    PartTest.java {

        lines 168 to 186 - Added tests for the minimum and maximum inventory levels.

    }


-----------------------------------------------------CREATED------------------------------------------------------------

styles.css - renamed the css file in the css folder. added data to the file.
Settings.html - added to be able to remotely set the store Name.
NameForm.java - added to DTO folder.
FormController.java - added to Controller folder.
UseEntity.java - added to domain folder.
UserRepository.java - added to repository folder.
