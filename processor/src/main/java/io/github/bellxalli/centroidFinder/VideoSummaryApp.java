package io.github.bellxalli.centroidFinder;

import java.io.File;

/**
 * Command-line application for processing a video and generating a CSV summary
 * containing centroid positions of detected color-based groups in each frame.
 *
 * The program expects four command-line arguments:
 *  InputPath  – path to the input video file
 *  OutputCsv  – destination CSV file
 *  TargetColor – color to track (formats: #RRGGBB, 0xRRGGBB, or RRGGBB)
 *  Threshold   – integer distance threshold used during binarization
 *
 * The application validates input values, initializes the processing components,
 * and uses VideoProcessor to analyze the video and generate the CSV output.
 * 
 * Authors: Xalli Bell and Emily Menken
 * 2025
 */
public class VideoSummaryApp {
    /**
     * Entry point for the video summary application.
     *
     * Steps performed by this method:
     *   1. Validates that four command-line arguments were provided.
     *   2. Validates and parses the target color and threshold values.
     *   3. Validates that the input video file exists.
     *   4. Initializes the color distance finder, binarizer, and group finder.
     *   5. Processes the video using VideoProcessor.
     *   6. Prints a summary of the completed operation.
     *
     * If invalid arguments are supplied, usage instructions are printed and
     * the program exits without further processing.
     *
     * Command-line arguments:
     *   args[0] – input video file path
     *   args[1] – output CSV file path
     *   args[2] – target color string
     *   args[3] – threshold value
     */
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

        //vlaidating and getting targetColor and threshold
        String colorArg = args[2].trim();
        String thresholdArg = args[3].trim();
        ValidateVideo validateNumbers = new ValidateVideo();
        int[] validIntInputs = validateNumbers.validateColorAndThreshold(colorArg, thresholdArg);
        int targetColor = validIntInputs[0];
        int threshold = validIntInputs[1];

        //validating and getting file
        ValidateFileInput validInput = new ValidateFileInput();
        input = validInput.fileValidate(input);       

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
