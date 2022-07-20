package com.madmobiledevs.ecommerce;

public class ValuesRemember {

   static int lastScrollValue;

    public ValuesRemember(int lastScrollValue) {
        this.lastScrollValue = lastScrollValue;
    }

    public int getLastScrollValue() {
        return lastScrollValue;
    }

    public void setLastScrollValue(int lastScrollValue) {
        this.lastScrollValue = lastScrollValue;
    }
}
