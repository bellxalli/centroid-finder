import express from 'express';
import router from './routes/salamander.routes.js'
import dotenv from 'dotenv'
dotenv.config()

const app = express();

//middleware
app.use(express.json());
app.use('/videos', express.static(process.env.VIDEOS_DIR));
app.use('/results', express.static(process.env.RESULTS_DIR));


//routes
app.use("/api", router);
app.use('/videos', express.static(process.env.VIDEOS_DIR))
app.use('/results', express.static(process.env.RESULTS_DIR))

const PORT = process.env.PORT
app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
})