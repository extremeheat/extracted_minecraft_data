package net.minecraft.world.level.chunk.storage;

import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;

@FunctionalInterface
public interface LegacyTagFixer {
   Supplier<LegacyTagFixer> EMPTY = () -> (tag) -> tag;

   CompoundTag applyFix(CompoundTag tag);

   default void markChunkDone(final ChunkPos pos) {
   }

   default int targetDataVersion() {
      return -1;
   }
}
