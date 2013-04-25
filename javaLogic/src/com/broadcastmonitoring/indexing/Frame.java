package com.broadcastmonitoring.indexing;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class Frame
{
	//private final byte[] buffer;
	private final int redundantThreshold;
	private final int startFreq;
	private final int realTime;
	private final String timestamp;
	private double[] freqMagnitudes;
	private final long timestampMilliseconds;
	public Frame(byte[] buffer, int redundantThreshold,int startFreq, int realTime, String timestamp, long timestampMilliseconds)
	{
		//this.buffer=buffer;
		//System.out.println(realTime+" >"+buffer[0]+" "+buffer[buffer.length/2]+" "+buffer[buffer.length-1]);
		this.redundantThreshold=redundantThreshold;
		this.startFreq=startFreq;
		this.realTime=realTime;
		this.timestamp=timestamp;
		this.timestampMilliseconds=timestampMilliseconds;
		
		Complex[] result=new Complex[buffer.length];
		FastFourierTransformer transformer=new FastFourierTransformer(DftNormalization.STANDARD);
		result=transformer.transform(getComplex(buffer), TransformType.FORWARD);
		//System.out.println(result[0].getReal()+" "+result[result.length/2].getReal()+" "+result[result.length-1].getReal());
		freqMagnitudes=convertComplexToDouble(result);
	}
	
	public void printFreqMagnitudes()
	{
		System.out.println(realTime+" > "+freqMagnitudes[0]+" "+freqMagnitudes[freqMagnitudes.length/2]+" "+freqMagnitudes[freqMagnitudes.length-1]);
	}
	
	private Complex[] getComplex(byte[] buffer)
	{
		Complex[] result=new Complex[buffer.length];
		for(int i=0;i<buffer.length;i++)
		{
			result[i]=new Complex(buffer[i], 0);
		}
		
		//System.out.println(result[0].getReal()+" "+result[result.length/2].getReal()+" "+result[result.length-1].getReal());
		return result;
	}
	
	private double[] convertComplexToDouble(Complex[] input)
	{
		double[] result=null;
		if(input.length%redundantThreshold==0)
		{
			result=new double[(input.length/redundantThreshold)-startFreq];
			
			for (int i = 0; i < result.length; i++) 
			{
				if((i+startFreq)<input.length)
				{
					result[i]=input[i+startFreq].abs();
				}
				else
				{
					System.err.println("****Trying to access a frequency that is greater that what has been sampled****\n# Frame.java #");
				}
				
			}
			//System.out.println(result[0]+" "+result[result.length/2]+" "+result[result.length-1]);
		}
		else
		{
			System.err.println("****The number of sampled frequencies appears to be not divisible by the redundantThreshold****\n# Frame.java #");
		}
		
		return result;
	}
	
	public int getRealTime()
	{
		return realTime;
	}
	
	public String getTimestamp()
	{
		return timestamp;
	}
	
	/*public int getNumberOfFreq()
	{
		return (buffer.length/redundantThreshold)-startFreq;
	}*/
	
	public double[] getFreqMagnitudes()
	{
		return freqMagnitudes;
	}
	
	public long getTimestampMilliseconds()
	{
		return timestampMilliseconds;
	}
}
