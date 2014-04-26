import java.io.*;
import java.util.Scanner;

public class FileCreator {

    private String gnuFile;
    private PrintWriter out;
    private PrintWriter densityFile;
    private PrintWriter remainingCars;

    public void initialize(CarArrayList carList, String gnuFileName) {
        try {
            gnuFile = gnuFileName;
            FileWriter outFileRemaining = new FileWriter("remaining");
            FileWriter outFilePositions = new FileWriter(gnuFileName + ".pos");
            PrintWriter outPositions = new PrintWriter(outFilePositions);
            outPositions.println("reset");
            outPositions.println("set style data lines");
            outPositions.print("plot ");
            for (int i = 0; i < carList.getCount(); i++) {
                outPositions.print("'" + gnuFileName + "' using 1:" + (i + 2) + " notitle");
                if (i < carList.getCount() - 1) {
                    outPositions.print(", ");
                }
            }
            outPositions.close();
            //
            FileWriter outFile = new FileWriter(gnuFileName);
            out = new PrintWriter(outFile);
            remainingCars = new PrintWriter(outFileRemaining);
        } catch (Exception e) {
            System.out.println("Error creating " + gnuFile);
        }

    }

    public void finalize() {
        out.close();
    }

    public void writeStatus(CarArrayList carArrayList) {
        remainingCars.println("time: "+carArrayList.time+" rem: "+carArrayList.remaining[2]+" gen:"+carArrayList.numCars+ " carsremain: "+carArrayList.remaining[0]);
        out.print(carArrayList.time + ";");
        for (int i = 0; i < carArrayList.getCount(); i++) {
            CarArrayList.CarInfo cari = (CarArrayList.CarInfo) carArrayList.cars.get(i);
            out.print(cari.id+";"+cari.x + ";");
        }
        out.println();
    }


}
