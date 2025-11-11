To run tests: mvn -f processor/pom.xml test

to run video: java -jar processor/target/centroid-finder-1.0-SNAPSHOT-jar-with-dependencies.jar processor/sampleInput/ensantina.mp4 processor/sampleOutput/ensantina.csv 654321 100


Notes:

GET /api/videos
Returns a list of all video files in /videos/VIDEO_NAME
    Responses: 
        200(OK) -> ["intro.mp4", "demo.mov"], 
        500(Internal Server Error) -> {"error": "Error reading video directory"}


GET /thumbnail/{filename}
returns the first frame of the video as a JPEG
    PATH PARAMETER: 
        filename = string, required (name of the video file)    
        Responses:
            200(OK) -> JPEG binary data (content-type= image/JPEG),
            500(Internal Server Error) -> {"error": "Error generating thumbnail"}


POST /process/{filename}
?targetColor=<hex>&threshold=<int>
    PATH PARAMETER: 
        filename = string, required (name of the video to process)
    QUERY PARAMETERS: 
        targetColor = string, required — Hex color, ex: ff0000
        threshold = number, required — Match threshold, ex: 120
    Responses:
        202(Accepted) -> {"jobId": "123e4567-e89b-12d3-a456-426614174000"},
        400(Bad Request) -> {"error": "Missing targetColor or threshold query parameter."},
        500(Internal Server Error) -> {"error": "Error starting job"}


GET /process/{jobId}/status
Checks the current job to see if its running, completed, or failed.
    PATH PARAMETER:
        jobId = string, required — ID returned by POST call
    Responses:
        200(OK) PROCESSING -> {"status": "processing"},
        200(OK) DONE {"status": "done", "result": "/results/intro.mp4.csv"},
        200(OK) ERROR -> {"status": "error", "error": "Error processing video: Unexpected ffmpeg error"},
        404(Not Found) -> {"error": "Job ID not found"},
        500(Internal Server Error) -> {"error": "Error fetching job status"}


    Questions:
    1. for the last one, why is it 200(OK) even if its for an error

    Answers (Researched):
    1. Because the request itself was fine, so even if theres an error with the job, we should return a 200(OK) since it's just talking about the server itself being ok


PLAN:
implement api requests and functionality in index.js
will likely have to break the file up 
get data that will be used in api requests from jar
DO NOT create everything from scratch use what we have


ARCHITECTURE SKETCH:

User (makes a request to the server)
v
Goes to: the Express Server, index.js
    (requests the user can make)
        GET /api/videos, GET /thumbnail/:filename, POST /process/:filename?targetColor&threshold, GET /process/:jobId/status
v
Goes to: VideoProcessor.jar
    (will read a video from /videos, outputs the CSV file to /results) 