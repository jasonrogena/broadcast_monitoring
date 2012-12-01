package com.broadcastmonitoring.indexing;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class Frame
{
	private final byte[] buffer;
	private final int redundantThreshold;
	public Frame(byte[] buffer, int redundantThreshold)
	{
		this.buffer=buffer;
		this.redundantThreshold=redundantThreshold;
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
		double[] result=new double[input.length/redundantThreshold];
		for (int i = 0; i < result.length; i++) 
		{
			result[i]=input[i].abs();
		}
		return result;
	}
	
	public int getNumberOfFreq()
	{
		return buffer.length/redundantThreshold;
	}
	
	public double[] getFreqMagnitudes()
	{
		Complex[] result=new Complex[buffer.length];
		FastFourierTransformer transformer=new FastFourierTransformer(DftNormalization.STANDARD);
		result=transformer.transform(getComplex(), TransformType.FORWARD);
		return convertComplexToDouble(result);
	}
}
