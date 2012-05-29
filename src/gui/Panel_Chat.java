package gui;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Panel_Chat extends JTabbedPane
{
	private Project_WMMessenger program;
	
	public Panel_Chat (Project_WMMessenger pwmm)
	{
		super(JTabbedPane.TOP);
		
		program = pwmm;
		
		initComponent();
	}
	
	public void initComponent()
	{
		addTab("Broadcast", null, new JPanel(), null);
	}

	public void refreshSize (int width, int height)
	{
		setPreferredSize(new Dimension(width, height));
		revalidate();
	}
}
