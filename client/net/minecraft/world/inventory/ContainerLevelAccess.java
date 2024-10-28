package net.minecraft.world.inventory;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ContainerLevelAccess {
   ContainerLevelAccess NULL = new ContainerLevelAccess() {
      public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> var1) {
         return Optional.empty();
      }
   };

   static ContainerLevelAccess create(final Level var0, final BlockPos var1) {
      return new ContainerLevelAccess() {
         public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> var1x) {
            return Optional.of(var1x.apply(var0, var1));
         }
      };
   }

   <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> var1);

   default <T> T evaluate(BiFunction<Level, BlockPos, T> var1, T var2) {
      return this.evaluate(var1).orElse(var2);
   }

   default void execute(BiConsumer<Level, BlockPos> var1) {
      this.evaluate((var1x, var2) -> {
         var1.accept(var1x, var2);
         return Optional.empty();
      });
   }
}
