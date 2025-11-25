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

/**
 * Processes a video by extracting frames, converting them to binary images,
 * detecting connected groups, and writing centroid information to a CSV file.
 * This class coordinates the workflow between a DistanceImageBinarizer and a 
 * DfsBinaryGroupFinder.
 * For each decoded video frame, the procesor does the following:
 *  Converts the fram to a BufferedImage
 *  Generates a binary representation of the frame
 *  Finds connected groups and their centroids
 *  Writes centroid coordinates and timestamps to a CSV output file
 * If no connected group is found for a frame, the CSV will record (-1,-1) for 
 * the centroid coordinates
 * 
 * Authors: Xalli Bell and Emily Menken
 * 2025
 */
public class VideoProcessor {

    protected DistanceImageBinarizer binarizer;
    protected DfsBinaryGroupFinder groupFinder;

    public VideoProcessor(DistanceImageBinarizer binarizer, DfsBinaryGroupFinder groupFinder) {
        this.binarizer = binarizer;
        this.groupFinder = groupFinder;
    }

    /**
     * Processes the input video and writes frame-by-frame centroid data to a CSV file.
     * The method performs the following steps:
     *  Opens the input video using JCodec
     *  Reads each frame sequentially
     *  Converts the frame into a BufferedImage
     *  Binarizes it using the configured DistamceImageBinarizer
     *  Detects connected pixel groups using the DfsBinaryGroupFinder
     *  Computes the timestamp based on frame index and FPS
     *  Writes the timestamp and centroid coordinates to the CSV output file
     * 
     * Fallback FPS of 30.0 is used if metadate is missing or unreadable.
     * If no group is detected in a frame, the method writes: time, -1, -1
     * 
     * Both the input video file and output CSV file must be non-null.
     * If either is null, the method exits without processing.  
     *    
     * @param input the video file to process.
     * @param csvOutput the destination CSV file where frame data will be written.
     * @throws RuntimeException if an I/O or JCodec error occurs during processing.
     */
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