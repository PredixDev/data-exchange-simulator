package com.ge.predix.solsvc.simulator.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 
 * @author developer relations -
 */
@SpringBootApplication
@ComponentScan(basePackages =
{
        "com.ge.predix.solsvc.simulator", "com.ge.predix.solsvc.ext.util", "com.ge.predix.solsvc.restclient",
        "com.ge.predix.solsvc.websocket.client", "com.ge.predix.solsvc.websocket.config"
})

@EnableAutoConfiguration(exclude =
{
        DataSourceAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class,
        PersistenceExceptionTranslationAutoConfiguration.class
})

@EnableAsync

public class DataExchangeSimulatorApplication
{

    private static final Logger log = LoggerFactory.getLogger(DataExchangeSimulatorApplication.class);

    /**
     * @param args
     *            -
     */
    public static void main(String[] args)
    {
        @SuppressWarnings(
        {
                "hiding", "unused"
        })
        Logger log = LoggerFactory.getLogger(DataExchangeSimulatorApplication.class);

        SpringApplication springApplication = new SpringApplication(DataExchangeSimulatorApplication.class);
        @SuppressWarnings(
        {
                "resource", "unused"
        })
        ApplicationContext ctx = springApplication.run(args);
        // System.gc();
        // printMemory();

    }

    /**
     * -
     */
    @SuppressWarnings("nls")
    public static void printMemory()
    {
        Runtime runtime = Runtime.getRuntime();
        int mb = 1024 * 1024;

        log.debug("##### Heap utilization statistics [MB] #####");

        // Print used memory
        log.debug("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);

        // Print free memory
        log.debug("Free Memory:" + runtime.freeMemory() / mb);

        // Print total available memory
        log.debug("Total Memory:" + runtime.totalMemory() / mb);

        // Print Maximum available memory
        log.debug("Max Memory:" + runtime.maxMemory() / mb);
    }

    /**
     * Add this bean or the @PropertySource above won't kick in
     * 
     * @return -
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
    {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
