package com.dtdpro.wowza.dblogbytes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.wowza.util.IOPerformanceCounter;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.module.*;
import com.wowza.wms.stream.IMediaStream;

public class DBLogBytes extends ModuleBase {

	private void logBytes(String strmtype,long strmbytes,String strmname,int strmet) {
		Connection conn = null;
		try 
		{
			conn = DriverManager.getConnection("jdbc:mysql://192.168.8.14/wowzadb?user=marchive&password=alison");
 
			PreparedStatement stmt = null;
 
			try 
			{
				stmt = conn.prepareStatement("INSERT INTO wza_bytes (byt_type,byt_bytes,byt_name,byt_seconds) VALUES (?,?,?,?)");
				stmt.setString(1, strmtype);
				stmt.setLong(2,strmbytes);
				stmt.setString(3,strmname);
				stmt.setInt(4,strmet);
				stmt.executeUpdate();
		    } 
			catch (SQLException sqlEx) 
			{
				getLogger().error("sqlexecuteException: " + sqlEx.toString());
			} 
			finally 
			{
				stmt.close();
			}
 
			conn.close();
		} 
		catch (SQLException ex) 
		{
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	
	public void onAppStart(IApplicationInstance appInstance) {
		try 
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance(); 
		} 
		catch (Exception e) 
		{ 
			getLogger().error("Error loading: com.mysql.jdbc.Driver: "+e.toString());
		} 
	}

	public void onStreamDestroy(IMediaStream stream) {
		IOPerformanceCounter perf = stream.getMediaIOPerformance();
		String streamType=stream.getStreamType();
		long bytes = perf.getMessagesOutBytes() - perf.getMessagesInBytes();
		String streamName=stream.getName();
		int elapsedTime = (int) stream.getElapsedTime().getTimeSeconds();
		logBytes(streamType,bytes,streamName,elapsedTime);
	}
 
	
}
