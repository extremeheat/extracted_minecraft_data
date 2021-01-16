package io.netty.handler.codec;

import io.netty.util.HashingStrategy;

public final class DefaultHeadersImpl<K, V> extends DefaultHeaders<K, V, DefaultHeadersImpl<K, V>> {
   public DefaultHeadersImpl(HashingStrategy<K> var1, ValueConverter<V> var2, DefaultHeaders.NameValidator<K> var3) {
      super(var1, var2, var3);
   }
}
