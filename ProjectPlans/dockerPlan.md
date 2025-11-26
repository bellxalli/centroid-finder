Plan for Docker Image:

    Image Base:
    * use official node image and install java

    Running Node and Java:
    * java and node must be installed

    Testing:
    * automated tests?
    * buildit, run it and then make request to API

    Endpoints still available:
    * Expose port in docker
    * have node listen on 0.0.0.0 (not localhost!)
    * publish ports

    Accessing videos and results:
    * Use environment variables (with defaults) and volumes 
    * Backend should not hardcode host paths

    Make the image small, cacheable, and quick to rebuild:
    * Use a small base image
    * Install minimal java
    * Use Docker layer caching
    * Use .dockerignore
