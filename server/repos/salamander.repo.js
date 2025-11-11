// salamander.repo.js — Repository layer for managing salamander video data.
// This handles the data logic, separate from controllers and routes.

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
