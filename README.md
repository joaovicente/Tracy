# Tracy #
A simple tracing framework inspired by XTrace.  
This library is designed to capure Java code execution paths by capturing discrete events containing ancestry, timing, host information, along with any additional optional context a developer wants to capture.  

## Terminology
* `taskId` is the correlation identifier of a trace (possibly spanning multiple runtimes)
* `parentId` is the id of the ancestor Tracy event in a Tracy graph
* `optId` is the operation id, which identifies segment of code execution (e.g. a method call). optId is automatically generated by the Tracy library

## Description
The following example illustrates how to use Tracy.  
In this example we have a nested trace starting with a Marge object instance who calls homer, who in turn calls bart.  
Both homer and bart calls are wrapped with a `Tracy.before()` and `Tracy.after()` event so the time elapsed can be measured.  
Additionally homer adds some context to his Tracy event by annotating the trace event using `Tracy.annotate()` with a key="Says" and value="Doh!"  
bart in turn adds two key value pairs to his Tracy event, key1="Says" value1="Caramba!", key2="AlsoSays", value2="Eat my Shorts!"
If you look at the output produced you will also see homer is the parent of bart, given bart `parentId` equals homer `optId`. This will provide suffcient information to create a Direct Acyclic Graph (DAG) from the Tracy events.  
In this example the taskId and parentOptId are randomly generated by Marge, but they could be passed in using `Tracy.setContex(taskId, parentOptId)` to provide linkage to an external Tracy event outside of Marge context (e.g. Grandpa could have called Marge on the phone).

## Sample Tracy events:
```
"taskId"="55DEB4C8", "parentOptId"="B948", "label"="bart", "optId"="740F", "msecBefore"="1405634050915", "msecAfter"="1405634051415", "msecElapsed"="500", "AlsoSays"="Eat my shorts!", "host"="ukdb807735.local", "Says"="Caramba!"
"taskId"="55DEB4C8", "parentOptId"="ACAB", "label"="homer", "optId"="B948", "msecBefore"="1405634049913", "msecAfter"="1405634051415", "msecElapsed"="1502", "host"="ukdb807735.local", "Says"="Doh!"
```

## Usage:
```java
  package com.apm4all.tracy;
  
  import java.util.List;
  public class Marge {
      public void bart() throws InterruptedException {
          Tracy.annotate("Says", "Caramba!", "AlsoSays", "Eat my shorts!");
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

### 1.2.0 ###
* Added Tracy event output as JSON using Tracy.getEventsAsJson()

### 1.1.0 ###
* Disable tracing if Tracy.setContext() is not called

### 1.0.0 ###
* Initial release - Performance tested. 99th percentile latency below 1ms for 9 event Tracy
