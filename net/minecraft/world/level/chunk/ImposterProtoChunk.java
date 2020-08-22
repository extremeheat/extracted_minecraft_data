package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class ImposterProtoChunk extends ProtoChunk {
   private final LevelChunk wrapped;

   public ImposterProtoChunk(LevelChunk var1) {
      super(var1.getPos(), UpgradeData.EMPTY);
      this.wrapped = var1;
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1) {
      return this.wrapped.getBlockEntity(var1);
   }

   @Nullable
   public BlockState getBlockState(BlockPos var1) {
      return this.wrapped.getBlockState(var1);
   }

   public FluidState getFluidState(BlockPos var1) {
      return this.wrapped.getFluidState(var1);
   }

   public int getMaxLightLevel() {
      return this.wrapped.getMaxLightLevel();
   }

   @Nullable
   public BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3) {
      return null;
   }

   public void setBlockEntity(BlockPos var1, BlockEntity var2) {
   }

   public void addEntity(Entity var1) {
   }

   public void setStatus(ChunkStatus var1) {
   }

   public LevelChunkSection[] getSections() {
      return this.wrapped.getSections();
   }

   @Nullable
   public LevelLightEngine getLightEngine() {
      return this.wrapped.getLightEngine();
   }

   public void setHeightmap(Heightmap.Types var1, long[] var2) {
   }

   private Heightmap.Types fixType(Heightmap.Types var1) {
      if (var1 == Heightmap.Types.WORLD_SURFACE_WG) {
         return Heightmap.Types.WORLD_SURFACE;
      } else {
         return var1 == Heightmap.Types.OCEAN_FLOOR_WG ? Heightmap.Types.OCEAN_FLOOR : var1;
      }
   }

   public int getHeight(Heightmap.Types var1, int var2, int var3) {
      return this.wrapped.getHeight(this.fixType(var1), var2, var3);
   }

   public ChunkPos getPos() {
      return this.wrapped.getPos();
   }

   public void setLastSaveTime(long var1) {
   }

   @Nullable
   public StructureStart getStartForFeature(String var1) {
      return this.wrapped.getStartForFeature(var1);
   }

   public void setStartForFeature(String var1, StructureStart var2) {
   }

   public Map getAllStarts() {
      return this.wrapped.getAllStarts();
   }

   public void setAllStarts(Map var1) {
   }

   public LongSet getReferencesForFeature(String var1) {
      return this.wrapped.getReferencesForFeature(var1);
   }

   public void addReferenceForFeature(String var1, long var2) {
   }

   public Map getAllReferences() {
      return this.wrapped.getAllReferences();
   }

   public void setAllReferences(Map var1) {
   }

   public ChunkBiomeContainer getBiomes() {
      return this.wrapped.getBiomes();
   }

   public void setUnsaved(boolean var1) {
   }

   public boolean isUnsaved() {
      return false;
   }

   public ChunkStatus getStatus() {
      return this.wrapped.getStatus();
   }

   public void removeBlockEntity(BlockPos var1) {
   }

   public void markPosForPostprocessing(BlockPos var1) {
   }

   public void setBlockEntityNbt(CompoundTag var1) {
   }

   @Nullable
   public CompoundTag getBlockEntityNbt(BlockPos var1) {
      return this.wrapped.getBlockEntityNbt(var1);
   }

   @Nullable
   public CompoundTag getBlockEntityNbtForSaving(BlockPos var1) {
      return this.wrapped.getBlockEntityNbtForSaving(var1);
   }

   public void setBiomes(ChunkBiomeContainer var1) {
   }

   public Stream getLights() {
      return this.wrapped.getLights();
   }

   public ProtoTickList getBlockTicks() {
      return new ProtoTickList((var0) -> {
         return var0.defaultBlockState().isAir();
      }, this.getPos());
   }

   public ProtoTickList getLiquidTicks() {
      return new ProtoTickList((var0) -> {
         return var0 == Fluids.EMPTY;
      }, this.getPos());
   }

   public BitSet getCarvingMask(GenerationStep.Carving var1) {
      return this.wrapped.getCarvingMask(var1);
   }

   public LevelChunk getWrapped() {
      return this.wrapped;
   }

   public boolean isLightCorrect() {
      return this.wrapped.isLightCorrect();
   }

   public void setLightCorrect(boolean var1) {
      this.wrapped.setLightCorrect(var1);
   }

   // $FF: synthetic method
   public TickList getLiquidTicks() {
      return this.getLiquidTicks();
   }

   // $FF: synthetic method
   public TickList getBlockTicks() {
      return this.getBlockTicks();
   }
}
