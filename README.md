# Tracy #

A simple tracing framework inspired by XTrace

## Version history ##
### 1.1.0
* Disable tracing if Tracy.setContext() is not called (ensuring worker context API is also safe to use)
### 1.0.0 ###
* Initial release - Performance tested. 99th percentile latency below 1ms for 9 event Tracy
