/**
 * Represents a dataset with values of x and y.
 */
public class DataSet4 {

    private float[] xValues; // Array for x values
    private float[] yValues; // Array for y values

    /**
     * Default constructor initializes the arrays with example data.
     */
    public DataSet4() {
        xValues = new float[] {18, 22, 23, 26, 28, 31, 33};
        yValues = new float[] {10000, 15000, 18000, 21000, 24000, 26500, 27000};
    }

    /**
     * Constructor that allows initializing the arrays with provided data.
     *
     * @param x Array of x values
     * @param y Array of y values
     */
    public DataSet4(float[] x, float[] y) {
        xValues = x;
        yValues = y;
    }

    /**
     * Retrieves the x values.
     *
     * @return Array of x values
     */
    public float[] getX() {
        return xValues;
    }

    /**
     * Retrieves the y values.
     *
     * @return Array of y values
     */
    public float[] getY() {
        return yValues;
    }

    /**
     * Main method demonstrating usage of the DataSet4 class.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Create an instance of DataSet4 using the default constructor
        DataSet4 dataSet = new DataSet4();

        // Get the arrays x and y
        float[] xValues = dataSet.getX();
        float[] yValues = dataSet.getY();

        // Print the x values
        System.out.println("X values:");
        for (float x : xValues) {
            System.out.println(x);
        }

        // Print the y values
        System.out.println("\nY values:");
        for (float y : yValues) {
            System.out.println(y);
        }
    }
}
