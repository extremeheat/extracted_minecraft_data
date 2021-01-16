package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface BiMap<K, V> extends Map<K, V> {
   @Nullable
   @CanIgnoreReturnValue
   V put(@Nullable K var1, @Nullable V var2);

   @Nullable
   @CanIgnoreReturnValue
   V forcePut(@Nullable K var1, @Nullable V var2);

   void putAll(Map<? extends K, ? extends V> var1);

   Set<V> values();

   BiMap<V, K> inverse();
}
