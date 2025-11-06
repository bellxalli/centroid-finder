package io.github.bellxalli.centroidFinder;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ServerTests {

    @Test
    void testDistanceRedVsBlue() {
        EuclideanColorDistance finder = new EuclideanColorDistance();
        int red = 0xFF0000;
        int blue = 0x0000FF;
        double dist = finder.distance(red, blue);
        assertEquals(Math.sqrt(255*255 + 255*255), dist, 0.001);
    }

    @Test
    void testSingleGroup() {
    int[][] image = {
        {1, 0, 0},
        {1, 1, 0},
        {0, 0, 0}
    };
    DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
    List<Group> groups = finder.findConnectedGroups(image);
    assertEquals(3, groups.get(0).size());
    assertEquals(new Coordinate(0, 0), groups.get(0).centroid());
}


    @Test
    void testDistanceImageBinarizer() {
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, 0xFF0000); // red
        img.setRGB(1, 0, 0x00FF00); // green
        img.setRGB(0, 1, 0x0000FF); // blue
        img.setRGB(1, 1, 0x000000); // black

        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(
            new EuclideanColorDistance(), 0xFF0000, 100
        );

        int[][] binary = binarizer.toBinaryArray(img);
        // red should be white (1), others black (0)
        assertEquals(1, binary[0][0]);
        assertEquals(0, binary[0][1]);
        assertEquals(0, binary[1][0]);
        assertEquals(0, binary[1][1]);
    }

    @Test
    void testBinarizingImageGroupFinder() {
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, 0xFF0000); // red
        img.setRGB(1, 0, 0x00FF00); // green
        img.setRGB(0, 1, 0xFF0000); // red
        img.setRGB(1, 1, 0x000000); // black

        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(
            new EuclideanColorDistance(), 0xFF0000, 100
        );

        DfsBinaryGroupFinder dfsFinder = new DfsBinaryGroupFinder();
        BinarizingImageGroupFinder groupFinder = new BinarizingImageGroupFinder(binarizer, dfsFinder);

        List<Group> groups = groupFinder.findConnectedGroups(img);
        assertEquals(1, groups.size());
        Group g = groups.get(0);
        assertEquals(2, g.size());
        assertEquals(new Coordinate(0, 0), g.centroid()); // centroid x=0, y=0
    }
}
