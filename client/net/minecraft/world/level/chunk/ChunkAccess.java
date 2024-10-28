package net.minecraft.world.level.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEventListenerRegistry;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.SerializableTickContainer;
import net.minecraft.world.ticks.TickContainerAccess;
import org.slf4j.Logger;

public abstract class ChunkAccess implements BlockGetter, BiomeManager.NoiseBiomeSource, LightChunk, StructureAccess {
   public static final int NO_FILLED_SECTION = -1;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final LongSet EMPTY_REFERENCE_SET = new LongOpenHashSet();
   protected final ShortList[] postProcessing;
   protected volatile boolean unsaved;
   private volatile boolean isLightCorrect;
   protected final ChunkPos chunkPos;
   private long inhabitedTime;
   /** @deprecated */
   @Nullable
   @Deprecated
   private BiomeGenerationSettings carverBiomeSettings;
   @Nullable
   protected NoiseChunk noiseChunk;
   protected final UpgradeData upgradeData;
   @Nullable
   protected BlendingData blendingData;
   protected final Map<Heightmap.Types, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Types.class);
   protected ChunkSkyLightSources skyLightSources;
   private final Map<Structure, StructureStart> structureStarts = Maps.newHashMap();
   private final Map<Structure, LongSet> structuresRefences = Maps.newHashMap();
   protected final Map<BlockPos, CompoundTag> pendingBlockEntities = Maps.newHashMap();
   protected final Map<BlockPos, BlockEntity> blockEntities = new Object2ObjectOpenHashMap();
   protected final LevelHeightAccessor levelHeightAccessor;
   protected final LevelChunkSection[] sections;

   public ChunkAccess(ChunkPos var1, UpgradeData var2, LevelHeightAccessor var3, Registry<Biome> var4, long var5, @Nullable LevelChunkSection[] var7, @Nullable BlendingData var8) {
      super();
      this.chunkPos = var1;
      this.upgradeData = var2;
      this.levelHeightAccessor = var3;
      this.sections = new LevelChunkSection[var3.getSectionsCount()];
      this.inhabitedTime = var5;
      this.postProcessing = new ShortList[var3.getSectionsCount()];
      this.blendingData = var8;
      this.skyLightSources = new ChunkSkyLightSources(var3);
      if (var7 != null) {
         if (this.sections.length == var7.length) {
            System.arraycopy(var7, 0, this.sections, 0, this.sections.length);
         } else {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", var7.length, this.sections.length);
         }
      }

      replaceMissingSections(var4, this.sections);
   }

   private static void replaceMissingSections(Registry<Biome> var0, LevelChunkSection[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2] == null) {
            var1[var2] = new LevelChunkSection(var0);
         }
      }

   }

   public GameEventListenerRegistry getListenerRegistry(int var1) {
      return GameEventListenerRegistry.NOOP;
   }

   @Nullable
   public abstract BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3);

   public abstract void setBlockEntity(BlockEntity var1);

   public abstract void addEntity(Entity var1);

   public int getHighestFilledSectionIndex() {
      LevelChunkSection[] var1 = this.getSections();

      for(int var2 = var1.length - 1; var2 >= 0; --var2) {
         LevelChunkSection var3 = var1[var2];
         if (!var3.hasOnlyAir()) {
            return var2;
         }
      }

      return -1;
   }

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public int getHighestSectionPosition() {
      int var1 = this.getHighestFilledSectionIndex();
      return var1 == -1 ? this.getMinBuildHeight() : SectionPos.sectionToBlockCoord(this.getSectionYFromSectionIndex(var1));
   }

   public Set<BlockPos> getBlockEntitiesPos() {
      HashSet var1 = Sets.newHashSet(this.pendingBlockEntities.keySet());
      var1.addAll(this.blockEntities.keySet());
      return var1;
   }

   public LevelChunkSection[] getSections() {
      return this.sections;
   }

   public LevelChunkSection getSection(int var1) {
      return this.getSections()[var1];
   }

   public Collection<Map.Entry<Heightmap.Types, Heightmap>> getHeightmaps() {
      return Collections.unmodifiableSet(this.heightmaps.entrySet());
   }

   public void setHeightmap(Heightmap.Types var1, long[] var2) {
      this.getOrCreateHeightmapUnprimed(var1).setRawData(this, var1, var2);
   }

   public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types var1) {
      return (Heightmap)this.heightmaps.computeIfAbsent(var1, (var1x) -> {
         return new Heightmap(this, var1x);
      });
   }

   public boolean hasPrimedHeightmap(Heightmap.Types var1) {
      return this.heightmaps.get(var1) != null;
   }

   public int getHeight(Heightmap.Types var1, int var2, int var3) {
      Heightmap var4 = (Heightmap)this.heightmaps.get(var1);
      if (var4 == null) {
         if (SharedConstants.IS_RUNNING_IN_IDE && this instanceof LevelChunk) {
            LOGGER.error("Unprimed heightmap: " + String.valueOf(var1) + " " + var2 + " " + var3);
         }

         Heightmap.primeHeightmaps(this, EnumSet.of(var1));
         var4 = (Heightmap)this.heightmaps.get(var1);
      }

      return var4.getFirstAvailable(var2 & 15, var3 & 15) - 1;
   }

   public ChunkPos getPos() {
      return this.chunkPos;
   }

   @Nullable
   public StructureStart getStartForStructure(Structure var1) {
      return (StructureStart)this.structureStarts.get(var1);
   }

   public void setStartForStructure(Structure var1, StructureStart var2) {
      this.structureStarts.put(var1, var2);
      this.unsaved = true;
   }

   public Map<Structure, StructureStart> getAllStarts() {
      return Collections.unmodifiableMap(this.structureStarts);
   }

   public void setAllStarts(Map<Structure, StructureStart> var1) {
      this.structureStarts.clear();
      this.structureStarts.putAll(var1);
      this.unsaved = true;
   }

   public LongSet getReferencesForStructure(Structure var1) {
      return (LongSet)this.structuresRefences.getOrDefault(var1, EMPTY_REFERENCE_SET);
   }

   public void addReferenceForStructure(Structure var1, long var2) {
      ((LongSet)this.structuresRefences.computeIfAbsent(var1, (var0) -> {
         return new LongOpenHashSet();
      })).add(var2);
      this.unsaved = true;
   }

   public Map<Structure, LongSet> getAllReferences() {
      return Collections.unmodifiableMap(this.structuresRefences);
   }

   public void setAllReferences(Map<Structure, LongSet> var1) {
      this.structuresRefences.clear();
      this.structuresRefences.putAll(var1);
      this.unsaved = true;
   }

   public boolean isYSpaceEmpty(int var1, int var2) {
      if (var1 < this.getMinBuildHeight()) {
         var1 = this.getMinBuildHeight();
      }

      if (var2 >= this.getMaxBuildHeight()) {
         var2 = this.getMaxBuildHeight() - 1;
      }

      for(int var3 = var1; var3 <= var2; var3 += 16) {
         if (!this.getSection(this.getSectionIndex(var3)).hasOnlyAir()) {
            return false;
         }
      }

      return true;
   }

   public void setUnsaved(boolean var1) {
      this.unsaved = var1;
   }

   public boolean isUnsaved() {
      return this.unsaved;
   }

   public abstract ChunkStatus getStatus();

   public ChunkStatus getHighestGeneratedStatus() {
      ChunkStatus var1 = this.getStatus();
      BelowZeroRetrogen var2 = this.getBelowZeroRetrogen();
      if (var2 != null) {
         ChunkStatus var3 = var2.targetStatus();
         return var3.isOrAfter(var1) ? var3 : var1;
      } else {
         return var1;
      }
   }

   public abstract void removeBlockEntity(BlockPos var1);

   public void markPosForPostprocessing(BlockPos var1) {
      LOGGER.warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", var1);
   }

   public ShortList[] getPostProcessing() {
      return this.postProcessing;
   }

   public void addPackedPostProcess(short var1, int var2) {
      getOrCreateOffsetList(this.getPostProcessing(), var2).add(var1);
   }

   public void setBlockEntityNbt(CompoundTag var1) {
      this.pendingBlockEntities.put(BlockEntity.getPosFromTag(var1), var1);
   }

   @Nullable
   public CompoundTag getBlockEntityNbt(BlockPos var1) {
      return (CompoundTag)this.pendingBlockEntities.get(var1);
   }

   @Nullable
   public abstract CompoundTag getBlockEntityNbtForSaving(BlockPos var1, HolderLookup.Provider var2);

   public final void findBlockLightSources(BiConsumer<BlockPos, BlockState> var1) {
      this.findBlocks((var0) -> {
         return var0.getLightEmission() != 0;
      }, var1);
   }

   public void findBlocks(Predicate<BlockState> var1, BiConsumer<BlockPos, BlockState> var2) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();

      for(int var4 = this.getMinSection(); var4 < this.getMaxSection(); ++var4) {
         LevelChunkSection var5 = this.getSection(this.getSectionIndexFromSectionY(var4));
         if (var5.maybeHas(var1)) {
            BlockPos var6 = SectionPos.of(this.chunkPos, var4).origin();

            for(int var7 = 0; var7 < 16; ++var7) {
               for(int var8 = 0; var8 < 16; ++var8) {
                  for(int var9 = 0; var9 < 16; ++var9) {
                     BlockState var10 = var5.getBlockState(var9, var7, var8);
                     if (var1.test(var10)) {
                        var2.accept(var3.setWithOffset(var6, var9, var7, var8), var10);
                     }
                  }
               }
            }
         }
      }

   }

   public abstract TickContainerAccess<Block> getBlockTicks();

   public abstract TickContainerAccess<Fluid> getFluidTicks();

   public abstract TicksToSave getTicksForSerialization();

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public boolean isOldNoiseGeneration() {
      return this.blendingData != null;
   }

   @Nullable
   public BlendingData getBlendingData() {
      return this.blendingData;
   }

   public void setBlendingData(BlendingData var1) {
      this.blendingData = var1;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void incrementInhabitedTime(long var1) {
      this.inhabitedTime += var1;
   }

   public void setInhabitedTime(long var1) {
      this.inhabitedTime = var1;
   }

   public static ShortList getOrCreateOffsetList(ShortList[] var0, int var1) {
      if (var0[var1] == null) {
         var0[var1] = new ShortArrayList();
      }

      return var0[var1];
   }

   public boolean isLightCorrect() {
      return this.isLightCorrect;
   }

   public void setLightCorrect(boolean var1) {
      this.isLightCorrect = var1;
      this.setUnsaved(true);
   }

   public int getMinBuildHeight() {
      return this.levelHeightAccessor.getMinBuildHeight();
   }

   public int getHeight() {
      return this.levelHeightAccessor.getHeight();
   }

   public NoiseChunk getOrCreateNoiseChunk(Function<ChunkAccess, NoiseChunk> var1) {
      if (this.noiseChunk == null) {
         this.noiseChunk = (NoiseChunk)var1.apply(this);
      }

      return this.noiseChunk;
   }

   /** @deprecated */
   @Deprecated
   public BiomeGenerationSettings carverBiome(Supplier<BiomeGenerationSettings> var1) {
      if (this.carverBiomeSettings == null) {
         this.carverBiomeSettings = (BiomeGenerationSettings)var1.get();
      }

      return this.carverBiomeSettings;
   }

   public Holder<Biome> getNoiseBiome(int var1, int var2, int var3) {
      try {
         int var4 = QuartPos.fromBlock(this.getMinBuildHeight());
         int var9 = var4 + QuartPos.fromBlock(this.getHeight()) - 1;
         int var10 = Mth.clamp(var2, var4, var9);
         int var7 = this.getSectionIndex(QuartPos.toBlock(var10));
         return this.sections[var7].getNoiseBiome(var1 & 3, var10 & 3, var3 & 3);
      } catch (Throwable var8) {
         CrashReport var5 = CrashReport.forThrowable(var8, "Getting biome");
         CrashReportCategory var6 = var5.addCategory("Biome being got");
         var6.setDetail("Location", () -> {
            return CrashReportCategory.formatLocation(this, var1, var2, var3);
         });
         throw new ReportedException(var5);
      }
   }

   public void fillBiomesFromNoise(BiomeResolver var1, Climate.Sampler var2) {
      ChunkPos var3 = this.getPos();
      int var4 = QuartPos.fromBlock(var3.getMinBlockX());
      int var5 = QuartPos.fromBlock(var3.getMinBlockZ());
      LevelHeightAccessor var6 = this.getHeightAccessorForGeneration();

      for(int var7 = var6.getMinSection(); var7 < var6.getMaxSection(); ++var7) {
         LevelChunkSection var8 = this.getSection(this.getSectionIndexFromSectionY(var7));
         int var9 = QuartPos.fromSection(var7);
         var8.fillBiomesFromNoise(var1, var2, var4, var9, var5);
      }

   }

   public boolean hasAnyStructureReferences() {
      return !this.getAllReferences().isEmpty();
   }

   @Nullable
   public BelowZeroRetrogen getBelowZeroRetrogen() {
      return null;
   }

   public boolean isUpgrading() {
      return this.getBelowZeroRetrogen() != null;
   }

   public LevelHeightAccessor getHeightAccessorForGeneration() {
      return this;
   }

   public void initializeLightSources() {
      this.skyLightSources.fillFrom(this);
   }

   public ChunkSkyLightSources getSkyLightSources() {
      return this.skyLightSources;
   }

   public static record TicksToSave(SerializableTickContainer<Block> blocks, SerializableTickContainer<Fluid> fluids) {
      public TicksToSave(SerializableTickContainer<Block> var1, SerializableTickContainer<Fluid> var2) {
         super();
         this.blocks = var1;
         this.fluids = var2;
      }

      public SerializableTickContainer<Block> blocks() {
         return this.blocks;
      }

      public SerializableTickContainer<Fluid> fluids() {
         return this.fluids;
      }
   }
}
