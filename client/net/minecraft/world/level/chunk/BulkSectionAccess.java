package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BulkSectionAccess implements AutoCloseable {
   private final LevelAccessor level;
   private final Long2ObjectMap<LevelChunkSection> acquiredSections = new Long2ObjectOpenHashMap();
   @Nullable
   private LevelChunkSection lastSection;
   private long lastSectionKey;

   public BulkSectionAccess(LevelAccessor var1) {
      super();
      this.level = var1;
   }

   @Nullable
   public LevelChunkSection getSection(BlockPos var1) {
      int var2 = this.level.getSectionIndex(var1.getY());
      if (var2 >= 0 && var2 < this.level.getSectionsCount()) {
         long var3 = SectionPos.asLong(var1);
         if (this.lastSection == null || this.lastSectionKey != var3) {
            this.lastSection = (LevelChunkSection)this.acquiredSections.computeIfAbsent(var3, (var3x) -> {
               ChunkAccess var5 = this.level.getChunk(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getZ()));
               LevelChunkSection var6 = var5.getSection(var2);
               var6.acquire();
               return var6;
            });
            this.lastSectionKey = var3;
         }

         return this.lastSection;
      } else {
         return null;
      }
   }

   public BlockState getBlockState(BlockPos var1) {
      LevelChunkSection var2 = this.getSection(var1);
      if (var2 == null) {
         return Blocks.AIR.defaultBlockState();
      } else {
         int var3 = SectionPos.sectionRelative(var1.getX());
         int var4 = SectionPos.sectionRelative(var1.getY());
         int var5 = SectionPos.sectionRelative(var1.getZ());
         return var2.getBlockState(var3, var4, var5);
      }
   }

   public void close() {
      ObjectIterator var1 = this.acquiredSections.values().iterator();

      while(var1.hasNext()) {
         LevelChunkSection var2 = (LevelChunkSection)var1.next();
         var2.release();
      }

   }
}
