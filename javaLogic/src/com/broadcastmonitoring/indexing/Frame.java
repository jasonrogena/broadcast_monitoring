package com.broadcastmonitoring.indexing;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class Frame
{
	private final byte[] buffer;
	private final int redundantThreshold;
	private final int startFreq;
	private final int realTime;
	public Frame(byte[] buffer, int redundantThreshold,int startFreq, int realTime)
	{
		this.buffer=buffer;
		this.redundantThreshold=redundantThreshold;
		this.startFreq=startFreq;
		this.realTime=realTime;
	}
	
	private Complex[] getComplex()
	{
		Complex[] result=new Complex[buffer.length];
		for(int i=0;i<buffer.length;i++)
		{
			result[i]=new Complex(buffer[i], 0);
		}
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
	
	public int getNumberOfFreq()
	{
		return (buffer.length/redundantThreshold)-startFreq;
	}
	
	public double[] getFreqMagnitudes()
	{
		Complex[] result=new Complex[buffer.length];
		FastFourierTransformer transformer=new FastFourierTransformer(DftNormalization.STANDARD);
		result=transformer.transform(getComplex(), TransformType.FORWARD);
		return convertComplexToDouble(result);
	}
}
