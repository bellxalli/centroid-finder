import java.util.*;
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
    // make thorough unit tests!
    // convert hex to rgb (use helper)
    // return euclidean color distance between 2 hex rgb colors
    //     color represented as 0x RR GG BB
    //     each component ranges 0 - 255
    // each color r, g, b is in 3D space 
    // Euclidean Distance formula:
    //     sqrt((r1 - r2)^2 + (g1 - g2)^2 + (b1 - b2)^2)
    // meaures how visually different two colors are



        return 0;
    }

    //helper convert 0xRRGGBB
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
