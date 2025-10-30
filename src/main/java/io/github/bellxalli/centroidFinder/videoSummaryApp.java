package io.github.bellxalli.centroidFinder;

//imports
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class videoSummaryApp {
    public static void main(String[] args)
    {
        File input = new File("ballsMoving.mp4");
        File output = new File("output_bw.mp4");

        //use video processor 
    }
}
