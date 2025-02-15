package cc.derick.YouTubeMemberEmojiDownloader.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import org.eclipse.swt.widgets.Display;

import cc.derick.YouTubeMemberEmojiDownloader.modal.ImgData;
import cc.derick.YouTubeMemberEmojiDownloader.ui.Console;

public class DownloadService {
	
	public boolean downloadAndSaveImage(ImgData imgData) {
		
		Path filePath = imgData.getFilePath();
		URL srcUrl = imgData.getSrc();
		

        Console.out(String.format("%s 下載 %s%n", 
        		filePath.getFileName(), attemptDownload(filePath, srcUrl) ? "成功" : "失敗"));
		
        return true;
	}
	

	private boolean attemptDownload(Path filePath, URL srcUrl) {
		
		int maxRetries = 3;
	    int attempt = 0;
	    
	    while (attempt < maxRetries) {
			attempt++;
			
			try(InputStream in = srcUrl.openStream();
				FileOutputStream out = new FileOutputStream(filePath.toFile())) {
				
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
	            	Console.err(String.format("下載錯誤: %n %s / %s", filePath.getFileName().toString(), srcUrl));
	            }
				
			}
		}
	    
	    return false;
	}
	
}
