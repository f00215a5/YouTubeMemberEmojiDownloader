package cc.derick.YouTubeMemberEmojiDownloader.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

public class Console {
	
	private static StyledText console;
	
	public static void init(StyledText styledText) {
        console = styledText;
    }
	 
	// 輸出紅色
    public static void err(String message) {
        if (console != null) {
            setConsoleMessage(message, SWT.COLOR_RED);
        }
    }

    // 輸出黑色
    public static void out(String message) {
        if (console != null) {
            setConsoleMessage(message, SWT.COLOR_BLACK);
        }
    }
    
    private static void setConsoleMessage(String message, int color) {
    	// 判斷是否在UI線呈
        if (Display.getCurrent() == null) {
            Display.getDefault().asyncExec(() -> updateConsole(message, color));
        } else {
            
            updateConsole(message, color);
        }
    }
    
    private static void updateConsole(String message, int color) {
        Display display = Display.getCurrent();
        if (display != null && console != null) {
            console.setForeground(display.getSystemColor(color));
            console.append(message);
            console.setCaretOffset(console.getText().length());
            console.setTopIndex(console.getLineCount() - 1);
        }
    }

}
