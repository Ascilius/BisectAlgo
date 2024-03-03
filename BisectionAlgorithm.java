import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class BisectionAlgorithm {

	public static void main(String[] arg0) {
		JFrame frame = new JFrame("Bisection Algorithm");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		BisectionAlgorithmPanel panel = new BisectionAlgorithmPanel(screenSize.getWidth(), screenSize.getHeight());
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}
}

// -----------------------------------------------------------------------------------------------------------------------------
// panel

class BisectionAlgorithmPanel extends JPanel {

	private double screenWidth, screenHeight, scale = 128, wide = 1, range = 1000.0, precision = 0.1;
	private boolean graph, custom, degree;
	private String degreeString;
	private int current = 0;

	private ArrayList<String> coefficientStrings = new ArrayList<String>();
	private ArrayList<Integer> coefficients;
	private BisectionPolynomial polynomial;

	private KeyHandler keyHandler;
	private MouseHandler mouseHandler;

	public BisectionAlgorithmPanel(double screenWidth, double screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.graph = true;
		this.custom = false;
		this.degree = false;
		this.keyHandler = new KeyHandler();

		generatePolynomial();

		addKeyListener(this.keyHandler);
		addMouseListener(this.mouseHandler);
		setFocusable(true);
	}

	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

		g.translate(screenWidth / 2, screenHeight / 2);

		// graph
		if (graph == true) {
			g.setColor(Color.GRAY);
			for (int i = (int) range / -2; i < range / 2; i++) {
				g.drawLine((int) (i * scale * wide), (int) (screenHeight / -2), (int) (i * scale * wide), (int) (screenHeight / 2));
			}
			for (int i = (int) range / -2; i < range / 2; i++) {
				g.drawLine((int) (screenWidth / -2), (int) (i * scale), (int) (screenWidth / 2), (int) (i * scale));
			}
		}
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2));
		g.drawLine((int) (screenWidth / -2), 0, (int) (screenWidth / 2), 0);
		g.drawLine(0, (int) (screenHeight / -2), 0, (int) (screenHeight / 2));

		// function
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1));
		for (int i = 0; i < polynomial.getGraph().size() - 1; i++) {
			g.drawLine((int) (polynomial.getGraph().get(i).getX() * scale * wide), (int) (polynomial.getGraph().get(i).getY() * -1 * scale), (int) (polynomial.getGraph().get(i + 1).getX() * scale * wide), (int) (polynomial.getGraph().get(i + 1).getY() * -1 * scale));
		}

		// solutions
		g.setColor(Color.RED);
		for (int i = 0; i < polynomial.getSolutions().size(); i++) {
			g.fillOval((int) (polynomial.getSolutions().get(i) * scale * wide) - 4, -4, 8, 8);
		}

		g.translate(screenWidth / -2, screenHeight / -2);

// -----------------------------------------------------------------------------------------------------------------------------		
// information

		// top
		int start = 10;
		g.setColor(Color.BLACK);
		if (custom == true) {
			g.setColor(Color.BLACK);
			if (current == 0 && degree == false) {
				g.setColor(Color.RED);
			}
			if (polynomial.getCoefficients().get(0) < 0) {
				g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
				g.drawString(polynomial.getCoefficients().get(0) + "x", 10, 30);
				g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
				g.drawString(Integer.toString(polynomial.getCoefficients().size() - 1), 40, 20);
				start = 50;
			} else {
				g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
				g.drawString(polynomial.getCoefficients().get(0) + "x", 10, 30);
				g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
				g.drawString(Integer.toString(polynomial.getCoefficients().size() - 1), 30, 20);
				start = 40;
			}
			for (int i = 1; i < polynomial.getCoefficients().size() - 1; i++) {
				g.setColor(Color.BLACK);
				if (current == i && degree == false) {
					g.setColor(Color.RED);
				}
				if (polynomial.getCoefficients().get(i) < 0) {
					g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
					g.drawString(polynomial.getCoefficients().get(i) + "x", start, 30);
					if (polynomial.getCoefficients().size() - 1 - i != 1) {
						g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
						g.drawString(Integer.toString(polynomial.getCoefficients().size() - 1 - i), start + 30, 20);
						start += 40;
					} else {
						start += 30;
					}
				} else {
					g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
					if (start != 10) {
						g.drawString("+" + polynomial.getCoefficients().get(i) + "x", start, 30);
					} else {
						g.drawString(polynomial.getCoefficients().get(i) + "x", start, 30);
					}
					if (polynomial.getCoefficients().size() - 1 - i != 1) {
						g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
						g.drawString(Integer.toString(polynomial.getCoefficients().size() - 1 - i), start + 35, 20);
						start += 40;
					} else {
						start += 30;
					}
				}
			}
			g.setColor(Color.BLACK);
			if (current == polynomial.getCoefficients().size() - 1 && degree == false) {
				g.setColor(Color.RED);
			}
			if (polynomial.getCoefficients().get(polynomial.getCoefficients().size() - 1) < 0) {
				g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
				g.drawString(Integer.toString(polynomial.getCoefficients().get(polynomial.getCoefficients().size() - 1)), start, 30);
			} else {
				g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
				g.drawString("+" + polynomial.getCoefficients().get(polynomial.getCoefficients().size() - 1), start, 30);
			}
		} else if (custom == false) {
			if (polynomial.getCoefficients().get(0) == 1) {
				g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
				g.drawString("x", 10, 30);
				g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
				g.drawString(Integer.toString(polynomial.getCoefficients().size() - 1), 20, 20);
				start = 30;
			} else if (polynomial.getCoefficients().get(0) < 0) {
				g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
				g.drawString(polynomial.getCoefficients().get(0) + "x", 10, 30);
				g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
				g.drawString(Integer.toString(polynomial.getCoefficients().size() - 1), 40, 20);
				start = 50;
			} else if (polynomial.getCoefficients().get(0) != 0) {
				g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
				g.drawString(polynomial.getCoefficients().get(0) + "x", 10, 30);
				g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
				g.drawString(Integer.toString(polynomial.getCoefficients().size() - 1), 30, 20);
				start = 40;
			}
			for (int i = 1; i < polynomial.getCoefficients().size() - 1; i++) {
				if (polynomial.getCoefficients().get(i) == 1) {
					g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
					if (start != 10) {
						g.drawString("+x", start, 30);
					} else {
						g.drawString("x", start, 30);
					}
					if (polynomial.getCoefficients().size() - 1 - i != 1) {
						g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
						g.drawString(Integer.toString(polynomial.getCoefficients().size() - 1 - i), start + 20, 20);
						start += 30;
					} else {
						start += 20;
					}
				} else if (polynomial.getCoefficients().get(i) < 0) {
					g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
					g.drawString(polynomial.getCoefficients().get(i) + "x", start, 30);
					if (polynomial.getCoefficients().size() - 1 - i != 1) {
						g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
						g.drawString(Integer.toString(polynomial.getCoefficients().size() - 1 - i), start + 30, 20);
						start += 40;
					} else {
						start += 30;
					}
				} else if (polynomial.getCoefficients().get(i) != 0) {
					g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
					if (start != 10) {
						g.drawString("+" + polynomial.getCoefficients().get(i) + "x", start, 30);
					} else {
						g.drawString(polynomial.getCoefficients().get(i) + "x", start, 30);
					}
					if (polynomial.getCoefficients().size() - 1 - i != 1) {
						g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
						g.drawString(Integer.toString(polynomial.getCoefficients().size() - 1 - i), start + 35, 20);
						start += 40;
					} else {
						start += 30;
					}
				}
			}
			if (polynomial.getCoefficients().get(polynomial.getCoefficients().size() - 1) < 0) {
				g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
				g.drawString(Integer.toString(polynomial.getCoefficients().get(polynomial.getCoefficients().size() - 1)), start, 30);
			} else if (polynomial.getCoefficients().get(polynomial.getCoefficients().size() - 1) != 0) {
				g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
				g.drawString("+" + polynomial.getCoefficients().get(polynomial.getCoefficients().size() - 1), start, 30);
			}
		}
		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		g.drawString("Solutions:", 10, 50);
		if (polynomial.getSolutions().size() > 0) {
			for (int i = 0; i < polynomial.getSolutions().size(); i++) {
				g.drawString(Double.toString(polynomial.getSolutions().get(i)), 10, 20 * i + 70);
				start = 20 * (i + 2) + 70;
			}
		} else {
			g.drawString("None", 10, 70);
			start = 110;
		}
		if (custom == false) {
			g.drawString("C - Create custom polynomial", 10, start);
		} else if (custom == true) {
			g.drawString("C - Generate random polynomial", 10, start);
			g.drawString("D - Input degree of the polynomial", 10, start + 20);
			g.drawString("Right - Move to next coefficient", 10, start + 40);
			g.drawString("Left - Move to previous coefficient", 10, start + 60);
			if (degree == true) {
				g.setColor(Color.RED);
			}
			g.drawString("Degree: " + degreeString, 10, start + 100);
		}

		// bottom
		g.setColor(Color.BLACK);
		if (graph == true) {
			g.drawString("Gridlines: On", 10, (int) (screenHeight - 350));
		} else if (graph == false) {
			g.drawString("Gridlines: Off", 10, (int) (screenHeight - 350));
		}
		g.drawString("Range: " + range, 10, (int) (screenHeight - 330));
		g.drawString("Stretch Factor: " + wide, 10, (int) (screenHeight - 310));
		g.drawString("Precision: " + precision, 10, (int) (screenHeight - 290));
		g.drawString("G - Toggles gridlines", 10, (int) (screenHeight - 250));
		g.drawString("I - Increases range", 10, (int) (screenHeight - 230));
		g.drawString("K - Decreases range", 10, (int) (screenHeight - 210));
		g.drawString("J - Compresses graph", 10, (int) (screenHeight - 190));
		g.drawString("L - Stretches graph", 10, (int) (screenHeight - 170));
		g.drawString(". - Increases precision", 10, (int) (screenHeight - 130));
		g.drawString(", - Decreases precision", 10, (int) (screenHeight - 110));
		g.drawString("Space - Generate new polynomial", 10, (int) (screenHeight - 70));
		g.drawString("Shift - Zoom in", 10, (int) (screenHeight - 50));
		g.drawString("Control - Zoom out", 10, (int) (screenHeight - 30));
	}

	// -----------------------------------------------------------------------------------------------------------------------------
	// polynomial compile
	public void compilePolynomial() {
		coefficients.clear();
		for (int i = 0; i < coefficientStrings.size(); i++) {
			coefficients.add(Integer.parseInt(coefficientStrings.get(i)));
		}
		polynomial = new BisectionPolynomial(coefficients, range, precision);
	}

	// -----------------------------------------------------------------------------------------------------------------------------
	// polynomial generation

	public void generatePolynomial() {
		coefficients = new ArrayList<Integer>();
		int terms = (int) (Math.random() * 8) + 3;
		coefficients.add((int) (Math.random() * 9) + 1);
		if (Math.random() < 0.5) {
			coefficients.add(coefficients.remove(0) * -1);
		}
		for (int i = 1; i < terms; i++) {
			coefficients.add((int) (Math.random() * 10));
			if (Math.random() < 0.5) {
				coefficients.add(coefficients.remove(i) * -1);
			}
		}
		if (coefficients.size() == 3) {
			polynomial = new BisectionQuadratic(coefficients, range, precision);
		} else {
			polynomial = new BisectionPolynomial(coefficients, range, precision);
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------
	// key input

	class KeyHandler extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (degree == true && (e.getKeyCode() == KeyEvent.VK_1 || e.getKeyCode() == KeyEvent.VK_2 || e.getKeyCode() == KeyEvent.VK_3 || e.getKeyCode() == KeyEvent.VK_4 || e.getKeyCode() == KeyEvent.VK_5 || e.getKeyCode() == KeyEvent.VK_6 || e.getKeyCode() == KeyEvent.VK_7 || e.getKeyCode() == KeyEvent.VK_8 || e.getKeyCode() == KeyEvent.VK_9 || e.getKeyCode() == KeyEvent.VK_0)) {
				if (e.getKeyCode() == KeyEvent.VK_1) {
					if (degreeString.equals("0") == true) {
						degreeString = "1";
					} else {
						degreeString += "1";
					}
				} else if (e.getKeyCode() == KeyEvent.VK_2) {
					if (degreeString.equals("0") == true) {
						degreeString = "2";
					} else {
						degreeString += "2";
					}
				} else if (e.getKeyCode() == KeyEvent.VK_3) {
					if (degreeString.equals("0") == true) {
						degreeString = "3";
					} else {
						degreeString += "3";
					}
				} else if (e.getKeyCode() == KeyEvent.VK_4) {
					if (degreeString.equals("0") == true) {
						degreeString = "4";
					} else {
						degreeString += "4";
					}
				} else if (e.getKeyCode() == KeyEvent.VK_5) {
					if (degreeString.equals("0") == true) {
						degreeString = "5";
					} else {
						degreeString += "5";
					}
				} else if (e.getKeyCode() == KeyEvent.VK_6) {
					if (degreeString.equals("0") == true) {
						degreeString = "6";
					} else {
						degreeString += "6";
					}
				} else if (e.getKeyCode() == KeyEvent.VK_7) {
					if (degreeString.equals("0") == true) {
						degreeString = "7";
					} else {
						degreeString += "7";
					}
				} else if (e.getKeyCode() == KeyEvent.VK_8) {
					if (degreeString.equals("0") == true) {
						degreeString = "8";
					} else {
						degreeString += "8";
					}
				} else if (e.getKeyCode() == KeyEvent.VK_9) {
					if (degreeString.equals("0") == true) {
						degreeString = "9";
					} else {
						degreeString += "9";
					}
				} else if (e.getKeyCode() == KeyEvent.VK_0) {
					if (degreeString.equals("0") != true) {
						degreeString += "0";
					}
				}
				while (coefficientStrings.size() < Integer.parseInt(degreeString) + 1) {
					coefficientStrings.add(0, "0");
				}
				compilePolynomial();
				repaint();
			} else if (degree == false && (e.getKeyCode() == KeyEvent.VK_1 || e.getKeyCode() == KeyEvent.VK_2 || e.getKeyCode() == KeyEvent.VK_3 || e.getKeyCode() == KeyEvent.VK_4 || e.getKeyCode() == KeyEvent.VK_5 || e.getKeyCode() == KeyEvent.VK_6 || e.getKeyCode() == KeyEvent.VK_7 || e.getKeyCode() == KeyEvent.VK_8 || e.getKeyCode() == KeyEvent.VK_9 || e.getKeyCode() == KeyEvent.VK_0 || e.getKeyCode() == KeyEvent.VK_MINUS)) {
				if (e.getKeyCode() == KeyEvent.VK_1) {
					coefficientStrings.add(current, coefficientStrings.remove(current) + "1");
				} else if (e.getKeyCode() == KeyEvent.VK_2) {
					coefficientStrings.add(current, coefficientStrings.remove(current) + "2");
				} else if (e.getKeyCode() == KeyEvent.VK_3) {
					coefficientStrings.add(current, coefficientStrings.remove(current) + "3");
				} else if (e.getKeyCode() == KeyEvent.VK_4) {
					coefficientStrings.add(current, coefficientStrings.remove(current) + "4");
				} else if (e.getKeyCode() == KeyEvent.VK_5) {
					coefficientStrings.add(current, coefficientStrings.remove(current) + "5");
				} else if (e.getKeyCode() == KeyEvent.VK_6) {
					coefficientStrings.add(current, coefficientStrings.remove(current) + "6");
				} else if (e.getKeyCode() == KeyEvent.VK_7) {
					coefficientStrings.add(current, coefficientStrings.remove(current) + "7");
				} else if (e.getKeyCode() == KeyEvent.VK_8) {
					coefficientStrings.add(current, coefficientStrings.remove(current) + "8");
				} else if (e.getKeyCode() == KeyEvent.VK_9) {
					coefficientStrings.add(current, coefficientStrings.remove(current) + "9");
				} else if (e.getKeyCode() == KeyEvent.VK_0) {
					coefficientStrings.add(current, coefficientStrings.remove(current) + "0");
				} else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
					coefficientStrings.add(current, "-" + coefficientStrings.remove(current));
				}
				coefficients.clear();
				for (int i = 0; i < coefficientStrings.size(); i++) {
					coefficients.add(Integer.parseInt(coefficientStrings.get(i)));
				}
				polynomial = new BisectionPolynomial(coefficients, range, precision);
				repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT && custom == true) {
				current++;
				if (current >= coefficients.size()) {
					current = 0;
				}
				repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_LEFT && custom == true) {
				current--;
				if (current < 0) {
					current = coefficients.size() - 1;
				}
				repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && custom == true) {
				if (custom == true) {
					if (degree == false) {
						if (coefficientStrings.get(current).length() > 1) {
							coefficientStrings.add(current, coefficientStrings.get(current).substring(0, coefficientStrings.remove(current).length() - 1));
						} else {
							coefficientStrings.remove(current);
							coefficientStrings.add(current, "0");
						}
						compilePolynomial();
						repaint();
					} else if (degree == true) {
						if (degreeString.length() > 1) {
							degreeString = degreeString.substring(0, degreeString.length() - 1);
						} else {
							degreeString = "0";
						}
						while (coefficientStrings.size() > Integer.parseInt(degreeString) + 1) {
							coefficientStrings.remove(0);
						}
						compilePolynomial();
						repaint();
					}
				}
			} else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				scale *= 2;
				repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
				scale /= 2;
				repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_SPACE && custom == false) {
				generatePolynomial();
				repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_G) {
				if (graph == true) {
					graph = false;
				} else if (graph == false) {
					graph = true;
				}
				repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_C) {
				if (custom == true) {
					custom = false;
					generatePolynomial();
				} else if (custom == false) {
					custom = true;
					degree = false;
					degreeString = "2";
					coefficients = new ArrayList<Integer>();
					coefficients.add(1);
					coefficients.add(0);
					coefficients.add(0);
					coefficientStrings.add("1");
					coefficientStrings.add("0");
					coefficientStrings.add("0");
					polynomial = new BisectionQuadratic(coefficients, range, precision);
				}
				repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_D && custom == true) {
				if (degree == false) {
					degree = true;
				} else if (degree == true) {
					degree = false;
				}
				current = 0;
				repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_I) {
				range *= 10;
				polynomial = new BisectionPolynomial(coefficients, range, precision);
				repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_K) {
				range /= 10;
				polynomial = new BisectionPolynomial(coefficients, range, precision);
				repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_J) {
				wide /= 2;
				repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_L) {
				wide *= 2;
				repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_PERIOD) {
				precision /= 10;
				polynomial = new BisectionPolynomial(coefficients, range, precision);
				repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_COMMA) {
				precision *= 10;
				polynomial = new BisectionPolynomial(coefficients, range, precision);
				repaint();
			}

		}

		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				System.exit(0);
			}
		}
	}

// -----------------------------------------------------------------------------------------------------------------------------
// mouse input

	class MouseHandler extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			System.out.println("Click");
		}
	}
}

// -----------------------------------------------------------------------------------------------------------------------------
// polynomial

class BisectionPolynomial {

	private ArrayList<Integer> coefficients;
	private ArrayList<Double> solutions;
	private ArrayList<BetterPoint> graph;

	private int terms;

	private static boolean debug = false;

	public BisectionPolynomial(ArrayList<Integer> coefficients, double range, double precision) {
		this.coefficients = coefficients;
		this.terms = coefficients.size();

		// solutions
		ArrayList<Double> start = new ArrayList<Double>();
		for (double i = range / 2 * -1; i < (range / 2 * -1) + range; i += precision) {
			if ((function(coefficients, i) >= 0 && function(coefficients, i + precision) < 0) || (function(coefficients, i) <= 0 && function(coefficients, i + precision) > 0)) {
				start.add(i);

				// debugging
				if (debug == true) {
					System.out.println("Close Solution");
				}

			}
		}
		solutions = solver(coefficients, precision, precision / 10, start);

		// derivative solutions
		ArrayList<Integer> derivativeCoefficients = new ArrayList<Integer>();
		for (int i = 0; i < coefficients.size() - 1; i++) {
			derivativeCoefficients.add(coefficients.get(i) * (coefficients.size() - 1 - i));
		}
		start.clear();
		for (double i = range / 2 * -1; i < (range / 2 * -1) + range; i += precision) {
			if ((function(derivativeCoefficients, i) >= 0 && function(derivativeCoefficients, i + precision) < 0) || (function(derivativeCoefficients, i) <= 0 && function(derivativeCoefficients, i + precision) > 0)) {
				start.add(i);

				// debugging
				if (debug == true) {
					System.out.println("Close Solution");
				}

			}
		}
		ArrayList<Double> derivativeSolutions = solver(derivativeCoefficients, range, precision, start);
		for (int i = 0; i < derivativeSolutions.size(); i++) {
			if (Math.abs(function(coefficients, derivativeSolutions.get(i))) <= Math.pow(10, -10)) {
				solutions.add(derivativeSolutions.get(i));
			}
		}

		// debugging
		if (debug == true) {
			System.out.println("Solution: " + solutions.get(0));
			for (int i = 1; i < solutions.size(); i++) {
				System.out.println("Another Solution: " + solutions.get(i));
			}
		}

		// graph
		graph = new ArrayList<BetterPoint>();
		for (double i = range / 2 * -1; i < (range / 2 * -1) + range; i += precision) {
			graph.add(new BetterPoint(i, function(coefficients, i)));
		}
	}

	// solver
	public ArrayList<Double> solver(ArrayList<Integer> coefficients, double range, double precision, ArrayList<Double> start) {

		// debugging
		if (debug == true) {
			System.out.println("New");
		}

		ArrayList<Double> newStart = new ArrayList<Double>();
		ArrayList<Double> solutions = new ArrayList<Double>();
		for (int i = 0; i < start.size(); i++) {
			for (double j = start.get(i); j < start.get(i) + range; j += precision) {

				// debugging
				if (debug == true) {
					System.out.println("Function: " + function(coefficients, j));
				}

				if (Math.abs(function(coefficients, j)) <= Math.pow(10, -10)) {
					solutions.add(j);

					// debugging
					if (debug == true) {
						System.out.println("Solution");
					}

					break;
				} else if ((function(coefficients, j) > 0 && function(coefficients, j + precision) < 0) || (function(coefficients, j) < 0 && function(coefficients, j + precision) > 0)) {
					newStart.add(j);

					// debugging
					if (debug == true) {
						System.out.println("Close Solution");
					}

					break;
				}
			}

			// debugging
			if (debug == true) {
				System.out.println("Next");
			}

		}
		if (newStart.size() > 0) {
			ArrayList<Double> moreSolutions = solver(coefficients, precision, precision / 10, newStart);
			for (int i = 0; i < moreSolutions.size(); i++) {
				solutions.add(moreSolutions.get(i));
			}
		}
		return solutions;
	}

	// function
	public double function(ArrayList<Integer> coefficients, double x) {
		double result = 0;
		for (int i = 0; i < coefficients.size(); i++) {
			result += coefficients.get(coefficients.size() - 1 - i) * Math.pow(x, i);
		}
		return result;
	}

	// set methods

	public void setSolutions(ArrayList<Double> newSolutions) {
		solutions = newSolutions;
	}

	// get methods

	public ArrayList<Integer> getCoefficients() {
		return coefficients;
	}

	public ArrayList<Double> getSolutions() {
		return solutions;
	}

	public ArrayList<BetterPoint> getGraph() {
		return graph;
	}

}

// -----------------------------------------------------------------------------------------------------------------------------
// quadratic

class BisectionQuadratic extends BisectionPolynomial {
	public BisectionQuadratic(ArrayList<Integer> coefficients, double range, double precision) {
		super(coefficients, range, precision);
		setSolutions(solver(coefficients));
	}

	public ArrayList<Double> solver(ArrayList<Integer> coefficients) { // quadratic formula solver
		int a = coefficients.get(0);
		int b = coefficients.get(1);
		int c = coefficients.get(2);
		ArrayList<Double> solutions = new ArrayList<Double>();
		if (Math.pow(b, 2) - (4 * a * c) > 0) {
			solutions.add(((-1 * b) + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a));
			solutions.add(((-1 * b) - Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a));
		} else if (Math.pow(b, 2) - (4 * a * c) == 0) {
			solutions.add(((-1 * b) + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a));
		}
		return solutions;
	}
}

// -----------------------------------------------------------------------------------------------------------------------------
// point

class BetterPoint {

	double x;
	double y;

	public BetterPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
}