package com.broadcastmonitoring.indexing;

public class Hash 
{
	private final int anchorFrequency;
	private final int otherFrequency;
	private final int timeDifference;
	private final int anchorRealTime;
	
	public Hash(int f1,int f2,int timeDifference, int anchorRealTime)
	{
		this.anchorFrequency=f1;
		this.otherFrequency=f2;
		this.timeDifference=timeDifference;
		this.anchorRealTime=anchorRealTime;
	}

}
