package org.example;

import java.util.Comparator;

public class MyComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        String str1 = o1.toString();
        String str2 = o2.toString();

        String[] arr1 = str1.split(";");
        String[] arr2 = str2.split(";");

        int len1 = arr1.length;
        int len2 = arr2.length;

        return len2 - len1;
    }
}
