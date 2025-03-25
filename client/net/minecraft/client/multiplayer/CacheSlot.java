package net.minecraft.client.multiplayer;

import java.util.function.Function;
import javax.annotation.Nullable;

public class CacheSlot<C extends CacheSlot.Cleaner<C>, D> {
   private final Function<C, D> operation;
   @Nullable
   private C context;
   @Nullable
   private D value;

   public CacheSlot(Function<C, D> var1) {
      super();
      this.operation = var1;
   }

   public D compute(C var1) {
      if (var1 == this.context && this.value != null) {
         return this.value;
      } else {
         Object var2 = this.operation.apply(var1);
         this.value = (D)var2;
         this.context = var1;
         var1.registerForCleaning(this);
         return (D)var2;
      }
   }

   public void clear() {
      this.value = null;
      this.context = null;
   }

   @FunctionalInterface
   public interface Cleaner<C extends Cleaner<C>> {
      void registerForCleaning(CacheSlot<C, ?> var1);
   }
}
