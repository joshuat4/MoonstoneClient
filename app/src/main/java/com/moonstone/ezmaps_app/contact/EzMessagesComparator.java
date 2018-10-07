package com.moonstone.ezmaps_app.contact;

import java.util.Comparator;

public class EzMessagesComparator implements Comparator<EzMessage> {
    @Override
    public int compare(EzMessage e1, EzMessage e2) {
        return e1.getTime().compareTo(e2.getTime());
    }
}
