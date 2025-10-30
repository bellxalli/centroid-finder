package io.github.bellxalli.centroidFinder;

//imports
import java.io.File;

public class videoSummaryApp {
    public static void main(String[] args)
    {
        //creating files to grab video and send output data to        
        File input = new File("ballsMoving.mp4");
        File output = new File("output.csv");

        //pick targetColor
        int targetColor = 1;//from commandline?;
        int threshold = 1;//also commandline?;

        //use euclidean color distance finder, binarizer, dsfgroupfinder
        ColorDistanceFinder distance = new EuclideanColorDistance();
        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(distance, targetColor, threshold);
        DfsBinaryGroupFinder groupFinder = new DfsBinaryGroupFinder();

        //use video processor
        VideoProcessor processor = new VideoProcessor(binarizer, groupFinder);

        //process video and get csv
        processor.processVideo(input, output);

        //tells me what's happened
        System.out.println("Video processed successfully! Results saved to: " + output.getAbsolutePath());
    }
}
