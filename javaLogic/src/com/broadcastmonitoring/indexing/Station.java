package com.broadcastmonitoring.indexing;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Station 
{
	private final SimpleStringProperty station;
	private final SimpleStringProperty type;
	private final SimpleStringProperty startTime;
	private final SimpleStringProperty stationInterface;
	private final SimpleIntegerProperty number;
	private final SimpleBooleanProperty running;
	
	public Station(String station, String type, String startTime, String stationInterface, int number, boolean running)
	{
		this.station=new SimpleStringProperty(station);
		this.type=new SimpleStringProperty(type);
		this.startTime=new SimpleStringProperty(startTime);
		this.stationInterface=new SimpleStringProperty(stationInterface);
		this.number=new SimpleIntegerProperty(number);
		this.running=new SimpleBooleanProperty(running);
		
	}
	
	public boolean isRunning()
	{
		return running.get();
	}
	
	public void setRunning(boolean status)
	{
		this.running.set(status);
		
	}
	
	public int getNumber()
	{
		return number.get();
	}
	
	public void setNumber(int number)
	{
		this.number.set(number);
	}
	
	public String getStation()
	{
		return station.get();
	}
	
	public void setStation(String station)
	{
		this.station.set(station);
	}
	
	public String getType()
	{
		return type.get();
	}
	
	public void setType(String type)
	{
		this.type.set(type);
	}
	
	public String getStartTime()
	{
		return startTime.get();
	}
	
	public void setStartTime(String startTime)
	{
		this.startTime.set(startTime);
	}
	
	public String getStationInterface()
	{
		return this.stationInterface.get();
	}
	
	public void setStationInterface(String stationInterface)
	{
		this.stationInterface.set(stationInterface);
	}

}
