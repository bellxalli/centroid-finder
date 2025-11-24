package io.github.bellxalli.centroidFinder;

/**
 * Utility class for validating and parsing color and threshold input values.
 * 
 * This class supports interpreting colors provided as strings in multiple
 * hexadecimal formats and converting them to integer representations. It 
 * also validates that a threshold argument is a valid integer.
 */
public class ValidateVideo {

    /**
     * Validates and parses a color and threshold value from string inputs.
     * 
     * The color argument may be provided in any of the following formats:
     *  #RRGGBB
     *  0xRRGGBB or 0XRRGGBB
     * RRGGBB
     * 
     * The method attempts to convert the color to an integer and the threshold
     * to a decimal integer. If either value is invalid, an error message is printed
     * and the method returns null.
     * 
     * @param colorArg a string representing a hex color in one of the supported formats.
     * @param thresholdArg a string representing an integer threshold.
     * @return an int[] of length 2 where index 0 = parsed color value and index 1 = parsed threshold 
     *         returns null if either argument is invalid 
     */
    public int[] validateColorAndThreshold(String colorArg, String thresholdArg){

        //getting targetColor from agrs and converting it into int from various formats
        int[] validColors = new int[2];
        int targetColor;
        try
        {
            if(colorArg.startsWith("#"))
            {
                targetColor = Integer.parseInt(colorArg.substring(1), 16);
                validColors[0] = targetColor;
            }
            else if(colorArg.startsWith("0x") || colorArg.startsWith("0X"))
            {
                targetColor = Integer.decode(colorArg);
                validColors[0] = targetColor;

            }
            else
            {
                targetColor = Integer.parseInt(colorArg, 16);
                validColors[0] = targetColor;

            }
        }
        catch (NumberFormatException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Invalid color format. Use #RRGGBB or 0xRRGGBB (e.g. #FF0000).");
            return null;
        }

        //getting and converting threshold to int
        int threshold;
        try 
        {
            threshold = Integer.parseInt(thresholdArg);
            validColors[1] = threshold;
        } 
        catch (NumberFormatException e) 
        {
            System.out.println(e.getMessage());
            System.out.println("Invalid threshold value. Must be an integer.");
            return null;
        }
        return validColors;
    }
}
