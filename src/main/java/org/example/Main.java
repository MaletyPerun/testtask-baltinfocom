package org.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    static volatile boolean check = true;

    static Map<Integer, Set<String>> result_id_set = new HashMap<>();

    public static void main(String[] args) {
        final String pathToFile = "/Users/teplo/Desktop/lng-4.txt";
        final String regex = "^(\"\\d*\")(;\"\\d*\")*$";
        final Pattern pattern = Pattern.compile(regex);


//        String test1 = "\"79748671113\"" ;
//        String test = "\"\";\"79076513686\";\"79499289445\";\"79895211259\";\"79970144607\";\"79460148141\";\"79124811542\";\"79660572200\";\"79245307223\";\"79220239511\"";
//        String test1 = "\"\";\"\";\"\";\"79587024342\";\"79373191100\";\"79297325003\";\"\";\"79082510931\";\"79407103756\";\"79218299774\";\"79297376383\"";
//        String test2 = "\"79748671113\";\"\";\"79448126993\"";
//        String test3 = "\"79675\"340418\";\"79874543026\";\"79020399615\";\"79223059005\";\"79895375164\";\"79964151960\";\"79121625654\";\"79168487484\";\"79177456020\"";
//        String test4 = "\"79578025704\";\"79290456698\";\"79822872594\"";
//        String test5 = "\"79282658856\";\"\";\"79723702292\";\"79815334258\";\"79667357621\";\"79547897526\";\"79569882751\"";
//        String test6 = "\"79899216253\";\"793218\"85\"889\";\"79823519313\";\"79187368421\";\"79967298407\";\"79495156330\";\"79806628428\";\"79751719169\"";
//        String test7 = "\"79846271511\";\"\";\"79905817365\";\"\";\"79385535785\";\"79975516840\";\"79409433153\";\"79340443814\";\"79579748263\"";
//        String test8 = "\"79055094466\";\"\"\"\";\"79139716599\";\"79089203411\";\"79286102508\"";
//        String test9 = "\"79338123053\";\"79819414725\";\"\";\"79777807599\";\"79468370712\";\"79721107967\";\"\"";
//        List<String> testList = List.of(test, test1, test2, test3, test4, test5, test6, test7, test8, test9);

        List<String> filteredList = new ArrayList<>();
//        // auto-close the resources
        Runnable task = new Runnable() {
            int countTime = 1;
            public void run() {
                while (check) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(countTime++);
                }
            }
        };

        LocalTime startTime = LocalTime.now();

        Thread myThread = new Thread(task);
        myThread.start();

        System.out.println("start to filtered");
        try (Stream lines = Files.lines(Paths.get(pathToFile))) {
            // does not preserve order
//            lines.limit(10).forEach(System.out::println);
            filteredList = (List<String>) lines.filter(s -> {
                Matcher matcher = pattern.matcher((CharSequence) s);
                return matcher.matches();
            }).collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("end to filter");

        System.out.println("start to find matches");
        findMatches(filteredList);
        System.out.println("end to find matches");

        print();


//        testList.stream().parallel().

//        try (Stream lines = Files.lines(Paths.get(pathToFile))) {
//            // does not preserve order
//            sourceList = (List<String>) lines.filter(s -> {
//                Matcher matcher = pattern.matcher((CharSequence) s);
//                return matcher.matches();
//            }).collect(Collectors.toList());
//
//            lines.count();
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }

//        System.out.println(filteredList.get(0));
//        System.out.println(filteredList.get(1));
//        System.out.println(filteredList);

//        for (int i = 0; i < 10; i++) {
//            System.out.println(filteredList.get(i));
////            System.out.println("***********");
//        }
        System.out.println(filteredList.size());
////        System.out.println(filteredList);
//        myThread.interrupt();
        check = false;
        long secondsOfWork = ChronoUnit.SECONDS.between(startTime, LocalTime.now());
        System.out.println(secondsOfWork);

        System.out.println("Hello world!");
    }

    public static void findMatches(List<String> list) {
        System.out.println("size of list = " + list.size());
        for (int j = 0; j < list.size(); j++) {
            System.out.println("get main str with id =" + j);
            for (int i = 1; i < list.size() - j; i++) {
                System.out.println("get compare str with id =" + i);
                int matches = doEquals(list.get(j), list.get(j + i));
                if (matches != 0) {
                    Set<String> set;
                    if (result_id_set.containsKey(matches)) {
                        set = new HashSet<>(result_id_set.get(matches));
                    } else {
                        set = new HashSet<>();
                    }
                    set.add(list.get(j));
                    set.add(list.get(j + i));
                    result_id_set.put(matches, set);
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
        List<Integer> keys = new ArrayList<>(result_id_set.keySet());
        for (int i = keys.size() - 1; i >= 0; i--) {
            System.out.println("Группа " + (4 - i));
            Set<String> set = new HashSet<>(result_id_set.get(keys.get(i)));
            for (String x : set) {
                System.out.println("Строка " + x);
            }
        }
    }
}
