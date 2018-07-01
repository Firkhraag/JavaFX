package org.spbu.histology.model;

import javafx.util.converter.DoubleStringConverter;

public class MyDoubleStringConverter extends DoubleStringConverter {
    @Override
    public Double fromString(final String value) {
        return value.isEmpty() || !isNumber(value) ? null :
            super.fromString(value);
    }
    public boolean isNumber(String value) {
        int size = value.length();
        int numOfPoints = 0;
        for (int i = 0; i < size; i++) {
            if (!Character.isDigit(value.charAt(i))) {
                if (i == 0) {
                    if (value.charAt(i) == '-')
                        continue;
                }
                else if (value.charAt(i) == '.') {
                    if (numOfPoints == 0) {
                        numOfPoints++;
                        continue;
                    }
                    else
                        return false;
                }
                return false;
            }
        }
        return size > 0;
    }
}
