package io.pivotal.literx;

//generic imports to help with simpler IDEs (ie tech.io)
import java.util.*;
import java.util.function.*;
import java.time.*;

import io.pivotal.literx.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Learn how to turn Reactive API to blocking one.
 *
 * @author Sebastien Deleuze
 */
public class Part10ReactiveToBlocking {
    /**
     * 때로는 코드의 일부만 리액티브하게 마이그레이션할 수 있으며, 리액티브 시퀀스를 더 필수적인 코드로 재사용해야 하는 경우도 있습니다.
     *
     * 따라서 Mono의 값을 사용할 수 있을 때까지 차단해야 하는 경우 Mono#block() 메서드를 사용하세요. onError 이벤트가 트리거되면 예외가 발생합니다.
     *
     * 가능한 한 종단 간 반응형 코드를 선호하여 이를 피해야 한다는 점에 유의하세요. 다른 반응형 코드의 중간에는 전체 반응형 파이프라인을 잠글 가능성이 있으므로 어떤 대가를 치르더라도 이를 피해야 합니다.
     * */

//========================================================================================

    // TODO Return the user contained in that Mono
    User monoToValue(Mono<User> mono) {
        return mono.block();
    }

//========================================================================================

    // TODO Return the users contained in that Flux
    Iterable<User> fluxToValues(Flux<User> flux) {
        return flux.toIterable();
    }

}
