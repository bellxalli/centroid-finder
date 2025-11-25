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
try {
  if (!fs.existsSync(RESULTS_DIR)) fs.mkdirSync(RESULTS_DIR, { recursive: true });
} catch (err) {
  console.error("Failed to create or access results directory:", err);
}

// ---------------------------
// Java processor JAR path
// ---------------------------
const JAR_PATH =
  process.env.JAR_PATH ||
  path.join('processor', 'target', 'centroid-finder-1.0-SNAPSHOT-jar-with-dependencies.jar');

if (!fs.existsSync(JAR_PATH)) {
  console.warn("⚠ Warning: Java processor JAR not found at:", JAR_PATH);
}

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

  let filePath;
  try {
    filePath = data.getVideoPath(filename);
  } catch (err) {
    return res.status(400).json({ error: 'Invalid filename' });
  }

  if (!fs.existsSync(filePath)) {
    return res.status(404).json({ error: 'Video not found' });
  }

  res.setHeader('Content-Type', 'image/jpeg');

  try {
    ffmpeg(filePath)
      .frames(1)
      .format('mjpeg')
      .on('error', (err) => {
        console.error('ffmpeg error:', err);
        if (!res.headersSent) {
          res.status(500).json({ error: 'Error generating thumbnail' });
        }
      })
      .pipe(res, { end: true });
  } catch (err) {
    console.error('Unexpected ffmpeg error:', err);
    res.status(500).json({ error: 'Thumbnail generation failed' });
  }
};

// ---------------------------
// POST /process/:filename?targetColor=<hex>&threshold=<int>
// ---------------------------
export const respondStartProcess = (req, res) => {
  try {
    const { filename } = req.params;
    let { targetColor, threshold } = req.query;

    if (!targetColor || !threshold) {
      return res.status(400).json({
        error: 'Missing targetColor or threshold query parameter.'
      });
    }

    // Validate target color
    if (targetColor.startsWith('#')) targetColor = '0x' + targetColor.slice(1);
    if (!/^0x[0-9A-Fa-f]{6}$/.test(targetColor)) {
      return res.status(400).json({ error: 'Invalid targetColor format. Expected #RRGGBB or 0xRRGGBB.' });
    }

    // Validate threshold
    threshold = parseInt(threshold);
    if (isNaN(threshold) || threshold < 0) {
      return res.status(400).json({ error: 'threshold must be a positive integer.' });
    }

    // Windows-safe paths
    const inputPath = data.getVideoPath(filename).replace(/\\/g, '/');
    const outputCsv = path.join(RESULTS_DIR, `${filename}.csv`).replace(/\\/g, '/');

    if (!fs.existsSync(inputPath)) {
      return res.status(404).json({ error: 'Video file not found' });
    }

    if (!fs.existsSync(JAR_PATH)) {
      return res.status(500).json({ error: 'Processing service unavailable (missing JAR).' });
    }

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

    javaProcess.on('error', (err) => {
      console.error('Failed to start Java process:', err);
      const job = jobs.get(jobId);
      if (job) job.status = 'error';
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

      if (!job) return;

      if (code === 0 && fs.existsSync(outputCsv)) {
        job.status = 'done';
        job.result = `/results/${filename}.csv`;
      } else {
        job.status = 'error';
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
