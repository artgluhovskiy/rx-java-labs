package org.art.rx.devoxx.sample;

import java.util.Random;

public class StockPriceGenerator {

    private static Random rnd = new Random(System.nanoTime());

    public static double generatePrice() {
        try {
            //Some work here
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Math.round(rnd.nextDouble() * 1000) / (double) 10;
    }
}
