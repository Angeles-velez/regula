/**
 * Represents a dataset containing x and y values for DataSet7.
 */
public class DataSet7 {

    private float[] xValues; // Array to store x values
    private float[] yValues; // Array to store y values

    /**
     * Default constructor initializes the dataset with example data.
     */
    public DataSet7() {
        xValues = new float[]{38.1f, 36.6f, 35f, 33.3f, 31.7f, 30f, 28.4f, 27.1f,
                27.1f, 27.1f, 27.1f, 26.8f, 25.1f, 23.8f, 22.7f,
                21.8f, 21.1f, 20.6f, 20.2f, 19.7f, 19.3f, 19.6f,
                20.2f, 20.7f};
        yValues = new float[]{2.24f, 2.24f, 2.24f, 2.24f, 2.24f, 2.24f, 2.24f,
                2.36f, 2.36f, 2.36f, 2.36f, 2.4f, 2.8f, 3.14f, 3.48f,
                3.83f, 4.27f, 4.68f, 4.97f, 5.41f, 5.72f, 5.89f,
                5.9f, 5.9f};
    }

    /**
     * Constructor that initializes the dataset with given x and y values.
     *
     * @param x Array of x values
     * @param y Array of y values
     */
    public DataSet7(float[] x, float[] y) {
        xValues = x;
        yValues = y;
    }

    /**
     * Retrieves the array of x values.
     *
     * @return Array of x values
     */
    public float[] getXValues() {
        return xValues;
    }

    /**
     * Retrieves the array of y values.
     *
     * @return Array of y values
     */
    public float[] getYValues() {
        return yValues;
    }

    /**
     * Main method to demonstrate usage of the DataSet7 class.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Create an instance of DataSet7 using the default constructor
        DataSet7 dataSet = new DataSet7();

        // Get the arrays of x and y values
        float[] xValues = dataSet.getXValues();
        float[] yValues = dataSet.getYValues();

        // Print the x and y values
        System.out.println("X values:");
        for (float x : xValues) {
            System.out.println(x);
        }

        System.out.println("\nY values:");
        for (float y : yValues) {
            System.out.println(y);
        }
    }
}
