Video Libary Options:

1) JCodec
* Pros
    * Pure Java meaning it's easier to deploy across platfroms since all you need is Java installed to run it
    * Open Source
    * Good for Basics meaning good at frame extractions and small MP4 manipulation
    * Small dependency footprint
* Cons
    * Slow for heavy work loads since it is pure java
    * Sparse documentation in places (API's especially)
* Estimate for 8 min video speed process: 3-8mins

2) mp4parser (IsoParser / IsoFile)
* Pros
    * Pure Java meaning it's easier to deploy across platforms since all you need to is Java installed to run it
    * Stable API (backwards compatibility)
    * Good at input / output and metadata work
* Cons
    * Note codec lib so cnat decode or encode data and can't modify frames or pixels
    * Complex API (MP$ box stuctures) whith limited documentation
* Estimate for 8 min video speed process: 3-8mins

3) FFmpeg Wrapper
* Pros
    * supports almost all formats and supports codec
    * Faster because it has native depenencies
    * Functional across many areas: concatentation, frame extractions, filtering, and more
    * Lots of documentation and tutorials available
* Cons
    * Requires native binaries (have to install additional resources)
    * More complicated setup due to native dependency management and configuration
    * Adds size to deployment
    * Licensing conserns
    * Platfrom specific issues (packaging and builds)
* Estimate for 8 min video speed process: 0.5-1.2 mins for native, 20 -30 secs for FFmpeg w/ GPU accel
