/*
 * Copyright (c) 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.simulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.entity.field.Field;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.simulator.types.SimulatorDataNode;
import com.ge.predix.solsvc.websocket.client.WebSocketClient;
import com.ge.predix.solsvc.websocket.client.WebSocketClientImpl;
import com.ge.predix.solsvc.websocket.config.DefaultWebSocketConfigForTimeseries;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

/**
 * 
 * @author predix.adoption@ge.com -
 */
@Component
public class ScheduledDataExchangeSimulator {
	/**
	 * 
	 */
	static Logger log = LogManager.getLogger(ScheduledDataExchangeSimulator.class);
	/**
	 * 
	 */
	private static List<SimulatorDataNode> nodeList = new ArrayList<SimulatorDataNode>();

	@Autowired
	private JsonMapper mapper;
	
	private ObjectMapper objectMapper = new ObjectMapper();

	private WebSocketClient webSocketClient = new WebSocketClientImpl();
	
	@Autowired
	private DefaultWebSocketConfigForTimeseries defaultWebsocketConfig;
	
	@Autowired
	private RestClient restClient;
	
	@Value("${predix.timeseries.websocket.uri}")
	private String serviceURL;
	 /**
     * 
     */
    private WebSocketAdapter messageListener = new WebSocketAdapter()
                                             {
                                                 @Override
                                                 public void onTextMessage(WebSocket wsocket, String message)
                                                 {
                                                     log.info("RECEIVED...." + message);                                           // $$ //$NON-NLS-1$
                                                 }

                                                 @Override
                                                 public void onBinaryMessage(WebSocket wsocket, byte[] binary)
                                                 {
                                                     String str = new String(binary, StandardCharsets.UTF_8);
                                                     log.info("RECEIVED...." + str);                                                   // $$ //$NON-NLS-1$
                                                 }
                                             };
	/**
	 * -
	 */
	@PostConstruct
	public void init() {
		nodeList = new ArrayList<SimulatorDataNode>(10);
		if (this.serviceURL != null && this.serviceURL.startsWith("wss://")) { //$NON-NLS-1$
		    this.webSocketClient.overrideWebSocketConfig(this.defaultWebsocketConfig);
		    this.webSocketClient.init(this.restClient, null, this.messageListener);
		}
		this.mapper.addSubtype(PutFieldDataRequest.class);
	}

	/**
	 * -
	 */
	@Scheduled(fixedDelay = 3000)
	public void simulateData() {
		try {
			if (nodeList.isEmpty()) {
				InputStream fis = null;
				try {
					log.info("NodeList empty reading from file"); //$NON-NLS-1$
					StringWriter writer = new StringWriter();
					fis = this.getClass().getClassLoader().getResourceAsStream("simulatorconfig.json"); //$NON-NLS-1$
					IOUtils.copy(fis, writer, "UTF-8"); //$NON-NLS-1$
					String simulatorConfig = writer.toString();
					log.info(simulatorConfig);
					nodeList = this.mapper.fromJsonArray(simulatorConfig, SimulatorDataNode.class);
					log.info("NodeList From File: " + nodeList.size()); //$NON-NLS-1$
				} finally {
					try {
						if (fis != null)
							fis.close();
					} catch (IOException e) {
						log.error("unable to close InputStream", e); //$NON-NLS-1$
						// swallow exception
					}
				}
			} else {
				log.info("NodeList size : " + nodeList.size()); //$NON-NLS-1$
			}
			//PutFieldDataRequest putFieldDataRequest = createDatapointsIngestion(nodeList);
			//log.info(putFieldDataRequest);
			//postData(mapper.toJson(putFieldDataRequest));
			
			if (this.serviceURL != null && this.serviceURL.startsWith("https://")) { //$NON-NLS-1$
			    String data = this.objectMapper.writeValueAsString(createDatapointsIngestion(nodeList));
	            log.info("Data : "+data); //$NON-NLS-1$
			    postDataRest(data);
			}else if (this.serviceURL != null && this.serviceURL.startsWith("wss://")) { //$NON-NLS-1$
			    String data = this.objectMapper.writeValueAsString(createTimeseriesDataBody(nodeList));
	            log.info("Data : "+data); //$NON-NLS-1$
	            
			    postData(data);
			}
		} catch (Throwable e) {
			log.error("unable to simulate data for nodelist=" + nodeList, e); //$NON-NLS-1$
			throw new RuntimeException("unable to simulate data for nodelist=" + nodeList, e); //$NON-NLS-1$
		}

	}

	private PutFieldDataRequest createDatapointsIngestion(List<SimulatorDataNode> aNodeList) {
		DatapointsIngestion datapointsIngestion = createTimeseriesDataBody(aNodeList);
		PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
		PutFieldDataCriteria criteria = new PutFieldDataCriteria();
		FieldData fieldData = new FieldData();
		Field field = new Field();
		FieldIdentifier fieldIdentifier = new FieldIdentifier();

		fieldIdentifier.setSource(FieldSourceEnum.PREDIX_TIMESERIES.name());
		field.setFieldIdentifier(fieldIdentifier);
		List<Field> fields = new ArrayList<Field>();
		fields.add(field);

		fieldIdentifier.setSource("handler/webSocketHandler"); //$NON-NLS-1$
        field.setFieldIdentifier(fieldIdentifier);
        fields.add(field);

		fieldData.setField(fields);

		fieldData.setData(datapointsIngestion);
		criteria.setFieldData(fieldData);
		List<PutFieldDataCriteria> list = new ArrayList<PutFieldDataCriteria>();
		list.add(criteria);
		putFieldDataRequest.setPutFieldDataCriteria(list);
		
		return putFieldDataRequest;
	}

	private DatapointsIngestion createTimeseriesDataBody(List<SimulatorDataNode> aNodeList) {
		DatapointsIngestion dpIngestion = new DatapointsIngestion();
		dpIngestion.setMessageId(UUID.randomUUID().toString());
		List<Body> bodies = new ArrayList<Body>();
		// log.info("NodeList : " + this.mapper.toJson(aNodeList));
		for (SimulatorDataNode node : aNodeList) {
			Body body = new Body();
			List<Object> datapoints = new ArrayList<Object>();
			body.setName(node.getAssetId() + ":" + node.getNodeName()); //$NON-NLS-1$

			List<Object> datapoint = new ArrayList<Object>();
			datapoint.add(getCurrentTimestamp());
			if (node.getLowerThreshold() == null)
				throw new UnsupportedOperationException(
						"lower threshold may not be null for nodeName=" + node.getNodeName()); //$NON-NLS-1$
			if (node.getUpperThreshold() == null)
				throw new UnsupportedOperationException(
						"upper threshold may not be null for nodeName=" + node.getNodeName()); //$NON-NLS-1$
			datapoint.add(generateRandomUsageValue(node.getLowerThreshold(), node.getUpperThreshold()));
			datapoints.add(datapoint);

			body.setDatapoints(datapoints);
			bodies.add(body);
		}
		dpIngestion.setBody(bodies);

		return dpIngestion;
	}

	private void postData(String request) {
		try {
			this.webSocketClient.postTextWSData(request);
		} catch (IOException | WebSocketException e) {
			e.printStackTrace();
		}
	}

	private void postDataRest(String content) {
	    List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json")); //$NON-NLS-1$//$NON-NLS-2$
        headers.add(new BasicHeader("Content-Type", "application/json")); //$NON-NLS-1$//$NON-NLS-2$
        if (this.serviceURL != null) {
                log.info("Service URL : " + this.serviceURL + " Data : " + content); //$NON-NLS-1$ //$NON-NLS-2$
                try (CloseableHttpResponse response = this.restClient.post(this.serviceURL, content, headers);) {
                        log.info(
                                        "Send Data to Ingestion Service : Response Code : " + response.getStatusLine().getStatusCode()); //$NON-NLS-1$
                        String res = this.restClient.getResponse(response);
                       
                        if (response.getStatusLine().getStatusCode() == 200) {
                                log.info(
                                                "Simulator Successfully sent data to serviceURL=" + this.serviceURL + " Response : " + res); //$NON-NLS-1$ //$NON-NLS-2$
                        } else {
                                log.error("Simulator FAILED to send data to serviceURL=" + this.serviceURL + " Response : " + res); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                } catch (IOException e) {
                        throw new RuntimeException(e);
                }
        } else
                log.error("Simulator FAILED to send data, serviceURL is empty =" + this.serviceURL); //$NON-NLS-1$
}
	private Timestamp getCurrentTimestamp() {
		java.util.Date date = new java.util.Date();
		Timestamp ts = new Timestamp(date.getTime());
		return ts;
	}

	private static double generateRandomUsageValue(double low, double high) {
		return low + Math.random() * (high - low);
	}

	/**
	 * @return the nodeList
	 */
	public static List<SimulatorDataNode> getNodeList() {
		return ScheduledDataExchangeSimulator.nodeList;
	}

	/**
	 * @param nodeList
	 *            the nodeList to set
	 */
	public static void setNodeList(List<SimulatorDataNode> nodeList) {
		log.info("updating nodelist=" + nodeList); //$NON-NLS-1$
		// if ( 1 == 1 ) throw new RuntimeException("unable to update");
		// ScheduledDataExchangeSimulator.nodeList = nodeList;
	}
}
