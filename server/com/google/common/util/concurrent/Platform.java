package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
final class Platform {
   static boolean isInstanceOfThrowableClass(@Nullable Throwable var0, Class<? extends Throwable> var1) {
      return var1.isInstance(var0);
   }

   private Platform() {
      super();
   }
}
