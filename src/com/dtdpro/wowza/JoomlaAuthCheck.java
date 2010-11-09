/*
 *  MWowzaTools - JoolmaAuthCheck.java
 *  (C)2010 DtD Productions
 *  License: GPL v2
 *  mike@dtdpro.com
 *  
 *  Requires MVid Video Manager
 */

package com.dtdpro.wowza;

import java.sql.*;

import com.wowza.wms.application.*;
import com.wowza.wms.amf.*;
import com.wowza.wms.client.*;
import com.wowza.wms.module.*;
import com.wowza.wms.request.*;

public class JoomlaAuthCheck extends ModuleBase {
	private String dbName=null;
	private String dbHost=null;
	private String dbUser=null;
	private String dbPass=null;
	private String tblPre=null;
	
	public void onAppStart(IApplicationInstance appInstance) {
		dbName=appInstance.getProperties().getPropertyStr("auth_dbname");
		dbHost=appInstance.getProperties().getPropertyStr("auth_dbhost");
		dbUser=appInstance.getProperties().getPropertyStr("auth_dbuser");
		dbPass=appInstance.getProperties().getPropertyStr("auth_dbpass");
		tblPre=appInstance.getProperties().getPropertyStr("auth_tblpre");
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
		String userId = getParamString(params, PARAM1,"0");
		String sessId = getParamString(params, PARAM2,"NoSessionProvided");
		String vidId = getParamString(params, PARAM3,"0");
		String playType = getParamString(params, PARAM4,"none");
		
		if (!userId.equals("0")) {
			Connection conn = null;
			
			try 
			{
				conn = DriverManager.getConnection("jdbc:mysql://"+dbHost+"/"+dbName+"?user="+dbUser+"&password="+dbPass);
	 
				Statement stmt = null;
				Statement stmt2 = null;
				ResultSet rs = null;
				ResultSet rs2 = null;
	 
				try 
				{
					stmt = conn.createStatement();
					rs = stmt.executeQuery("SELECT count(*) as sessCount FROM "+tblPre+"session where userid = '"+userId+"' and session_id = '"+sessId+"'");
					if (rs.next() == true)
					{
					    if (rs.getInt("sessCount") > 0)	{ 
					    	
					    	if (playType.equals("ppv")) {
						    	stmt2 = conn.createStatement();
								rs2 = stmt2.executeQuery("SELECT count(*) as authCount FROM "+tblPre+"mvid_auth where auth_user = '"+userId+"' and auth_video = '"+vidId+"'");
								if (rs2.next() == true)
								{
								    if (rs2.getInt("authCount") > 0)	{ 
								    	client.acceptConnection();
									} else {
										client.rejectConnection("NotPaidFor");
									}
								} else {
									client.rejectConnection("DatabaseError");
								}
					    	} else {
						    	client.acceptConnection();
					    	}
						} else {
							client.rejectConnection("UserNotLoggedIn");
						}
					} else {
						client.rejectConnection("DatabaseError");
					}
			        
					
			    } 
				catch (SQLException sqlEx) 
				{
					getLogger().error("sqlexecuteException: " + sqlEx.toString());
					client.rejectConnection("DatabaseError");
				} 
				finally 
				{
					stmt.close();
					rs.close();
				}
	 			conn.close();
			} 
			catch (SQLException ex) 
			{
				// handle any errors
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
				client.rejectConnection("DatabaseError");
			}
		} else {
			if (playType.equals("public")) {
		    	client.acceptConnection();
			} else {
				client.rejectConnection("GuestNotLoggedIn");
			}
		}
		getLogger().info("onConnect: " + client.getClientId());
	}

	public void onConnectAccept(IClient client) {
		getLogger().info("onConnectAccept: " + client.getClientId());
	}

	public void onConnectReject(IClient client) {
		getLogger().info("onConnectReject: " + client.getClientId());
	}

}