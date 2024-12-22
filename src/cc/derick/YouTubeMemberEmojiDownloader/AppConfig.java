package cc.derick.YouTubeMemberEmojiDownloader;

public class AppConfig {
	
	public static final String VERSION = "1.1.0";
	private static int threadPoolSize = 10;
	
	public static int getThreadPoolSize() {
        return threadPoolSize;
    }

    public static void setThreadPoolSize(int size) {
        threadPoolSize = size + 1;
    }
	
}
