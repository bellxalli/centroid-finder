import express from 'express';
import router from './routes/salamander.routes'


const app = express();

//middleware
app.use(express.json());
app.use('/videos', express.static(process.env.VIDEOS_DIR));
app.use('/results', express.static(process.env.RESULTS_DIR));


//route
app.use("/api", router);


const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
})