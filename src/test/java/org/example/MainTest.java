package org.example;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.example.Main.findMatches;
import static org.example.Main.pattern;
import static org.example.Main.readFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {
    static final String pathToTestFile = "src/test/resources/test-file-1.txt";
    static final String pathToFailFile = "src/test/resources/test-file-2.txt";
    static final Set<Map<Long, Set<Long[]>>> expectedSetOfDuplicatesInMaps = new LinkedHashSet<>();

    static final Map<Long, Set<Long[]>> expectedOneMapWithDuplicates = new HashMap<>();
    static final int maxAmountElementsTest = 3;
    static final Set<Long[]> expectedFailSet = Set.of(
            new Long[]{79584895463L, 79813087441L, 79279047724L, 0L, 79023442969L},
            new Long[]{79300855205L, 79361449905L, 79405798876L, 79813087441L, 79219383129L, 79647376560L},
            new Long[]{0L, 79023442969L, 79023442969L, 79813087441L, 79584895463L, 79219383129L, 79647376560L},
            new Long[]{79805535143L, 79219383129L, 79813087441L, 0L, 79647376560L});
    static final Set<Long[]> expectedSet = Set.of(
            new Long[]{79805535143L, 79813087441L, 79279047724L, 0L, 79023442969L},
            new Long[]{79300855205L, 79361449905L, 79405798876L, 79813087441L, 79219383129L, 79647376560L},
            new Long[]{0L, 79023442969L, 79023442969L, 79813087441L, 79584895463L, 79219383129L, 79647376560L},
            new Long[]{79805535143L, 79219383129L, 79813087441L, 0L, 79647376560L});
    static final Set<Long[]> expectedSetOfDuplicates = Set.of(
            new Long[]{79805535143L, 79813087441L, 79279047724L, 0L, 79023442969L},
            new Long[]{79805535143L, 79219383129L, 79813087441L, 0L, 79647376560L});

    static {
        expectedOneMapWithDuplicates.put(79805535143L, expectedSetOfDuplicates);
        expectedSetOfDuplicatesInMaps.add(expectedOneMapWithDuplicates);
    }

    @Test
    void readFileTest() {
        Set<Long[]> testSet = new LinkedHashSet<>(readFile(pathToTestFile, pattern));
        assertEquals(expectedSet.size(), testSet.size());
        assertTrue(Collections.disjoint(testSet, expectedSet));
    }

    @Test
    void notFoundFile() {
        assertTrue(readFile(pathToFailFile, pattern).isEmpty());
    }

    @Test
    void foundMatchesTest() {
        Set<Map<Long,Set<Long[]>>> testSetOfMaps = new LinkedHashSet<>(findMatches(expectedSet, maxAmountElementsTest));
        assertEquals(testSetOfMaps.size(), expectedSetOfDuplicatesInMaps.size());
        Map<Long,Set<Long[]>> mapTest = testSetOfMaps.stream().findFirst().get();
        Map<Long,Set<Long[]>> mapExpected = expectedSetOfDuplicatesInMaps.stream().findFirst().get();
        assertEquals(mapTest.keySet().size(), mapExpected.keySet().size());
        assertTrue(mapTest.keySet().containsAll(mapExpected.keySet()));
    }

    @Test
    void notFoundMatchesTest() {
        Set<Map<Long,Set<Long[]>>> testSetOfMaps = new LinkedHashSet<>(findMatches(expectedFailSet, maxAmountElementsTest));
        assertTrue(testSetOfMaps.isEmpty());
    }
}