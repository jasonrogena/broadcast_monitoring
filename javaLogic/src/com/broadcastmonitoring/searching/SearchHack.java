package com.broadcastmonitoring.searching;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.broadcastmonitoring.database.Database;
import com.broadcastmonitoring.indexing.Hash;

public class SearchHack
{
	public static void main(String[] args)
	{
		Scanner in=new Scanner(System.in);
		Database database=new Database("broadcast_monitoring", "root", "jason");
		final List<Hash> advertHashes=new ArrayList<Hash>();;
		String dir="../bin/hashes";
		int hashSetGroupSize=7;//3 will be somewhat equal to 3secs worth of hashes
		
		//check if hash dir exists
		File directory=new File(dir);
		if(!directory.exists())
		{
			System.err.println("****"+dir+" does not exist****\n# SearchingServer.java #");
		}
		
		//get user input
		System.out.println("Enter the id of the advert you want to search");
		int advertId=in.nextInt();
		
		System.out.println("Enter the channel you want to search for the advert");
		int channelId=in.nextInt();
		
		//fetch hash sets for the advert
		ResultSet fetchedHashSetUrls=database.runSelectQuery("SELECT url FROM `hash_set` WHERE parent = "+String.valueOf(advertId)+" AND `parent_type` = 0");
		if(fetchedHashSetUrls!=null)
		{
			try 
			{
				while(fetchedHashSetUrls.next())//for each of the urls deserialize corresponding hashset
				{
					//System.out.println(fetchedHashSetUrls.getString(1));
					InputStream hashSetSerFile=new FileInputStream(dir+"/"+fetchedHashSetUrls.getString(1));
					InputStream buffer=new BufferedInputStream(hashSetSerFile);
					ObjectInput objectInput=new ObjectInputStream(buffer);
					List<Hash> fetchedHashSet=(List<Hash>)objectInput.readObject();
					if(fetchedHashSet!=null)
					{
						advertHashes.addAll(fetchedHashSet);
						//System.out.println(advertHashes.size());
					}
					else
					{
						System.err.println("****"+fetchedHashSetUrls.getString(1)+" was not deserialized****\n# SearchServer.java #");
					}
					objectInput.close();
				}
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			//Fetch all the hash_sets for the channel
			ResultSet fetchedChannelHashUrls=database.runSelectQuery("SELECT url FROM `hash_set` WHERE parent = "+String.valueOf(channelId)+" AND `parent_type` = 0 ORDER BY `start_real_time` ASC");
			/*hashsets are ordered based on the realTime of the first hash in a hashset. this appears to be more accurate than 
			ordering the hashes based on their ids since hashsets are inserted into the db asynchronously*/
			if(fetchedChannelHashUrls!=null)
			{
				try
				{
					int currentGroupNumber=0;
					//TODO: consider overlapping groups
					List<String> groupUrls=new ArrayList<String>();
					while(fetchedChannelHashUrls.next())
					{
						currentGroupNumber++;
						groupUrls.add(fetchedChannelHashUrls.getString(1));
						/*if(currentGroupNumber==hashSetGroupSize || fetchedChannelHashUrls.isLast())
						{
							HashSetGroup hashSetGroup=new HashSetGroup(groupUrls, advertHashes, dir);
							//hashSetGroup.showHashSetUrls();
							hashSetGroup.process();
							//TODO: do the processing for the group
							currentGroupNumber=0;
							groupUrls=new ArrayList<String>();
						}*/
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				System.err.println("****No hash sets where found for the specified channel****\n# SerchingServer.java #");
			}
		}
		else
		{
			System.err.println("****No hash sets where found for the specified advert****\n# SerchingServer.java #");
		}
		
		database.close();
		in.close();
	}
}
