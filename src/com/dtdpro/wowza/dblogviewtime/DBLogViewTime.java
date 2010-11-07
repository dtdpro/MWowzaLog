package com.dtdpro.wowza.dblogviewtime;

import com.wowza.wms.application.*;
import com.wowza.wms.amf.*;
import com.wowza.wms.client.*;
import com.wowza.wms.module.*;
import com.wowza.wms.request.*;

import java.sql.*;

public class DBLogViewTime extends ModuleBase {

	public void onAppStart(IApplicationInstance appInstance) {
		// preload the driver class
		try 
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance(); 
		} 
		catch (Exception e) 
		{ 
			getLogger().error("Error loading: com.mysql.jdbc.Driver: "+e.toString());
		} 
	}

	public void onConnect(IClient client, RequestFunction function, AMFDataList params) {
		Connection conn = null;
		String appName = client.getApplication().getName();
		try 
		{
			conn = DriverManager.getConnection("jdbc:mysql://192.168.8.14/wowzadb?user=marchive&password=alison");
 
			PreparedStatement preparedStatement = null;
 
			try 
			{
				preparedStatement = conn.prepareStatement("INSERT INTO wza_viewed (view_user,view_timein,view_connid,view_app) VALUES (?,NOW(),?,?)", Statement.RETURN_GENERATED_KEYS);
		        preparedStatement.setInt(1, 0);
		        preparedStatement.setInt(2,client.getClientId());
		        preparedStatement.setString(3,appName);
		        
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
		
		getLogger().info("onConnect: " + client.getClientId());
	}

	public void onConnectAccept(IClient client) {
		getLogger().info("onConnectAccept: " + client.getClientId());
	}

	public void onConnectReject(IClient client) {
		getLogger().info("onConnectReject: " + client.getClientId());
	}
	public void onDisconnect(IClient client) {
		Connection conn = null;
		try 
		{
			conn = DriverManager.getConnection("jdbc:mysql://192.168.8.14/wowzadb?user=marchive&password=alison");
			Statement stmt = null;
			try 
			{
				stmt = conn.createStatement();
				stmt.executeUpdate("UPDATE wza_viewed SET view_timeout = NOW() WHERE view_connid = "+client.getClientId());
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
		
		getLogger().info("onDisconnect: " + client.getClientId());
	}
	
	public void play(IClient client, RequestFunction function,
	        AMFDataList params) {
		getLogger().info("Overriding Play");
		
		String streamName = getParamString(params, PARAM1);
		
		Connection conn = null;
		try 
		{
			conn = DriverManager.getConnection("jdbc:mysql://192.168.8.14/wowzadb?user=marchive&password=alison");
			Statement stmt = null;
			try 
			{
				stmt = conn.createStatement();
				stmt.executeUpdate("UPDATE wza_viewed SET view_video = '"+streamName+"' WHERE view_connid = "+client.getClientId());
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
		
		ModuleCore.play(client, function, params);
	}

}