import java.awt.*;
import javax.swing.*;

public class Processor extends JPanel implements Runnable {

	final int ORIG_TILE_SIZE = 16;
	final int SCALE = 3;
	final int FRAME_RATE = 60;

	final int TILE_SIZE = (ORIG_TILE_SIZE * SCALE);
	final int MAX_SCREEN_COL = 16;
	final int MAX_SCREEN_ROW = 12;
	final int SCREEN_WIDTH = (TILE_SIZE * MAX_SCREEN_COL);
	final int SCREEN_HEIGHT = (TILE_SIZE * MAX_SCREEN_ROW);

	Thread thread;
	Cube cube = new Cube(this);

	public Processor() {

		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
	}

	public void startThread() {
		thread = new Thread(this);
		thread.start();
	}

	public void update() {

	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;

		cube.draw(g2);

		g2.dispose();
	}

	@Override
	public void run() {

		double drawInterval = (1000000000 / FRAME_RATE);
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		long timer = 0;

		while(thread != null) {

			currentTime = System.nanoTime();

			delta += ((currentTime - lastTime) / drawInterval);
			timer += (currentTime - lastTime);
			lastTime = currentTime;

			if(delta >= 1) {

				update();
				repaint();
				delta--;
			}
		}
	}
}
