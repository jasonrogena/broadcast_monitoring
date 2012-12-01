package com.broadcastmonitoring.indexing;

public class HashMap
{
	protected final Frame[] frames;
	protected final double[][] freqMagnitudes;
	protected int[][] constelationMap; 
	public HashMap(Frame[] frames)
	{
		this.frames=frames;
		freqMagnitudes=new double[frames.length][];
	}
	
	private void generateFreqMagnitudes()
	{
		for (int i = 0; i < frames.length; i++) 
		{
			freqMagnitudes[i]=frames[i].getFreqMagnitudes();
		}
	}
	
	public void getHashes()
	{
		
	}
	
	private int[][] generateConstelationMap()
	{
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
				System.err.println("****Hashmap has 0 frames****");
			}
			else
			{
				System.err.println("****Frames in hashMap have 0 frequency magnitudes****");
			}
		}
		return result;
		
	}

}
