import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class AgarioGui extends JPanel {
	
	final static int width = 1800;
	final static int height = 1000;
	
	public AgarioGui() {
		JFrame frame = new JFrame("Agar.io");
		frame.setSize(width, height);
		frame.setBackground(Color.white);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setColor(Color.red);
		g2d.fillOval(width / 2 - 150, height /2 - 150, 300, 300);
	}
}
