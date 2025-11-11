import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const videosDir = path.join(__dirname, '..', '..', 'processor', 'sampleInput');

export const getVideos = () => {
  try {
    return fs.readdirSync(videosDir).filter(file => /\.(mp4)$/i.test(file));
  } catch {
    return [];
  }
};

export const getVideoPath = (filename) => path.join(videosDir, filename);
