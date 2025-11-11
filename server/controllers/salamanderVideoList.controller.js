import fs from 'fs';
import path from 'path';
import { v4 as uuidv4 } from 'uuid';
import { spawn } from 'child_process';
import dotenv from 'dotenv';
import * as data from '../db/salamanderData.js'; // dynamic sampleInput paths

// Load .env file for optional path overrides (VIDEOS_DIR, RESULTS_DIR, JAR_PATH)
dotenv.config();

// ---------------------------
// In-memory job store
// ---------------------------
// Maps jobId -> { status, filename, result? }
// NOTE: ephemeral storage — will reset if server restarts
const jobs = new Map();

// ---------------------------
// Results directory setup
// ---------------------------
// Where processed CSV files are written
// Use .env override if provided, otherwise default to server/results
const RESULTS_DIR = process.env.RESULTS_DIR || path.join(__dirname, '..', 'results');
if (!fs.existsSync(RESULTS_DIR)) fs.mkdirSync(RESULTS_DIR, { recursive: true });

// ---------------------------
// Java processor JAR path
// ---------------------------
// Points to your compiled processor JAR
// Can be overridden via .env
const JAR_PATH =
  process.env.JAR_PATH ||
  path.join('processor', 'target', 'centroid-finder-1.0-SNAPSHOT-jar-with-dependencies.jar');

// ---------------------------
// GET /api/videos
// ---------------------------
// Returns an array of all video filenames in processor/sampleInput
// Uses salamanderData.getVideos() to ensure it reads the correct folder
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
// Returns the first frame of the requested video as a JPEG
// Uses ffmpeg to extract the frame from processor/sampleInput
export const requestThumbnail = (req, res) => {
  const { filename } = req.params;
  const filePath = data.getVideoPath(filename);

  if (!fs.existsSync(filePath)) return res.status(404).json({ error: 'Video not found' });

  // Spawn ffmpeg to output a single frame as image2pipe
  const ffmpeg = spawn('ffmpeg', [
    '-i',
    filePath,
    '-frames:v',
    '1',
    '-f',
    'image2pipe',
    '-',
  ]);

  res.setHeader('Content-Type', 'image/jpeg');
  ffmpeg.stdout.pipe(res);

  ffmpeg.stderr.on('data', (d) => console.error(`ffmpeg error: ${d}`));
  ffmpeg.on('close', (code) => {
    if (code !== 0) console.error(`ffmpeg exited with code ${code}`);
  });
};

// ---------------------------
// POST /process/:filename?targetColor=<hex>&threshold=<int>
// ---------------------------
// Starts a background processing job on the given video
// Uses your Java processor JAR on the video in processor/sampleInput
export const respondStartProcess = (req, res) => {
  try {
    const { filename } = req.params;
    const { targetColor, threshold } = req.query;

    // Validate required query params
    if (!targetColor || !threshold)
      return res.status(400).json({ error: 'Missing targetColor or threshold query parameter.' });

    const inputPath = data.getVideoPath(filename);
    const outputCsv = path.join(RESULTS_DIR, `${filename}.csv`);

    if (!fs.existsSync(inputPath)) return res.status(404).json({ error: 'Video file not found' });

    const jobId = uuidv4();
    jobs.set(jobId, { status: 'processing', filename });

    // Spawn Java processor in detached mode
    const javaProcess = spawn(
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
// Returns the current status for a previously created job
export const requestJobStatus = (req, res) => {
  const { jobId } = req.params;
  const job = jobs.get(jobId);
  if (!job) return res.status(404).json({ error: 'Job ID not found' });

  res.status(200).json(job);
};
