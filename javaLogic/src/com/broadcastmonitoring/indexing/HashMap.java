package com.broadcastmonitoring.indexing;

import com.broadcastmonitoring.database.Database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class HashMap
{
	protected final Frame[] frames;
	protected final int targetZoneSize;
	protected final int anchor2peakMaxFreqDiff;
	protected final int parent;
	protected final int parentType;

	public HashMap(Frame[] frames, int targetZoneSize, int anchor2peakMaxFreqDiff, int parent, int parentType)
	{
		//frames[0].printBuffer();
		//frames[9].printBuffer();
		this.frames=frames;
		this.targetZoneSize=targetZoneSize;
		this.anchor2peakMaxFreqDiff=anchor2peakMaxFreqDiff;
		this.parent=parent;
		this.parentType=parentType;
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
		//System.out.println("generateConstelationMap called");
		//generateFreqMagnitudes();
		PeakProcessor peakProcessor=null;
		
		/*//Debug
		for (int i = 0; i < freqMagnitudes.length; i++)
		{
			for (int j = 0; j < freqMagnitudes[i].length; j++) 
			{
				System.out.print(" "+freqMagnitudes[i][j]+" ");
				if(j==freqMagnitudes[i].length-1)
				{
					System.out.print("\n");
				}
			}
		}
		//Debug*/
		
		int[][] result=null;
		//for each of the magnitudes in freqMagnitudes check if is a peak
		if(freqMagnitudes.length>0 && freqMagnitudes[0].length>0)
		{
			//System.out.println("processing");
			result=new int[freqMagnitudes.length][];
			for (int i = 0; i < freqMagnitudes.length; i++) 
			{
				result[i]=new int[freqMagnitudes[i].length];
				
				for (int j = 0; j < freqMagnitudes[i].length; j++)
				{
					/*int pass=0;
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
						//System.out.print(" found ");
						result[i][j]=1;
						if(peakProcessor==null)
						{
							peakProcessor=new PeakProcessor(targetZoneSize,anchor2peakMaxFreqDiff);
						}
						peakProcessor.addPeak(i, frames[i].getRealTime(), j, frames[i].getTimestamp());
					}
					else
					{
						result[i][j]=0;
					}*/
					
					if(isPeak(100, j, freqMagnitudes[i], 100))
					{
						result[i][j]=1;
						if(peakProcessor==null)
						{
							peakProcessor=new PeakProcessor(targetZoneSize,anchor2peakMaxFreqDiff);
						}
						peakProcessor.addPeak(i, frames[i].getRealTime(), j, frames[i].getTimestamp());
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
		
		/*for (int k = 0; k < result.length; k++)
		{
			for (int k2 = 0; k2 < result[k].length; k2++)
			{
				System.out.print(" "+result[k][k2]+" ");
				if (k2==result[k].length-1)
				{
					System.out.print("\n");
				}
			}
			
		}*/
		
		return peakProcessor;
		
	}
	
	private boolean isPeak(int k,int i,double[] array, int threshold)
	{
		double sum1=0;
		for (int j = i-1; j >=i-k && j>=0; j--)
		{
			sum1=sum1+array[j];
		}
		double div1=sum1/k;
		div1=array[i]-div1;
		
		double sum2=0;
		for (int j = i+1; j < array.length && j<=i+k; j++)
		{
			sum2=sum2+array[j];
		}
		double div2=sum2/k;
		div2=array[i]-div2;
		
		double div=(div1+div2)/2;
		if(div>=threshold)
		{
			//System.out.print(" "+div+" ");
			return true;
		}
		
		return false;
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
					
					Database database=new Database("broadcast_monitoring", "root", "jason");
					
					String fileName=String.valueOf(hashes.get(0).getRealTime())+"_"+hashes.get(0).getTimestamp()+"_"+hashes.get(hashes.size()-1).getTimestamp()+".ser";
					String dir="../bin/hashes";
					
					//check and create dir
					File directory=new File(dir);
					if(!directory.exists())
					{
						System.err.println("****"+dir+" does not exist. Trying to create dir ****\n# HashMap.java #");
						if(directory.mkdirs())
						{
							System.out.println("*****"+dir+" created****\n# HashMap.java #");
						}
						else
						{
							System.err.println("**** Unable to create "+dir+" ****\n# HashMap.java #");
						}
					}
					
					File file=new File(dir,fileName);
					if(file.exists()==false)
					{
						FileOutputStream fileOutputStream=null;
						ObjectOutputStream objectOutputStream=null;
						try 
						{
							file.createNewFile();
							fileOutputStream=new FileOutputStream(file);
							objectOutputStream=new ObjectOutputStream(fileOutputStream);
							objectOutputStream.writeObject(hashes);
							
							database.initInsertStatement("INSERT INTO `broadcast_monitoring`.`hash_set`(`start_timestamp`,`stop_timestamp`,url,parent,`start_real_time`,`parent_type`) VALUES (?,?,?,?,?,?)");
							database.addColumnValue(hashes.get(0).getTimestamp());
							database.addColumnValue(hashes.get(hashes.size()-1).getTimestamp());
							database.addColumnValue(fileName);
							database.addColumnValue(parent);
							database.addColumnValue(hashes.get(0).getRealTime());
							database.addColumnValue(parentType);
							
							database.executeInsert();
						} 
						catch (FileNotFoundException e)
						{
							e.printStackTrace();
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
						}
						finally
						{
							if(objectOutputStream!=null)
							{
								try 
								{
									objectOutputStream.close();
								} 
								catch (IOException e) 
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							if(fileOutputStream!=null)
							{
								try 
								{
									fileOutputStream.close();
								} 
								catch (IOException e) 
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
					else
					{
						System.err.println("****File already exists****\n# HashMap.java #");
					}
					database.close();
					
				}
			}
		}
		
	}

}
