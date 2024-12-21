package cc.derick.YouTubeMemberEmojiDownloader.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
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
import cc.derick.YouTubeMemberEmojiDownloader.modal.ImgData;
import cc.derick.YouTubeMemberEmojiDownloader.service.DataProcessedService;
import cc.derick.YouTubeMemberEmojiDownloader.service.DownloadService;

public class DownloaderUI {
	
	private static DownloadService downloadService = new DownloadService();
	private static DataProcessedService dataProcessedService = new DataProcessedService();
	
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
        Text console = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
        console.setEditable(false);
        console.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));  // span 4 columns
        
        // 下方按鈕
        Composite bottomPanel = new Composite(shell, SWT.NONE);
        bottomPanel.setLayout(new GridLayout(2, false));
        
        Button startButton = new Button(bottomPanel, SWT.PUSH);
        startButton.setText("開始下載");
        
        Button stopButton = new Button(bottomPanel, SWT.PUSH);
        stopButton.setText("停止");
        
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
            String outputPath = outPutField.getText();
            int selectedIndex = formatCombo.getSelectionIndex();
            
            if (selectedFile.isEmpty() || outputPath.isEmpty()) {
                console.append("請選擇文件和輸出資料夾\n");
                return;
            }
            
            console.append("開始下載...\n");
            
            List<ImgData> imgDatas = new ArrayList<>();
            // 邏輯預留區
            if(selectedIndex == 0) {
            	imgDatas = dataProcessedService.htmlParser(selectedFile, console);
            } else if (selectedIndex == 2) {				
            	imgDatas = dataProcessedService.txtParser(selectedFile, console);
			}
            
            handleProcessedFormat(imgDatas, console, outputPath);
            
        });

        stopButton.addListener(SWT.Selection, e -> {
            console.append("停止下載...\n");
            // 邏輯預留區
            isStop.set(true);
        });
        
        formatCombo.addSelectionListener(new SelectionAdapter() {
        	
        	@Override
            public void widgetSelected(SelectionEvent e) {
                if (formatCombo.getSelectionIndex() == 1) {
                    textAreaButton.setEnabled(true);
                } else {
                    textAreaButton.setEnabled(false);
                }
            }
        	
		});
        
        
        //直接輸入
        textAreaButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	
                Shell inputShell = new Shell(shell, SWT.TITLE | SWT.CLOSE | SWT.APPLICATION_MODAL);
                inputShell.setText("輸入");
                inputShell.setSize(650, 700);
                inputShell.setLayout(new GridLayout(1, false));

                Text textArea = new Text(inputShell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
                textArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
                Composite buttonComposite = new Composite(inputShell, SWT.NONE);
                buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false)); // This stays as GridData for the composite
                RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);  
                rowLayout.justify = true;
                buttonComposite.setLayout(rowLayout);

                Button submitButton = new Button(buttonComposite, SWT.PUSH);
                submitButton.setText("送出");

                RowData rowData = new RowData();
                rowData.width = SWT.DEFAULT; 
                submitButton.setLayoutData(rowData);

                submitButton.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                    	String outputPath = outPutField.getText();
                        String text = textArea.getText();
                        
                        if(outputPath.isEmpty() || text.isEmpty()) {
                        	inputShell.close();
                        	console.append("請選擇輸出路徑和輸入Html節點\n");
                        	return;
                        }
                        
                        inputShell.close();
                        console.append("開始下載...\n");
                        List<ImgData> imgDatas = dataProcessedService.inputParser(text, console);
                        handleProcessedFormat(imgDatas, console, outputPath);
                    }
                });

                inputShell.open();
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
    
    private static void handleProcessedFormat(List<ImgData> imgDatas, Text console, String outputPath) {
    	
    	long totalLines = imgDatas.size();
    	long count = imgDatas.stream()
	    			.map(imgData -> {
	    				if(isStop.get()) {
	    					return 0;
	    				}		
	    				boolean success = downloadService.downloadAndSaveImage(imgData, console, outputPath);
	    				return success ? 1 : 0;
	    			})
	    			.reduce(0, Integer::sum);
    	
    	console.append(String.format("下載成功: %d, 下載失敗; %d%n", count, totalLines - count));
    	
    }
}
