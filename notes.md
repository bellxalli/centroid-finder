Wave 1:
    take notes for the the rest of the waves and understand what each part of the project does.

Wave 2:
    dfsBinaryGroupFinder finds pixel groups that are connected (represeted by 1's) in a binary image.
    input = 2d array
    throws exceptions for null and invalid values
    pixels directions are up down left and right (r = y, c = x)
    returns list of sorted groups (sorted in desc -> size, x, then y) (group size = # of pixels)
    entroid  = avg of each of the pixel locations accross each direction (dimention)
    do INTEGER DIVISION

Wave 3:
    make thorough unit tests!
    convert hex to rgb (use helper)
    return euclidean color distance between 2 hex rgb colors
        color represented as 0x RR GG BB
        each component ranges 0 - 255
    each color r, g, b is in 3D space 
    Euclidean Distance formula:
        sqrt((r1 - r2)^2 + (g1 - g2)^2 + (b1 - b2)^2)
    meaures how visually different two colors are

Wave 4:
    create the new image (black and white)
        uses color distance to figure out which pixel is black or white in final result
        if distacne is < threshold then pixel = 1 (white), else pixel = black (0)
    do research on `java.awt.image.BufferedImage`
        speifically `getRGB` and `setRGB`
    use following to create a new image:
        new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    will be calling methods from BinaryGroupFinder and ColorDistanceFinder
    unit tests: mocks and fakes
    HINT: use the shift opertors <<,>> to change AARRGGBB to RRGGBB

Wave 5:
    calling methods from BinaryGroupFinder and ImageBinarizer
    THOROUGH UNIT TESTS: mocks and fakes
    not recommed to use external library only JUnit (if AI tries ask to use stubs instead)

Wave 6:
    validate that code works, use following command:
        javac -cp lib/junit-platform-console-standalone-1.12.0.jar src/*.java && java -cp src ImageSummaryApp sampleInput/squares.jpg FFA200 164
    will run main method against sample image
    Once working, clean code up, commit and push and submit!

Wave 7(optional):
    optional fun play time in a separate branch to make code more efficient and / or experiment! 