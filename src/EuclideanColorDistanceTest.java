import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EuclideanColorDistanceTest {

    private final EuclideanColorDistance distanceFinder = new EuclideanColorDistance();

    @Test
    void testSameColorHasZeroDistance() {
        int color = 0x112233; // (17, 34, 51)
        // sqrt((17-17)^2 + (34-34)^2 + (51-51)^2) = sqrt(0) = 0
        double result = distanceFinder.distance(color, color);
        assertEquals(0.0, result, 0.0001, "Distance should be zero for identical colors");
    }
    @Test
    void testRedDifferenceOnly() {
        int colorA = 0xFF0000; // (255,0,0)
        int colorB = 0x000000; // (0,0,0)
        // sqrt((255-0)^2 + (0-0)^2 + (0-0)^2) = 255
        double expected = 255.0;
        double result = distanceFinder.distance(colorA, colorB);
        assertEquals(expected, result, 0.0001);
    }

    @Test
    void testGreenDifferenceOnly() {
        int colorA = 0x00FF00; // (0,255,0)
        int colorB = 0x000000; // (0,0,0)
        // sqrt((0-0)^2 + (255-0)^2 + (0-0)^2) = 255
        double expected = 255.0;
        double result = distanceFinder.distance(colorA, colorB);
        assertEquals(expected, result, 0.0001);
    }

    @Test
    void testBlueDifferenceOnly() {
        int colorA = 0x0000FF; // (0,0,255)
        int colorB = 0x000000; // (0,0,0)
        // sqrt((0-0)^2 + (0-0)^2 + (255-0)^2) = 255
        double expected = 255.0;
        double result = distanceFinder.distance(colorA, colorB);
        assertEquals(expected, result, 0.0001);
    }

    @Test
    void testBlackAndWhite() {
        int black = 0x000000; // (0, 0, 0)
        int white = 0xFFFFFF; // (255, 255, 255)
        // sqrt((255)^2 + (255)^2 + (255)^2) = sqrt(195075) ≈ 441.673
        double expected = Math.sqrt(3 * Math.pow(255, 2));
        double result = distanceFinder.distance(black, white);
        assertEquals(expected, result, 0.0001);
    }

    @Test
    void testRedAndGreen() {
        int red = 0xFF0000;   // (255, 0, 0)
        int green = 0x00FF00; // (0, 255, 0)
        // sqrt((255-0)^2 + (0-255)^2 + (0-0)^2) = sqrt(65025 + 65025 + 0) = sqrt(130050) ≈ 360.624
        double expected = Math.sqrt(Math.pow(255, 2) + Math.pow(255, 2));
        double result = distanceFinder.distance(red, green);
        assertEquals(expected, result, 0.0001);
    }

    @Test
    void testRedAndBlue() {
        int red = 0xFF0000;  // (255, 0, 0)
        int blue = 0x0000FF; // (0, 0, 255)
        // sqrt((255-0)^2 + (0-0)^2 + (0-255)^2) = sqrt(65025 + 0 + 65025) = sqrt(130050) ≈ 360.624
        double expected = Math.sqrt(Math.pow(255, 2) + Math.pow(255, 2));
        double result = distanceFinder.distance(red, blue);
        assertEquals(expected, result, 0.0001);
    }

    @Test
    void testGreenAndBlue() {
        int green = 0x00FF00; // (0, 255, 0)
        int blue = 0x0000FF;  // (0, 0, 255)
        // sqrt((0-0)^2 + (255-0)^2 + (0-255)^2) = sqrt(0 + 65025 + 65025) = sqrt(130050) ≈ 360.624
        double expected = Math.sqrt(Math.pow(255, 2) + Math.pow(255, 2));
        double result = distanceFinder.distance(green, blue);
        assertEquals(expected, result, 0.0001);
    }

    @Test
    void testSmallDifference() {
        int colorA = 0x112233; // (17, 34, 51)
        int colorB = 0x112234; // (17, 34, 52)
        // sqrt((17-17)^2 + (34-34)^2 + (51-52)^2) = sqrt(0 + 0 + 1) = sqrt(1) = 1
        double expected = 1.0;
        double result = distanceFinder.distance(colorA, colorB);
        assertEquals(expected, result, 0.0001);
    }

    @Test
    void testMidRangeColors() {
        int colorA = 0x7F7F7F; // (127, 127, 127)
        int colorB = 0x808080; // (128, 128, 128)
        // sqrt((127-128)^2 + (127-128)^2 + (127-128)^2) = sqrt(1 + 1 + 1) = sqrt(3) ≈ 1.732
        double expected = Math.sqrt(3);
        double result = distanceFinder.distance(colorA, colorB);
        assertEquals(expected, result, 0.0001);
    }

    @Test
    void testBoundaryValues() {
        int colorA = 0xFEFEFE; // (254,254,254)
        int colorB = 0xFFFFFF; // (255,255,255)
        // sqrt((254-255)^2 + (254-255)^2 + (254-255)^2) = sqrt(3) ≈ 1.732
        double expected = Math.sqrt(3);
        double result = distanceFinder.distance(colorA, colorB);
        assertEquals(expected, result, 0.0001);
    }


    @Test
    void testComponentExtractionIndirectly() {
        int colorA = 0xFF0000; // (255, 0, 0)
        int colorB = 0x000000; // (0, 0, 0)
        // sqrt((255-0)^2 + (0-0)^2 + (0-0)^2) = sqrt(65025) = 255
        double expected = 255.0;
        double result = distanceFinder.distance(colorA, colorB);
        assertEquals(expected, result, 0.0001);
    }

    @Test
    void testSymmetryProperty() {
        int colorA = 0x123456;
        int colorB = 0x654321;
        double result1 = distanceFinder.distance(colorA, colorB);
        double result2 = distanceFinder.distance(colorB, colorA);
        assertEquals(result1, result2, 0.0001, "Distance should be symmetric");
    }

    @Test
    void testRandomColors() {
        int colorA = 0x1A2B3C; // (26, 43, 60)
        int colorB = 0x4D5E6F; // (77, 94, 111)
        // sqrt((26-77)^2 + (43-94)^2 + (60-111)^2)
        // sqrt(51^2 + 51^2 + 51^2) = sqrt(7803) ≈ 88.339
        double expected = Math.sqrt(Math.pow(51,2) + Math.pow(51,2) + Math.pow(51,2));
        double result = distanceFinder.distance(colorA, colorB);
        assertEquals(expected, result, 0.0001);
    }

    @Test
    void testMonotonicity() {
        int colorNear = 0x111111;
        int colorFar = 0xFFFFFF;
        int base = 0x000000;
        double nearDistance = distanceFinder.distance(base, colorNear);
        double farDistance = distanceFinder.distance(base, colorFar);
        assertTrue(nearDistance < farDistance, "Closer color should have smaller distance");
    }


}
