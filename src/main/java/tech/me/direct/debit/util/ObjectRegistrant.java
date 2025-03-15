package tech.me.direct.debit.util;

@FunctionalInterface
public interface ObjectRegistrant<K, V> {
    void register(K key, V value);

}
