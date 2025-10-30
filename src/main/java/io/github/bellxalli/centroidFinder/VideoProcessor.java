package io.github.bellxalli.centroidFinder;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class VideoProcessor {
    private final DistanceImageBinarizer binarizer;
    private final DfsBinaryGroupFinder groupFinder;

    public VideoProcessor(DistanceImageBinarizer binarizer, DfsBinaryGroupFinder groupFinder) 
    {
        this.binarizer = binarizer;
        this.groupFinder = groupFinder;
    }


    //get frame from video processor that will then be manipulated 
    //using preexisting classes and their methods 
    //  imageBinarizer and DfsgroupFinder to help
    // convert to binarized img
    // then turn frames into new video
    public void processVideo(File input, File outputCsv) throws IOException, JCodecException//add exceptions to be thrown
    {
        //getting frame 
        FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(input));
        Picture frame;
        int frameIndex = 0;

        try(FileWriter writer = new FileWriter(outputCsv))
        {
            writer.write("Frame,X,Y,GroupSize\n"); //the columns

            //iterative
            while((frame = grab.getNativeFrame()) != null) //making sure frame is actually there
            {
                BufferedImage og = AWTUtil.toBufferedImage(frame); //turning frame to buffered img

                //convert to binary using binaiazer
                int [][] binaryArray = binarizer.toBinaryArray(og);

                //find groups and centroids
                List<Group> groups = groupFinder.findConnectedGroups(binaryArray);
                if(!groups.isEmpty())
                {
                    Group largest = groups.get(0);
                    int x = largest.centroid().x();
                    int y = largest.centroid().y();
                    int size = largest.size();

                    writer.write(frameIndex + "," + x + "," + y + "," + size + "\n");
                }
                else
                {
                    writer.write(frameIndex + ",-1,-1,0\n"); //no groups were found
                }

                frameIndex++; //increase frame index
            }
        }
        //tells me it's done
        System.out.println("✅ CSV written to: " + outputCsv.getAbsolutePath());

    }
}
