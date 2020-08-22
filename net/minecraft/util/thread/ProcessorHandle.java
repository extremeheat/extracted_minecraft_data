package net.minecraft.util.thread;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ProcessorHandle extends AutoCloseable {
   String name();

   void tell(Object var1);

   default void close() {
   }

   default CompletableFuture ask(Function var1) {
      CompletableFuture var2 = new CompletableFuture();
      var2.getClass();
      Object var3 = var1.apply(of("ask future procesor handle", var2::complete));
      this.tell(var3);
      return var2;
   }

   static ProcessorHandle of(final String var0, final Consumer var1) {
      return new ProcessorHandle() {
         public String name() {
            return var0;
         }

         public void tell(Object var1x) {
            var1.accept(var1x);
         }

         public String toString() {
            return var0;
         }
      };
   }
}
