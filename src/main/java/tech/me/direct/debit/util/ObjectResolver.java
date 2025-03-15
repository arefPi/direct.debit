package tech.me.direct.debit.util;

import java.util.Optional;

@FunctionalInterface
public interface ObjectResolver<K, V> {
    Optional<V> resolve(K key);
}
