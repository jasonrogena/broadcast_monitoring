package com.broadcastmonitoring.indexing;

import javax.sound.sampled.LineUnavailableException;

public class IndexingServer 
{
	public static void main(String[] args)
	{
		float sampleRate=44100; //results in about 20000 freqs
		int frameSize=8192; //determines the  resolution of the spectrogram
		int hashmapSize=10; //determines the number of frames to be considered in a constelation map
		int redundantThreshold=2; //
		int startFreq=100; //determines the point at which low frequencies will be considered since lower frequencies have more pink noise
		int targetZoneSize=20; //determines the number of peaks to be in a target zone when forming a hash
		int anchor2peakMaxFreqDiff=5; //determines the number of frequencies to be considered when selecting the target zone for a hash
		try 
		{
			Streamer streamer=new Streamer(sampleRate, frameSize, hashmapSize, redundantThreshold, startFreq, targetZoneSize, anchor2peakMaxFreqDiff);
			streamer.startAnalyzing();
		} 
		catch (LineUnavailableException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
