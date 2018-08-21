package org.art.rx.devoxx.sample;

public class StockInfo {

    private String ticket;
    private double price;

    public StockInfo(String symbol, double price) {
        this.ticket = symbol;
        this.price = price;
    }

    public String getTicket() {
        return ticket;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "StockInfo{" +
                "ticket='" + ticket + '\'' +
                ", price=" + price +
                '}';
    }
}
