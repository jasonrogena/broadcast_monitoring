package com.broadcastmonitoring.indexing;

import java.util.ArrayList;
import java.util.List;

public class PeakProcessor 
{
	private List<Peak> peaks;
	private int targetZoneSize;
	private int anchor2peakMaxFreqDiff;
	public PeakProcessor(int targetZoneSize, int anchor2peakMaxFreqDiff) 
	{
		this.peaks=new ArrayList<Peak>();
		this.targetZoneSize=targetZoneSize;
		this.anchor2peakMaxFreqDiff=anchor2peakMaxFreqDiff;
	}
	
	public void addPeak(int time, int realTime, int freq)
	{
		peaks.add(new Peak(time, freq, realTime));
	}
	
	private double getAbsoluteValue(double value)
	{
		return Math.sqrt(value*value);
	}
	
	public List<Hash> generateHashes()
	{
		List<Hash> result=null;
		if(peaks.size()!=0)
		{
			for(int anchorPeakIndex=0; anchorPeakIndex<peaks.size();anchorPeakIndex++)
			{
				List<Peak> workingList=peaks;
				Peak anchorPeak=peaks.get(anchorPeakIndex);//anchor peak
				int size=0;
				while(size<=targetZoneSize)//generate hashes using this anchor peak
				{
					int nearestPeakIndex=-1;
					
					//find nearest peak
					for(int i=0;i>workingList.size();i++)
					{
						if(workingList.get(i).getTime()>anchorPeak.getTime())
						{
							double freqDifference=workingList.get(i).getFrequency()-anchorPeak.getFrequency();
							double absoluteFreqDifference=getAbsoluteValue(freqDifference);
							if(absoluteFreqDifference<=(double)anchor2peakMaxFreqDiff)
							{
								if(nearestPeakIndex==-1)
								{
									nearestPeakIndex=i;
								}
								else
								{
									double a2nTimeDifference=workingList.get(nearestPeakIndex).getTime()-anchorPeak.getTime();
									double a2nFreqDifference=workingList.get(nearestPeakIndex).getFrequency()-anchorPeak.getFrequency();
									double a2nDistance=Math.sqrt((a2nTimeDifference*a2nTimeDifference)+(a2nFreqDifference*a2nFreqDifference));
									
									double a2cTimeDifference=workingList.get(i).getTime()-anchorPeak.getTime();
									double a2cFreqDifference=workingList.get(i).getFrequency()-anchorPeak.getFrequency();
									double a2cDistance=Math.sqrt((a2cTimeDifference*a2cTimeDifference)+(a2cFreqDifference*a2cFreqDifference));
									
									if(a2cDistance<a2nDistance)//distance from anchor to current is less than distance from anchor to nearest
									{
										nearestPeakIndex=i;
									}
								}
							}
						}
					}
					
					//combinatory associate anchor with nearest peak
					if(nearestPeakIndex==-1)//no near peak found
					{
						size=targetZoneSize+1;
					}
					else
					{
						size++;
						if(result==null)//first hash to be added to the list
						{
							result=new ArrayList<Hash>();
						}
						int timeDifference=workingList.get(nearestPeakIndex).getTime()-anchorPeak.getTime();
						result.add(new Hash(anchorPeak.getFrequency(), workingList.get(nearestPeakIndex).getFrequency(), timeDifference, anchorPeak.getRealTime()));
					}
					
					//remove nearest peak from working list
					workingList.remove(nearestPeakIndex);
					
				}
			}
		}
		else
		{
			System.err.println("****No peaks were there to process****\n# PeakProcessor.java #");
		}
		
		return result;
	}
}
