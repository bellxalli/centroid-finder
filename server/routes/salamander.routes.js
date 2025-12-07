/*
 * This is the salamander.routes.js file.
 * This file defines the API endpoints. These endpoints include video listing, 
 * thumbnail retrieval, video processing (binarization), and checking job (processing)
 * status.
 * 
 * Author: Xalli Bell and Emily Menken
 * 2025
*/

import express from 'express';
import { requestSalamanderVideos } from '../controllers/salamanderVideoList.controller.js';
import { requestThumbnail } from '../controllers/salamanderVideoList.controller.js';
import { requestJobStatus } from '../controllers/salamanderVideoList.controller.js';
import { respondStartProcess } from '../controllers/salamanderVideoList.controller.js';
import { getCsvByFilename } from '../controllers/salamanderVideoList.controller.js';

const router = express.Router();

// Returns list of all videos
router.get("/videos", requestSalamanderVideos);

// Returns thumbnail for a specific video
router.get("/thumbnail/:filename", requestThumbnail);

// Start processing a video
router.post("/process/:filename", respondStartProcess);

// Check job status
router.get("/process/:jobId/status", requestJobStatus);

// Download CSV file *by filename* (this matches your frontend)
router.get("/results/:filename", getCsvByFilename);

export default router;
