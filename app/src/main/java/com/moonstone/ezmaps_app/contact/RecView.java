package com.moonstone.ezmaps_app.contact;

public enum RecView {
    REQUESTS (0),
    CONTACTS (1),
    GROUPCHATS (2);

    private int numVal;

    RecView(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
