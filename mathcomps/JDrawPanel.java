package mathcomps;

import utilities.Vectors;

import javax.swing.*;
import java.awt.*;

public class JDrawPanel extends JPanel {

    protected double[] O;
    protected double x0, y0, x1, y1;
    protected int w, h;
    protected double[] u, v;
    protected boolean initialized = false;

    public JDrawPanel() {
        setBackground(Color.BLACK);
    }

    public void setScale(double[] Origin, double[] A, double[] B, double xO, double yO, double xA, double yB) {
        O = Origin;
        u = Vectors.Multiply(Vectors.Vector(O, A), 1 / (xA - xO));
        v = Vectors.Multiply(Vectors.Vector(O, B), 1 / (yB - yO));      //(10,7)
        x0 = xO;
        y0 = yO;
        x1 = xA;
        y1 = yB;
        w = getWidth();
        h = getHeight();
        initialized = true;
    }


    public boolean isInitialized() {
        return initialized;
    }

    public int[] transform(double x, double y) {
        double[] vec = Vectors.LinComb(O, x - x0, u, y - y0, v);
        int[] P = {(int) vec[0], (int) vec[1]};
        return P;
    }

    private int[] lastPoint = new int[2];

    public void move(Graphics g, double x, double y) {
        lastPoint = transform(x, y);
    }

    public void line(Graphics g, double x, double y) {
        int[] P = transform(x, y);
        g.drawLine(lastPoint[0], lastPoint[1], P[0], P[1]);
        lastPoint[0] = P[0];
        lastPoint[1] = P[1];
    }

    public void fillRhomboid(Graphics g,
                             double a1, double b1,
                             double a2, double b2,
                             double a3, double b3,
                             double a4, double b4) {


        int[] A1 = transform(a1, b1);
        int[] A2 = transform(a2, b2);
        int[] A3 = transform(a3, b3);
        int[] A4 = transform(a4, b4);
        int[] xs = {A1[0], A2[0], A3[0], A4[0]};
        int[] ys = {A1[1], A2[1], A3[1], A4[1]};
        Polygon p = new Polygon(xs, ys, 4);
        g.fillPolygon(p);
    }

}