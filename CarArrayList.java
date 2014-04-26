import utilities.Conversions;
import utilities.MatlabImport;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static utilities.Distributions.getPoissonRandom;

/**
 * Created by Lada on 22.2.14.
 */
public class CarArrayList {

    public class CarInfo {
        double x = 0;
        double xold = 0;
        double v = 0;
        double vold =0;
        double vFree = 0;
        double a = 0.3;
        double b = 3;
        double L = 7;
        double T = 1.5;
        double p = 0.5;
        int id;
        double rightBias = 0.3;
        double leftUnBias = -0.1;
        int lane = 1;
        double aThreshold = 0.2;
        double bSafe = 4;
        int delta = 4;
        boolean overtaking = true;

        public Image image;
        public String imageName;

        public CarInfo(String imageFile,int id) {
            try {
                imageName = imageFile;
                image = ImageIO.read(new File(imageFile));
            } catch (Exception e) {
                System.out.println("Error reading " + imageFile);
            }
            this.id = id;
        }

        public int nextLane() {
            return (lane == 1) ? 2 : 1;
        }

    }
    int countAll = 0;
    double time = 0;
    double dt = 0.01;
    MatlabImport matlabdata = new MatlabImport("0170","01");
    private String defaultImageFile = "car.png";
    int timeStepData = 0;
    int[] remaining = new int[3];
    double time60 = 0;
   //   int numberofcarspersecond = 4;
 //   double generateTimeLane1 = getPoissonRandom(numberofcarspersecond,60);
    double generateTimeLane1 = 0;
    public ArrayList cars = new ArrayList();
    double hwLength = 2700;
    boolean cyclic = false;
    double carsToPoisson;
    boolean add;
    double numCars;


    public CarArrayList() {
   //            cars.add(new CarInfo(defaultImageFile));
        int first =  (int) matlabdata.getNumCars(timeStepData,0);
        remaining[1] = timeStepData;
        remaining[2] = 0;
        if (first == 0) {
          generateTimeLane1 +=60;
          remaining[0] = 0;
          add = false;
          numCars = 0;
        }
        else {
          generateTimeLane1 += getPoissonRandom(first,60);
          remaining[0] = first;
          add = true;
          numCars = first;
        }


    }

    public CarArrayList(int numOfCars) {
        super();
        for (int i=0;i<numOfCars;i++)
        cars.add(new CarInfo(defaultImageFile,i));
     }

    public int getCount() {
        return cars.size();
    }


    private CarInfo copyCar(CarInfo source) {
        CarInfo target = new CarInfo(source.imageName,source.id);
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

    private CarArrayList copyCars(CarArrayList source) {
        int n = source.getCount();
        CarArrayList target = new CarArrayList();
        for (int i = 0; i < n; i++)
            target.cars.add(i, copyCar((CarInfo) source.cars.get(i)));
        return target;
    }

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
        CarInfo source = (CarInfo) cars.get(i);
        for (int j = 0; j < getCount(); j++){
            CarInfo actual = (CarInfo) cars.get(j);
            if (actual==null) continue;
            if ((i != j) & (actual.lane == lane)) {
                dist = actual.xold - source.x;
                if ((dist < 0) & cyclic)
                    dist = (hwLength - source.x) + (actual.xold - 0);
                if ((dist > 0) & ((minDist < 0) | (dist < minDist))) {
                    minDist = dist;
                    lead = j;
                }
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
        CarInfo source = (CarInfo) cars.get(i);
        for (int j = 0; j < getCount(); j++){
            CarInfo actual = (CarInfo) cars.get(j);
            if (actual==null) continue;
            if ((i != j) & (actual.lane == lane)) {
                dist = source.x - actual.xold;
                if ((dist < 0) & (cyclic))
                    dist = (hwLength - actual.xold) + (source.x - 0);
                if ((dist > 0) & ((minDist < 0) | (dist < minDist))) {
                    minDist = dist;
                    follow = j;
                }
            }
        }
        return follow;
    }


    public double getFrontDistance(int i, int j) {
        CarInfo cari = (CarInfo) cars.get(i);
        CarInfo carj = (CarInfo) cars.get(j);
        double d = carj.x - cari.x;
        if ((d < 0)&cyclic)
            d = hwLength - cari.x + carj.x;
        return d;
    }

    public double getBackDistance(int i, int j) {
        CarInfo cari = (CarInfo) cars.get(i);
        CarInfo carj = (CarInfo) cars.get(j);
        double d = cari.x - carj.x;
        if ((d < 0)&cyclic)
            d = hwLength - carj.x + cari.x;
        return d;
    }

    public double acceleration(int i, int j) {
        if ((j < 0) | (i < 0))
            return 0;
        CarInfo car = (CarInfo) cars.get(i);
        CarInfo carj = (CarInfo) cars.get(j);
        double s = carj.xold - car.x > 0 ? carj.xold - car.x : hwLength - car.x + carj.xold;
        double sStar = car.L + car.v * car.T + car.v * (car.v - carj.vold) / (2 * Math.sqrt(car.a * car.b));
        return -car.a * sqr(sStar / s);
    }

    public void nextTime(){
        time += dt;
        time60 += dt;
    }

    public void destroyCar(){
        for (int i = 0; i < this.getCount(); i++) {
            CarInfo cari = (CarInfo) cars.get(i);
            if (cari.x >= hwLength)
                cars.remove(i);
       }

    }

    public void nextStep() {
  //      CarArrayList old = copyCars(this);
        if (time60>=59.99){
            time60 = 0;
            timeStepData++;
            remaining[2]=remaining[0];
            numCars = 4 + remaining[2];//matlabdata.getNumCars(timeStepData,0);
            remaining[0]=(int) numCars;
        }

        if ((generateTimeLane1<=this.time+0.01)&(generateTimeLane1>=this.time-0.01)){
            boolean generate;
            generate = !add ? false : true;
            add = true;
        //    carsToPoisson = numCars + remaining[2];
            // remaining 0 - number of cars to be generated, remaining 1 - timeStepData, remaining 2 - number of cars to be generated in this period

            if (numCars==0){
                generateTimeLane1 += 60 - (generateTimeLane1 % 60);
                add = false;
            }
            else {
                double plus;
                do {
                //  plus = getPoissonRandom(5,60);
                  plus = 14;
                }
                while (plus>60);

                generateTimeLane1+=plus;
                if (generate){
                    remaining[0] --;
                    generateCar();
                }
            }
        }

        if (cars==null){
            nextTime();
            return;
        }
        if (this.getCount()>0)
        for (int i = 0; i < this.getCount(); i++) {
            CarInfo cari = (CarInfo) cars.get(i);
            cari.vold = cari.v;
            cari.xold = cari.x;
            if (cari==null) continue;
            CarInfo car = cari;
            int leader = findLeader(i, car.lane); //leading car in the current lane
            int leaderNext = findLeader(i, car.nextLane()); //leading car in the next lane
            int follower = findFollower(i, car.nextLane()); //following car in the next lane
            double aFree = (car.vFree == 0) ? 0 : car.a * (1 - power(car.v / car.vFree, car.delta));
            double acc1 = this.acceleration(i, leader);  //acceleration in the current lane
            double acc2 = this.acceleration(i, leaderNext); //acceleration in the next lane
            // acceleration of previous car in the next lane
            double accB = this.acceleration(follower, findLeader(follower, car.nextLane()));
            // hypothetical acceleration of previous car if the current car changes the lane
            double accB2 = this.acceleration(follower, i);
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

        }
        destroyCar();
        nextTime();
    }

 /*   public int timesToGetInto(int time,int lane){
       int carCount = matlabdata.getNumCars(time, lane);
       double pst = getPoissonRandom(carCount*(1/60));
       return 5;
    }*/


    public void generateCar(){
    //   Random rnd = new Random();
        int position = this.getCount();
   //     int time = timesToGetInto(0,0);
        cars.add(position,new CarInfo(defaultImageFile,countAll));
        countAll++;
        CarArrayList.CarInfo carnew = (CarArrayList.CarInfo) this.cars.get(position);
        carnew.x=0;
        carnew.vFree = Conversions.KmHtoMS(matlabdata.getSpeed(timeStepData,0));// Conversions.KmHtoMS(100 + 20*rnd.nextGaussian());
        if (carnew.vFree< Conversions.KmHtoMS(80)) carnew.vFree=Conversions.KmHtoMS(80);
        carnew.v = Conversions.KmHtoMS(matlabdata.getSpeed(timeStepData,0)); //Conversions.KmHtoMS(100 + 20*rnd.nextGaussian());
        if (carnew.v< Conversions.KmHtoMS(80)) carnew.v= Conversions.KmHtoMS(80);
    }

}





