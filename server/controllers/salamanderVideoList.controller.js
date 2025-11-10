import fs from 'fs';
import path from 'path';
import { v4 as uuidv4 } from 'uuid';
import { fileURLToPath } from 'url';
import { getVideos } from "../repos/salamander.repo";
import { getThumbNail } from "../repos/salamander.repo";
import { getVideoJobStatus } from "../repos/salamander.repo";
import { postVideoProcess } from "../repos/salamander.repo";

const __filename = fielURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const jobs = new Map(); //store here for now


export const requestSalamanderVideos = (req, res) => 
{
    const videosDir = path.join(__dirname, '..', 'videos');
    fs.readdir(videosDir, (err, files) => {
    if(err) 
    {
        return res.status(500).json({error: 'Error reading video directory'});
    }
    const videoFiles = files.filter(file => 
        /\.(mp4)$/i.test(file)
    );
        res.status(200).json(videoFiles);
    });
}

export const requestThumbnail = (req, res) => 
{
    const {fielname} = req.params;
    const filePath = path.join(__dirname, '..', 'videos', filename);
    fs.access(filePath, fs.constants.F_OK, (err) => {
        if(err)
        {
            return res.status(500).json({error: 'Error generating thumbnail'});
        }
        res.status(200).json({message: `Thumbnail for ${filename} would go here`});
    }); //update later with no placeholders
}

export const respondStartProcess = (req, res) => 
{
    try
    {
        const {filename} = req.params;
        const {targetColor, threshold} = req.query;

        if(!targetColor || !threshold)
        {
            return res.status(400).json({
                error: "Missing targetColor or threshold query parameter."
            }); // bad request
        }

        const jobId = uuidv4();

        jobs.set(jobId, {status: 'processing', filename});
        
        setTimeout(() => {
            jobs.set(jobId, {
                status: 'done',
                result: `/results/${filename}.csv`
            });
        }, 5000); 

        res.status(202).json({jobId}); // good
    }
    catch(err)
    {
        console.error("Error in respondStartProcess:", error);

        // internal server error
        res.status(500).json({
            error: "Internal server error. Please try again later."
        });
    }
}

export const requestJobStatus = (req, res) => 
{
    const {jobId} = req.params;
    const job = jobs.get(jobId);

    if(!job)
    {
        return res.status(404).json({error: 'Job ID not found'});
    }

    res.status(200).json(job);
}