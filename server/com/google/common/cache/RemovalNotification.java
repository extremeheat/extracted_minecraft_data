package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.AbstractMap.SimpleImmutableEntry;
import javax.annotation.Nullable;

@GwtCompatible
public final class RemovalNotification<K, V> extends SimpleImmutableEntry<K, V> {
   private final RemovalCause cause;
   private static final long serialVersionUID = 0L;

   public static <K, V> RemovalNotification<K, V> create(@Nullable K var0, @Nullable V var1, RemovalCause var2) {
      return new RemovalNotification(var0, var1, var2);
   }

   private RemovalNotification(@Nullable K var1, @Nullable V var2, RemovalCause var3) {
      super(var1, var2);
      this.cause = (RemovalCause)Preconditions.checkNotNull(var3);
   }

   public RemovalCause getCause() {
      return this.cause;
   }

   public boolean wasEvicted() {
      return this.cause.wasEvicted();
   }
}
