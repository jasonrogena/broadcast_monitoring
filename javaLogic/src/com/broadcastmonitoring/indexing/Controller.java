package com.broadcastmonitoring.indexing;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.broadcastmonitoring.database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

public class Controller implements Initializable
{
	@FXML private ComboBox<String> stationTypeComboBox;
	@FXML private ComboBox<String> interfaceComboBox;
	@FXML private ComboBox<String> stationComboBox;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		initStationTypeComboBox();
		initInterfaceComboBox();
		initStationComboBox();
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
			return null;
		}
		
	}
	
}
