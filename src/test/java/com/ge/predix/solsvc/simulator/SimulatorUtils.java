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

import com.ge.predix.entity.simulator.Range;
import com.ge.predix.entity.simulator.Simulation;
import com.ge.predix.entity.simulator.Tag;
import com.ge.predix.entity.simulator.TagSet;
import com.ge.predix.solsvc.simulator.types.Constants;

import java.util.ArrayList;
import java.util.List;

//import com.ge.predix.entity.simulator.Pattern;

/**
 * 
 * @author 212421693 -
 */
public class SimulatorUtils
{

    /**
     * @param lowerThreshold -
     * @param upperThreshold -
     * @return range -
     */
    public static Range getRange(double lowerThreshold, double upperThreshold)
    {
        Range range = new Range();
        range.setLowerThreshold(lowerThreshold);
        range.setUpperThreshold(upperThreshold);
        range.setDuration(10);
        return range;
    }

    /**
     * @param start -
     * @param durationType -
     * @param minDuration -
     * @param maxDuration -
     * @param interval -
     * @return Pattern -
     */
    // public static Pattern getPattern( long start, String durationType, int minDuration,int maxDuration,int interval) {
    // Pattern subcription = new Pattern();
    // subcription.setStart(start);
    // subcription.setDurationType(durationType);
    // subcription.setMinDuration(minDuration);
    // subcription.setMaxDuration(maxDuration);
    // subcription.setInterval(interval);
    // return subcription;
    // }

    /**
     * 
     * @param assetId -
     * @param dataType -
     * @param nodeName -
     * @param simulationType -
     * @return -
     */
    public static Tag getTag(String assetId, String dataType, String nodeName, String simulationType)
    {
        Tag tag = new Tag();
        tag.setAssetId(assetId);
        tag.setDataType(dataType);
        tag.setNodeName(nodeName);
        tag.setSimulationType(simulationType);
        return tag;
    }

    /**
     * @return -
     */
    @SuppressWarnings("nls")
    public static Simulation getSimualtion()
    {
        Simulation simulation = new Simulation();
        simulation.setName("Test Simulation");
        // List<TagSet> tagSets = new ArrayList<TagSet>();

        // List<Tag> patterns = new ArrayList<Tag>();
        // Pattern pattern1 = getPattern (0l,"FIXED",120,120,5);
        // patterns.add(pattern1);
        // Pattern pattern2 = getPattern (0l,"FIXED",120,120,5);
        // patterns.add(pattern2);
        // Pattern pattern3 = getPattern (0l,"FIXED",120,120,5);
        // patterns.add(pattern3);
        // Pattern pattern4 = getPattern (0l,"FIXED",120,120,5);
        // patterns.add(pattern4);
        // Pattern pattern5 = getPattern (0l, "RANDOM", 120,120,5);
        // patterns.add(pattern5);

        TagSet tagSet = new TagSet();
        // tagSet.setSubscription(patterns);
        List<Tag> tags = new ArrayList<Tag>();
        // #1 tag
        Tag tag = getTagWithRange("Compressor-2017", Constants.SIMULATOR_DATA_TYPE_DOUBLE, "CompressionRatio", "RANDOM",
                2.0d, 2.5d);

        Range range2 = getRange(2.5d, 3.0d);
        tag.getRange().add(range2);
        Range range3 = getRange(3.0d, 5.5d);
        tag.getRange().add(range3);
        Range range4 = getRange(4.7d, 4.0d);
        tag.getRange().add(range4);
        Range range5 = getRange(2.5d, 3.0d);
        tag.getRange().add(range5);
        tags.add(tag);
        // #2 tag

        tag = getTagWithRange("Compressor-2017", Constants.SIMULATOR_DATA_TYPE_DOUBLE, "DischargePressure", "RANDOM",
                0d, 10.0d);
        range2 = getRange(10d, 30d);
        tag.getRange().add(range2);
        range3 = getRange(25d, 30);
        tag.getRange().add(range3);
        range4 = getRange(0d, 15d);
        tag.getRange().add(range4);
        range5 = getRange(15d, 30d);
        tag.getRange().add(range5);

        tags.add(tag);

        tagSet.setTag(tags);
        // tagSets.add(tagSet);

        // #3 tag Test case for Random duration with a different pattern
        // TagSet tagSet2 = new TagSet();

        // List<Tag> tags2 = new ArrayList<Tag>();

        // patterns = new ArrayList<Pattern>();
        // pattern1 = getPattern (System.currentTimeMillis(),"RANDOM",60,120,5);
        // patterns.add(pattern1);
        // pattern2 = getPattern( 0l, "RANDOM", 30,120,5);
        // patterns.add(pattern2);
        // pattern3 = getPattern (0l, "RANDOM", 30,120,5);
        // patterns.add(pattern3);
        // pattern4 = getPattern (0l, "RANDOM", 10,60,5);
        // patterns.add(pattern4);
        // pattern5 = getPattern (0l, "RANDOM", 10,60,5);
        // patterns.add(pattern5);
        // tagSet2.setSubscription(patterns);
        //
        // tag = getTagWithRange("Compressor-2017",Constants.SIMULATOR_DATA_TYPE_DOUBLE,"SuctionPressure","RANDOM",0d, 0.5d);
        //
        // range2 = getRange(0d, 0.5d);
        // tag.getRange().add(range2);
        // range3 = getRange(0d, 0.5d);
        // tag.getRange().add(range3);
        // range4 = getRange(0d, 0.5d);
        // tag.getRange().add(range4);
        // range5 = getRange(0d, 0.5d);
        // tag.getRange().add(range5);
        //
        // tags.add(tag);
        // tags2.add(tag);
        // tagSet2.setTag(tags2);
        // tagSets.add(tagSet2);

        simulation.setTagSet(tagSet);

        return simulation;
    }

    /**
     * @return -
     */
    @SuppressWarnings("nls")
    public static Simulation getSimualtionLite()
    {
        Simulation simulation = new Simulation();
        simulation.setName("Test Lite Simulation");
        // List<TagSet> tagSets = new ArrayList<TagSet>();

        // Pattern 1 :from now every 2 sec send a datapoint for 10 sec ( range of 2.0d, 2.5d)
        // Pattern 2: from the last moment, send datapoints every 2 sec for 10 sec ( range of 2.5d, 3.0d)

        // List<Pattern> patterns = new ArrayList<Pattern>();
        // Pattern pattern1 = getPattern (0l,"FIXED",10,15,2); //long start(timestamp in unix epoch or 0 means now), String durationType, int minDuration( in
        // sec for total duration of that period),int maxDuration(Fixed , this have nothing to do ),(int interval=2 sec between datapoints)
        // patterns.add(pattern1);
        // Pattern pattern2 = getPattern (0l,"FIXED",10,15,2);
        // patterns.add(pattern2);

        TagSet tagSet = new TagSet();
        // tagSet.setSubscription(patterns);
        List<Tag> tags = new ArrayList<Tag>();
        // #1 tag
        Tag tag = getTagWithRange("Compressor-2017-test", Constants.SIMULATOR_DATA_TYPE_DOUBLE, "CompressionRatio",
                "RANDOM", 2.0d, 2.5d);
        Range range2 = getRange(2.5d, 3.0d);
        tag.getRange().add(range2);
        tags.add(tag);
        tagSet.setTag(tags);
        // tagSets.add(tagSet);
        simulation.setTagSet(tagSet);

        return simulation;
    }

    /**
     * @return -
     */
    @SuppressWarnings("nls")
    public static Simulation getSimualtionGaussianLite()
    {
        Simulation simulation = new Simulation();
        simulation.setName("Test Lite Gaussian Simulation");
        // List<TagSet> tagSets = new ArrayList<TagSet>();

        // Pattern 1 :from now every 2 sec send a datapoint for 10 sec ( range of 2.0d, 2.5d)
        // Pattern 2: from the last moment, send datapoints every 2 sec for 10 sec ( range of 2.5d, 3.0d)

        // List<Pattern> patterns = new ArrayList<Pattern>();
        // Pattern pattern1 = getPattern (0l,Constants.FIXED_DURATION_TYPE,10,15,2); //long start(timestamp in unix epoch or 0 means now), String durationType,
        // int minDuration( in sec for total duration of that period),int maxDuration(Fixed , this have nothing to do ),(int interval=2 sec between datapoints)
        // patterns.add(pattern1);
        //
        // Pattern pattern2 = getPattern (0l,Constants.SPIKE_DURATION_TYPE,10,15,2);
        // patterns.add(pattern2);

        TagSet tagSet = new TagSet();
        // tagSet.setSubscription(patterns);
        List<Tag> tags = new ArrayList<Tag>();
        // #1 tag
        Tag tag = getTagWithRange("Compressor-2017-test-spike1", Constants.SIMULATOR_DATA_TYPE_DOUBLE,
                "CompressionRatio", "RANDOM", 2.0d, 2.5d);
        tag.setStart(0);
        tag.setInterval(5);
        Range range2 = getRange(2d, 7.0d);
        tag.getRange().add(range2);
        tags.add(tag);
        tagSet.setTag(tags);
        // tagSets.add(tagSet);
        simulation.setTagSet(tagSet);

        return simulation;
    }

    /*
     * @param assetId -
     * @param dataType -
     * @param nodeName -
     * @param simulationType -
     * @param lowerThreshold -
     * @param upperThreshold -
     * @param start -
     * @param durationType -
     * @param minDuration -
     * @param maxDuration -
     * @return - Tag
     */
    private static Tag getTagWithRange(String assetId, String dataType, String nodeName, String simulationType,
            double lowerThreshold, double upperThreshold)
    {
        Tag tag = SimulatorUtils.getTag(assetId, dataType, nodeName, simulationType);
        List<Range> ranges = new ArrayList<Range>();
        Range range = SimulatorUtils.getRange(lowerThreshold, upperThreshold);
        range.setDuration(10);
        ranges.add(range);
        tag.setRange(ranges);

        return tag;

    }
}
