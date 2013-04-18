package com.broadcastmonitoring.searching;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SearchResult 
{
	private final SimpleStringProperty startTime;
	private final SimpleStringProperty stopTime;
	private final SimpleIntegerProperty searchableContent;
	private final SimpleIntegerProperty channel;
	private final SimpleIntegerProperty largestBinFreq;
	private final SimpleIntegerProperty probabilityRatio;
	public SearchResult(int searchableContent, int channel, String startTime, String stopTime, int largestFreq, int ProbabilityRatio)
	{
		this.searchableContent=new SimpleIntegerProperty(searchableContent);
		this.channel=new SimpleIntegerProperty(channel);
		this.startTime=new SimpleStringProperty(startTime);
		this.stopTime=new SimpleStringProperty(stopTime);
		this.largestBinFreq=new SimpleIntegerProperty(largestFreq);
		this.probabilityRatio=new SimpleIntegerProperty(ProbabilityRatio);
	}
	
	public int getChannel()
	{
		return channel.get();
	}
	
	public int getSearchableContent()
	{
		return searchableContent.get();
	}
	
	public String getStartTime()
	{
		return startTime.get();
	}
	
	public String getStopTime()
	{
		return stopTime.get();
	}
	
	public int getLargestBinFreq()
	{
		return largestBinFreq.get();
	}
	
	public int getProbabilityRatio()
	{
		return probabilityRatio.get();
	}
}
