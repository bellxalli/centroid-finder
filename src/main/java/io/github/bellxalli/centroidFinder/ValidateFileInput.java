package io.github.bellxalli.centroidFinder;

import java.io.File;

public class ValidateFileInput {


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
