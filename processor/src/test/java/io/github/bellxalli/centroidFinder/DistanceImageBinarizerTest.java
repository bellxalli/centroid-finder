package io.github.bellxalli.centroidFinder;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.BufferedImage;

public class DistanceImageBinarizerTest {

    // ✅ Inner helper class that implements your ColorDistanceFinder interface
    private static class SimpleColorDistanceFinder implements ColorDistanceFinder {
        @Override
        public double distance(int colorA, int colorB) {
            int r1 = (colorA >> 16) & 0xFF;
            int g1 = (colorA >> 8) & 0xFF;
            int b1 = colorA & 0xFF;

            int r2 = (colorB >> 16) & 0xFF;
            int g2 = (colorB >> 8) & 0xFF;
            int b2 = colorB & 0xFF;

            int dr = r1 - r2;
            int dg = g1 - g2;
            int db = b1 - b2;

            return Math.sqrt(dr * dr + dg * dg + db * db);
        }
    }

    @Test
    public void testToBinaryArray_singleWhitePixel_shouldBe1() {
        ColorDistanceFinder finder = new SimpleColorDistanceFinder();
        int targetColor = 0xFFFFFF;
        int threshold = 5;

        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(finder, targetColor, threshold);

        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, 0xFFFFFF);

        int[][] result = binarizer.toBinaryArray(image);
        assertEquals(1, result[0][0], "Expected white pixel to be classified as 1");
    }

    @Test
    public void testToBinaryArray_singleBlackPixel_shouldBe0() {
        ColorDistanceFinder finder = new SimpleColorDistanceFinder();
        int targetColor = 0xFFFFFF;
        int threshold = 5;

        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(finder, targetColor, threshold);

        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, 0x000000);

        int[][] result = binarizer.toBinaryArray(image);
        assertEquals(0, result[0][0], "Expected black pixel to be classified as 0");
    }

    @Test
    public void testToBinaryArray_multiplePixels_mixedOutput() {
        ColorDistanceFinder finder = new SimpleColorDistanceFinder();
        int targetColor = 0xFFFFFF;
        int threshold = 100;

        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(finder, targetColor, threshold);

        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, 0xFFFFFF); // should be white
        image.setRGB(1, 0, 0x000000); // should be black
        image.setRGB(0, 1, 0xFFFFFF); // should be white
        image.setRGB(1, 1, 0x000000); // should be black

        int[][] result = binarizer.toBinaryArray(image);

        assertEquals(1, result[0][0]);  // pixel (0, 0) white
        assertEquals(0, result[0][1]);  // pixel (1, 0) black
        assertEquals(1, result[1][0]);  // pixel (0, 1) white
        assertEquals(0, result[1][1]);  // pixel (1, 1) black

    }

    @Test
    public void testToBufferedImage_shouldReturnCorrectRGBValues() {
        ColorDistanceFinder finder = new SimpleColorDistanceFinder();
        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(finder, 0, 0); // dummy target and threshold

        int[][] binary = {
            {1, 0},
            {0, 1}
        };

        BufferedImage image = binarizer.toBufferedImage(binary);

        assertEquals(0xFFFFFF, image.getRGB(0, 0) & 0x00FFFFFF, "Pixel [0][0] should be white");
        assertEquals(0x000000, image.getRGB(1, 0) & 0x00FFFFFF, "Pixel [0][1] should be black");
        assertEquals(0x000000, image.getRGB(0, 1) & 0x00FFFFFF, "Pixel [1][0] should be black");
        assertEquals(0xFFFFFF, image.getRGB(1, 1) & 0x00FFFFFF, "Pixel [1][1] should be white");

    }

    @Test
    public void testAlphaChannelIsIgnored() {
        ColorDistanceFinder finder = new SimpleColorDistanceFinder();
        int targetColor = 0x123456;
        int threshold = 1; // very low threshold → expect black

        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(finder, targetColor, threshold);

        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        int colorWithAlpha = 0x80FFFFFF; // alpha + white
        image.setRGB(0, 0, colorWithAlpha);

        int[][] result = binarizer.toBinaryArray(image);
        assertEquals(0, result[0][0], "Alpha should be ignored; pixel should be classified as black");
    }
}
