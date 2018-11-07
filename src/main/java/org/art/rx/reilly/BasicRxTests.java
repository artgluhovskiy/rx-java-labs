package org.art.rx.reilly;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.concurrent.TimeUnit;

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

    @Test
    @DisplayName("Flating observables - uses concurrent subscriptions internally")
    void test5() throws InterruptedException {
        Observable.just(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
                .flatMap(this::loadRecordsFor)
                .subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    @DisplayName("Concatenating observables - doesn't use concurrent subscription internally")
    void test6() throws InterruptedException {
        Observable.just(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
                .concatMap(this::loadRecordsFor)
                .subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(5);
    }

    private Observable<String> loadRecordsFor(DayOfWeek day) {
        switch (day) {
            case SUNDAY:
                return Observable
                        .interval(90, TimeUnit.MILLISECONDS)
                        .take(5)
                        .map(i -> "Sun-" + i + " Thread-" + Thread.currentThread());
            case MONDAY:
                return Observable
                        .interval(90, TimeUnit.MILLISECONDS)
                        .take(5)
                        .map(i -> "Mon-" + i + " Thread-" + Thread.currentThread());
            default:
                return Observable.empty();
        }
    }

    @Test
    @DisplayName("ObservableFromArray, ObservableMap, ObservableSubscribeOn debugging - non blocking")
    void test7() {
        Observable.just("One", "Two", "Three")
                .map(s -> {
                    TimeUnit.SECONDS.sleep(1);
                    return s + "!";
                })
                .subscribeOn(Schedulers.computation())
                .subscribe(System.out::println);
    }

    @Test
    @DisplayName("ObservableFromArray, ObservableMap, ObservableSubscribeOn debugging - blocking")
    void test8() {
        Observable.just("One", "Two", "Three")
                .map(s -> {
                    TimeUnit.SECONDS.sleep(1);
                    return s + "!";
                })
                .forEach(s -> System.out.println("forEach: Thread: " + Thread.currentThread()));
    }

    @Test
    @DisplayName("Lazy computations - defer()")
    void test9() {
        Observable.defer(
                () -> Observable.just(compute())
        ).map(val -> val * 3);
    }

    private int compute() throws InterruptedException {
        System.out.println("Sleeping...");
        TimeUnit.SECONDS.sleep(5);
        return 1;
    }

    @Test
    @DisplayName("onErrorResumeNext - error handling - Observable backup")
    void test10() {
        Observable.just("One", "Two", "Three")
                .map(item -> {
                    if (item.startsWith("Tw")) {
                        throw new Exception();
                    }
                    return item + "!";
                })
                .onErrorResumeNext(Observable.just("No Problem"))
                .map(String::toUpperCase)
                .subscribe(System.out::println);
    }

    @Test
    @DisplayName("Lazy observables concatenation")
    void test11() {
        Observable.defer(() -> {
            System.out.println("First observable activation");
            return Observable.fromArray("One", "Two", "Three");
        }).concatWith(Observable.defer(() -> {
            System.out.println("Second observable activation");
            return Observable.just("Four", "Five");
        })).take(3)
        .subscribe(System.out::println);
    }
}
