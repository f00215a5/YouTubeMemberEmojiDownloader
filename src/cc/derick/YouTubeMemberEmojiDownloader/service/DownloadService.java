package cc.derick.YouTubeMemberEmojiDownloader.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Text;

import cc.derick.YouTubeMemberEmojiDownloader.modal.ImgData;

public class DownloadService {
	
	public boolean downloadAndSaveImage(ImgData imgData, Text console, String outputPath) {
	    
	    URL url = null;
	    StringBuffer fileNameSb = new StringBuffer(imgData.getAlt());
		
		try {
			
			url = new URI(imgData.getSrc()).toURL();
			
			Optional.ofNullable(getFileExtension(url))
        			.ifPresent(fileExtension -> fileNameSb.append(fileExtension));
			
			//檢查名稱
			Path outputFile = Paths.get(outputPath, fileNameSb.toString());
	        String baseName = fileNameSb.toString().substring(0, fileNameSb.lastIndexOf("."));;
	        int counter = 1;

	        Pattern pattern = Pattern.compile("\\((\\d+)\\)$");
	        Matcher matcher = pattern.matcher(baseName);

	        if (matcher.find()) {
	            counter = Integer.parseInt(matcher.group(1)) + 1;
	            baseName = baseName.substring(0, matcher.start());
	        }


	        while (Files.exists(outputFile)) {
	        	String extension = fileNameSb.toString().substring(fileNameSb.lastIndexOf("."));
	            fileNameSb.setLength(0);
	            fileNameSb.append(baseName).append("(").append(counter).append(")").append(extension);
	            outputFile = Paths.get(outputPath, fileNameSb.toString());
	            counter++;
	        }
	        
	        String fileName = fileNameSb.toString();
	        
	        console.append(String.format("%s 下載 %s%n", fileName, attemptDownload(url, fileName, outputPath) ? "成功" : "失敗"));
			
	        return true;
	        
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return false;
	}
	
	private static String getFileExtension(URL url) throws IOException {
		
		URLConnection connection = url.openConnection();
        String mimeType = connection.getContentType();
        
        if (mimeType != null) {
            switch (mimeType) {
                case "image/png":
                    return ".png";
                case "image/jpeg":
                    return ".jpg";
                case "image/gif":
                    return ".gif";
                case "image/webp":
                    return ".webp";
                default:
                    return "";
            }
        }
        return "";
    }

	private boolean attemptDownload(URL url, String fileName, String outputPath) {
		
		int maxRetries = 3;
	    int attempt = 0;
	    
	    while (attempt < maxRetries) {
			attempt++;
			
			try(InputStream in = url.openStream();
				FileOutputStream out = new FileOutputStream(new File(outputPath, fileName.toString()))) {
				
				byte[] buffer = new byte[4096];
				
				int bytesRead;
	            while ((bytesRead = in.read(buffer)) != -1) {
	                out.write(buffer, 0, bytesRead);
	            }
	            
	            return true;
				
			} catch (Exception e) {
				// TODO: handle exception
				
				try {
	                Thread.sleep(2000);
	            } catch (InterruptedException interruptedException) {
	                interruptedException.printStackTrace();
	            }
				
			}
		}
	    
	    return false;
	}
	
}
