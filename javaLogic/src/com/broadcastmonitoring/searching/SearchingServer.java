package com.broadcastmonitoring.searching;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import com.broadcastmonitoring.database.Database;
import com.broadcastmonitoring.indexing.Frame;
import com.broadcastmonitoring.indexing.Hash;
import com.broadcastmonitoring.indexing.HashMap;
import com.broadcastmonitoring.indexing.Streamer;
import com.broadcastmonitoring.utils.Log;
import com.broadcastmonitoring.utils.StreamGobbler;
import com.broadcastmonitoring.utils.Time;

public class SearchingServer extends Application implements Initializable
{
	//TODO: THERE IS DEFFINATELY A PROBLEM WITH THE SEARCH SERVER. IT DOES NOT BEHAVE THE SAME WAY AS THE SEARCH HACK
	//TODO: best solution seems to let mplayer play the media and the server capture mplayer standard output
	public static final int MEDIA_TYPE_AUDIO=0;
	public static final int MEDIA_TYPE_VIDEO=1;
	private static Scanner in=new Scanner(System.in);
	
	@FXML private TableView<Log> logTable;
	@FXML private TableColumn<Log, String> timeTC;
	@FXML private TableColumn<Log, String> tagTC;
	@FXML private TableColumn<Log, String> messageTC;
	@FXML private ScrollPane settingsScrollPane;
	@FXML private GridPane settingsTableGridPane;
	@FXML private TextField hashSetGroupSizeTF;
	@FXML private TextField keyPieceMultiplierTF;
	@FXML private TextField limitKeyPieceSizeTF;
	@FXML private TextField sampleRateTF;
	@FXML private TextField frameSizeTF;
	@FXML private TextField hashMapSizeTF;
	@FXML private TextField redundantThresholdTF;
	@FXML private TextField highPassFilterTF;
	@FXML private TextField targetZoneSizeTF;
	@FXML private TextField anchor2PeakMaxFreqDiffTF;
	@FXML private TextField baseForSampledFreqTF;
	@FXML private Button settingsCancelButton;
	@FXML private Button settingsSaveButton;
	@FXML private Button clearLogButton;
	@FXML private AnchorPane searchTabAnchorPane;
	private Node searchNode=null;
	private ComboBox<String> stationToSearchCB;
	private Button searchButton;
	private TextField searchableContentNameTF;
	private TextField searchableContentLocationTF;
	
	private static final ObservableList<Log> logs=FXCollections.observableArrayList();
	
	/*private static final float sampleRate=44100;
	private static final int hashmapSize=10;
	private static final int frameSize=2048;
	private static final int redundantThreshold=2;
	private static final int startFreq=50;
	private static final int targetZoneSize=5;
	private static final int anchor2peakMaxFreqDiff=1000;
	private static final int sampledFrequencies=5;
	private static final int hashSetGroupSize=7;
	private static final int keyPieceMultiplier=2;
	private static final boolean limitKeyPieceSize=true;
	private static final int smoothingWidth=101;*/
	private static float sampleRate;
	private static int hashmapSize;
	private static int frameSize;
	private static int redundantThreshold;
	private static int startFreq;
	private static int targetZoneSize;
	private static int anchor2peakMaxFreqDiff;
	private static int sampledFrequencies;
	
	private static int hashSetGroupSize;
	private static int keyPieceMultiplier;
	private static boolean limitKeyPieceSize;
	
	private static int smoothingWidth=101;
	private static final String hashDir="../bin/hashes";
	
	private int stopSearchFlag=0;
	
	public static void main(String[] args)
	{
		try
		{
			Database database=new Database("broadcast_monitoring", "root", "jason");
			ResultSet resultSet=database.runSelectQuery("SELECT name,value FROM variables WHERE type = 1 OR type = 2");
			while(resultSet.next())
			{
				if(resultSet.getString("name").equals("sampleRate"))
				{
					sampleRate=resultSet.getFloat("value");
					logs.add(new Log(Time.getTime("gmt"), "INFO", "sampleRate initialized to "+String.valueOf(sampleRate)));
				}
				else if(resultSet.getString("name").equals("limitKeyPieceSize"))
				{
					if(resultSet.getInt("value")==1)
					{
						limitKeyPieceSize=true;
					}
					else
					{
						limitKeyPieceSize=false;
					}
					logs.add(new Log(Time.getTime("gmt"), "INFO", "limitKeyPieceSize initialized to "+String.valueOf(limitKeyPieceSize)));
				}
				else if(resultSet.getString("name").equals("keyPieceMultiplier"))
				{
					keyPieceMultiplier=resultSet.getInt("value");
					logs.add(new Log(Time.getTime("gmt"), "INFO", "keyPieceMultiplier initialized to "+String.valueOf(keyPieceMultiplier)));
				}
				else if(resultSet.getString("name").equals("hashSetGroupSize"))
				{
					hashSetGroupSize=resultSet.getInt("value");
					logs.add(new Log(Time.getTime("gmt"), "INFO", "hashSetGroupSize initialized to "+String.valueOf(hashSetGroupSize)));
				}
				else if(resultSet.getString("name").equals("frameSize"))
				{
					frameSize=resultSet.getInt("value");
					logs.add(new Log(Time.getTime("gmt"), "INFO", "frameSize initialized to "+String.valueOf(frameSize)));
				}
				else if(resultSet.getString("name").equals("hashmapSize"))
				{
					hashmapSize=resultSet.getInt("value");
					logs.add(new Log(Time.getTime("gmt"), "INFO", "hashmapSize initialized to "+String.valueOf(hashmapSize)));
				}
				else if(resultSet.getString("name").equals("redundantThreshold"))
				{
					redundantThreshold=resultSet.getInt("value");
					logs.add(new Log(Time.getTime("gmt"), "INFO", "redundantThreshold initialized to "+String.valueOf(redundantThreshold)));
				}
				else if(resultSet.getString("name").equals("startFreq"))
				{
					startFreq=resultSet.getInt("value");
					logs.add(new Log(Time.getTime("gmt"), "INFO", "startFreq initialized to "+String.valueOf(startFreq)));
				}
				else if(resultSet.getString("name").equals("targetZoneSize"))
				{
					targetZoneSize=resultSet.getInt("value");
					logs.add(new Log(Time.getTime("gmt"), "INFO", "targetZoneSize initialized to "+String.valueOf(targetZoneSize)));
				}
				else if(resultSet.getString("name").equals("anchor2peakMaxFreqDiff"))
				{
					anchor2peakMaxFreqDiff=resultSet.getInt("value");
					logs.add(new Log(Time.getTime("gmt"), "INFO", "anchor2peakMaxFreqDiff initialized to "+String.valueOf(anchor2peakMaxFreqDiff)));
				}
				else if(resultSet.getString("name").equals("sampledFrequencies"))
				{
					sampledFrequencies=resultSet.getInt("value");
					logs.add(new Log(Time.getTime("gmt"), "INFO", "sampledFrequencies initialized to "+String.valueOf(sampledFrequencies)));
				}
			}
			database.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		launch(args);
		/*int id=-1;
		System.out.println("Which method do you want to use to index the searchable content?\n 1. Using internal conversions and bytearrays (buggy)\n 2. Using a loopback cable");
		int method=in.nextInt();
		in.nextLine();
		if(method==1)
		{
			id=method1();
		}
		else if(method==2)
		{
			id=method2();
		}
		
		//server selects key searchable content piece
		final List<Hash> key=getKeyPiece(hashSetGroupSize, id, hashDir, limitKeyPieceSize, keyPieceMultiplier);
		
		//user selects channel to search
		int channelNumber=getChannelNumber();
		in.close();
		
		//key searchable content piece is used to search through channel hashSets
		while(true)
		{
			searchKeyInChannel(id, channelNumber, hashSetGroupSize, key, hashDir);	
		}
		*/
		//if key matches with hashSet then run the search algorithm
		
		
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		initSearchNode();
		
		MyEventHandler eventHandler=new MyEventHandler();
		
		timeTC.setCellValueFactory(new PropertyValueFactory<Log, String>("time"));
		timeTC.setEditable(false);
		tagTC.setCellValueFactory(new PropertyValueFactory<Log, String>("tag"));
		tagTC.setEditable(false);
		messageTC.setCellValueFactory(new PropertyValueFactory<Log, String>("message"));
		messageTC.setEditable(false);
		
		logTable.setItems(logs);
		
		clearLogButton.setOnAction(eventHandler);
		
		settingsScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		settingsScrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		settingsScrollPane.setContent(settingsTableGridPane);
		initSettings();
		settingsCancelButton.setOnAction(eventHandler);
		settingsSaveButton.setOnAction(eventHandler);
	}

	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		Parent root;
		try
		{
			root=FXMLLoader.load(getClass().getResource("searching_server.fxml"));
			primaryStage.setTitle("Searching Server");
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
					{
				
				@Override
				public void handle(WindowEvent arg0) 
				{
					System.exit(0);
				}
			});
			
			primaryStage.setScene(new Scene(root));
			primaryStage.show();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			//TODO: add log to log list
		}
	}
	
	private void initSearchNode()
	{
		try 
		{
			Node searchNode;
			searchTabAnchorPane.getChildren().clear();
			searchNode = FXMLLoader.load(getClass().getResource("search_node.fxml"));
			searchTabAnchorPane.getChildren().addAll(searchNode);
			this.searchNode=searchNode;
			
			initStationToSearchCB();
			initSearchButton();
			initSearchableContentNameTF();
			initSearchableContentLocationTF();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			logs.add(new Log(Time.getTime("gmt"), "ERROR", "IOException thrown while trying to load search_node.fxml"));
		}
	}
	
	private void initSearchableContentNameTF()
	{
		if(searchNode!=null)
		{
			searchableContentNameTF=(TextField)searchNode.lookup("#searchableContentNameTF");
		}
		else
		{
			logs.add(new Log(Time.getTime("gmt"), "ERROR", "searchNode is null therefore it's child searchableContentNameTF cannot be located"));
		}
	}
	private void initSearchableContentLocationTF()
	{
		if(searchNode!=null)
		{
			searchableContentLocationTF=(TextField)searchNode.lookup("#searchableContentLocationTF");
		}
		else
		{
			logs.add(new Log(Time.getTime("gmt"), "ERROR", "searchNode is null therefore it's child searchableContentLocationTF cannot be located"));
		}
	}
	
	private void initSearchButton()
	{
		if(searchNode!=null)
		{
			searchButton=(Button)searchNode.lookup("#searchButton");
			searchButton.setDisable(false);
			searchButton.setOnAction(new MyEventHandler());
		}
		else
		{
			logs.add(new Log(Time.getTime("gmt"), "ERROR", "searchNode is null therefore it's child searchButton cannot be located"));
		}
	}
	
	private void initStationToSearchCB()
	{
		if(searchNode!=null)
		{
			stationToSearchCB=(ComboBox<String>)searchNode.lookup("#stationToSearchCB");
			ExecutorService executorService=Executors.newFixedThreadPool(1);
			StationHandler stationHandler=new StationHandler(stationToSearchCB);
			Future<String> future=executorService.submit(stationHandler);
		}
		else
		{
			logs.add(new Log(Time.getTime("gmt"), "ERROR", "searchNode is null therefore it's child stationToSearchCB cannot be located"));
		}
	}
	
	private class StationHandler implements Callable<String>
	{
		private ComboBox<String> stationToSearchCB;
		
		public StationHandler(ComboBox<String> stationToSearchCB) 
		{
			this.stationToSearchCB=stationToSearchCB;
		}
		@Override
		public String call() throws Exception 
		{
			Database database=new Database("broadcast_monitoring", "root", "jason");
			ResultSet resultSet=database.runSelectQuery("SELECT name FROM channel");
			ObservableList<String> stationNames=FXCollections.observableArrayList();
			while(resultSet.next())
			{
				stationNames.add(resultSet.getString(1));
			}
			stationToSearchCB.setItems(stationNames);
			database.close();
			return null;
		}
		
	}
	
	private void initSettings()
	{
		sampleRateTF.setText(String.valueOf(sampleRate));
		frameSizeTF.setText(String.valueOf(frameSize));
		hashMapSizeTF.setText(String.valueOf(hashmapSize));
		redundantThresholdTF.setText(String.valueOf(redundantThreshold));
		highPassFilterTF.setText(String.valueOf(startFreq));
		targetZoneSizeTF.setText(String.valueOf(targetZoneSize));
		anchor2PeakMaxFreqDiffTF.setText(String.valueOf(anchor2peakMaxFreqDiff));
		baseForSampledFreqTF.setText(String.valueOf(sampledFrequencies));
		hashSetGroupSizeTF.setText(String.valueOf(hashSetGroupSize));
		keyPieceMultiplierTF.setText(String.valueOf(keyPieceMultiplier));
		limitKeyPieceSizeTF.setText(String.valueOf(limitKeyPieceSize));
	}
	
	private class MyEventHandler implements EventHandler<ActionEvent>
	{

		@Override
		public void handle(ActionEvent event)
		{
			if(event.getSource()==settingsCancelButton)
			{
				initSettings();
			}
			else if(event.getSource()==settingsSaveButton)
			{
				int sampleRate=(int)Float.parseFloat(sampleRateTF.getText());
				int frameSize=Integer.parseInt(frameSizeTF.getText());
				int hashMapSize=Integer.parseInt(hashMapSizeTF.getText());
				int redundantThreshold=Integer.parseInt(redundantThresholdTF.getText());
				int highPassFilter=Integer.parseInt(highPassFilterTF.getText());
				int targetZoneSize=Integer.parseInt(targetZoneSizeTF.getText());
				int anchor2PeakMaxFreqDiff=Integer.parseInt(anchor2PeakMaxFreqDiffTF.getText());
				int baseForSampledFreq=Integer.parseInt(baseForSampledFreqTF.getText());
				int hashSetGroupSize=Integer.parseInt(hashSetGroupSizeTF.getText());
				int keyPieceMultiplier=Integer.parseInt(keyPieceMultiplierTF.getText());
				boolean limitKeyPieceSize;
				if(limitKeyPieceSizeTF.getText().toLowerCase().equals("true"))
				{
					limitKeyPieceSize=true;
				}
				else
				{
					limitKeyPieceSize=false;
					limitKeyPieceSizeTF.setText("false");
				}
				ExecutorService executorService=Executors.newFixedThreadPool(1);
				SettingsUpdateThread settingsUpdateThread=new SettingsUpdateThread(sampleRate, frameSize, hashMapSize, redundantThreshold, highPassFilter, targetZoneSize, anchor2PeakMaxFreqDiff, baseForSampledFreq, hashSetGroupSize, keyPieceMultiplier, limitKeyPieceSize);
				Future<String> future=executorService.submit(settingsUpdateThread);
			}
			else if(event.getSource()==clearLogButton)
			{
				logs.clear();
			}
			else if(event.getSource()==searchButton)
			{
				String searchableContentName=searchableContentNameTF.getText().trim();
				String searchableContentLocation=searchableContentLocationTF.getText().trim();
				String stationToSearch=stationToSearchCB.getValue();
				searchButton.setDisable(true);
				
				ExecutorService executorService=Executors.newFixedThreadPool(1);
				SearchHandler searchHandler=new SearchHandler(searchableContentName, searchableContentLocation, stationToSearch);
				Future<Integer> future=executorService.submit(searchHandler);
			}
			
		}
		
	}
	
	private class SettingsUpdateThread implements Callable<String>
	{
		int newSampleRate;
		int newFrameSize;
		int newHashMapSize;
		int newRedundantThreshold;
		int newHighPassFilter;
		int newTargetZoneSize;
		int newAnchor2PeakMaxFreqDiff;
		int newBaseForSampledFreq;
		int newHashSetGroupSize;
		int newKeyPieceMultiplier;
		boolean newLimitKeyPieceSize;
		
		public SettingsUpdateThread(int sampleRate, int frameSize, int hashMapSize, int redundantThreshold, int highPassFilter, int targetZoneSize, int anchor2PeakMaxFreqDiff, int baseForSampledFreq, int hashSetGroupSize, int keyPieceMultiplier, boolean limitKeyPieceSize)
		{
			this.newSampleRate=sampleRate;
			this.newFrameSize=frameSize;
			this.newHashMapSize=hashMapSize;
			this.newRedundantThreshold=redundantThreshold;
			this.newHighPassFilter=highPassFilter;
			this.newTargetZoneSize=targetZoneSize;
			this.newAnchor2PeakMaxFreqDiff=anchor2PeakMaxFreqDiff;
			this.newBaseForSampledFreq=baseForSampledFreq;
			this.newHashSetGroupSize=hashSetGroupSize;
			this.newKeyPieceMultiplier=keyPieceMultiplier;
			this.newLimitKeyPieceSize=limitKeyPieceSize;
		}
		
		@Override
		public String call() throws Exception 
		{
			Database database=new Database("broadcast_monitoring", "root", "jason");
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newSampleRate)+" WHERE name = 'sampleRate' AND ( type = 1 OR type = 2 )");
			sampleRate=newSampleRate;
			logs.add(new Log(Time.getTime("gmt"), "INFO", "Value of sampleRate updated to "+String.valueOf(sampleRate)));
			
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newAnchor2PeakMaxFreqDiff)+" WHERE name = 'anchor2peakMaxFreqDiff' AND ( type = 1 OR type = 2 )");
			anchor2peakMaxFreqDiff=newAnchor2PeakMaxFreqDiff;
			logs.add(new Log(Time.getTime("gmt"), "INFO", "Value of anchor2peakMaxFreqDiff updated to "+String.valueOf(anchor2peakMaxFreqDiff)));
			
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newFrameSize)+" WHERE name = 'frameSize' AND ( type = 1 OR type = 2 )");
			frameSize=newFrameSize;
			logs.add(new Log(Time.getTime("gmt"), "INFO", "Value of frameSize updated to "+String.valueOf(frameSize)));
			
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newHashMapSize)+" WHERE name = 'hashmapSize' AND ( type = 1 OR type = 2 )");
			hashmapSize=newHashMapSize;
			logs.add(new Log(Time.getTime("gmt"), "INFO", "Value of hashmapSize updated to "+String.valueOf(hashmapSize)));
			
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newRedundantThreshold)+" WHERE name = 'redundantThreshold' AND ( type = 1 OR type = 2 )");
			redundantThreshold=newRedundantThreshold;
			logs.add(new Log(Time.getTime("gmt"), "INFO", "Value of redundantThreshold updated to "+String.valueOf(redundantThreshold)));
			
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newBaseForSampledFreq)+" WHERE name = 'sampledFrequencies' AND ( type = 1 OR type = 2 )");
			sampledFrequencies=newBaseForSampledFreq;
			logs.add(new Log(Time.getTime("gmt"), "INFO", "Value of sampledFrequencies updated to "+String.valueOf(sampledFrequencies)));
			
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newHighPassFilter)+" WHERE name = 'startFreq' AND ( type = 1 OR type = 2 )");
			startFreq=newHighPassFilter;
			logs.add(new Log(Time.getTime("gmt"), "INFO", "Value of startFreq updated to "+String.valueOf(startFreq)));
			
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newTargetZoneSize)+" WHERE name = 'targetZoneSize' AND ( type = 1 OR type = 2 )");
			targetZoneSize=newTargetZoneSize;
			logs.add(new Log(Time.getTime("gmt"), "INFO", "Value of targetZoneSize updated to "+String.valueOf(targetZoneSize)));
			
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newHashSetGroupSize)+" WHERE name = 'hashSetGroupSize' AND ( type = 1 OR type = 2 )");
			hashSetGroupSize=newHashSetGroupSize;
			logs.add(new Log(Time.getTime("gmt"), "INFO", "Value of hashSetGroupSize updated to "+String.valueOf(hashSetGroupSize)));
			
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newKeyPieceMultiplier)+" WHERE name = 'keyPieceMultiplier' AND ( type = 1 OR type = 2 )");
			keyPieceMultiplier=newKeyPieceMultiplier;
			logs.add(new Log(Time.getTime("gmt"), "INFO", "Value of keyPieceMultiplier updated to "+String.valueOf(keyPieceMultiplier)));
			
			int newLimitKeyPieceSizeInt;
			if(newLimitKeyPieceSize==true)
			{
				newLimitKeyPieceSizeInt=1;
			}
			else
			{
				newLimitKeyPieceSizeInt=0;
			}
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newLimitKeyPieceSizeInt)+" WHERE name = 'limitKeyPieceSize' AND ( type = 1 OR type = 2 )");
			limitKeyPieceSize=newLimitKeyPieceSize;
			logs.add(new Log(Time.getTime("gmt"), "INFO", "Value of limitKeyPieceSize updated to "+String.valueOf(limitKeyPieceSize)));
			
			database.close();
			return null;
		}
		
	}
	
	private class SearchHandler implements Callable<Integer>
	{
		private String searchableContentName;
		private String searchableContentUrl;
		private String channel;
		private Stage popup;
		private StackPane popupLayout;
		private Text message;
		
		public SearchHandler(String searchableContentName, String searchableContentUrl, String channel)
		{
			this.searchableContentName=searchableContentName;
			this.searchableContentUrl=searchableContentUrl;
			this.channel=channel;
			
			popup=new Stage(StageStyle.UTILITY);
			popupLayout=new StackPane();
			popupLayout.getChildren().add(message);
			popup.setScene(new Scene(popupLayout, 50, 300));
		}
		@Override
		public Integer call() throws Exception 
		{
			popup.show();
			message.setText("Adding Searchable Content to Database");
			int searchableContentID=addSearchableContentToDB(searchableContentName);
			message.setText("Generating Hashes from Searchable Content");
			generateHashes(searchableContentUrl, searchableContentID,smoothingWidth);
			List<Hash> key=getKeyPiece(hashSetGroupSize, searchableContentID,hashDir,limitKeyPieceSize,keyPieceMultiplier);
			int channelNumber=getChannelNumber(channel);
			
			popup.close();
			
			while(stopSearchFlag==0)
			{
				searchKeyInChannel(searchableContentID,channelNumber,hashSetGroupSize,key,hashDir);
			}
			
			return null;
		}
		
		private int addSearchableContentToDB(String name)
		{
			System.out.println("Adding searchable content to database");
			logs.add(new Log(Time.getTime("gmt"), "INFO", "Adding searchable content to database"));
			
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
		
		private void generateHashes(String url, int id, int smoothingWidth)
		{
			System.out.println("\nEnsure that:");
			System.out.println(" - the audio output mixer is not muted");
			System.out.println(" - the audio output mixer's volume is sufficient (above 80%)");
			System.out.println(" - no audio in currently being played by this machine");
			
			try 
			{
				Streamer streamer=new Streamer(sampleRate, frameSize, hashmapSize, redundantThreshold, startFreq, targetZoneSize, anchor2peakMaxFreqDiff, sampledFrequencies, id, url, logs);
				streamer.startAnalyzing();
			} 
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private List<Hash> getKeyPiece(int hashSetGroupSize, int id, String hashDir, boolean limit, int multiplyer)
		{
			System.out.println("Determining key for searchable content");
			logs.add(new Log(Time.getTime("gmt"), "INFO", "Determining key for searchable content"));
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
						@SuppressWarnings("unchecked")
						List<Hash> fetchedHashSet=(List<Hash>)objectInput.readObject();
						if(fetchedHashSet!=null)
						{
							sContentHashes.addAll(fetchedHashSet);
						}
						else
						{
							System.err.println("****"+fetchedHashSetUrls.getString(1)+" was not deserialized****\n# SearchServer.java #");
							logs.add(new Log(Time.getTime("gmt"), "ERROR", "SearchServer.java: "+fetchedHashSetUrls.getString(1)+" was not deserialized"));
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
			logs.add(new Log(Time.getTime("gmt"), "INFO", "Number of hashes in key : "+sContentHashes.size()));
			return sContentHashes;
		}
		private int getChannelNumber(String channel)
		{
			logs.add(new Log(Time.getTime("gmt"), "INFO", "Getting Channel number for Database"));
			Database database=new Database("broadcast_monitoring", "root", "jason");
			ResultSet resultSet=database.runSelectQuery("SELECT `number` FROM `channel` WHERE name = '"+channel+"'");
			try 
			{
				if(resultSet.next())
				{
					int id=resultSet.getInt("number");
					database.close();
					return id;
				}
			} 
			catch (SQLException e)
			{
				e.printStackTrace();
				logs.add(new Log(Time.getTime("gmt"), "ERROR", "SQLException thrown while trying to get channel number"));
			}
			database.close();
			return -1;
		}
		
		private void searchKeyInChannel(int id, int channel, int hashSetGroupSize, List<Hash> key, String hashDir)
		{
			//get search pointer from database
			Database database=new Database("broadcast_monitoring", "root", "jason");
			ResultSet resultSet=database.runSelectQuery("SELECT `last_start_real_time` FROM `search_pointer` WHERE parent = "+String.valueOf(id)+" AND channel = "+String.valueOf(channel));
			if(resultSet!=null)
			{
				try
				{
					System.out.println("Obtaining the search pointer");
					logs.add(new Log(Time.getTime("gmt"), "INFO", "Obtaining the search pointer"));
					int lastStartRealTime;
					if(resultSet.next())
					{
						lastStartRealTime=resultSet.getInt(1);
						System.out.println("Pointer found. Pointing to time : "+lastStartRealTime);
						logs.add(new Log(Time.getTime("gmt"), "INFO", "Pointer found. Pointing to time : "+lastStartRealTime));
					}
					else//no search pointer exists
					{
						System.out.println("Pointer not found, adding a new pointer to the database");
						logs.add(new Log(Time.getTime("gmt"), "INFO", "Pointer not found, adding a new pointer to the database"));
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
							logs.add(new Log(Time.getTime("gmt"), "INFO", "Number of hash sets in database after last firstrealtime : "+numberofHashSets));
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
											logs.add(new Log(Time.getTime("gmt"), "INFO", "Updating last firstRealTime to :"+newLastStartRealTime));
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
}
