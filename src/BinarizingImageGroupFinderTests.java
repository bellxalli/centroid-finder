
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class BinarizingImageGroupFinderTests {

    // Mock ImageBinarizer that returns a predetermined binary array
    static class MockBinarizer implements ImageBinarizer {
        int[][] toReturn;

        MockBinarizer(int[][] toReturn) { this.toReturn = toReturn; }

        @Override
        public int[][] toBinaryArray(BufferedImage image) { return toReturn; }

        @Override
        public BufferedImage toBufferedImage(int[][] image) { return null; }
    }

    // Mock BinaryGroupFinder that returns predetermined groups
    static class MockGroupFinder implements BinaryGroupFinder {
        List<Group> groupsToReturn;

        MockGroupFinder(List<Group> groupsToReturn) { this.groupsToReturn = groupsToReturn; }

        @Override
        public List<Group> findConnectedGroups(int[][] image) { return groupsToReturn; }
    }

    // Fake BinaryGroupFinder: counts 1s as groups of size 1
    static class FakeGroupFinder implements BinaryGroupFinder {
        @Override
        public List<Group> findConnectedGroups(int[][] image) {
            List<Group> result = new ArrayList<>();
            for (int y = 0; y < image.length; y++) {
                for (int x = 0; x < image[y].length; x++) {
                    if (image[y][x] == 1) {
                        result.add(new Group(1, new Coordinate(x, y)));
                    }
                }
            }
            return result;
        }
    }

    @Test
    public void testEmptyImageReturnsEmptyList() {
        int[][] emptyArray = new int[0][0];
        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(
            new MockBinarizer(emptyArray),
            new MockGroupFinder(new ArrayList<>())
        );

        List<Group> result = finder.findConnectedGroups(new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB));
        assertTrue(result.isEmpty(), "Expected empty list for empty image");
    }

    @Test
    public void testMockBinarizerAndGroupFinder() {
        int[][] binArray = {{1, 0}, {0, 1}};
        List<Group> mockGroups = new ArrayList<>();
        mockGroups.add(new Group(1, new Coordinate(0, 0)));
        mockGroups.add(new Group(1, new Coordinate(1, 1)));

        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(
            new MockBinarizer(binArray),
            new MockGroupFinder(mockGroups)
        );

        List<Group> result = finder.findConnectedGroups(new BufferedImage(2,2,BufferedImage.TYPE_INT_RGB));
        assertEquals(2, result.size());
        assertEquals(mockGroups, result, "Expected the mock groups to be returned");
    }

    @Test
    public void testFakeGroupFinderCountsOnes() {
        int[][] binArray = {
            {1, 0, 1},
            {0, 1, 0},
            {1, 0, 0}
        };

        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(
            new MockBinarizer(binArray),
            new FakeGroupFinder()
        );

        List<Group> result = finder.findConnectedGroups(new BufferedImage(3,3,BufferedImage.TYPE_INT_RGB));
        assertEquals(5, result.size(), "Expected 5 groups of size 1 (each 1 in array)");
    }
}