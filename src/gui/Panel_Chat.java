package gui;

import java.awt.Dimension;

import javax.swing.JTabbedPane;

public class Panel_Chat extends JTabbedPane
{
	private Project_WMMessenger program;
	
	private Panel_Broadcast panel_broadcast;
	
	public Panel_Chat (Project_WMMessenger pwmm)
	{
		super(JTabbedPane.TOP);
		
		program = pwmm;
		
		initComponent();
	}
	
	public void initComponent()
	{
		panel_broadcast = new Panel_Broadcast();
		addTab("Broadcast", null, panel_broadcast, null);
	}

	public void refreshSize (int width, int height)
	{
		setPreferredSize(new Dimension(width, height));
		revalidate();
	}
}
