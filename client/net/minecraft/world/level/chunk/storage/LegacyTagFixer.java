package net.minecraft.world.level.chunk.storage;

import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;

@FunctionalInterface
public interface LegacyTagFixer {
   Supplier<LegacyTagFixer> EMPTY = () -> (var0) -> var0;

   CompoundTag applyFix(CompoundTag var1);

   default void markChunkDone(ChunkPos var1) {
   }

   default int targetDataVersion() {
      return -1;
   }
}
