package org.example;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;


public class Main {

    // кол-во после фильтра и сортировки: 998578 (windows X, lng)
    // кол-во после фильтра и/или сортировки: 998578 (MacOS, lng-4) 13/8 сек

    static volatile boolean check = true;
    static List<long[]> mainFilteredAndSortedList = new LinkedList<>();
    static Set<long[]> copySetOfMainWithOneMatch = new LinkedHashSet<>();
    static Set<Long> setOfCopies = new LinkedHashSet<>();
    static Set<Map<Long, Set<long[]>>> result = new LinkedHashSet<>();
    static int maxAmountElementsOfLine = 0;
    static String pathToFile = "";

    public static void main(String[] args) {
        pathToFile = args[0];
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


        System.out.println("начало чтения файла");
        readFile(pathToFile, pattern);
        if (!mainFilteredAndSortedList.isEmpty()) {
            findMaxAmount();
            findMatches();
            print();
        } else {
            System.out.println("файл пуст или неправильный формат данных");
        }

        check = false;
        long secondsOfWork = ChronoUnit.SECONDS.between(startTime, LocalTime.now());
        System.out.printf("время работы: %d сек \n", secondsOfWork);
    }

    public static void readFile(String pathToFile, Pattern pattern) {
        List<long[]> list = null;
        try (Stream<String> lines = Files.lines(Paths.get(pathToFile))) {
            list = lines.filter(s -> {
                        Matcher matcher = pattern.matcher(s);
                        return matcher.matches();
                    })
                    .filter(s -> (s.length() != 0))
                    .distinct()
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

        try {
            mainFilteredAndSortedList.addAll(list);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void findMaxAmount() {
        for (long[] x : mainFilteredAndSortedList) {
            if (maxAmountElementsOfLine <= x.length)
                maxAmountElementsOfLine = x.length;
        }
    }

    private static void findMatches() {
        int size = 1;
        while (maxAmountElementsOfLine >= size) {

            int finalSize = size;
            long[] thumb = mainFilteredAndSortedList.stream()
                    .flatMapToLong(s -> LongStream.of(Arrays.stream(s)
                            .skip(finalSize - 1)
                            .findFirst()
                            .orElse(0)))
                    .toArray();

            Set<Long> clear = new HashSet<>();
            Set<Long> copies = new HashSet<>();
            for (int i = 0; i < thumb.length; i++) {
                if (thumb[i] != 0 && !clear.add(thumb[i])) {
                    setOfCopies.add(thumb[i]);
                    copies.add(thumb[i]);
                }
            }

            Set<long[]> set = mainFilteredAndSortedList.stream()
                    .filter(s -> Arrays.stream(s)
                            .skip(finalSize - 1)
                            .limit(1)
                            .anyMatch(copies::contains))
                    .collect(Collectors.toSet());

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

            copySetOfMainWithOneMatch.addAll(set);
            mainFilteredAndSortedList.removeAll(set);
            result.add(prepareToResult);

            size++;
        }
    }

    public static void print() {

        String newFileName = pathToFile.replace(".txt", "-result.txt");
        Path outputPath = Path.of(newFileName);
        if (!Files.exists(outputPath)) {
            try {
                Files.createFile(outputPath);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("ошибка создания файла");
            }
        }

        try (FileOutputStream fos = new FileOutputStream(outputPath.toFile());
                PrintStream out = new PrintStream(fos)) {
            out.println(takeQuantityOfGroups());
            for (Map<Long, Set<long[]>> x : result) {
                for (Map.Entry<Long, Set<long[]>> longs : x.entrySet()) {
                    out.println(String.format("группа %d", longs.getKey()));
                    for (long[] line : longs.getValue()) {
                        out.println(Arrays.toString(line));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.out.println("файл не найден");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("ошибка записи в файл");
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
