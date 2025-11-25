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

    // Ensure directory is accessible
    try {
      fs.accessSync(videosDir, fs.constants.R_OK);
    } catch (accessErr) {
      console.error(`Cannot read sampleInput folder: ${videosDir}`, accessErr);
      return [];
    }

    const files = fs.readdirSync(videosDir);

    if (!Array.isArray(files)) {
      console.error("Unexpected directory read result:", files);
      return [];
    }

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
  // Prevent directory traversal attacks
  if (
    typeof filename !== "string" ||
    filename.includes('..') ||
    filename.includes('/') ||
    filename.includes('\\')
  ) {
    throw new Error("Invalid filename");
  }

  const filePath = path.join(videosDir, filename);

  // Extra safety — ensure the path is inside videosDir
  const resolved = path.resolve(filePath);
  if (!resolved.startsWith(path.resolve(videosDir))) {
    throw new Error("Invalid filename path");
  }

  return resolved;
};
