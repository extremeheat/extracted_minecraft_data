package net.minecraft.client.renderer.chunk;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.DebugLevelSource;

class SectionCopy {
   private final Map<BlockPos, BlockEntity> blockEntities;
   @Nullable
   private final PalettedContainer<BlockState> section;
   private final boolean debug;
   private final LevelHeightAccessor levelHeightAccessor;

   SectionCopy(LevelChunk var1, int var2) {
      super();
      this.levelHeightAccessor = var1;
      this.debug = var1.getLevel().isDebug();
      this.blockEntities = ImmutableMap.copyOf(var1.getBlockEntities());
      if (var1 instanceof EmptyLevelChunk) {
         this.section = null;
      } else {
         LevelChunkSection[] var3 = var1.getSections();
         if (var2 >= 0 && var2 < var3.length) {
            LevelChunkSection var4 = var3[var2];
            this.section = var4.hasOnlyAir() ? null : var4.getStates().copy();
         } else {
            this.section = null;
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
         BlockState var5 = null;
         if (var3 == 60) {
            var5 = Blocks.BARRIER.defaultBlockState();
         }

         if (var3 == 70) {
            var5 = DebugLevelSource.getBlockStateFor(var2, var4);
         }

         return var5 == null ? Blocks.AIR.defaultBlockState() : var5;
      } else if (this.section == null) {
         return Blocks.AIR.defaultBlockState();
      } else {
         try {
            return this.section.get(var2 & 15, var3 & 15, var4 & 15);
         } catch (Throwable var8) {
            CrashReport var6 = CrashReport.forThrowable(var8, "Getting block state");
            CrashReportCategory var7 = var6.addCategory("Block being got");
            var7.setDetail("Location", (CrashReportDetail)(() -> CrashReportCategory.formatLocation(this.levelHeightAccessor, var2, var3, var4)));
            throw new ReportedException(var6);
         }
      }
   }
}
