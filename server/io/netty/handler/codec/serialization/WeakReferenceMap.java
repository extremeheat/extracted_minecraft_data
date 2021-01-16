package io.netty.handler.codec.serialization;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;

final class WeakReferenceMap<K, V> extends ReferenceMap<K, V> {
   WeakReferenceMap(Map<K, Reference<V>> var1) {
      super(var1);
   }

   Reference<V> fold(V var1) {
      return new WeakReference(var1);
   }
}
