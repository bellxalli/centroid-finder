# ---------- Base image ----------
# Use Node.js 25 on Alpine Linux (small, lightweight image)
FROM node:25-alpine

# ---------- Working directory ----------
# Set the working directory inside the container to root
WORKDIR /

# ---------- Install system dependencies ----------
# Install Java 21 (to run your JAR), ffmpeg (video processing), and bash
RUN apk add --no-cache openjdk21 ffmpeg bash

# ---------- Install Node dependencies ----------
# Copy package.json / package-lock.json and install Node.js dependencies
COPY package*.json ./
RUN npm install --production
RUN npm install cors --production  # <--- ensure cors is installed

# ---------- Copy application code ----------
# Copy all remaining project files into the container
COPY . .

# ---------- Environment variables ----------
# Default paths for mounted volumes inside container
ENV VIDEOS_DIR=/videos
ENV RESULTS_DIR=/results

# Path to your Java processor JAR inside the container
ENV JAR_PATH=/processor/target/centroid-finder-1.0-SNAPSHOT-jar-with-dependencies.jar

# ---------- Expose container port ----------
# Make port 3000 accessible from host
EXPOSE 3000

# ---------- Default command ----------
# Run your Node.js server
CMD ["node", "server/index.js"]
