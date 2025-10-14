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
    * The groups are sorted in DESCENDING order according to Group's compareTo method
    * (size first, then x, then y). That is, the largest group will be first, the 
    * smallest group will be last, and ties will be broken first by descending 
    * y value, then descending x value.
    * 
    * @param image a rectangular 2D array containing only 1s and 0s
    * @return the found groups of connected pixels in descending order
    */
    @Override
    public List<Group> findConnectedGroups(int[][] image) {

        // (rows = y, cols = x)
        int rows = 0;
        int cols = 0;
        String coords = (rows + "," + cols);
        // int value = image[rows][cols];

        if(image[0] == null || image[1] == null) throw new NullPointerException("Array/Subarray is null and can't be!"); 
        if(image.length==0) throw new IllegalArgumentException ("Image can't be 0");
        if(cols < 0) throw new IllegalArgumentException ("Column is negative and can't be!");
        if(rows < 0) throw new IllegalArgumentException ("Row is negative and can't be!");
        if(image[0].length>(image[1].length)||image[0].length<(image[1].length)) throw new IllegalArgumentException ("The image isn't rectangular in shape/The rows and columns are not equal!");

        for (int[] row : image){
            for (int value : row){

                // if value is not 1 or 0 its invalid
                if (value != 0 && value != 1){

                throw new IllegalArgumentException("Invalid value: " + value + "!");

            }//end if
         }//end for1
        }//end for2


        // if() throw new IllegalArgumentException ("Illegal Argument exception");




        


// finds 1s and 0s in a binary image
//uses a 2d array- int[]][] image

        return null;

    }//end findConnectedGroups
    
}
