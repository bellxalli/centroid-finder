***Required***
Refactoring & Code Organization
    Remove duplicate or unused code (e.g., duplicate static middleware in index.js).
    ***Organize files and folders for clarity (e.g., separate API, static, and utility code).

Testing
    Add integration tests for API endpoints and processor interactions.
    Test error cases and edge conditions (invalid input, missing files, etc.).

Error Handling
    Validate all inputs and outputs (both server and Java processor).
    Gracefully handle missing files or bad requests.

Documentation
    Document all functions and classes (Java and JS).
    Add usage instructions for running locally and in Docker.

***Optional***
Security
    Optimize Docker image size (multi-stage builds, only production dependencies).
    Restrict CORS origins to only trusted domains.

Performance
    Profile video processing for bottlenecks; consider more efficient libraries if needed.
    Optimize Docker image size (multi-stage builds, only production dependencies).

Bug Fixes
    Check for redundant middleware and route registrations (e.g., repeated express.static calls).
    Fix status display and error handling for processor calls in the server.

Other
    Review and update dependencies for security and compatibility.
    Automate linting and formatting for both Java and JS.


