package net.minecraft.server.packs.resources;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@FunctionalInterface
public interface PreparableReloadListener {
   CompletableFuture<Void> reload(SharedState currentReload, Executor taskExecutor, PreparationBarrier preparationBarrier, Executor reloadExecutor);

   default void prepareSharedState(final SharedState currentReload) {
   }

   default String getName() {
      return this.getClass().getSimpleName();
   }

   public static final class StateKey<T> {
      public StateKey() {
         super();
      }
   }

   public static final class SharedState {
      private final ResourceManager manager;
      private final Map<StateKey<?>, Object> state = new IdentityHashMap();

      public SharedState(final ResourceManager manager) {
         super();
         this.manager = manager;
      }

      public ResourceManager resourceManager() {
         return this.manager;
      }

      public <T> void set(final StateKey<T> key, final T value) {
         this.state.put(key, value);
      }

      public <T> T get(final StateKey<T> key) {
         return (T)Objects.requireNonNull(this.state.get(key));
      }
   }

   @FunctionalInterface
   public interface PreparationBarrier {
      <T> CompletableFuture<T> wait(T t);
   }
}
