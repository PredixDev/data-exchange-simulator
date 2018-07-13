/*
 * Copyright (c) 2018 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.simulator;

import java.util.Random;

/**
 * 
 * @author 212421693 -
 */
public class NormalDistrubtion
{

    /**
     * @param args -
     * @throws InterruptedException -
     */
    @SuppressWarnings("nls")
    public static void main(String[] args)
            throws InterruptedException
    {
        /*
         * int height = 10;
         * int widthOfCurve = 3;
         * //its a Gaussian pattern here f(x) = ae power -(x-b)(x-b) / 2c*c
         * long startTime = Instant.now().toEpochMilli();
         * Date date = new Date();
         * long endTime = date.getTime() + TimeUnit.SECONDS.toMillis(1200);
         * int centerOfCurve = (int )(endTime-startTime) /2 ;
         * long samplingtime = 20 ; // msec
         * long currentTime = startTime;
         * System.out.println("START");
         * while (endTime>currentTime) {
         * double exponement = ( 1 * ((currentTime - centerOfCurve)*(currentTime - centerOfCurve)) ) / (2 * (widthOfCurve*widthOfCurve));
         * double y = height * Math.exp(exponement);
         * System.out.println("x= "+currentTime + " y= "+y);
         * Thread.sleep(samplingtime);
         * currentTime= System.currentTimeMillis();
         * }
         * System.out.println("END");
         */
        NormalDistrubtion gaussian = new NormalDistrubtion();
        double MEAN = 100.0f;
        double VARIANCE = 10.0f;
        for (int idx = 1; idx <= 10; ++idx)
        {
            log("idx= " + idx + " Generated : " + gaussian.getGaussian(MEAN, VARIANCE));
        }
    }

    private Random fRandom = new Random();

    private double getGaussian(double aMean, double aVariance)
    {
        return aMean + this.fRandom.nextGaussian() * aVariance;
    }

    private static void log(Object aMsg)
    {
        System.out.println(String.valueOf(aMsg));
    }

}
