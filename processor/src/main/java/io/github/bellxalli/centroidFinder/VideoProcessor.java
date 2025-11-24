package io.github.bellxalli.centroidFinder;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

public class VideoProcessor {

    protected DistanceImageBinarizer binarizer;
    protected DfsBinaryGroupFinder groupFinder;

    public VideoProcessor(DistanceImageBinarizer binarizer, DfsBinaryGroupFinder groupFinder) {
        this.binarizer = binarizer;
        this.groupFinder = groupFinder;
    }

    public void processVideo(File input, File csvOutput) {
        // initializing to null for later use
        SeekableByteChannel channel = null;
        BufferedWriter writer = null;

        try 
        {
            // handle null safely for test case
            if (input == null || csvOutput == null) 
                return;
            
            // open the video file for reading
            channel = NIOUtils.readableChannel(input);
            FrameGrab grab = FrameGrab.createFrameGrab(channel);

            // create the CSV file for writing output
            writer = new BufferedWriter(new FileWriter(csvOutput));
            writer.write("Frame Time, x, y\n"); // write header of CSV
            int frameIndex = 0;

            // calculate frames per second (fps) safely
            double fps = 30.0; // default fallback if metadata is missing
            try 
            {
                var meta = grab.getVideoTrack().getMeta();
                if (meta.getTotalDuration() > 0 && meta.getTotalFrames() > 0) 
                    fps = meta.getTotalFrames() / meta.getTotalDuration(); // get timestamp for frames
    
            } 
            catch (Exception e) 
            {
                System.out.println("Warning: Could not determine FPS, using default 30.");
            }

            Picture picture;
            while ((picture = grab.getNativeFrame()) != null)
            {
                // convert to BufferedImage
                BufferedImage frame = AWTUtil.toBufferedImage(picture);

                // convert to binary
                int[][] binary = binarizer.toBinaryArray(frame);

                // find groups and centroids
                List<Group> groups = groupFinder.findConnectedGroups(binary);

                // calculate time for given frame
                double timeInSeconds = frameIndex / fps;

                // adding data from frame to CSV regardless if centroid found or not
                if (!groups.isEmpty())
                {
                    Group largest = groups.get(0);
                    int x = largest.centroid().x();
                    int y = largest.centroid().y();

                    writer.write(String.format("%.3f,%d,%d\n", timeInSeconds, x, y));
                }
                else
                {
                    writer.write(String.format("%.3f,-1,-1\n", timeInSeconds)); // no groups were found
                }

                frameIndex++;
            }

        }
        catch (IOException | JCodecException e)
        {
            throw new RuntimeException("Error processing video: " + e.getMessage(), e);
        }
        finally
        {
            try 
            {
                if (writer != null) writer.close();
                if (channel != null) channel.close(); // close the underlying channel instead of grab
            } 
            catch (IOException ignored) {}
        }
    }
}