/**
 * Represents a dataset containing x and y values for DataSet8.
 */
public class DataSet8 {

    private float[] xValues; // Array to store x values
    private float[] yValues; // Array to store y values

    /**
     * Default constructor initializes the dataset with example data.
     */
    public DataSet8() {
        xValues = new float[] {551, 545, 536, 523, 506, 486, 464, 460,
                455, 450, 445, 441, 415, 386, 355, 324,
                294, 264, 235, 210, 187, 168, 152, 138};
        yValues = new float[] {53.5f, 50.1f, 46.7f, 43.5f, 40.4f, 37.6f, 35f,
                34.5f, 34.1f, 33.6f, 33.2f, 32.7f, 30.8f, 29.1f,
                27.6f, 26.5f, 25.5f, 24.3f, 23f, 21.3f, 19.7f,
                18.7f, 17.9f, 17.6f};
    }

    /**
     * Constructor that initializes the dataset with given x and y values.
     *
     * @param x Array of x values
     * @param y Array of y values
     */
    public DataSet8(float[] x, float[] y) {
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
     * Main method to demonstrate usage of the DataSet8 class.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Create an instance of DataSet8 using the default constructor
        DataSet8 dataSet = new DataSet8();

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
