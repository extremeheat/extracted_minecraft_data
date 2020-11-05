package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.Fluid;
import org.apache.logging.log4j.LogManager;

public interface ChunkAccess extends BlockGetter, FeatureAccess {
   @Nullable
   BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3);

   void setBlockEntity(BlockEntity var1);

   void addEntity(Entity var1);

   @Nullable
   default LevelChunkSection getHighestSection() {
      LevelChunkSection[] var1 = this.getSections();

      for(int var2 = var1.length - 1; var2 >= 0; --var2) {
         LevelChunkSection var3 = var1[var2];
         if (!LevelChunkSection.isEmpty(var3)) {
            return var3;
         }
      }

      return null;
   }

   default int getHighestSectionPosition() {
      LevelChunkSection var1 = this.getHighestSection();
      return var1 == null ? this.getMinBuildHeight() : var1.bottomBlockY();
   }

   Set<BlockPos> getBlockEntitiesPos();

   LevelChunkSection[] getSections();

   Collection<Entry<Heightmap.Types, Heightmap>> getHeightmaps();

   void setHeightmap(Heightmap.Types var1, long[] var2);

   Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types var1);

   int getHeight(Heightmap.Types var1, int var2, int var3);

   ChunkPos getPos();

   Map<StructureFeature<?>, StructureStart<?>> getAllStarts();

   void setAllStarts(Map<StructureFeature<?>, StructureStart<?>> var1);

   default boolean isYSpaceEmpty(int var1, int var2) {
      if (var1 < this.getMinBuildHeight()) {
         var1 = this.getMinBuildHeight();
      }

      if (var2 >= this.getMaxBuildHeight()) {
         var2 = this.getMaxBuildHeight() - 1;
      }

      for(int var3 = var1; var3 <= var2; var3 += 16) {
         if (!LevelChunkSection.isEmpty(this.getSections()[this.getSectionIndex(var3)])) {
            return false;
         }
      }

      return true;
   }

   @Nullable
   ChunkBiomeContainer getBiomes();

   void setUnsaved(boolean var1);

   boolean isUnsaved();

   ChunkStatus getStatus();

   void removeBlockEntity(BlockPos var1);

   default void markPosForPostprocessing(BlockPos var1) {
      LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", var1);
   }

   ShortList[] getPostProcessing();

   default void addPackedPostProcess(short var1, int var2) {
      getOrCreateOffsetList(this.getPostProcessing(), var2).add(var1);
   }

   default void setBlockEntityNbt(CompoundTag var1) {
      LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
   }

   @Nullable
   CompoundTag getBlockEntityNbt(BlockPos var1);

   @Nullable
   CompoundTag getBlockEntityNbtForSaving(BlockPos var1);

   Stream<BlockPos> getLights();

   TickList<Block> getBlockTicks();

   TickList<Fluid> getLiquidTicks();

   UpgradeData getUpgradeData();

   void setInhabitedTime(long var1);

   long getInhabitedTime();

   static ShortList getOrCreateOffsetList(ShortList[] var0, int var1) {
      if (var0[var1] == null) {
         var0[var1] = new ShortArrayList();
      }

      return var0[var1];
   }

   boolean isLightCorrect();

   void setLightCorrect(boolean var1);
}
