package net.minecraft.util.thread;

import com.mojang.datafixers.util.Either;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ProcessorHandle<Msg> extends AutoCloseable {
   String name();

   void tell(Msg var1);

   default void close() {
   }

   default <Source> CompletableFuture<Source> ask(Function<? super ProcessorHandle<Source>, ? extends Msg> var1) {
      CompletableFuture var2 = new CompletableFuture();
      Objects.requireNonNull(var2);
      Object var3 = var1.apply(method_1("ask future procesor handle", var2::complete));
      this.tell(var3);
      return var2;
   }

   default <Source> CompletableFuture<Source> askEither(Function<? super ProcessorHandle<Either<Source, Exception>>, ? extends Msg> var1) {
      CompletableFuture var2 = new CompletableFuture();
      Object var3 = var1.apply(method_1("ask future procesor handle", (var1x) -> {
         Objects.requireNonNull(var2);
         var1x.ifLeft(var2::complete);
         Objects.requireNonNull(var2);
         var1x.ifRight(var2::completeExceptionally);
      }));
      this.tell(var3);
      return var2;
   }

   // $FF: renamed from: of (java.lang.String, java.util.function.Consumer) net.minecraft.util.thread.ProcessorHandle
   static <Msg> ProcessorHandle<Msg> method_1(final String var0, final Consumer<Msg> var1) {
      return new ProcessorHandle<Msg>() {
         public String name() {
            return var0;
         }

         public void tell(Msg var1x) {
            var1.accept(var1x);
         }

         public String toString() {
            return var0;
         }
      };
   }
}
