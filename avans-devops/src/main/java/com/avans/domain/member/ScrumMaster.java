package com.avans.domain.member;

public class ScrumMaster extends TeamMember {
    public ScrumMaster(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "ScrumMaster{" +
                "name='" + name + '\'' +
                '}';
    }
    
}
