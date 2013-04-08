package com.broadcastmonitoring.indexing;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.sound.sampled.LineUnavailableException;
import com.broadcastmonitoring.database.Database;

public class IndexingServer extends Application implements Initializable
{
	
	@FXML private ComboBox<String> stationTypeComboBox;
	@FXML private ComboBox<String> interfaceComboBox;
	@FXML private ComboBox<String> stationComboBox;
	@FXML private TextField sampleRateTF;
	@FXML private TextField frameSizeTF;
	@FXML private TextField hashMapSizeTF;
	@FXML private TextField redundantThresholdTF;
	@FXML private TextField highPassFilterTF;
	@FXML private TextField targetZoneSizeTF;
	@FXML private TextField anchor2PeakMaxFreqDiffTF;
	@FXML private TextField baseForSampledFreqTF;
	@FXML private Button addStationButton;
	@FXML private TextField stationNameTF;
	@FXML private Button settingsSaveButton;
	@FXML private Button settingsCancelButton;
	@FXML private Tab indexingTab;
	@FXML private Button startIndexingButton;
	@FXML private TableView<Station> indexingTable;
	@FXML private TableColumn<Station, String> stationInterfaceTC;
	@FXML private TableColumn<Station, String> stationTC;
	@FXML private TableColumn<Station, String> stationTypeTC;
	@FXML private TableColumn<Station, String> startTimeTC;
	//@FXML private TableColumn<Station, String> indexingStatusTC;
	
	private final ObservableList<Station> indexingInfo=FXCollections.observableArrayList();
	private List<Streamer> streamers;
	
	protected static float sampleRate; //is in Hz. results in about 20000 freqs. This is also the standard sample rate for WAV files which this app uses
	protected static int frameSize; //determines the  resolution of the spectrogram = number of hashes generated
	protected static int hashmapSize; //determines the number of frames to be considered in a constelation map
	protected static int redundantThreshold; //
	protected static int startFreq; //determines the point at which low frequencies will be considered since lower frequencies have more pink noise (high pass filter because of the pink noise)
	protected static int targetZoneSize; //determines the number of peaks to be in a target zone when forming a hash
	protected static int anchor2peakMaxFreqDiff; //determines the range of frequencies to be considered when selecting the target zone for a hash
	protected static int sampledFrequencies; //determines the frequencies to be sampled, if 100 then only frequencies divisible by 100 will be sampled
	
	/*
	 protected static float sampleRate=44100; //is in Hz. results in about 20000 freqs. This is also the standard sample rate for WAV files which this app uses
	protected static int frameSize=2048; //determines the  resolution of the spectrogram = number of hashes generated
	protected static int hashmapSize=10; //determines the number of frames to be considered in a constelation map
	protected static int redundantThreshold=2; //
	protected static int startFreq=50; //determines the point at which low frequencies will be considered since lower frequencies have more pink noise (high pass filter because of the pink noise)
	protected static int targetZoneSize=5; //determines the number of peaks to be in a target zone when forming a hash
	protected static int anchor2peakMaxFreqDiff=1000; //determines the range of frequencies to be considered when selecting the target zone for a hash
	protected static int sampledFrequencies=5; //determines the frequencies to be sampled, if 100 then only frequencies divisible by 100 will be sampled
	
	 */
	//TODO: ability to stop indexing a station
	//TODO: logs
	
	public static void main(String[] args)
	{
		try
		{
			Database database=new Database("broadcast_monitoring", "root", "jason");
			ResultSet resultSet=database.runSelectQuery("SELECT name,value FROM variables WHERE type = 0");
			while(resultSet.next())
			{
				if(resultSet.getString("name").equals("sampleRate"))
				{
					sampleRate=resultSet.getFloat("value");
					System.out.println("sampleRate="+sampleRate);
				}
				else if(resultSet.getString("name").equals("frameSize"))
				{
					frameSize=resultSet.getInt("value");
					System.out.println("frameSize="+frameSize);
				}
				else if(resultSet.getString("name").equals("hashmapSize"))
				{
					hashmapSize=resultSet.getInt("value");
					System.out.println("hashmapSize="+hashmapSize);
				}
				else if(resultSet.getString("name").equals("redundantThreshold"))
				{
					redundantThreshold=resultSet.getInt("value");
					System.out.println("redundantThreshold="+redundantThreshold);
				}
				else if(resultSet.getString("name").equals("startFreq"))
				{
					startFreq=resultSet.getInt("value");
					System.out.println("startFreq="+startFreq);
				}
				else if(resultSet.getString("name").equals("targetZoneSize"))
				{
					targetZoneSize=resultSet.getInt("value");
					System.out.println("targetZoneSize="+targetZoneSize);
				}
				else if(resultSet.getString("name").equals("anchor2peakMaxFreqDiff"))
				{
					anchor2peakMaxFreqDiff=resultSet.getInt("value");
					System.out.println("anchor2peakMaxFreqDiff="+anchor2peakMaxFreqDiff);
				}
				else if(resultSet.getString("name").equals("sampledFrequencies"))
				{
					sampledFrequencies=resultSet.getInt("value");
					System.out.println("sampledFrequencies="+sampledFrequencies);
				}
			}
			database.close();
			
			//Streamer streamer=new Streamer(sampleRate, frameSize, hashmapSize, redundantThreshold, startFreq, targetZoneSize, anchor2peakMaxFreqDiff, sampledFrequencies);
			launch(args);
			//streamer.startAnalyzing();
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		Parent root;
		try 
		{
			root = FXMLLoader.load(getClass().getResource("indexing_server.fxml"));
			primaryStage.setTitle("Indexing Server");
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
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		initStationTypeComboBox();
		initInterfaceComboBox();
		initStationComboBox();
		initSettings();
		MyEventHandler eventHandler=new MyEventHandler();
		addStationButton.setOnAction(eventHandler);
		settingsSaveButton.setOnAction(eventHandler);
		settingsCancelButton.setOnAction(eventHandler);
		indexingTab.setOnSelectionChanged(new EventHandler<Event>()
				{
			
			@Override
			public void handle(Event arg0) 
			{
				if(indexingTab.isSelected())
				{
					initStationComboBox();
				}
			}
		});
		startIndexingButton.setOnAction(eventHandler);
		
		stationInterfaceTC.setCellValueFactory(new PropertyValueFactory<Station, String>("stationInterface"));
		stationInterfaceTC.setEditable(false);
		stationTC.setCellValueFactory(new PropertyValueFactory<Station, String>("station"));
		stationTC.setEditable(false);
		stationTypeTC.setCellValueFactory(new PropertyValueFactory<Station, String>("type"));
		stationTypeTC.setEditable(false);
		startTimeTC.setCellValueFactory(new PropertyValueFactory<Station, String>("startTime"));
		startTimeTC.setEditable(false);
		//indexingStatusTC.setCellValueFactory(new PropertyValueFactory<Station, String>("running"));
		//indexingStatusTC.setEditable(true);
		
		indexingTable.setItems(indexingInfo);
		streamers=new ArrayList<Streamer>();
	}
	
	private void initStationTypeComboBox()
	{
		ObservableList<String> stationTypes=FXCollections.observableArrayList();
		stationTypes.add("TV");
		stationTypes.add("Radio");
		stationTypeComboBox.setItems(stationTypes);
	}
	
	private void initInterfaceComboBox()
	{
		ObservableList<String> interfaces=FXCollections.observableArrayList();
		interfaces.add("Default");
		interfaceComboBox.setItems(interfaces);
	}
	
	private void initStationComboBox()
	{
		ExecutorService executorService=Executors.newFixedThreadPool(1);
		StationHandler stationHandler=new StationHandler(stationComboBox);
		Future<String> future=executorService.submit(stationHandler);
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
	}
	
	private class StationHandler implements Callable<String>
	{
		private ComboBox<String> stationComboBox;
		
		public StationHandler(ComboBox<String> stationComboBox) 
		{
			this.stationComboBox=stationComboBox;
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
			stationComboBox.setItems(stationNames);
			database.close();
			return null;
		}
		
	}
	
	private class StationInsertionThread implements Callable<String>
	{
		private String name;
		private int type;
		private String interfc;
		public StationInsertionThread(String name, int type, String interfc)
		{
			this.name=name;
			this.type=type;
			this.interfc=interfc;
		}
		
		@Override
		public String call() throws Exception 
		{
			Database database=new Database("broadcast_monitoring", "root", "jason");
			database.initInsertStatement("INSERT INTO channel(name,type,interface) VALUES(?,?,?)");
			database.addColumnValue(name);
			database.addColumnValue(type);
			database.addColumnValue(interfc);
			database.executeInsert();
			database.close();
			return null;
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
		
		public SettingsUpdateThread(int sampleRate, int frameSize, int hashMapSize, int redundantThreshold, int highPassFilter, int targetZoneSize, int anchor2PeakMaxFreqDiff, int baseForSampledFreq)
		{
			this.newSampleRate=sampleRate;
			this.newFrameSize=frameSize;
			this.newHashMapSize=hashMapSize;
			this.newRedundantThreshold=redundantThreshold;
			this.newHighPassFilter=highPassFilter;
			this.newTargetZoneSize=targetZoneSize;
			this.newAnchor2PeakMaxFreqDiff=anchor2PeakMaxFreqDiff;
			this.newBaseForSampledFreq=baseForSampledFreq;
		}
		
		@Override
		public String call() throws Exception 
		{
			Database database=new Database("broadcast_monitoring", "root", "jason");
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newSampleRate)+" WHERE name = 'sampleRate' AND type = 0");
			sampleRate=newSampleRate;
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newAnchor2PeakMaxFreqDiff)+" WHERE name = 'anchor2peakMaxFreqDiff' AND type = 0");
			anchor2peakMaxFreqDiff=newAnchor2PeakMaxFreqDiff;
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newFrameSize)+" WHERE name = 'frameSize' AND type = 0");
			frameSize=newFrameSize;
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newHashMapSize)+" WHERE name = 'hashmapSize' AND type = 0");
			hashmapSize=newHashMapSize;
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newRedundantThreshold)+" WHERE name = 'redundantThreshold' AND type = 0");
			redundantThreshold=newRedundantThreshold;
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newBaseForSampledFreq)+" WHERE name = 'sampledFrequencies' AND type = 0");
			sampledFrequencies=newBaseForSampledFreq;
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newHighPassFilter)+" WHERE name = 'startFreq' AND type = 0");
			startFreq=newHighPassFilter;
			database.runUpdateQuery("UPDATE variables SET value = "+String.valueOf(newTargetZoneSize)+" WHERE name = 'targetZoneSize' AND type = 0");
			targetZoneSize=newTargetZoneSize;
			
			database.close();
			return null;
		}
		
	}
	
	private class MyEventHandler implements EventHandler<ActionEvent>
	{

		@Override
		public void handle(ActionEvent event)
		{
			if(event.getSource()==addStationButton)
			{
				String stationName=stationNameTF.getText();
				String stationTypeText=stationTypeComboBox.getValue();
				String stationInterfaceText=interfaceComboBox.getValue();
				if(stationName!=null&&!stationName.trim().equals("")&&stationTypeText!=null&&stationInterfaceText!=null)
				{
					int stationType=-1;
					if(stationTypeText.trim().equals("TV"))
					{
						stationType=0;
					}
					else if(stationTypeText.trim().equals("Radio"))
					{
						stationType=1;
					}
					ExecutorService executorService=Executors.newFixedThreadPool(1);
					StationInsertionThread stationInsertionThread=new StationInsertionThread(stationName.trim(), stationType, stationInterfaceText.trim());
					Future<String> future=executorService.submit(stationInsertionThread);
					stationNameTF.clear();

					//stationTypeComboBox.setValue(null);
					//interfaceComboBox.setValue(null);
				}
				else if(stationName==null || stationName.trim().equals(""))
				{
					stationNameTF.setPromptText("Input the Station's name");
				}
				else if(stationTypeText==null)
				{
					stationTypeComboBox.setPromptText("Select the Station's type");
				}
				else if(stationInterfaceText==null)
				{
					interfaceComboBox.setPromptText("Select the Station's interface");
				}
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
				ExecutorService executorService=Executors.newFixedThreadPool(1);
				SettingsUpdateThread settingsUpdateThread=new SettingsUpdateThread(sampleRate, frameSize, hashMapSize, redundantThreshold, highPassFilter, targetZoneSize, anchor2PeakMaxFreqDiff, baseForSampledFreq);
				Future<String> future=executorService.submit(settingsUpdateThread);
			}
			else if(event.getSource()==settingsCancelButton)
			{
				initSettings();
			}
			else if(event.getSource()==startIndexingButton)
			{
				String selectedStation=stationComboBox.getValue();
				if(selectedStation!=null)
				{
					try
					{
						Database database=new Database("broadcast_monitoring", "root", "jason");
						ResultSet resultSet=database.runSelectQuery("SELECT number, interface, type FROM channel WHERE name = '"+selectedStation+"'");
						if(resultSet.next())
						{
							int stationNumber=resultSet.getInt("number");
							String stationInterface=resultSet.getString("interface");
							String stationType="Unknown";
							Date date=new Date();
							SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
							String timestamp=sdf.format(date)+"( GMT)";
							if(resultSet.getInt("type")==0)
							{
								stationType="TV";
							}
							else if(resultSet.getInt("type")==1)
							{
								stationType="Radio";
							}
							int checkFlag=0;
							for(int i=0; i<indexingInfo.size(); i++)//check if there is a station currently being indexed with the same interface
							{
								if(indexingInfo.get(i).getStationInterface().equals(stationInterface))
								{
									checkFlag=1;
									break;
								}
							}
							if(checkFlag==0)
							{
								Station newStation=new Station(selectedStation, stationType, timestamp, stationInterface, stationNumber, true);
								Streamer streamer=new Streamer(sampleRate, frameSize, hashmapSize, redundantThreshold, startFreq, targetZoneSize, anchor2peakMaxFreqDiff, sampledFrequencies, newStation);
								indexingInfo.add(newStation);
								streamer.startAnalyzing();
								streamers.add(streamer);
							}
							else
							{
								stationComboBox.setPromptText("Select station with idle interface");
							}
						}
						database.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}		
	}
}
