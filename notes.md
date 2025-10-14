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
