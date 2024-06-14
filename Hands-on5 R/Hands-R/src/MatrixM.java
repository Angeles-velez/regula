public class MatrixM {
    public final double[][] data;

    public MatrixM(double[][] data) {
        this.data = data;
    }

    // Constructor para convertir un arreglo unidimensional en una matriz columna
    public MatrixM(double[] data, int N) {
        this.data = new double[N][1];
        for (int i = 0; i < N; i++) {
            this.data[i][0] = data[i];
        }
    }

    // Método para obtener la transposición de la matriz
    public MatrixM transpose() {
        int m = data.length;
        int n = data[0].length;
        double[][] transposedData = new double[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                transposedData[j][i] = data[i][j];
            }
        }
        return new MatrixM(transposedData);
    }

    // Método para multiplicar dos matrices
    public MatrixM times(MatrixM other) {
        int m1 = data.length;
        int n1 = data[0].length;
        int n2 = other.data[0].length;
        double[][] resultData = new double[m1][n2];
        for (int i = 0; i < m1; i++) {
            for (int j = 0; j < n2; j++) {
                for (int k = 0; k < n1; k++) {
                    resultData[i][j] += data[i][k] * other.data[k][j];
                }
            }
        }
        return new MatrixM(resultData);
    }

    // Método para resolver un sistema de ecuaciones lineales
    public MatrixM solve(MatrixM other) {
        int n = data.length;
        double[][] augmentedMatrix = new double[n][n + 1];

        // Construir la matriz aumentada
        for (int i = 0; i < n; i++) {
            System.arraycopy(data[i], 0, augmentedMatrix[i], 0, n);
            augmentedMatrix[i][n] = other.data[i][0];
        }

        // Aplicar eliminación Gauss-Jordan para resolver el sistema
        for (int i = 0; i < n; i++) {
            // Encontrar el máximo en la columna i para pivote
            double maxEl = Math.abs(augmentedMatrix[i][i]);
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(augmentedMatrix[k][i]) > maxEl) {
                    maxEl = Math.abs(augmentedMatrix[k][i]);
                    maxRow = k;
                }
            }

            // Intercambiar filas para tener el máximo como pivote
            double[] temp = augmentedMatrix[maxRow];
            augmentedMatrix[maxRow] = augmentedMatrix[i];
            augmentedMatrix[i] = temp;

            // Hacer el pivote en la diagonal 1
            double diagElement = augmentedMatrix[i][i];
            for (int j = 0; j < n + 1; j++) {
                augmentedMatrix[i][j] /= diagElement;
            }

            // Hacer 0 en las demás filas para la columna i
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double factor = augmentedMatrix[k][i];
                    for (int j = 0; j < n + 1; j++) {
                        augmentedMatrix[k][j] -= factor * augmentedMatrix[i][j];
                    }
                }
            }
        }

        // Extraer la solución de la matriz aumentada
        double[][] solution = new double[n][1];
        for (int i = 0; i < n; i++) {
            solution[i][0] = augmentedMatrix[i][n];
        }
        return new MatrixM(solution);
    }

    // Método para obtener una copia de los elementos de la matriz en un arreglo unidimensional
    public double[] getColumnPackedCopy() {
        int m = data.length;
        int n = data[0].length;
        double[] result = new double[m * n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                result[i + j * m] = data[i][j];
            }
        }
        return result;
    }
}
