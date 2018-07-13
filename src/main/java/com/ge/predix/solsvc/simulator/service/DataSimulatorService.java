package com.ge.predix.solsvc.simulator.service;

import com.ge.predix.entity.simulator.Tag;
import com.ge.predix.solsvc.simulator.thread.TagThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 
 * @author developer relations -
 */
@Service
public class DataSimulatorService
{
    private static Logger          log       = LoggerFactory.getLogger(DataSimulatorService.class);

    private Object                 lock      = new Object();

    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private TaskExecutor           taskExecutor;

    @Autowired
    private ApplicationContext     applicationContext;

    private Map<String, Future<?>> futureMap = new HashMap<String, Future<?>>();

    /**
     * Start Simulation
     * 
     * @param tag -
     * @param simulationName -
     */
    @Async
    public void startSimulation(Tag tag, String simulationName)
    {
        log.debug("In executeAsynchronously scheduling a thread"); //$NON-NLS-1$

        TagThread tagThread = this.applicationContext.getBean(TagThread.class, tag);
        ThreadPoolTaskExecutor threadPool = (ThreadPoolTaskExecutor) this.taskExecutor;
        Future<?> ft = threadPool.submit(tagThread);
        this.getFutureMap().put(simulationName.toLowerCase(), ft);
        // this.taskExecutor.execute(tagThread);
        if ( log.isDebugEnabled() )
        {
            log.debug("****** Thread pool details -startSimulation *****"); //$NON-NLS-1$
            printTaskDetails();
        }
    }

    /**
     * Stop the simulation
     * 
     * @param simulationName -
     */
    @SuppressWarnings("nls")
    @Async
    public void stopSimulation(String simulationName)
    {
        log.debug("In stopSimulation stop a thread"); //$NON-NLS-1$
        Future<?> ft = this.getFutureMap().get(simulationName.toLowerCase());
        if ( ft == null )
        {
            log.info("No Task with name " + simulationName);
        }
        else
        {
            // stop the execution of thread
            log.info("Now going to stop the simulator with name " + simulationName);
            ft.cancel(true);
        }
        this.getFutureMap().remove(simulationName.toLowerCase());
        if ( log.isDebugEnabled() )
        {
            log.debug("****** Thread pool details -stopSimulation*****"); //$NON-NLS-1$
            printTaskDetails();
        }

    }

    /**
     * -
     */
    private void printTaskDetails()
    {
        log.debug("Printing TaskExector details"); //$NON-NLS-1$
        ThreadPoolTaskExecutor threadPool = (ThreadPoolTaskExecutor) this.taskExecutor;
        log.debug("Total Active Threads = " + threadPool.getActiveCount());  //$NON-NLS-1$

    }

    /**
     * @return the futureMap
     */

    public Map<String, Future<?>> getFutureMap()
    {
        synchronized (this.lock)
        {
            return this.futureMap;
        }
    }

    /**
     * @param futureMap the futureMap to set
     */
    public void setFutureMap(Map<String, Future<?>> futureMap)
    {
        synchronized (this.lock)
        {
            this.futureMap = futureMap;
        }
    }

}
