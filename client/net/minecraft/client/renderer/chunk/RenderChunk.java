package net.minecraft.client.renderer.chunk;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.DebugLevelSource;

class RenderChunk {
   private final Map<BlockPos, BlockEntity> blockEntities;
   @Nullable
   private final List<PalettedContainer<BlockState>> sections;
   private final boolean debug;
   private final LevelChunk wrapped;

   RenderChunk(LevelChunk var1) {
      super();
      this.wrapped = var1;
      this.debug = var1.getLevel().isDebug();
      this.blockEntities = ImmutableMap.copyOf(var1.getBlockEntities());
      if (var1 instanceof EmptyLevelChunk) {
         this.sections = null;
      } else {
         LevelChunkSection[] var2 = var1.getSections();
         this.sections = new ArrayList(var2.length);
         LevelChunkSection[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            LevelChunkSection var6 = var3[var5];
            this.sections.add(var6.hasOnlyAir() ? null : var6.getStates().copy());
         }
      }

   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1) {
      return (BlockEntity)this.blockEntities.get(var1);
   }

   public BlockState getBlockState(BlockPos var1) {
      int var2 = var1.getX();
      int var3 = var1.getY();
      int var4 = var1.getZ();
      if (this.debug) {
         BlockState var9 = null;
         if (var3 == 60) {
            var9 = Blocks.BARRIER.defaultBlockState();
         }

         if (var3 == 70) {
            var9 = DebugLevelSource.getBlockStateFor(var2, var4);
         }

         return var9 == null ? Blocks.AIR.defaultBlockState() : var9;
      } else if (this.sections == null) {
         return Blocks.AIR.defaultBlockState();
      } else {
         try {
            int var5 = this.wrapped.getSectionIndex(var3);
            if (var5 >= 0 && var5 < this.sections.size()) {
               PalettedContainer var10 = (PalettedContainer)this.sections.get(var5);
               if (var10 != null) {
                  return (BlockState)var10.get(var2 & 15, var3 & 15, var4 & 15);
               }
            }

            return Blocks.AIR.defaultBlockState();
         } catch (Throwable var8) {
            CrashReport var6 = CrashReport.forThrowable(var8, "Getting block state");
            CrashReportCategory var7 = var6.addCategory("Block being got");
            var7.setDetail("Location", () -> {
               return CrashReportCategory.formatLocation(this.wrapped, var2, var3, var4);
            });
            throw new ReportedException(var6);
         }
      }
   }
}
