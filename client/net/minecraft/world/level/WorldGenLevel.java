package net.minecraft.world.level;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public interface WorldGenLevel extends ServerLevelAccessor {
   long getSeed();

   List<? extends StructureStart<?>> startsForFeature(SectionPos var1, StructureFeature<?> var2);

   default boolean ensureCanWrite(BlockPos var1) {
      return true;
   }

   default void setCurrentlyGenerating(@Nullable Supplier<String> var1) {
   }
}
