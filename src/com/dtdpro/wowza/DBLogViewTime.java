/*
 *  MWowzaLog - DBLogViewTime.java
 *  (C)2010 DtD Productions
 *  License: GPL v2
 *  mike@dtdpro.com
 */
package com.dtdpro.wowza;

import com.wowza.util.IOPerformanceCounter;
import com.wowza.wms.amf.AMFDataList;
import com.wowza.wms.application.*;
import com.wowza.wms.client.IClient;
import com.wowza.wms.module.*;
import com.wowza.wms.request.RequestFunction;
import com.wowza.wms.stream.IMediaStream;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DBLogViewTime extends ModuleBase {
	
	private String dbName=null;
	private String dbHost=null;
	private String dbUser=null;
	private String dbPass=null;
	private String tblPre=null;
	private String appName="";
	private String sessId="noFlash";
	private String startTime="";
	private String userId="0";

	public void onAppStart(IApplicationInstance appInstance) {
		dbName=appInstance.getProperties().getPropertyStr("dbname");
		dbHost=appInstance.getProperties().getPropertyStr("dbhost");
		dbUser=appInstance.getProperties().getPropertyStr("dbuser");
		dbPass=appInstance.getProperties().getPropertyStr("dbpass");
		tblPre=appInstance.getProperties().getPropertyStr("tblpre");
		appName=appInstance.getApplication().getName();
		try 
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance(); 
		} 
		catch (Exception e) 
		{ 
			getLogger().error("Error loading: com.mysql.jdbc.Driver: "+e.toString());
		} 
	}

	public void onConnect(IClient client, RequestFunction function, AMFDataList params) 
	{
		
		userId = getParamString(params, PARAM1,"0");
		sessId = getParamString(params, PARAM2,"noFlash");
	}
	public void onStreamCreate(IMediaStream stream) {
		startTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

	}

	public void onStreamDestroy(IMediaStream stream) {
		//if (stream.getStreamType().equals("default")) {
			String endTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
			Connection conn = null;
			String streamType=stream.getStreamType();
			String streamName=stream.getName();
			IOPerformanceCounter perf = stream.getMediaIOPerformance();
			long bytes = perf.getMessagesOutBytes() - perf.getMessagesInBytes();
			if (bytes != 0) {
				try 
				{
					conn = DriverManager.getConnection("jdbc:mysql://"+dbHost+"/"+dbName+"?user="+dbUser+"&password="+dbPass);
		 
					PreparedStatement stmt = null;
		 
					try 
					{
						stmt = conn.prepareStatement("INSERT INTO "+tblPre+"viewed (view_user,view_session,view_timein,view_timeout,view_app,view_type,view_video) VALUES (?,?,?,?,?,?,?)");
						stmt.setString(1,userId);
						stmt.setString(2,sessId);
						stmt.setString(3,startTime);
						stmt.setString(4,endTime);
						stmt.setString(5,appName);
						stmt.setString(6,streamType);
						stmt.setString(7,streamName);
				        
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
		//}
	}

}