/*
 * Copyright (c) 2018 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.simulator.thread;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.entity.simulator.Range;
import com.ge.predix.entity.simulator.Tag;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.simulator.service.TimeSeriesIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//import com.ge.predix.entity.simulator.Pattern;

/**
 * 
 * @author 212421693 -
 */
@Component
@Scope("prototype")
public class TagThread
        implements Runnable
{
    private static Logger      log          = LoggerFactory.getLogger(TagThread.class);
    /**
     * 
     */
    Tag                        tag;

    /**
     * We need a single ingestionService for each tag
     */
    @Autowired
    TimeSeriesIngestionService ingestionSerivce;

    private ObjectMapper       objectMapper = new ObjectMapper();

    /**
     * @param tag -
     */
    public TagThread(Tag tag)
    {
        super();
        this.tag = tag;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        long threadId = Thread.currentThread().getId();
        String threadName = Thread.currentThread().getName();
        log.info("Thread # " + threadId + " is doing this task as " + threadName); //$NON-NLS-1$ //$NON-NLS-2$
        log.debug("This is run method from the thread" + this.tag.getNodeName()); //$NON-NLS-1$
        postData(this.tag);

    }

    /**
     * 
     * @param simulatorTag -
     */
    public void postData(Tag simulatorTag)
    {
        log.debug("Posting data ... "); //$NON-NLS-1$
        int counter = 0;

        try
        {
            // this inputs are in seconds
            long startTime = TimeUnit.SECONDS.toMillis(simulatorTag.getStart());
            if ( startTime <= 0 )
            {
                Date currenrtDateTime = new Date();
                startTime = currenrtDateTime.getTime();
            }
            long interval = TimeUnit.SECONDS.toMillis(simulatorTag.getInterval());
            if ( interval <= 0 )
            {
                interval = TimeUnit.SECONDS.toMillis(5);
            }
            // Continuous flow of data
            while (!Thread.currentThread().isInterrupted())
            {
                log.debug("...#pattern repeat=" + counter++); //$NON-NLS-1$
                String data;

                for (Range range : simulatorTag.getRange())
                {

                    long rangeDuration = TimeUnit.SECONDS.toMillis(range.getDuration());// duration is in seconds

                    for (long duration = 0l; duration <= rangeDuration;)
                    {

                        DatapointsIngestion dpIngestion = new DatapointsIngestion();
                        dpIngestion.setMessageId(UUID.randomUUID().toString());
                        List<Body> bodies = new ArrayList<Body>();
                        Body dpIngestionBody = this.ingestionSerivce.createTimeseriesIngestionRequest(simulatorTag,
                                range, startTime);
                        bodies.add(dpIngestionBody);
                        // log.debug("Body = "+dpIngestionBody.getDatapoints());
                        dpIngestion.setBody(bodies);
                        data = this.objectMapper.writeValueAsString(dpIngestion);
                        this.ingestionSerivce.sendToTimeseries(data);
                        Thread.sleep(interval);
                        startTime += interval;
                        duration += interval;
                    }
                }

            }

        }
        catch (JsonProcessingException | InterruptedException e)
        {
            throw new RuntimeException("unable to simulate data for tag", e); //$NON-NLS-1$
        }

    }

}
