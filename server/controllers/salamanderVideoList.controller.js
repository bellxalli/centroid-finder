import fs from 'fs';
import { getVideos } from "../repos/salamander.repo";
import { getThumbNail } from "../repos/salamander.repo";
import { getVideoJobStatus } from "../repos/salamander.repo";
import { postVideoProcess } from "../repos/salamander.repo";

const __filename = fielURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const jobs = new Map(); //store here for now


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
    const {filename} = req.params;
    const {targetColor, threshold} = req.query;

    if(!targetColor || !threshold)
    {
        return res.status(400).json({
            error: "Missing targetColor or threshold query parameter."
        });
    }

    const jobId = uuidv4();

    jobs.set(jobId, {status: 'processing', filename});
    
    setTimeout(() => {
        jobs.set(jobId, {
            status: 'done',
            result: `/results/${filename}.csv`
        });
    }, 5000);

    res.status(202).json({jobId});
}

export const requestJobStatus = (req, res) => 
{
    //get jobId and status
}