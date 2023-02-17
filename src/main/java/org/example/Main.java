package org.example;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;


public class Main {

    // кол-во после фильтра и сортировки: 998578 (windows X, lng)
    // кол-во после фильтра и/или сортировки: 998578 (MacOS, lng-4) 13/8 сек

    static volatile boolean check = true;
    static ConcurrentMap<Integer, long[]> mainFilteredAndSortedMap = new ConcurrentHashMap<>();
    static AtomicInteger numberOfLine = new AtomicInteger(0);
    static CopyOnWriteArrayList<long[]> mainFilteredAndSortedList = new CopyOnWriteArrayList<>(); // сортировка от большего к меньшему
    static Set<long[]> copySetOfMainWithOneMatch = new HashSet<>();
    static List<long[]> copyListOfMainWithoutOneMatch = new ArrayList<>();
    static LinkedList<long[]> mainFilteredAndSortedLinkedList = new LinkedList<>();
    static int maxAmountElementsOfLine = 0;
    static List<Integer> idToRemove = new ArrayList<>();
    //    static List<Integer> sortedIntList = new ArrayList<>();
    static List<List<Long>> sortedLongList = new ArrayList<>();
    static Map<Integer, Set<long[]>> resultIdSet = new HashMap<>();

    public static void main(String[] args) {
//        final String pathToFile = "/Users/teplo/Desktop/lng-4.txt";
//        final String pathToFile = "/Users/teplo/Desktop/lng-4 2.txt";
        final String pathToFile = "C://Users/Алексей/Desktop/lng.txt";
//        final String pathToFile = "C://Users/Алексей/Desktop/lng-2.txt";
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

        String str4 = str3.replace("\"\"", "\"0\"");
        String str5 = str4.replace("\"", "");

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
        // FIXME: 15.02.2023
        // TODO: 17.02.2023 проверить лист и связанный лист
        readFile(pathToFile, pattern);
        // не повторяющийся (и отсортированный от большего к меньшему по кол-ву элементов) список
        System.out.println("end to filter, size = " + mainFilteredAndSortedList.size());


        // FIXME: 15.02.2023
//        printOrder(mainFilteredAndSortedList, 10);


        System.out.println("start to find matches");
        // FIXME: 15.02.2023
//        findMatches(mainFilteredAndSortedList);
//        findMatchesOnMap(mainFilteredAndSortedMap);
        findMatches();

        System.out.println("end to find matches");
//        findMatchesOnFirstElement(mainFilteredAndSortedList);


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

    // TODO: 15.02.2023 методы
    public static void readFile(String pathToFile, Pattern pattern) {
        List<long[]> list = null;
        Map<Integer, long[]> map = null;
        try (Stream<String> lines = Files.lines(Paths.get(pathToFile))) {
            list = lines.filter(s -> {
                        Matcher matcher = pattern.matcher(s);
                        return matcher.matches();
                    })
                    .filter(s -> (s.length() != 0))
                    .distinct()
//                    .sorted((s1, s2) -> new MyComparator().compare(s1, s2))
                    .map(s -> s.replace("\"\"", "0"))
                    .map(s -> s.replace("\"", ""))
                    .map(s -> Arrays.stream(s.split(";"))
                            .mapToLong(Long::parseLong)
                            .toArray())
//                    .collect(Collectors.toMap(i -> numberOfLine.getAndIncrement(), s -> s));
                    .toList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
//            mainFilteredAndSortedMap.putAll(map);
//            System.out.println("size of map = " + mainFilteredAndSortedMap.size());
            mainFilteredAndSortedList.addAll(list);
            System.out.println(mainFilteredAndSortedList.size());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        maxAmountElementsOfLine = mainFilteredAndSortedList.get(0).length;

        System.out.println("max len = " + mainFilteredAndSortedList.get(0).length);
        System.out.println("line #1: " + Arrays.toString(mainFilteredAndSortedList.get(0)));
//        System.out.println("line #1: " + Arrays.toString(mainFilteredAndSortedMap.get(0)));
//        return 0;
    }

    private static void findMatches() {
        findOneMatchOnStream();
        findAnotherMatchesOnStream();
    }

    private static void findOneMatchOnStream() {

        int size = 1;
        while (maxAmountElementsOfLine >= size) {
            int finalSize = size;
            long[] thumb = mainFilteredAndSortedList.stream()
                    .filter(longs -> longs.length >= finalSize)
                    .flatMapToLong(s -> LongStream.of(Arrays.stream(s)
                            .skip(finalSize - 1)
                            .findFirst()
                            .orElse(0)))
                    .toArray();

            Set<Long> clear = new HashSet<>();
            Set<Long> copies = new HashSet<>();
            Set<Integer> copiesId = new HashSet<>();
            Set<Integer> copiesIdTo1 = new HashSet<>();
            for (int i = 0; i < thumb.length; i++) {
                if (!clear.add(thumb[i])) {
                    copies.add(thumb[i]);
                    copiesId.add(i);
                }
            }

            Set<long[]> set = mainFilteredAndSortedList.stream()
                    .filter(s -> Arrays.stream(s)
                            .skip(finalSize - 1)
                            .filter(l -> copies.contains(l))
                            .findFirst().isPresent())
                    .collect(Collectors.toSet());

            copySetOfMainWithOneMatch.addAll(set);

            System.out.println("size:" + finalSize + " of test heap-to-remove = " + thumb.length);
            System.out.println("size:" + finalSize + " of clear-set = " + clear.size());
            System.out.println("size:" + finalSize + " of copies-set = " + copies.size());
            System.out.println("size:" + finalSize + " of copies-id-set = " + copiesId.size());
            System.out.println("size:" + finalSize + " of copy-set = " + copySetOfMainWithOneMatch.size());
            System.out.println("*****");

            size++;
        }

//        Set<long[]> overCopies = Set.copyOf(copySetOfMainWithOneMatch);

        System.out.println("mainFilteredAndSortedList size = " + mainFilteredAndSortedList.size());
        // FIXME: 17.02.2023 убрать использование List, так как после первой полной проверки длина = 2,5 ляма!
        System.out.println("copyListOfMainWithOneMatch size = " + copySetOfMainWithOneMatch.size());
//        System.out.println("overCopies size = " + overCopies.size());

        resultIdSet.put(1, copySetOfMainWithOneMatch);
    }

    private static void findAnotherMatchesOnStream() {
    }

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
}
