package io.github.bellxalli.centroidFinder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class ErrorAndEdgecaseTests {

    // ---------------------------
    // ValidateFileInput edge cases
    // ---------------------------
    @Test
    public void testFileDoesNotExist() {
        ValidateFileInput validator = new ValidateFileInput();
        File f = new File("nonexistentfile.mp4");
        assertNull(validator.fileValidate(f), "Should return null for non-existent file");
    }

    @Test
    public void testFileExists() throws Exception {
        File f = File.createTempFile("temp", ".txt");
        f.deleteOnExit();
        ValidateFileInput validator = new ValidateFileInput();
        assertEquals(f, validator.fileValidate(f), "Should return the same file if it exists");
    }

    // ---------------------------
    // ValidateVideo edge cases
    // ---------------------------
    @Test
    public void testInvalidColorFormat() {
        ValidateVideo validator = new ValidateVideo();
        assertNull(validator.validateColorAndThreshold("GHIJKL", "50"), "Invalid hex should return null");
    }

    @Test
    public void testInvalidThreshold() {
        ValidateVideo validator = new ValidateVideo();
        assertNull(validator.validateColorAndThreshold("0xFF0000", "abc"), "Non-integer threshold should return null");
    }

    @Test
    public void testValidInputs() {
        ValidateVideo validator = new ValidateVideo();
        int[] result = validator.validateColorAndThreshold("0xFF00FF", "10");
        assertNotNull(result);
        assertEquals(0xFF00FF, result[0]);
        assertEquals(10, result[1]);
    }

    // ---------------------------
    // DfsBinaryGroupFinder edge cases
    // ---------------------------
    @Test
    public void testEmptyImage() {
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        int[][] image = new int[0][0];
        List<Group> groups = finder.findConnectedGroups(image);
        assertTrue(groups.isEmpty(), "Empty image should return empty group list");
    }

    @Test
    public void testNullRowInImage() {
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        int[][] image = new int[2][];
        image[0] = new int[]{1, 0};
        image[1] = null;
        assertThrows(NullPointerException.class, () -> finder.findConnectedGroups(image));
    }

    @Test
    public void testNonRectangularImage() {
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        int[][] image = new int[][] {{1, 0}, {1, 0, 1}};
        assertThrows(IllegalArgumentException.class, () -> finder.findConnectedGroups(image));
    }

    @Test
    public void testInvalidPixelValue() {
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        int[][] image = new int[][] {{1, 2}};
        assertThrows(IllegalArgumentException.class, () -> finder.findConnectedGroups(image));
    }

    @Test
    public void testSinglePixelGroup() {
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        int[][] image = new int[][] {{1}};
        List<Group> groups = finder.findConnectedGroups(image);
        assertEquals(1, groups.size());
        Group g = groups.get(0);
        assertEquals(1, g.size());
        assertEquals(0, g.centroid().x());
        assertEquals(0, g.centroid().y());
    }

    // ---------------------------
    // DistanceImageBinarizer edge cases
    // ---------------------------
    @Test
    public void testAllBlackImage() {
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        // set all pixels to black (0x000000)
        DistanceImageBinarizer bin = new DistanceImageBinarizer(new EuclideanColorDistance(), 0xFFFFFF, 1);
        int[][] arr = bin.toBinaryArray(img);
        for (int y = 0; y < arr.length; y++)
            for (int x = 0; x < arr[0].length; x++)
                assertEquals(0, arr[y][x], "Pixel should be black");
    }

    @Test
    public void testBinaryConversionBackAndForth() {
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, 0xFFFFFF);
        img.setRGB(0, 1, 0x000000);
        DistanceImageBinarizer bin = new DistanceImageBinarizer(new EuclideanColorDistance(), 0xFFFFFF, 0);
        int[][] arr = bin.toBinaryArray(img);
        BufferedImage converted = bin.toBufferedImage(arr);
        assertEquals(0xFFFFFF, converted.getRGB(0, 0) & 0xFFFFFF);
        assertEquals(0x000000, converted.getRGB(0, 1) & 0xFFFFFF);
    }

    // ---------------------------
    // EuclideanColorDistance edge cases
    // ---------------------------
    @Test
    public void testSameColorDistance() {
        EuclideanColorDistance d = new EuclideanColorDistance();
        double dist = d.distance(0x123456, 0x123456);
        assertEquals(0.0, dist, 0.0001);
    }

    @Test
    public void testMaxColorDistance() {
        EuclideanColorDistance d = new EuclideanColorDistance();
        double dist = d.distance(0x000000, 0xFFFFFF);
        assertTrue(dist > 0, "Distance between black and white should be positive");
    }
}
