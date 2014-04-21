package utilities;

/**
 * Created with IntelliJ IDEA.
 * User: scholtz
 * Date: 10/27/12
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class Vectors {

    public static double[] Vector(double[] A, double[] B) {
        double[] v = new double[A.length];
        for (int i = 0; i < A.length; i++)
            v[i] = B[i] - A[i];
        return v;
    }

    public static double[] Multiply(double[] v, double k) {
        double[] newV = new double[v.length];
        for (int i = 0; i < v.length; i++)
            newV[i] = k * v[i];
        return newV;
    }

    public static double[] LinComb(double alpha, double[] u, double beta, double[] v) {
        double[] newV = new double[u.length];
        for (int i = 0; i < u.length; i++)
            newV[i] = alpha * u[i] + beta * v[i];
        return newV;
    }

    public static double[] LinComb(double[] r0, double alpha, double[] u, double beta, double[] v) {
        double[] newV = new double[u.length];
        for (int i = 0; i < u.length; i++)
            newV[i] = r0[i] + alpha * u[i] + beta * v[i];
        return newV;
    }


}
