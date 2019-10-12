package com.cbcds.app.unitable;

import com.cbcds.app.unitable.database.Class;

import java.util.Comparator;

public class ClassComparator implements Comparator<Class> {
    @Override
    public int compare(Class c1, Class c2) {
        int number1 = c1.getNumber();
        int number2 = c2.getNumber();
        if (number1 > number2) {
            return 1;
        } else if (number1 < number2) {
            return -1;
        } else {
            return 0;
        }
    }
}
