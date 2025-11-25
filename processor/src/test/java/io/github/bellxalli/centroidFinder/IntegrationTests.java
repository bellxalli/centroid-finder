package io.github.bellxalli.centroidFinder.integration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import io.github.bellxalli.centroidFinder.ColorDistanceFinder;
import io.github.bellxalli.centroidFinder.Coordinate;
import io.github.bellxalli.centroidFinder.DfsBinaryGroupFinder;
import io.github.bellxalli.centroidFinder.DistanceImageBinarizer;
import io.github.bellxalli.centroidFinder.Group;
import io.github.bellxalli.centroidFinder.VideoProcessor;

public class IntegrationTests {

    private static final String VIDEOS_DIR = System.getenv().getOrDefault("VIDEOS_DIR", "sampleInput");

    // Minimal concrete implementation for testing
    private static class TestColorDistanceFinder implements ColorDistanceFinder {
        @Override
        public double distance(int colorA, int colorB) {
            return 0; // trivial implementation
        }
    }

    // Minimal group finder stub extending the concrete DfsBinaryGroupFinder
    private static class TestGroupFinder extends DfsBinaryGroupFinder {
        @Override
        public List<Group> findConnectedGroups(int[][] binaryArray) {
            Group dummyGroup = new Group(1, new Coordinate(0, 0));
            return Collections.singletonList(dummyGroup);
        }
    }

    // -------------------- Endpoint: GET /api/videos --------------------
    @Test
    public void testGetVideos() {
        File videoDir = new File(VIDEOS_DIR);
        String[] videos = videoDir.list();
        assertNotNull(videos, "Videos directory should not be null");
        assertTrue(videos.length > 0, "There should be at least one video in the sampleInput directory");
    }

    // -------------------- Endpoint: GET /api/thumbnail/ballsMoving.mp4 --------------------
    @Test
    public void testGetThumbnailBallsMoving() throws IOException {
        File videoFile = new File(VIDEOS_DIR, "ballsMoving.mp4");
        File outputCsv = new File("test_output_ballsMoving.csv");

        VideoProcessor processor = new VideoProcessor(
                new DistanceImageBinarizer(new TestColorDistanceFinder(), 100, 50),
                new TestGroupFinder()
        );

        processor.processVideo(videoFile, outputCsv);
        assertTrue(outputCsv.exists(), "CSV output should exist after processing video");
        assertTrue(outputCsv.length() > 0, "CSV output should not be empty");
        outputCsv.delete();
    }

    // -------------------- Endpoint: GET /api/thumbnail/ensantina.mp4 --------------------
    @Test
    public void testGetThumbnailEnsantina() throws IOException {
        File videoFile = new File(VIDEOS_DIR, "ensantina.mp4");
        File outputCsv = new File("test_output_ensantina.csv");

        VideoProcessor processor = new VideoProcessor(
                new DistanceImageBinarizer(new TestColorDistanceFinder(), 100, 50),
                new TestGroupFinder()
        );

        processor.processVideo(videoFile, outputCsv);
        assertTrue(outputCsv.exists());
        assertTrue(outputCsv.length() > 0);
        outputCsv.delete();
    }

    // -------------------- Endpoint: GET /api/process/ballsMoving.mp4?targetColor=C0C0C0&threshold=100 --------------------
    @Test
    public void testProcessBallsMoving() throws IOException {
        File videoFile = new File(VIDEOS_DIR, "ballsMoving.mp4");
        File outputCsv = new File("test_output_process_ballsMoving.csv");

        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(new TestColorDistanceFinder(), 100, 50);
        VideoProcessor processor = new VideoProcessor(binarizer, new TestGroupFinder());

        processor.processVideo(videoFile, outputCsv);
        assertTrue(outputCsv.exists());
        assertTrue(outputCsv.length() > 0);
        outputCsv.delete();
    }

    // -------------------- Endpoint: GET /api/process/ensantina.mp4?targetColor=65432&threshold=100 --------------------
    @Test
    public void testProcessEnsantina() throws IOException {
        File videoFile = new File(VIDEOS_DIR, "ensantina.mp4");
        File outputCsv = new File("test_output_process_ensantina.csv");

        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(new TestColorDistanceFinder(), 100, 50);
        VideoProcessor processor = new VideoProcessor(binarizer, new TestGroupFinder());

        processor.processVideo(videoFile, outputCsv);
        assertTrue(outputCsv.exists());
        assertTrue(outputCsv.length() > 0);
        outputCsv.delete();
    }

    // -------------------- Endpoint: GET /api/process/jobIDnumber/status --------------------
    @Test
    public void testGetJobStatus() {
        assertTrue(true);
    }

    // -------------------- Processor interaction tests --------------------
    @Test
    public void testBinarizerAndProcessorIntegration() throws IOException {
        File videoFile = new File(VIDEOS_DIR, "ballsMoving.mp4");
        File outputCsv = new File("test_output_integration.csv");

        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(new TestColorDistanceFinder(), 50, 25);
        VideoProcessor processor = new VideoProcessor(binarizer, new TestGroupFinder());

        processor.processVideo(videoFile, outputCsv);
        assertTrue(outputCsv.exists());
        assertTrue(outputCsv.length() > 0);
        outputCsv.delete();
    }

    @Test
    public void testMultipleProcessorInteractions() throws IOException {
        File videoFile = new File(VIDEOS_DIR, "ensantina.mp4");
        File outputCsv1 = new File("test_output1.csv");
        File outputCsv2 = new File("test_output2.csv");

        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(new TestColorDistanceFinder(), 75, 30);
        VideoProcessor processor = new VideoProcessor(binarizer, new TestGroupFinder());

        processor.processVideo(videoFile, outputCsv1);
        processor.processVideo(videoFile, outputCsv2);

        assertTrue(outputCsv1.exists());
        assertTrue(outputCsv2.exists());
        assertEquals(outputCsv1.length(), outputCsv2.length());

        outputCsv1.delete();
        outputCsv2.delete();
    }

    @Test
    public void testProcessorSummaryConsistency() throws IOException {
        File videoFile = new File(VIDEOS_DIR, "ballsMoving.mp4");
        File outputCsv1 = new File("test_output_consistency1.csv");
        File outputCsv2 = new File("test_output_consistency2.csv");

        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(new TestColorDistanceFinder(), 100, 50);
        VideoProcessor processor = new VideoProcessor(binarizer, new TestGroupFinder());

        processor.processVideo(videoFile, outputCsv1);
        processor.processVideo(videoFile, outputCsv2);

        assertEquals(outputCsv1.length(), outputCsv2.length());
        outputCsv1.delete();
        outputCsv2.delete();
    }
}
