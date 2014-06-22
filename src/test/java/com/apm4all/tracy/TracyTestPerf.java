package com.apm4all.tracy;
import java.util.List;

import org.databene.contiperf.*;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

@PerfTest(threads=1, duration=5000, rampUp = 100)
public class TracyTestPerf {
	private static final Logger logger = (Logger) LoggerFactory.getLogger(TracyTestPerf.class);
	static final String TASK_ID = "TID-ab1234-x";
	static final String PARENT_OPT_ID = "AAAA";
	static final String L1_LABEL_NAME = "L1 Operation";
	static final String L11_LABEL_NAME = "L11 Operation";
	@Rule
	public ContiPerfRule i = new ContiPerfRule();

	@Required(average = 1, percentile99=1, max = 20)
	@Test
	public void testQuadEventAndAnnotationTracePerformance() {
		Tracy.setContext();
		Tracy.before("L1");
		  Tracy.annotate("L1Key", "L1Value");
          Tracy.before("L11");
		  Tracy.annotate("L11Key", "L11Value");
		  Tracy.after("L11");
		  Tracy.before("L12");
		  Tracy.annotate("L12Key", "L12Value");
		  Tracy.after("L12");
		  Tracy.before("L13");
		  Tracy.annotate("L13Key", "L13Value");
		  Tracy.after("L13");
		Tracy.after("L1");
		List<TracyEvent> events = Tracy.getEvents();
		//TODO: Test impact of logging using a logging framework 
		for (TracyEvent event : events)	{
            logger.info(event.toString());
//			System.out.println(event.toString());
		}
	}
}
