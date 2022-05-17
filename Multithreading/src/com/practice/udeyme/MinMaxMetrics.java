package com.practice.udeyme;

import java.util.ArrayList;
import java.util.List;

public class MinMaxMetrics {
    
    // Add all necessary member variables
	List<Long> longList = new ArrayList<Long>();
	long minMetrics=Long.MAX_VALUE;
	long maxMetrics=Long.MIN_VALUE;

    /**
     * Initializes all member variables
     */
    public MinMaxMetrics() {
     for (Long val : longList) {
		if(val<minMetrics)
			minMetrics=val;
		if(val>maxMetrics)
			maxMetrics=val;
	}
    }

    /**
     * Adds a new sample to our metrics.
     */
    public void addSample(long newSample) {
    	if(longList.size()==0)
    	{
    		minMetrics=newSample;
    		maxMetrics=newSample;
    	}
    	else
    	{
    		if(newSample>maxMetrics)
    			maxMetrics=newSample;
    		if(newSample<minMetrics)
    			minMetrics= newSample;
    	}
        longList.add(newSample);
    }

    /**
     * Returns the smallest sample we've seen so far.
     */
    public long getMin() {
       return minMetrics;
    }

    /**
     * Returns the biggest sample we've seen so far.
     */
    public long getMax() {
       return maxMetrics;
    }
}
