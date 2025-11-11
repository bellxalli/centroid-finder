import fs from 'fs';
import path from 'path';
import { v4 as uuidv4 } from 'uuid';
import dotenv from 'dotenv';
import * as data from '../db/salamanderData.js'; // dynamic sampleInput paths
import ffmpeg from 'fluent-ffmpeg';
import ffmpegPath from 'ffmpeg-static'; // precompiled ffmpeg binary for Node

// Load .env file for optional path overrides (VIDEOS_DIR, RESULTS_DIR, JAR_PATH)
dotenv.config();

// Set ffmpeg path for fluent-ffmpeg
ffmpeg.setFfmpegPath(ffmpegPath);

// ---------------------------
// In-memory job store
// ---------------------------
// Maps jobId -> { status, filename, result? }
// NOTE: ephemeral storage — will reset if server restarts
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
// Uses fluent-ffmpeg + ffmpeg-static to extract the first frame
export const requestThumbnail = (req, res) => {
  const { filename } = req.params;
  const filePath = data.getVideoPath(filename);

  if (!fs.existsSync(filePath)) return res.status(404).json({ error: 'Video not found' });

  res.setHeader('Content-Type', 'image/jpeg');

  // Extract single frame and pipe to response
  ffmpeg(filePath)
    .frames(1)
    .format('mjpeg') // JPEG output
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
    const { targetColor, threshold } = req.query;

    if (!targetColor || !threshold)
      return res.status(400).json({ error: 'Missing targetColor or threshold query parameter.' });

    const inputPath = data.getVideoPath(filename);
    const outputCsv = path.join(RESULTS_DIR, `${filename}.csv`);

    if (!fs.existsSync(inputPath)) return res.status(404).json({ error: 'Video file not found' });

    const jobId = uuidv4();
    jobs.set(jobId, { status: 'processing', filename });

    // Spawn Java processor in detached mode
    const javaProcess = require('child_process').spawn(
      'java',
      ['-jar', JAR_PATH, inputPath, outputCsv, targetColor, threshold],
      { detached: true, stdio: 'ignore' }
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
