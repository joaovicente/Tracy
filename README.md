[![Build Status](https://travis-ci.org/joaovicente/Tracy.svg)](https://travis-ci.org/joaovicente/Tracy)
[![Coverage Status](https://coveralls.io/repos/joaovicente/Tracy/badge.svg?branch=master)](https://coveralls.io/github/joaovicente/Tracy)
[![][mavenbadge img]][mavenbadge]
[mavenbadge]:http://search.maven.org/#search|gav|1|g%3A%22com.apm4all.tracy%22%20AND%20a%3A%22Tracy%22
[mavenbadge img]:https://maven-badges.herokuapp.com/maven-central/com.apm4all.tracy/Tracy/badge.svg

# What is Tracy <img src="https://cloud.githubusercontent.com/assets/3578589/13300175/bad4d3c6-db36-11e5-94a4-aa4a9c3a9dbc.gif" width="50"/> ?
Tracy is an instrumentation library designed to capture Java code execution paths by capturing discrete transaction processing, telemetry events containing timing, call-flow-ancestry and host identification, along with any other context a developer would like to capture.

# Why does Tracy exist?
* I want to understand the performance characteristics of web applications at each tier/service through analysis of discrete transaction telemetry events.
* I want be able to record transaction metadata at any tier so I can then do ad-hoc analysis to better understand the effect user actions have on the system.
* I believe an Open Source APM core framework can provide standardized building blocks enabling an engineering team to gain insight into the dynamics of their system.
* I believe that with a standardized Open Source APM core, engineering teams can more easily build their own tools for increased application insight and analytics.

## Architecture
The Tracy library is part of a suite of components which make the Tracy framework. The Tracy framework objective is to capture telemetry in a manner such that it can be used to measure and analyze transactions passing through a system.

### Sample system

Consider a system made up of:
* Gateway
* Service 1
* Service 2

#### System flow
The diagram below depicts a Task (distinct user interaction with the system) being executed in the system.
A Task is defined by a discrete user interaction with the system with the intent to produce a result. In a HTTP context, it starts with a user Request and ends with a Response, including all of its inter-system interactions executed in the context of the request.

<img src="https://cloud.githubusercontent.com/assets/3578589/19279707/6c2e944c-8fda-11e6-8986-2ea9969fd22f.png">

To understand the telemetry event model of Tracy it is important to understand the following concepts:
* `TracyFrame:` Telemetry (JSON) event containing identification of the Task, Component, Operation (e.g. class method name), Host and method before/after timing information.
```javascript
  {
    "taskId":"T-1234",
    "label":"gwServerMethod",
    "optId":"AAAA",
    "parentOptId":"0000",
    "msecBefore":1427817826500,
    "msecAfter" :1427817826620,
    "msecElapsed":120
  }
```
* `taskId:` Correlation identifier of a TracyTask (spanning multiple hosts/JVMs). taskId MUST be unique (i.e. must not be re-used for more than one Task)
* `label:` Identifier which will be used to identify the Operation (e.g. Java method)
* `optId:` Operation identifier, which identifies segment of code execution. optId is automatically generated by the Tracy library (MUST be unique within a taskId)
* `parentOptId:` The optId of the ancestor (calling method optId if in the same JVM, otherwise RPC originating method on the client JVM). parentOptId is the key to organize TracyFrames into a Graph.

These concepts become clearer when we look at a TracyTask as a graph for the sample system described above

<img src="https://cloud.githubusercontent.com/assets/3578589/19279708/6c3a5bba-8fda-11e6-973e-ea964b5b5b3c.png">


The following code snippets illustrate how to inline instrument to produce the TracyFrames for the _Gateway_ component shown above.


_Context initialisation code:_
```java
public void servletInitialisationMethod() {
    Tracy.setContext("T-1234", "0000", "Gateway");
    Tracy.before("gwServerMethod");
    // Do work
    Tracy.after("gwServerMethod");
    for (String event : Tracy.getEventsAsJson())  {
      bh.consume(event);
    }
    Tracy.clearContext();
}
```

_Method instrumentation code:_
```java
public void gwServerMethod() {
  Tracy.before("gwServerMethod");
  // Do work
  gwClientMethod();
  Tracy.after("gwServerMethod");
}

public void gwClientMethod() {
  Tracy.before("gwClientMethod");
  // Calling Service1 here
  Tracy.after("gwClientMethod");
}
```

_Context termination code:_
```java
public void servletFinalizationMethod() {
    for (String event : Tracy.getEventsAsJson())  {
      System.out.println(event);
    }
    Tracy.clearContext();
}
```

If you want something a little more hands-on have a look at the Tracy simulator code in [hello-tracy project](https://github.com/joaovicente/hello-tracy/blob/master/src/main/java/com/apm4all/tracy/HelloTracy.java) or [TracyTest.java](https://github.com/joaovicente/Tracy/blob/develop/src/test/java/com/apm4all/tracy/TracyTest.java)

Another visualization which is particularly useful for performance analysis is the timeline

<img src="https://cloud.githubusercontent.com/assets/3578589/19279706/6c2ab480-8fda-11e6-9c42-f78918f8965a.png">

## Going further with Tracy
Peer Open Source projects
 [tracy-web-services](https://github.com/joaovicente/tracy-web-services) and [tracy-ui](https://github.com/joaovicente/tracy-ui) (screenshot below) then provide backend and visualization from Tracy events, including application performance statistics and drill-downs.
<br><br>
<img src="https://cloud.githubusercontent.com/assets/3578589/16894054/00a37962-4b43-11e6-9839-296e41d4e635.png" width="800"/>

For more information on how it all comes together in your development environment using Docker see https://github.com/joaovicente/tracy-demo
## Version history ##

### 4.0.0 ###
* Introduced `Tracy.annotateRoot()` to allow annotations straight throught to the  root Tracy frame
* Introduced `@Profiled` and `@RequestProfiling` interfaces kindly donated by [Jakub Staš](https://github.com/JakubStas)

### 3.5.3 ###
* Fixed [Issue #7](https://github.com/joaovicente/Tracy/issues/7) `Tracy.setContext() ` with null parenOptId  
* Extended test coverage for Tracy.frameError... methods when different errors are raised at 2 frame depth levels   

### 3.5.2 ###
* Extending [Issue #3](https://github.com/joaovicente/Tracy/issues/3) to handle backslash 

### 3.5.1 ###
* Handling annotation values containing double quotes as per [Issue #3](https://github.com/joaovicente/Tracy/issues/3) 

### 3.5.0 ###
* Implemented `Tracy.frameErrorWithoutPopping(String error) `

### 3.4.0 ###
* Implemented `Tracy.annotate(String name, float value) ` and `Tracy.annotate(String name, double value) `

### 3.3.1 ###
* Improved hostname resolution robustess

### 3.3.0 ###
* `getHttpResponseBufferAnnotations() ` now returning annotations in CSV format
* Implemented `Tracy.annotate(String name, boolean value) `

### 3.2.2 ###
* Making Tracy.HTTP_HEADER_X_TRACY_ANNOTATIONS public

### 3.2.1 ###
* getHttpResponseBufferAnnotations() now returns an empty string instead of null if no there are no annotations to retrieve  

### 3.2.0 ###
* Added `annotateOnHttpResponseBuffer(String key)` and `getHttpResponseBufferAnnotations()` implementation allowing to send annotations to Client via `X-Tracy-Annotations ` HTTP header
* Added `annotateFromHttpRequestAnnotations()` to facilitate annotating annotations received from the Client in the `X-Tracy-Annotations` HTTP header (currently only supporting string annotations in CSV format e.g. `key1,val1,key2,val2`)

### 3.1.0 ###
* Tracy.getEventsAsJsonTracySegment() introduced to support tracy-publisher `{"tracySegment":[{"taskId":"TID-ab1234-x"`...

### 3.0.0 ###
* Tracy to respect annotation type in JSON Tracy representation `{"taskId":"ABCDE","label":"myCall","optId":"BED1","msecBefore":1427817826607,"msecAfter":1427817826608,"msecElapsed":1, "myAge":43...}` instead of `{"taskId":"ABCDE","label":"myCall","optId":"BED1","msecBefore":"1427817826607","msecAfter":"1427817826608","msecElapsed":"1", "myAge":"43"...}`

### 2.5.0 ###
* Tracy.annotate("anyKey", null) to create a {"anyKey": "null"} annotation
* Tracy.setTaskId(String myFinalTaskId)
* Tracy.getEventsAsJsonArray()

### 2.4.1 ###
* Added TracyEvent.getAnnotation(key)

### 2.4.0 ###
* Removed legacy TracyFuture implementation and tests (new implementation illustrated in TracyConcurrentTest)
* To remove ambiguity, now only supporting one Tracy.context() with three string parameters: TaskId (null generates a random one), parentOptId, componentName

### 2.3.0 ###
* Implemented Tracy.annotate(String longName, long longValue)
* In case where an exception is thown `Tracy.frameError(String errorString)` can be used to close Tracy stack frame with a `errorString` error.
* In case where an exception is thown `Tracy.outerError(String errorString)` can be used to close all Tracy frames with a a generic error and the outer frame with `errorString`.
* Fixed issue where when annotate was called before first Tracy.before() an java.util.EmptyStackException. Tracy now silently ignores the annotation() call

### 2.2.0 ###
* Improved multi-threading support for `java.util.concurrent` using TracyCallable (composite extension of `java.util.concurrent.Callable`) and TracyFutureTask as an extension of `java.util.concurrent.FutureTask`. See `TracyConcurrentTest.java` for usage

### 2.1.0 ###
* Produce `"error"="Unknown"` when Tracy event is not-aftered
* Support for `component` label

### 2.0.2 ###
* Implemented Tracy.annotate(String intName, int intValue) for user convenience
* Allow for user defined optIds - Tracy.setOptId("U001")
* Add safety upon exception thrown unit test
* Improved Tracy API protection against NullPointerException when context is not initialised

### 2.0.1 ###
* Upgrading to junit version 4.11

### 2.0.0 ###
* Improved Futures API by introducing TracyableFutureRequest and TracyableFutureResponse

### 1.2.0 ###
* Added Tracy event output as JSON using Tracy.getEventsAsJson()

### 1.1.0 ###
* Disable tracing if Tracy.setContext() is not called

### 1.0.0 ###
* Initial release - Performance tested. 99th percentile latency below 1ms for 9 event Tracy
