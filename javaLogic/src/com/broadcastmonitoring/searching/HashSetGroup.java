package com.broadcastmonitoring.searching;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import com.broadcastmonitoring.indexing.Hash;

public class HashSetGroup
{
	List<String> hashSetUrls;
	final List<Hash> advertHashes;
	final List<Hash> groupHashes;
	final String hashSetDir;
	List<Double> bin;
	List<Integer> binFrequencies;
	
	public HashSetGroup(List<String> hashSetUrls, List<Hash> advertHashes, String dir)
	{
		this.hashSetUrls=hashSetUrls;
		this.advertHashes=advertHashes;
		this.groupHashes=new ArrayList<Hash>();
		this.hashSetDir=dir;
		bin=new ArrayList<Double>();
		binFrequencies=new ArrayList<Integer>();
	}
	
	public void process()
	{
		System.out.println("Deserializing hashes");
		deserializeHashSets();
		System.out.println("Matching hashes");
		matchHashes();
		System.out.println("Processing bin frequencies");
		processBinFrequencies();
	}
	
	public void showHashSetUrls()
	{
		System.out.println("group");
		for (int i = 0; i < hashSetUrls.size(); i++) 
		{
			System.out.println(hashSetUrls.get(i));
		}
	}
	
	private void deserializeHashSets()
	{
		//System.out.println("group");
		for (int i = 0; i < hashSetUrls.size(); i++) 
		{
			try 
			{
				InputStream hashSetSerFile=new FileInputStream(hashSetDir+"/"+hashSetUrls.get(i));
				InputStream buffer=new BufferedInputStream(hashSetSerFile);
				ObjectInput objectInput=new ObjectInputStream(buffer);
				List<Hash> fetchedHashSet=(List<Hash>)objectInput.readObject();
				if(fetchedHashSet!=null)
				{
					groupHashes.addAll(fetchedHashSet);
					//System.out.println(groupHashes.size());
				}
				else
				{
					System.err.println("****"+hashSetUrls.get(i)+" was not deserialized****\n# HashSetGroup.java #");
				}
				objectInput.close();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void matchHashes()
	{
		for (int i = 0; i < groupHashes.size(); i++)//for each of the hashes in the group
		{
			if(i%1000==0)
			{
				System.out.print("*");
			}
			for (int j = 0; j < advertHashes.size(); j++)//compare the hash with all the hashes in the advert
			{
				if(groupHashes.get(i).getF1()==advertHashes.get(j).getF1() && groupHashes.get(i).getF2()==advertHashes.get(j).getF2() && groupHashes.get(i).getTimeDifference()==advertHashes.get(j).getTimeDifference())
				{
					double timeDifference=groupHashes.get(i).getRealTime()-advertHashes.get(j).getRealTime();
					if(bin.contains(timeDifference))
					{
						binFrequencies.set(bin.indexOf(timeDifference), binFrequencies.get(bin.indexOf(timeDifference))+1);//increment frequency of number by one
						//the assumption here is that the frequency of a number has the same index as the number
					}
					else
					{
						bin.add(timeDifference);
						binFrequencies.add(1);
					}
				}
			}
		}
	}
	
	private void processBinFrequencies()
	{
		if(binFrequencies.size()>0)
		{
			int indexOfLargestFreq=0;
			System.out.println("BIN FREQUENCIES");
			for (int i = 0; i < binFrequencies.size(); i++)
			{
				System.out.print(" "+binFrequencies.get(i)+" ");
				if(i==0)
				{
					indexOfLargestFreq=i;
				}
				else
				{
					if(binFrequencies.get(i)>=binFrequencies.get(i-1))
					{
						indexOfLargestFreq=i;
					}
				}
			}
			System.out.println("\n THE LAGEST FREQ = "+binFrequencies.get(indexOfLargestFreq));
		}
		else
		{
			System.err.println("****none of the hashes from the advert and the channel appear to match****\n# HashSetGroup.java #");
		}
		
	}
	
}
