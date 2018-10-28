package org.art.rx.devoxx.sample;

import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;

public class Sample {

    public static void main(String[] args) throws InterruptedException {
        List<String> symbols = Arrays.asList("GOOG", "AAPL", "MSFT", "INTC");
        Observable<StockInfo> feed = StockServer.getFeed(symbols);
        System.out.println("Got observable!");
        feed.map(stockInfo -> new StockInfo(stockInfo.getTicket(), stockInfo.getPrice() * 0.9))
                .filter(stockInfo -> stockInfo.getPrice() > 60)
                .onErrorResumeNext(throwable -> callABack(throwable, symbols))
                .subscribeOn(Schedulers.io())
                .share();

        //Subscriber 1
        feed.subscribe(Sample::printStockInfo, Sample::handleError, () -> System.out.println("DONE!"));

        Thread.sleep(5000);

        //Subscriber 2 (will share the same observable)
        feed.subscribe(Sample::printStockInfo, Sample::handleError, () -> System.out.println("DONE!"));

        System.out.println("After subscription! Async mode...");

        Thread.sleep(10000);
    }

    private static Observable<? extends StockInfo> callABack(Throwable throwable, List<String> symbols) {
        System.out.println(throwable);
        return StockServer.getFeed(symbols);
    }

    private static void handleError(Throwable throwable) {
        System.out.println(throwable);
    }

    private static void printStockInfo(StockInfo stockInfo) {
        System.out.println("Thread: " + Thread.currentThread());
        System.out.println(stockInfo);
    }
}
