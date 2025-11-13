import javax.swing.*;

public class Window {

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
	}
}
