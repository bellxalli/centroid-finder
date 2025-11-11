import fs from 'fs';
import path from 'path';
import { v4 as uuidv4 } from 'uuid';
import { fileURLToPath } from 'url';
import { spawn } from 'child_process';
import dotenv from 'dotenv';

// repo functions are imported from your repository layer.
// Note: original imports referenced the same file multiple times; keeping pattern.
import { getVideos } from "../repos/salamander.repo";
import { getThumbNail } from "../repos/salamander.repo";
import { getVideoJobStatus } from "../repos/salamander.repo";
import { postVideoProcess } from "../repos/salamander.repo";

dotenv.config(); // load .env

// __filename / __dirname polyfill for ES modules: gives current file path
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Simple in-memory job store for now.
// This Map stores jobId -> { status, filename, ... }.
// NOTE: this is ephemeral — if the process restarts all jobs are lost.
const jobs = new Map();

// Load paths from .env or use defaults
const VIDEOS_DIR = process.env.VIDEOS_DIR || path.join(__dirname, '..', 'videos');
const RESULTS_DIR = process.env.RESULTS_DIR || path.join(__dirname, '..', 'results');
const JAR_PATH = process.env.JAR_PATH || path.join(__dirname, '..', '..', 'processor', 'target', 'videoprocessor.jar');

/**
 * GET /api/videos
 * Reads the local videos directory and returns an array of video filenames.
 */
export const requestSalamanderVideos = (req, res) => {
    fs.readdir(VIDEOS_DIR, (err, files) => {
        if (err) return res.status(500).json({ error: 'Error reading video directory' });

        const videoFiles = files.filter(file => /\.(mp4|mov)$/i.test(file));
        res.status(200).json(videoFiles);
    });
};

/**
 * GET /thumbnail/:filename
 * Returns a JPEG (first frame) for the requested video filename.
 */
export const requestThumbnail = (req, res) => {
    const { filename } = req.params;
    const filePath = path.join(VIDEOS_DIR, filename);

    fs.access(filePath, fs.constants.F_OK, (err) => {
        if (err) return res.status(404).json({ error: 'Video not found' });

        // Spawn ffmpeg to extract the first frame as JPEG
        const ffmpeg = spawn('ffmpeg', [
            '-i', filePath,
            '-frames:v', '1',
            '-f', 'image2pipe',
            '-'
        ]);

        res.setHeader('Content-Type', 'image/jpeg');
        ffmpeg.stdout.pipe(res);

        ffmpeg.stderr.on('data', data => console.error(`ffmpeg error: ${data}`));
        ffmpeg.on('close', code => {
            if (code !== 0) {
                console.error(`ffmpeg exited with code ${code}`);
                // cannot send after piping stdout, so just log
            }
        });
    });
};

/**
 * POST /process/:filename?targetColor=<hex>&threshold=<int>
 * Starts an asynchronous processing job for the given video file.
 */
export const respondStartProcess = (req, res) => {
    try {
        const { filename } = req.params;
        const { targetColor, threshold } = req.query;

        if (!targetColor || !threshold)
            return res.status(400).json({ error: 'Missing targetColor or threshold query parameter.' });

        const inputPath = path.join(VIDEOS_DIR, filename);
        const outputCsv = path.join(RESULTS_DIR, `${filename}.csv`);

        if (!fs.existsSync(inputPath)) return res.status(404).json({ error: 'Video file not found' });

        const jobId = uuidv4();
        jobs.set(jobId, { status: 'processing', filename });

        // Spawn Java processor JAR in detached mode
        const javaProcess = spawn(
            'java',
            ['-jar', JAR_PATH, inputPath, outputCsv, targetColor, threshold],
            {
                detached: true,
                stdio: 'ignore'
            }
        );
        javaProcess.unref();

        // Poll for CSV file existence to mark job as done
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

/**
 * GET /process/:jobId/status
 * Returns the current status for a previously created jobId.
 */
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
