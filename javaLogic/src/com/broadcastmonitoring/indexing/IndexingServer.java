package com.broadcastmonitoring.indexing;

import javax.sound.sampled.LineUnavailableException;

public class IndexingServer 
{
	public static void main(String[] args)
	{
		float sampleRate=44100; //is in Hz. results in about 20000 freqs. This is also the standard sample rate for WAV files which this app uses
		int frameSize=2048; //determines the  resolution of the spectrogram = number of hashes generated
		int hashmapSize=10; //determines the number of frames to be considered in a constelation map
		int redundantThreshold=2; //
		int startFreq=50; //determines the point at which low frequencies will be considered since lower frequencies have more pink noise (high pass filter because of the pink noise)
		int targetZoneSize=5; //determines the number of peaks to be in a target zone when forming a hash
		int anchor2peakMaxFreqDiff=1000; //determines the range of frequencies to be considered when selecting the target zone for a hash
		int sampledFrequencies=5; //determines the frequencies to be sampled, if 100 then only frequencies divisible by 100 will be sampled
		try
		{
			Streamer streamer=new Streamer(sampleRate, frameSize, hashmapSize, redundantThreshold, startFreq, targetZoneSize, anchor2peakMaxFreqDiff, sampledFrequencies);
			streamer.startAnalyzing();
		} 
		catch (LineUnavailableException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
