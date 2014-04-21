import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class CarList {

    public class CarInfo {
        double x = 0;
        double v = 0;
        double vFree = 0;
        double a = 0.3;
        double b = 3;
        double L = 7;
        double T = 1.5;
        double p = 0.5;
        double rightBias = 0.3;
        double leftUnBias = -0.1;
        int lane = 1;
        double aThreshold = 0.2;
        double bSafe = 4;
        int delta = 4;
        boolean overtaking = true;

        public Image image;
        public String imageName;

        public CarInfo(String imageFile) {
            try {
                imageName = imageFile;
                image = ImageIO.read(new File(imageFile));
            } catch (Exception e) {
                System.out.println("Error reading " + imageFile);
            }
        }

        public int nextLane() {
            return (lane == 1) ? 2 : 1;
        }


    }

    private int n;
    double time = 0;
    private String defaultImageFile = "car.png";
    public CarInfo[] cars;
    double hwLength;
    boolean cyclic = false;

    public CarList(int numberOfCars) {
        n = numberOfCars;
        cars = new CarInfo[n];
        for (int i = 0; i < n; i++) {
            cars[i] = new CarInfo(defaultImageFile);
        }
    }

    public int getCount() {
        return cars.length;
    }

    private CarInfo copyCar(CarInfo source) {
        CarInfo target = new CarInfo(source.imageName);
        target.x = source.x;
        target.v = source.v;
        target.vFree = source.vFree;
        target.a = source.a;
        target.b = source.b;
        target.L = source.L;
        target.T = source.T;
        target.lane = source.lane;
        target.aThreshold = source.aThreshold;
        target.bSafe = source.bSafe;
        target.p = source.p;
        return target;
    }

    private CarList copyCars(CarList source) {
        int n = source.getCount();
        CarList target = new CarList(n);
        for (int i = 0; i < n; i++)
            target.cars[i] = copyCar(source.cars[i]);
        return target;
    }

    double dt = 0.01;

    public double sqr(double x) {
        return x * x;
    }

    public double power(double x, int n) {
        double y = 1;
        for (int i = 0; i < n; i++)
            y *= x;
        return y;
    }

    public int findLeader(int i, int lane) {
        if (i < 0)
            return -1;
        double minDist = -1; //negative distance corresponds to leading car
        double dist;
        int lead = -1;
        for (int j = 0; j < getCount(); j++)
            if ((i != j) & (cars[j].lane == lane)) {
                dist = cars[j].x - cars[i].x;
                if ((dist < 0) & cyclic)
                    dist = (hwLength - cars[i].x) + (cars[j].x - 0);
                if ((dist > 0) & ((minDist < 0) | (dist < minDist))) {
                    minDist = dist;
                    lead = j;
                }
            }
        return lead;
    }


    public int findFollower(int i, int lane) {
        if (i < 0)
            return -1;
        double minDist = -1; //negative distance corresponds to the last car
        double dist;
        int follow = -1;
        for (int j = 0; j < getCount(); j++)
            if ((i != j) & (cars[j].lane == lane)) {
                dist = cars[i].x - cars[j].x;
                if ((dist < 0) & (cyclic))
                    dist = (hwLength - cars[j].x) + (cars[i].x - 0);
                if ((dist > 0) & ((minDist < 0) | (dist < minDist))) {
                    minDist = dist;
                    follow = j;
                }
            }
        return follow;
    }


    public double getFrontDistance(int i, int j) {
        double d = cars[j].x - cars[i].x;
        if ((d < 0)&cyclic)
            d = hwLength - cars[i].x + cars[j].x;
        return d;
    }

    public double getBackDistance(int i, int j) {
        double d = cars[i].x - cars[j].x;
        if ((d < 0)&cyclic)
            d = hwLength - cars[j].x + cars[i].x;
        return d;
    }


    public double acceleration(int i, int j) {
        if ((j < 0) | (i < 0))
            return 0;
        CarInfo car = cars[i];
        double s = cars[j].x - car.x > 0 ? cars[j].x - car.x : hwLength - car.x + cars[j].x;
        double sStar = car.L + car.v * car.T + car.v * (car.v - cars[j].v) / (2 * Math.sqrt(car.a * car.b));
        return -car.a * sqr(sStar / s);
    }

 //   boolean overtaking = true;

    public void nextStep() {
        CarList old = copyCars(this);
        for (int i = 0; i < old.getCount(); i++) {
            if (cars[i]==null) continue;
            CarInfo car = cars[i];
            int leader = findLeader(i, car.lane); //leading car in the current lane
            int leaderNext = findLeader(i, car.nextLane()); //leading car in the next lane
            int follower = findFollower(i, car.nextLane()); //following car in the next lane
            double aFree = (car.vFree == 0) ? 0 : car.a * (1 - power(car.v / car.vFree, car.delta));
            double acc1 = old.acceleration(i, leader);  //acceleration in the current lane
            double acc2 = old.acceleration(i, leaderNext); //acceleration in the next lane
            // acceleration of previous car in the next lane
            double accB = old.acceleration(follower, findLeader(follower, car.nextLane()));
            // hypothetical acceleration of previous car if the current car changes the lane
            double accB2 = old.acceleration(follower, i);
            // we prefer to ride in the right lane
            double right = (car.lane == 2) ? car.rightBias : car.leftUnBias;

            boolean incentive = right + acc2 - acc1 > car.p * (accB - accB2) + car.aThreshold;
            boolean politeness = accB2 > -car.bSafe;

            double acc;

            if (car.overtaking & incentive & politeness) {
                acc = acc2;
                car.lane = car.nextLane();
            } else {
                acc = acc1;
            }
            car.v += (aFree + acc) * dt;
      //      if (car.v<0) car.v=0;
            car.x += car.v * dt;
            if (cyclic)
                if (car.x >= hwLength) {
                    car.x = car.x - hwLength;
                }
      //      if (car.x>5600) car.p=0.2;
      //      if (car.x>5900) car.p=0.05;
      //      if (car.x>6050) car.p=0.5;
      //     if ((car.x>6050)&(car.x<6150)) car.overtaking=false;
            time += dt;
        }
    }
}
