import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class AgarioGui extends JPanel {
	
	final static int width = 1800;
	final static int height = 1000;
	
	public AgarioData data = null;
	
	public AgarioGui() {
		JFrame frame = new JFrame("Agar.io");
		frame.setSize(width, height);
		frame.setBackground(Color.white);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);
	}
	
	public void update(AgarioData data) {
		this.data = data;
		this.repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (data == null)
			return;
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.clearRect(0, 0, getWidth(), getHeight());
		
		for (AgarioData.Cell cell : data.getCells()) {
			g2d.setColor(cell.color);
			g2d.fillOval(width/2 + cell.x/4, height/2 + cell.y/4, cell.size, cell.size);
		}
		data.clearCells();
	}
}
