package net.minecraft.util;

import javax.annotation.Nullable;

public class ExceptionCollector<T extends Throwable> {
   @Nullable
   private T result;

   public ExceptionCollector() {
      super();
   }

   public void add(T var1) {
      if (this.result == null) {
         this.result = var1;
      } else {
         this.result.addSuppressed(var1);
      }

   }

   public void throwIfPresent() throws T {
      if (this.result != null) {
         throw this.result;
      }
   }
}
