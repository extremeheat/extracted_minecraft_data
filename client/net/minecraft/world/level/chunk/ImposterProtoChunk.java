package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.TickContainerAccess;

public class ImposterProtoChunk extends ProtoChunk {
   private final LevelChunk wrapped;
   private final boolean allowWrites;

   public ImposterProtoChunk(LevelChunk var1, boolean var2) {
      super(
         var1.getPos(),
         UpgradeData.EMPTY,
         var1.levelHeightAccessor,
         var1.getLevel().registryAccess().registryOrThrow(Registries.BIOME),
         var1.getBlendingData()
      );
      this.wrapped = var1;
      this.allowWrites = var2;
   }

   @Nullable
   @Override
   public BlockEntity getBlockEntity(BlockPos var1) {
      return this.wrapped.getBlockEntity(var1);
   }

   @Override
   public BlockState getBlockState(BlockPos var1) {
      return this.wrapped.getBlockState(var1);
   }

   @Override
   public FluidState getFluidState(BlockPos var1) {
      return this.wrapped.getFluidState(var1);
   }

   @Override
   public int getMaxLightLevel() {
      return this.wrapped.getMaxLightLevel();
   }

   @Override
   public LevelChunkSection getSection(int var1) {
      return this.allowWrites ? this.wrapped.getSection(var1) : super.getSection(var1);
   }

   @Nullable
   @Override
   public BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3) {
      return this.allowWrites ? this.wrapped.setBlockState(var1, var2, var3) : null;
   }

   @Override
   public void setBlockEntity(BlockEntity var1) {
      if (this.allowWrites) {
         this.wrapped.setBlockEntity(var1);
      }
   }

   @Override
   public void addEntity(Entity var1) {
      if (this.allowWrites) {
         this.wrapped.addEntity(var1);
      }
   }

   @Override
   public void setStatus(ChunkStatus var1) {
      if (this.allowWrites) {
         super.setStatus(var1);
      }
   }

   @Override
   public LevelChunkSection[] getSections() {
      return this.wrapped.getSections();
   }

   @Override
   public void setHeightmap(Heightmap.Types var1, long[] var2) {
   }

   private Heightmap.Types fixType(Heightmap.Types var1) {
      if (var1 == Heightmap.Types.WORLD_SURFACE_WG) {
         return Heightmap.Types.WORLD_SURFACE;
      } else {
         return var1 == Heightmap.Types.OCEAN_FLOOR_WG ? Heightmap.Types.OCEAN_FLOOR : var1;
      }
   }

   @Override
   public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types var1) {
      return this.wrapped.getOrCreateHeightmapUnprimed(var1);
   }

   @Override
   public int getHeight(Heightmap.Types var1, int var2, int var3) {
      return this.wrapped.getHeight(this.fixType(var1), var2, var3);
   }

   @Override
   public Holder<Biome> getNoiseBiome(int var1, int var2, int var3) {
      return this.wrapped.getNoiseBiome(var1, var2, var3);
   }

   @Override
   public ChunkPos getPos() {
      return this.wrapped.getPos();
   }

   @Nullable
   @Override
   public StructureStart getStartForStructure(Structure var1) {
      return this.wrapped.getStartForStructure(var1);
   }

   @Override
   public void setStartForStructure(Structure var1, StructureStart var2) {
   }

   @Override
   public Map<Structure, StructureStart> getAllStarts() {
      return this.wrapped.getAllStarts();
   }

   @Override
   public void setAllStarts(Map<Structure, StructureStart> var1) {
   }

   @Override
   public LongSet getReferencesForStructure(Structure var1) {
      return this.wrapped.getReferencesForStructure(var1);
   }

   @Override
   public void addReferenceForStructure(Structure var1, long var2) {
   }

   @Override
   public Map<Structure, LongSet> getAllReferences() {
      return this.wrapped.getAllReferences();
   }

   @Override
   public void setAllReferences(Map<Structure, LongSet> var1) {
   }

   @Override
   public void setUnsaved(boolean var1) {
      this.wrapped.setUnsaved(var1);
   }

   @Override
   public boolean isUnsaved() {
      return false;
   }

   @Override
   public ChunkStatus getStatus() {
      return this.wrapped.getStatus();
   }

   @Override
   public void removeBlockEntity(BlockPos var1) {
   }

   @Override
   public void markPosForPostprocessing(BlockPos var1) {
   }

   @Override
   public void setBlockEntityNbt(CompoundTag var1) {
   }

   @Nullable
   @Override
   public CompoundTag getBlockEntityNbt(BlockPos var1) {
      return this.wrapped.getBlockEntityNbt(var1);
   }

   @Nullable
   @Override
   public CompoundTag getBlockEntityNbtForSaving(BlockPos var1) {
      return this.wrapped.getBlockEntityNbtForSaving(var1);
   }

   @Override
   public void findBlocks(Predicate<BlockState> var1, BiConsumer<BlockPos, BlockState> var2) {
      this.wrapped.findBlocks(var1, var2);
   }

   @Override
   public TickContainerAccess<Block> getBlockTicks() {
      return this.allowWrites ? this.wrapped.getBlockTicks() : BlackholeTickAccess.emptyContainer();
   }

   @Override
   public TickContainerAccess<Fluid> getFluidTicks() {
      return this.allowWrites ? this.wrapped.getFluidTicks() : BlackholeTickAccess.emptyContainer();
   }

   @Override
   public ChunkAccess.TicksToSave getTicksForSerialization() {
      return this.wrapped.getTicksForSerialization();
   }

   @Nullable
   @Override
   public BlendingData getBlendingData() {
      return this.wrapped.getBlendingData();
   }

   @Override
   public void setBlendingData(BlendingData var1) {
      this.wrapped.setBlendingData(var1);
   }

   @Override
   public CarvingMask getCarvingMask(GenerationStep.Carving var1) {
      if (this.allowWrites) {
         return super.getCarvingMask(var1);
      } else {
         throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("Meaningless in this context"));
      }
   }

   @Override
   public CarvingMask getOrCreateCarvingMask(GenerationStep.Carving var1) {
      if (this.allowWrites) {
         return super.getOrCreateCarvingMask(var1);
      } else {
         throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("Meaningless in this context"));
      }
   }

   public LevelChunk getWrapped() {
      return this.wrapped;
   }

   @Override
   public boolean isLightCorrect() {
      return this.wrapped.isLightCorrect();
   }

   @Override
   public void setLightCorrect(boolean var1) {
      this.wrapped.setLightCorrect(var1);
   }

   @Override
   public void fillBiomesFromNoise(BiomeResolver var1, Climate.Sampler var2) {
      if (this.allowWrites) {
         this.wrapped.fillBiomesFromNoise(var1, var2);
      }
   }

   @Override
   public void initializeLightSources() {
      this.wrapped.initializeLightSources();
   }

   @Override
   public ChunkSkyLightSources getSkyLightSources() {
      return this.wrapped.getSkyLightSources();
   }
}
