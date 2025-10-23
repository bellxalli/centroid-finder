
package io.github.bellxalli.centroidFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;




public class DfsBinaryGroupFinder implements BinaryGroupFinder {
   /**
    * Finds connected pixel groups of 1s in an integer array representing a binary image.
    * 
    * The input is a non-empty rectangular 2D array containing only 1s and 0s.
    * If the array or any of its subarrays are null, a NullPointerException
    * is thrown. If the array is otherwise invalid, an IllegalArgumentException
    * is thrown.
    *
    * Pixels are considered connected vertically and horizontally, NOT diagonally.
    * The top-left cell of the array (row:0, column:0) is considered to be coordinate
    * (x:0, y:0). Y increases downward and X increases to the right. For example,
    * (row:4, column:7) corresponds to (x:7, y:4).
    *
    * The method returns a list of sorted groups. The group's size is the number 
    * of pixels in the group. The centroid of the group
    * is computed as the average of each of the pixel locations across each dimension.
    * For example, the x coordinate of the centroid is the sum of all the x
    * coordinates of the pixels in the group divided by the number of pixels in that group.
    * Similarly, the y coordinate of the centroid is the sum of all the y
    * coordinates of the pixels in the group divided by the number of pixels in that group.
    * The division should be done as INTEGER DIVISION.
    *
    * The groups are sorted in DESCENDING order according to Group's compareTo method.
    * 
    * @param image a rectangular 2D array containing only 1s and 0s
    * @return the found groups of connected pixels in descending order
    */
    @Override
    public List<Group> findConnectedGroups(int[][] image) {

        // (rows = y, cols = x)
        int rows = 0;
        int cols = 0;
        // String coords = (rows + "," + cols);
        List<Group> groups = new ArrayList<>();

        if (image.length == 0) return new ArrayList<>();

        for (int[] row : image) {
           
            if (row == null) throw new NullPointerException("Subarray is null and can't be!");

        }//end for

        int height = image.length;
        int width = image[0].length;

        boolean[][] visited = new boolean[height][width];

        for (int[] row : image){

            if (row.length != width) throw new IllegalArgumentException(
            "The image isn't rectangular in shape/The rows and columns are not equal!");

        }//end for

        for (int[] row : image){
            for (int value : row){

                // if value is not 1 or 0 its invalid
                if (value != 0 && value != 1){

                throw new IllegalArgumentException("Invalid value: " + value + "!");

            }//end if
         }//end forInner
        }//end forOutter


        int[][] directions = {

            {-1,0}, // up
            {1,0}, // down
            {0,-1}, // left
            {0,1} // right

        };


    for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (image[y][x] == 1 && !visited[y][x]) {

                    List<int[]> pixels = new ArrayList<>(); //makes a pixels list every time

                    helper(image, visited, y, x, pixels, directions, height, width);

                    int size = pixels.size();
                    int sumOfX = 0;
                    int sumOfY = 0;

                    for (int[] pix : pixels) {

                        sumOfY += pix[0];
                        sumOfX += pix[1];

                    }//end forInner

                    int centroidX = sumOfX / size;
                    int centroidY = sumOfY / size;

                    groups.add(new Group(size, new Coordinate(centroidX, centroidY)));

                }//end if 
            }//end forOuter
        }//end main for

        Collections.sort(groups, Collections.reverseOrder());

        return groups;

    }//end findConnectedGroups
    

    public void helper(int[][] image, boolean[][] visited, int y, int x, List<int[]> pixels, int[][] directions, int height, int width) {
        
        visited[y][x] = true;

        pixels.add(new int[]{y, x});

        for (int[] d : directions) {

            int newY = y + d[0];
            int newX = x + d[1];

            if (newY >= 0 && newY < height && 
            newX >= 0 && newX < width && 
            !visited[newY][newX] && 
            image[newY][newX] == 1) 
            {
            helper(image, visited, newY, newX, pixels, directions, height, width);
            } //end if
        }//end for
    }//end helper

}
