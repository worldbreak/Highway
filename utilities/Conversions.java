package utilities;

public class Conversions {

    public static double Radians(int degrees) {
        return degrees * Math.PI / 180;
    }

    public static double KmHtoMS(double kmh) {
        return kmh / 3.6;
    }

    public static double MStoKmH(double ms) {
        return ms * 3.6;
    }

    public static String LaneToStr(int lane) {
        return (lane == 1) ? "right lane" : "left lane";
    }
}