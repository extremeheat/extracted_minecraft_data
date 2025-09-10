package net.minecraft.util.debug;

import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

public interface DebugValueAccess {
   <T> void forEachChunk(DebugSubscription<T> var1, BiConsumer<ChunkPos, T> var2);

   @Nullable
   <T> T getChunkValue(DebugSubscription<T> var1, ChunkPos var2);

   <T> void forEachBlock(DebugSubscription<T> var1, BiConsumer<BlockPos, T> var2);

   @Nullable
   <T> T getBlockValue(DebugSubscription<T> var1, BlockPos var2);

   <T> void forEachEntity(DebugSubscription<T> var1, BiConsumer<Entity, T> var2);

   @Nullable
   <T> T getEntityValue(DebugSubscription<T> var1, Entity var2);

   <T> void forEachEvent(DebugSubscription<T> var1, EventVisitor<T> var2);

   @FunctionalInterface
   public interface EventVisitor<T> {
      void accept(T var1, int var2, int var3);
   }
}
