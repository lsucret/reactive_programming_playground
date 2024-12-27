package io.pivotal.literx.repository;

import io.pivotal.literx.domain.User;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactiveUserRepository implements ReactiveRepository<User> {
    @Override
    public Mono<Void> save(Publisher<User> publisher) {
        return null;
    }

    @Override
    public Mono<User> findFirst() {
        return Mono.just(User.SKYLER);
    }

    @Override
    public Flux<User> findAll() {
        return Flux.just(User.SKYLER, User.JESSE, User.WALTER, User.SAUL);
    }

    @Override
    public Mono<User> findById(String id) {
        return null;
    }
}
