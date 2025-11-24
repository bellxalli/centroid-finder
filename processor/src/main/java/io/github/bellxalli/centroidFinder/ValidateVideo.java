package io.github.bellxalli.centroidFinder;

public class ValidateVideo {
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
