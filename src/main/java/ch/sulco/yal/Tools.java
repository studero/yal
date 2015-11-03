package ch.sulco.yal;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Tools {
	public static void main(String[] args) throws Exception {
		AppConfig appConfig = new AppConfig();
		List<String> files = new ArrayList<>();
		files.add("/sounds/Buzzy_b_03.wav");
		files.add("/sounds/dx5_110_01.wav");
		files.add("/sounds/fm_edd_109_05.wav");
		for (String file : files) {
			byte[] data = Files.readAllBytes(Paths.get(Tools.class.getResource(file).getFile()));
			Clip clip = AudioSystem.getClip();
			clip.open(appConfig.getAudioFormat(), data, 0,
					data.length);
			System.out.println(file);
			System.out.println(data.length);
			System.out.println(clip.getMicrosecondLength());
		}
	}
}
