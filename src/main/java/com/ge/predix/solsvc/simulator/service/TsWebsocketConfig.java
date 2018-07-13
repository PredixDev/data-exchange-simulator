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

import com.ge.predix.solsvc.restclient.config.DefaultOauthRestConfig;
import com.ge.predix.solsvc.websocket.config.IWebSocketConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 
 * @author 212421693 -
 */
@Component("tsWebSocketConfig")
public class TsWebsocketConfig extends DefaultOauthRestConfig
implements IWebSocketConfig
{

    @Value("${predix.timeseries.websocket.uri}")
    private String             wsUri;

    @Value("${predix.timeseries.zoneid.header:Predix-Zone-Id}")
    private String             zoneIdHeader;
    
    @Value("${predix.timeseries.zoneid:#{null}}")
    private String             zoneId;

    @Value("${predix.timeseries.websocket.pool.maxIdle:5}")
    private int                wsMaxIdle;

    @Value("${predix.timeseries.websocket.pool.maxActive:5}")
    private int                wsMaxActive;

    @Value("${predix.timeseries.websocket.pool.maxWait:8000}")
    private int                wsMaxWait;
    
    
/* (non-Javadoc)
 * @see com.ge.predix.solsvc.websocket.config.IWebSocketConfig#getWsUri()
 */
@Override
public String getWsUri()
{
    return this.wsUri;
}

/* (non-Javadoc)
 * @see com.ge.predix.solsvc.websocket.config.IWebSocketConfig#setWsUri(java.lang.String)
 */
@Override
public void setWsUri(String wsUri)
{
    this.wsUri = wsUri;
    
}

/* (non-Javadoc)
 * @see com.ge.predix.solsvc.websocket.config.IWebSocketConfig#getZoneId()
 */
@Override
public String getZoneId()
{
   return this.zoneId;
}

/* (non-Javadoc)
 * @see com.ge.predix.solsvc.websocket.config.IWebSocketConfig#getWsMaxIdle()
 */
@Override
public int getWsMaxIdle()
{
   return this.wsMaxIdle;
}

/* (non-Javadoc)
 * @see com.ge.predix.solsvc.websocket.config.IWebSocketConfig#getWsMaxActive()
 */
@Override
public int getWsMaxActive()
{
   return this.wsMaxActive;
}

/* (non-Javadoc)
 * @see com.ge.predix.solsvc.websocket.config.IWebSocketConfig#getWsMaxWait()
 */
@Override
public int getWsMaxWait()
{
   return this.wsMaxWait;
}

/* (non-Javadoc)
 * @see com.ge.predix.solsvc.websocket.config.IWebSocketConfig#getZoneIdHeader()
 */
@Override
public String getZoneIdHeader()
{
    return this.zoneIdHeader;
}
}
