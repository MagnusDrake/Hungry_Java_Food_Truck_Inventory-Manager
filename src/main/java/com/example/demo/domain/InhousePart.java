package com.example.demo.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("1")
public class InhousePart extends Part{
    private int partId;

    public InhousePart(long id, String name, double price, int inv, int min, int max, int partId) {
        super(id, name, price, inv, min, max);
        this.partId = partId;
    }
    public InhousePart(String name, double price, int inv, int min, int max, int partId) {
        super(name, price, inv, min, max);
        this.partId = partId;
    }

    public InhousePart() {
    }

    public int getPartId() {

        return partId;
    }

    public void setPartId(int partId) {

        this.partId = partId;
    }
}
