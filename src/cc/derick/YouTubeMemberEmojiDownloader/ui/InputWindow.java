package cc.derick.YouTubeMemberEmojiDownloader.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cc.derick.YouTubeMemberEmojiDownloader.TaskCompletionCallback;
import cc.derick.YouTubeMemberEmojiDownloader.modal.ImgData;
import cc.derick.YouTubeMemberEmojiDownloader.service.DataProcessedService;

public class InputWindow {
	
	private Text outPutField;
    private DataProcessedService dataProcessedService;
    private Shell parentShell;
    private Button startButton;
    private Button stopButton;
    
    
	public InputWindow(Text outPutField, DataProcessedService dataProcessedService, Shell parentShell,
			Button startButton, Button stopButton) {
		super();
		this.outPutField = outPutField;
		this.dataProcessedService = dataProcessedService;
		this.parentShell = parentShell;
		this.startButton = startButton;
		this.stopButton = stopButton;
	}


	public void open() {
		
		Shell inputShell = new Shell(parentShell, SWT.TITLE | SWT.CLOSE | SWT.APPLICATION_MODAL);
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
            	String outputPathString = outPutField.getText();
                String text = textArea.getText();
                
                if(outputPathString.isEmpty() || text.isEmpty()) {
                	inputShell.close();
                	Console.err("請選擇輸出路徑和輸入Html節點\n");
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
                
                inputShell.close();
                Console.out("開始下載...\n");
                List<ImgData> imgDatas = dataProcessedService.inputParser(text, outputPath);
                
                DownloaderUI.handleProcessedFormat(imgDatas, new TaskCompletionCallback() {
                    @Override
                    public void onTaskCompleted() {
                        Display.getDefault().asyncExec(() -> {
                            startButton.setEnabled(true);
                            stopButton.setEnabled(false);
                        });
                    }
                });
                
            }
        });
        
        inputShell.open();
        while (!inputShell.isDisposed()) {
            if (!inputShell.getDisplay().readAndDispatch()) {
                inputShell.getDisplay().sleep();
            }
        }
        
	}
	
    private static boolean isValidPath(String path) {

        String invalidChars = "[<>\"|?*]";
        
        if (path.matches(".*" + invalidChars + ".*")) {
            return false;
        }

        return true;
    }

}
