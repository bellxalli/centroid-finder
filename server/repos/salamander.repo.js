import fse from 'fs-extra';

//read json array and convert to js object array
const salamanderVideos = fse.readJSONSync("./src/db/salamanderVideos.json");

//get list of videos
export const getVideos = () =>
{
    return salamanderVideos;
}

//get thumbnail associated with each video
export const getThumbNail = () =>
{
    //get thumbanil and return it
}

export const postVideoProcess = () =>
{
    //send video process to backend
}

export const getVideoJobStatus = () => 
{
    //get jobId to track video processing status
}