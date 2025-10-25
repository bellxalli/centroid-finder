package io.github.bellxalli.centroidFinder;

import org.jcodec.api.FrameGrab;
import org.jcodec.scale.AWTUtil;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ExtractFrameToPNG {
    public static void main(String[] args) throws Exception {
        File videoFile = new File("ballsMoving.mp4");
        FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(videoFile));
        grab.seekToSecondPrecise(2); // ⏱️ jump to 2s mark

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

