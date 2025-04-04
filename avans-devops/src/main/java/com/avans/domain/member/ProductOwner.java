package com.avans.domain.member;

public class ProductOwner extends TeamMember {

    public ProductOwner(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "ProductOwner{" +
                "name='" + name + '\'' +
                '}';
    }
    
}
