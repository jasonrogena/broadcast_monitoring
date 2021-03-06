package com.broadcastmonitoring.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread
{
    InputStream is;
    String type;
    boolean silent;
    
    public StreamGobbler(InputStream is, String type, boolean silent)
    {
        this.is = is;
        this.type = type;
        this.silent=silent;
    }
    
    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
            {
            	if(silent==false)
            		System.out.println(type + ">" + line);    
            }
         }
        catch (IOException ioe)
        {
        	if(silent==false)
        		ioe.printStackTrace();  
        }
    }
}