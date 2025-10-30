package io.github.bellxalli.centroidFinder;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import java.awt.image.BufferedImage;
import java.io.File;
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
    public void processVideo(File input, File output)
    {
        //getting frame and creating new path for manipulated frames to be written out to and by
        FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(input));
        SequenceEncoder encoder = new SequenceEncoder(NIOUtils.writableChannel(output));


    }

}
