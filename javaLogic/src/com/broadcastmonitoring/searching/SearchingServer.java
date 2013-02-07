package com.broadcastmonitoring.searching;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.broadcastmonitoring.database.Database;
import com.broadcastmonitoring.indexing.Frame;
import com.broadcastmonitoring.indexing.HashMap;
import com.broadcastmonitoring.utils.StreamGobbler;

public class SearchingServer
{
	public static final int MEDIA_TYPE_AUDIO=0;
	public static final int MEDIA_TYPE_VIDEO=1;
	private static Scanner in=new Scanner(System.in);
	
	private static final int hashmapSize=10;
	private static final int frameSize=2048;
	private static final int redundantThreshold=2;
	private static final int startFreq=50;
	private static final int targetZoneSize=5;
	private static final int anchor2peakMaxFreqDiff=5;
	public static void main(String[] args)
	{
		//user uploads the video or audio
		int mediaType=getMediaType();
		String name=getSearchableContentName();
		String url=getAdvertURL();
		
		//if advertisement is video, rip audio from video
		if(mediaType==MEDIA_TYPE_VIDEO)
		{
			//AudioRipper audioRipper=new AudioRipper(url);
			//url=audioRipper.rip();\
			url=rip(url);
		}
		
		//convert the audio WAV file
		url=convertAudioFile(url);
		
		//add searchable content to database
		int id=addSearchableContentToDB(name);
		
		//get stream from audio WAV file
		generateHashes(url,id);
		
		//generate hashes from stream
		
		//store hashes in database
		
		//user selects channel to search
		
		//server selects key advert piece
		
		//key advert piece is used to search through channel hashSets
		
		//if key matches with hashSet then run the search algorithm
		in.close();
	}
	
	private static int getMediaType()
	{
		System.out.println("What media type will you use to search?\n 0 for audio\n 1 for video");
		int mediaType=in.nextInt();
		in.nextLine();
		if(mediaType==0)
		{
			return MEDIA_TYPE_AUDIO;
		}
		else if(mediaType==1)
		{
			return MEDIA_TYPE_VIDEO;
		}
		else
		{
			System.err.println("****Input error, the user entered an unrecognised command.\n# SearcingServer.java #****");
		}
		return -1;
	}
	
	private static String getSearchableContentName()
	{
		System.out.println("Enter the searchable content's name");
		String name=null;
		name = in.nextLine();
		return name;
	}
	
	private static String getAdvertURL()
	{
		System.out.println("Enter absolute url of the searchable content");
		String url=null;
		url = in.nextLine();
		System.out.println("you entered "+url);
		return url;
	}
	
	private static String rip(String url)//logic in this method should not be run in a different thread from parent object
	{
		try
		{
			Date date=new Date();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
			sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
			String timestamp=sdf.format(date);
			String newURL="/tmp/"+timestamp+".wav";
			System.out.println(newURL);
			String command="mplayer -slave -ao pcm:fast:file="+newURL+" -vo null -vc null "+url;
			Process mPlayerProcess=Runtime.getRuntime().exec(command);
			
			StreamGobbler errorGobbler=new StreamGobbler(mPlayerProcess.getErrorStream(), "ERROR");
			StreamGobbler outputGobbler=new StreamGobbler(mPlayerProcess.getInputStream(), "OUTPUT");
			errorGobbler.start();
			outputGobbler.start();
			
			mPlayerProcess.waitFor();
			
			System.out.println("finished ripping audio");
			return newURL;
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static String convertAudioFile(String url)
	{
		File audioFile=new File(url);
		try
		{
			AudioInputStream audioInputStream=AudioSystem.getAudioInputStream(audioFile);
			AudioFormat newAudioFormat=new AudioFormat(44100, 8, 1, true, true);
			if(audioInputStream.getFormat()==newAudioFormat)
			{
				System.out.println("no need to convert audio file");
				audioInputStream.close();
				return url;
			}
			else
			{
				System.out.println("converting audio file");
				Date date=new Date();
				SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
				sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
				String timestamp=sdf.format(date);
				String newURL="/tmp/"+timestamp+"converted.wav";
				AudioInputStream newAudioInputStream=AudioSystem.getAudioInputStream(newAudioFormat,audioInputStream);
				File newAudioFile=new File(newURL);
				AudioSystem.write(newAudioInputStream, AudioFileFormat.Type.WAVE, newAudioFile);
				audioInputStream.close();
				newAudioInputStream.close();
				return newURL;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private static int addSearchableContentToDB(String name)
	{
		System.out.println("Adding searchable content to database");
		
		Database database=new Database("broadcast_monitoring", "root", "jason");
		
		//get last id if any and increment
		int id=-1;
		ResultSet resultSet=database.runSelectQuery("SELECT id FROM `searchable_content` ORDER BY time DESC LIMIT 1");
		try 
		{
			if(resultSet!=null && resultSet.next())
			{
				id=resultSet.getInt(1)+1;
			}
			else
			{
				id=1;
			}
		}
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//insert searchable content to db
		if(id!=-1)
		{
			Date date=new Date();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
			String timestamp=sdf.format(date);
			
			database.initInsertStatement("INSERT INTO `searchable_content`(id,name,time) VALUES(?,?,?)");
			database.addColumnValue(id);
			database.addColumnValue(name);
			database.addColumnValue(timestamp);
			
			database.executeInsert();
			
			return id;
		}
		return -1;
	}
	
	private static void generateHashes(String url, int id)
	{
		File audioFile=new File(url);
		try 
		{
			AudioInputStream audioInputStream=AudioSystem.getAudioInputStream(audioFile);
			ByteArrayOutputStream arrayOutputStream=new ByteArrayOutputStream();
			AudioFormat audioFormat=audioInputStream.getFormat();
			
			//TODO: how to control volume
			
			System.out.println("Sample Rate: "+audioFormat.getSampleRate());
			System.out.println("Sample Size in bits: "+audioFormat.getSampleSizeInBits());
			System.out.println("Channels: "+audioFormat.getChannels());
			System.out.println("Is big endian: "+audioFormat.isBigEndian());
			
			int counter=1;
			Frame[] frameBuffer=new Frame[hashmapSize];
			int frameCount=1;
			int timeCounter=0;
			byte[] buffer=new byte[frameSize];
			while((counter=audioInputStream.read(buffer, 0,buffer.length))!=-1)
			{
				if(counter>0 && frameCount<=frameBuffer.length)
				{
					arrayOutputStream.write(buffer,0,counter);
					timeCounter++;
					
					Date date=new Date();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
					String timestamp=sdf.format(date);
					
					frameBuffer[frameCount-1]=new Frame(buffer,redundantThreshold,startFreq,timeCounter, timestamp);
					if(frameCount==hashmapSize)
					{
						HashMap hashMap=new HashMap(frameBuffer,targetZoneSize, anchor2peakMaxFreqDiff,id,1);//parent type for channel is 0
						hashMap.generateHashes();
						frameCount=1;
						frameBuffer=new Frame[hashmapSize];//make you use the framebuffer you passed in the hashmap before you reinitialize this framebuffer
					}
					else
					{
						frameCount++;
					}
				}
				else
				{
					if(counter<=0)
					{
						System.out.println("Finished indexing the audio file");
					}
					if(frameCount>frameBuffer.length)
					{
						System.err.println("****The number of frames has just exceeded what the frameBuffer can handle****\n# Streamer.java #");
					}
				}
			}
			audioInputStream.close();
			arrayOutputStream.close();
			//return arrayOutputStream.toByteArray();
			
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
