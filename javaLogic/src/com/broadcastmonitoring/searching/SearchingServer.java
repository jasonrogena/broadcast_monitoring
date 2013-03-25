package com.broadcastmonitoring.searching;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.broadcastmonitoring.indexing.Hash;
import com.broadcastmonitoring.indexing.HashMap;
import com.broadcastmonitoring.utils.StreamGobbler;

public class SearchingServer
{
	//TODO: THERE IS DEFFINATELY A PROBLEM WITH THE SEARCH SERVER. IT DOES NOT BEHAVE THE SAME WAY AS THE SEARCH HACK
	//TODO: best solution seems to let mplayer play the media and the server capture mplayer standard output
	public static final int MEDIA_TYPE_AUDIO=0;
	public static final int MEDIA_TYPE_VIDEO=1;
	private static Scanner in=new Scanner(System.in);
	
	private static final int hashmapSize=10;
	private static final int frameSize=2048;
	private static final int redundantThreshold=2;
	private static final int startFreq=50;
	private static final int targetZoneSize=5;
	private static final int anchor2peakMaxFreqDiff=1000;
	private static final int sampledFrequencies=5;
	private static final int hashSetGroupSize=7;
	private static final int multiplyer=2;
	private static final boolean limitKeyPieceSize=true;
	private static final int smoothingWidth=101;
	private static final String hashDir="../bin/hashes";
	
	public static void main(String[] args)
	{
		int id;
		//ask user if searchable content has already been indexed
		if(isSearchableContentNew()==false)
		{
			id=getSearchableContentId();
		}
		else
		{
			/*if content is new*/
			
			//user uploads the video or audio
			int mediaType=getMediaType();
			String name=getSearchableContentName();
			String url=getAdvertURL();
			
			//if searchable content is video, rip audio from video
			if(mediaType==MEDIA_TYPE_VIDEO)
			{
				//AudioRipper audioRipper=new AudioRipper(url);
				//url=audioRipper.rip();\
				url=rip(url);
			}
			
			//convert the audio WAV file
			url=convertAudioFile(url);
			
			//add searchable content to database
			id=addSearchableContentToDB(name);
			
			//get stream from audio WAV file, generate hashes from stream and store hashes in database
			generateHashes(url,id,smoothingWidth);
			
			/*end if*/
		}
		
		//server selects key searchable content piece
		final List<Hash> key=getKeyPiece(hashSetGroupSize, id, hashDir, limitKeyPieceSize, multiplyer);
		
		//user selects channel to search
		int channelNumber=getChannelNumber();
		in.close();
		
		//key searchable content piece is used to search through channel hashSets
		while(true)
		{
			searchKeyInChannel(id, channelNumber, hashSetGroupSize, key, hashDir);	
		}
		
		//if key matches with hashSet then run the search algorithm
		
	}
	
	private static boolean isSearchableContentNew()
	{
		System.out.println("Has the content you want to search been indexed before?\n 0 for no\n 1 for yes");
		int result=in.nextInt();
		in.nextLine();
		if(result==0)//content has not been indexed therefore new
		{
			return true;
		}
		else if(result==1)
		{
			return false;
		}
		else
		{
			System.err.println("****Input error, the user entered an unrecognised command.\nAssuming that the content has not been indexed!\n# SearcingServer.java #****");
			return true;
		}
	}
	
	private static int getSearchableContentId()
	{
		System.out.println("Enter the searchable content's ID");
		int id=in.nextInt();
		in.nextLine();
		return id;
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
			String command="mplayer -slave -ao pcm:fast:file="+newURL+" -vo null -vc null "+url;//TODO: does reducing volume have a bad impact on recognition
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
			
			database.close();
			return id;
		}
		
		database.close();
		return -1;
	}
	
	private static void generateHashes(String url, int id, int smoothingWidth)
	{
		File audioFile=new File(url);
		try 
		{
			AudioInputStream audioInputStream=AudioSystem.getAudioInputStream(audioFile);//file headers stripped here
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
						HashMap hashMap=new HashMap(frameBuffer,targetZoneSize, anchor2peakMaxFreqDiff,id,1,smoothingWidth, sampledFrequencies);//parent type for channel is 0
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
	
	private static List<Hash> getKeyPiece(int hashSetGroupSize, int id, String hashDir, boolean limit, int multiplyer)
	{
		System.out.println("Determining key for searchable content");
		//set keyPiece size in the number of hashSets
		int keyPieceSize=hashSetGroupSize*multiplyer;
		
		List<Hash> sContentHashes=new ArrayList<Hash>();
		
		//get hashsets from database
		Database database=new Database("broadcast_monitoring", "root", "jason");
		ResultSet fetchedHashSetUrls;
		if(limit==true)
		{
			fetchedHashSetUrls=database.runSelectQuery("SELECT url FROM `hash_set` WHERE parent = "+String.valueOf(id)+" AND `parent_type` = 1 ORDER BY `start_real_time` ASC LIMIT "+String.valueOf(keyPieceSize));
		}
		else
		{
			fetchedHashSetUrls=database.runSelectQuery("SELECT url FROM `hash_set` WHERE parent = "+String.valueOf(id)+" AND `parent_type` = 1 ORDER BY `start_real_time` ASC");
		}
		
		/*the above query selects the first hashSets of the searchable content
		 * this is what will be the key for the searchable content*/
		if(fetchedHashSetUrls!=null)
		{
			try
			{
				while(fetchedHashSetUrls.next())
				{
					InputStream hashSetSerFile=new FileInputStream(hashDir+"/"+fetchedHashSetUrls.getString(1));
					InputStream buffer=new BufferedInputStream(hashSetSerFile);
					ObjectInput objectInput=new ObjectInputStream(buffer);
					List<Hash> fetchedHashSet=(List<Hash>)objectInput.readObject();
					if(fetchedHashSet!=null)
					{
						sContentHashes.addAll(fetchedHashSet);
					}
					else
					{
						System.err.println("****"+fetchedHashSetUrls.getString(1)+" was not deserialized****\n# SearchServer.java #");
					}
					objectInput.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		database.close();
		System.out.println("Number of hashes in key : "+sContentHashes.size());
		return sContentHashes;
	}
	
	private static int getChannelNumber()
	{
		System.out.println("What channel do you want to search");
		int channelNumber=in.nextInt();
		in.nextLine();
		return channelNumber;
	}
	
	private static void searchKeyInChannel(int id, int channel, int hashSetGroupSize, List<Hash> key, String hashDir)
	{
		//get search pointer from database
		Database database=new Database("broadcast_monitoring", "root", "jason");
		ResultSet resultSet=database.runSelectQuery("SELECT `last_start_real_time` FROM `search_pointer` WHERE parent = "+String.valueOf(id)+" AND channel = "+String.valueOf(channel));
		if(resultSet!=null)
		{
			try
			{
				System.out.println("Obtaining the search pointer");
				int lastStartRealTime;
				if(resultSet.next())
				{
					lastStartRealTime=resultSet.getInt(1);
					System.out.println("Pointer found. Pointing to time : "+lastStartRealTime);
				}
				else//no search pointer exists
				{
					System.out.println("Pointer not found, adding a new pointer to the database");
					database.initInsertStatement("INSERT INTO `search_pointer`(parent,channel,`last_hash_set_id`,`last_start_real_time`) VALUES(?,?,?,?)");
					database.addColumnValue(id);
					database.addColumnValue(channel);
					database.addColumnValue(0);
					database.addColumnValue(0);
					
					database.executeInsert();
					
					lastStartRealTime=0;
				}
				
				//check if the first hashSets after the pointer can form a hashSetGroup
				ResultSet fetchedNumberOfHashSets=database.runSelectQuery("SELECT COUNT(id) FROM `hash_set` WHERE parent = "+String.valueOf(channel)+" AND `parent_type` = 0 AND `start_real_time` > "+String.valueOf(lastStartRealTime)+" ORDER BY `start_real_time` ASC");
				if(fetchedNumberOfHashSets!=null)
				{
					if(fetchedNumberOfHashSets.next())
					{
						int numberofHashSets=fetchedNumberOfHashSets.getInt(1);
						System.out.println("number of hash sets in database after last firstrealtime : "+numberofHashSets);
						if(numberofHashSets>=hashSetGroupSize)//hash sets can form a group
						{
							//fetch
							ResultSet fetchedChannelHashUrls=database.runSelectQuery("SELECT url,`start_real_time` FROM `hash_set` WHERE parent = "+String.valueOf(channel)+" AND `parent_type` = 0 AND `start_real_time` > "+String.valueOf(lastStartRealTime)+" ORDER BY `start_real_time` ASC");
							if(fetchedChannelHashUrls!=null)
							{
								int currentGroupNumber=0;
								List<String> groupUrls=new ArrayList<String>();
								while(fetchedChannelHashUrls.next())
								{
									currentGroupNumber++;
									groupUrls.add(fetchedChannelHashUrls.getString(1));
									if(currentGroupNumber==hashSetGroupSize)//the last hash set in the group
									{
										//update pointer
										int newLastStartRealTime=fetchedChannelHashUrls.getInt(2);
										System.out.println("Updating last firstRealTime to :"+newLastStartRealTime);
										database.runUpdateQuery("UPDATE `search_pointer` SET `last_start_real_time` = "+String.valueOf(newLastStartRealTime)+" WHERE parent = "+String.valueOf(id)+" AND channel = "+String.valueOf(channel));
										
										//compare
										KeyProcessor keyProcessor=new KeyProcessor(groupUrls, key, hashDir);
										keyProcessor.process();
										
										//set resultset to last row
										fetchedChannelHashUrls.last();
									}
								}
							}
							//update search pointer
						}
					}
				}
				
				//if so, search hashsetgroup in key and update search pointer
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		database.close();
	}
}
