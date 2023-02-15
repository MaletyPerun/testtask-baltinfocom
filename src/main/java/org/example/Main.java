package org.example;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Main {

    // кол-во после фильтра и сортировки: 998578 (windows X, lng)
    // кол-во после фильтра и/или сортировки: 998578 (MacOS, lng-4) 13/8 сек

    static volatile boolean check = true;
    static CopyOnWriteArrayList<List<String>> mainFilteredAndSortedList = new CopyOnWriteArrayList<>(); // сортировка от большего к меньшему

    //    static List<Integer> sortedIntList = new ArrayList<>();
    static List<List<Long>> sortedLongList = new ArrayList<>();
    static Map<Integer, Set<List<String>>> resultIdSet = new HashMap<>();

    public static void main(String[] args) {
        final String pathToFile = "/Users/teplo/Desktop/lng-4.txt";
//        final String pathToFile = "/Users/teplo/Desktop/lng-4 2.txt";
//        final String pathToFile = "C://Users/Алексей/Desktop/lng.txt";
        final String regex = "^(\"\\d*\")(;\"\\d*\")*$";
        final Pattern pattern = Pattern.compile(regex);

        String str1 = "\"79805535143\";\"79629844729\";\"79279047724\";\"\";\"79023442969\"";
        String str2 = "\"79300855205\";\"79361449905\";\"79405798876\";\"79813087441\";\"79584895463\";\"79219383129\";\"79647376560\"";
        String str3 = "\"\";\"79361449905\";\"79405798876\";\"79813087441\";\"79584895463\";\"79219383129\";\"79647376560\"";

        String[] arr1 = str1.split(";");
        String[] arr2 = str2.split(";");


//        int[] arrayA = {-2, 1, 3, 5, 7, 9};
//        int[] arrayB = {-1, 0, 1, 3, 5, 7, 10};
//        int j = Arrays.mismatch(arrayA, 1, arrayA.length, arrayB, 2, arrayB.length);
//        System.out.println(j);

        List<String> list1 = List.of("\"79448126993\"", "\"\"", "\"79448126993\"");
        List<String> list2 = List.of("\"79448126993\"", "\"\"", "\"79822872594\"");

        // TODO: 16.02.2023
        boolean disjoint = Collections.disjoint(list1, list2);
        if (!disjoint) {
            int len1 = list1.size();
            int len2 = list2.size();

            int len = Math.min(len1, len2);
            int matches = 0;
            for (int i = 0; i < len; i++) {
                String str_1 = list1.get(i);
                String str_2 = list2.get(i);

                if (!("\"\"".equals(str_1)) && str_1.equals(str_2))
                    matches++;
            }
            System.out.println("matches = " + matches);
        }
        System.out.println(disjoint);
        System.out.println(list1);
        System.out.println("****");
        System.out.println(list2);


        String str4 = str3.replace("\"\"", "\"0\"");
        String str5 = str4.replace("\"", "");
//        System.out.println(str4);
//        System.out.println(str5);


        Runnable task = new Runnable() {
            int countTime = 0;

            public void run() {
                while (check) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.println(countTime++);
                }
            }
        };

        LocalTime startTime = LocalTime.now();

        Thread myThread = new Thread(task);
        myThread.start();

        System.out.println("start to filtered");
//        Set<String> filteredSet = readFile(pathToFile, pattern);
        // FIXME: 15.02.2023
        readFile(pathToFile, pattern);
        // не повторяющийся (и отсортированный от меньшего к большому по кол-ву элементов) список

        System.out.println("end to filter, size = " + mainFilteredAndSortedList.size());

        // FIXME: 15.02.2023
//        printOrder(mainFilteredAndSortedList, 10);

        System.out.println("start to find matches");
        // FIXME: 15.02.2023 
        findMatches(mainFilteredAndSortedList);
        System.out.println("end to find matches");
//        findMatchesOnFirstElement(mainFilteredAndSortedList);
//        System.out.println();

//        strParseToInt((List<String[]>) mainFilteredAndSortedList);

        // FIXME: 15.02.2023
//        printOrderInt(sortedLongList, 10);

//        findMatchesOnLong();
//        filteredSet.

//        log.info("start to find matches");
//        System.out.println("start to find matches");
//        findMatchesOnStr(filteredList);
//        System.out.println("end to find matches");
//        log.info("end to find matches");

        print();

        check = false;
        long secondsOfWork = ChronoUnit.SECONDS.between(startTime, LocalTime.now());
        System.out.println("seconds of work = " + secondsOfWork);
    }

    private static void findMatches(CopyOnWriteArrayList<List<String>> list) {
        for (int i = 0; i < list.size(); i++) {
            List<String> line = list.get(i);
//            System.out.println("start to find matches with line = " + list.get(i).toString());
            if (i % 100 == 0) {
                System.out.println(i);
            }
            for (int j = 1; j < list.size() - i; j++) {
                int matches = 0;
                if (!(Collections.disjoint(line, list.get(j + i)))) {
                    matches = writeTypeAndCountOfMatches(line, list.get(j + i));
//                    System.out.println("HAVE JOINT!");
                }
                if (matches != 0) {
                    Set<List<String>> set;
                    if (resultIdSet.containsKey(matches)) {
                        set = new HashSet<>(resultIdSet.get(matches));
                        System.out.println("update set with matches = " + matches);
                    } else {
                        set = new HashSet<>();
                        System.out.println("new set with matches = " + matches);
                    }
                    set.add(list.get(j));
                    set.add(list.get(j + i));
                    resultIdSet.put(matches, set);
                }
            }
        }
    }

    private static int writeTypeAndCountOfMatches(List<String> line1, List<String> line2) {
        int len = Math.min(line1.size(), line2.size());
        int matches = 0;
        for (int i = 0; i < len; i++) {
            if (!("\"\"".equals(line1.get(i))) && line1.get(i).equals(line2.get(i)))
                matches++;
        }
        return matches;
    }

//    private static void findMatchesOnFirstElement(CopyOnWriteArrayList<String[]> mainFilteredAndSortedList) {
////        for (int i = 0; i < mainFilteredAndSortedList.size(); i++) {
//        String[] firstLine = mainFilteredAndSortedList.get(2);
//        String firstElementOfFirstLineStr = firstLine[0];
//        long firstElementOfFirstLineLong = Long.parseLong(firstElementOfFirstLineStr);
//        int countOfMatches = 0;
//        int typeOfMatches = 1;
//        for (int j = 2; j < mainFilteredAndSortedList.size() - 2; j++) {
//            long x = 0;
//            if (mainFilteredAndSortedList.get(j).length == 0) {
//                continue;
//            }
//            try {
//                x = Long.parseLong(mainFilteredAndSortedList.get(j)[0]);
//            } catch (Exception e) {
////                System.out.println("mainFilteredAndSortedList.get(" + j + ")[0] = " + mainFilteredAndSortedList.get(j)[0]);
//                continue;
//            }
//            if (firstElementOfFirstLineLong == Long.parseLong(mainFilteredAndSortedList.get(j)[0])) {
//                Set<String[]> set;
//                if (resultIdSet.containsKey(typeOfMatches)) {
//                    set = new HashSet<>(resultIdSet.get(typeOfMatches));
//                } else {
//                    set = new HashSet<>();
//                }
//                set.add(mainFilteredAndSortedList.get(0));
//                set.add(mainFilteredAndSortedList.get(j));
//                resultIdSet.put(typeOfMatches, set);
//                countOfMatches++;
//            }
//        }
//        if (countOfMatches == 0) {
//            mainFilteredAndSortedList.remove(0);
//        } else {
//
//            countOfMatches = 0;
//        }
//        System.out.println("matches = " + countOfMatches);
//        System.out.println(resultIdSet.size());
//    }

    // TODO: 15.02.2023 методы

    public static void readFile(String pathToFile, Pattern pattern) {
        List<List<String>> list = null;
        try (Stream<String> lines = Files.lines(Paths.get(pathToFile))) {
            list = lines.filter(s -> {
                        Matcher matcher = pattern.matcher(s);
                        return matcher.matches();
                    })
                    .filter(s -> (s.length() != 0))
                    .distinct()
                    .sorted((s1, s2) -> new MyComparator().compare(s1, s2))
//                    .map(s -> s.replace("\"\"", "\"0\""))
//                    .map(s -> s.replace("\"", ""))
                    .map(s -> Arrays.stream(s.split(";"))
//                            .filter(s1 -> !("".equals(s1)))
                            .toList())
                    .toList();
//                            .mapToLong(Long::parseLong)
//                            .toArray())
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
//        return new CopyOnWriteArrayList<String>(mainFilteredAndSortedList);
        try {
            mainFilteredAndSortedList.addAll(list);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


//    public static void findMatchesOnStr(List<String> list) {
//        System.out.println("size of list = " + list.size());
//        for (int j = 0; j < list.size(); j++) {
//            System.out.println("get main str with id =" + j);
//            for (int i = 1; i < list.size() - j; i++) {
//                System.out.println("get compare str with id =" + i);
//                int matches = doEquals(list.get(j), list.get(j + i));
//                if (matches != 0) {
//                    Set<String> set;
//                    if (resultIdSet.containsKey(matches)) {
//                        set = new HashSet<>(resultIdSet.get(matches));
//                    } else {
//                        set = new HashSet<>();
//                    }
//                    set.add(list.get(j));
//                    set.add(list.get(j + i));
//                    resultIdSet.put(matches, set);
//                }
//            }
//        }
//    }

//    public static void findMatchesOnLong(List<List<Long>> list, int limit) {
//        List<List<Long>> shortList = list.stream()
//                .limit(limit)
//                .toList();
//        findMatchesOnLong(shortList);
//    }

//    public static void findMatchesOnLong(List<List<Long>> list) {
//        for (int i = 0; i < list.size(); i++) {
//            List<Long> lList1 = list.get(i);
//            for (int j = 1; j < list.size() - i; j++) {
//
//            }
//            List<Long> lList2 = list.get(i + 1);
//            int len = Math.min(lList1.size(), lList2.size());
//            int matches = 0;
//            for (int j = 0; j < len; j++) {
//                Long l1 = lList1.get(j);
//                Long l2 = lList2.get(j);
//                if (l1.equals(l2)) {
//                    matches++;
//                }
//            }
//            if (matches != 0) {
//                Set<String> set;
//                if (resultIdSet.containsKey(matches)) {
//                    set = new HashSet<>(resultIdSet.get(matches));
//                } else {
//                    set = new HashSet<>();
//                }
////                set.add(list.get(j));
////                set.add(list.get(j + i));
//                resultIdSet.put(matches, set);
//            }
//        }
//        System.out.println("size of list = " + list.size());
//        for (int j = 0; j < list.size(); j++) {
//            System.out.println("get main str with id =" + j);
//            for (int i = 1; i < list.size() - j; i++) {
//                System.out.println("get compare str with id =" + i);
//                int matches = doEquals(list.get(j), list.get(j + i));
//                if (matches != 0) {
//                    Set<String> set;
//                    if (resultIdSet.containsKey(matches)) {
//                        set = new HashSet<>(resultIdSet.get(matches));
//                    } else {
//                        set = new HashSet<>();
//                    }
//                    set.add(list.get(j));
//                    set.add(list.get(j + i));
//                    resultIdSet.put(matches, set);
//                }
//            }
//        }
//    }

    public static int doEquals(String str1, String str2) {
        String[] arr1 = str1.split(";");
        String[] arr2 = str2.split(";");
        int length = Math.min(arr1.length, arr2.length);
        return (int) IntStream.range(0, length)
                .filter(i -> !("".equals(arr1[i])) && (arr1[i].equals(arr2[i])))
                .count();
    }

//    public static int doEquals(List<Long> lList1, List<Long> lList2) {
//        int length = Math.min(arr1.length, arr2.length);
//        return (int) IntStream.range(0, length)
//                .filter(i -> !("".equals(arr1[i])) && (arr1[i].equals(arr2[i])))
//                .count();
//    }

    public static void print() {
//        List<Integer> keys = new ArrayList<>(resultIdSet.keySet());
//        int length = keys.size();
//        for (int i = length - 1; i >= 0; i--) {
//            System.out.println("Группа " + (length - i));
//            Set<String[]> set = new HashSet<>(resultIdSet.get(keys.get(i)));
//            for (String[] x : set) {
//                System.out.println("Строка " + x);
//            }
//        }
        for (int x : resultIdSet.keySet()) {
            System.out.println(x);
        }
    }

//    public static void strParseToInt(List<String[]> strList) {
//        sortedLongList = strList.stream()
//                .map(s -> s.replaceAll("\"", ""))
//                .map(s -> {
//                    return Arrays.stream(s.split(";"))
////                            .map(s1 -> s1.replace("\"", ""))
//                            .map(l -> {
//                                if ("".equals(l)) {
//                                    return 0l;
//                                } else {
//                                    return Long.parseLong(l);
//                                }
//                            })
//                            .toList();
//                })
//                .collect(Collectors.toList());
//
//        // FIXME: 15.02.2023 тестовая
////        String str = strList.get(0);
////        String[] arrStr = str.split(";");
////        for (String x : arrStr) {
////            x = x.replaceAll("\"", "");
////            long m = 0;
////            if (!(x.equals(""))) {
////                m = Long.parseLong(x);
////            }
////            System.out.println(m);
////        }
//
//
////        System.out.println(str);
////        List<Integer> listOfInt = Arrays.stream(str.split(";"))
////                .map(s -> s.replaceAll("\"", ""))
////                .map(Integer::parseInt)
////                .toList();
////        printOrderInt(listOfInt, 10);
//
////        List<Integer> listOfInteger = convert
//    }

    //    public static void printOrder(CopyOnWriteArrayList<String[]> conList, int limit) {
    public static void printOrder(List<long[]> conList, int limit) {
        System.out.println("***ORDER LIST STR***");
//        conList.stream()
//                .limit(limit)
//                .forEach(s -> Arrays.stream(s).forEach(System.out::println));
        for (int i = 0; i < limit; i++) {
            try {
                Arrays.stream(conList.get(i)).forEach(s -> System.out.print(s + " "));
            } catch (Exception e) {
                System.out.println("i = " + i);
                System.out.println(conList.size());
                System.out.println(conList.get(i));
            }
            System.out.println();
        }
        System.out.println("***END ORDER***");
    }

    public static void printOrder(Set<String> set, int limit) {
        System.out.println("***ORDER SET STR***");
        set.stream()
                .limit(limit)
                .forEach(System.out::println);
        System.out.println("***END ORDER***");
    }

//    public static void printOrderInt(List<Integer> set, int limit) {
//    public static void printOrderInt(List<List<Integer>> list, int limit) {
//        System.out.println("***ORDER LIST INT***");
//        list.stream()
//                .limit(limit)
//                .forEach(System.out::println);
//        System.out.println("***END ORDER***");
//    }

    public static void printOrderInt(List<List<Long>> list, int limit) {
        System.out.println("***ORDER LIST LONG***");
        list.stream()
                .limit(limit)
                .forEach(System.out::println);
        System.out.println("***END ORDER***");
    }
}
