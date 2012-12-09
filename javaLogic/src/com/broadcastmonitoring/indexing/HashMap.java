package com.broadcastmonitoring.indexing;

import java.util.List;

public class HashMap
{
	protected final Frame[] frames;
	protected final int targetZoneSize;
	protected final int anchor2peakMaxFreqDiff;

	public HashMap(Frame[] frames, int targetZoneSize, int anchor2peakMaxFreqDiff)
	{
		this.frames=frames;
		this.targetZoneSize=targetZoneSize;
		this.anchor2peakMaxFreqDiff=anchor2peakMaxFreqDiff;
	}
	
	private double[][] generateFreqMagnitudes()
	{
		double[][] result=new double[frames.length][];
		for (int i = 0; i < frames.length; i++) 
		{
			result[i]=frames[i].getFreqMagnitudes();
			if(result[i]==null || result[i].length==0)
			{
				System.err.println("****No magnitudes were returned for frame "+String.valueOf(i)+"****\n# HashMap.java #");
			}
		}
		return result;
	}
	
	public void generateHashes()
	{
		Thread thread=new Thread(new HashProcessor());
		thread.run();
	}
	
	private PeakProcessor generateConstelationMap(double[][] freqMagnitudes)
	{
		
		//generateFreqMagnitudes();
		PeakProcessor peakProcessor=null;
		
		int[][] result=null;
		//for each of the magnitudes in freqMagnitudes check if is a peak
		if(freqMagnitudes.length>0 && freqMagnitudes[0].length>0)
		{
			result=new int[freqMagnitudes.length][];
			for (int i = 0; i < freqMagnitudes.length; i++) 
			{
				result[i]=new int[freqMagnitudes[i].length];
				for (int j = 0; j < freqMagnitudes[i].length; j++)
				{
					int pass=0;
					if((i-1)>=0)
					{
						if(freqMagnitudes[i][j]>freqMagnitudes[i-1][j])
						{
							pass++;
						}
					}
					else
					{
						pass++;
					}
					if((i+1)<freqMagnitudes.length)
					{
						if(freqMagnitudes[i][j]>freqMagnitudes[i+1][j])
						{
							pass++;
						}
					}
					else
					{
						pass++;
					}
					if((j-1)>=0)
					{
						if(freqMagnitudes[i][j]>freqMagnitudes[i][j-1])
						{
							pass++;
						}
					}
					else
					{
						pass++;
					}
					if((j+1)<freqMagnitudes[i].length)
					{
						if(freqMagnitudes[i][j]>freqMagnitudes[i][j+1])
						{
							pass++;
						}
					}
					else
					{
						pass++;
					}
					
					if(pass==4)//passed all 4 tests
					{
						result[i][j]=1;
						if(peakProcessor==null)
						{
							peakProcessor=new PeakProcessor(targetZoneSize,anchor2peakMaxFreqDiff);
						}
						peakProcessor.addPeak(i, frames[i].getRealTime(), j);
					}
					else
					{
						result[i][j]=0;
					}
				}
			}
		}
		else
		{
			if(freqMagnitudes.length<=0)
			{
				System.err.println("****Hashmap has 0 frames****\n# HashMap.java #");
			}
			else
			{
				System.err.println("****Frames in hashMap have 0 frequency magnitudes****\n# HashMap.java #");
			}
		}
		return peakProcessor;
		
	}
	
	private class HashProcessor implements Runnable
	{

		@Override
		public void run() 
		{
			double[][] freqMagnitudes;
			freqMagnitudes=generateFreqMagnitudes();
			PeakProcessor peakProcessor=generateConstelationMap(freqMagnitudes);
			if(peakProcessor==null)
			{
				System.err.println("****The peak processor was not initialized. This is probably because no peaks were found in the constelation map****\n# HashMap.java #");
			}
			else
			{
				List<Hash> hashes=peakProcessor.generateHashes();
				if(hashes==null)
				{
					System.err.println("****No hashes were generated****\n# HashMap.java #");
				}
				else
				{
					System.out.println("number of hashes = "+hashes.size());
				}
			}
		}
		
	}

}
