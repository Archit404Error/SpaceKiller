import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class AlienGUI {
	private static JFrame frame;
	Movement m = new Movement();
	public static JLabel scoreL;

	public void constrict() {
		frame.setSize(frame.getWidth() - 25, frame.getHeight());
	}

	public static void main(String[] args) {
		frame = new JFrame("Alien Killer!");
		scoreL = new JLabel("Score " + Movement.score);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);
		JOptionPane.showMessageDialog(null, "Welcome to... Space Killer By Archit!\n(This game definitely isn't a ripped-off version of Galaga...)");
		JOptionPane.showMessageDialog(null, "Rules:\nYou see that red bar at the bottom? That's your health. Once the alien drops a bomb, you'll lose health, and if you run out, you die!(Not morbidly of course, just in a fun, 1980s fashion!)");
		JOptionPane.showMessageDialog(null, "Your score is displayed at the bottom of the screen, and a green bar depicting your progress will be shown at the top once you kill the bug.\nFinally, the bug gains a protective armor after half-way through the game. You must shoot it twice to kill it at this point in the game");
		JOptionPane.showMessageDialog(null, "You must press the arrow keys to move the ship, and press space or the up arrow key to shoot");
		frame.add(scoreL, BorderLayout.SOUTH);
		frame.setLocationRelativeTo(null);
		Movement m = new Movement();
		frame.add(m);
		frame.getContentPane().setBackground(Color.YELLOW);
		frame.setVisible(true);
	frame.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_RIGHT || arg0.getKeyCode() == KeyEvent.VK_D) {
					Movement.playerX += 10;
				}
				if (arg0.getKeyCode() == KeyEvent.VK_LEFT || arg0.getKeyCode() == KeyEvent.VK_A) {
					Movement.playerX -= 10;
				}
				if (arg0.getKeyCode() == KeyEvent.VK_SPACE || arg0.getKeyCode() == KeyEvent.VK_UP || arg0.getKeyCode() == KeyEvent.VK_W) {
					Movement.cement = false;
					m.pressed(true);
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}
}

class Movement extends JComponent {
	Graphics2D graphic;
	BufferedImage arrow = null, alien1 = null, alien2 = null, ship = null, bullet = null, background = null;
	static int playerY = 600;
	boolean pressed = false, changed = false, dropped = false;
	static boolean cement = true;
	static int playerX = 500, score = 0;
	int x = 0, rectX = 1, rectY = 0, rectMove = 1, progress = 0, health = 984, aLives = 1, bulletX = 0, bulletY = 0;
	Rectangle r;

	public Movement() {
		try {
			arrow = ImageIO.read(getClass().getResourceAsStream("/Arrow.png"));
			alien1 = ImageIO.read(getClass().getResourceAsStream("/Alien.png"));
			alien2 = ImageIO.read(getClass().getResourceAsStream("/Alien2.png"));
			ship = ImageIO.read(getClass().getResourceAsStream("/Ship.png"));
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
				}
			}
		});

		animationThread.start();
	}

	public void pressed(boolean f) {
		pressed = f;
	}

	public void alienAttack() {
		if (!dropped) {
			bulletX = rectX;
			bulletY = rectY;
		}
		Random r = new Random();
		int choice = r.nextInt(79) + 1;
		if (choice == 4) {
			dropped = true;
		}
		if (bulletY > getHeight()) {
			dropped = false;
			bulletY = rectY;
			bulletX = rectX;
			health -= 0.05 * getWidth();
		}
	}

	public void hit() {
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
				rectMove += 1;
				score += 1;
				AlienGUI.scoreL.setText("Score: " + score);
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

	public void isDead() {
		if (progress >= getWidth() || health <= 0 || rectY > 600) {
			System.exit(0);
		}
	}
	
	public void paintComponent(Graphics g) {
		alienAttack();
		isDead();
		graphic = (Graphics2D) g;
		graphic.setColor(Color.GREEN);
		graphic.drawImage(background, 0, 0, getWidth(), getHeight(), null);
		if (dropped) {
			graphic.drawImage(bullet, bulletX, bulletY, 40, 80, null);
		}
		graphic.fillRect(0, 0, progress, 10);
		
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
