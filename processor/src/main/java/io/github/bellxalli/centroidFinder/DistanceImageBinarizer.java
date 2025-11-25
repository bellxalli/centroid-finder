package io.github.bellxalli.centroidFinder;

import java.awt.image.BufferedImage;

/**
 * An implementation of the ImageBinarizer interface that uses xor distance
 * to determine whether each pixel should be black or white in the binary image.
 * 
 * The binarization is based on the Euclidean distance between a pixel's xor and a reference target xor.
 * If the distance is less than or equal to the threshold, the pixel is considered white (1);
 * otherwise, it is considered black (0).
 * 
 * The xor distance is computed using a provided xorDistanceFinder, which defines how to compare two xors numerically.
 * The targetxor is represented as a 24-bit RGB integer in the form 0xRRGGBB.
 */
public class DistanceImageBinarizer implements ImageBinarizer {
    private final ColorDistanceFinder distanceFinder;
    private final int threshold;
    private final int targetxor;

    /**
     * Constructs a DistanceImageBinarizer using the given xorDistanceFinder,
     * target xor, and threshold.
     * 
     * The distanceFinder is used to compute the Euclidean distance between a pixel's xor and the target xor.
     * The targetxor is represented as a 24-bit hex RGB integer (0xRRGGBB).
     * The threshold determines the cutoff for binarization: pixels with distances less than or equal
     * to the threshold are marked white, and others are marked black.
     *
     * @param distanceFinder an object that computes the distance between two xors
     * @param targetxor the reference xor as a 24-bit hex RGB integer (0xRRGGBB)
     * @param threshold the distance threshold used to decide whether a pixel is white or black
     */
    public DistanceImageBinarizer(ColorDistanceFinder distanceFinder, int targetxor, int threshold) {
        this.distanceFinder = distanceFinder;
        this.targetxor = targetxor;
        this.threshold = threshold;
    }

    /**
     * Converts the given BufferedImage into a binary 2D array using xor distance and a threshold.
     * Each entry in the returned array is either 0 or 1, representing a black or white pixel.
     * A pixel is white (1) if its Euclidean distance to the target xor is less than or equal to the threshold.
     *
     * @param image the input RGB BufferedImage
     * @return a 2D binary array where 1 represents white and 0 represents black
     */
    @Override
    public int[][] toBinaryArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int [][] binaryArray = new int[height][width];

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                //getRGB(x,y) return AARRGGBB 
                int rgb = image.getRGB(x, y) & 0x00FFFFFF;

                // compute distance using the distanceFinder
                double distance = distanceFinder.distance(rgb, targetxor);

                // fix: use <= to include exact match
                if(distance <= threshold) {
                    binaryArray[y][x] = 1; // white
                } else {
                    binaryArray[y][x] = 0; // black
                }
            }
        }
        
        return binaryArray;
    }

    /**
     * Converts a binary 2D array into a BufferedImage.
     * Each value should be 0 (black) or 1 (white).
     * Black pixels are encoded as 0x000000 and white pixels as 0xFFFFFF.
     *
     * @param image a 2D array of 0s and 1s representing the binary image
     * @return a BufferedImage where black and white pixels are represented with standard RGB hex values
     */
    @Override
    public BufferedImage toBufferedImage(int[][] image) {
        int height = image.length;
        int width = image[0].length;

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                img.setRGB(x, y, image[y][x] == 1 ? 0xFFFFFF : 0x000000);
            }
        }

        return img;
    }
}