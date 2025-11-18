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
app.use('/videos', express.static(process.env.VIDEOS_DIR))
app.use('/results', express.static(process.env.RESULTS_DIR))

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
})