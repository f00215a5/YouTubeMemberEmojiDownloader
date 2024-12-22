package cc.derick.YouTubeMemberEmojiDownloader.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cc.derick.YouTubeMemberEmojiDownloader.AppConfig;
import cc.derick.YouTubeMemberEmojiDownloader.TaskCompletionCallback;
import cc.derick.YouTubeMemberEmojiDownloader.modal.ImgData;
import cc.derick.YouTubeMemberEmojiDownloader.service.DataProcessedService;
import cc.derick.YouTubeMemberEmojiDownloader.service.DownloadService;

public class DownloaderUI {
	
	private static ExecutorService executorService;
	
	static {
        initializeThreadPool();
    }
	
	private static DownloadService downloadService = new DownloadService();
	private static DataProcessedService dataProcessedService = new DataProcessedService();
	
	private static InputWindow inputWindow;
	
	private static AtomicBoolean isStop = new AtomicBoolean(false);
	

    public static void main(String args[]) {
        
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText(String.format("會員表情下載器 %s", AppConfig.VERSION));
        shell.setSize(600, 600);
        shell.setLayout(new GridLayout(3, false));
        
        // 上方工具區
        Label fileLabel = new Label(shell, SWT.NONE);
        fileLabel.setText("選擇檔案:");
        
        Text fileField = new Text(shell, SWT.BORDER);
        fileField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        Button fileButton = new Button(shell, SWT.PUSH);
        fileButton.setText("瀏覽");
        
        Label outPutLabel = new Label(shell, SWT.NONE);
        outPutLabel.setText("輸出路徑:");
        
        Text outPutField = new Text(shell, SWT.BORDER);
        outPutField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        Button outPutButton = new Button(shell, SWT.PUSH);
        outPutButton.setText("瀏覽");
        
        Label formatLabel = new Label(shell, SWT.NONE);
        formatLabel.setText("選擇格式:");

        Combo formatCombo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
        formatCombo.setItems("Html 節點檔案", "直接貼上節點", "檔名+網址");
        formatCombo.select(0);
        formatCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        Button textAreaButton = new Button(shell, SWT.PUSH);
        textAreaButton.setText("輸入");
        textAreaButton.setEnabled(false);  // Initially disabled
        
        // 中間 Console 區塊
        StyledText console = new StyledText(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
        console.setEditable(false);
        console.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));  // span 4 columns
        Console.init(console);
        
        // 下方按鈕
        Composite bottomPanel = new Composite(shell, SWT.NONE);
        bottomPanel.setLayout(new GridLayout(2, false));
        
        Button startButton = new Button(bottomPanel, SWT.PUSH);
        startButton.setText("開始下載");
        
        Button stopButton = new Button(bottomPanel, SWT.PUSH);
        stopButton.setText("停止");
        stopButton.setEnabled(true);
        
        
        // 事件處理
        fileButton.addListener(SWT.Selection, e -> {
        	
            FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
            fileDialog.setText("選擇檔案");
            fileDialog.setFilterExtensions(new String[] { "*.txt" });
            
            String selectedFile = fileDialog.open();
            if (selectedFile != null) {
                fileField.setText(selectedFile);
            }
            
        });

        outPutButton.addListener(SWT.Selection, e -> {
        	
            DirectoryDialog dirDialog = new DirectoryDialog(shell, SWT.OPEN);
            dirDialog.setText("選擇輸出資料夾");
            
            String selectedDir = dirDialog.open();
            if (selectedDir != null) {
                outPutField.setText(selectedDir);
            }
            
        });
        
        startButton.addListener(SWT.Selection, e -> {
        	String selectedFile = fileField.getText();
        	String outputPathString = outPutField.getText().trim();
            int selectedIndex = formatCombo.getSelectionIndex();
            
            if (selectedFile.isEmpty() || outputPathString.isEmpty()) {
                Console.err("請選擇文件和輸出資料夾\n");
                return;
            }
            
            if (!isValidPath(outputPathString)) {
            	Console.err("路徑包含非法字符，請重新輸入\n");
                return;
            }
            
            Path outputPath = Paths.get(outputPathString);
            
            if (!Files.exists(outputPath)) {
                try {
                    Files.createDirectories(outputPath); // 如果不存在，创建该目录
                    Console.out("已成功創建輸出資料夾: " + outputPath.toString() + "\n");
                } catch (IOException ex) {
                    Console.err("創建資料夾時出現錯誤: " + ex.getMessage() + "\n");
                    return;
                }
            }
            
            Console.out("開始下載...\n");
            stopButton.setEnabled(false);
            startButton.setEnabled(true);
            
            List<ImgData> imgDatas = new ArrayList<>();
            // 邏輯預留區
            if(selectedIndex == 0) {
            	imgDatas = dataProcessedService.htmlParser(selectedFile, outputPath);
            } else if (selectedIndex == 2) {				
            	imgDatas = dataProcessedService.txtParser(selectedFile, outputPath);
			}
            
            handleProcessedFormat(imgDatas, new TaskCompletionCallback() {
                @Override
                public void onTaskCompleted() {
                    
                    Display.getDefault().asyncExec(() -> {
                        startButton.setEnabled(true);
                        stopButton.setEnabled(false);
                    });
                }
            });
            
        });

        stopButton.addListener(SWT.Selection, e -> {
        	Console.out("停止下載中...\n");
            // 邏輯預留區
            isStop.set(true);
            stopButton.setEnabled(true);
            startButton.setEnabled(false);
        });
        
        formatCombo.addSelectionListener(new SelectionAdapter() {
        	
        	@Override
            public void widgetSelected(SelectionEvent e) {
                if (formatCombo.getSelectionIndex() == 2) {
                	
                    textAreaButton.setEnabled(false);
                    startButton.setEnabled(true);
                    fileButton.setEnabled(true);
                    fileField.setEnabled(true);
                    fileField.setText("");
                    
                } else {
                	
                    textAreaButton.setEnabled(true);
                    startButton.setEnabled(false);
                    fileButton.setEnabled(false);
                    fileField.setEnabled(false);
                    
                }
            }
        	
		});
        
        
        //直接輸入
        textAreaButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                if (inputWindow == null) {
                    inputWindow = new InputWindow(outPutField, dataProcessedService, shell, 
                    					startButton, startButton);
                }
                
                inputWindow.open();
            }
        });
        
        
        shell.open();
        
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }
    
    protected static void handleProcessedFormat(List<ImgData> imgDatas, TaskCompletionCallback callback) {
    	
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
                if (callback != null) {
                    callback.onTaskCompleted();
                }
            });
        });
    	
    }
    
    private static void initializeThreadPool() {
        executorService = Executors.newFixedThreadPool(AppConfig.getThreadPoolSize());  
    }
    
    private static boolean isValidPath(String path) {

    	String invalidChars = "[<>\"|?*]";

        if (path.matches(".*" + invalidChars + ".*")) {
            return false;
        }

        return true;
    }
    
}
