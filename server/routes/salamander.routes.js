import express from 'express';
import { requestSalamanderVideos } from '../controllers/salamanderVideoList.controller';
import { requestThumbnail } from '../controllers/salamanderVideoList.controller';
import { requestJobStatus } from '../controllers/salamanderVideoList.controller';
import { respondStartProcess } from '../controllers/salamanderVideoList.controller';

const router = express.Router();

router.get("/videos/:videoName", requestSalamanderVideos);
router.get("/thumbnail/:videoName", requestThumbnail);
router.post("/process/:videoName", respondStartProcess);
router.get("/process/:jobId/status", requestJobStatus);

export default router;