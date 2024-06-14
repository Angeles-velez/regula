public class DataSet3  {

    private float[] x;
    private float[] y;

    public DataSet3() {
        this.x = new float[]{18, 22, 23, 26, 28, 31, 33};
        this.y = new float[]{10000, 15000, 18000, 21000, 24000, 26500, 27000};
    }

    public DataSet3(float[] x, float[] y) {
        this.x = x;
        this.y = y;
    }


    public float[] getX() {
        return this.x;
    }

    public float[] getY() {
        return this.y;
    }

}