package com.example.demo.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 *
 *
 *
 */
@Entity
@DiscriminatorValue("2")
public class OutsourcedPart extends Part{
private String companyName;

    public OutsourcedPart() {
    }

    public OutsourcedPart(long id, String name, double price, int inv, int min, int max, String companyName) {
        super(id, name, price, inv, min, max);
        this.companyName = companyName;
    }
    public OutsourcedPart(String name, double price, int inv, int min, int max, String companyName) {
        super(name, price, inv, min, max);
        this.companyName = companyName;
    }

    public String getCompanyName() {

        return companyName;
    }

    public void setCompanyName(String companyName) {

        this.companyName = companyName;
    }
}
