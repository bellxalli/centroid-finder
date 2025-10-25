package io.github.bellxalli.centroidFinder;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.awt.AWTUtil;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ExtractFrameToPNG {
    public static void main(String[] args) throws Exception {
        File videoFile = new File("short_video.mp4");
        FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(videoFile));

        Picture picture = grab.getNativeFrame();

        if (picture != null) {
            BufferedImage image = AWTUtil.toBufferedImage(picture); // ✅ Converts JCodec Picture → AWT image
            ImageIO.write(image, "png", new File("frame.png"));     // ✅ Writes to PNG
            System.out.println("✅ Frame saved as frame.png");
        } else {
            System.out.println("⚠️ Could not extract frame.");
        }
    }
}




// import java.io.File;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.nio.ByteBuffer;

// import org.jcodec.api.FrameGrab;
// import org.jcodec.api.JCodecException;
// import org.jcodec.common.io.NIOUtils;
// import org.jcodec.common.model.Picture;

// /**
//  * Extracts a frame from a video using only core JCodec classes.
//  * Does NOT require AWT or BufferedImage.
//  */
// public class ExtractFrameSimple {

//     public static void main(String[] args) {
//         File videoFile = new File("ballsMoving.mp4"); // your video file
//         double secondToExtract = 2.0;                 // timestamp to extract

//         try {
//             // Create a FrameGrab directly from the file
//             FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(videoFile));

//             // Seek to the desired second
//             grab.seekToSecondPrecise(secondToExtract);

//             // Decode a single frame
//             Picture picture = grab.getNativeFrame();

//             if (picture == null) {
//                 System.out.println("⚠️ No frame found at " + secondToExtract + "s.");
//                 return;
//             }

//             // Convert the Picture to raw RGB bytes
//             ByteBuffer buffer = ByteBuffer.wrap(picture.getPlaneData(0));

//             // Save the raw bytes to a file (for demonstration)
//             try (FileOutputStream fos = new FileOutputStream("frame_raw.rgb")) {
//                 fos.write(buffer.array());
//             }

//             // ✅ Print out frame width and height
//             System.out.println("Frame width: " + picture.getWidth());
//             System.out.println("Frame height: " + picture.getHeight());

//             System.out.println("✅ Frame extracted at " + secondToExtract + "s (saved as raw RGB data).");

//         } catch (IOException | JCodecException e) {
//             e.printStackTrace();
//         }
//     }
// }
