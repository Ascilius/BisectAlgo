import java.util.ArrayList;

public class PolySolver {

	private static final double ALMOST_ZERO = 0.0000000001;
	private static final int RANGE = 1;

	public static void main(String[] arg0) {
		// input
		double[] coefficients = { 1.0, 0.0, -2.0 };
		Polynomial poly = new Polynomial(coefficients);

		// debugging
		for (int i = 0; i < poly.getCoefficients().length; i++) {
			System.out.print(poly.getCoefficient(i) + " ");
		}
		System.out.println();

		ArrayList<Double> solutions = new ArrayList<Double>();
		solutions = solver(solutions, poly.getCoefficients());

		// solutions
		for (int i = 0; i < poly.getCoefficients().length - 2; i++) {
			System.out.print(poly.getCoefficient(i) + "x^" + (poly.getCoefficients().length - 1 - i) + "+");
		}
		System.out.print(poly.getCoefficient(poly.getCoefficients().length - 2) + "x+" + poly.getCoefficient(poly.getCoefficients().length - 1));
		System.out.println();
		System.out.print("x = ");
		for (int i = 0; i < solutions.size() - 1; i++) {
			System.out.print(solutions.get(i) + " ");
		}
		System.out.println(solutions.get(solutions.size() - 1));
	}

	// solver
	public static ArrayList<Double> solver(ArrayList<Double> solutions, double[] coefficients) {
		if (coefficients.length == 0) {
			return solutions;
		}
		double[] newCoefficients = new double[coefficients.length - 1];
		double i = RANGE * -1;
		while (true) {
			for (int j = 1; j < coefficients.length; j++) {
				coefficients[j] = coefficients[j - 1] * i;
			}
			if (Math.abs(coefficients[coefficients.length - 1]) <= ALMOST_ZERO) {
				for (int j = 0; j < newCoefficients.length; j++) {
					newCoefficients[j] = coefficients[j];
				}
				solutions.add(i);
				
				// debugging
				System.out.println("YEET");
				
				return solver(solutions, newCoefficients);
			}
			i += 0.0000000001;
		}
	}
}
