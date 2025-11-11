import fs from 'fs';
import path from 'path';
import { v4 as uuidv4 } from 'uuid';
import dotenv from 'dotenv';
import * as data from '../db/salamanderData.js';
import ffmpeg from 'fluent-ffmpeg';
import ffmpegPath from 'ffmpeg-static';
import { spawn } from 'child_process';

// Load .env file
dotenv.config();

// Set ffmpeg path
ffmpeg.setFfmpegPath(ffmpegPath);

// ---------------------------
// In-memory job store
// ---------------------------
const jobs = new Map();

// ---------------------------
// Results directory setup
// ---------------------------
const RESULTS_DIR = process.env.RESULTS_DIR || path.join(__dirname, '..', 'results');
if (!fs.existsSync(RESULTS_DIR)) fs.mkdirSync(RESULTS_DIR, { recursive: true });

// ---------------------------
// Java processor JAR path
// ---------------------------
const JAR_PATH =
  process.env.JAR_PATH ||
  path.join('processor', 'target', 'centroid-finder-1.0-SNAPSHOT-jar-with-dependencies.jar');

// ---------------------------
// GET /api/videos
// ---------------------------
export const requestSalamanderVideos = (req, res) => {
  try {
    const videos = data.getVideos();
    res.status(200).json(videos);
  } catch (err) {
    console.error('Error listing videos:', err);
    res.status(500).json({ error: 'Error reading video directory' });
  }
};

// ---------------------------
// GET /thumbnail/:filename
// ---------------------------
export const requestThumbnail = (req, res) => {
  const { filename } = req.params;
  const filePath = data.getVideoPath(filename);

  if (!fs.existsSync(filePath)) return res.status(404).json({ error: 'Video not found' });

  res.setHeader('Content-Type', 'image/jpeg');

  ffmpeg(filePath)
    .frames(1)
    .format('mjpeg')
    .on('error', (err) => {
      console.error('ffmpeg error:', err);
      res.status(500).json({ error: 'Error generating thumbnail' });
    })
    .pipe(res, { end: true });
};

// ---------------------------
// POST /process/:filename?targetColor=<hex>&threshold=<int>
// ---------------------------
export const respondStartProcess = (req, res) => {
  try {
    const { filename } = req.params;
    let { targetColor, threshold } = req.query;

    if (!targetColor || !threshold)
      return res.status(400).json({ error: 'Missing targetColor or threshold query parameter.' });

    // Windows-safe paths
    const inputPath = data.getVideoPath(filename).replace(/\\/g, '/');
    const outputCsv = path.join(RESULTS_DIR, `${filename}.csv`).replace(/\\/g, '/');

    if (!fs.existsSync(inputPath)) return res.status(404).json({ error: 'Video file not found' });

    // Convert #RRGGBB → 0xRRGGBB
    if (targetColor.startsWith('#')) targetColor = '0x' + targetColor.slice(1);

    const jobId = uuidv4();
    jobs.set(jobId, { status: 'processing', filename });

    console.log('Running Java command with:');
    console.log('JAR:', JAR_PATH);
    console.log('Input:', inputPath);
    console.log('Output:', outputCsv);
    console.log('TargetColor:', targetColor);
    console.log('Threshold:', threshold);

    const javaProcess = spawn('java', ['-jar', JAR_PATH, inputPath, outputCsv, targetColor, threshold], {
      shell: false
    });

    javaProcess.stdout.on('data', (data) => {
      console.log(`[Java stdout] ${data.toString().trim()}`);
    });

    javaProcess.stderr.on('data', (data) => {
      console.error(`[Java stderr] ${data.toString()}`);
    });

    javaProcess.on('exit', (code) => {
      console.log(`Java process exited with code ${code}`);
      const job = jobs.get(jobId);

      if (code === 0 && fs.existsSync(outputCsv)) {
        if (job) {
          job.status = 'done';
          job.result = `/results/${filename}.csv`;
        }
      } else {
        if (job) job.status = 'error';
      }
    });

    res.status(202).json({ jobId });
  } catch (err) {
    console.error('Error starting processing job:', err);
    res.status(500).json({ error: 'Error starting job' });
  }
};

// ---------------------------
// GET /process/:jobId/status
// ---------------------------
export const requestJobStatus = (req, res) => {
  const { jobId } = req.params;
  const job = jobs.get(jobId);
  if (!job) return res.status(404).json({ error: 'Job ID not found' });

  res.status(200).json(job);
};
