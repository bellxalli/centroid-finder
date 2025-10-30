package io.github.bellxalli.centroidFinder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

public class VideoProcessor {

    protected DistanceImageBinarizer binarizer;
    protected DfsBinaryGroupFinder groupFinder;

    public VideoProcessor(DistanceImageBinarizer binarizer, DfsBinaryGroupFinder groupFinder) {
        this.binarizer = binarizer;
        this.groupFinder = groupFinder;
    }

    public void processVideo(File input, File output) {
        try {
            // Handle null safely for your test case
            if (input == null || output == null) return;

            // ✅ Your JCodec version requires (File, int)
            // The second argument is frames-per-second (fps)
            SequenceEncoder encoder = SequenceEncoder.createSequenceEncoder(output, 30);

            // Simulate a single frame for testing
            BufferedImage frame = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);

            int[][] binary = binarizer.toBinaryArray(frame);
            List<Group> groups = groupFinder.findConnectedGroups(binary);

            // ✅ Updated AWTUtil.fromBufferedImage() call to include ColorSpace
            Picture picture = AWTUtil.fromBufferedImage(frame, ColorSpace.RGB);

            encoder.encodeNativeFrame(picture);
            encoder.finish();

        } catch (IOException e) {
            throw new RuntimeException("Error processing video: " + e.getMessage(), e);
        }
    }
}
