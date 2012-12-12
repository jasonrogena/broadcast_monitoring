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
	
	public void addPeak(int time, int realTime, int freq, String timestamp)
	{
		peaks.add(new Peak(time, freq, realTime, timestamp));
	}
	
	private double getAbsoluteValue(double value)
	{
		return Math.sqrt(value*value);
	}
	
	public List<Hash> generateHashes()
	{
		List<Hash> result=null;
		System.out.println("*******************************");
		if(peaks.size()!=0)
		{
			System.out.println("number of peaks = "+peaks.size()+" " );
			//System.out.println("Real time for first peak = "+peaks.get(0).getRealTime());
			//System.out.println("Real time for last peak = "+peaks.get(peaks.size()-1).getRealTime());
			for(int anchorPeakIndex=0; anchorPeakIndex<peaks.size(); anchorPeakIndex++)
			{
				List<Integer> indexesOfAddedPeaks=new ArrayList<Integer>();
				
				Peak anchorPeak=peaks.get(anchorPeakIndex);//anchor peak
				int size=0;
				
				//System.out.print("Number of peaks = "+peaks.size());
				
				//System.out.print(" target zone size = "+targetZoneSize);
				while(size<=targetZoneSize)//generate hashes using this anchor peak
				{
					int nearestPeakIndex=-1;
					
					//find nearest peak
					for(int i=0;i<peaks.size();i++)
					{
						if(peaks.get(i).getTime()>anchorPeak.getTime() && indexesOfAddedPeaks.contains(i)==false)//changed since it appears that most frames are duplicates
						{
							double freqDifference=peaks.get(i).getFrequency()-anchorPeak.getFrequency();
							double absoluteFreqDifference=getAbsoluteValue(freqDifference);
							if(absoluteFreqDifference<=(double)anchor2peakMaxFreqDiff)
							{
								if(nearestPeakIndex==-1)
								{
									nearestPeakIndex=i;
								}
								else
								{
									double a2nTimeDifference=peaks.get(nearestPeakIndex).getTime()-anchorPeak.getTime();
									double a2nFreqDifference=peaks.get(nearestPeakIndex).getFrequency()-anchorPeak.getFrequency();
									double a2nDistance=Math.sqrt((a2nTimeDifference*a2nTimeDifference)+(a2nFreqDifference*a2nFreqDifference));
									
									double a2cTimeDifference=peaks.get(i).getTime()-anchorPeak.getTime();
									double a2cFreqDifference=peaks.get(i).getFrequency()-anchorPeak.getFrequency();
									double a2cDistance=Math.sqrt((a2cTimeDifference*a2cTimeDifference)+(a2cFreqDifference*a2cFreqDifference));
									
									if(a2cDistance<a2nDistance)//distance from anchor to current is less than distance from anchor to nearest
									{
										//System.out.print(" nearest peak found ");
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
						int timeDifference=peaks.get(nearestPeakIndex).getTime()-anchorPeak.getTime();
						result.add(new Hash(anchorPeak.getFrequency(), peaks.get(nearestPeakIndex).getFrequency(), timeDifference, anchorPeak.getRealTime(), anchorPeak.getTimestamp()));
						
						//remove nearest peak from working list
						//workingList.remove(nearestPeakIndex);
						indexesOfAddedPeaks.add(nearestPeakIndex);
					}
				}
			}
		}
		else
		{
			System.err.println("****No peaks were there to process****\n# PeakProcessor.java #");
		}
		
		if(result!=null)
		{
			//System.out.println("real time for first hash = "+result.get(0).getRealTime());
			//System.out.println("real time for last hash = "+result.get(result.size()-1).getRealTime());
		}
		
		return result;
	}
}
