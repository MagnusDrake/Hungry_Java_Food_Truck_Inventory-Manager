package com.example.demo.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Project: demoDarbyFrameworks2-master
 * Package: com.example.demo.domain
 * <p>
 * User: carolyn.sher
 * Date: 6/24/2022
 * Time: 3:44 PM
 * <p>
 * Created with IntelliJ IDEA
 * To change this template use File | Settings | File Templates.
 */
class PartTest {
    Part partIn;
    Part partOut;

    @BeforeEach
    void setUp() {
        partIn = new InhousePart();
        partOut = new OutsourcedPart();
    }

    @Test
    void getId() {
        Long idValue = 4L;
        partIn.setId(idValue);
        assertEquals(idValue, partIn.getId());
        partOut.setId(idValue);
        assertEquals(idValue, partOut.getId());
    }

    @Test
    void setId() {
        Long idValue = 4L;
        partIn.setId(idValue);
        assertEquals(idValue, partIn.getId());
        partOut.setId(idValue);
        assertEquals(idValue, partOut.getId());
    }

    @Test
    void getName() {
        String name = "test inhouse part";
        partIn.setName(name);
        assertEquals(name, partIn.getName());
        name = "test outsourced part";
        partOut.setName(name);
        assertEquals(name, partOut.getName());
    }

    @Test
    void setName() {
        String name = "test inhouse part";
        partIn.setName(name);
        assertEquals(name, partIn.getName());
        name = "test outsourced part";
        partOut.setName(name);
        assertEquals(name, partOut.getName());
    }

    @Test
    void getPrice() {
        double price = 1.0;
        partIn.setPrice(price);
        assertEquals(price, partIn.getPrice());
        partOut.setPrice(price);
        assertEquals(price, partOut.getPrice());
    }

    @Test
    void setPrice() {
        double price = 1.0;
        partIn.setPrice(price);
        assertEquals(price, partIn.getPrice());
        partOut.setPrice(price);
        assertEquals(price, partOut.getPrice());
    }

    @Test
    void getInv() {
        int inv = 5;
        partIn.setInv(inv);
        assertEquals(inv, partIn.getInv());
        partOut.setInv(inv);
        assertEquals(inv, partOut.getInv());
    }

    @Test
    void setInv() {
        int inv = 5;
        partIn.setInv(inv);
        assertEquals(inv, partIn.getInv());
        partOut.setInv(inv);
        assertEquals(inv, partOut.getInv());
    }

    @Test
    void getProducts() {
        Product product1 = new Product();
        Product product2 = new Product();
        Set<Product> myProducts = new HashSet<>();
        myProducts.add(product1);
        myProducts.add(product2);
        partIn.setProducts(myProducts);
        assertEquals(myProducts, partIn.getProducts());
        partOut.setProducts(myProducts);
        assertEquals(myProducts, partOut.getProducts());
    }

    @Test
    void setProducts() {
        Product product1 = new Product();
        Product product2 = new Product();
        Set<Product> myProducts = new HashSet<>();
        myProducts.add(product1);
        myProducts.add(product2);
        partIn.setProducts(myProducts);
        assertEquals(myProducts, partIn.getProducts());
        partOut.setProducts(myProducts);
        assertEquals(myProducts, partOut.getProducts());
    }

    @Test
    void testToString() {
        String name = "test inhouse part";
        partIn.setName(name);
        assertEquals(name, partIn.toString());
        name = "test outsourced part";
        partOut.setName(name);
        assertEquals(name, partOut.toString());
    }

    @Test
    void testEquals() {
        partIn.setId(1l);
        Part newPartIn = new InhousePart();
        newPartIn.setId(1l);
        assertEquals(partIn, newPartIn);
        partOut.setId(1l);
        Part newPartOut = new OutsourcedPart();
        newPartOut.setId(1l);
        assertEquals(partOut, newPartOut);

    }

    @Test
    void testHashCode() {
        partIn.setId(1l);
        partOut.setId(1l);
        assertEquals(partIn.hashCode(), partOut.hashCode());
    }

    /**
     * Unit Tests for the Part class
     * Focusing on Min and Max fields
     * Test 1: Verify the Minimum field setter and getter
     */
    @Test
    void testGetMin() {
        int expectedMin = 5;
        partIn.setMin(expectedMin);

        // Assert that the value we put in is the value we get out
        assertEquals(expectedMin, partIn.getMin(), "Minimum value should be equal to the set value");
    }

    /**
     * Test 2: Verify the Maximum field setter and getter
     */
    @Test
    void testGetMax() {
        int expectedMax = 100;
        partIn.setMax(expectedMax);

        // Assert that the value we put in is the value we get out
        assertEquals(expectedMax, partIn.getMax(), "Maximum value should be equal to the set value");
    }
}