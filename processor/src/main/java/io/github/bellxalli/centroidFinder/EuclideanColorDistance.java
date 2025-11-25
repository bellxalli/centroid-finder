package io.github.bellxalli.centroidFinder;

public class EuclideanColorDistance implements ColorDistanceFinder {

    /**
     * Returns the euclidean color distance between two hex RGB colors.
     * 
     * Each color is represented as a 24-bit integer in the form 0xRRGGBB, where
     * RR is the red component, GG is the green component, and BB is the blue component,
     * each ranging from 0 to 255.
     * 
     * The Euclidean color distance is calculated by treating each color as a point
     * in 3D space (red, green, blue) and applying the Euclidean distance formula:
     * 
     * sqrt((r1 - r2)^2 + (g1 - g2)^2 + (b1 - b2)^2)
     * 
     * This gives a measure of how visually different the two colors are.
     * 
     * @param colorA the first color as a 24-bit hex RGB integer
     * @param colorB the second color as a 24-bit hex RGB integer
     * @return the Euclidean distance between the two colors
     */
    @Override
    public double distance(int colorA, int colorB) {
        //get rgb values
        int[] rgb1 = hexToRGB(colorA);
        int[] rgb2 = hexToRGB(colorB);

    //break formula into little pieces
        //do subtraction and squaring
        double r = Math.pow((rgb1[0] - rgb2[0]),2);
        double g = Math.pow((rgb1[1] - rgb2[1]),2);
        double b = Math.pow((rgb1[2] - rgb2[2]),2);
        //do addistion
        double rgbSum = r + g + b;
        //square root
        double euclideanResult = Math.sqrt(rgbSum);

        return euclideanResult;
    }


    /**
    * Converts a 25-bit integer color value in 0xRRGGBB format to its individual
    * red, green, and blue components.
    * The method extracts each color channel using bitwise operations, returns them 
    * as an array of three integers in RGB order.
    * 
    * @param color a 24-bit integer representing a color in the form 0xRRGGBB
    * @return An int array of length 3 containing the RGB components ([red,green,blue])
    */
    private int[] hexToRGB(int color)
    {
        int[] rgb = new int[3];
        int red = (color & 0xFF0000) >> 16;
        int green = (color & 0x00FF00) >> 8;
        int blue = color & 0x0000FF;

        rgb[0] = red;
        rgb[1] = green;
        rgb[2] = blue;

        return rgb;
    }
}