package io.pivotal.literx;

//generic imports to help with simpler IDEs (ie tech.io)

import java.util.*;
import java.util.function.*;
import java.time.*;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.BlockingRepository;
import io.pivotal.literx.repository.BlockingUserRepository;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

/**
 * Learn how to call blocking code from Reactive one with adapted concurrency strategy for
 * blocking code that produces or receives data.
 * <p>
 * For those who know RxJava:
 * - RxJava subscribeOn = Reactor subscribeOn
 * - RxJava observeOn = Reactor publishOn
 * - RxJava Schedulers.io <==> Reactor Schedulers.elastic
 *
 * @author Sebastien Deleuze
 * @see Flux#subscribeOn(Scheduler)
 * @see Flux#publishOn(Scheduler)
 * @see Schedulers
 */
public class Part11BlockingToReactive {
    /**
     * 가장 큰 질문은 "레거시, 반응하지 않는 코드를 어떻게 처리할 것인가?"입니다.
     * <p>
     * 차단 코드(예: 데이터베이스에 대한 JDBC 연결)가 있고, 성능에 너무 큰 영향을 주지 않으면서 이를 반응형 파이프라인에 통합하고 싶다고 가정해 보겠습니다.
     * <p>
     * 가장 좋은 방법은 스케줄러를 통해 코드의 본질적으로 차단되는 부분을 자체 실행 컨텍스트로 격리하여 나머지 파이프라인의 효율성을 높게 유지하고 꼭 필요한 경우에만 추가 스레드를 생성하는 것입니다.
     * <p>
     * JDBC 예제에서는 fromItable 팩토리 메서드를 사용할 수 있습니다. 하지만 파이프라인의 나머지 부분을 차단하기 위해 호출을 어떻게 방지할 수 있을까요?
     */
//========================================================================================

    @Test
    public void slowPublisherFastSubscriber() throws InterruptedException {
        BlockingUserRepository repository = new BlockingUserRepository();
        Flux<User> flux = blockingRepositoryToFlux(repository);
        Thread.sleep(1000);
//        assertThat(repository.getCallCount())
//                .withFailMessage("The call to findAll must be deferred until the flux is subscribed")
//                .isEqualTo(0);
        StepVerifier.create(flux)
                .expectNext(User.SKYLER, User.JESSE, User.WALTER, User.SAUL)
                .verifyComplete();
    }

    // TODO Create a Flux for reading all users from the blocking repository deferred until the flux is subscribed, and run it with a bounded elastic scheduler
    Flux<User> blockingRepositoryToFlux(BlockingRepository<User> repository) {
        // Flux.defer로 처리하기
        return Flux.defer(() -> Flux.fromIterable(repository.findAll()))
                .publishOn(Schedulers.boundedElastic());
        // Mono로 처리하기
//        return Mono.fromCallable(repository::findAll)
//                .publishOn(Schedulers.boundedElastic())
//                .flatMapMany(Flux::fromIterable);
        // .publishOn(Schedulers.boundedElastic())

    }

//========================================================================================

    // TODO Insert users contained in the Flux parameter in the blocking repository using a bounded elastic scheduler
    //  and return a Mono<Void> that signal the end of the operation
    Mono<Void> fluxToBlockingRepository(Flux<User> flux, BlockingRepository<User> repository) {
        return flux.publishOn(Schedulers.boundedElastic())
                .doOnNext(user -> repository.save(user))
                .then();
    }

}
