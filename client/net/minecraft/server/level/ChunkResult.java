package net.minecraft.server.level;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public interface ChunkResult<T> {
   static <T> ChunkResult<T> of(final T value) {
      return new Success<T>(value);
   }

   static <T> ChunkResult<T> error(final String error) {
      return error((Supplier)(() -> error));
   }

   static <T> ChunkResult<T> error(final Supplier<String> errorSupplier) {
      return new Fail<T>(errorSupplier);
   }

   boolean isSuccess();

   @Nullable T orElse(@Nullable T orElse);

   static <R> @Nullable R orElse(final ChunkResult<? extends R> chunkResult, final @Nullable R orElse) {
      R result = chunkResult.orElse((Object)null);
      return (R)(result != null ? result : orElse);
   }

   @Nullable String getError();

   ChunkResult<T> ifSuccess(Consumer<T> consumer);

   <R> ChunkResult<R> map(Function<T, R> map);

   <E extends Throwable> T orElseThrow(Supplier<E> exceptionSupplier) throws E;

   public static record Success<T>(T value) implements ChunkResult<T> {
      public Success {
         super();
      }

      public boolean isSuccess() {
         return true;
      }

      public T orElse(final @Nullable T orElse) {
         return this.value;
      }

      public @Nullable String getError() {
         return null;
      }

      public ChunkResult<T> ifSuccess(final Consumer<T> consumer) {
         consumer.accept(this.value);
         return this;
      }

      public <R> ChunkResult<R> map(final Function<T, R> map) {
         return new Success<R>(map.apply(this.value));
      }

      public <E extends Throwable> T orElseThrow(final Supplier<E> exceptionSupplier) throws E {
         return this.value;
      }
   }

   public static record Fail<T>(Supplier<String> error) implements ChunkResult<T> {
      public Fail {
         super();
      }

      public boolean isSuccess() {
         return false;
      }

      public @Nullable T orElse(final @Nullable T orElse) {
         return orElse;
      }

      public String getError() {
         return (String)this.error.get();
      }

      public ChunkResult<T> ifSuccess(final Consumer<T> consumer) {
         return this;
      }

      public <R> ChunkResult<R> map(final Function<T, R> map) {
         return new Fail<R>(this.error);
      }

      public <E extends Throwable> T orElseThrow(final Supplier<E> exceptionSupplier) throws E {
         throw (Throwable)exceptionSupplier.get();
      }
   }
}
