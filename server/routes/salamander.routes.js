import express from 'express';
import { requestSalamanderVideos } from '../controllers/salamanderVideoList.controller.js';
import { requestThumbnail } from '../controllers/salamanderVideoList.controller.js';
import { requestJobStatus } from '../controllers/salamanderVideoList.controller.js';
import { respondStartProcess } from '../controllers/salamanderVideoList.controller.js';

const router = express.Router();

// Returns list of all videos
router.get("/videos", requestSalamanderVideos);

// Returns thumbnail for a specific video
router.get("/thumbnail/:filename", requestThumbnail);

// Start processing a video
router.post("/process/:filename", respondStartProcess);

// Check job status
router.get("/process/:jobId/status", requestJobStatus);

export default router;
