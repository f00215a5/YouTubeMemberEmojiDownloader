package cc.derick.YouTubeMemberEmojiDownloader.modal;

public class VideoInfoModal {
	
	private String title;
	
	private String channelTitle;
	
	private String videoId;
	
	private String publishedAt;
	
	private String fileName;
	
	private String thumbnailUrl;
	

	public VideoInfoModal(String title, String channelTitle, String videoId, String publishedAt) {
		this.title = title;
		this.channelTitle = channelTitle;
		this.videoId = videoId;
		this.publishedAt = publishedAt;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setChannelTitle(String channelTitle) {
		this.channelTitle = channelTitle;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public void setPublishedAt(String publishedAt) {
		this.publishedAt = publishedAt;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getTitle() {
		return title;
	}

	public String getChannelTitle() {
		return channelTitle;
	}

	public String getVideoId() {
		return videoId;
	}

	public String getPublishedAt() {
		return publishedAt;
	}

	public String getFileName() {
		return fileName;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	
}
