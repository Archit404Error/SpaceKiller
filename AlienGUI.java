import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

//Created by Archit
public class AlienGUI {
	public JFrame frame;// creates the JFrame
	private Movement m;// Creates an Object of the class containing the thread
	public JLabel scoreL;

	public AlienGUI() {
		frame = new JFrame("Alien Killer!");
		scoreL = new JLabel("Score " + Movement.score);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("Ship.png")));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);
		JOptionPane.showMessageDialog(null,
				"Welcome to... Space Killer By Archit!\n(This game definitely isn't a ripped-off version of Galaga...)");
		JOptionPane.showMessageDialog(null,
				"Rules:\nYou see that red bar at the bottom? That's your health. Once the alien drops a bomb, you'll lose health, and if you run out, you die!(Not morbidly of course, just in a fun, 1980s fashion!)");
		JOptionPane.showMessageDialog(null,
				"Your score is displayed at the bottom of the screen, and a green bar depicting your progress will be shown at the top once you kill the bug.\nFinally, the bug gains a protective armor after half-way through the game. You must shoot it twice to kill it at this point in the game");
		JOptionPane.showMessageDialog(null,
				"You must press the arrow keys to move the ship, and press space or the up arrow key to shoot");

		m = new Movement(scoreL);
		frame.add(scoreL, BorderLayout.SOUTH);
		frame.setLocationRelativeTo(null);
		frame.add(m);// adds thread to frame
		frame.getContentPane().setBackground(Color.YELLOW);// turns bottom of screen yellow
		frame.setVisible(true);
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_RIGHT || arg0.getKeyCode() == KeyEvent.VK_D) {
					m.playerX += 10;
				}
				if (arg0.getKeyCode() == KeyEvent.VK_LEFT || arg0.getKeyCode() == KeyEvent.VK_A) {
					m.playerX -= 10;
				}
				if (arg0.getKeyCode() == KeyEvent.VK_SPACE || arg0.getKeyCode() == KeyEvent.VK_UP
						|| arg0.getKeyCode() == KeyEvent.VK_W) {
					m.cement = false;
					m.pressed(true);
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				if (arg0.getKeyCode() == KeyEvent.VK_RIGHT || arg0.getKeyCode() == KeyEvent.VK_D) {
					m.playerX += 1;
				}
				if (arg0.getKeyCode() == KeyEvent.VK_LEFT || arg0.getKeyCode() == KeyEvent.VK_A) {
					m.playerX -= 1;
				}
				if (arg0.getKeyCode() == KeyEvent.VK_SPACE || arg0.getKeyCode() == KeyEvent.VK_UP
						|| arg0.getKeyCode() == KeyEvent.VK_W) {
					Movement.cement = false;
					m.pressed(true);
				}
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				while (arg0.getKeyCode() == KeyEvent.VK_RIGHT || arg0.getKeyCode() == KeyEvent.VK_D) {
					m.playerX += 10;
				}
				while (arg0.getKeyCode() == KeyEvent.VK_LEFT || arg0.getKeyCode() == KeyEvent.VK_A) {
					m.playerX -= 10;
				}
				while (arg0.getKeyCode() == KeyEvent.VK_SPACE || arg0.getKeyCode() == KeyEvent.VK_UP
						|| arg0.getKeyCode() == KeyEvent.VK_W) {
					m.cement = false;
					m.pressed(true);
				}
			}
		});
	}

	public static void main(String[] args) {
		AlienGUI a = new AlienGUI();
	}
}

class Movement extends JComponent {// thread class
	Graphics2D graphic;
	BufferedImage arrow = null, alien1 = null, alien2 = null, heart = null, ship = null, bullet = null,
			background = null;// images for game
	static int playerY = 600;
	boolean pressed = false, changed = false, dropped = false;
	static boolean cement = true;
	static int playerX = 500, score = 0;
	int x = 0, rectX = 1, rectY = 0, rectMove = 1, progress = 0, health = 984, pLives = 4, aLives = 1, bulletX = 0,
			bulletY = 0;
	Rectangle r, playerR;

	JLabel scoreL = null;

	public Movement(JLabel scoreL) {
		try {
			this.scoreL = scoreL;
			arrow = ImageIO.read(getClass().getResourceAsStream("/Arrow.png"));
			alien1 = ImageIO.read(getClass().getResourceAsStream("/Alien.png"));
			alien2 = ImageIO.read(getClass().getResourceAsStream("/Alien2.png"));
			ship = ImageIO.read(getClass().getResourceAsStream("/Ship.png"));
			heart = ImageIO.read(getClass().getResourceAsStream("/Lives.png"));
			bullet = ImageIO.read(getClass().getResourceAsStream("/Test.png"));
			background = ImageIO.read(getClass().getResourceAsStream("/Background.jpg"));
		} catch (Exception e) {
		}
		Thread animationThread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					repaint();

					hit();
					if (dropped) {
						bulletY += 10;
					}
					if (pressed) {
						playerY -= 10;
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (isDead()) {
						JOptionPane.showMessageDialog(null, "You Lose");
						System.exit(0);
					}
					if (win()) {
						JOptionPane.showMessageDialog(null, "You Win");
						System.exit(0);
					}
				}
			}
		});

		animationThread.start();
	}

	public void pressed(boolean f) {
		pressed = f;// this helps to launch the player's bullet
	}

	public void alienAttack() {// how the alien attacks
		playerR = new Rectangle(playerX - 40, 600, 80, 40);
		if (!dropped) {
			if (rectMove > 0)
				bulletX = rectX + 33 + rectMove;
			else
				bulletX = rectX - 33 + rectMove;
			bulletY = rectY + 35;
		}
		Random r = new Random();
		int choice = r.nextInt(19) + 1;
		if (choice == r.nextInt(19) + 1) {
			dropped = true;
		}
		if (bulletY > getHeight()) {// checks if alien bullet missed
			dropped = false;
			bulletY = rectY + 35;
			if (rectMove > 0)
				bulletX = rectX + 33 + rectMove;
			else
				bulletX = rectX - 33 + rectMove;
		}
		if (playerR.contains(bulletX, bulletY)) {// checks if alien attack hit player
			health -= 0.25 * getWidth();
			dropped = false;
			bulletY = rectY;
			pLives -= 1;
			bulletX = rectX;
		}
	}

	public void hit() {// checks if player hit alien
		r = new Rectangle(rectX, rectY, 100, 100);
		if ((!changed) && progress > getWidth() / 2) {
			changed = true;
			aLives = 2;
		}
		if ((r.contains(playerX, playerY)) && rectY < 600) {
			// JOptionPane.showMessageDialog(null, "HIT");
			aLives -= 1;
			pressed = false;
			cement = true;
			if (aLives == 0) {
				rectX = 1;
				rectY = 0;
				if (rectMove < 0) {
					rectMove *= -1;
				}
				rectMove += 2;
				score += 1;
				scoreL.setText("Score: " + score);
				progress += .05 * getWidth();
				aLives = 1;
				changed = false;
			}
		}
		if (playerY < 0) {
			cement = true;
			pressed = false;
		}
	}

	public boolean win() {
		if (progress > getWidth()) {
			return true;
		}
		return false;
	}

	public boolean isDead() {
		if (health <= 0 || rectY > 600) {
			return true;
		}
		return false;
	}

	public void paintComponent(Graphics g) {
		alienAttack();

		graphic = (Graphics2D) g;
		graphic.setColor(Color.GREEN);
		graphic.drawImage(background, 0, 0, getWidth(), getHeight(), null);
		if (dropped) {
			graphic.drawImage(bullet, bulletX, bulletY, 40, 80, null);
		}
		graphic.fillRect(0, 0, progress, 10);
		for (int i = 0; i < pLives; i++) {
			graphic.drawImage(heart, 0 + i * 40, 30, 40, 40, null);
		}
		graphic.setColor(Color.RED);
		graphic.fillRect(0, 735, health, 10);
		graphic.setColor(Color.BLUE);
		rectX += rectMove;
		if (rectX >= getWidth() - 100) {
			rectMove *= -1;
			rectY += 100;
		}
		if (rectX <= 0) {
			rectMove *= -1;
			rectY += 100;
		}
		if (pressed == true) {
			graphic.drawImage(arrow, playerX, playerY, 40, 80, null);
		}
		graphic.drawImage(ship, playerX, 600, 40, 80, null);
		if (aLives == 1) {
			graphic.drawImage(alien1, rectX, rectY, 100, 100, null);
		} else {
			graphic.drawImage(alien2, rectX, rectY, 100, 100, null);
		}
		if (cement) {
			playerY = 600;
		}
	}

}