/*
 * This file is the salamander.repo.js.
 * This file implements the repository layer of the Centroid Finder application. 
 * The file does data operations that manage the video files and tracking 
 * video-processing jobs. The reposity abstracts data acess from the controller 
 * and routing layers allowing for the application to work with a clean API that is 
 * independent of the filesystem or database.
 * 
 * Authors: Xalli Bell and Emily Menken
 * 2025
*/


import fs from "fs";
import path from "path";

// In a real app this might connect to a database.
// For now, we use a local Map() to store job status.
const jobMap = new Map();

const VIDEOS_DIR = path.resolve("./videos");

export function getAllVideos() {
  // Simulate fetching all video files from a directory or database
  try {
    if (!fs.existsSync(VIDEOS_DIR)) {
      console.error(`Videos directory not found: ${VIDEOS_DIR}`);
      return [];
    }

    try {
      fs.accessSync(VIDEOS_DIR, fs.constants.R_OK);
    } catch (err) {
      console.error(`Videos directory not readable: ${VIDEOS_DIR}`, err);
      return [];
    }

    const files = fs.readdirSync(VIDEOS_DIR);

    if (!Array.isArray(files)) {
      console.error("Unexpected readdir result:", files);
      return [];
    }

    return files.filter((file) => /\.mp4$/i.test(file));
  } catch (err) {
    console.error("Error reading videos directory:", err);
    return [];
  }
}

export function startProcessingJob(videoName) {
  // Generate a simple unique job ID and store its status as "processing"
  try {
    if (
      typeof videoName !== "string" ||
      videoName.includes("..") ||
      videoName.includes("/") ||
      videoName.includes("\\")
    ) {
      throw new Error("Invalid video filename");
    }

    const jobId = `job_${Date.now()}_${Math.floor(Math.random() * 1e6)}`;

    jobMap.set(jobId, { status: "processing", videoName });
    return jobId;
  } catch (err) {
    console.error("Error starting job:", err);
    return null;
  }
}

export function getJobStatus(jobId) {
  // Return the job status; if job not found, return "not_found"
  try {
    if (typeof jobId !== "string")
      return { status: "not_found" };

    return jobMap.get(jobId) || { status: "not_found" };
  } catch (err) {
    console.error("Error retrieving job:", err);
    return { status: "not_found" };
  }
}
