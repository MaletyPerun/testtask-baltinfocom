package org.example;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
    static Set<long[]> mainFilteredAndSortedSet = new LinkedHashSet<>(); // сортировка от большего к меньшему
    static Set<long[]> copySetOfMainWithOneMatch = new LinkedHashSet<>();
    static List<long[]> copyListOfMainWithoutOneMatch = new ArrayList<>();
    static LinkedList<long[]> mainFilteredAndSortedLinkedList = new LinkedList<>();
    //    static Set<Set<long[]>> setOfColums = new LinkedHashSet<>();
    static int maxAmountElementsOfLine = 0;
    static Map<Integer, Set<long[]>> mapOfDuplicateWithCount = new HashMap<>();
    static List<Integer> idToRemove = new ArrayList<>();
    //    static List<Integer> sortedIntList = new ArrayList<>();
    static List<List<Long>> sortedLongList = new ArrayList<>();
    static Map<Integer, Set<long[]>> resultCountRepeatSet = new HashMap<>();

    public static void main(String[] args) {
        final String pathToFile = "/Users/teplo/Desktop/lng-4.txt";
//        final String pathToFile = "/Users/teplo/Desktop/lng-4 2.txt";
//        final String pathToFile = "C://Users/Алексей/Desktop/lng.txt";
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
//        System.out.println("end to filter, size = " + mainFilteredAndSortedSet.size());
        // FIXME: 15.02.2023
//        printOrder(mainFilteredAndSortedList, 10);
        System.out.println("start to find matches");
        // FIXME: 15.02.2023
//        findMatches(mainFilteredAndSortedList);
//        findMatchesOnMap(mainFilteredAndSortedMap);
        findMatches(mainFilteredAndSortedSet, 1);
        print();

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

//        print();

        check = false;
        long secondsOfWork = ChronoUnit.SECONDS.between(startTime, LocalTime.now());
        System.out.println("seconds of work = " + secondsOfWork);
    }

    // TODO: 15.02.2023 методы
    public static void readFile(String pathToFile, Pattern pattern) {
        List<long[]> list = null;
        try (Stream<String> lines = Files.lines(Paths.get(pathToFile))) {
            list = lines.filter(s -> {
                        Matcher matcher = pattern.matcher(s);
                        return matcher.matches();
                    })
                    .filter(s -> (s.length() != 0))
                    .distinct()
                    .sorted((s1, s2) -> new MyComparator().compare(s1, s2))
                    .map(s -> s.replace("\"\"", "0"))
                    .map(s -> s.replace("\"", ""))
                    .map(s -> Arrays.stream(s.split(";"))
                            .mapToLong(Long::parseLong)
                            .toArray())
//                    .collect(Collectors.toMap(i -> numberOfLine.getAndIncrement(), s -> s));
                    .toList();
            System.out.println(list.size());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // TODO: 20.02.2023 валидация
//        if (list.isEmpty()) {
//
//        }
        try {
//            mainFilteredAndSortedMap.putAll(map);
//            System.out.println("size of map = " + mainFilteredAndSortedMap.size());
            mainFilteredAndSortedSet.addAll(list);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

//        maxAmountElementsOfLine = mainFilteredAndSortedList.get(0).length;
//        System.out.println("maxAmountElementsOfLine = " + maxAmountElementsOfLine);
//        System.out.println("mainFilteredAndSortedList size of readFile = " + mainFilteredAndSortedList.size());
//        System.out.println("line #1 of mainFilteredAndSortedList of readFile = " + Arrays.toString(mainFilteredAndSortedList.get(0)));

//        System.out.println("line #1: " + Arrays.toString(mainFilteredAndSortedMap.get(0)));
//        return 0;
    }

    private static void findMatches(Set<long[]> copiesOfLines, int countOfDuplicate) {
        Set<Set<long[]>> columns = new LinkedHashSet<>(takeColumns(copiesOfLines));
        Set<long[]> set = findOneMatchOnStream(columns, countOfDuplicate);
        if (!set.isEmpty()) {
            countOfDuplicate++;
            findMatches(set, countOfDuplicate);
        }
//        findAnotherMatchesOnStream(mainFilteredAndSortedList);
//        findAnotherMatchesOnStream(set);
    }

    private static Set<Set<long[]>> takeColumns(Set<long[]> copiesOfLines) {
        int size = 1;
        Set<Set<long[]>> setOfColumns = new LinkedHashSet<>();
        while (maxAmountElementsOfLine >= size) {

            // взять столбец long и сделать массив
            int finalSize = size;
            long[] thumb = copiesOfLines.stream()
//                    .filter(longs -> longs.length >= finalSize)
                    .flatMapToLong(s -> LongStream.of(Arrays.stream(s)
                            .skip(finalSize - 1)
//                            .filter(l -> l != 0)
                            .findFirst()
                            .orElse(0)))
                    .toArray();
            // получил отдельный i-столбец

            // найти совпадающие значения в столбце
            Set<Long> clear = new HashSet<>();
            Set<Long> copies = new HashSet<>();
//            Set<Integer> copiesId = new HashSet<>();
//            Set<Integer> copiesIdTo1 = new HashSet<>();
            for (int i = 0; i < thumb.length; i++) {
                if (thumb[i] != 0 && !clear.add(thumb[i])) {
                    copies.add(thumb[i]);
//                    copiesId.add(i);
                }
            }
            // получил set повторяющихся значений в столбце

            // найти из предоставленной карты строки с повторяющимися значениями в i-столбце
            Set<long[]> set = mainFilteredAndSortedSet.stream()
                    .filter(s -> Arrays.stream(s)
                            .skip(finalSize - 1)
                            .limit(1)
//                            .findFirst()
                            .anyMatch(copies::contains))
//                            .filter(l -> copies.contains(l))
//                            .findFirst().isPresent())
                    .collect(Collectors.toSet());

//            copySetOfMainWithOneMatch.addAll(set);
            // TODO: 19.02.2023 добавить set, а не массив
            setOfColumns.add(set);
//            Collections.addAll(listOfThumbs, copies);
            // получил set строк с повторяющимися значениями в i-столбце

//            System.out.println("size:" + finalSize + " of test heap-to-remove = " + thumb.length);
//            System.out.println("size:" + finalSize + " of clear-set = " + clear.size());
//            System.out.println("size:" + finalSize + " of copies-set = " + copies.size());
//            System.out.println("size:" + finalSize + " of copies-id-set = " + copiesId.size());
//            System.out.println("size:" + finalSize + " of copy-set = " + copySetOfMainWithOneMatch.size());
//            System.out.println("*****");

            size++;
        }

//        searchNestCopies(listOfThumbs);


//        System.out.println(mainFilteredAndSortedList.size() + " : mainFilteredAndSortedList, size, before clean, findOneMatchOnStream");
//        System.out.println(Arrays.toString(mainFilteredAndSortedList.get(0)) + " : line #1, mainFilteredAndSortedList, size, before clean, findOneMatchOnStream");
//        System.out.println(copySetOfMainWithOneMatch.size() + " : copySetOfMainWithOneMatch, size, before clean, findOneMatchOnStream");
//        System.out.println(listOfThumbs.size() + "listOfThumbs");

//        resultCountRepeatSet.put(1, new HashSet<>(copySetOfMainWithOneMatch));
//        mainFilteredAndSortedList.retainAll(copySetOfMainWithOneMatch);
//        copySetOfMainWithOneMatch.clear();


//        System.out.println(resultCountRepeatSet.get(1).size() + " : resultCountRepeatSet, 1 repeat, size, after clean");
//        System.out.println(mainFilteredAndSortedList.size() + " : mainFilteredAndSortedList, size, after clean, findOneMatchOnStream");
//        System.out.println(Arrays.toString(mainFilteredAndSortedList.get(0)) + " : line #1, mainFilteredAndSortedList, size, after clean, findOneMatchOnStream");
//        System.out.println(copySetOfMainWithOneMatch.size() + " : copySetOfMainWithOneMatch, size, after clean, findOneMatchOnStream");

        return setOfColumns;
//        return null;
    }

    private static Set<long[]> findOneMatchOnStream(Set<Set<long[]>> columns, int countOfDuplicate) {
        Set<long[]> nextSetLevel = new LinkedHashSet<>();
        Set<long[]> duplicate = new LinkedHashSet<>();
        for (Set<long[]> overLongs : columns) {
            for (Set<long[]> longs : columns) {
                Set<long[]> set = new HashSet<>(overLongs);
                set.retainAll(longs);
                if (!set.isEmpty()) {
                    continue;
                }
                if (!set.containsAll(longs)) {
                    nextSetLevel.addAll(set);
                } else {
                    duplicate.addAll(longs);
                }
            }
        }
        mapOfDuplicateWithCount.put(countOfDuplicate, duplicate);
//        findMatches(nextSetLevel, countOfDuplicate);
        return nextSetLevel;





//        int size = 1;
//        while (maxAmountElementsOfLine >= size) {
//
//            // взять столбец long и сделать массив
//            int finalSize = size;
////            long[] thumb = copiesOfLines.stream()
//////                    .filter(longs -> longs.length >= finalSize)
////                    .flatMapToLong(s -> LongStream.of(Arrays.stream(s)
////                            .skip(finalSize - 1)
//////                            .filter(l -> l != 0)
////                            .findFirst()
////                            .orElse(0)))
////                    .toArray();
////            // получил отдельный i-столбец
////
////            // найти совпадающие значения в столбце
////            Set<Long> clear = new HashSet<>();
////            Set<Long> copies = new HashSet<>();
//////            Set<Integer> copiesId = new HashSet<>();
//////            Set<Integer> copiesIdTo1 = new HashSet<>();
////            for (int i = 0; i < thumb.length; i++) {
////                if (thumb[i] != 0 && !clear.add(thumb[i])) {
////                    copies.add(thumb[i]);
//////                    copiesId.add(i);
////                }
////            }
//            // получил set повторяющихся значений в столбце
//
//            // найти из предоставленной карты строки с повторяющимися значениями в i-столбце
////            Set<long[]> set = copiesOfLines.stream()
////                    .filter(s -> Arrays.stream(s)
////                            .skip(finalSize - 1)
////                            .limit(1)
//////                            .findFirst()
////                            .anyMatch(columns::contains))
//////                            .filter(l -> copies.contains(l))
//////                            .findFirst().isPresent())
////                    .collect(Collectors.toSet());
////
////            copySetOfMainWithOneMatch.addAll(set);
////
////
////            // TODO: 19.02.2023 добавить set, а не массив
////            setOfColums.add(set);
////            Collections.addAll(listOfThumbs, copies);
//            // получил set строк с повторяющимися значениями в i-столбце
//
////            System.out.println("size:" + finalSize + " of test heap-to-remove = " + thumb.length);
////            System.out.println("size:" + finalSize + " of clear-set = " + clear.size());
////            System.out.println("size:" + finalSize + " of copies-set = " + copies.size());
////            System.out.println("size:" + finalSize + " of copies-id-set = " + copiesId.size());
////            System.out.println("size:" + finalSize + " of copy-set = " + copySetOfMainWithOneMatch.size());
////            System.out.println("*****");
//
//            size++;
//        }

//        searchNestCopies(listOfThumbs);


//        System.out.println(mainFilteredAndSortedList.size() + " : mainFilteredAndSortedList, size, before clean, findOneMatchOnStream");
//        System.out.println(Arrays.toString(mainFilteredAndSortedList.get(0)) + " : line #1, mainFilteredAndSortedList, size, before clean, findOneMatchOnStream");
//        System.out.println(copySetOfMainWithOneMatch.size() + " : copySetOfMainWithOneMatch, size, before clean, findOneMatchOnStream");
//        System.out.println(listOfThumbs.size() + "listOfThumbs");

//        resultCountRepeatSet.put(1, new HashSet<>(copySetOfMainWithOneMatch));
//        mainFilteredAndSortedList.retainAll(copySetOfMainWithOneMatch);
//        copySetOfMainWithOneMatch.clear();


//        System.out.println(resultCountRepeatSet.get(1).size() + " : resultCountRepeatSet, 1 repeat, size, after clean");
//        System.out.println(mainFilteredAndSortedList.size() + " : mainFilteredAndSortedList, size, after clean, findOneMatchOnStream");
//        System.out.println(Arrays.toString(mainFilteredAndSortedList.get(0)) + " : line #1, mainFilteredAndSortedList, size, after clean, findOneMatchOnStream");
//        System.out.println(copySetOfMainWithOneMatch.size() + " : copySetOfMainWithOneMatch, size, after clean, findOneMatchOnStream");

//        return copySetOfMainWithOneMatch;
        //        return listOfThumbs;
    }

//    private static void searchNestCopies(Set<long[]> listOfThumbs) {
//
//        // найти совпадающие значения в столбце
//        Set<Long> clear = new HashSet<>();
//        Set<Long> copies = new HashSet<>();
////            Set<Integer> copiesId = new HashSet<>();
////            Set<Integer> copiesIdTo1 = new HashSet<>();
//        for (int i = 0; i < thumb.length; i++) {
//            if (!clear.add(thumb[i])) {
//                copies.add(thumb[i]);
////                    copiesId.add(i);
//            }
//        }
//        // получил set повторяющихся значений в столбце
//
//        // найти из предоставленной карты строки с повторяющимися значениями в i-столбце
//        Set<long[]> set = mainFilteredAndSortedList.stream()
//                .filter(s -> Arrays.stream(s)
//                        .skip(finalSize - 1)
//                        .anyMatch(copies::contains))
////                            .filter(l -> copies.contains(l))
////                            .findFirst().isPresent())
//                .collect(Collectors.toSet());
//
//        copySetOfMainWithOneMatch.addAll(set);
//        Collections.addAll(listOfThumbs, thumb);
//
//
//    }

//    private static void findAnotherMatchesOnStream(Set<long[]> copiesFromPreve) {
//        Set<long[]> nextSetLevel = new LinkedHashSet<>();
//        int finalSize = 1;
////        Iterator iterator = listOfThumbs.iterator();
//
//
//        // TODO: 19.02.2023 работать с LinkedHashSet столбцов
////        for (long[] thumb : listOfThumbs) {
////        for (Set<Long> thumb : listOfThumbs) {
//////        while (iterator.hasNext()) {
//////            long[] longs = (long[]) iterator.next();
//////            Set<Long> clear = new HashSet<>();
//////            Set<Long> copies = new HashSet<>();
////////            Set<Integer> copiesId = new HashSet<>();
////////            Set<Integer> copiesIdTo1 = new HashSet<>();
//////
//////            for (long longs : thumb) {
//////                if (longs != 0 && !clear.add(longs)) {
//////                    copies.add(longs);
//////                }
//////            }
////
//////            for (int i = 0; i < thumb.length; i++) {
//////                if (thumb[i] != 0 && !clear.add(thumb[i])) {
//////                    copies.add(thumb[i]);
////////                    copiesId.add(i);
//////                }
//////            }
////
//////            if(copies.size() == 1)
//////                continue;
////            // получил set повторяющихся значений в столбце
////
////            // найти из предоставленной карты строки с повторяющимися значениями в i-столбце
////            int finalSize1 = finalSize;
////            // TODO: 19.02.2023 сделать проверку на входящий пустой set copies
////            Set<long[]> set = mainCopyOfMethod.stream()
////                    .filter(s -> Arrays.stream(s)
////                            .skip(finalSize1 - 1)
////                            .limit(1)
////                            .anyMatch(thumb::contains))
////                    .collect(Collectors.toSet());
////
////
////            // TODO: 19.02.2023 перевести все на HashLinkedSet
////            // TODO: 19.02.2023 подумать над frequency
////            copyEveryCopiesWithDuplicate.addAll(everyCopiesWithDuplicate);
//////            if (!set.isEmpty()) {
////////                mainCopyOfMethod.clear();
//////                mainCopyOfMethod.retainAll(set);
//////            }
////            everyCopiesWithDuplicate.addAll(set);
////            copyEveryCopiesWithDuplicate.retainAll(set);
////            thumbWithOwnLines.put(finalSize, set);
////            // TODO: 19.02.2023 здесь добавлю уже дублированные, поэтому ухожу в цикл
//////            nextSetLevel.addAll(copyEveryCopiesWithDuplicate);
//////            mainCopyOfMethod.clear();
//////            mainCopyOfMethod.addAll(nextSetLevel);
////
////            finalSize++;
////        }
//
//
////        Set<long[]> set = new HashSet<>();
////        Set<long[]> set1 = thumbWithOwnLines.get(1);
//
////        for (Set<long[]> overLongs : setOfColums) {
////            for (Set<long[]> longs : setOfColums) {
////                Set<long[]> set = new HashSet<>(overLongs);
////                set.retainAll(longs);
////                if (!set.isEmpty()) {
////                    continue;
////                }
////                if (!set.containsAll(longs)) {
////                    nextSetLevel.addAll(set);
////                } else {
////                    copySetOfMainWithOneMatch.addAll(longs);
////                }
////
////            }
////        }
//
////        // TODO: 20.02.2023 найдены те самые 5 строчек
////        System.out.println(nextSetLevel.size() + " nextSetLevel size");
//    }

    public static void print() {
        for (Map.Entry<Integer, Set<long[]>> entry : mapOfDuplicateWithCount.entrySet())
            System.out.println(entry.getKey() + " : " + entry.getValue().size());
    }
}
