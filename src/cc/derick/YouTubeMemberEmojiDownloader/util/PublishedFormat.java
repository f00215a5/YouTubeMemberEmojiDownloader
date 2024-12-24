package cc.derick.YouTubeMemberEmojiDownloader.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import cc.derick.YouTubeMemberEmojiDownloader.ui.Console;

public class PublishedFormat {
	
	public static String toString(String time) {
		Instant instant = Instant.parse(time);
		ZonedDateTime zonedDateTime = instant.atZone(ZoneOffset.UTC);
		try {
			return DateTimeFormatter.ofPattern("yy.MM.dd").format(zonedDateTime);
		} catch (Exception e) {
			Console.err("時間轉換失敗");
		}
		return "";
	}

}
