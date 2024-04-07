package net.minecraft.world.level;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;

public interface WorldGenLevel extends ServerLevelAccessor {
   long getSeed();

   default boolean ensureCanWrite(BlockPos var1) {
      return true;
   }

   default void setCurrentlyGenerating(@Nullable Supplier<String> var1) {
   }
}
