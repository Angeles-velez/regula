/**
 * Represents a dataset with values of x and y.
 */
public class DataSet5 {

    private float[] xValues; // Array for x values
    private float[] yValues; // Array for y values

    /**
     * Default constructor initializes the arrays with example data.
     */
    public DataSet5() {
        xValues = new float[] {2050, 2045, 2040, 2035, 2030, 2025, 2020, 2019, 2018, 2017,
                2016, 2015, 2010, 2005, 2000, 1995, 1990, 1985, 1980, 1975,
                1970, 1965, 1960, 1955};
        yValues = new float[] {0.23f, 0.35f, 0.5f, 0.66f, 0.8f, 0.92f, 1.04f, 1.02f, 1.04f,
                1.07f, 1.1f, 1.2f, 1.47f, 1.67f, 1.85f, 1.99f, 2.17f, 2.33f,
                2.32f, 2.33f, 2.15f, 2.07f, 1.91f, 1.72f};
    }

    /**
     * Constructor that allows initializing the arrays with provided data.
     *
     * @param x Array of x values
     * @param y Array of y values
     */
    public DataSet5(float[] x, float[] y) {
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
     * Main method demonstrating usage of the DataSet5 class.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Create an instance of DataSet5 using the default constructor
        DataSet5 dataSet = new DataSet5();

        // Get the arrays x and y
        float[] xValues = dataSet.getX();
        float[] yValues = dataSet.getY();

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
