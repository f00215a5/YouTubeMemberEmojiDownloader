package cc.derick.YouTubeMemberEmojiDownloader.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.swt.widgets.Display;
import org.json.JSONArray;
import org.json.JSONObject;

import cc.derick.YouTubeMemberEmojiDownloader.modal.ImgData;
import cc.derick.YouTubeMemberEmojiDownloader.modal.VideoInfoModal;
import cc.derick.YouTubeMemberEmojiDownloader.ui.Console;
import cc.derick.YouTubeMemberEmojiDownloader.util.PublishedFormat;

public class PlayListService {
	
	private DataProcessedService dataProcessedService = new DataProcessedService();
	private DownloadService downloadService = new DownloadService();
	
	public boolean handlePlaylistJson(String filePath, Path outputPath, Map<String, Boolean> method, 
			ExecutorService executorService, AtomicBoolean isStop) {
		
		boolean isThumbnail = method.get("Thumbnail");
		boolean isFileName = method.get("FileName");
		List<VideoInfoModal> infoList = new ArrayList();
		
		try {
		
			String jsonString = new String(Files.readAllBytes(Paths.get(filePath)));
		    JSONArray playlist = new JSONArray(jsonString);
		    
		    for (int i = 0; i < playlist.length(); i++) {
		    	
		    	JSONObject item = playlist.getJSONObject(i);
		        
		    	String title = item.getString("title");
		    	String channelTitle = item.getString("channelTitle");
		    	String videoId = item.getString("videoId");
		    	String publishedAtString = item.getString("publishedAt");
		    	String publishedAt = PublishedFormat.toString(publishedAtString);
		    	
		    	VideoInfoModal videoInfo = new VideoInfoModal(title, channelTitle, videoId, publishedAt);
		    	
		    	if (isThumbnail) {
		            JSONObject thumbnails = item.getJSONObject("thumbnails");
		            String thumbnailUrl = Optional.ofNullable(thumbnails.optJSONObject("maxres"))
		                    				.map(maxres -> maxres.optString("url", null))
		                    				.orElse(thumbnails.getJSONObject("high").getString("url"));
		            
		            videoInfo.setThumbnailUrl(thumbnailUrl);
		        }
		    	
		    	if (isFileName) {
		            
		            String fileName = publishedAt +
		            					title.replaceAll("/", "⧸")
		            						.replaceAll(":", "：")
		            						.replaceAll("\\?", "？")
		            						.replaceAll("\\\\", "∖")
		            						.replace("\"", "＂")
		            						.replace("<", "＜")
		            				        .replace(">", "＞")
		            				        .replace("|", "｜")
		            						+
		            					" [" + videoId + "]" +
		            					".mp4";

		            videoInfo.setFileName(fileName);
		        }
		    	
		    	infoList.add(videoInfo);
		    	
			}
		    
		    if(isThumbnail && !isStop.get()) {
		    	downloadThumbnail(infoList, outputPath, executorService, isStop, () -> {
	                Display.getDefault().asyncExec(() -> {
	                    Console.out("縮圖下載完成");
	                });
	            });
		    }
		    
		    if(isFileName && !isStop.get()) {
		    	outputVideoFileName(infoList, outputPath, isStop);
		    }
			
			return true;
		
		} catch (IOException e) {
			// TODO 自動產生的 catch 區塊
			Console.err("處理Json檔案錯誤");
			return true;
		}
		
	}
	
	public void downloadThumbnail(List<VideoInfoModal> infoList, Path outputPath, 
			ExecutorService executorService, AtomicBoolean isStop, Runnable onComplete) {
		
		List<ImgData> imgDatas = dataProcessedService.viedoInfoParser(infoList, outputPath);
		
		AtomicLong successCount = new AtomicLong(0);
        AtomicLong failureCount = new AtomicLong(0);
        
        CountDownLatch latch = new CountDownLatch(imgDatas.size());
    	
        for (ImgData imgData : imgDatas) {
        	
        	if (isStop.get()) {
        		latch.countDown();
                continue;
            }
        	
        	executorService.submit(() -> {
                boolean success = downloadService.downloadAndSaveImage(imgData);

                if (success) {
                    successCount.incrementAndGet();
                } else {
                    failureCount.incrementAndGet();
                }

                latch.countDown();
            });
        	
        }
        
        
        executorService.submit(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                Console.err("線程等待錯誤" + e);
            }

            long success = successCount.get();
            long failure = failureCount.get();

            Display.getDefault().asyncExec(() -> {
                Console.out(String.format("下载成功: %d, 下载失败: %d%n", success, failure));
            });
            
            if (onComplete != null) {
                onComplete.run(); // 通知完成
            }
        });
		
	}
	
	public void outputVideoFileName(List<VideoInfoModal> infoList, Path outputPath, AtomicBoolean isStop) {
		
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
		
		Path outputFile = outputPath.resolve("VideoFileNames_" + timestamp + ".txt");
		
		try (BufferedWriter writer = 
				Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
	        for (VideoInfoModal videoInfo : infoList) {
	            
	            if (isStop.get()) {
	                Console.out("以按下停止，終止輸出...");
	                return;
	            }
       
	            String fileName = videoInfo.getFileName();
	            if (fileName != null && !fileName.isEmpty()) {
	                writer.write(fileName);
	                writer.newLine();
	            }
	        }
	        
	        Console.out("文件输出完成: " + outputFile.toAbsolutePath() + System.lineSeparator());
	        
	    } catch (IOException e) {
	    	
	        Console.err("文件輸出失敗: " + e.getMessage());
	        
	    }
		
	}
	
}
