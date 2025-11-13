import java.awt.*;

public class Cube {

	Processor processor;

	public int x;
	public int y;

	public Cube(Processor processor) {

		this.processor = processor;

		setDefaultValues();
	}

	public void setDefaultValues() {

		x = ((processor.SCREEN_WIDTH / 2) - processor.TILE_SIZE);
		y = ((processor.SCREEN_HEIGHT / 2) - processor.TILE_SIZE);
	}

	public void update() {

	}

	public void draw(Graphics2D g2) {

		g2.setColor(Color.white);
		g2.fillRect(x, y, processor.TILE_SIZE, processor.TILE_SIZE);
	}
}
