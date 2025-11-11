import { spawn } from 'child_process';
import path from 'path';
import dotenv from 'dotenv';
dotenv.config();

const videosDir = process.env.VIDEOS_DIR;

export const getThumbNail = (filename, outputPath) => {
    const inputPath = path.join(videosDir, filename);

    return new Promise((resolve, reject) => {
        const ffmpeg = spawn('ffmpeg', [
            '-i', inputPath,
            '-frames:v', '1', // only 1 frame
            '-q:v', '2',      // quality
            outputPath
        ]);

        ffmpeg.on('close', (code) => {
            if (code === 0) resolve(outputPath);
            else reject(new Error('Error generating thumbnail'));
        });
    });
};
