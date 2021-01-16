package io.netty.handler.codec.serialization;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;

final class SoftReferenceMap<K, V> extends ReferenceMap<K, V> {
   SoftReferenceMap(Map<K, Reference<V>> var1) {
      super(var1);
   }

   Reference<V> fold(V var1) {
      return new SoftReference(var1);
   }
}
