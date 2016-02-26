package com.example.lianghuiyong.tool;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

import com.txznet.sdk.TXZCallManager;

import java.util.ArrayList;
import java.util.List;


public class EcarPort {
	public static String _Action_ = "com.android.ecar.recv";
	// CMD参数所用的标示
	public static String _CMD_ = "ecarSendKey";
	// TYPE参数的标示
	public static String _TYPE_ = "cmdType";
	// _TYPE_ = "standCMD"时为普通广播
	public static String _TYPE_STANDCMD_ = "standCMD";
	// 所传递的参数列表，每个参数名会用逗号分开，需要解释出参数名，
	// 再以此作为Key取出数据
	public static String _KEYS_ = "keySet";
	
	//apn database uri
    public static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");// 获取当前设置的APN  
    public static final Uri APN_TABLE_URI = Uri.parse("content://telephony/carriers");// 取得全部APN列表  
	

	public static void startVoip(Context context) {
		if (context != null) {
			Intent tmpIntent = new Intent();
			tmpIntent.setAction(_Action_);
			tmpIntent.putExtra(_CMD_, "StartVoip");
			tmpIntent.putExtra(_TYPE_, _TYPE_STANDCMD_);
			tmpIntent.putExtra(_KEYS_, "");
			context.sendBroadcast(tmpIntent);
		}
	}

	public static void callECar(Context context) {
		if (context != null) {
			Intent tmpIntent = new Intent();
			tmpIntent.setAction(_Action_);
			tmpIntent.putExtra(_CMD_, "CallECar");
			tmpIntent.putExtra(_TYPE_, _TYPE_STANDCMD_);
			tmpIntent.putExtra(_KEYS_, "");
			context.sendBroadcast(tmpIntent);
			
			
		}
	}

	public static void viewContacts(Context context) {
		if (context != null) {
			Intent tmpIntent = new Intent();
			tmpIntent.setAction(_Action_);
			tmpIntent.putExtra(_CMD_, "StartContacts");
			tmpIntent.putExtra(_TYPE_, _TYPE_STANDCMD_);
			tmpIntent.putExtra(_KEYS_, "");
			context.sendBroadcast(tmpIntent);
		}
	}
	
	/*
	 * 同步更新通讯录到txz
	 * 
	*/
	public static void syncEcarContacts(Context iCtx) {
		
		List<TXZCallManager.Contact> ecarlist = null;
		TXZCallManager.Contact con = null;
		
		if ((iCtx != null))
		{			
			String [] arrProjection = {"name","number"};
	        ContentResolver tmpContentResolver = iCtx.getContentResolver(); 
	        Uri tmpUri = Uri.parse("content://com.android.ecar.provider.contacts/Contacts");
	        Cursor tmpCursor = null;
	        tmpCursor = tmpContentResolver.query(tmpUri, arrProjection, null, null, null);
	        if (tmpCursor != null)
	        {
	        	ecarlist = new ArrayList<TXZCallManager.Contact>();
	        	String name = "";
	        	String number = "";
	        	
	        	if (ecarlist != null) {
	        		if (!ecarlist.isEmpty()){
	        			ecarlist.clear();
	        			TXZCallManager.getInstance().syncContacts(ecarlist);
	        		}
	        	}
	        	
	        	while (tmpCursor.moveToNext())
	        	{
	        		name = tmpCursor.getString(tmpCursor.getColumnIndex("name"));
	        		number = tmpCursor.getString(tmpCursor.getColumnIndex("number"));
	        		if ((name != null) && (name.length() > 0) && (number != null) && (number.length() > 0))
	        		{
						con = new TXZCallManager.Contact();
						con.setName(name);
						con.setNumber(number);
						ecarlist.add(con);
	        		}
	        			
	        	}
	        	tmpCursor.close();
	        	TXZCallManager.getInstance().syncContacts(ecarlist);
	        }
		}
	}
	
	/**
	 * 高德地图URI API  http://developer.amap.com/api/uri-api/android-uri-explain/
	 * cat=android.intent.category.DEFAULT
	 * dat=androidamap://navi?sourceApplication=appname&poiname=fangheng&lat=36.547901&lon=104.258354&dev=1&style=2
	 * pkg=com.autonavi.minimap
	 * context
	 * toLat:目的地纬度
	 * toLng:目的地经度
	 * name:POI名称
	 * style:导航方式(0 速度快; 1 费用少; 2 路程短; 3 不走高速；4 躲避拥堵；5 不走高速且避免收费；6
	 * 不走高速且躲避拥堵；7 躲避收费和拥堵；8 不走高速躲避收费和拥堵))
	 * dev:是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
	 */
	
	public static void startGaodeMapNavi(Context context, double toLat, double toLng, String name, int style, int dev) {

		String cat = Intent.CATEGORY_DEFAULT;
		String pkg = "com.autonavi.minimap";
		
		StringBuilder uri = new StringBuilder();
		uri.append("androidamap://navi?sourceApplication=AutoLauncher&poiname=");

		uri.append(name);
		uri.append("&lat=");
		uri.append(toLat);
		uri.append("&lon=");
		uri.append(toLng);
		uri.append("&dev=");
		uri.append(dev);
		uri.append("&style=");
		uri.append(style);

		String dat = uri.toString();

		Intent intent = new Intent();
		intent.addCategory(cat);
		intent.setPackage(pkg);
		intent.setData(Uri.parse(dat));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
    /** 
     * 向系统apn表中插入cmnet apn 
     *  
     * @param context 
     * @param name 
     *            APN名称 
     * @param apn 
     *            apn 
     * */  
    public static void InsertAPN(Context context, String name, String apn , String mcc ,String mnc) {  
        int id = -1;  
        ContentResolver resolver = context.getContentResolver();  
        ContentValues values = new ContentValues();  
        values.put("name", name);  
        values.put("apn", apn);  
        values.put("numeric", mcc+mnc);  
        values.put("proxy", "");  
        values.put("type", "default");  
        values.put("mcc", mcc);  
        values.put("mnc", mnc);  
        values.put("port", "");  
        values.put("mmsproxy", "");  
        values.put("mmsport", "");  
        values.put("user", "");  
        values.put("server", "");  
        values.put("password", "");  
        values.put("mmsc", "");  
  
        Cursor c = null;  
        try {  
            Uri newRow = resolver.insert(APN_TABLE_URI, values);  
            if (newRow != null) {  
                c = resolver.query(newRow, null, null, null, null);  
                int idindex = c.getColumnIndex("_id");  
                c.moveToFirst();  
                id = c.getShort(idindex);  
            }  
        } catch (SQLException e) {  
        }  
  
        if (c != null) {  
            c.close();  
        }  
        SetNowAPN(context, id);  
    } 

    /** 
     * 把指定的apn设置为当前的apn 
     *  
     * @param context 
     * @param id 
     *            系统数据库表中要设置为当前apn的id值 
     * */  
    public static void SetNowAPN(final Context context, final int id) {  
        ContentResolver resolver = context.getContentResolver();  
        ContentValues values = new ContentValues();  
        values.put("apn_id", id);  
        try {  
            resolver.update(PREFERRED_APN_URI, values, null, null);  
        } catch (SQLException e) {  
        }  
    }  
    
    public static void deleteCurAPN(Context context) {
    	ContentResolver resolver = context.getContentResolver();
    	
    	long apnId = -1;
    	Uri uri = null;
    	Cursor cr = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null); 
    	while (cr != null && cr.moveToNext()) {
    		int idindex = cr.getColumnIndexOrThrow("_id");
    		apnId = cr.getLong(idindex);
    		uri = ContentUris.withAppendedId(APN_TABLE_URI , apnId);
    		Log.d("Ecar", "apnid1="+apnId);
    		Log.d("Ecar", "uri = "+uri);
    		if (uri!=null){
    			resolver.delete(uri, null, null);
    		}
    	}
    	if (cr!=null){
    		cr.close();
    		cr = null;
    	}

  
    }
    
    public boolean getCurAPN(Context context) {
    	Cursor cr = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null); 
    	while (cr != null && cr.moveToNext()) {
        	return true;
    	}
		return false;

    }
}
