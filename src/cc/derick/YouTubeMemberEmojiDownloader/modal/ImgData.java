package cc.derick.YouTubeMemberEmojiDownloader.modal;

import java.net.URL;
import java.nio.file.Path;

public class ImgData {
	
	private Path filePath;
	
	private URL src;

	public ImgData(Path filePath, URL src) {
		this.filePath = filePath;
		this.src = src;
	}

	public Path getFilePath() {
		return filePath;
	}

	public URL getSrc() {
		return src;
	}

}
