/*
 * Copyright (c) 2026-present, pull-vert and Jayo contributors.
 * Use of this source code is governed by the Apache 2.0 license.
 */

package jayo.stream;

import jayo.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * An async stream of elements that are being produced asynchronously.
 *
 * @param <T> the type of elements in this stream.
 */
public interface AsyncStream<T> extends Iterable<@NonNull Result<T>> {
    /**
     * When an element is produced asynchronously, the given action is invoked with the exception (or {@code null} if
     * none), or the successful result (that may be {@code null} if expected) of this operation.
     *
     * @param action the action to perform
     */
    void whenEachCompletes(final @NonNull BiConsumer<? super T, ? super @Nullable Throwable> action);

    /**
     * @return a sequential {@code Stream} that streams the results of this async stream as its source, as they complete
     * either successfully or exceptionally.
     * <p>
     * Note: since the source elements are produced asynchronously, there may be a delay before each result is emitted.
     * <p>
     * This method should be overridden when the {@link #spliterator()} method cannot return a spliterator that is
     * {@code IMMUTABLE}, {@code CONCURRENT}, or <em>late-binding</em>.
     */
    default @NonNull Stream<@NonNull Result<T>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * @return a sequential {@code Stream} of the successful results of this async stream, ignoring any failures.
     */
    default @NonNull Stream<T> streamSuccesses() {
        return stream()
                .filter(Result::isSuccess)
                .map(Result::getOrThrow); // will never throw since we filtered only the successful results.
    }
}
