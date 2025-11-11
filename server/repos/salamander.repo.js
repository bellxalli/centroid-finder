import fs from "fs";

const jobMap = new Map();

export function getAllVideos() {
  return fs.readdirSync("./videos").filter(file => file.endsWith(".mp4"));
}

export function startProcessingJob(videoName) {
  const jobId = `job_${Date.now()}`;
  jobMap.set(jobId, { status: "processing", videoName });
  return jobId;
}

export function getJobStatus(jobId) {
  return jobMap.get(jobId) || { status: "not_found" };
}
