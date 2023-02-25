package org.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;


public class Main {

    // кол-во после фильтра и сортировки: 998578 (windows X, lng)
    // кол-во после фильтра и/или сортировки: 998578 (MacOS, lng-4) 13/8 сек

    static volatile boolean check = true;
    static List<long[]> mainFilteredAndSortedList = new LinkedList<>(); // сортировка от большего к меньшему
    static Set<long[]> copySetOfMainWithOneMatch = new LinkedHashSet<>();
    static Set<Long> setOfCopies = new LinkedHashSet<>();
    static Set<Map<Long, Set<long[]>>> result = new LinkedHashSet<>();
    static int maxAmountElementsOfLine = 0;
    static Map<Integer, Set<long[]>> mapOfDuplicateWithCount = new HashMap<>();
    static String path = "/Users/teplo/Desktop/lng-4.txt";

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
        Set<long[]> mainCopies = readFile(pathToFile, pattern);
        findMaxAmount();
        // не повторяющийся (и отсортированный от большего к меньшему по кол-ву элементов) список
        // FIXME: 15.02.2023
//        printOrder(mainFilteredAndSortedList, 10);
        System.out.println("start to find matches");
        // FIXME: 15.02.2023
//        findMatches(mainFilteredAndSortedList);
//        findMatchesOnMap(mainFilteredAndSortedMap);
        findMatches(mainCopies, 1);
        try {
            print();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("ошибка записи в файл");
        }

        System.out.println("end to find matches");

        check = false;
        long secondsOfWork = ChronoUnit.SECONDS.between(startTime, LocalTime.now());
        System.out.println("seconds of work = " + secondsOfWork);
    }

    private static void findMaxAmount() {
        for (long[] x : mainFilteredAndSortedList) {
            if (maxAmountElementsOfLine <= x.length)
                maxAmountElementsOfLine = x.length;
        }
    }

    // TODO: 15.02.2023 методы
    public static Set<long[]> readFile(String pathToFile, Pattern pattern) {
        List<long[]> list = null;
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
            mainFilteredAndSortedList.addAll(list);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return new LinkedHashSet<>(mainFilteredAndSortedList);
    }

    private static void findMatches(Set<long[]> copiesOfLines, int countOfDuplicate) {
        Set<Set<long[]>> columns = new LinkedHashSet<>(takeColumns(copiesOfLines));
        Set<long[]> set = findOneMatchOnStream(columns, countOfDuplicate);
        if (!set.isEmpty()) {
            countOfDuplicate++;
            findMatches(set, countOfDuplicate);
        }
    }

    private static Set<Set<long[]>> takeColumns(Set<long[]> copiesOfLines) {
        int size = 1;
        Set<Set<long[]>> setOfColumns = new LinkedHashSet<>();
        while (maxAmountElementsOfLine >= size) {

            // взять столбец long и сделать массив
            int finalSize = size;
            long[] thumb = mainFilteredAndSortedList.stream()
                    .flatMapToLong(s -> LongStream.of(Arrays.stream(s)
                            .skip(finalSize - 1)
                            .findFirst()
                            .orElse(0)))
                    .toArray();
            // получил отдельный i-столбец

            // найти совпадающие значения в столбце
            Set<Long> clear = new HashSet<>();
            Set<Long> copies = new HashSet<>();
            for (int i = 0; i < thumb.length; i++) {
                if (thumb[i] != 0 && !clear.add(thumb[i])) {
                    setOfCopies.add(thumb[i]);
                    copies.add(thumb[i]);
                }
            }
            // получил set повторяющихся значений в столбце

            // найти из предоставленной карты строки с повторяющимися значениями в i-столбце
            Set<long[]> set = mainFilteredAndSortedList.stream()
                    .filter(s -> Arrays.stream(s)
                            .skip(finalSize - 1)
                            .limit(1)
                            .anyMatch(copies::contains))
                    .collect(Collectors.toSet());
            // собран set из строк, где есть совпадения дубликата в i-столбце

            Map<Long, Set<long[]>> prepareToResult = mainFilteredAndSortedList.stream()
                    .filter(s -> Arrays.stream(s)
                            .skip(finalSize - 1)
                            .limit(1)
                            .anyMatch(copies::contains))
                    .collect(Collectors.groupingBy(
                            s -> Arrays.stream(s)
                                    .skip(finalSize - 1)
                                    .limit(1)
                                    .findFirst().orElse(0L),
                            Collectors.toSet()
                    ));
            // собрана мапа : повтораящийся long -> строки с этим Long[] i-столбца

            // добавить строки из предыдущего set
            copySetOfMainWithOneMatch.addAll(set);

            // вычисть главную карту из предыдущего set
            mainFilteredAndSortedList.removeAll(set);
            System.out.println(set + " i-set");

            // добавить мапу в результирующий список
            result.add(prepareToResult);
            setOfColumns.add(set);

            size++;
        }
        return setOfColumns;
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
        return nextSetLevel;
    }

    public static void print() throws IOException {

        // create file/check exists
        String newFileName = path.replace(".txt", "-result.txt");
        Path outputPath = Path.of(newFileName);
        if (!Files.exists(outputPath)) {
            Files.createFile(outputPath);
        }

        // write in file
        try (FileOutputStream fos = new FileOutputStream(outputPath.toFile());
                PrintStream out = new PrintStream(fos)) {
            out.println(takeQuantityOfGroups());
            for (Map<Long, Set<long[]>> x : result) {
                for (Map.Entry<Long, Set<long[]>> longs : x.entrySet()) {
                    out.println(String.format("группа %d", longs.getKey()));
                    // печать набор строк из группы
                    for (long[] line : longs.getValue()) {
                        out.println(Arrays.toString(line));
                    }
                }
            }
        }
    }

    private static int takeQuantityOfGroups() {
        int quantityOfGroups = 0;
        for (Map<Long, Set<long[]>> x : result) {
            quantityOfGroups += x.keySet().size();
        }
        return quantityOfGroups;
    }
}
