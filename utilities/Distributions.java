package utilities;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Mira
 * Date: 11.2.14
 * Time: 10:50
 * To change this template use File | Settings | File Templates.
 */
public class Distributions {

    public static double getPoissonRandom(double cars, double timeUnit) {
        double lambda = cars/timeUnit;
        Random r = new Random();
        double rnd = r.nextDouble();
        if (lambda>0.0){
            return (-1/lambda)*Math.log(rnd);
        } else {
            return rnd*timeUnit;
        }

    }
}
