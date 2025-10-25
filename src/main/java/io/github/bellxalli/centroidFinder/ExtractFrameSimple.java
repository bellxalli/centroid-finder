package io.github.bellxalli.centroidFinder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;

/**
 * Extracts a frame from a video using only core JCodec classes.
 * Does NOT require AWT or BufferedImage.
 */
public class ExtractFrameSimple {

    public static void main(String[] args) {
        File videoFile = new File("ballsMoving.mp4"); // your video file
        double secondToExtract = 2.0;                 // timestamp to extract

        try {
            // Create a FrameGrab directly from the file
            FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(videoFile));

            // Seek to the desired second
            grab.seekToSecondPrecise(secondToExtract);

            // Decode a single frame
            Picture picture = grab.getNativeFrame();

            if (picture == null) {
                System.out.println("⚠️ No frame found at " + secondToExtract + "s.");
                return;
            }

            // Convert the Picture to raw RGB bytes
            ByteBuffer buffer = ByteBuffer.wrap(picture.getPlaneData(0));

            // Save the raw bytes to a file (for demonstration)
            try (FileOutputStream fos = new FileOutputStream("frame_raw.rgb")) {
                fos.write(buffer.array());
            }

            System.out.println("✅ Frame extracted at " + secondToExtract + "s (saved as raw RGB data).");

        } catch (IOException | JCodecException e) {
            e.printStackTrace();
        }
    }
}
