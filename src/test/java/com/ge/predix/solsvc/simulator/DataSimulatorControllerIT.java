package com.ge.predix.solsvc.simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.entity.simulator.Simulation;
import com.ge.predix.solsvc.simulator.boot.DataExchangeSimulatorApplication;
import com.ge.predix.solsvc.simulator.types.DataSimulatorResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

/**
 * Spins up Spring Boot and accesses the URLs of the Rest apis
 * 
 * @author developer relations
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataExchangeSimulatorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DataSimulatorControllerIT
{

    @Value("${local.server.port}")
    private int              localServerPort;

    private URL              base;
    private ObjectMapper     objectMapper = new ObjectMapper();

    @Autowired
    private TestRestTemplate template;

    /**
     * @throws Exception
     *             -
     */
    @Before
    public void setUp()
            throws Exception
    {
        //
    }

    /**
     * 
     * @throws Exception
     *             -
     */
    @SuppressWarnings("nls")
    @Test
    public void getHello()
            throws Exception
    {
        this.base = new URL("http://localhost:" + this.localServerPort + "/echo");
        ResponseEntity<String> response = this.template.getForEntity(this.base.toString(), String.class);
        assertThat(response.getBody(), startsWith("Greetings from Service"));

    }

    /**
     * @throws Exception
     *             -
     */
    @SuppressWarnings("nls")
    // @Test
    public void startSimulation()
            throws Exception
    {
        this.base = new URL("http://localhost:" + this.localServerPort + "/start-simulation");
        Simulation simulation = SimulatorUtils.getSimualtion();
        HttpEntity<Simulation> request = new HttpEntity<>(simulation);
        @SuppressWarnings("unused")
        String data = this.objectMapper.writeValueAsString(simulation);
        ResponseEntity<DataSimulatorResponse> response = this.template.exchange(this.base.toString(), HttpMethod.POST,
                request, DataSimulatorResponse.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        DataSimulatorResponse DSresponse = response.getBody();
        assertThat(DSresponse, notNullValue());
        Thread.sleep(50000); // this is request othewise the application context
                             // shutdown

    }

    /**
     * @throws Exception
     *             -
     */
    @SuppressWarnings("nls")
    // @Test
    public void startLiteSimulation()
            throws Exception
    {
        this.base = new URL("http://localhost:" + this.localServerPort + "/start-simulation");
        Simulation simulation = SimulatorUtils.getSimualtionLite();
        HttpEntity<Simulation> request = new HttpEntity<>(simulation);
        @SuppressWarnings("unused")
        String data = this.objectMapper.writeValueAsString(simulation);

        ResponseEntity<DataSimulatorResponse> response = this.template.exchange(this.base.toString(), HttpMethod.POST,
                request, DataSimulatorResponse.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        DataSimulatorResponse DSresponse = response.getBody();
        assertThat(DSresponse, notNullValue());
        Thread.sleep(50000); // this is request othewise the application context
                             // shutdown

    }

    /**
     * @throws Exception
     *             -
     */
    @SuppressWarnings("nls")
    // @Test
    public void startGaussianLiteSimulation()
            throws Exception
    {
        this.base = new URL("http://localhost:" + this.localServerPort + "/start-simulation");
        Simulation simulation = SimulatorUtils.getSimualtionGaussianLite();
        HttpEntity<Simulation> request = new HttpEntity<>(simulation);
        @SuppressWarnings("unused")
        String data = this.objectMapper.writeValueAsString(simulation);

        ResponseEntity<DataSimulatorResponse> response = this.template.exchange(this.base.toString(), HttpMethod.POST,
                request, DataSimulatorResponse.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        DataSimulatorResponse DSresponse = response.getBody();
        assertThat(DSresponse, notNullValue());
        Thread.sleep(50000); // this is request othewise the application context
                             // shutdown

    }

    /**
     * @throws Exception
     *             -
     */
    @SuppressWarnings("nls")
    @Test
    public void startStopGaussianLiteSimulation()
            throws Exception
    {
        this.base = new URL("http://localhost:" + this.localServerPort + "/start-simulation");
        Simulation simulation = SimulatorUtils.getSimualtionGaussianLite();
        HttpEntity<Simulation> request = new HttpEntity<>(simulation);
        @SuppressWarnings("unused")
        String data = this.objectMapper.writeValueAsString(simulation);

        ResponseEntity<DataSimulatorResponse> response = this.template.exchange(this.base.toString(), HttpMethod.POST,
                request, DataSimulatorResponse.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        DataSimulatorResponse DSresponse = response.getBody();
        assertThat(DSresponse, notNullValue());
        Thread.sleep(50); // this is request othewise the application context
                          // shutdown

        this.base = new URL("http://localhost:" + this.localServerPort + "/stop-simulation");

        HttpEntity<String> request2 = new HttpEntity<>(simulation.getName());
        @SuppressWarnings("unused")
        String data2 = this.objectMapper.writeValueAsString(simulation.getName());

        ResponseEntity<DataSimulatorResponse> response2 = this.template.exchange(this.base.toString(), HttpMethod.POST,
                request2, DataSimulatorResponse.class);

        assertThat(response2.getStatusCode(), is(HttpStatus.OK));
        DataSimulatorResponse DSresponse2 = response2.getBody();
        assertThat(DSresponse2, notNullValue());
        Thread.sleep(50000); // this is request othewise the application context
                             // shutdown

    }

}
