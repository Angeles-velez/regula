/**
 * Represents a dataset containing x and y values for DataSet10.
 */
public class DataSet10 {

    private float[] xValues; // Array to store x values
    private float[] yValues; // Array to store y values

    /**
     * Default constructor initializes the dataset with example data.
     */
    public DataSet10() {
        xValues = new float[]{9735033990f, 9481803274f, 9198847240f, 8887524213f, 8548487400f,
                8184437460f, 7794798739f, 7713468100f, 7631091040f, 7547858925f,
                7464022049f, 7379797139f, 6956823603f, 6541907027f, 6143493823f,
                5744212979f, 5327231061f, 4870921740f, 4458003514f, 4079480606f,
                3700437046f, 3339583597f, 3034949748f, 2773019936f};
        yValues = new float[]{1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
    }

    /**
     * Constructor that initializes the dataset with given x and y values.
     *
     * @param x Array of x values
     * @param y Array of y values
     */
    public DataSet10(float[] x, float[] y) {
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
     * Main method to demonstrate usage of the DataSet10 class.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Create an instance of DataSet10 using the default constructor
        DataSet10 dataSet = new DataSet10();

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
