package com.example.lianghuiyong.tool;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.glsx.sdk.ddmusic.PlayCommand;
import com.txznet.sdk.TXZMusicManager.MusicModel;
import com.txznet.sdk.TXZMusicManager.MusicTool;
import com.txznet.sdk.TXZMusicManager.MusicToolStatusListener;
import com.txznet.sdk.TXZTtsManager;

public class DidihuMusicTool implements MusicTool {
	private Context context;
	//音乐
	Intent kumusicIntent = new Intent();

	public DidihuMusicTool(Context context){
		this.context = context ;
	}
	@Override
	public void unfavourMusic() {
		//TXZTtsManager.getInstance().speakText("模拟取消收藏当前音乐指令");
	}

	@Override
	public void switchSong() {
		//TXZTtsManager.getInstance().speakText("模拟切歌");
	}

	@Override
	public void switchModeRandom() {
		//TXZTtsManager.getInstance().speakText("模拟切换到随机播放模式");
	}

	@Override
	public void switchModeLoopOne() {
		//TXZTtsManager.getInstance().speakText("模拟切换到单曲循环播放模式");
	}

	@Override
	public void switchModeLoopAll() {
		//TXZTtsManager.getInstance().speakText("模拟切换到顺序播放播放模式");
	}

	@Override
	public void setStatusListener(MusicToolStatusListener listener) {
	}

	@Override
	public void prev() {
		//上一曲
		PlayCommand.musicPrevious(context);
		//TXZTtsManager.getInstance().speakText("模拟播放上一首");
	}

	@Override
	public void playRandom() {
		//TXZTtsManager.getInstance().speakText("模拟随便听听");

	}

	private String toArrayString(String[] ss) {
		if (ss == null || ss.length == 0)
			return null;
		StringBuilder s = new StringBuilder();
		for (String t : ss) {
			if (s.length() > 0)
				s.append(',');
			s.append(t);
		}
		return s.toString();
	}

	@Override
	public void playMusic(MusicModel model) {
		//TXZTtsManager.getInstance().speakText(
		//		"模拟播放音乐模型：" + "歌名=" + model.getTitle() + "，歌手=" + toArrayString(model.getArtist()) + "，专辑=" + model.getAlbum() + "，关键字=" + toArrayString(model.getKeywords()));

		Intent intent = new Intent("com.glsx.bootup.ddmusic");
		intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);

		if (model.getTitle() != null) {
			intent.putExtra("musicName", model.getTitle()); //歌名
		}

		if(model.getArtist() != null){
			intent.putExtra("artist",model.getArtist()[0]); //歌手名
		}
		context.startActivity(intent);
	}

	@Override
	public void playFavourMusic() {
		//TXZTtsManager.getInstance().speakText("模拟播放收藏音乐");
	}

	@Override
	public void play() {
		//TXZTtsManager.getInstance().speakText("模拟打开音乐");
		kumusicIntent.setClassName("com.glsx.ddmusic", "com.glsx.ddmusic.ui.launcher.MusicActivity"); //滴滴音乐
		//kumusicIntent.setClassName("cn.kuwo.kwmusiccar","cn.kuwo.kwmusiccar.WelcomeActivity");//酷我音乐
		kumusicIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

		//打开播放器
		context.startActivity(kumusicIntent);

		//播放
		PlayCommand.musicPlay(context);
	}

	@Override
	public void pause() {
		//TXZTtsManager.getInstance().speakText("模拟暂停指令");
		//暂停
		PlayCommand.musicPause(context);
	}

	@Override
	public void exit() {
		PlayCommand.musicPause(context);
		//TXZTtsManager.getInstance().speakText("模拟关闭音乐");
	}

	@Override
	public void next() {
		//TXZTtsManager.getInstance().speakText("模拟播放下一首");
		//下一曲
		PlayCommand.musicNext(context);
	}

	@Override
	public boolean isPlaying() {
		return false;
	}

	@Override
	public MusicModel getCurrentMusicModel() {
		return null;
	}

	@Override
	public void favourMusic() {
		//TXZTtsManager.getInstance().speakText("模拟收藏当前音乐指令");
	}
}
