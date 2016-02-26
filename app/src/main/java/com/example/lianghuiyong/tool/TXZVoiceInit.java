package com.example.lianghuiyong.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.lianghuiyong.R;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZAsrManager.CommandListener;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZConfigManager.AsrEngineType;
import com.txznet.sdk.TXZConfigManager.InitListener;
import com.txznet.sdk.TXZConfigManager.InitParam;
import com.txznet.sdk.TXZConfigManager.TtsEngineType;
import com.txznet.sdk.TXZMusicManager;
import com.txznet.sdk.TXZMusicManager.MusicModel;
import com.txznet.sdk.TXZStatusManager;
import com.txznet.sdk.TXZStatusManager.StatusListener;
import com.txznet.sdk.TXZTtsManager;

import static com.example.lianghuiyong.tool.EcarPort.callECar;


public class TXZVoiceInit {
	public final static String TAG = "TXZVoiceInit";
	private Context mContext = null;
	public final static int MSG_SPEECH_OPEN_FM = 0x201;

	public TXZVoiceInit(Context context){
		mContext = context;
		init();
	}

	@SuppressLint("HandlerLeak")
	private Handler mSpeechHandler = new Handler(){
	
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SPEECH_OPEN_FM:
				TXZAsrManager.getInstance().regCommandForFM(87, 108, "CMD_OPEN_FM");
				break;

			default:
				break;
			}
		};
	};
	
	public void init(){
		TXZConfigManager.getInstance().initialize(
				mContext,
				new InitParam(//
						mContext.getString(R.string.txz_sdk_init_app_id), // 接入的appId
						mContext.getString(R.string.txz_sdk_init_app_token) // 接入的appToken
				).setWakeupKeywordsNew(
						mContext.getResources().getStringArray(R.array.txz_sdk_init_wakeup_keywords) // 设置的唤醒词
				).setTtsType(TtsEngineType.TTS_YUNZHISHENG
				).setAsrType(AsrEngineType.ASR_YUNZHISHENG),
				new InitListener() {
					@Override
					public void onSuccess() {

						TXZAsrManager.getInstance().regCommand(new String[]{"打开FM发射器", "打开发射器"}, "CMD_OPEN_FMT");
						TXZAsrManager.getInstance().regCommand(new String[]{"关闭FM发射器", "关闭发射器"}, "CMD_CLOSE_FMT");
						TXZAsrManager.getInstance().regCommand(new String[]{"返回主界面"}, "CMD_BACK_HOME");
						TXZAsrManager.getInstance().regCommand(new String[]{"打开行车记录仪", "打开记录仪"}, "CMD_OPEN_DVR");
						TXZAsrManager.getInstance().regCommand(new String[]{"打开电子狗", "打开安驾电子狗"}, "CMD_OPEN_EDOG");
						TXZAsrManager.getInstance().regCommand(new String[]{"拨打一键通", "呼叫一键通"}, "CMD_CALL_ECAR");
						//TXZAsrManager.getInstance().regCommandForFM(87, 108, "CMD_OPEN_FM");
						mSpeechHandler.sendEmptyMessageDelayed(MSG_SPEECH_OPEN_FM, 1000);


						TXZAsrManager.getInstance().addCommandListener(new CommandListener() {

							@Override
							public void onCommand(String cmd, String data) {
								if (data.equals("CMD_OPEN_FMT")) {
									Intent intent = null;
									TXZTtsManager.getInstance().speakText("正在为您打开FM发射器");
									intent = new Intent();
									intent.addCategory(Intent.CATEGORY_DEFAULT);
									intent.setClassName("com.mediatek.FMTransmitter", "com.mediatek.FMTransmitter.FMTransmitterActivity");
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
									mContext.startActivity(intent);
									return;
								}
								if (data.equals("CMD_CLOSE_FMT")) {
									if (mContext != null) {
										Intent fmtcloseIntent = new Intent("com.android.fmtranmistter.close");
										mContext.getApplicationContext().sendBroadcast(fmtcloseIntent);
									}
									return;
								}
								if (data.equals("CMD_BACK_HOME")) {
									TXZTtsManager.getInstance().speakText("正在为您返回主界面");
									Intent intent = new Intent();
									intent.setAction(Intent.ACTION_MAIN);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									intent.addCategory(Intent.CATEGORY_HOME);
									mContext.startActivity(intent);
									return;
								}
								if (data.equals("CMD_OPEN_DVR")) {
									Intent intent = null;
									TXZTtsManager.getInstance().speakText("已为您打开行车记录仪");
									intent = new Intent();
									intent.addCategory(Intent.CATEGORY_DEFAULT);
									//intent.setClassName("com.dvr.android.dvr", "com.dvr.android.dvr.RecorderActivity");
									intent.setClassName("com.dvr.android.dvr", "com.dvr.android.dvr.DVRActivity");
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK
											| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
									mContext.startActivity(intent);
									return;
								}
								if (data.equals("CMD_OPEN_EDOG")) {
									Intent intent = null;
									TXZTtsManager.getInstance().speakText("已为您打开安驾电子狗");
									intent = new Intent();
									intent.addCategory(Intent.CATEGORY_DEFAULT);
									intent.setClassName("com.chetuobang.android.edog", "com.chetuobang.android.edog.SplashActivity");
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
									mContext.startActivity(intent);
									return;
								}
								if (data.equals("CMD_CALL_ECAR")) {
									TXZTtsManager.getInstance().speakText("已为您打开一键通");
									callECar(mContext);
									return;
								}

								if (data.startsWith("CMD_OPEN_FM#")) {
									float fre = (float) (Math.floor(Float.parseFloat(data.replace("CMD_OPEN_FM#", "")) * 10f) / 10);
									int freq = (int) (fre * 10);
									Log.d(TAG, "fre = " + fre);
									if (freq < 875 || freq > 1080) {
										TXZTtsManager.getInstance().speakText("不支持这个频段");
									} else {
										TXZTtsManager.getInstance().speakText("已为您调频到" + fre + "兆赫");
										if (mContext != null) {
											Intent fmtIntent = new Intent("com.android.fmtranmistter.send");
											fmtIntent.putExtra("FRE", freq);
											mContext.getApplicationContext().sendBroadcast(fmtIntent);
										}
										return;
									}
								}

							}

						});

						TXZStatusManager.getInstance().addStatusListener(
								new StatusListener() {
									@Override
									public void onMusicPlay() {
										MusicModel m = TXZMusicManager
												.getInstance()
												.getCurrentMusicModel();
										Log.d("StatusListener",
												"onMusicPlay: "
														+ (m == null ? "none"
														: m.getTitle()));
									}

									@Override
									public void onMusicPause() {
										MusicModel m = TXZMusicManager
												.getInstance()
												.getCurrentMusicModel();
										Log.d("StatusListener",
												"onMusicPause: "
														+ (m == null ? "none"
														: m.getTitle()));
									}

									@Override
									public void onEndTts() {
										Log.d("StatusListener", "onEndTts");
									}

									@Override
									public void onEndCall() {
										Log.d("StatusListener", "onEndCall");
									}

									@Override
									public void onEndAsr() {
										Log.d("StatusListener", "onEndAsr");
									}

									@Override
									public void onBeginTts() {
										Log.d("StatusListener", "onBeginTts");
									}

									@Override
									public void onBeginCall() {
										Log.d("StatusListener", "onBeginCall");
									}

									@Override
									public void onBeginAsr() {
										Log.d("StatusListener", "onBeginAsr");
									}

									@Override
									public void onBeepEnd() {
										Log.d("StatusListener", "onBeepEnd");
									}
								});
						}

					@Override
					public void onError(int errCode, String errDesc) {

					}
				});
	}
}
