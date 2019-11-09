package com.gabizou.cameraiq.demo.util;

import org.pcollections.OrderedPSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;

public class DemoFunctional {

    public static <T> Collector<T, List<T>, OrderedPSet<T>> toImmutableSet() {
        return Collector.of(ArrayList::new, Collection::add, (left, right) -> {
            left.addAll(right);
            return left;
        }, OrderedPSet::from);
    }
}
