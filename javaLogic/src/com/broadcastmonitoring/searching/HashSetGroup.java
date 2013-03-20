package com.broadcastmonitoring.searching;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JFrame;

import com.broadcastmonitoring.indexing.Hash;

public class HashSetGroup
{
	private List<String> hashSetUrls;
	private final List<Hash> advertHashes;
	private final List<Hash> groupHashes;
	private final String hashSetDir;
	private List<Double> bin;
	private List<Integer> binFrequencies;
	private List<List<Hash>> advertHashSetPieces;
	private Map<Double, Integer> binMap;
	private Chart2D chart;
	private ITrace2D trace;
	private long comparisonsDone;
	private long matches;
	
	
	public HashSetGroup(List<String> hashSetUrls, List<Hash> advertHashes, String dir)
	{
		this.hashSetUrls=hashSetUrls;
		this.advertHashes=advertHashes;
		this.groupHashes=new ArrayList<Hash>();
		this.hashSetDir=dir;
		bin=new ArrayList<Double>();
		binFrequencies=new ArrayList<Integer>();
		advertHashSetPieces=new ArrayList<List<Hash>>();
		binMap=new HashMap<Double, Integer>();
		comparisonsDone=0;
		matches=0;
		//initChart();
	}
	
	private void initChart()
	{
		chart=new Chart2D();
		trace=new Trace2DLtd(hashSetUrls.get(0));//the number of time differences is not known before hand
		trace.setColor(Color.RED);
		chart.addTrace(trace);
		
		JFrame frame=new JFrame();
		frame.getContentPane().add(chart);
		frame.setSize(1000,700);
		frame.addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosing(WindowEvent e)
			{
				super.windowClosing(e);
			}
			
		});
		frame.setVisible(true);
	}
	
	public void process()
	{
		System.out.println("--------------------------------------------------");
		System.out.println("Deserializing group hashes");
		deserializeHashSets();
		/*System.out.println("chopping advert hashSet");
		chopAdvertHashSet();
		System.out.println("Processing advert pieces");
		processAdvertHashPieces();*/
		System.out.println("Comparing hashes");
		matchHashes();
		System.out.println("\nprocessing bin frequencies");
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
	
	private void chopAdvertHashSet()
	{
		int startPoint=0;
		int stopPoint=groupHashes.size()-1;
		int flag=0;
		int displacement=groupHashes.size()/2;
		while(flag==0)
		{
			if(stopPoint<advertHashes.size())
			{
				List<Hash> piece=advertHashes.subList(startPoint, stopPoint);
				advertHashSetPieces.add(piece);
				startPoint=startPoint+displacement;
				if(stopPoint==(advertHashes.size()-1))
				{
					flag=1;
				}
				else
				{
					if((stopPoint+displacement)<advertHashes.size())
					{
						stopPoint=stopPoint+displacement;
					}
					else
					{
						stopPoint=advertHashes.size()-1;
					}
				}
			}
			else
			{
				flag=1;
				System.err.println("****It appears the advert is smaller than the hashSetGroup****\n# HashSetGroup.java #");
			}
		}
	}

	private void processAdvertHashPieces()
	{
		for (int i = 0; i < advertHashSetPieces.size(); i++)
		{
			List<Hash> currentPiece=advertHashSetPieces.get(i);
			List<Double> pieceBin=new ArrayList<Double>();;
			List<Integer> pieceBinFrequencies=new ArrayList<Integer>();;
			System.out.println("----New Piece being processed----");
			
			//compare current piece with grouphashes
			System.out.println("comparing hashes from piece to hashes from hashsetgroup");
			for (int groupPointer = 0; groupPointer < groupHashes.size(); groupPointer++) 
			{
				if(groupPointer%1000==0)
				{
					System.out.print("*");
				}
				for (int piecePointer = 0; piecePointer < currentPiece.size(); piecePointer++) 
				{
					/*int f1Diff=Math.abs(groupHashes.get(groupPointer).getF1()-currentPiece.get(piecePointer).getF1());
					int f2Diff=Math.abs(groupHashes.get(groupPointer).getF2()-currentPiece.get(piecePointer).getF2());
					int groupFreqDiff=groupHashes.get(groupPointer).getF1()-groupHashes.get(groupPointer).getF2();
					int advertFreqDiff=currentPiece.get(piecePointer).getF1()-currentPiece.get(piecePointer).getF2();*/
					if(groupHashes.get(groupPointer).getF1()==currentPiece.get(piecePointer).getF1() && groupHashes.get(groupPointer).getF2()==currentPiece.get(piecePointer).getF2() && groupHashes.get(groupPointer).getTimeDifference()==currentPiece.get(piecePointer).getTimeDifference())
					//if(groupFreqDiff==advertFreqDiff && groupHashes.get(groupPointer).getTimeDifference()==currentPiece.get(piecePointer).getTimeDifference())
					{
						double timeDifference=groupHashes.get(groupPointer).getRealTime()-currentPiece.get(piecePointer).getRealTime();
						if(pieceBin.contains(timeDifference))
						{
							pieceBinFrequencies.set(pieceBin.indexOf(timeDifference), pieceBinFrequencies.get(pieceBin.indexOf(timeDifference))+1);
							//the assumption here is that the frequency of a number has the same index as the number
						}
						else
						{
							pieceBin.add(timeDifference);
							pieceBinFrequencies.add(1);
						}
					}
				}
			}
			
			//process bin frequencies
			System.out.println("\nprocessing piece's bin frequencies");
			if(pieceBinFrequencies.size()>0)
			{
				int indexOfLargestFreq=0;
				//System.out.println("PIECE BIN FREQUENCIES");
				for (int j = 0; j < pieceBinFrequencies.size(); j++)
				{
					System.out.print(" "+pieceBinFrequencies.get(j)+" ");
					if(j==0)
					{
						indexOfLargestFreq=j;
					}
					else
					{
						if(pieceBinFrequencies.get(j)>pieceBinFrequencies.get(indexOfLargestFreq))
						{
							indexOfLargestFreq=j;
							//System.out.println(binFrequencies.get(i)+" is larger than "+binFrequencies.get(i-1));
						}
					}
				}
				System.out.println("\n THE LAGEST FREQ = "+pieceBinFrequencies.get(indexOfLargestFreq));
			}
			else
			{
				System.err.println("****none of the hashes from this piece and the channel appear to match****\n# HashSetGroup.java #");
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
				comparisonsDone++;
				if(groupHashes.get(i).getF1()==advertHashes.get(j).getF1() && groupHashes.get(i).getF2()==advertHashes.get(j).getF2() && groupHashes.get(i).getTimeDifference()==advertHashes.get(j).getTimeDifference())
				{
					matches++;
					double timeDifference=groupHashes.get(i).getRealTime()-advertHashes.get(j).getRealTime();
					/*if(bin.contains(timeDifference))
					{
						binFrequencies.set(bin.indexOf(timeDifference), binFrequencies.get(bin.indexOf(timeDifference))+1);//increment frequency of number by one
						//the assumption here is that the frequency of a number has the same index as the number
					}
					else
					{
						bin.add(timeDifference);
						binFrequencies.add(1);
					}*/
					
					if(binMap.containsKey(timeDifference))
					{
						binMap.put(timeDifference, binMap.get(timeDifference)+1);//increment frequency (key value) by one
					}
					else
					{
						binMap.put(timeDifference, 1);
					}
				}
			}
		}
	}
	
	private void processBinFrequencies()
	{
		/*if(binFrequencies.size()>0)
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
					if(binFrequencies.get(i)>binFrequencies.get(indexOfLargestFreq))
					{
						indexOfLargestFreq=i;
						//System.out.println(binFrequencies.get(i)+" is larger than "+binFrequencies.get(i-1));
					}
				}
			}
			System.out.println("\n THE LAGEST FREQ = "+binFrequencies.get(indexOfLargestFreq));
		}*/
		if(binMap.size()>0)
		{
			System.out.println("BIN FREQUENCIES");
			long largestFrequency=0;
			SortedSet<Double> keys=new TreeSet<Double>(binMap.keySet());
			for(Double key : keys)
			{
				System.out.print(" "+key+" = "+binMap.get(key)+" ");
				//trace.addPoint(key, binMap.get(key));
				if(binMap.get(key)>largestFrequency)
				{
					largestFrequency=binMap.get(key);
				}
			}
			System.out.println("\n largest frequency = "+largestFrequency);
			long a=largestFrequency*10000000;
			a=a/comparisonsDone;
			long b=largestFrequency*10000;
			b=b/matches;
			System.out.println(comparisonsDone+" comparisons done. Ratio btwn largest freq & comparisons done = "+a);
			System.out.println(matches+" - total matches. Ratio btwn largest freq & total matches = "+b);
			/*try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		else
		{
			System.err.println("****none of the hashes from the advert and the channel appear to match****\n# HashSetGroup.java #");
		}
		
	}
}
