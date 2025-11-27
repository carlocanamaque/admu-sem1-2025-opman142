import com.fazecast.jSerialComm.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Processor extends JPanel implements Runnable, KeyListener {

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

    // Neutral gyro values (will be initialized on first reading)
    Integer neutralX = null;
    Integer neutralY = null;
    
    public Processor() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
	this.setFocusable(true);
	this.addKeyListener(this);
    }

    public void startThread() {
        thread = new Thread(this);
        thread.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        cube.draw(g2);
        g2.dispose();
    }

	private void changeBackgroundColor() {

	}

    @Override
    public void run() {
        double drawInterval = (1000000000 / FRAME_RATE);
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (thread != null) {
            currentTime = System.nanoTime();
            delta += ((currentTime - lastTime) / drawInterval);
            lastTime = currentTime;

            if (delta >= 1) {
                repaint();
                delta--;
            }
        }
    }

	@Override 
	public void keyTyped(KeyEvent e) {
		char key = e.getKeyChar();
		switch (key) {
        		case '1': cube.color = Color.RED; break;
        		case '2': cube.color = Color.GREEN; break;
        		case '3': cube.color = Color.BLUE; break;
	        	case '4': cube.color = Color.YELLOW; break;
        		case '5': cube.color = Color.CYAN; break;
	        	case '6': cube.color = Color.MAGENTA; break;
	        	case '7': cube.color = Color.ORANGE; break;
		        case '8': cube.color = Color.PINK; break;
        		case '9': cube.color = Color.LIGHT_GRAY; break;
        		default: return; // ignore other keys
    		}
    	System.out.println("Key " + key + " pressed, cube color changed to " + cube.color);
	}

	@Override
	public void keyPressed(KeyEvent e) { }

	@Override
	public void keyReleased(KeyEvent e) { }


    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Smart Toys Software");

        Processor processor = new Processor();
        window.add(processor);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        processor.startThread();

        // Start a separate thread for reading gyro data
        new Thread(() -> {
            GyroSerial gyro = new GyroSerial(null); // auto-select port

            if (gyro.serialPort == null || !gyro.serialPort.isOpen()) {
                System.out.println("Exiting: no port open.");
                return;
            }

            System.out.println("Reading gyro data... Press Ctrl+C to stop.");

            while (true) {
                String line = gyro.readLine();
		System.out.println(line);
                if (line != null) {
                    try {
                        String[] parts = line.split(",");
                        int rawX = (int) Double.parseDouble(parts[0]);
                        int rawY = (int) Double.parseDouble(parts[1]);

                        // Initialize neutral position on first valid reading
                        if (processor.neutralX == null || processor.neutralY == null) {
                            processor.neutralX = rawX;
                            processor.neutralY = rawY;
                        }

                        // Compute delta from neutral
                        int deltaX = rawX - processor.neutralX;
                        int deltaY = rawY - processor.neutralY;

                        // Map to screen center
                        processor.cube.x = ((processor.SCREEN_WIDTH / 2) - processor.TILE_SIZE) + deltaX;
                        processor.cube.y = ((processor.SCREEN_HEIGHT / 2) - processor.TILE_SIZE) + deltaY;

                    } catch (Exception e) {
                        System.out.println("Invalid line from gyro: " + line);
                    }
                }
            }
        }).start();
    }
}

