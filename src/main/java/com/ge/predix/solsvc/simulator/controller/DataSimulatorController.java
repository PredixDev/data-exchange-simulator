package com.ge.predix.solsvc.simulator.controller;

import com.ge.predix.entity.simulator.Simulation;

import com.ge.predix.entity.simulator.Tag;
import com.ge.predix.entity.simulator.TagSet;
import com.ge.predix.solsvc.simulator.service.DataSimulatorService;
import com.ge.predix.solsvc.simulator.types.DataSimulatorResponse;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author developer relations -
 */
@RestController
public class DataSimulatorController
{
    private static Logger             log                = LoggerFactory.getLogger(DataSimulatorController.class);

    @Autowired
    private DataSimulatorService      dsAsynchronousService;

    private Map<String, List<String>> mapOfListOfThreads = new HashMap<>();

    /**
     * @return -
     */
    @SuppressWarnings("nls")
    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public String health()
    {
        return String.format("{\"status\":\"up\", \"date\": \" " + new Date() + "\"}");
    }

    /**
     * @param echo -
     * @return -
     */
    @SuppressWarnings("nls")
    @RequestMapping(value = "/echo", method = RequestMethod.GET)
    public String index(@RequestParam(value = "echo", defaultValue = "echo this text") String echo)
    {
        return "Greetings from Service";
    }

    /**
     * @return -
     */
    @SuppressWarnings({
            "nls", "unchecked"
    })
    @RequestMapping(value = "/simulations", method = RequestMethod.GET)
    public @ResponseBody DataSimulatorResponse getSimulations()
    {
        DataSimulatorResponse response = new DataSimulatorResponse();
        try
        {
            JSONObject obj = new JSONObject();

            obj.put("current-simulations", this.mapOfListOfThreads.keySet());
            response.setIsError(Boolean.FALSE);
            response.setResponseString(obj.toJSONString());
        }
        catch (Throwable e)
        {
            log.error("get simulations failed", e);
            response.setIsError(Boolean.TRUE);
            response.setErrorMessage(e.getMessage());
        }
        return response;   
    }
 
        /**
     * @param simulation -
     * @return -
     */
    @SuppressWarnings("nls")
    @RequestMapping(value = "/start-simulation", method = RequestMethod.POST)
    public @ResponseBody DataSimulatorResponse addTagSet(@RequestBody Simulation simulation)
    {
        DataSimulatorResponse response = new DataSimulatorResponse();
        List<String> listOfTagThreads = new ArrayList<>();
        if ( StringUtils.isEmpty(simulation.getName()) )
        {
            log.error("start simulation failed");
            response.setIsError(Boolean.TRUE);
            response.setErrorMessage("Simulation Name is empty");
        }
        TagSet tagSet = simulation.getTagSet();
        try
        {
            if ( tagSet != null )
            {

                for (Tag tag : tagSet.getTag())
                {
                    String tagName = simulation.getName() + tag.getAssetId() + "." + tag.getNodeName() + "-"
                            + tag.getSimulationType();
                    listOfTagThreads.add(tagName);
                    log.debug("calling AsynchronousService for start-simulation " + tagName);
                    this.dsAsynchronousService.startSimulation(tag, tagName);
                    response.setIsError(Boolean.FALSE);
                    response.setResponseString("Starting the simulation with name " + tagName);
                }
                this.mapOfListOfThreads.put(simulation.getName().toLowerCase(), listOfTagThreads);
            }

        }
        catch (Throwable e)
        {
            log.error("start simulation failed", e);
            response.setIsError(Boolean.TRUE);
            response.setErrorMessage(e.getMessage());
        }
        return response;
    }

    /**
     * @param simulationName -
     * @return -
     */
    @SuppressWarnings("nls")
    @RequestMapping(value = "/stop-simulation", method = RequestMethod.POST)
    public @ResponseBody DataSimulatorResponse stopSimulation(@RequestBody String simulationName)
    {
        DataSimulatorResponse response = new DataSimulatorResponse();

        try
        {
            List<String> listOfTagThreads = this.mapOfListOfThreads.get(simulationName.toLowerCase());
            if ( listOfTagThreads != null )
            {
                for (String tagThread : listOfTagThreads)
                {
                    log.debug("calling AsynchronousService for stop-simulation for " + tagThread); //$NON-NLS-1$
                    this.dsAsynchronousService.stopSimulation(tagThread);
                }
                response.setIsError(Boolean.FALSE);
                response.setResponseString("Stop Simulator Task submitted");
            }
            else
            {
                response.setResponseString("No task exists with name=" + simulationName.toLowerCase());
            }

        }
        catch (Throwable e)
        {
            log.error("stop simulation failed", e); //$NON-NLS-1$
            response.setIsError(Boolean.TRUE);
            response.setErrorMessage(e.getMessage());
        }
        return response;
    }

}
