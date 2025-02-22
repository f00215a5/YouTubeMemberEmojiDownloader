package cc.derick.YouTubeMemberEmojiDownloader.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cc.derick.YouTubeMemberEmojiDownloader.modal.ImgData;
import cc.derick.YouTubeMemberEmojiDownloader.modal.VideoInfoModal;
import cc.derick.YouTubeMemberEmojiDownloader.ui.Console;
import cc.derick.YouTubeMemberEmojiDownloader.util.MimeTypeUtils;

public class DataProcessedService {
	
	private Set<Path> existingImagePaths = new HashSet<>();
	
	private String[] extensions = MimeTypeUtils.getExtensionsSet().toArray(new String[0]);
	
	private void loadExistingImagePaths(Path outputPath) {

		existingImagePaths.clear();
		
		for(String extension : extensions) {
			String pattern = "*" + extension;
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(outputPath, pattern)) {
				for (Path entry : stream) {
					existingImagePaths.add(entry);
				}
			} catch (Exception e) {
				Console.err("獲得檔案清單錯誤");
			}
		}

    }
	
	public List<ImgData> htmlParser(String filePath, Path outputPath) {

		List<ImgData> imgDatas = new ArrayList<>();
		
		try {
			
			loadExistingImagePaths(outputPath);
			
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
				
				URL url = checkURL(src);
				
				if(url == null) {
					return;
				}
				
				String alt = Optional.ofNullable(img.attr("alt"))
						.filter(a -> !a.isEmpty())
						.orElseGet(() -> {
							return "img";
						})
						.replaceAll("[\\\\/:*?\"<>|]", "_");
				
				String extension = getExtension(url);
				
				if(extension == null) {
					return;
				}
				
				Path path = getUniqueFilePath(outputPath, alt, extension);
				
				if(path == null) {
					return;
				}
				
				imgDatas.add(new ImgData(path, url));
				
			});
			
		} catch (Exception e) {
			// TODO: handle exception
			Console.err(String.format("Html轉換錯誤: %s%n", e));
		}
		
		
		return imgDatas;
		
	}
	
	public List<ImgData> txtParser(String filePath, Path outputPath) {
		
		List<ImgData> imgDatas = new ArrayList<>();
		
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			
			loadExistingImagePaths(outputPath);
			
		    lines.forEach(line -> {
		        int index = line.indexOf("http://") != -1 ? line.indexOf("http://") : line.indexOf("https://");
		        if (index != -1) {
		        	
		            String src = line.substring(index).trim();
		            
		            URL url = checkURL(src);
					
					if(url == null) {
						return;
					}
					
		            String alt = Optional.of(line.substring(0, index).trim())
		                .filter(a -> !a.isEmpty() && index != 0)
		                .orElseGet(() -> {  
		                    return "img";
		                })
		                .replaceAll("[\\\\/:*?\"<>|]", "_");
		            
		            String extension = getExtension(url);
					
					if(extension == null) {
						return;
					}
					
					Path path = getUniqueFilePath(outputPath, alt, extension);
					
					if(path == null) {
						return;
					}
		            
					imgDatas.add(new ImgData(path, url));
		        }
		    });
		    
		} catch (IOException e) {
			Console.err(String.format("TXT轉換錯誤: %s%n", e));
		}
		
		return imgDatas;
	}
	
	public List<ImgData> inputParser(String input, Path outputPath) {
		
		List<ImgData> imgDatas = new ArrayList<>();
		
		loadExistingImagePaths(outputPath);
		
		Document document = Jsoup.parse(input);
		
		Elements imgTags = document.select("img");
		
		imgTags.forEach(img -> {
				String srcTemp = img.attr("src");
				String src = srcTemp.substring(0, srcTemp.lastIndexOf("="));
				
				URL url = checkURL(src);
				
				if(url == null) {
					return;
				}
				
				String alt = Optional.ofNullable(img.attr("alt"))
						.filter(a -> !a.isEmpty())
						.orElseGet(() -> {
							return "img";
						})
						.replaceAll("[\\\\/:*?\"<>|]", "_");
				
				String extension = getExtension(url);
				
				if(extension == null) {
					return;
				}
				
				Path path = getUniqueFilePath(outputPath, alt, extension);
				
				if(path == null) {
					return;
				}
	            
				imgDatas.add(new ImgData(path, url));
		});
		
		return imgDatas;
		
	}
	
	public List<ImgData> viedoInfoParser(List<VideoInfoModal> infoList, Path outputPath) {
		
		List<ImgData> imgDatas = new ArrayList<>();
		loadExistingImagePaths(outputPath);
		
		infoList.forEach(info -> {
			URL url = checkURL(info.getThumbnailUrl());
			
			if(url == null) {
				return;
			}
			
			String fileName = info.getTitle()
								.replaceAll("/", "⧸")
								.replaceAll(":", "：")
								.replaceAll("\\?", "？")
								.replaceAll("\\\\", "∖")
								.replace("\"", "＂")
								.replace("<", "＜")
						        .replace(">", "＞")
						        .replace("|", "｜")+
							" [" + info.getVideoId() + "]";
			
			String extension = getExtension(url);
			
			if(extension == null) {
				return;
			}
			
			Path path = getUniqueFilePath(outputPath, fileName, extension);
			
			if(path == null) {
				return;
			}
            
			imgDatas.add(new ImgData(path, url));
			
		});
				
		return imgDatas;
		
	}
	
	public Path getUniqueFilePath(Path outputPath, String alt, String extension) {
			
			Path outputFile;
			int counter = 1;
			
			StringBuffer fileName = new StringBuffer(alt + extension);
			if(fileName.length() > 255) {
				fileName.setLength(0);
				fileName.append("img").append(extension);
			}
			
			outputFile = outputPath.resolve(fileName.toString());
			
			while (existingImagePaths.contains(outputFile) && counter < 1000) {
		        
		        fileName.setLength(0);
		        fileName.append(alt).append("(").append(counter).append(")").append(extension);
		        
		        outputFile = outputPath.resolve(fileName.toString());
		        
		        counter++;
		    }
			
			if(counter == 1000) {
				Console.err(String.format("檔名重複衝突: %s %n", alt));
				return null;
			}
			
			try {
		        String normalizedPath = new String(outputFile.toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		        outputFile = Paths.get(normalizedPath);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }

			existingImagePaths.add(outputFile);
		    return outputFile;
		}
	
	private URL checkURL(String src) {
		
		if(src.startsWith("http://") || src.startsWith("https://")) {
			
			try {
				return new URI(src).toURL();
			} catch (MalformedURLException e) {
				Console.err(String.format("MalformedURLException: %s%n", src));
				return null;
			} catch (URISyntaxException e) {
				Console.err(String.format("URISyntaxException: %s%n", src));
				return null;
			}
			
		}
		
		//使用相對路徑
		Console.err(String.format("src格式錯誤: %s%n", src));
		return null;
		
	}
		
	private String getExtension(URL url) {
		
        try {
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            connection.connect();

            String mimeType = connection.getContentType();
            if (mimeType != null) {
                
                return MimeTypeUtils.getExtension(mimeType);
                
            } else {
            	Console.err(String.format("無法取得MIME: %n"));
                return null;
            }

        } catch (IOException e) {
            Console.err(String.format("URL格式錯誤: %n"));
            return null;
        }
        
	}
	
}
