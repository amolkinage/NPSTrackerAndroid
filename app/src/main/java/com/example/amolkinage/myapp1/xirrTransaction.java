package com.example.amolkinage.myapp1;

/**
 * Created by amolkinage on 21/11/15.
 */

import java.util.*;
import java.math.*;

public class xirrTransaction {

    public Date date;
    public double amount;


    // Calculates the resulting amount
    public static double irrResult(Vector<xirrTransaction> transactions,double rate)
    {
        double  r = rate + 1;
        double  result =  transactions.elementAt(0).amount;

        Date date0 = transactions.elementAt(0).date;

        for (int  i = 1; i < transactions.size(); i++) {
            double amount = transactions.elementAt(i).amount;
            Date datei = transactions.elementAt(i).date;
            double days = (double) ( (datei.getTime() - date0.getTime()) / (1000*60*60*24));
            result += amount / Math.pow(r, days / 365);
        }
        return result;
    }

    // Calculates the first derivation
    public static double irrResultDeriv(Vector<xirrTransaction> transactions,double rate)
    {
        double r = rate + 1;
        double  result = 0;
        Date date0 = transactions.elementAt(0).date;

        for (int  i = 1; i < transactions.size(); i++) {
            double amount = transactions.elementAt(i).amount;
            Date datei = transactions.elementAt(i).date;
            double days =  (double) ( (datei.getTime() - date0.getTime()) / (1000*60*60*24));
            double frac =  days / 365;
            result -= frac * amount / Math.pow(r, frac + 1);
        }
        return result;
    }
    public static double CalculateXIRR(Vector<xirrTransaction> transactions,double rate)
    {
        // Credits: algorithm inspired by Apache OpenOffice

        // Check that values contains at least one positive value and one negative value
        boolean positive = false;
        boolean negative = false;
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.elementAt(i).amount > 0) positive = true;
            if (transactions.elementAt(i).amount < 0) negative = true;
        }

        // Return error if values does not contain at least one positive value and one negative value
        if (!positive || !negative) return Double.NaN;

        // Initialize guess and resultRate
        double guess =  rate;
        double resultRate = guess;

        // Set maximum epsilon for end of iteration
        double epsMax = 1e-10;

        // Set maximum number of iterations
        int iterMax = 500;

        // Implement Newton's method
        double newRate, epsRate, resultValue;
        int iteration = 0;
        boolean contLoop = true;

        do {
            resultValue = irrResult(transactions, resultRate);
            newRate = resultRate - resultValue / irrResultDeriv(transactions, resultRate);
            epsRate = Math.abs(newRate - resultRate);
            resultRate = newRate;
            contLoop = (epsRate > epsMax) && (Math.abs(resultValue) > epsMax);
        } while(contLoop && (++iteration < iterMax));

        if(contLoop) {
            return Double.NaN;
        }

        // Return internal rate of return
        return resultRate;

    }
}
