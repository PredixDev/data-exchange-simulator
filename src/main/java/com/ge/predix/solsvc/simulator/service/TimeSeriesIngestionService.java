/*
 * Copyright (c) 2018 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.simulator.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.simulator.Range;
import com.ge.predix.entity.simulator.Tag;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.simulator.types.Constants;
import com.ge.predix.solsvc.websocket.client.WebSocketClient;
import com.ge.predix.solsvc.websocket.config.IWebSocketConfig;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;


/**
 * 
 * @author developer relations -
 */
@Service
@Scope("prototype")
@ImportResource(
{
  "classpath*:META-INF/spring/predix-websocket-client-scan-context.xml"    
})
public class TimeSeriesIngestionService {
	private static Logger log = LoggerFactory.getLogger(TimeSeriesIngestionService.class);
	@Autowired
	private JsonMapper mapper;

	@Autowired
	private WebSocketClient webSocketClient;

	@Autowired
	@Qualifier("tsWebSocketConfig")
	private IWebSocketConfig apmTsWebsocketConfig;


	private WebSocketAdapter messageListener = new WebSocketAdapter() {
		@SuppressWarnings("synthetic-access")
		@Override
		public void onTextMessage(WebSocket wsocket, String message) {
			log.info("RECEIVED...." + message); // $$ //$NON-NLS-1$
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void onBinaryMessage(WebSocket wsocket, byte[] binary) {
			String str = new String(binary, StandardCharsets.UTF_8);
			log.info("RECEIVED...." + str); // $$ //$NON-NLS-1$
		}
	};

	@SuppressWarnings("javadoc")
	@PostConstruct
	public void init() {
		if (this.apmTsWebsocketConfig.getWsUri() != null && this.apmTsWebsocketConfig.getWsUri().startsWith("wss://")) { //$NON-NLS-1$
			this.webSocketClient.overrideWebSocketConfig(this.apmTsWebsocketConfig);
			this.webSocketClient.init(null, this.messageListener);
		}
		this.mapper.addSubtype(PutFieldDataRequest.class); // ??
	}

	/**
	 * @param data
	 *            -
	 */
	@SuppressWarnings("nls")
    public void sendToTimeseries(String data) {
		
	    try {
			log.debug("Send To Time Series "+ data);
            this.webSocketClient.postTextWSData(data);
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
        }
	}

	/**
	 * @param simulatorTag -
	 * @param range -
	 * @param timeForData -
	 * @return -
	 */
	public Body createTimeseriesIngestionRequest(Tag simulatorTag, Range range, long timeForData) {
		double randomValue = generateRandomUsageValue(range.getLowerThreshold(),
						range.getUpperThreshold());
		return createTimeseriesRequestBody(simulatorTag,randomValue,timeForData);

	}
	

    /**
     * @param tag -
     * @param lastdPTime -
     * @return currentTime
     */
    public long getDatapointTime(Tag tag, long lastdPTime) {
        long currentTime = 0l;
        if (lastdPTime == 0 && tag.getStart() == 0) {
            currentTime = Instant.now().toEpochMilli();
        } else if (lastdPTime != 0 && tag.getStart() == 0) {
            Date date = new Date(lastdPTime);
            currentTime = (date.getTime() + TimeUnit.SECONDS.toMillis(tag.getInterval()));
        } else if (tag.getStart() != 0) {
            Date date = new Date(tag.getStart());
            currentTime = (date.getTime());
        }
        return currentTime;
    }

	
	private double generateRandomUsageValue(double low, double high) {
	    return low + Math.random() * (high - low);
	}
	
	   /**
    *
    * @param tag
    * @param dpValue
    * @param dPTime
    * @return
    */
   private Body createTimeseriesRequestBody(Tag tag, double dpValue, long dPTime)
   {
       Body body = new Body();
       String separator = tag.getAssetNodeSeparator() == null ? Constants.DEFAULT_ASSETID_NODENAME_SEPARATOR : tag.getAssetNodeSeparator();
       body.setName(tag.getAssetId() + separator + tag.getNodeName());
       List<Object> datapoints = new ArrayList<Object>();
       datapoints.add(getDatapoint(dPTime, dpValue));

       body.setDatapoints(datapoints);
       body.setAttributes(new com.ge.predix.entity.util.map.Map());
      return body;
   }
   
   /**
    * @param currentTime
    * @param randomeValue
    * @return -
    */
   private List<Object> getDatapoint(long currentTime, double randomeValue) {
       List<Object> datapoint = new ArrayList<Object>();
       datapoint.add(currentTime);
       datapoint.add(randomeValue);
       return datapoint;
   }
}
