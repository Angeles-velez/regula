/**
 * Represents a dataset containing x and y values.
 */
public class DataSet6 {

    private float[] xValues; // Array to store x values
    private float[] yValues; // Array to store y values

    /**
     * Default constructor initializes the dataset with example data.
     */
    public DataSet6() {
        xValues = new float[]{3711367, 5585537, 7793541, 10016298, 11726140, 13001447, 13970396,
                13775474, 13965495, 14159536, 14364846, 15174247, 17334249, 18206876,
                18530592, 18128958, 17783558, 17081433, 15169989, 13582621, 11213294,
                9715129, 8133417, 6711079};
        yValues = new float[]{0, -414772, -415736, -415732, -440124, -464081, -532687, -532687,
                -532687, -532687, -532687, -470015, -531169, -377797, -136514,
                -110590, 9030, 115942, 222247, 421208, -68569, -17078, -30805, -21140};
    }

    /**
     * Constructor that initializes the dataset with given x and y values.
     *
     * @param x Array of x values
     * @param y Array of y values
     */
    public DataSet6(float[] x, float[] y) {
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
     * Main method to demonstrate usage of the DataSet6 class.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Create an instance of DataSet6 using the default constructor
        DataSet6 dataSet = new DataSet6();

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
