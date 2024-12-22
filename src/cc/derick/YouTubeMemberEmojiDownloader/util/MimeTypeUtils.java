package cc.derick.YouTubeMemberEmojiDownloader.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MimeTypeUtils {
	
    private static final Map<String, String> mimeToExtensionMap = new HashMap<>();
    
    static {
        mimeToExtensionMap.put("image/png", ".png");
        mimeToExtensionMap.put("image/jpeg", ".jpg");
        mimeToExtensionMap.put("image/gif", ".gif");
        mimeToExtensionMap.put("image/webp", ".webp");
        mimeToExtensionMap.put("image/svg+xml", ".svg");
        mimeToExtensionMap.put("image/bmp", ".bmp");
        mimeToExtensionMap.put("image/tiff", ".tiff");
        mimeToExtensionMap.put("image/x-icon", ".ico");
    }
    
    
    public static String getExtension(String mimeType) {
        return mimeToExtensionMap.getOrDefault(mimeType, "");
    }

    
    public static void addMimeType(String mimeType, String extension) {
        mimeToExtensionMap.put(mimeType, extension);
    }

    
    public static Map<String, String> getAllMimeTypes() {
        return mimeToExtensionMap;
    }
    
    public static Set<String> getExtensionsSet() {
    	return new HashSet<>(mimeToExtensionMap.values());
    }

}
