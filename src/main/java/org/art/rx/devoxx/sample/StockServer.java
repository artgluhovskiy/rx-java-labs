package org.art.rx.devoxx.sample;

import rx.Observable;
import rx.Subscriber;

import java.util.List;

public class StockServer {

    public static Observable<StockInfo> getFeed(List<String> symbols) {
        return Observable.create(subscriber -> processRequest(subscriber, symbols));
    }

    private static void processRequest(Subscriber<? super StockInfo> subscriber, List<String> symbols) {
        System.out.println("Processing...");
        while (!subscriber.isUnsubscribed()) {
            symbols.stream()
                    .map(StockFetcher::fetch)
                    .forEach(subscriber::onNext);
        }
    }
}
