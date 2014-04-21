import utilities.Conversions;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    private JLabel currentCarLabel;
    private JLabel speedLabel;
    private JLabel positionLabel;
    private JLabel vFreeLabel;
    private JLabel nextCarLabel;
    private JLabel nextCarSpeedLabel;
    private JLabel nextCarDistanceLabel;
    private JLabel prevCarLabel;
    private JLabel prevCarSpeedLabel;
    private JLabel prevCarDistanceLabel;

    private int carIndex = -1;
    private CarArrayList carArrayList;
    private CarList carList;
    private boolean initialized = false;
    private int width, height;


    public void initialize(CarArrayList carsList) {
        createGUI();
        //
        carArrayList = carsList;
        initialized = true;
    }

    public void setCar(int i) {
        carIndex = i;
        //

        refreshData();
    }

    public void createGUI() {
        setBackground(Color.BLACK);
        setLayout(null);
        //
        currentCarLabel = new JLabel("Selected car:");
        currentCarLabel.setForeground(Color.YELLOW);
        add(currentCarLabel);

        positionLabel = new JLabel("Position: x = ");
        add(positionLabel);
        speedLabel = new JLabel("Speed: v = ");
        add(speedLabel);
        vFreeLabel = new JLabel("Free speed: vf = ");
        add(vFreeLabel);

        nextCarLabel = new JLabel("Next car: ");
        nextCarLabel.setForeground(Color.YELLOW);
        add(nextCarLabel);

        nextCarDistanceLabel = new JLabel("Distance: s = ");
        add(nextCarDistanceLabel);

        nextCarSpeedLabel = new JLabel("Speed: v = ");
        add(nextCarSpeedLabel);

        prevCarLabel = new JLabel("Previous car: ");
        prevCarLabel.setForeground(Color.YELLOW);
        add(prevCarLabel);

        prevCarDistanceLabel = new JLabel("Distance: s = ");
        add(prevCarDistanceLabel);

        prevCarSpeedLabel = new JLabel("Speed: v = ");
        add(prevCarSpeedLabel);

    }

    public void arrangeGUI() {
        if (!initialized)
            return;
        width = getWidth();
        height = getHeight();
        currentCarLabel.setBounds(20, 0, width - 40, 30);
        positionLabel.setBounds(20, 30, width - 40, 30);
        speedLabel.setBounds(20, 60, width - 40, 30);
        vFreeLabel.setBounds(20, 90, width - 40, 30);
        nextCarLabel.setBounds(20, 120, width - 40, 30);
        nextCarDistanceLabel.setBounds(20, 150, width - 40, 30);
        nextCarSpeedLabel.setBounds(20, 180, width - 40, 30);
        prevCarLabel.setBounds(20, 210, width - 40, 30);
        prevCarDistanceLabel.setBounds(20, 240, width - 40, 30);
        prevCarSpeedLabel.setBounds(20, 270, width - 40, 30);
    }

    public void refreshData() {
        if (!initialized)
            return;
        if ((carIndex==-1) | (carArrayList.getCount()==0))
            return;
        CarArrayList.CarInfo car = (CarArrayList.CarInfo) carArrayList.cars.get(carIndex);
    //    CarList.CarInfo car = carList.cars[carIndex];
        currentCarLabel.setText("Selected car: " + carIndex);
        positionLabel.setText("Position x = " + Math.round(car.x) + " m");
        speedLabel.setText("Speed v = " + Math.round(Conversions.MStoKmH(car.v)) + " km/h");
        vFreeLabel.setText("Free speed vf = " + Math.round(Conversions.MStoKmH(car.vFree)) + " km/h");
        int iNext = carArrayList.findLeader(carIndex, car.lane);

        if (iNext < 0) {
            nextCarLabel.setText("this car is the leading car in the " + Conversions.LaneToStr(car.lane));
            nextCarDistanceLabel.setText("");
            nextCarSpeedLabel.setText("");
        } else {
            CarArrayList.CarInfo nextCar = (CarArrayList.CarInfo) carArrayList.cars.get(iNext);
        //    CarList.CarInfo nextCar = carList.cars[iNext];
            nextCarLabel.setText("Next car: " + iNext);
            nextCarDistanceLabel.setText("Distance: s = " + Math.round(carArrayList.getFrontDistance(carIndex, iNext)) + " m");
            nextCarSpeedLabel.setText("Speed: v = " + Math.round(Conversions.MStoKmH(nextCar.v)) + " km/h");
        }

        int iPrev = carArrayList.findFollower(carIndex, car.lane);

        if (iPrev < 0) {
            prevCarLabel.setText("this car is the last car in the " + Conversions.LaneToStr(car.lane));
            prevCarDistanceLabel.setText("");
            prevCarSpeedLabel.setText("");
        } else {
            CarArrayList.CarInfo prevCar = (CarArrayList.CarInfo) carArrayList.cars.get(iPrev);
            //CarList.CarInfo prevCar = carList.cars[iPrev];
            prevCarLabel.setText("Previous car: " + iPrev);
            prevCarDistanceLabel.setText("Distance: s = " + Math.round(carArrayList.getBackDistance(carIndex, iPrev)) + " m");
            prevCarSpeedLabel.setText("Speed: v = " + Math.round(Conversions.MStoKmH(prevCar.v)) + " km/h");
        }


    }
}
