package com.ge.predix.solsvc.simulator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.ge.predix.solsvc.simulator.boot.DataExchangeSimulatorApplication;

/**
 * 
 * @author 212546387 -
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DataExchangeSimulatorApplication.class)
@WebAppConfiguration
@IntegrationTest
@PropertySource(value="file:config/application.properties")
@ImportResource({
	"classpath*:META-INF/spring/ext-util-scan-context.xml",
	"classpath*:META-INF/spring/predix-websocket-client-scan-context.xml"})
public class DataExchangeSimulatorApplicationIT {
	
	@Autowired
	private ScheduledDataExchangeSimulator simulator;
	
	/**
	 *  -
	 */
	@Test
	public void testSimulator() {
		this.simulator.simulateData();
	}

	
}
