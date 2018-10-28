package org.art.rx.reilly;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class BasicRxTests {

    @Test
    @DisplayName("Synchronous message emitting")
    void test0() {
        Observable.create(s -> {
            System.out.println("I'm going to sleep for one second before emitting the message!");
            Thread.sleep(1000);
            s.onNext("Custom message...");
            s.onComplete();
        }).doOnNext(i -> System.out.println(Thread.currentThread()))
                .subscribe(System.out::println);
        System.out.println("Will print AFTER the message is emitted (sync)!");
    }

    @Test
    @DisplayName("Merging async observables into one observable")
    void test1() throws InterruptedException {
        Observable<String> a = Observable.create(s -> new Thread(() -> {
            s.onNext("one");
            s.onNext("two");
            s.onComplete();
        }).start());
        Observable<String> b = Observable.create(s -> new Thread(() -> {
            s.onNext("three");
            s.onNext("four");
            s.onComplete();
        }).start());
        Observable<String> c = a.mergeWith(b);
        Thread.sleep(500);
        c.subscribe(System.out::println);
    }

    @Test
    @DisplayName("Simple Single test")
    void test2() throws InterruptedException {
        Single<String> a = Single.<String>create(s -> {
            s.onSuccess("Data A");
        }).subscribeOn(Schedulers.io());
        Single<String> b = Single.just("Data B").subscribeOn(Schedulers.io());
        Flowable<String> c = a.mergeWith(b);
        Thread.sleep(500);
        c.subscribe(System.out::println);
    }

    @Test
    @DisplayName("Caching events")
    void test3() {
        Observable<Integer> ints = Observable.<Integer>create(subscriber -> {
            System.out.println("Creation...");
            subscriber.onNext(42);
            subscriber.onNext(18);
            subscriber.onComplete();
        }).cache();
        ints.subscribe(System.out::println);
        ints.subscribe(System.out::println);
    }

    @Test
    @DisplayName("Cold observable with interval")
    void test4() {
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(System.out::println);
    }


}
