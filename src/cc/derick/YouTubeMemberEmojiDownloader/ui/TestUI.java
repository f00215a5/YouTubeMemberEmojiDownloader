package cc.derick.YouTubeMemberEmojiDownloader.ui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TestUI {
	
	public static void main(String[] args) {
		
		JFrame f = new JFrame("Text Area Examples");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTextArea content = new JTextArea(100,10) ;
		JScrollPane g = new JScrollPane(content);
		f.setBounds(100,100,300,300);
		f.setVisible(true);
		f.setLayout (null); //nul表示自訂樣式，不使用預設的樣式         
		content.setBounds(10,10,100,100);
//		g.setBounds(110,10,200,100);
		f.add(g);
		
	}
}
