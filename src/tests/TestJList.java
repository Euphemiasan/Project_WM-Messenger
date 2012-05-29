package tests;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class TestJList {

	public static void main (String[] args)
	{
		JFrame fenetre = new JFrame();
		fenetre.setPreferredSize(new Dimension(200, 400));
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		fenetre.pack();
		
		String[] list = {"1","2","3","4","5","6","7","8","9","10"};
		JList jlist = new JList(list);
		
		JScrollPane scrollpane = new JScrollPane(jlist);
		fenetre.add(scrollpane);
		
		fenetre.setVisible(true);
	}
}
