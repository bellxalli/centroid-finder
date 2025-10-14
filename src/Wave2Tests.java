import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for DfsBinaryGroupFinder.
 *
 * All tests follow the Arrange-Act-Assert pattern.
 * Most are real integration-style unit tests on the DFS logic,
 * except for the two clearly labeled "mock" and "fake" tests at the end.
 */
public class Wave2Tests {

    private final BinaryGroupFinder finder = new DfsBinaryGroupFinder();

    @Test
    public void testNullImageThrowsNullPointerException() {
        int[][] image = null;
        assertThrows(NullPointerException.class, () -> finder.findConnectedGroups(image));
    }

    @Test
    public void testNullRowThrowsNullPointerException() {
        int[][] image = new int[2][];
        image[0] = new int[] {0, 0};
        image[1] = null;
        assertThrows(NullPointerException.class, () -> finder.findConnectedGroups(image));
    }

    @Test
    public void testNonRectangularThrowsIllegalArgumentException() {
        int[][] image = new int[2][];
        image[0] = new int[] {1, 0, 0};
        image[1] = new int[] {0, 1}; // different length
        assertThrows(IllegalArgumentException.class, () -> finder.findConnectedGroups(image));
    }

    @Test
    public void testInvalidValueThrowsIllegalArgumentException() {
        int[][] image = {
            {1, 0},
            {0, 2} // invalid value
        };
        assertThrows(IllegalArgumentException.class, () -> finder.findConnectedGroups(image));
    }

    @Test
    public void testEmptyImageReturnsEmptyList() {
        int[][] image = new int[0][];
        List<Group> groups = finder.findConnectedGroups(image);
        assertTrue(groups.isEmpty());
    }

    @Test
    public void testSinglePixelCreatesSingleGroupWithCorrectCentroid() {
        int[][] image = {
            {0, 0, 0},
            {0, 1, 0},
            {0, 0, 0}
        };
        List<Group> groups = finder.findConnectedGroups(image);
        assertEquals(1, groups.size());
        Group g = groups.get(0);
        assertEquals(1, g.size());
        assertEquals(1, g.x());
        assertEquals(1, g.y());
    }

    @Test
    public void testConnectedPixelsFormSingleGroupAndCentroidIntegerDivision() {
        int[][] image = {
            {0,0,0,0},
            {0,1,1,0},
            {0,0,1,0},
            {0,0,0,0}
        };
        List<Group> groups = finder.findConnectedGroups(image);
        assertEquals(1, groups.size());
        Group g = groups.get(0);
        assertEquals(3, g.size());
        assertEquals(1, g.x());
        assertEquals(1, g.y());
    }

    @Test
    public void testSeparateGroupsAndSortingOrder() {
        int[][] image = new int[5][6];

        // Group A (3 pixels)
        image[0][0] = 1;
        image[0][1] = 1;
        image[1][0] = 1;

        // Group B (2 pixels)
        image[0][4] = 1;
        image[0][5] = 1;

        // Group C (3 pixels)
        image[3][2] = 1;
        image[4][2] = 1;
        image[4][3] = 1;

        List<Group> groups = finder.findConnectedGroups(image);

        assertEquals(3, groups.size());
        Group first = groups.get(0);
        Group second = groups.get(1);
        Group third = groups.get(2);

        assertEquals(3, first.size());
        assertEquals(2, first.x());
        assertEquals(3, first.y());

        assertEquals(3, second.size());
        assertEquals(0, second.x());
        assertEquals(0, second.y());

        assertEquals(2, third.size());
        assertEquals(4, third.x());
        assertEquals(0, third.y());
    }

    @Test
    public void testNoDiagonalConnection() {
        int[][] image = {
            {1, 0},
            {0, 1}
        };
        List<Group> groups = finder.findConnectedGroups(image);
        assertEquals(2, groups.size());
        Group first = groups.get(0);
        Group second = groups.get(1);
        assertEquals(1, first.size());
        assertEquals(1, second.size());
        assertEquals(1, first.x());
        assertEquals(1, first.y());
        assertEquals(0, second.x());
        assertEquals(0, second.y());
    }

    // -----------------------------------------------------
    // MOCK TEST
    // -----------------------------------------------------
    /**
     * This test manually "mocks" BinaryGroupFinder.
     * Instead of running the real DFS, we override it inline and
     * simulate what the method should return.
     */
    @Test
    public void mockTest_manualMockFinder() {
        // Arrange: create a mock BinaryGroupFinder that simulates known behavior
        BinaryGroupFinder mockFinder = new BinaryGroupFinder() {
            @Override
            public List<Group> findConnectedGroups(int[][] image) {
                // Pretend the algorithm found exactly one group of size 99 at (x=5, y=5)
                List<Group> mockResult = new ArrayList<>();
                mockResult.add(new Group(99, 5, 5));
                return mockResult;
            }
        };

        int[][] dummyImage = {{1, 1}, {0, 0}}; // not even used here

        // Act
        List<Group> result = mockFinder.findConnectedGroups(dummyImage);

        // Assert
        assertEquals(1, result.size());
        Group g = result.get(0);
        assertEquals(99, g.size());
        assertEquals(5, g.x());
        assertEquals(5, g.y());
    }

    // -----------------------------------------------------
    // FAKE TEST
    // -----------------------------------------------------
    /**
     * This test uses a simple "fake" implementation of the finder.
     * The fake simulates a smaller version of the real class that
     * returns predictable data only when given a specific image.
     */
    @Test
    public void fakeTest_simpleFakeFinder() {
        // Arrange: fake implementation
        class FakeBinaryGroupFinder implements BinaryGroupFinder {
            @Override
            public List<Group> findConnectedGroups(int[][] image) {
                // If the top-left cell is 1, return a fake group, else return empty
                if (image != null && image.length > 0 && image[0][0] == 1)
                    return List.of(new Group(1, 0, 0));
                return List.of();
            }
        }

        BinaryGroupFinder fakeFinder = new FakeBinaryGroupFinder();
        int[][] image = {{1, 0}, {0, 0}}; // triggers fake behavior

        // Act
        List<Group> result = fakeFinder.findConnectedGroups(image);

        // Assert
        assertEquals(1, result.size());
        assertEquals(new Group(1, 0, 0), result.get(0));
    }
}
