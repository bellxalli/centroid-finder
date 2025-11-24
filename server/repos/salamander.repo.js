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

// In a real app this might connect to a database.
// For now, we use a local Map() to store job status.
const jobMap = new Map();

export function getAllVideos() {
  // Simulate fetching all video files from a directory or database
  const files = fs.readdirSync("./videos"); // assumes a /videos directory
  return files.filter(file => file.endsWith(".mp4"));
}

export function startProcessingJob(videoName) {
  // Generate a simple unique job ID and store its status as "processing"
  const jobId = `job_${Date.now()}`;
  jobMap.set(jobId, { status: "processing", videoName });
  return jobId;
}

export function getJobStatus(jobId) {
  // Return the job status; if job not found, return "not_found"
  return jobMap.get(jobId) || { status: "not_found" };
}
