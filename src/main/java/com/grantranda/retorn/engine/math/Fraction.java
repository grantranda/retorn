package com.grantranda.retorn.engine.math;

public class Fraction {

    private int numerator;
    private int denominator;

    public Fraction() {
        this(0, 1);
    }

    public Fraction(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public int getNumerator() {
        return numerator;
    }

    public void setNumerator(int numerator) {
        this.numerator = numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public void setDenominator(int denominator) {
        this.denominator = denominator;
    }

    public void set(int numerator, int denominator) {
        setNumerator(numerator);
        setDenominator(denominator);
    }

    public double getRatio() {
        return (double) numerator / denominator;
    }

    public Fraction simplify() {
        int gcd = gcd();
        if (gcd != 1) {
            numerator /= gcd;
            denominator /= gcd;
        }
        return this;
    }

    public int gcd() {
        return gcd(numerator, denominator);
    }

    public static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    public String toRatio() {
        return numerator + ":" + denominator;
    }

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }
}
