package com.example.lianghuiyong.data;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.example.lianghuiyong.R;

//查询数据库获取城市天气中的代码
public class DBManager {
	private final int BUFFER_SIZE = 400000;
	public static final String PACKAGE_NAME = "com.example.lianghuiyong";
	public static final String DB_NAME = "citychin.db";
	public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME + "/databases";

	private Context context;

	public DBManager(Context context) {
		this.context = context;
	}

	/** copy the database under raw*/
	public void copyDatabase() {

		File file = new File(DB_PATH);
		if (!file.isDirectory())
		{
			file.mkdir();
		}
		String dbfile = DB_PATH + "/" + DB_NAME;
		try {
			//if (!(new File(dbfile).exists())) {
				FileOutputStream fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[BUFFER_SIZE];
				readDB(fos, buffer, R.raw.citychina);
				fos.close();
			//}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void readDB(FileOutputStream fos, byte[] buffer, int db_id) throws IOException {
		int count;
		InputStream is;
		is = this.context.getResources().openRawResource(db_id);
		while ((count = is.read(buffer)) > 0) {
			fos.write(buffer, 0, count);
		}
		is.close();
	}
}
