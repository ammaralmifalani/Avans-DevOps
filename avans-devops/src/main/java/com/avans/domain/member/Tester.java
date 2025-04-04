package com.avans.domain.member;

public class Tester extends TeamMember {
    public Tester(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "Tester{" +
                "name='" + name + '\'' +
                '}';
    }
    
}
