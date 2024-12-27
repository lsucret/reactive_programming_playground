package io.pivotal.literx;


//generic imports to help with simpler IDEs (ie tech.io)

import io.pivotal.literx.domain.User;
import org.junit.jupiter.api.Test;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Learn how to deal with errors.
 * https://github.com/reactor/lite-rx-api-hands-on/blob/master/src/test/java/io/pivotal/literx/Part07ErrorsTest.java
 * @author Sebastien Deleuze
 * @see Exceptions#propagate(Throwable)
 */
public class Part07Errors {

    //========================================================================================
    @Test
    void test07() throws InterruptedException {
        Mono<User> userMono = betterCallSaulForBogusMono(Mono.error(new NumberFormatException("test")))
//        Mono<User> userMono = betterCallSaulForBogusMono(Mono.just(User.JESSE))
                .doFirst(() -> System.out.println("start"))
                .doOnNext(user -> System.out.println(user.getFirstname()))
                .doOnCancel(() -> System.out.println("cancel"));

        userMono.subscribe();

        Thread.sleep(1000);
    }

    // TODO Return a Mono<User> containing User.SAUL when an error occurs in the input Mono, else do not change the input Mono.
    Mono<User> betterCallSaulForBogusMono(Mono<User> mono) {
        return mono.onErrorReturn(IllegalStateException.class, User.SAUL)
//                .onErrorReturn(NumberFormatException.class, User.SKYLER)
                ;
    }

//========================================================================================

    // TODO Return a Flux<User> containing User.SAUL and User.JESSE when an error occurs in the input Flux, else do not change the input Flux.
    Flux<User> betterCallSaulAndJesseForBogusFlux(Flux<User> flux) {
        return flux.onErrorResume((throwable -> Flux.just(User.SAUL, User.JESSE)));
    }

//========================================================================================

    @Test
    public void handleCheckedExceptions() {
        Flux<User> flux = capitalizeMany(Flux.just(User.SAUL, User.JESSE));

        StepVerifier.create(flux)
                .verifyError(Part07Errors.GetOutOfHereException.class);
    }

    // TODO Implement a method that capitalizes each user of the incoming flux using the
    //  #capitalizeUser method and emits an error containing a GetOutOfHereException error
    Flux<User> capitalizeMany(Flux<User> flux) {
        return flux
                .onErrorResume(GetOutOfHereException.class, (throwable) -> Flux.just(User.SAUL, User.JESSE))
                .map(user -> {
                    try {
                        return capitalizeUser(user);
                    } catch (GetOutOfHereException e) {
                        throw Exceptions.propagate(e);
                    }
                });
    }

    User capitalizeUser(User user) throws GetOutOfHereException {
        if (user.equals(User.SAUL)) {
            throw new GetOutOfHereException();
        }
        return new User(user.getUsername(), user.getFirstname(), user.getLastname());
    }

    protected final class GetOutOfHereException extends Exception {
        private static final long serialVersionUID = 0L;
    }

}
