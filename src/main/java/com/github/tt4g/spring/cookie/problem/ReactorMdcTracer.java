// This file was written from the original code:
// https://github.com/archie-swif/webflux-mdc/blob/9e10c34dc2790d04f4d9cfe228c8be56a2ab920c/src/main/java/com/example/webfluxmdc/MdcContextLifter.java
package com.github.tt4g.spring.cookie.problem;

import java.util.Optional;

import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import org.slf4j.MDC.MDCCloseable;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;
import reactor.util.context.Context;

public class ReactorMdcTracer<T> implements CoreSubscriber<T> {

    static final String CONTEXT_KEY = ReactorMdcTracer.class.getName();

    private static final String HOOK_KEY = ReactorMdcTracer.class.getName();

    public static final String MDC_KEY = "trace-id";

    public static void installHook() {
        Hooks.onEachOperator(
            HOOK_KEY,
            Operators.lift((scannable, coreSubscriber) ->
                new ReactorMdcTracer<>(coreSubscriber)));
    }

    public static void resetHook() {
        Hooks.resetOnEachOperator(HOOK_KEY);
    }

    private final CoreSubscriber<T> coreSubscriber;

    ReactorMdcTracer(CoreSubscriber<T> coreSubscriber) {
        this.coreSubscriber = coreSubscriber;
    }

    @Override
    public Context currentContext() {
        return this.coreSubscriber.currentContext();
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.coreSubscriber.onSubscribe(s);
    }

    @Override
    public void onNext(T t) {
        withTraceId(() -> this.coreSubscriber.onNext(t));
    }

    @Override
    public void onError(Throwable t) {
        withTraceId(() -> this.coreSubscriber.onError(t));
    }

    @Override
    public void onComplete() {
        withTraceId(() -> this.coreSubscriber.onComplete());
    }

    private void withTraceId(Runnable body) {
        Context context = this.coreSubscriber.currentContext();
        Optional<TraceId> traceIdOptional = context.getOrEmpty(CONTEXT_KEY);

        traceIdOptional.ifPresentOrElse(traceId -> {
                try (MDCCloseable mdcCloseable = MDC.putCloseable(MDC_KEY, traceId.getTraceId())) {
                    body.run();
                }
            },
            body);
    }

}
