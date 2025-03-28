package com.avans.domain.member;

public class LeadDeveloper extends TeamMember {
    public LeadDeveloper(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "LeadDeveloper{" +
                "name='" + name + '\'' +
                '}';
    }
    
}
