package org.art.rx.reilly;

import io.reactivex.Observable;
import org.art.rx.reilly.client.RssStreamClient;
import org.art.rx.reilly.model.RssArticle;

import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        RssStreamClient client = RssStreamClient.getRssStreamClient();
        Observable<RssArticle> rssArticleObservable = client.getRssArticleObservable();
        rssArticleObservable.subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(60);
    }
}
