package com.broadcastmonitoring.indexing;

import java.io.Serializable;

public class Hash implements Serializable
{
	private final int anchorFrequency;
	private final int otherFrequency;
	private final int timeDifference;
	private final int anchorRealTime;
	private final String timestamp;
	
	public Hash(int f1,int f2,int timeDifference, int anchorRealTime, String timestamp)
	{
		this.anchorFrequency=f1;
		this.otherFrequency=f2;
		this.timeDifference=timeDifference;
		this.anchorRealTime=anchorRealTime;
		this.timestamp=timestamp;
	}
	
	public int getF1()
	{
		return anchorFrequency;
	}
	
	public int getF2()
	{
		return otherFrequency;
	}
	
	public int getTimeDifference()
	{
		return timeDifference;
	}
	
	public int getRealTime()
	{
		return anchorRealTime;
	}
	
	public String getTimestamp()
	{
		return timestamp;
	}

}
