import { spawn } from 'child_process';
import path from 'path';
import { v4 as uuidv4 } from 'uuid';
import dotenv from 'dotenv';
dotenv.config();

const jobs = new Map(); // in-memory job tracking (could move to DB later)

export const postVideoProcess = (filename, targetColor, threshold) => {
    const jobId = uuidv4();
    const jarPath = process.env.JAR_PATH;
    const videosDir = process.env.VIDEOS_DIR;
    const resultsDir = process.env.RESULTS_DIR;

    const videoPath = path.join(videosDir, filename);
    const outputCsv = path.join(resultsDir, `${filename}.csv`);

    jobs.set(jobId, { status: 'processing', filename });

    try {
        // Example command: java -jar videoProcessor.jar input.mp4 ff0000 120 output.csv
        const child = spawn('java', [
            '-jar',
            jarPath,
            videoPath,
            targetColor,
            threshold,
            outputCsv
        ], {
            detached: true,
            stdio: 'ignore' // prevents Node from waiting for the process
        });

        child.unref(); // allow Node to exit even if this child runs

        // You can optionally use fs.watch() or poll output file existence to mark completion

        return jobId; // Return immediately
    } catch (err) {
        console.error('Error starting JAR process:', err);
        jobs.set(jobId, { status: 'error', error: err.message });
        throw new Error('Error starting job');
    }
};
