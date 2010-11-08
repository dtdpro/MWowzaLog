package com.dtdpro.wowza;

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
	private String appName="";
	private String sessId="";
	private String startTime="";
	private int userId=0;

	public void onAppStart(IApplicationInstance appInstance) {
		dbName=appInstance.getProperties().getPropertyStr("dbname");
		dbHost=appInstance.getProperties().getPropertyStr("dbhost");
		dbUser=appInstance.getProperties().getPropertyStr("dbuser");
		dbPass=appInstance.getProperties().getPropertyStr("dbpass");
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
		
		userId = getParamInt(params, PARAM1,0);
		sessId = getParamString(params, PARAM2,"noFlash");
	}
	public void onStreamCreate(IMediaStream stream) {
		startTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
		/*if (stream.getStreamType().equals("default")) {
			Connection conn = null;
			int connId = stream.getClientId();
			try 
			{
				conn = DriverManager.getConnection("jdbc:mysql://"+dbHost+"/"+dbName+"?user="+dbUser+"&password="+dbPass);
	 
				PreparedStatement preparedStatement = null;
	 
				try 
				{
					preparedStatement = conn.prepareStatement("INSERT INTO wza_viewed (view_user,view_session,view_timein,view_connid,view_app) VALUES (?,?,NOW(),?,?)");
			        preparedStatement.setInt(1, userId);
			        preparedStatement.setString(2,sessId);
			        preparedStatement.setInt(3,connId);
			        preparedStatement.setString(4,appName);
			        
			        preparedStatement.executeUpdate();
			    } 
				catch (SQLException sqlEx) 
				{
					getLogger().error("sqlexecuteException: " + sqlEx.toString());
				} 
				finally 
				{
					preparedStatement.close();
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
		}*/
	}

	public void onStreamDestroy(IMediaStream stream) {
		/*if (stream.getStreamType().equals("default")) {
			String streamName = stream.getName();
			Connection conn = null;
			try 
			{
				conn = DriverManager.getConnection("jdbc:mysql://"+dbHost+"/"+dbName+"?user="+dbUser+"&password="+dbPass);
				Statement stmt = null;
				try 
				{
					stmt = conn.createStatement();
					stmt.executeUpdate("UPDATE wza_viewed SET view_video = '"+streamName+"',view_timeout=NOW() WHERE view_connid = "+stream.getClientId());
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
		}*/
		if (stream.getStreamType().equals("default")) {
			String endTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
			Connection conn = null;
			try 
			{
				conn = DriverManager.getConnection("jdbc:mysql://"+dbHost+"/"+dbName+"?user="+dbUser+"&password="+dbPass);
	 
				PreparedStatement preparedStatement = null;
	 
				try 
				{
					preparedStatement = conn.prepareStatement("INSERT INTO wza_viewed (view_user,view_session,view_timein,view_timeout,view_app) VALUES (?,?,?,?,?,?)");
			        preparedStatement.setInt(1, userId);
			        preparedStatement.setString(2,sessId);
			        preparedStatement.setString(3,startTime);
			        preparedStatement.setString(4,endTime);
			        preparedStatement.setString(5,appName);
			        
			        preparedStatement.executeUpdate();
			    } 
				catch (SQLException sqlEx) 
				{
					getLogger().error("sqlexecuteException: " + sqlEx.toString());
				} 
				finally 
				{
					preparedStatement.close();
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
	}

}