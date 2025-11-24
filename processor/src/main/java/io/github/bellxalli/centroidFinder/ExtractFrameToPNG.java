package io.github.bellxalli.centroidFinder;

import org.jcodec.api.FrameGrab;
import org.jcodec.scale.AWTUtil;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utility class for extracting a single video frame and saving it as a PNG image.
 * It uses the JCodec library to:
 *  Open a video file
 *  Seek to a specific timestamp
 *  Decode a single frame
 *  Convert it inot a BufferedImage
 *  Write the image to disk as a PNG
 * The extracted frame is saved as frame.png in the working directory.
 */
public class ExtractFrameToPNG {
    public static void main(String[] args) throws Exception {
        File videoFile = new File("ballsMoving.mp4");
        FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(videoFile));
        grab.seekToSecondPrecise(2); // jump to 2s mark

        Picture picture = grab.getNativeFrame();

        if (picture != null) {
            BufferedImage image = AWTUtil.toBufferedImage(picture); // Converts JCodec Picture → AWT image
            ImageIO.write(image, "png", new File("frame.png"));     // Writes to PNG
            
            System.out.println("Width: " + picture.getWidth());
            System.out.println("Width: " + picture.getHeight());
            System.out.println("Frame saved as frame.png");
        } else {
            System.out.println("Could not extract frame.");
        }
    }
}

