import fs from 'fs';
import path from 'path';
import dotenv from 'dotenv';
dotenv.config();

const resultsDir = process.env.RESULTS_DIR;

export const getVideoJobStatus = (jobId, jobs) => {
    const job = jobs.get(jobId);
    if (!job) {
        return null; // controller will handle 404
    }

    // If job was "processing", check if output file exists yet
    const outputFile = path.join(resultsDir, `${job.filename}.csv`);
    if (job.status === 'processing' && fs.existsSync(outputFile)) {
        job.status = 'done';
        job.result = `/results/${job.filename}.csv`;
        jobs.set(jobId, job);
    }

    return job;
};
