class PolinomialRegression {
    private final double[] xData;
    private final double[] yData;
    private final int degree;
    private double[] coefficients;

    public PolinomialRegression(double[] xData, double[] yData, int degree) {
        this.xData = xData;
        this.yData = yData;
        this.degree = degree;
    }

    public void fit() {
        int N = xData.length;
        int numCoefficients = degree + 1;
        double[][] X = new double[N][numCoefficients];
        double[] Y = new double[N];

        for (int i = 0; i < N; i++) {
            double xVal = xData[i];
            Y[i] = yData[i];
            for (int j = 0; j < numCoefficients; j++) {
                X[i][j] = Math.pow(xVal, j);
            }
        }

        // Resolver el sistema de ecuaciones normales (X^T * X) * a = X^T * y
        double[][] XT = transpose(X);
        double[][] XTX = multiply(XT, X);
        double[] XTY = multiply(XT, Y);
        coefficients = solve(XTX, XTY);
    }

    private double[][] transpose(double[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        double[][] transposed = new double[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }

    private double[][] multiply(double[][] A, double[][] B) {
        int mA = A.length;
        int nA = A[0].length;
        int mB = B.length;
        int nB = B[0].length;
        double[][] C = new double[mA][nB];
        for (int i = 0; i < mA; i++) {
            for (int j = 0; j < nB; j++) {
                for (int k = 0; k < nA; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return C;
    }

    private double[] multiply(double[][] A, double[] B) {
        int mA = A.length;
        int nA = A[0].length;
        double[] C = new double[nA];
        for (int i = 0; i < mA; i++) {
            for (int j = 0; j < nA; j++) {
                C[j] += A[i][j] * B[i];
            }
        }
        return C;
    }

    private double[] solve(double[][] A, double[] B) {
        int n = A.length;
        double[] solution = new double[n];

        for (int i = n - 1; i >= 0; i--) {
            double sum = B[i];
            for (int j = i + 1; j < n; j++) {
                sum -= A[i][j] * solution[j];
            }
            solution[i] = sum / A[i][i];
        }
        return solution;
    }

    public void printRegEquation() {
        System.out.print("Ecuación de regresión: y = ");
        for (int i = 0; i < coefficients.length; i++) {
            System.out.print(coefficients[i] + " * x^" + i);
            if (i < coefficients.length - 1) {
                System.out.print(" + ");
            }
        }
        System.out.println();
    }

    public void predict(double x) {
        double y = 0;
        for (int i = 0; i < coefficients.length; i++) {
            y += coefficients[i] * Math.pow(x, i);
        }
        System.out.println("Predicción para x = " + x + " es y = " + y);
    }
}
