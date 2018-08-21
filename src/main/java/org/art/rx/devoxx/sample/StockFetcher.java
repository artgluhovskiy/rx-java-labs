package org.art.rx.devoxx.sample;

public class StockFetcher {

    public static StockInfo fetch(String symbol) {
        if (Math.random() > 0.9) {
            throw new RuntimeException("ohoh");
        }
        return new StockInfo(symbol, StockPriceGenerator.generatePrice());
    }
}
