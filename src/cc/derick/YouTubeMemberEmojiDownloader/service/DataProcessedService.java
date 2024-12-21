package cc.derick.YouTubeMemberEmojiDownloader.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.swt.widgets.Text;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cc.derick.YouTubeMemberEmojiDownloader.modal.ImgData;

public class DataProcessedService {
	
	public List<ImgData> htmlParser(String filePath, Text console) {

		List<ImgData> imgDatas = new ArrayList<>();
		
		try {
			
			List<String> lines = Files.readAllLines(Paths.get(filePath));
	        
	        StringBuffer html = new StringBuffer();
	        for (String line : lines) {
	        	html.append(line).append("\n");
	        }
			
			Document document = Jsoup.parse(html.toString());
			
			Elements imgTags = document.select("img");
			
			imgTags.forEach(img -> {
				String srcTemp = img.attr("src");
				String src = srcTemp.substring(0, srcTemp.lastIndexOf("="));
				String alt = Optional.ofNullable(img.attr("alt"))
						.filter(a -> !a.isEmpty())
						.orElseGet(() -> {
							String fileName = src.substring(src.lastIndexOf("/") + 1);
							fileName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
							return fileName.length() > 8 ? fileName.substring(fileName.length() - 8) : fileName;
						})
						.replaceAll("[\\\\/:*?\"<>|]", "_");
				imgDatas.add(new ImgData(alt, src));
			});
			
		} catch (Exception e) {
			// TODO: handle exception
			console.append(String.format("Html轉換錯誤: %s%n", e));
		}
		
		
		return imgDatas;
		
	}
	
	public List<ImgData> txtParser(String filePath, Text console) {
		
		List<ImgData> imgDatas = new ArrayList<>();
		
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			
		    lines.forEach(line -> {
		        int index = line.indexOf("http://") != -1 ? line.indexOf("http://") : line.indexOf("https://");
		        if (index != -1) {
		            String src = line.substring(index).trim();
		            String alt = Optional.of(line.substring(0, index).trim())
		                .filter(a -> !a.isEmpty() && index != 0)
		                .orElseGet(() -> {
		                    String fileName = src.substring(src.lastIndexOf("/") + 1);
		                    fileName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
		                    return fileName.length() > 8 ? fileName.substring(fileName.length() - 8) : fileName;
		                })
		                .replaceAll("[\\\\/:*?\"<>|]", "_");
		            imgDatas.add(new ImgData(alt, src));
		        }
		    });
		    
		} catch (IOException e) {
			console.append(String.format("TXT轉換錯誤: %s%n", e));
		}
		
		return imgDatas;
	}
	
	public List<ImgData> inputParser(String input, Text console) {
		
		List<ImgData> imgDatas = new ArrayList<>();
		
		Document document = Jsoup.parse(input);
		
		Elements imgTags = document.select("img");
		
		imgTags.forEach(img -> {
				String srcTemp = img.attr("src");
				String src = srcTemp.substring(0, srcTemp.lastIndexOf("="));
				String alt = Optional.ofNullable(img.attr("alt"))
						.filter(a -> !a.isEmpty())
						.orElseGet(() -> {
							String fileName = src.substring(src.lastIndexOf("/") + 1);
							fileName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
							return fileName.length() > 8 ? fileName.substring(fileName.length() - 8) : fileName;
						})
						.replaceAll("[\\\\/:*?\"<>|]", "_");
				imgDatas.add(new ImgData(alt, src));
		});
		
		return imgDatas;
		
	}
	
}
