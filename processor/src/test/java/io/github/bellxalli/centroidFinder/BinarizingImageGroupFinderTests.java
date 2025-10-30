package io.github.bellxalli.centroidFinder;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BinarizingImageGroupFinderTests {

    // -----------------------------------------------------
    // MOCK TEST
    // -----------------------------------------------------
    @Test
    public void mockTest_manualMockFinder() {
        // Arrange: inline mock finder
        BinaryGroupFinder mockFinder = new BinaryGroupFinder() {
            @Override
            public List<Group> findConnectedGroups(int[][] image) {
                List<Group> mockResult = new ArrayList<>();
                mockResult.add(new Group(99, new Coordinate(5, 5)));
                return mockResult;
            }
        };

        int[][] dummyImage = {{1, 1}, {0, 0}}; // image not actually used

        // Act
        List<Group> result = mockFinder.findConnectedGroups(dummyImage);

        // Assert
        assertEquals(1, result.size());
        Group g = result.get(0);
        assertEquals(99, g.size());
        assertEquals(5, g.centroid().x());
        assertEquals(5, g.centroid().y());
    }

    // -----------------------------------------------------
    // FAKE TEST
    // -----------------------------------------------------
    @Test
    public void fakeTest_simpleFakeFinder() {
        // Arrange: inline fake finder
        BinaryGroupFinder fakeFinder = new BinaryGroupFinder() {
            @Override
            public List<Group> findConnectedGroups(int[][] image) {
                if (image != null && image.length > 0 && image[0].length > 0 && image[0][0] == 1)
                    return List.of(new Group(1, new Coordinate(0, 0)));
                return List.of();
            }
        };

        int[][] image = {{1, 0}, {0, 0}}; // triggers fake behavior

        // Act
        List<Group> result = fakeFinder.findConnectedGroups(image);

        // Assert
        assertEquals(1, result.size());
        assertEquals(new Group(1, new Coordinate(0, 0)), result.get(0));
    }

    // -----------------------------------------------------
    // TEST: Empty image returns empty list
    // -----------------------------------------------------
    @Test
    public void testEmptyImageReturnsEmptyList() {
        // Arrange: inline mock binarizer and finder
        ImageBinarizer mockBinarizer = new ImageBinarizer() {
            @Override
            public int[][] toBinaryArray(BufferedImage image) {
                return new int[0][0]; // empty array
            }
            @Override
            public BufferedImage toBufferedImage(int[][] image) { return null; }
        };

        BinaryGroupFinder mockFinder = new BinaryGroupFinder() {
            @Override
            public List<Group> findConnectedGroups(int[][] image) {
                return new ArrayList<>();
            }
        };

        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(mockBinarizer, mockFinder);

        // Act
        List<Group> result = finder.findConnectedGroups(new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB));

        // Assert
        assertTrue(result.isEmpty(), "Expected empty list for empty image");
    }

    // -----------------------------------------------------
    // TEST: MockBinarizer + MockGroupFinder
    // -----------------------------------------------------
    @Test
    public void testMockBinarizerAndGroupFinder() {
        // Arrange: inline mock binarizer
        ImageBinarizer mockBinarizer = new ImageBinarizer() {
            @Override
            public int[][] toBinaryArray(BufferedImage image) {
                return new int[][] {{1, 0}, {0, 1}};
            }
            @Override
            public BufferedImage toBufferedImage(int[][] image) { return null; }
        };

        BinaryGroupFinder mockGroupFinder = new BinaryGroupFinder() {
            @Override
            public List<Group> findConnectedGroups(int[][] image) {
                List<Group> groups = new ArrayList<>();
                groups.add(new Group(1, new Coordinate(0, 0)));
                groups.add(new Group(1, new Coordinate(1, 1)));
                return groups;
            }
        };

        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(mockBinarizer, mockGroupFinder);

        // Act
        List<Group> result = finder.findConnectedGroups(new BufferedImage(2,2,BufferedImage.TYPE_INT_RGB));

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(new Group(1, new Coordinate(0, 0))));
        assertTrue(result.contains(new Group(1, new Coordinate(1, 1))));
    }

    // -----------------------------------------------------
    // TEST: FakeGroupFinder
    // -----------------------------------------------------
    @Test
    public void testFakeGroupFinderCountsOnes() {
        int[][] binArray = {
            {1, 0, 1},
            {0, 1, 0},
            {1, 0, 0}
        };

        // Arrange: inline fake finder
        BinaryGroupFinder fakeFinder = new BinaryGroupFinder() {
            @Override
            public List<Group> findConnectedGroups(int[][] image) {
                List<Group> result = new ArrayList<>();
                if (image == null) return result;
                for (int y = 0; y < image.length; y++) {
                    if (image[y] == null) continue;
                    for (int x = 0; x < image[y].length; x++) {
                        if (image[y][x] == 1)
                            result.add(new Group(1, new Coordinate(x, y)));
                    }
                }
                return result;
            }
        };

        // Arrange: inline binarizer
        ImageBinarizer mockBinarizer = new ImageBinarizer() {
            @Override
            public int[][] toBinaryArray(BufferedImage image) { return binArray; }
            @Override
            public BufferedImage toBufferedImage(int[][] image) { return null; }
        };

        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(mockBinarizer, fakeFinder);

        // Act
        List<Group> result = finder.findConnectedGroups(new BufferedImage(3,3,BufferedImage.TYPE_INT_RGB));

        // Assert
        assertEquals(4, result.size(), "Expected 4 groups of size 1 (each 1 in array)");
    }
}
