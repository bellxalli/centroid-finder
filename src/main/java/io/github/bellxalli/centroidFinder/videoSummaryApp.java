package io.github.bellxalli.centroidFinder;

//imports
import java.io.File;

public class VideoSummaryApp {
    public static void main(String[] args)
    {
        //checking number of arguments
        if(args.length < 4)
        {
            System.out.println("Usage: java -jar videoprocessor.jar <inputPath> <outputCsv> <targetColor> <threshold>");
            System.out.println("Example: java -jar videoprocessor.jar ballsMoving.mp4 output.csv 0xFF0000 60");
            return; 
        }
        
        //creating files to grab video and send output data to from commandline inputs     
        File input = new File(args[0]);
        File output = new File(args[1]);

        //getting targetColor from agrs and converting it into int from various formats
        String colorArg = args[2].trim();
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
            System.out.println("Invalid color format. Use #RRGGBB or 0xRRGGBB (e.g. #FF0000).");
            return;
        }

        //getting and converting threshold to int
        int threshold;
        try 
        {
            threshold = Integer.parseInt(args[3]);
        } 
        catch (NumberFormatException e) 
        {
            System.out.println("Invalid threshold value. Must be an integer.");
            return;
        }

        //ensure input file exists
        if (!input.exists()) 
        {
            System.out.println("Input file not found: " + input.getAbsolutePath());
            return;
        }

        //use euclidean color distance finder, binarizer, dsfgroupfinder
        ColorDistanceFinder distance = new EuclideanColorDistance();
        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(distance, targetColor, threshold);
        DfsBinaryGroupFinder groupFinder = new DfsBinaryGroupFinder();

        //use video processor
        VideoProcessor processor = new VideoProcessor(binarizer, groupFinder);

        //process video and get csv
        processor.processVideo(input, output);

        //tells me what's happened
        System.out.println("✅ Video processed successfully!");
        System.out.println("   Input file: " + input.getName());
        System.out.println("   Output CSV: " + output.getAbsolutePath());
        System.out.println("   Target color: " + String.format("#%06X", targetColor));
        System.out.println("   Threshold: " + threshold);    
    }
}
