package com.zzpxyx.subplayer.parser;

import java.io.File;
import java.util.List;

import com.zzpxyx.subplayer.core.Subtitle;

public class AdaptvieParser {
	public List<Subtitle> getSubtitles(File file) {
		List<Subtitle> subtitleList;

		String fileName = file.getName();
		String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
		switch (fileExtName) {
		case "srt":
		case "ssa":
		case "ass":
			return null;
		default:
			return null;
		}
	}
}
