package com.alienlab.wechat.onlive.service.downloader;

import java.io.File;

import com.alienlab.wechat.onlive.service.OnliveStart;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;

/**
 * @author Eric
 * @Date:2016年4月3日上午10:28:54
 * @version 1.0
 */
public class AudioCorrect {
	/**
	 * 音频格式转换
	 * 
	 * @param webroot
	 *            根目录
	 * @param sourcePath
	 *            输出目录
	 */

	public static boolean Amr2Mp3(String amrname) {
		Runtime run = null;
		try {
			run = Runtime.getRuntime();
			long start = System.currentTimeMillis();
			String cmd = OnliveStart.ffmpegpath+"\\"+"ffmpeg.exe -i \"" + OnliveStart.streamPath + "\\" + amrname
					+ "\" -acodec libmp3lame \"" + OnliveStart.streamPath +"\\"+ amrname + ".mp3\"";
			System.out.println("cmd>>>"+cmd);
			Process p = run.exec(cmd);

			p.getOutputStream().close();
			p.getInputStream().close();
			p.getErrorStream().close();
			p.waitFor();
			long end = System.currentTimeMillis();
			System.out.println(cmd + "转换成功，用时:" + (end - start) + "ms");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			run.freeMemory();
		}

	}

	// 测试
	public static void main(String[] args) {

		String path1 = "d:\\1460023855351.amr";
		String path2 = "d:\\export.mp3";
		changeToMp3(path1, path2);

	}

	public static void changeToMp3(String sourcePath, String targetPath) {
		File source = new File(sourcePath);
		File target = new File(targetPath);
		AudioAttributes audio = new AudioAttributes();
		Encoder encoder = new Encoder();

		audio.setCodec("libmp3lame");
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("mp3");
		attrs.setAudioAttributes(audio);

		try {
			encoder.encode(source, target, attrs);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InputFormatException e) {
			e.printStackTrace();
		} catch (EncoderException e) {
			e.printStackTrace();
		}
	}

}
