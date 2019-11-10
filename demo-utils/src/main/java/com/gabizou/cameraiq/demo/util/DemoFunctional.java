package com.gabizou.cameraiq.demo.util;

import akka.japi.Pair;
import org.pcollections.OrderedPSet;
import org.pcollections.POrderedSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collector;

public class DemoFunctional {

    public static <T> Collector<T, List<T>, POrderedSet<T>> toImmutableSet() {
        return Collector.of(ArrayList::new, Collection::add, (left, right) -> {
            left.addAll(right);
            return left;
        }, OrderedPSet::from);
    }

    public static <T> Collector<CompletableFuture<T>, Pair<List<T>, List<String>>, Pair<POrderedSet<T>, Set<String>>> toPairOfObjectAndExceptions() {
        return Collector.of(
                () -> Pair.create(new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>()),
                (pair, stage) -> {
                    // Our accumilator
                    stage.whenCompleteAsync((user, error) -> {
                        if (user != null) {
                            pair.first().add(user);
                        } else if (error != null) {
                            pair.second().add(error.getMessage());
                        } else {
                            pair.second().add("No user found, but no " +
                                "error");
                        }
                        // We call join to ensure that the consumer will
                        // hold until the service call is completed, without
                        // having to worry about threads (since the join
                        // is called already asynchronously, our combiner
                        // is thread-safe).
                    }).join();
                }, (left, right) -> { // Our combiner
                    left.first().addAll(right.first());
                    left.second().addAll(right.second());
                    return left;
                }, list -> // Our finalizer
                Pair.create(OrderedPSet.from(list.first()),
                    OrderedPSet.from(list.second())));
    }
}
