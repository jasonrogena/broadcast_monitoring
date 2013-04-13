package com.broadcastmonitoring.searching;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import com.broadcastmonitoring.indexing.Hash;

public class KeyProcessor extends HashSetGroup
{
	
	public KeyProcessor(List<String> hashSetUrls, List<Hash> advertHashes, String dir, int parent, int channel, String channelStartTime, String channelStopTime, String scStartTime, String scStopTime)
	{
		super(hashSetUrls, advertHashes, dir, parent, channel, channelStartTime, channelStopTime, scStartTime, scStopTime);
	}
}
