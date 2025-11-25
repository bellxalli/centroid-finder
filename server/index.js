/*
 * This is the index.js file. 
 * This file initializes and configures aen Express.js server for the Centroid Finder logic. 
 * It loads enviroment variables, applies CORS, sets up middleware,
 * mounts API routes, and configures static file hosting. 
 * 
 * Authors: Xalli Bell and Emily Menken
 * 2025
*/


import express from 'express';
import router from './routes/salamander.routes.js'
import dotenv from 'dotenv'

import cors from 'cors';

dotenv.config()

const app = express();

app.use(
  cors({
    origin: 'http://localhost:4000', //frontend URL
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
    credentials: true,
  })
);

//middleware
app.use(express.json());
app.use('/videos', express.static(process.env.VIDEOS_DIR));
app.use('/results', express.static(process.env.RESULTS_DIR));

//routes
app.use("/api", router);

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
})