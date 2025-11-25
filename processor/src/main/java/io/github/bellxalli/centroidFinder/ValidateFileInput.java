package io.github.bellxalli.centroidFinder;

import java.io.File;

/**
 * Utility class for validating file inputs.
 * Provides methods to verify that a given file 
 * object points to anexisting file before it is 
 * used for further processing.
 */
public class ValidateFileInput {

    /**
     * Validates that the provided file exists on disk.
     * If the file does not exist, an error message is printed and
     * null is returned. Otherwise the same file object is returned
     * unchanged.
     * @param input the file to validate.
     * @return the input the file if it exists or null if it doesn't.
     */
    public File fileValidate(File input)
    {
        //ensure input file exists
        if (!input.exists()) 
        {
            System.out.println("Input file not found: " + input.getAbsolutePath());
            return null;
        }
        return input;
    }
}
