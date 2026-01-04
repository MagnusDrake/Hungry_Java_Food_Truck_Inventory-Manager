package com.example.demo.domain;

import com.example.demo.validators.ValidEnufParts;
import com.example.demo.validators.ValidProductPrice;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List; // <--- Changed from Set to List

@Entity
@Table(name="Products")
@ValidProductPrice
@ValidEnufParts
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotBlank(message = "Product name is required")
    private String name;
    @Min(value = 0, message = "Price value must be positive")
    private double price;
    @Min(value = 0, message = "Inventory value must be positive")
    private int inv;
    @Min(value = 0, message = "Labor cost cannot be negative")
    private double laborCost;

    // 1. Changed to List to allow duplicates
    // 2. Added @JoinTable here so Product "Owns" the list

    @ManyToMany(cascade = {
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH},
            fetch = FetchType.LAZY)
    @JoinTable(name = "product_part",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "part_id"))
    @OrderColumn(name = "parts_order")
    private List<Part> parts = new ArrayList<>();

    public Product() {
    }

    public Product(String name, double price, int inv, double laborCost) {
        this.name = name;
        this.price = price;
        this.inv = inv;
        this.laborCost = laborCost;
    }

    public Product(long id, String name, double price, int inv, double laborCost) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.inv = inv;
        this.laborCost = laborCost;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public double getPrice() {

        return price;
    }

    public void setPrice(double price) {

        this.price = price;
    }

    public int getInv() {

        return inv;
    }

    public void setInv(int inv) {

        this.inv = inv;
    }

    // --- UPDATE GETTERS AND SETTERS TO MATCH LIST ---
    public List<Part> getParts() {

        return parts;
    }

    public void setParts(List<Part> parts) {

        this.parts = parts;
    }

    public String toString(){

        return this.name;
    }

    // --- Add Getter and Setter ---
    public double getLaborCost() {
        return laborCost;
    }

    public void setLaborCost(double laborCost) {
        this.laborCost = laborCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}