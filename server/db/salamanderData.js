// server/db/salamanderData.js
// Dynamic "database" that lists videos in processor/sampleInput

import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

// Polyfill __filename / __dirname for ES modules
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Path to processor/sampleInput folder
const videosDir = path.join(__dirname, '..', '..', 'processor', 'sampleInput');

/**
 * Get all video filenames in the sampleInput directory.
 * Only returns .mp4 files (case-insensitive)
 */
export const getVideos = () => {
  try {
    if (!fs.existsSync(videosDir)) {
      console.error(`Sample input folder not found: ${videosDir}`);
      return [];
    }

    const files = fs.readdirSync(videosDir);
    return files.filter(file => /\.(mp4)$/i.test(file));
  } catch (err) {
    console.error("Error reading sampleInput directory:", err);
    return []; // Return empty array if folder is unreadable
  }
};

/**
 * Get the absolute path to a specific video in sampleInput.
 * Useful for passing to your Java processor.
 */
export const getVideoPath = (filename) => {
  return path.join(videosDir, filename);
};
