package com.example.lianghuiyong.brodcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.lianghuiyong.tool.EcarPort;
import com.txznet.sdk.TXZTtsManager;

public class BroadcastMonitor extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String iAction = intent.getAction();
		if ("com.android.ecar.send".equals(iAction))
		{
			
			String cmd = intent.getStringExtra("ecarSendKey");
			String cmdType = intent.getStringExtra("cmdType");
			String params =  intent.getStringExtra("keySet");
			if (cmd != null && cmd.length() > 0)
			{
				if (cmd.equals("Upgrade"))
				{
					//
					String tmpPath = intent.getStringExtra("Path");
					if (tmpPath != null && tmpPath.length() > 0)
					{
						System.out.println(">>>>>>>>>> Install App:"+tmpPath+" <<<<<<<<<<");
					}
				}
				else if (cmd.equals("StartMap"))
				{
					//
					String poiName = intent.getStringExtra("stand_poiName");
					String poiLatitude = intent.getStringExtra("stand_latitude");
					String poiLongitude = intent.getStringExtra("stand_longitude");
					if ((poiName != null && poiName.length() > 0) &&
						(poiLatitude != null && poiLatitude.length() > 0)	&&
						(poiLongitude != null && poiLongitude.length() > 0))
					{
						//System.out.println(">>>>>>>>>> Recv Poi:"+poiName+";"+poiLatitude+";"+poiLongitude+" <<<<<<<<<<");
						EcarPort.startGaodeMapNavi(context, Double.parseDouble(poiLatitude), Double.parseDouble(poiLongitude), poiName, 2, 1);
					}
				}
				else if (cmd.equals("HideCallUI"))
				{
					//
					String tmpOper = intent.getStringExtra("oper");
					if (tmpOper != null && tmpOper.length() > 0)
					{
						if (tmpOper.equals("hide"))
						{
							System.out.println(">>>>>>>>>> Recv msg: hide Call UI <<<<<<<<<<");	
						}
						else if (tmpOper.equals("show"))
						{
							System.out.println(">>>>>>>>>> Recv msg: show Call UI  <<<<<<<<<<");	
						}
					}
					
				}
				else if (cmd.equals("SetAPN"))
				{
					//
					String tmpAPN = intent.getStringExtra("APN");
					String tmpMNC = intent.getStringExtra("MNC");
					String tmpMCC = intent.getStringExtra("MCC");
					if ((tmpAPN != null && tmpAPN.length() > 0) &&
						(tmpMNC != null && tmpMNC.length() > 0) &&
						(tmpMCC != null && tmpMCC.length() > 0))
					{
						
						//System.out.println(">>>>>>>>>> SetApn: APN:"+tmpAPN+";MNC:"+tmpMNC+";MCC:"+tmpMCC+"  <<<<<<<<<<");
						//EcarPort.setAPN(context, tmpAPN, tmpMCC, tmpMNC);
						EcarPort.InsertAPN(context, tmpAPN, tmpAPN , tmpMCC ,tmpMNC);
						
					}
				}
				else if (cmd.equals("QueryAPN"))
				{
					//
					System.out.println(">>>>>>>>>> Recv msg: QueryAPN  <<<<<<<<<<");	
				}
				else if (cmd.equals("TTSSpeak"))
				{
					//
					String text = intent.getStringExtra("text");
					if (text != null && text.length() > 0)
					{
						//System.out.println(">>>>>>>>>> TTS Message:"+text+"  <<<<<<<<<<");	
						TXZTtsManager.getInstance().speakText(text);
					}
				}
				else if (cmd.equals("UpdateContacts"))
				{
					EcarPort.syncEcarContacts(context);
				}
			}
		}
	}
}
