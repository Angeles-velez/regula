/**
 * Represents a dataset containing x and y values for DataSet9.
 */
public class DataSet9 {

    private float[] xValues; // Array to store x values
    private float[] yValues; // Array to store y values

    /**
     * Default constructor initializes the dataset with example data.
     */
    public DataSet9() {
        xValues = new float[]{876613025, 811749463, 744380367, 675456367, 607341981,
                542742539, 483098640, 471828295, 460779764, 449963381,
                439391699, 429069459, 380744554, 334479406, 291350282,
                255558824, 222296728, 190321782, 160941941, 132533810,
                109388950, 93493844, 80565723, 71958495};
        yValues = new float[]{16.84f, 17.09f, 17.31f, 17.48f, 17.59f, 17.66f, 17.7f,
                17.71f, 17.73f, 17.74f, 17.75f, 17.75f, 17.74f, 17.54f,
                17.2f, 16.78f, 16.39f, 16.1f, 15.68f, 15.27f, 15f, 14.95f,
                14.85f, 14.78f};
    }

    /**
     * Constructor that initializes the dataset with given x and y values.
     *
     * @param x Array of x values
     * @param y Array of y values
     */
    public DataSet9(float[] x, float[] y) {
        this.xValues = x;
        this.yValues = y;
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
     * Main method to demonstrate usage of the DataSet9 class.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Create an instance of DataSet9 using the default constructor
        DataSet9 dataSet = new DataSet9();

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
