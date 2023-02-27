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
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;


public class Main {

    // кол-во после фильтра и сортировки: 998578 (windows X, lng)
    // кол-во после фильтра и/или сортировки: 998578 (MacOS, lng-4) 13/8 сек

    static volatile boolean check = true;
    static Set<Long[]> mainFilteredSet = new LinkedHashSet<>();
    static Set<Map<Long, Set<Long[]>>> result = new LinkedHashSet<>();
    static int maxAmountElementsOfLine = 0;
    static String pathToFile = "";
    static final String regex = "^(\"\\d*\")(;\"\\d*\")*$";
    static final Pattern pattern = Pattern.compile(regex);

    public static void main(String[] args) {
//        pathToFile = args[0];
//        pathToFile = "/Users/teplo/Desktop/lng-4.txt";
        pathToFile = "C://Users/Алексей/Desktop/lng.txt";
//        pathToFile = "C://Users/Алексей/Desktop/lng-2.txt";

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
        Set<Long[]> copyOfMainSet = readFile(pathToFile, pattern);
        if (!copyOfMainSet.isEmpty()) {
            findMaxAmount();
            Set<Map<Long, Set<Long[]>>> forTest = findMatches(copyOfMainSet, maxAmountElementsOfLine);
            print(forTest);
        } else {
            System.out.println("файл пуст или неправильный формат данных");
        }
        check = false;
        long secondsOfWork = ChronoUnit.SECONDS.between(startTime, LocalTime.now());
        System.out.printf("файл прочитан, время работы: %d сек \n", secondsOfWork);
    }

    public static Set<Long[]> readFile(String pathToFile, Pattern pattern) {
        Set<Long[]> set = null;
        try (Stream<String> lines = Files.lines(Paths.get(pathToFile))) {
            set = lines.filter(s -> {
                        Matcher matcher = pattern.matcher(s);
                        return matcher.matches();
                    })
                    .filter(s -> (s.length() > 2))
                    .distinct()
                    .map(s -> s.replace("\"\"", "0"))
                    .map(s -> s.replace("\"", ""))
                    .map(s -> Arrays.stream(s.split(";"))
                            .map(Long::valueOf)
                            .toList()
                            .toArray(Long[]::new))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("ошибка чтения файла или неверный путь");
        }

        if (set != null && !set.isEmpty()) {
            mainFilteredSet.addAll(set);
        }

        return new LinkedHashSet<>(mainFilteredSet);
    }

    private static void findMaxAmount() {
        for (Long[] x : mainFilteredSet) {
            if (maxAmountElementsOfLine <= x.length)
                maxAmountElementsOfLine = x.length;
        }
    }

    public static Set<Map<Long, Set<Long[]>>> findMatches(Set<Long[]> copyOfMainSet, int maxElements) {
        int size = 1;
        Set<Map<Long, Set<Long[]>>> setOfMatchesPerColumn = new LinkedHashSet<>();
        while (maxElements >= size) {

            int finalSize = size;
            long[] thumb = copyOfMainSet.stream()
                    .flatMapToLong(s -> LongStream.of(Arrays.stream(s)
                            .skip(finalSize - 1)
                            .findFirst()
                            .orElse(0L)))
                    .toArray();

            Set<Long> clear = new HashSet<>();
            Set<Long> copies = new HashSet<>();
            for (long l : thumb) {
                if (l != 0 && !clear.add(l)) {
                    copies.add(l);
                }
            }

            Map<Long, Set<Long[]>> mapWithMatches = copyOfMainSet.stream()
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

            if (!mapWithMatches.isEmpty()) {
                result.add(mapWithMatches);
                setOfMatchesPerColumn.add(mapWithMatches);
            }
            size++;
        }
        return setOfMatchesPerColumn;
    }

    public static void print(Set<Map<Long, Set<Long[]>>> forTest) {

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
            for (Map<Long, Set<Long[]>> x : forTest) {
                for (Map.Entry<Long, Set<Long[]>> longs : x.entrySet()) {
                    out.printf("группа %d\n", longs.getKey());
                    for (Long[] line : longs.getValue()) {
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
        for (Map<Long, Set<Long[]>> x : result) {
            quantityOfGroups += x.keySet().size();
        }
        return quantityOfGroups;
    }
}
