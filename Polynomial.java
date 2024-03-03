
public class Polynomial {

	double[] coefficients;

	public Polynomial(double[] coefficients) {
		this.coefficients = coefficients;
	}

	public double[] getCoefficients() {
		return coefficients;
	}

	public double getCoefficient(int index) {
		return coefficients[index];
	}
}