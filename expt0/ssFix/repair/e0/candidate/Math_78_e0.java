import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.*;

/**
 * Created by IntelliJ IDEA.
 * User: igorevsukov
 * Date: Dec 27, 2009
 * Time: 11:21:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class Sample {
    protected ArrayList<Double> sample = new ArrayList<Double>();
    public ArrayList<Double> getSample() { return sample; }
    public double get(int i) { return sample.get(i); }
    public double set(int i, double v) { return sample.set(i, v); }
    public double remove(int i) { return sample.remove(i); }
    public Boolean add(double v) { return sample.add(v); }
    public Boolean remove(double v) { return sample.remove(v); }
    public int size() { return sample.size(); }

    public Sample(String filename) {
        loadFromFile(filename);

        calculateSpectrums();
    }

    protected void loadFromFile(String filename) {
        try {
            BufferedReader input = new BufferedReader(new FileReader(filename));
            this.getSample().clear();

            String line;
            while ((line = input.readLine()) != null) {
//                line.replace(',','.');
                this.add(new Double(line));
            }
            input.close();

            taum = size()/3;
            mean = null;
//            dispertion = null;
        }
        catch (FileNotFoundException nofileex) {
            nofileex.printStackTrace();
        }
        catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }


    protected double amplitudeSpectrum[];
    public double[] getAmplitudeSpectrum() { return amplitudeSpectrum; }
    protected double energySpectrum[];
    public double[] getEnergySpectrum() { return energySpectrum; }
    protected double phaseSpectrum[];
    public double[] getPhaseSpectrum() { return phaseSpectrum; }
    public void calculateSpectrums() {
        int count = 0;
        if (size() % 2 == 0) {
            count = size() / 2 - 1;
        } else {
            count = (size() - 1) / 2;
        }
        count++; //чтобы доходило до 0.5
        amplitudeSpectrum = new double[count];
        energySpectrum = new double[count];
        phaseSpectrum = new double[count];

//        final int count = amplitudeSpectrum.length;
        double freq = 0;
        double fstep = 0.5 / count;
        for (int i = 0; i < count; i++) {
            amplitudeSpectrum[i] = calcPk(i);
            energySpectrum[i] = calcP2k(i);
            phaseSpectrum[i] = calcPHIk(i);
           // freq += fstep;
        }
    }

    private Double mean = null;
    public double getMean(){
        if (mean == null) {
            double t  = 0;
            for(Double i : getSample()) t += i;
            t /= size();
            mean = new Double(t);
        }
        return mean;
    }
//    private Double dispertion = null;
//    public double getDispertion(){
//        if (dispertion == null)
//        {
//            double t  = 0;
//            double m = getMean();
//            for(Double i : getSample())
//                t += (i - m) * (i - m);
//            t /= (size() - 1.0);
//            t = Math.sqrt(t);
//            dispertion = new Double(t);
//        }
//        return dispertion;
//    }

    /**
     * @param k
     * @return коефициент Ак статистической оценки для периодического процесса
     *  с периодом Т и гармоникой 1/Т
     */
    protected double calcAk(double k){
        final int N = this.size();
        double t = 2 * k * Math.PI / N;
        if (k == 0) return getMean();
        double A = 0;
        for(int  i = 0; i < N; i++)
           A += get(i) * cos(t * (i+1));
        return 2 * A / N;
    }
    /**
     * @param k
     * @return коефициент Bк статистической оценки для периодического процесса
     *  с периодом Т и гармоникой 1/Т
     */
    protected double calcBk(double k){
        final int N = this.size();
        double t = 2 * k * Math.PI / N;
        double B = 0;
        for(int  i = 0; i < N; i++)
           B += get(i) * sin(t * (i+1));
        return 2 * B / N;
    }

    /**
     * @param k
     * @return Амплитуда периодического случ. процесса, записанного в тригонометрической форме
     */
    public double calcPk(double k ){
        double ak = calcAk(k);
        double bk = calcBk(k);
        return sqrt(ak * ak + bk * bk) / 2.0;
    }
    /**
     * @param k
     * @return Фазовый спектр дискретного преобразования Фурье 
     */
    public double calcPHIk(double k){
        double ak = calcAk(k);
        double bk = calcBk(k);
        return Math.atan(bk / ak);
    }
    /**
     * @param k
     * @return Энергетический спектр дискретного преобразования Фурье
     */
    public double calcP2k(double k){
        return pow(calcPk(k),2);
    }

    /**
     * @param tau
     * @return значение автоковариационной функции
     */
    private double calcAutoCovariation(int tau){
        double kTau = 0;
        double N = size();
        double m1 = 0, m2 = 0;
        for(int i = 0; i < (N - tau); i++){
            m1 += get(i);
            m2 += get(i+tau);
        }
        m1 /= (double)(N - tau);
        m2 /= (double)(N - tau);
        for(int i = 0; i < (N- tau); i++)
            kTau += (get(i) - m1) * (get(i+tau) - m2);
        kTau /= (double)(N - tau);
        return kTau;
    }
    /**
     * @param tau
     * @return значение автокореляционной функции
     */
    public double calcAutoCorrelation(int tau){
        double N = size();
        double kTau = 0;
        double d1 = 0, d2 = 0;
        double m1 = 0, m2 = 0;
        for(int i = 0; i < (N - tau); i++){
            m1 += get(i);
            m2 += get(i+tau);
        }
        m1 /= (double)(N - tau);
        m2 /= (double)(N - tau);
        for(int i = 0; i < (N - tau); i++){
            d1 += (get(i) - m1) * (get(i) - m1);
            d2 += (get(i+tau) - m2) * (get(i+tau) - m2);
        }
        d1 /= (double)(N - tau - 1);
        d2 /= (double)(N - tau - 1);
        for(int i = 0; i < (N- tau); i++)
            kTau += (get(i) - m1) * (get(i+tau) - m2);
        kTau /= (double)(N - tau);
        return kTau / sqrt(d1 * d2);
    }

    protected double taum;
    public double getTaum() { return taum; }
    public void setTaum(double taum) { this.taum = taum; }
    /**
     * @param tau
     * @return вычисление кореляционного окна
     */
    protected double calcAlfa(double tau){
        // TODO: переделате, в каждом варианте функция своя
        if (Math.abs(tau) > taum) return 0;

        int q = 2;
//        return 1 - Math.pow(Math.abs(tau)/taum, q);
        return 1.0/(1.0 + pow(Math.abs(tau)/taum,q));
    }

    /**
     * @param f
     * @return сглаженная выборочная оценка спектральной плотности
     */
    public  double calcSpectrumDensity(double f){
        //поскольку шаг(h) у нас = 1
        double S = 0;
//        int L = 5;
        int L = (int) taum;
        for(int tau = 1; tau < L; tau++)
            S += calcAutoCovariation(tau) * calcAlfa(tau) * cos(2 * Math.PI * tau * f);
        S *= 4;
        S += 2 * calcAutoCovariation(0);
        return S;
    }
    /**
     * @param f
     * @return сглаженная выборочная оценка нормированной спектральной плотности
     */
    public double calcNormalizedSpectrumDensity(double f){
        double S = 0;
//        int L = 5;
        int L = (int)taum;
        for(int tau = 1; tau < L; tau++)
            S += calcAutoCorrelation(tau) * calcAlfa(tau) * Math.cos(2 * Math.PI * tau * f);
        S *= 4;
        S += 2;
        return S;
    }

    /**
     * вычисляет передаточную функцию
     * @param M порядок фильтра
     * @param T интервал, с которым используется выборка, с.
     * @param freq массив частот, в которых необходимо вычислить передаточную функцию
     * @param a матрица параметров a
     * @param b вектор параметров b
     * @return значение передаточной функции
     */
    public static double[] CalcABZ( int M, double T, double[] freq, double[][] a, double[]  b ){
            int npo = freq.length;

            int MyM;
            if (M% 2 == 0) MyM = M / 2;
            else MyM = (M+1) / 2;

            double fact = 2*Math.PI/T;

            int ip = M - MyM;

            double[] abz = new double[npo];
            for (int q = 0; q < npo; q++) {
                double s1 = Math.sin(freq[q] * fact);
                double c1 = Math.cos(freq[q] * fact);
                double s2 = Math.sin(2 * freq[q] * fact);
                double c2 = Math.cos(2 * freq[q] * fact);

                double absa = 1;
                for (int j = 0; j < ip; j++) {
                    double ar = b[0] + b[1] * c1 + b[2] * c2;
                    double ai = -b[1] * s1 - b[2] * s2;
                    double anm = ar * ar + ai * ai;

                    ar = a[0][j] * c1 + a[1][j] * c2 + 1; //p? p?
                    ai = -a[0][j] * s1 - a[1][j] * s2;    //p? p?

                    absa *= anm / (ar * ar + ai * ai);
                }
                abz[q] = absa;
            }

            return abz;
    }


    //Sinus LOW
    protected double aSL[][];
    /**
     * @return параметры a для синусного фильтра высоких частот Баттеруорта
     */
    public double[][] getASL() { return aSL; }
    /**
     * вычисляет параметры a для синусного фильтра высоких частот Баттеруорта
     * @param M порядок фильтра
     * @param T интервал, с которым используется выборка, с.
     * @param B частота отсекания фильтра
     * @return параметры a для синусного фильтра высоких частот Баттеруорта
     */
    protected double[][] calcASL(int M, double T, double B) {
        int MyM;
        if (M % 2 == 0) MyM = M / 2;
        else MyM = (M+1) / 2;
        aSL = new double[2][MyM];

        double fact = sin(B*T*Math.PI);
        double sector = Math.PI/M;
        double wedge = sector/2;
        double fn = 0;
        double F = 1;

        for (int i = 1; i <= M/2; i++ ) {
            fn = i - 1;
            double U = fact* sin(fn*sector + wedge);

            double C = 1 - fact*fact;
            double D = (-C + sqrt(C*C + 4*U*U))/2;
            double E = sqrt(D + 1) + sqrt(D);
            double G = 2*(2*U*U/D - 1)/E/E;
            double H = -pow(E, -4);
            F = F*(1 - G - H);

            aSL[0][i-1] = -G;
            aSL[1][i-1] = -H;
        }

        if (M % 2 != 0) {
            aSL[0][(M + 1)/2 - 1] = -(2* pow(fact,2) + 1 - 2*fact* sqrt(fact*fact + 1));
            aSL[1][(M + 1)/2 - 1] = 0;
        }
        return getASL();
    }

    protected double bSL[];
    /**
     * @return параметры b для синусного фильтра высоких частот Баттеруорта
     */
    public double[] getBSL() { return bSL; }
    /**
     * вычисляем параметры b для синусного фильтра высоких частот Баттеруорта
     * @param M порядок фильтра
     * @param T интервал, с которым используется выборка, с.
     * @param B частота отсекания фильтра
     * @return параметры b для синусного фильтра высоких частот Баттеруорта
     */
    protected double[] calcBSL(int M, double T, double B) {
        double pi = Math.PI;

        bSL = new double[3];

        double fact = Math.sin(B*T*pi);
        double sector = pi/M;
        double wedge = sector/2;
        double fn;
        double F = 1;

        for (int i = 1; i <= M / 2; i++ ) {
            fn = i - 1;
            double U = fact*Math.sin(fn*sector + wedge);

            double C = 1 - fact*fact;
            double D = (-C + sqrt(C*C + 4*U*U))/2;
            double E = sqrt(D + 1) + sqrt(D);
            double G = 2*(2*U*U/D - 1)/E/E;
            double H = -pow(E, -4);
            F = F*(1 - G - H);
        }


        if (M % 2 != 0)
            F = F * (1 - (2 * fact * fact + 1 - 2 * fact * sqrt(fact * fact + 1)));

        double b0 = pow(F, 2.0/M);
        bSL[0] = b0;
        return getBSL();
    }

    protected double SL[];
    /**
     * @return значение отфильтрованой выборки по методу синусного фильтра высоких частот Баттеруорта
     */
    public double[] getSinusLow() { return SL; }
    /**
     * вычисляет значение отфильтрованого процесса методом синусного фильтра высоких частот Баттеруорта
     * @param M порядок фильтра
     * @param T интервал, с которым используется выборка, с.
     * @param B частота отсекания фильтра
     * @return значение отфильтрованой выборки по методу синусного фильтра высоких частот Баттеруорта
     */
    public double[] calcSinusLow(int M, double T, double B) {
        SL = new double[size()];
        final int n = size();
        int MyM;
        if (M% 2 == 0)
            MyM = M / 2;
        else
            MyM = (M+1) / 2;

        double[][] a = calcASL(M, T, B);
        double[] b = calcBSL(M, T, B);
        double[][] y = new double[n][MyM];
        for (int i=0; i<n; i++) { y[i][0] = get(i); }

        for (int p=1; p<MyM; p++) {
            y[0][p] = b[0]*y[0][p - 1];
            y[1][p] = b[0]*y[1][p - 1] - a[0][p]*y[0][p];

            for (int i=2; i<n; i++) {
                y[i][p] = b[0]*y[i][p - 1] - a[0][p]*y[i - 1][p] - a[1][p]*y[i - 2][p];
            }
        }
        for (int i=0; i<n;i++) {
            SL[i] = y[i][MyM - 1];
        }
        return getSinusLow();
    }


    //Sinus High
    protected double aSH[][];
    /**
     * @return параметры a для синусного фильтра высоких частот Баттеруорта
     */
    public double[][] getASH() { return aSH; }
    /**
     * вычисляет параметры a для синусного фильтра высоких частот Баттеруорта
     * @param M порядок фильтра
     * @param T интервал, с которым используется выборка, с.
     * @param B частота отсекания фильтра
     * @return параметры a для синусного фильтра высоких частот Баттеруорта
     */
    protected double[][] calcASH(int M, double T, double B) {
        double pi = Math.PI;
        int MyM;
        if (M% 2 == 0) MyM = M / 2;
        else MyM = (M+1) / 2;
        
        aSH = new double[2][MyM];

        double fact = Math.sin(B * T * pi);
        double sector = pi / M;
        double wedge = sector / 2;
        double fn = 0;
        double F = 1;

        for (int i = 1; i <= M / 2; i++) {
            fn = i - 1;
            double U = fact * Math.sin(fn * sector + wedge);

            double C = 1 - fact * fact;
            double D = (-C + sqrt(C * C + 4 * U * U)) / 2;
            double E = sqrt(D + 1) + sqrt(D);
            double G = 2 * (2 * U * U / D - 1) / E / E;
            double H = -pow(E, -4);
            F = F * (1 - G - H);

            aSH[0][i-1] = G;
            aSH[1][i-1] = -H;
        }

        if (M % 2 != 0) {
            aSH[0][(M + 1) / 2 - 1] = -(2 * fact * fact + 1 - 2 * fact * sqrt(fact * fact + 1));
            aSH[1][(M + 1) / 2 - 1] = 0;
        }

        return this.getASH();
    }

    protected double bSH[];
    /**
     * @return параметры b для синусного фильтра высоких частот Баттеруорта
     */
    public double[] getBSH() { return bSH; }
    /**
     * вычисляем параметры b для синусного фильтра высоких частот Баттеруорта
     * @param M порядок фильтра
     * @param T интервал, с которым используется выборка, с.
     * @param B частота отсекания фильтра
     * @return параметры b для синусного фильтра высоких частот Баттеруорта
     */
    protected double[] calcBSH(int M, double T, double B) {
        double pi = Math.PI;

        bSH = new double[3];
        int MyM;
        if (M % 2 == 0) MyM = M / 2;
        else MyM = (M+1) / 2;

        double fact = Math.sin(B * T * pi);
        double sector = pi / M;
        double wedge = sector / 2;
        double fn = 0;
        double F = 1;

        for (int i = 1; i <= M / 2; i++) {
            fn = i - 1;
            double U = fact * Math.sin(fn * sector + wedge);

            double C = 1 - fact * fact;
            double D = (-C + sqrt(C * C + 4 * U * U)) / 2;
            double E = sqrt(D + 1) + sqrt(D);
            double G = 2 * (2 * U * U / D - 1) / E / E;
            double H = -pow(E, -4);
            F = F * (1 - G - H);
        }
        if (M % 2 != 0)
            F = F * (1 - (2 * fact * fact + 1 - 2 * fact * sqrt(fact * fact + 1)));
        double b0 = M/2%2 == 0 ? pow(F, 2.0/M) : pow(F, 2.0/(M + 1));
        bSH[0] = b0;

        return getBSH();
    }

    protected double SH[];
    /**
     * @return значение отфильтрованой выборки по методу синусного фильтра высоких частот Баттеруорта
     */
    public double[] getSinusHigh() { return SH; }
    /**
     * вычисляет значение отфильтрованого процесса методом синусного фильтра высоких частот Баттеруорта
     * @param M порядок фильтра
     * @param T интервал, с которым используется выборка, с.
     * @param B частота отсекания фильтра
     * @return значение отфильтрованой выборки по методу синусного фильтра высоких частот Баттеруорта
     */
    public double[] calcSinusHigh(int M, double T, double B) {
        this.SH = new double[size()];
        final int n = size();

        double[][] a = calcASH(M, T, B);
        double[] b = calcBSH(M, T, B);
        int MyM;
        if (M% 2 == 0) MyM = M / 2;
        else MyM = (M+1) / 2;
        double[][] y = new double[n][MyM];
        for (int i = 0; i < n; i++) {
            y[i][0] = get(i);
        }

        for (int p = 1; p < MyM; p++) {
            y[0][p] = b[0] * y[0][p - 1];
            y[1][p] = b[0] * y[1][p - 1] - a[0][p] * y[0][p];

            for (int i = 2; i < n; i++) {
                y[i][p] = b[0] * y[i][p - 1] - a[1][p] * y[i - 1][p] - a[1][p] * y[i - 2][p];
            }
        }

        for (int i = 0; i < n; i++) {
            SH[i] =  y[i][MyM - 1];
        }

        return getSinusHigh();
    }


    //obscured filter
    protected double aOF[][];
    /**
     * @return параметры a для загораживающего фильтра
     */
    public double[][] getAOF() { return aOF; }
    /**
     * вычисляет параметры a для загораживающего фильтра
     * @param M порядок фильтра
     * @param T интервал, с которым используется выборка, с.
     * @param B частота отсекания фильтра
     * @param Fc
     * @return параметры a для загораживающего фильтра
     */
    protected double[][] calcAOF(int M, double T, double B, double Fc) {
        double pi = Math.PI;

        aOF = new double[2][M];

        double fact = B * T * pi;
        double ang2 = 2 * pi * Fc * T;
        double Q = Math.cos(ang2) * Math.cos(fact);
        double S = Math.sin(ang2) * Math.sin(fact);

        double sector = 2 * pi / M;
        double wedge = sector / 2;
        double fn = 0;
//        double[][] b = new double[3][M];
        int MyM;
        if (M% 2 == 0) MyM = M / 2;
        else MyM = (M+1) / 2;
        for (int i = 1; i <= MyM; i++) {
            fn = i - 1;
            double ang = fn * sector + wedge;
            double A = S * Math.cos(ang) + Q;
            double U = S * Math.sin(ang);

            double C = 1 - A * A - U * U;

            double D = (-C + sqrt(C * C + 4 * U * U)) / 2;
            double E = sqrt(D + 1) + sqrt(D);
            double G = 2 * sqrt(1 - U * U / D) / E;
            double H = -pow(E, -2);
            if (A < 0) G = -G;

            aOF[0][i - 1] = -G;
            aOF[1][i - 1] = -H;
        }

        return this.getAOF();
    }

    protected double bOF[];
    /**
     * @return параметры b для загораживающего фильтра
     */
    public double[] getBOF() { return bOF; }
    /**
     * вычисляем параметры b для загораживающего фильтра
     * @param M порядок фильтра
     * @param T интервал, с которым используется выборка, с.
     * @param B частота отсекания фильтра
     * @param Fc
     * @return параметры b для загораживающего фильтра
     */
    protected double[] calcBOF(int M, double T, double B, double Fc) {
        double pi = Math.PI;
        double fact = B * T * pi;
        double ang2 = 2 * pi * Fc * T;
        double Q = Math.cos(ang2) * Math.cos(fact);
        bOF = new double[3];
        bOF[0] = 1;
        bOF[1] = -2 * Q;
        bOF[2] = 1;

        return getBOF();
    }

    protected double OF[];
    /**
     * @return значение отфильтрованой выборки по методу загораживающего фильтра
     */
    public double[] getObscuredFilter() { return OF; }
    /**
     * вычисляет значение отфильтрованого процесса методом загораживающего фильтра
     * @param M порядок фильтра
     * @param T интервал, с которым используется выборка, с.
     * @param B частота отсекания фильтра
     * @param Fc
     * @return значение отфильтрованой выборки по методу загораживающего фильтра
     */
    public double[] calcObscuredFilter(int M, double T, double B, double Fc) {
        int n = size();
        OF = new double[n];

        double pi = Math.PI;

        double[][] a = calcAOF(M, T, B, Fc);

        double fact = B * T * pi;
        double ang2 = 2 * pi * Fc * T;
        double Q = Math.cos(ang2) * Math.cos(fact);

        double[] b = calcBOF(M, T, B, Fc).clone();

        double[] freq = new double[] { 0, 1.0 / 2 / T };
        double[] abz = Sample.CalcABZ(M, T, freq, a, b);
        double htran = Math.max(abz[0], abz[1]);

        
        b[0] = pow(htran, -1.0 / M);
        b[1] = -2 * Q * pow(htran, -1.0/ M);
        b[2] = pow(htran, -1.0 / M);
        int MyM;
        if (M% 2 == 0) MyM = M / 2;
        else MyM = (M+1) / 2;
        double[][] y = new double[n][MyM];

        for (int i = 0; i < n; i++) y[i][0] = get(i);
        

        for (int p = 1; p < MyM; p++) {
            y[0][p] = b[0] * y[0][p - 1];
            y[1][p] = b[0] * y[1][p - 1] - a[0][p] * y[0][p];

            for (int i = 2; i < n; i++) {
                y[i][p] = b[0] * y[i][p - 1] + b[1] * y[i - 1][p - 1] + b[2] * y[i - 2][p - 1] - a[0][p] * y[i - 1][p] - a[1][p] * y[i - 2][p];
            }
        }

        for (int i = 0; i < n; i++) OF[i] = y[i][MyM - 1];

        return getObscuredFilter();
    }


    //pass filter
    protected double aPF[][];
    /**
     * @return параметры a для пропускающего фильтра
     */
    public double[][] getAPF() { return aPF; }
    /**
     * вычисляет параметры a для пропускающего фильтра
     * @param M порядок фильтра
     * @param T интервал, с которым используется выборка, с.
     * @param B частота отсекания фильтра
     * @param Fc
     * @return параметры a для пропускающего фильтра
     */
    protected double[][] calcAPF(int M, double T, double B, double Fc) {
        double pi = Math.PI;
        int MyM;
        if (M% 2 == 0) MyM = M / 2;
        else MyM = (M+1) / 2;
        aPF = new double[2][M];

        double fact = B * T * pi;
        double ang2 = 2*pi*Fc*T;
        double Q = Math.cos(ang2)*Math.cos(fact);
        double S = Math.sin(ang2)*Math.sin(fact);

        double sector = 2*pi / M;
        double wedge = sector / 2;
        double fn;

        for (int i = 1; i <= MyM; i++) {
            fn = i - 1;
            double ang = fn*sector + wedge;
            double A = S*Math.cos(ang) + Q;
            double U = S*Math.sin(ang);

            double C = 1 - A*A - U*U;

            double D = (-C + sqrt(C * C + 4 * U * U)) / 2;
            double E = sqrt(D + 1) + sqrt(D);
            double G = 2* sqrt(1 - U*U/D)/E;
            double H = -pow(E, -2);
            if (A<0) G = -G;
            aPF[0][i-1] = -G;
            aPF[1][i-1] = -H;
        }

        return getAPF();
    }

    protected double bPF[];
    /**
     * @return параметры b для загораживающего фильтра
     */
    public double[] getBPF() { return bPF; }
    /**
     * вычисляем параметры b для загораживающего фильтра
     * @param M порядок фильтра
     * @param T интервал, с которым используется выборка, с.
     * @param B частота отсекания фильтра
     * @param Fc центр ширины частоты B
     * @return параметры b для загораживающего фильтра
     */
    protected double[] calcBPF(int M, double T, double B, double Fc) {
        bPF = new double[3];
        bPF[0] = 1;
        //    double[] freq = new double[] { Math.acos(Q) / 2.0 / pi / T };
        //     double[] abz = ComputeABZ(M, n, T, freq, a, bPF);

        //    htran = abz[0];


        //    double bVal = Math.pow(htran, -1.0/M);

        //      for (int i=0; i<M/2; i++)
        //  {
        //      bPF[0][i] = bVal;
        //  }

        return getBPF();
    }

    protected double PF[];
    /**
     * @return значение отфильтрованой выборки по методу загораживающего фильтра
     */
    public double[] getPassFilter() { return PF; }
    /**
     * вычисляет значение отфильтрованого процесса методом загораживающего фильтра
     * @param M порядок фильтра
     * @param T интервал, с которым используется выборка, с.
     * @param B частота отсекания фильтра
     * @param Fc
     * @return значение отфильтрованой выборки по методу загораживающего фильтра
     */
    public double[] calcPassFilter(int M, double T, double B, double Fc) {
        int n = size();
        PF = new double[n];

        double pi = Math.PI;

        double[][] a = calcAPF(M, T, B, Fc);
        double[] b = calcBPF(M, T, B, Fc);

        double fact = B * T * pi;
        double ang2 = 2*pi*Fc*T;
        double Q = Math.cos(ang2)*Math.cos(fact);

        double htran = 0;

        double[] freq = new double[] { Math.acos(Q) / 2.0 / pi / T };
        double[] abz = Sample.CalcABZ(M, T, freq, a, b);

        htran = abz[0];

        double bVal = pow(htran, -1.0/M);
        int MyM;
        b[0] = bVal;
        if (M% 2 == 0) MyM = M / 2;
        else MyM = (M+1) / 2;
        double[][] y = new double[n][MyM];
        for (int i = 0; i < n; i++) {
            y[i][0] = get(i);
        }

        for (int p = 1; p < MyM; p++) {
            y[0][p] = bVal * y[0][p - 1];
            y[1][p] = bVal * y[1][p - 1] - a[0][p] * y[0][p];

            for (int i = 2; i < n; i++) {
                y[i][p] = bVal * y[i][p - 1] - a[0][p] * y[i - 1][p] - a[1][p] * y[i - 2][p];
            }
        }

        for (int i = 0; i < n; i++) {
            PF[i] = y[i][MyM - 1];
        }

        return getPassFilter();
    }
}
