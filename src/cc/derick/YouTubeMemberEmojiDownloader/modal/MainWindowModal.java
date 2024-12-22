package cc.derick.YouTubeMemberEmojiDownloader.modal;

import java.awt.Button;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cc.derick.YouTubeMemberEmojiDownloader.service.DataProcessedService;

public class MainWindowModal {
	
	private Shell parentShell;
	
	private Text outPutField;
	
    private Button startButton;
    
    private Button stopButton;
    
    private DataProcessedService dataProcessedService;
    
    

	public MainWindowModal(Shell parentShell, Text outPutField, Button startButton, Button stopButton,
			DataProcessedService dataProcessedService) {
		super();
		this.parentShell = parentShell;
		this.outPutField = outPutField;
		this.startButton = startButton;
		this.stopButton = stopButton;
		this.dataProcessedService = dataProcessedService;
	}

	public Shell getParentShell() {
		return parentShell;
	}

	public Text getOutPutField() {
		return outPutField;
	}

	public Button getStartButton() {
		return startButton;
	}

	public Button getStopButton() {
		return stopButton;
	}

	public DataProcessedService getDataProcessedService() {
		return dataProcessedService;
	}

}
