package io.github.bellxalli.centroidFinder;

import java.io.File;


public class ValidateVideo {
    
    public static void validateInputs(String colorArg, String thresholdArg, File input){

    //getting targetColor from agrs and converting it into int from various formats

            int targetColor;
            try
            {
                if(colorArg.startsWith("#"))
                {
                    targetColor = Integer.parseInt(colorArg.substring(1), 16);
                }
                else if(colorArg.startsWith("0x") || colorArg.startsWith("0X"))
                {
                    targetColor = Integer.decode(colorArg);
                }
                else
                {
                    targetColor = Integer.parseInt(colorArg, 16);
                }
            }
            catch (NumberFormatException e)
            {
                System.out.println(e.getMessage());
                System.out.println("Invalid color format. Use #RRGGBB or 0xRRGGBB (e.g. #FF0000).");
                return;
            }

            //getting and converting threshold to int
            int threshold;
            try 
            {
                threshold = Integer.parseInt(thresholdArg);
            } 
            catch (NumberFormatException e) 
            {
                System.out.println(e.getMessage());
                System.out.println("Invalid threshold value. Must be an integer.");
                return;
            }

            //ensure input file exists
            if (!input.exists()) 
            {
                System.out.println("Input file not found: " + input.getAbsolutePath());
                return;
            }


    }



}
