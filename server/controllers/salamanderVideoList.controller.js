import fs from 'fs';
import path from 'path';
import { v4 as uuidv4 } from 'uuid';
import { fileURLToPath } from 'url';
import { spawn } from 'child_process';
import dotenv from 'dotenv';
import { getVideos, getThumbNail, getVideoJobStatus, postVideoProcess } from "../repos/salamander.repo";

dotenv.config();

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const jobs = new Map();

const VIDEOS_DIR = process.env.VIDEOS_DIR || path.join(__dirname, '..', 'videos');
const RESULTS_DIR = process.env.RESULTS_DIR || path.join(__dirname, '..', 'results');
const JAR_PATH = process.env.JAR_PATH || path.join(__dirname, '..', '..', 'processor', 'target', 'videoprocessor.jar');

export const requestSalamanderVideos = (req, res) => {
    fs.readdir(VIDEOS_DIR, (err, files) => {
        if (err) return res.status(500).json({ error: 'Error reading video directory' });
        res.status(200).json(files.filter(file => /\.(mp4|mov)$/i.test(file)));
    });
};

export const requestThumbnail = (req, res) => {
    const { filename } = req.params;
    const filePath = path.join(VIDEOS_DIR, filename);

    fs.access(filePath, fs.constants.F_OK, (err) => {
        if (err) return res.status(404).json({ error: 'Video not found' });

        const ffmpeg = spawn('ffmpeg', ['-i', filePath, '-frames:v', '1', '-f', 'image2pipe', '-']);
        res.setHeader('Content-Type', 'image/jpeg');
        ffmpeg.stdout.pipe(res);

        ffmpeg.stderr.on('data', data => console.error(`ffmpeg error: ${data}`));
        ffmpeg.on('close', code => { if (code !== 0) console.error(`ffmpeg exited with code ${code}`); });
    });
};

export const respondStartProcess = (req, res) => {
    try {
        const { filename } = req.params;
        const { targetColor, threshold } = req.query;

        if (!targetColor || !threshold) return res.status(400).json({ error: 'Missing targetColor or threshold query parameter.' });

        const inputPath = path.join(VIDEOS_DIR, filename);
        const outputCsv = path.join(RESULTS_DIR, `${filename}.csv`);
        if (!fs.existsSync(inputPath)) return res.status(404).json({ error: 'Video file not found' });

        const jobId = uuidv4();
        jobs.set(jobId, { status: 'processing', filename });

        const javaProcess = spawn('java', ['-jar', JAR_PATH, inputPath, outputCsv, targetColor, threshold], { detached: true, stdio: 'ignore' });
        javaProcess.unref();

        const checkInterval = setInterval(() => {
            if (fs.existsSync(outputCsv)) {
                jobs.set(jobId, { status: 'done', result: `/results/${filename}.csv` });
                clearInterval(checkInterval);
            }
        }, 2000);

        res.status(202).json({ jobId });
    } catch (error) {
        console.error('Error in respondStartProcess:', error);
        res.status(500).json({ error: 'Error starting job' });
    }
};

export const requestJobStatus = (req, res) => {
    try {
        const { jobId } = req.params;
        const job = jobs.get(jobId);
        if (!job) return res.status(404).json({ error: 'Job ID not found' });
        res.status(200).json(job);
    } catch (err) {
        console.error('Error fetching job status:', err);
        res.status(500).json({ error: 'Error fetching job status' });
    }
};
