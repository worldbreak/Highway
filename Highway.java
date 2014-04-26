import mathcomps.JDrawPanel;
import utilities.Conversions;
import utilities.MatlabImport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import static utilities.Distributions.getPoissonRandom;

public class Highway extends JFrame implements ComponentListener, Runnable {

    private class JHighWay extends JDrawPanel {


        double hwLength = 8000; /* in meters */
        double position = 0; /* position of the left edge */
        double zoom = 40; /* meters per screen */
        double viewAngle = Conversions.Radians(45); /* in degrees */

        double markerLength = 1.5; /* marker between lanes */
        double markerWidth = 1;

        double hwWidth = 10; /* width in meters */
        int borderX = 50; /* border along the road in pixels */
        int borderY = 70;

        int w, h;

        Polygon road;
        Font numbersFont = new Font("Times New Roman", Font.PLAIN, 20);



        private void drawHighway(Graphics g) {
            g.setColor(Color.GRAY);
            /* corners of the road */
            int[] xs = new int[4];
            int[] ys = new int[4];
            xs[0] = borderX;
            ys[0] = h - borderY; //left down
            xs[1] = w - 2 * borderX;
            ys[1] = h - borderY; //right down
            xs[2] = w - borderX;
            ys[2] = borderY; //right up
            xs[3] = 2 * borderX;
            ys[3] = borderY;
            road = new Polygon(xs, ys, 4);
            g.fillPolygon(road);
            g.setClip(road);
            double[] O = {xs[0], ys[0]};
            double[] A = {xs[1], ys[1]};
            double[] B = {xs[3], ys[3]};

            setScale(O, A, B, position, -hwWidth / 2, position + zoom, hwWidth / 2);
            g.setColor(Color.WHITE);

            /* markers  */

            int im = (int) (position / (2 * markerLength) - 1);
            double xm;
            do {
                xm = (2 * im + 1) * markerLength;
                fillRhomboid(g,
                        xm, -markerWidth / 2,
                        xm + markerLength, -markerWidth / 2,
                        xm + markerLength, markerWidth / 2,
                        xm, markerWidth / 2
                );
                im++;
            } while (xm < position + zoom);

        }

        public void update(Graphics g) {
            paint(g);
        }

        public void paint(Graphics g) {
            super.paint(g);

            w = getWidth();
            h = getHeight();
            position = 0;
            if (carArrayList.cars.size()!=0){
                if (selectedCar!=-1){
                CarArrayList.CarInfo carp = (CarArrayList.CarInfo) carArrayList.cars.get(selectedCar);
                position = carp.x - zoom / 2;
                }else{
                CarArrayList.CarInfo carp = (CarArrayList.CarInfo) carArrayList.cars.get(0);
                position = carp.x - zoom / 2;
                }

            }
            drawHighway(g);
            move(g, position, -hwWidth / 2);
            line(g, position, hwWidth / 2);
            if (carArrayList.cars.size()==0)
                return;

            for (int i = 0; i < carArrayList.getCount(); i++) {
                CarArrayList.CarInfo car = (CarArrayList.CarInfo) carArrayList.cars.get(i);
             //   CarList.CarInfo car = carList.cars[i];
                if ((car.x >= position) & (car.x <= position + zoom)) {
                    int[] coords = transform(car.x, 0);
                    g.drawImage(car.image, coords[0],
                            (car.lane == 1) ? coords[1] - 30 : coords[1] - 90, null);
                }
            }

            g.setFont(numbersFont);
            g.setClip(null);
            Rectangle roadBounds = road.getBounds();

            for (int i = 0; i < carArrayList.getCount(); i++){
                CarArrayList.CarInfo carii = (CarArrayList.CarInfo) carArrayList.cars.get(i);
                if ((carii.x >= position) & (carii.x <= position + zoom)) {
                    int[] coords = transform(carii.x, 0);
                    g.setColor((i == selectedCar) ? Color.YELLOW : Color.WHITE);
                    g.drawString("" + carii.id, coords[0] + 30, roadBounds.y + roadBounds.height + 30);
                }

            }
        }


    }



    public void run() {
        while (animator != null) {
            if (carArrayList.time == 86400){
                    animator.interrupt();
                    animator = null;
                    runBtn.setText("Start simulation");
                    fileCreator.finalize();
                    System.out.println(carArrayList.matlabdata.getAllCarsInDay());
            }
            int before = carArrayList.getCount();
            carArrayList.nextStep();
            int after = carArrayList.getCount();
            if (before!=after){
                carSelector.setItems(carArrayList,-1);
            }
            if (drawing) highWay.repaint();
            controlPanel.refreshData();
            timeLabel.setText("Time: t = " + Math.round(carArrayList.time));
            fileWriteSteps++;
            if (fileWriteSteps > fileWriteStepsMax) {
                fileWriteSteps = 0;
                fileCreator.writeStatus(carArrayList);
            }
            try {
             //     Thread.sleep(0,1);
            } catch (Exception e) {
                System.out.println("Thread problem");
            }
        }
    }


    Thread animator = null;


    JHighWay highWay;
    JPanel contentPane;
    JButton runBtn, generateBtn, drawingBtn, specialBtn;
    CarList carList = null;
    CarArrayList carArrayList = new CarArrayList();
    CarSelector carSelector;
    JScrollBar zoomBar;
    JLabel zoomLabel;
    JLabel timeLabel;
    ControlPanel controlPanel;
    FileCreator fileCreator;

    int fileWriteStepsMax = 60;
    int fileWriteSteps = fileWriteStepsMax;
    boolean drawing = true;


    int selectedCar = -1;

    public Highway() {
        super("Highway simulation");
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 400);
        createGUI();
        setVisible(true);
        // loading picture

    }

    public static void main(String[] args) {
        new Highway();
    }


    private void createGUI() {
        contentPane = new JPanel();
        contentPane.setBackground(Color.DARK_GRAY);
        contentPane.setLayout(null);
        addComponentListener(this);

        setContentPane(contentPane);
        highWay = new JHighWay();
        add(highWay);

        runBtn = new JButton("Start simulation");
        runBtn.addActionListener(new RunButton());
        runBtn.setEnabled(false);
        add(runBtn);

        generateBtn = new JButton("Generate cars");
        generateBtn.addActionListener(new GenerateButton());
        add(generateBtn);

        carSelector = new CarSelector();
        carSelector.setEditable(false);
        carSelector.setEnabled(false);
        carSelector.addActionListener(new CarSelected());
        carSelector.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jComboBox1MouseClicked(evt);
            }
        }
        );
        add(carSelector);

        zoomBar = new JScrollBar(JScrollBar.HORIZONTAL, 40, 5, 20, 500);
        zoomBar.addAdjustmentListener(new ZoomChange());
        add(zoomBar);
        zoomLabel = new JLabel("Meters / screen = " + highWay.zoom);
        zoomLabel.setForeground(Color.YELLOW);
        add(zoomLabel);

        controlPanel = new ControlPanel();
        controlPanel.setBackground(Color.DARK_GRAY);
        add(controlPanel);

        timeLabel = new JLabel("Time: t = ");
        timeLabel.setForeground(Color.YELLOW);
        add(timeLabel);

        drawingBtn = new JButton("Stop drawing");
        drawingBtn.addActionListener(new DrawingButton());
        add(drawingBtn);

        specialBtn = new JButton("");
        specialBtn.addActionListener(new SpecialButton());
        add(specialBtn);

    }

    private void arrangeGUI() {
        zoomBar.setBounds(50, 30, getWidth() - 100, 20);
        zoomLabel.setBounds(50, 10, getWidth() - 100, 20);
        highWay.setBounds(50, 50, getWidth() - 100, getHeight() / 3);
        runBtn.setBounds(50, getHeight() / 3 + 70, 200, 30);
        generateBtn.setBounds(300, getHeight() / 3 + 70, 200, 30);
        drawingBtn.setBounds(520, getHeight() / 3 + 70, 200, 30);
        specialBtn.setBounds(750, getHeight() / 3 + 70, 200, 30);
        carSelector.setBounds(250, getHeight() / 3 + 130, 100, 30);
        controlPanel.setBounds(50, getHeight() / 3 + 170, getWidth() / 3, (int) (1.5 * getHeight() / 3));
        controlPanel.arrangeGUI();
        timeLabel.setBounds(50 + (getWidth() - 100) / 2, 10, getWidth() - 100, 20);
        //
        specialBtn.setText(SpecialEventsList[specials]);
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
        arrangeGUI();
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
        arrangeGUI();
    }

    private class RunButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (animator == null) {
                runBtn.setText("Stop simulation");
                fileCreator.initialize(carArrayList, "cars");
                animator = new Thread(Highway.this);
                animator.start();
            } else {
                animator.interrupt();
                animator = null;
                runBtn.setText("Start simulation");
                fileCreator.finalize();

            }
        }
    }

    private class GenerateButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
         //   int N = matlabdata.getAllCarsInDay();
       /*     carArrayList = new CarArrayList(50);
            carArrayList.hwLength = highWay.hwLength;
        /*    carList = new CarList(50);
            carList.hwLength = highWay.hwLength;
        */

            int numberofcarspersecond = 4;
            double distance = 100;
            double vFree = Conversions.KmHtoMS(120);
            double vLast = 0;
            double xLast = 0;

           /*    CarList.CarInfo car0 = carList.cars[0];
            car0.x = 900;
            car0.vFree = vFree;
            car0.v = vFree;

            CarList.CarInfo car1 = carList.cars[1];
            car1.x = 100;
            car1.vFree = vFree;
            car1.v = vFree; */

            //equidistantly distributed
       /*     for (int j = 0; j < N - 1; j++) {
                CarList.CarInfo car = carList.cars[j];
                car.x = j * distance;
                car.vFree = vFree;
                vLast = car.v = equilibriumVelocity(0, 2 * vFree, 0, distance);
            }     */

       //     Random rnd = new Random();

        /*   for (int j=0;j < 49; j++){
                CarList.CarInfo car = carList.cars[j];
                xLast = car.x = xLast + distance + distance*(Math.random()-0.5);
                car.vFree = Conversions.KmHtoMS(100 + 20*rnd.nextGaussian());
                if (car.vFree< Conversions.KmHtoMS(80)) car.vFree=Conversions.KmHtoMS(80);
                vLast = car.v = Conversions.KmHtoMS(100 + 20*rnd.nextGaussian());
                if (car.v< Conversions.KmHtoMS(80)) vLast=car.v=Conversions.KmHtoMS(80);
            }*/

      /*      for (int j=0;j < 49; j++){
                CarArrayList.CarInfo carj = (CarArrayList.CarInfo) carArrayList.cars.get(j);
                xLast = carj.x = xLast + distance + distance*(Math.random()-0.5);
                carj.vFree = Conversions.KmHtoMS(100 + 20*rnd.nextGaussian());
                if (carj.vFree< Conversions.KmHtoMS(80)) carj.vFree=Conversions.KmHtoMS(80);
                vLast = carj.v = Conversions.KmHtoMS(100 + 20*rnd.nextGaussian());
                if (carj.v< Conversions.KmHtoMS(80)) vLast=carj.v=Conversions.KmHtoMS(80);
            } */

            // we set up the leading car separately
     /*      CarList.CarInfo car = carList.cars[N - 1];
            car.x = (N - 1) * distance;
            car.v = car.vFree = vLast;*/
    /*  uzavreni jednoho pruhu na 100metru
            CarList.CarInfo carfirst = carList.cars[N - 2];
            carfirst.x = 6050;
            carfirst.v = 0;
            carfirst.vFree = 0;


            CarList.CarInfo carfirst2 = carList.cars[N - 1];
            carfirst2.x = 6150;
            carfirst2.v = 0;
            carfirst2.vFree = 0;
      uzavreni jednoho pruhu na 100metru */

            fileCreator = new FileCreator();

            carSelector.removeAllItems();
            carSelector.setItems(carArrayList,selectedCar);
            carSelector.setEnabled(true);

            highWay.repaint();
            runBtn.setEnabled(true);
            controlPanel.initialize(carArrayList);
            controlPanel.arrangeGUI();
            controlPanel.setCar(0);
            generateBtn.setEnabled(false);
        }
    }

    private void ChangeCarSelect(){
        selectedCar = carSelector.getSelectedIndex();
        carSelector.setItems(carArrayList,selectedCar);
        controlPanel.setCar(selectedCar);
        highWay.repaint();

    }

    private void jComboBox1MouseClicked(java.awt.event.MouseEvent evt)
    {
        ChangeCarSelect();
    }


    private class CarSelected implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ChangeCarSelect();
        }
    }

    private class ZoomChange implements AdjustmentListener {
        public void adjustmentValueChanged(AdjustmentEvent e) {
            highWay.zoom = e.getValue();
            zoomLabel.setText("Meters / screen = " + e.getValue());
          //  if ((animator != null) & (!animator.isAlive())) {
            highWay.repaint();
          //  }
        }
    }


    private class DrawingButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (drawing) {
                drawingBtn.setText("Restore drawing");
                drawing = false;
            } else {
                drawingBtn.setText("Stop drawing");
                drawing = true;
            }
        }
    }


    int specials = 0;
    public static final String[] SpecialEventsList = {"Slow down leading car", "Accelerate the same car"};

    private class SpecialButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {


            CarList.CarInfo car;
            switch (specials) {
                case 0:
                    /* leading car slows down */
                    car = carList.cars[carList.getCount() - 1];
                    car.vFree = Conversions.KmHtoMS(50);
                    break;
                case 1: /* accelerate it back */
                    car = carList.cars[carList.getCount() - 1];
                    car.vFree = Conversions.KmHtoMS(250);
                    car.a = 1;
                    car.p = 0;
                    car.rightBias = 0;
                    car.leftUnBias = 0;
                    break;
            }

            specials = (specials == 1) ? 0 : specials + 1;
            specialBtn.setText(SpecialEventsList[specials]);
        }
    }

    public double y(double x, int i, double distance) {
        CarArrayList.CarInfo car = (CarArrayList.CarInfo) carArrayList.cars.get(i);
    //    CarList.CarInfo car = carList.cars[i];
        double afree = car.a * (1 - carList.power(x / car.vFree, car.delta));

        double s = distance;
        double sStar = car.L + x * car.T;//
        return afree - car.a * carList.sqr(sStar / s);

    }

    public double equilibriumVelocity(double vLow, double vHigh, int i, double distance) {
        double nextIteration;
        double n = 0;
        double epsilon = 1E-10;
        do {
            n++;
            nextIteration = vHigh - ((y(vHigh, i, distance) * (vHigh - vLow)) / (y(vHigh, i, distance) - y(vLow, i, distance)));
            if ((y(nextIteration, i, distance) * y(vLow, i, distance)) < 0) {
                vHigh = nextIteration;
            } else {
                vLow = nextIteration;
            }
        }
        while (Math.abs(y(nextIteration, i, distance)) >= epsilon);
        System.out.println("Method of secants converged after " + n + " steps");
        return nextIteration;
    }
}
