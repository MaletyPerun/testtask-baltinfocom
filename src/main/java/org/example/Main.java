package org.example;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@Slf4j
public class Main {

    static volatile boolean check = true;

    static Map<Integer, Set<String>> resultIdSet = new HashMap<>();

    public static void main(String[] args) {
        final String pathToFile = "/Users/teplo/Desktop/lng-4.txt";
        final String regex = "^(\"\\d*\")(;\"\\d*\")*$";
        final Pattern pattern = Pattern.compile(regex);

        List<String> filteredList = new ArrayList<>();

        Runnable task = new Runnable() {
            int countTime = 1;
            public void run() {
                while (check) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                    countTime++;
                    log.info(String.valueOf(countTime));
                }
            }
        };

        LocalTime startTime = LocalTime.now();

        Thread myThread = new Thread(task);
        myThread.start();

        log.info("start to filtered");
        try (Stream lines = Files.lines(Paths.get(pathToFile))) {
            filteredList = (List<String>) lines.filter(s -> {
                Matcher matcher = pattern.matcher((CharSequence) s);
                return matcher.matches();
            }).collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        log.info(String.valueOf(filteredList.size()));
        log.info("end to filter");

        log.info("start to find matches");
        findMatches(filteredList);
        log.info("end to find matches");

        print();

        check = false;
        long secondsOfWork = ChronoUnit.SECONDS.between(startTime, LocalTime.now());
        log.info(String.valueOf(secondsOfWork));
    }

    public static void findMatches(List<String> list) {
        log.info("size of list = " + list.size());
        for (int j = 0; j < list.size(); j++) {
            log.info("get main str with id =" + j);
            for (int i = 1; i < list.size() - j; i++) {
                log.info("get compare str with id =" + i);
                int matches = doEquals(list.get(j), list.get(j + i));
                if (matches != 0) {
                    Set<String> set;
                    if (resultIdSet.containsKey(matches)) {
                        set = new HashSet<>(resultIdSet.get(matches));
                    } else {
                        set = new HashSet<>();
                    }
                    set.add(list.get(j));
                    set.add(list.get(j + i));
                    resultIdSet.put(matches, set);
                }
            }
        }
    }

    public static int doEquals(String str1, String str2) {
        String[] arr1 = str1.split(";");
        String[] arr2 = str2.split(";");
        int length = Math.min(arr1.length, arr2.length);
        return (int) IntStream.range(0, length)
                .filter(i -> !("".equals(arr1[i])) && (arr1[i].equals(arr2[i])))
                .count();
    }

    public static void print() {
        List<Integer> keys = new ArrayList<>(resultIdSet.keySet());
        int length = keys.size();
        for (int i = length - 1; i >= 0; i--) {
            System.out.println("Группа " + (length - i));
            Set<String> set = new HashSet<>(resultIdSet.get(keys.get(i)));
            for (String x : set) {
                System.out.println("Строка " + x);
            }
        }
    }
}
