[![Build Status](https://travis-ci.org/joaovicente/Tracy.svg?branch=2.0.1)](https://travis-ci.org/joaovicente/Tracy)
<!---
[![Coverage Status](https://img.shields.io/coveralls/joaovicente/Tracy.svg)](https://coveralls.io/r/joaovicente/Tracy)
-->
# Tracy #
A simple tracing framework inspired by XTrace.  
This library is designed to capure Java code execution paths by capturing discrete events containing ancestry, timing, host information, along with any additional optional context a developer wants to capture.  

## Terminology
* `taskId` is the correlation identifier of a trace (possibly spanning multiple runtimes)
* `parentId` is the id of the ancestor Tracy event in a Tracy graph
* `optId` is the operation id, which identifies segment of code execution (e.g. a method call). optId is automatically generated by the Tracy library

## Description
The following example illustrates how to use Tracy.  
In this example we have a nested trace starting with a Marge object instance who calls homer, who in turn calls Bart.  
Both homer and bart calls are wrapped with a `Tracy.before()` and `Tracy.after()` event so the time elapsed can be measured.  
Additionally homer adds some context to his Tracy event by annotating the trace event using `Tracy.annotate()` with a key="Says" and value="Doh!"  
bart in turn adds two key value pairs to his Tracy event, key1="Says" value1="Caramba!", key2="AlsoSays", value2="Cowabunga!"
If you look at the output produced you will also see homer is the parent of bart, given bart `parentId` equals homer `optId`. This will provide suffcient information to create a Direct Acyclic Graph (DAG) from the Tracy events.  
In this example the taskId and parentOptId are randomly generated by Marge, but they could be passed in using `Tracy.setContext(taskId, parentOptId)` to provide linkage to an external Tracy event outside of Marge context (e.g. Grandpa could have called Marge on the phone).

## Sample Tracy events:
```
"taskId"="55DEB4C8", "parentOptId"="B948", "label"="bart", "optId"="740F", "msecBefore"="1405634050915", "msecAfter"="1405634051415", "msecElapsed"="500", "AlsoSays"="Cowabunga!!", "host"="ukdb807735.local", "Says"="Caramba!"
"taskId"="55DEB4C8", "parentOptId"="ACAB", "label"="homer", "optId"="B948", "msecBefore"="1405634049913", "msecAfter"="1405634051415", "msecElapsed"="1502", "host"="ukdb807735.local", "Says"="Doh!"
```

## Usage:
```java
  package com.apm4all.tracy;
  
  import java.util.List;
  public class Marge {
      public void bart() throws InterruptedException {
          Tracy.annotate("Says", "Caramba!", "AlsoSays", "Cowabunga!!");
          Thread.sleep(500);
      }
      public void homer() throws InterruptedException {
          Tracy.annotate("Says", "Doh!");
          Thread.sleep(1000);
          Tracy.before("bart");
          bart();
          Tracy.after("bart");
      }
      public static void main(String[] args) throws InterruptedException {
          Marge marge = new Marge(); 
          Tracy.setContext();
          Tracy.before("homer");
          marge.homer();
          Tracy.after("homer");
          List<TracyEvent> events = Tracy.getEvents();
          for (TracyEvent event : events) {
              System.out.println(event.toString());
          }
      }
  }
```
## Version history ##

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
