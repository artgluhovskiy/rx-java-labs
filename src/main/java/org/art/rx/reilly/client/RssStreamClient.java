package org.art.rx.reilly.client;

import io.reactivex.Observable;
import lombok.extern.log4j.Log4j2;
import org.art.rx.reilly.model.RssArticle;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * Rss Article Stream Client implementation.
 * Uses Spring Webflux {@link WebClient} internally
 * to connect to the RSS streaming server. Is considered
 * as a holder of a {@link Flux} instance, which emits
 * RSS articles and can be adapted to RxJava {@link Observable}.
 */
@Log4j2
public class RssStreamClient {

    private static final String RSS_STREAM_ENDPOINT = "http://localhost:8080/rss/stream";

    private final Flux<RssArticle> rssFlux;

    private RssStreamClient() {
        log.info("Rss Streaming Client initialization. RSS stream endpoint: {}", RSS_STREAM_ENDPOINT);
        this.rssFlux = initRssArticleFlux();
    }

    private static class ClientHolder {
        static final RssStreamClient RSS_STREAM_CLIENT_INSTANCE = new RssStreamClient();
    }

    public static RssStreamClient getRssStreamClient() {
        return ClientHolder.RSS_STREAM_CLIENT_INSTANCE;
    }

    private Flux<RssArticle> initRssArticleFlux() {
        return WebClient.create(RSS_STREAM_ENDPOINT)
                .get()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_STREAM_JSON_VALUE)
                .retrieve()
                .bodyToFlux(RssArticle.class);
    }

    public Flux<RssArticle> getRssFlux() {
        return rssFlux;
    }

    public Observable<RssArticle> getRssArticleObservable() {
        Observable<RssArticle> rssObservable = null;
        if (rssFlux != null) {
            rssObservable = Observable.fromPublisher(rssFlux);
        }
        return rssObservable;
    }
}

