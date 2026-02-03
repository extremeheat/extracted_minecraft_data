package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.TickContainerAccess;
import org.jspecify.annotations.Nullable;

public class ImposterProtoChunk extends ProtoChunk {
   private final LevelChunk wrapped;
   private final boolean allowWrites;

   public ImposterProtoChunk(final LevelChunk wrapped, final boolean allowWrites) {
      super(wrapped.getPos(), UpgradeData.EMPTY, wrapped.levelHeightAccessor, wrapped.getLevel().palettedContainerFactory(), wrapped.getBlendingData());
      this.wrapped = wrapped;
      this.allowWrites = allowWrites;
   }

   public @Nullable BlockEntity getBlockEntity(final BlockPos pos) {
      return this.wrapped.getBlockEntity(pos);
   }

   public BlockState getBlockState(final BlockPos pos) {
      return this.wrapped.getBlockState(pos);
   }

   public FluidState getFluidState(final BlockPos pos) {
      return this.wrapped.getFluidState(pos);
   }

   public LevelChunkSection getSection(final int sectionIndex) {
      return this.allowWrites ? this.wrapped.getSection(sectionIndex) : super.getSection(sectionIndex);
   }

   public @Nullable BlockState setBlockState(final BlockPos pos, final BlockState state, final @Block.UpdateFlags int flags) {
      return this.allowWrites ? this.wrapped.setBlockState(pos, state, flags) : null;
   }

   public void setBlockEntity(final BlockEntity blockEntity) {
      if (this.allowWrites) {
         this.wrapped.setBlockEntity(blockEntity);
      }

   }

   public void addEntity(final Entity entity) {
      if (this.allowWrites) {
         this.wrapped.addEntity(entity);
      }

   }

   public void setPersistedStatus(final ChunkStatus status) {
      if (this.allowWrites) {
         super.setPersistedStatus(status);
      }

   }

   public LevelChunkSection[] getSections() {
      return this.wrapped.getSections();
   }

   public void setHeightmap(final Heightmap.Types key, final long[] data) {
   }

   private Heightmap.Types fixType(final Heightmap.Types type) {
      if (type == Heightmap.Types.WORLD_SURFACE_WG) {
         return Heightmap.Types.WORLD_SURFACE;
      } else {
         return type == Heightmap.Types.OCEAN_FLOOR_WG ? Heightmap.Types.OCEAN_FLOOR : type;
      }
   }

   public Heightmap getOrCreateHeightmapUnprimed(final Heightmap.Types type) {
      return this.wrapped.getOrCreateHeightmapUnprimed(type);
   }

   public int getHeight(final Heightmap.Types type, final int x, final int z) {
      return this.wrapped.getHeight(this.fixType(type), x, z);
   }

   public Holder<Biome> getNoiseBiome(final int quartX, final int quartY, final int quartZ) {
      return this.wrapped.getNoiseBiome(quartX, quartY, quartZ);
   }

   public ChunkPos getPos() {
      return this.wrapped.getPos();
   }

   public @Nullable StructureStart getStartForStructure(final Structure structure) {
      return this.wrapped.getStartForStructure(structure);
   }

   public void setStartForStructure(final Structure structure, final StructureStart structureStart) {
   }

   public Map<Structure, StructureStart> getAllStarts() {
      return this.wrapped.getAllStarts();
   }

   public void setAllStarts(final Map<Structure, StructureStart> starts) {
   }

   public LongSet getReferencesForStructure(final Structure structure) {
      return this.wrapped.getReferencesForStructure(structure);
   }

   public void addReferenceForStructure(final Structure structure, final long reference) {
   }

   public Map<Structure, LongSet> getAllReferences() {
      return this.wrapped.getAllReferences();
   }

   public void setAllReferences(final Map<Structure, LongSet> data) {
   }

   public void markUnsaved() {
      this.wrapped.markUnsaved();
   }

   public boolean canBeSerialized() {
      return false;
   }

   public boolean tryMarkSaved() {
      return false;
   }

   public boolean isUnsaved() {
      return false;
   }

   public ChunkStatus getPersistedStatus() {
      return this.wrapped.getPersistedStatus();
   }

   public void removeBlockEntity(final BlockPos pos) {
   }

   public void markPosForPostprocessing(final BlockPos blockPos) {
   }

   public void setBlockEntityNbt(final CompoundTag entityTag) {
   }

   public @Nullable CompoundTag getBlockEntityNbt(final BlockPos blockPos) {
      return this.wrapped.getBlockEntityNbt(blockPos);
   }

   public @Nullable CompoundTag getBlockEntityNbtForSaving(final BlockPos blockPos, final HolderLookup.Provider registryAccess) {
      return this.wrapped.getBlockEntityNbtForSaving(blockPos, registryAccess);
   }

   public void findBlocks(final Predicate<BlockState> predicate, final BiConsumer<BlockPos, BlockState> consumer) {
      this.wrapped.findBlocks(predicate, consumer);
   }

   public TickContainerAccess<Block> getBlockTicks() {
      return this.allowWrites ? this.wrapped.getBlockTicks() : BlackholeTickAccess.emptyContainer();
   }

   public TickContainerAccess<Fluid> getFluidTicks() {
      return this.allowWrites ? this.wrapped.getFluidTicks() : BlackholeTickAccess.emptyContainer();
   }

   public ChunkAccess.PackedTicks getTicksForSerialization(final long currentTick) {
      return this.wrapped.getTicksForSerialization(currentTick);
   }

   public @Nullable BlendingData getBlendingData() {
      return this.wrapped.getBlendingData();
   }

   public CarvingMask getCarvingMask() {
      if (this.allowWrites) {
         return super.getCarvingMask();
      } else {
         throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("Meaningless in this context"));
      }
   }

   public CarvingMask getOrCreateCarvingMask() {
      if (this.allowWrites) {
         return super.getOrCreateCarvingMask();
      } else {
         throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("Meaningless in this context"));
      }
   }

   public LevelChunk getWrapped() {
      return this.wrapped;
   }

   public boolean isLightCorrect() {
      return this.wrapped.isLightCorrect();
   }

   public void setLightCorrect(final boolean isLightCorrect) {
      this.wrapped.setLightCorrect(isLightCorrect);
   }

   public void fillBiomesFromNoise(final BiomeResolver biomeResolver, final Climate.Sampler sampler) {
      if (this.allowWrites) {
         this.wrapped.fillBiomesFromNoise(biomeResolver, sampler);
      }

   }

   public void initializeLightSources() {
      this.wrapped.initializeLightSources();
   }

   public ChunkSkyLightSources getSkyLightSources() {
      return this.wrapped.getSkyLightSources();
   }
}
