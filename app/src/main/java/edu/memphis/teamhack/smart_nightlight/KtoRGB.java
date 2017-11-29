package edu.memphis.teamhack.smart_nightlight;

/**
 * Created by Daniel on 11/28/2017.
 */

public class KtoRGB {
    /**
     * Convert color temperature in Kelvins to RGB color for AWT
     *
     * @param temperature
     * @return ready to use color object
     */
//    public static double[] getRGBFromK(int temperature) {
//        // Used this: https://gist.github.com/paulkaplan/5184275 at the beginning
//        // based on http://stackoverflow.com/questions/7229895/display-temperature-as-a-color-with-c
//        // this answer: http://stackoverflow.com/a/24856307
//        // (so, just interpretation of pseudocode in Java)
//
//        double x = temperature / 1000.0;
//        if (x > 40) {
//            x = 40;
//        }
//        double red;
//        double green;
//        double blue;
//
//        // R
//        if (temperature < 6527) {
//            red = 1;
//        } else {
//            double[] redpoly = {4.93596077e0, -1.29917429e0,
//                1.64810386e-01, -1.16449912e-02,
//                4.86540872e-04, -1.19453511e-05,
//                1.59255189e-07, -8.89357601e-10};
//            red = poly(redpoly, x);
//
//        }
//        // G
//        if (temperature < 850) {
//            green = 0;
//        } else if (temperature <= 6600) {
//            double[] greenpoly = {-4.95931720e-01, 1.08442658e0,
//                -9.17444217e-01, 4.94501179e-01,
//                -1.48487675e-01, 2.49910386e-02,
//                -2.21528530e-03, 8.06118266e-05};
//            green = poly(greenpoly, x);
//        } else {
//            double[] greenpoly = {3.06119745e0, -6.76337896e-01,
//                8.28276286e-02, -5.72828699e-03,
//                2.35931130e-04, -5.73391101e-06,
//                7.58711054e-08, -4.21266737e-10};
//
//            green = poly(greenpoly, x);
//        }
//        // B
//        if (temperature < 1900) {
//            blue = 0;
//        } else if (temperature < 6600) {
//            double[] bluepoly = {4.93997706e-01, -8.59349314e-01,
//                5.45514949e-01, -1.81694167e-01,
//                4.16704799e-02, -6.01602324e-03,
//                4.80731598e-04, -1.61366693e-05};
//            blue = poly(bluepoly, x);
//        } else {
//            blue = 1;
//        }
//
//        red = clamp(red, 0, 1);
//        blue = clamp(blue, 0, 1);
//        green = clamp(green, 0, 1);
//        double [] rgb = new double[3];
//        rgb[0]=red;
//        rgb[1]=green;
//        rgb[2]=blue;
//        return rgb;
//    }
//
//    public static double poly(double[] coefficients, double x) {
//        double result = coefficients[0];
//        double xn = x;
//        for (int i = 1; i < coefficients.length; i++) {
//            result += xn * coefficients[i];
//            xn *= x;
//
//        }
//        return result;
//    }
//
//    public static double clamp(double x, double min, double max) {
//        if (x < min) {
//            return min;
//        }
//        if (x > max) {
//            return max;
//        }
//        return x;
//    }
    private static double k2r(double temp) {

        temp = temp / 100;
        double red = 0;
        //calculate red
        if (temp <= 66) {
            red = 255;
        } else {
            red = temp - 60;
            red = 329.698727446 * (Math.pow(red, -0.1332047592));
            if (red < 0)
                red = 0;
            else if (red > 255)
                red = 255;
        }
        return red;
    }

    private static double k2g(double temp) {
        temp = temp / 100;
        double green = 0;
        //calculate green
        if (temp <= 66) {
            green = temp;
            green = 99.4708025861 * Math.log(green) - 161.1195681661;
            if (green < 0)
                green = 0;
            else if (green > 255)
                green = 255;
        } else {
            green = temp - 60;
            green = 288.1221695283 * Math.pow(green, -0.0755148492);
            if (green < 0)
                green = 0;
            else if (green > 255)
                green = 255;
        }
        return green;
    }

    private static double k2b(double temp) {
        temp = temp / 100;
        double blue = 0;
        //calculate blue
        if (temp >= 66)
            blue = 255;
        else {

            if (temp <= 19)
                blue = 0;
            else {
                blue = temp - 10;
                blue = 138.5177312231 * Math.log(blue) - 305.0447927307;
                if (blue < 0)
                    blue = 0;
                else if (blue > 255)
                    blue = 255;
            }

        }
        return blue;
    }

    public static double[] k2rgb(double kelvin) {
        double rgb[] = new double[3];
        double r = k2r(kelvin);
        double g = k2g(kelvin);
        double b = k2b(kelvin);

        rgb[0] = r;
        rgb[1] = g;
        rgb[2] = b;
        return rgb;

    }

    //only works with 0-255 RGB arrays
    public static String rgb2hex(double [] rgb){
        Double RGB[]=new Double[3];

        RGB[0] = Double.valueOf(rgb[0]);
        RGB[1] = Double.valueOf(rgb[1]);
        RGB[2] = Double.valueOf(rgb[2]);

        int r = RGB[0].intValue();
        int g = RGB[1].intValue();
        int b = RGB[2].intValue();

        Integer R = Integer.valueOf(r);
        Integer G = Integer.valueOf(g);
        Integer B = Integer.valueOf(b);

        String rHex = Integer.toHexString(R);
        String gHex = Integer.toHexString(G);
        String bHex = Integer.toHexString(B);

        if(rHex.length()==1)
            rHex = "0"+rHex;
        if(gHex.length()==1)
            gHex = "0"+gHex;
        if(bHex.length()==1)
            bHex = "0"+bHex;
        return rHex+gHex+bHex;
    }
    public static String k2hex(double temperature)
    {
        return rgb2hex((k2rgb(temperature)));
    }
}
