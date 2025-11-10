import { getVideos } from "../repos/salamander.repo";
import { getThumbNail } from "../repos/salamander.repo";
import { getVideoJobStatus } from "../repos/salamander.repo";
import { postVideoProcess } from "../repos/salamander.repo";

export const requestSalamanderVideos = (req, res) => 
{
    const videos = getVideos();
    if(videos)
        res.status(200).json(videos);
    else
        res.status(500).send();
}

export const requestThumbnail = (req, res) => 
{
    //get thumbnail
}

export const respondStartProcess = (req, res) => 
{
    //send video to be processed
}

export const requestJobStatus = (req, res) => 
{
    //get jobId and status
}