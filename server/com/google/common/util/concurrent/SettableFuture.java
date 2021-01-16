package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import javax.annotation.Nullable;

@GwtCompatible
public final class SettableFuture<V> extends AbstractFuture.TrustedFuture<V> {
   public static <V> SettableFuture<V> create() {
      return new SettableFuture();
   }

   @CanIgnoreReturnValue
   public boolean set(@Nullable V var1) {
      return super.set(var1);
   }

   @CanIgnoreReturnValue
   public boolean setException(Throwable var1) {
      return super.setException(var1);
   }

   @Beta
   @CanIgnoreReturnValue
   public boolean setFuture(ListenableFuture<? extends V> var1) {
      return super.setFuture(var1);
   }

   private SettableFuture() {
      super();
   }
}
