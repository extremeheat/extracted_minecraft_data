package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ChunkTickList;
import net.minecraft.world.level.EmptyTickList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelChunk implements ChunkAccess {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final TickingBlockEntity NULL_TICKER = new TickingBlockEntity() {
      public void tick() {
      }

      public boolean isRemoved() {
         return true;
      }

      public BlockPos getPos() {
         return BlockPos.ZERO;
      }

      public String getType() {
         return "<null>";
      }
   };
   @Nullable
   public static final LevelChunkSection EMPTY_SECTION = null;
   private final LevelChunkSection[] sections;
   private ChunkBiomeContainer biomes;
   private final Map<BlockPos, CompoundTag> pendingBlockEntities;
   private final Map<BlockPos, LevelChunk.RebindableTickingBlockEntityWrapper> tickersInLevel;
   private boolean loaded;
   private final Level level;
   private final Map<Heightmap.Types, Heightmap> heightmaps;
   private final UpgradeData upgradeData;
   private final Map<BlockPos, BlockEntity> blockEntities;
   private final Map<StructureFeature<?>, StructureStart<?>> structureStarts;
   private final Map<StructureFeature<?>, LongSet> structuresRefences;
   private final ShortList[] postProcessing;
   private TickList<Block> blockTicks;
   private TickList<Fluid> liquidTicks;
   private volatile boolean unsaved;
   private long inhabitedTime;
   @Nullable
   private Supplier<ChunkHolder.FullChunkStatus> fullStatus;
   @Nullable
   private Consumer<LevelChunk> postLoad;
   private final ChunkPos chunkPos;
   private volatile boolean isLightCorrect;

   public LevelChunk(Level var1, ChunkPos var2, ChunkBiomeContainer var3) {
      this(var1, var2, var3, UpgradeData.EMPTY, EmptyTickList.empty(), EmptyTickList.empty(), 0L, (LevelChunkSection[])null, (Consumer)null);
   }

   public LevelChunk(Level var1, ChunkPos var2, ChunkBiomeContainer var3, UpgradeData var4, TickList<Block> var5, TickList<Fluid> var6, long var7, @Nullable LevelChunkSection[] var9, @Nullable Consumer<LevelChunk> var10) {
      super();
      this.pendingBlockEntities = Maps.newHashMap();
      this.tickersInLevel = Maps.newHashMap();
      this.heightmaps = Maps.newEnumMap(Heightmap.Types.class);
      this.blockEntities = Maps.newHashMap();
      this.structureStarts = Maps.newHashMap();
      this.structuresRefences = Maps.newHashMap();
      this.level = var1;
      this.chunkPos = var2;
      this.upgradeData = var4;
      Heightmap.Types[] var11 = Heightmap.Types.values();
      int var12 = var11.length;

      for(int var13 = 0; var13 < var12; ++var13) {
         Heightmap.Types var14 = var11[var13];
         if (ChunkStatus.FULL.heightmapsAfter().contains(var14)) {
            this.heightmaps.put(var14, new Heightmap(this, var14));
         }
      }

      this.biomes = var3;
      this.blockTicks = var5;
      this.liquidTicks = var6;
      this.inhabitedTime = var7;
      this.postLoad = var10;
      this.sections = new LevelChunkSection[var1.getSectionsCount()];
      if (var9 != null) {
         if (this.sections.length == var9.length) {
            System.arraycopy(var9, 0, this.sections, 0, this.sections.length);
         } else {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", var9.length, this.sections.length);
         }
      }

      this.postProcessing = new ShortList[var1.getSectionsCount()];
   }

   public LevelChunk(ServerLevel var1, ProtoChunk var2, @Nullable Consumer<LevelChunk> var3) {
      this(var1, var2.getPos(), var2.getBiomes(), var2.getUpgradeData(), var2.getBlockTicks(), var2.getLiquidTicks(), var2.getInhabitedTime(), var2.getSections(), var3);
      Iterator var4 = var2.getBlockEntities().values().iterator();

      while(var4.hasNext()) {
         BlockEntity var5 = (BlockEntity)var4.next();
         this.setBlockEntity(var5);
      }

      this.pendingBlockEntities.putAll(var2.getBlockEntityNbts());

      for(int var6 = 0; var6 < var2.getPostProcessing().length; ++var6) {
         this.postProcessing[var6] = var2.getPostProcessing()[var6];
      }

      this.setAllStarts(var2.getAllStarts());
      this.setAllReferences(var2.getAllReferences());
      var4 = var2.getHeightmaps().iterator();

      while(var4.hasNext()) {
         Entry var7 = (Entry)var4.next();
         if (ChunkStatus.FULL.heightmapsAfter().contains(var7.getKey())) {
            this.getOrCreateHeightmapUnprimed((Heightmap.Types)var7.getKey()).setRawData(((Heightmap)var7.getValue()).getRawData());
         }
      }

      this.setLightCorrect(var2.isLightCorrect());
      this.unsaved = true;
   }

   public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types var1) {
      return (Heightmap)this.heightmaps.computeIfAbsent(var1, (var1x) -> {
         return new Heightmap(this, var1x);
      });
   }

   public Set<BlockPos> getBlockEntitiesPos() {
      HashSet var1 = Sets.newHashSet(this.pendingBlockEntities.keySet());
      var1.addAll(this.blockEntities.keySet());
      return var1;
   }

   public LevelChunkSection[] getSections() {
      return this.sections;
   }

   public BlockState getBlockState(BlockPos var1) {
      int var2 = var1.getX();
      int var3 = var1.getY();
      int var4 = var1.getZ();
      if (this.level.isDebug()) {
         BlockState var9 = null;
         if (var3 == 60) {
            var9 = Blocks.BARRIER.defaultBlockState();
         }

         if (var3 == 70) {
            var9 = DebugLevelSource.getBlockStateFor(var2, var4);
         }

         return var9 == null ? Blocks.AIR.defaultBlockState() : var9;
      } else {
         try {
            int var5 = this.getSectionIndex(var3);
            if (var5 >= 0 && var5 < this.sections.length) {
               LevelChunkSection var10 = this.sections[var5];
               if (!LevelChunkSection.isEmpty(var10)) {
                  return var10.getBlockState(var2 & 15, var3 & 15, var4 & 15);
               }
            }

            return Blocks.AIR.defaultBlockState();
         } catch (Throwable var8) {
            CrashReport var6 = CrashReport.forThrowable(var8, "Getting block state");
            CrashReportCategory var7 = var6.addCategory("Block being got");
            var7.setDetail("Location", () -> {
               return CrashReportCategory.formatLocation(this, var2, var3, var4);
            });
            throw new ReportedException(var6);
         }
      }
   }

   public FluidState getFluidState(BlockPos var1) {
      return this.getFluidState(var1.getX(), var1.getY(), var1.getZ());
   }

   public FluidState getFluidState(int var1, int var2, int var3) {
      try {
         int var4 = this.getSectionIndex(var2);
         if (var4 >= 0 && var4 < this.sections.length) {
            LevelChunkSection var8 = this.sections[var4];
            if (!LevelChunkSection.isEmpty(var8)) {
               return var8.getFluidState(var1 & 15, var2 & 15, var3 & 15);
            }
         }

         return Fluids.EMPTY.defaultFluidState();
      } catch (Throwable var7) {
         CrashReport var5 = CrashReport.forThrowable(var7, "Getting fluid state");
         CrashReportCategory var6 = var5.addCategory("Block being got");
         var6.setDetail("Location", () -> {
            return CrashReportCategory.formatLocation(this, var1, var2, var3);
         });
         throw new ReportedException(var5);
      }
   }

   @Nullable
   public BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3) {
      int var4 = var1.getY();
      int var5 = this.getSectionIndex(var4);
      LevelChunkSection var6 = this.sections[var5];
      if (var6 == EMPTY_SECTION) {
         if (var2.isAir()) {
            return null;
         }

         var6 = new LevelChunkSection(SectionPos.blockToSectionCoord(var4));
         this.sections[var5] = var6;
      }

      boolean var7 = var6.isEmpty();
      int var8 = var1.getX() & 15;
      int var9 = var4 & 15;
      int var10 = var1.getZ() & 15;
      BlockState var11 = var6.setBlockState(var8, var9, var10, var2);
      if (var11 == var2) {
         return null;
      } else {
         Block var12 = var2.getBlock();
         ((Heightmap)this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING)).update(var8, var4, var10, var2);
         ((Heightmap)this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES)).update(var8, var4, var10, var2);
         ((Heightmap)this.heightmaps.get(Heightmap.Types.OCEAN_FLOOR)).update(var8, var4, var10, var2);
         ((Heightmap)this.heightmaps.get(Heightmap.Types.WORLD_SURFACE)).update(var8, var4, var10, var2);
         boolean var13 = var6.isEmpty();
         if (var7 != var13) {
            this.level.getChunkSource().getLightEngine().updateSectionStatus(var1, var13);
         }

         boolean var14 = var11.hasBlockEntity();
         if (!this.level.isClientSide) {
            var11.onRemove(this.level, var1, var2, var3);
         } else if (!var11.is(var12) && var14) {
            this.removeBlockEntity(var1);
         }

         if (!var6.getBlockState(var8, var9, var10).is(var12)) {
            return null;
         } else {
            if (!this.level.isClientSide) {
               var2.onPlace(this.level, var1, var11, var3);
            }

            if (var2.hasBlockEntity()) {
               BlockEntity var15 = this.getBlockEntity(var1, LevelChunk.EntityCreationType.CHECK);
               if (var15 == null) {
                  var15 = ((EntityBlock)var12).newBlockEntity(var1, var2);
                  if (var15 != null) {
                     this.addAndRegisterBlockEntity(var15);
                  }
               } else {
                  var15.setBlockState(var2);
                  this.updateBlockEntityTicker(var15);
               }
            }

            this.unsaved = true;
            return var11;
         }
      }
   }

   @Nullable
   public LevelLightEngine getLightEngine() {
      return this.level.getChunkSource().getLightEngine();
   }

   @Deprecated
   public void addEntity(Entity var1) {
   }

   public void setHeightmap(Heightmap.Types var1, long[] var2) {
      ((Heightmap)this.heightmaps.get(var1)).setRawData(var2);
   }

   public int getHeight(Heightmap.Types var1, int var2, int var3) {
      return ((Heightmap)this.heightmaps.get(var1)).getFirstAvailable(var2 & 15, var3 & 15) - 1;
   }

   @Nullable
   private BlockEntity createBlockEntity(BlockPos var1) {
      BlockState var2 = this.getBlockState(var1);
      return !var2.hasBlockEntity() ? null : ((EntityBlock)var2.getBlock()).newBlockEntity(var1, var2);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1) {
      return this.getBlockEntity(var1, LevelChunk.EntityCreationType.CHECK);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1, LevelChunk.EntityCreationType var2) {
      BlockEntity var3 = (BlockEntity)this.blockEntities.get(var1);
      if (var3 == null) {
         CompoundTag var4 = (CompoundTag)this.pendingBlockEntities.remove(var1);
         if (var4 != null) {
            BlockEntity var5 = this.promotePendingBlockEntity(var1, var4);
            if (var5 != null) {
               return var5;
            }
         }
      }

      if (var3 == null) {
         if (var2 == LevelChunk.EntityCreationType.IMMEDIATE) {
            var3 = this.createBlockEntity(var1);
            if (var3 != null) {
               this.addAndRegisterBlockEntity(var3);
            }
         }
      } else if (var3.isRemoved()) {
         this.blockEntities.remove(var1);
         return null;
      }

      return var3;
   }

   public void addAndRegisterBlockEntity(BlockEntity var1) {
      this.setBlockEntity(var1);
      if (this.isInLevel()) {
         this.updateBlockEntityTicker(var1);
      }

   }

   private boolean isInLevel() {
      return this.loaded || this.level.isClientSide();
   }

   private boolean isTicking(BlockPos var1) {
      return (this.level.isClientSide() || this.getFullStatus().isOrAfter(ChunkHolder.FullChunkStatus.TICKING)) && this.level.getWorldBorder().isWithinBounds(var1);
   }

   public void setBlockEntity(BlockEntity var1) {
      BlockPos var2 = var1.getBlockPos();
      if (this.getBlockState(var2).hasBlockEntity()) {
         var1.setLevel(this.level);
         var1.clearRemoved();
         BlockEntity var3 = (BlockEntity)this.blockEntities.put(var2.immutable(), var1);
         if (var3 != null && var3 != var1) {
            var3.setRemoved();
         }

      }
   }

   public void setBlockEntityNbt(CompoundTag var1) {
      this.pendingBlockEntities.put(new BlockPos(var1.getInt("x"), var1.getInt("y"), var1.getInt("z")), var1);
   }

   @Nullable
   public CompoundTag getBlockEntityNbtForSaving(BlockPos var1) {
      BlockEntity var2 = this.getBlockEntity(var1);
      CompoundTag var3;
      if (var2 != null && !var2.isRemoved()) {
         var3 = var2.save(new CompoundTag());
         var3.putBoolean("keepPacked", false);
         return var3;
      } else {
         var3 = (CompoundTag)this.pendingBlockEntities.get(var1);
         if (var3 != null) {
            var3 = var3.copy();
            var3.putBoolean("keepPacked", true);
         }

         return var3;
      }
   }

   public void removeBlockEntity(BlockPos var1) {
      if (this.isInLevel()) {
         BlockEntity var2 = (BlockEntity)this.blockEntities.remove(var1);
         if (var2 != null) {
            var2.setRemoved();
         }
      }

      this.removeBlockEntityTicker(var1);
   }

   private void removeBlockEntityTicker(BlockPos var1) {
      LevelChunk.RebindableTickingBlockEntityWrapper var2 = (LevelChunk.RebindableTickingBlockEntityWrapper)this.tickersInLevel.remove(var1);
      if (var2 != null) {
         var2.rebind(NULL_TICKER);
      }

   }

   public void runPostLoad() {
      if (this.postLoad != null) {
         this.postLoad.accept(this);
         this.postLoad = null;
      }

   }

   public void markUnsaved() {
      this.unsaved = true;
   }

   public boolean isEmpty() {
      return false;
   }

   public ChunkPos getPos() {
      return this.chunkPos;
   }

   public void replaceWithPacketData(@Nullable ChunkBiomeContainer var1, FriendlyByteBuf var2, CompoundTag var3, int var4) {
      boolean var5 = var1 != null;
      if (var5) {
         this.blockEntities.values().forEach(this::onBlockEntityRemove);
         this.blockEntities.clear();
      } else {
         this.blockEntities.values().removeIf((var2x) -> {
            if (this.isPositionInSection(var4, var2x.getBlockPos())) {
               var2x.setRemoved();
               return true;
            } else {
               return false;
            }
         });
      }

      for(int var6 = 0; var6 < this.sections.length; ++var6) {
         LevelChunkSection var7 = this.sections[var6];
         if ((var4 & 1 << var6) == 0) {
            if (var5 && var7 != EMPTY_SECTION) {
               this.sections[var6] = EMPTY_SECTION;
            }
         } else {
            if (var7 == EMPTY_SECTION) {
               var7 = new LevelChunkSection(this.getSectionYFromSectionIndex(var6));
               this.sections[var6] = var7;
            }

            var7.read(var2);
         }
      }

      if (var1 != null) {
         this.biomes = var1;
      }

      Heightmap.Types[] var11 = Heightmap.Types.values();
      int var12 = var11.length;

      for(int var8 = 0; var8 < var12; ++var8) {
         Heightmap.Types var9 = var11[var8];
         String var10 = var9.getSerializationKey();
         if (var3.contains(var10, 12)) {
            this.setHeightmap(var9, var3.getLongArray(var10));
         }
      }

   }

   private void onBlockEntityRemove(BlockEntity var1) {
      var1.setRemoved();
      this.tickersInLevel.remove(var1.getBlockPos());
   }

   private boolean isPositionInSection(int var1, BlockPos var2) {
      return (var1 & 1 << this.getSectionIndex(var2.getY())) != 0;
   }

   public ChunkBiomeContainer getBiomes() {
      return this.biomes;
   }

   public void setLoaded(boolean var1) {
      this.loaded = var1;
   }

   public Level getLevel() {
      return this.level;
   }

   public Collection<Entry<Heightmap.Types, Heightmap>> getHeightmaps() {
      return Collections.unmodifiableSet(this.heightmaps.entrySet());
   }

   public Map<BlockPos, BlockEntity> getBlockEntities() {
      return this.blockEntities;
   }

   public CompoundTag getBlockEntityNbt(BlockPos var1) {
      return (CompoundTag)this.pendingBlockEntities.get(var1);
   }

   public Stream<BlockPos> getLights() {
      return StreamSupport.stream(BlockPos.betweenClosed(this.chunkPos.getMinBlockX(), 0, this.chunkPos.getMinBlockZ(), this.chunkPos.getMaxBlockX(), this.getMaxBuildHeight() - 1, this.chunkPos.getMaxBlockZ()).spliterator(), false).filter((var1) -> {
         return this.getBlockState(var1).getLightEmission() != 0;
      });
   }

   public TickList<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public TickList<Fluid> getLiquidTicks() {
      return this.liquidTicks;
   }

   public void setUnsaved(boolean var1) {
      this.unsaved = var1;
   }

   public boolean isUnsaved() {
      return this.unsaved;
   }

   @Nullable
   public StructureStart<?> getStartForFeature(StructureFeature<?> var1) {
      return (StructureStart)this.structureStarts.get(var1);
   }

   public void setStartForFeature(StructureFeature<?> var1, StructureStart<?> var2) {
      this.structureStarts.put(var1, var2);
   }

   public Map<StructureFeature<?>, StructureStart<?>> getAllStarts() {
      return this.structureStarts;
   }

   public void setAllStarts(Map<StructureFeature<?>, StructureStart<?>> var1) {
      this.structureStarts.clear();
      this.structureStarts.putAll(var1);
   }

   public LongSet getReferencesForFeature(StructureFeature<?> var1) {
      return (LongSet)this.structuresRefences.computeIfAbsent(var1, (var0) -> {
         return new LongOpenHashSet();
      });
   }

   public void addReferenceForFeature(StructureFeature<?> var1, long var2) {
      ((LongSet)this.structuresRefences.computeIfAbsent(var1, (var0) -> {
         return new LongOpenHashSet();
      })).add(var2);
   }

   public Map<StructureFeature<?>, LongSet> getAllReferences() {
      return this.structuresRefences;
   }

   public void setAllReferences(Map<StructureFeature<?>, LongSet> var1) {
      this.structuresRefences.clear();
      this.structuresRefences.putAll(var1);
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setInhabitedTime(long var1) {
      this.inhabitedTime = var1;
   }

   public void postProcessGeneration() {
      ChunkPos var1 = this.getPos();

      for(int var2 = 0; var2 < this.postProcessing.length; ++var2) {
         if (this.postProcessing[var2] != null) {
            ShortListIterator var3 = this.postProcessing[var2].iterator();

            while(var3.hasNext()) {
               Short var4 = (Short)var3.next();
               BlockPos var5 = ProtoChunk.unpackOffsetCoordinates(var4, this.getSectionYFromSectionIndex(var2), var1);
               BlockState var6 = this.getBlockState(var5);
               BlockState var7 = Block.updateFromNeighbourShapes(var6, this.level, var5);
               this.level.setBlock(var5, var7, 20);
            }

            this.postProcessing[var2].clear();
         }
      }

      this.unpackTicks();
      UnmodifiableIterator var8 = ImmutableList.copyOf(this.pendingBlockEntities.keySet()).iterator();

      while(var8.hasNext()) {
         BlockPos var9 = (BlockPos)var8.next();
         this.getBlockEntity(var9);
      }

      this.pendingBlockEntities.clear();
      this.upgradeData.upgrade(this);
   }

   @Nullable
   private BlockEntity promotePendingBlockEntity(BlockPos var1, CompoundTag var2) {
      BlockState var4 = this.getBlockState(var1);
      BlockEntity var3;
      if ("DUMMY".equals(var2.getString("id"))) {
         if (var4.hasBlockEntity()) {
            var3 = ((EntityBlock)var4.getBlock()).newBlockEntity(var1, var4);
         } else {
            var3 = null;
            LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", var1, var4);
         }
      } else {
         var3 = BlockEntity.loadStatic(var1, var4, var2);
      }

      if (var3 != null) {
         var3.setLevel(this.level);
         this.addAndRegisterBlockEntity(var3);
      } else {
         LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", var4, var1);
      }

      return var3;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public ShortList[] getPostProcessing() {
      return this.postProcessing;
   }

   public void unpackTicks() {
      if (this.blockTicks instanceof ProtoTickList) {
         ((ProtoTickList)this.blockTicks).copyOut(this.level.getBlockTicks(), (var1) -> {
            return this.getBlockState(var1).getBlock();
         });
         this.blockTicks = EmptyTickList.empty();
      } else if (this.blockTicks instanceof ChunkTickList) {
         ((ChunkTickList)this.blockTicks).copyOut(this.level.getBlockTicks());
         this.blockTicks = EmptyTickList.empty();
      }

      if (this.liquidTicks instanceof ProtoTickList) {
         ((ProtoTickList)this.liquidTicks).copyOut(this.level.getLiquidTicks(), (var1) -> {
            return this.getFluidState(var1).getType();
         });
         this.liquidTicks = EmptyTickList.empty();
      } else if (this.liquidTicks instanceof ChunkTickList) {
         ((ChunkTickList)this.liquidTicks).copyOut(this.level.getLiquidTicks());
         this.liquidTicks = EmptyTickList.empty();
      }

   }

   public void packTicks(ServerLevel var1) {
      if (this.blockTicks == EmptyTickList.empty()) {
         this.blockTicks = new ChunkTickList(Registry.BLOCK::getKey, var1.getBlockTicks().fetchTicksInChunk(this.chunkPos, true, false), var1.getGameTime());
         this.setUnsaved(true);
      }

      if (this.liquidTicks == EmptyTickList.empty()) {
         this.liquidTicks = new ChunkTickList(Registry.FLUID::getKey, var1.getLiquidTicks().fetchTicksInChunk(this.chunkPos, true, false), var1.getGameTime());
         this.setUnsaved(true);
      }

   }

   public int getSectionsCount() {
      return this.level.getSectionsCount();
   }

   public int getMinSection() {
      return this.level.getMinSection();
   }

   public ChunkStatus getStatus() {
      return ChunkStatus.FULL;
   }

   public ChunkHolder.FullChunkStatus getFullStatus() {
      return this.fullStatus == null ? ChunkHolder.FullChunkStatus.BORDER : (ChunkHolder.FullChunkStatus)this.fullStatus.get();
   }

   public void setFullStatus(Supplier<ChunkHolder.FullChunkStatus> var1) {
      this.fullStatus = var1;
   }

   public boolean isLightCorrect() {
      return this.isLightCorrect;
   }

   public void setLightCorrect(boolean var1) {
      this.isLightCorrect = var1;
      this.setUnsaved(true);
   }

   public void invalidateAllBlockEntities() {
      this.blockEntities.values().forEach(this::onBlockEntityRemove);
   }

   public void registerAllBlockEntitiesAfterLevelLoad() {
      this.blockEntities.values().forEach(this::updateBlockEntityTicker);
   }

   private <T extends BlockEntity> void updateBlockEntityTicker(T var1) {
      BlockState var2 = var1.getBlockState();
      BlockEntityTicker var3 = var2.getTicker(this.level, var1.getType());
      if (var3 == null) {
         this.removeBlockEntityTicker(var1.getBlockPos());
      } else {
         this.tickersInLevel.compute(var1.getBlockPos(), (var3x, var4) -> {
            TickingBlockEntity var5 = this.createTicker(var1, var3);
            if (var4 != null) {
               var4.rebind(var5);
               return var4;
            } else if (this.isInLevel()) {
               LevelChunk.RebindableTickingBlockEntityWrapper var6 = new LevelChunk.RebindableTickingBlockEntityWrapper(var5);
               this.level.addBlockEntityTicker(var6);
               return var6;
            } else {
               return null;
            }
         });
      }

   }

   private <T extends BlockEntity> TickingBlockEntity createTicker(T var1, BlockEntityTicker<T> var2) {
      return new LevelChunk.BoundTickingBlockEntity(var1, var2);
   }

   class RebindableTickingBlockEntityWrapper implements TickingBlockEntity {
      private TickingBlockEntity ticker;

      private RebindableTickingBlockEntityWrapper(TickingBlockEntity var2) {
         super();
         this.ticker = var2;
      }

      private void rebind(TickingBlockEntity var1) {
         this.ticker = var1;
      }

      public void tick() {
         this.ticker.tick();
      }

      public boolean isRemoved() {
         return this.ticker.isRemoved();
      }

      public BlockPos getPos() {
         return this.ticker.getPos();
      }

      public String getType() {
         return this.ticker.getType();
      }

      public String toString() {
         return this.ticker.toString() + " <wrapped>";
      }

      // $FF: synthetic method
      RebindableTickingBlockEntityWrapper(TickingBlockEntity var2, Object var3) {
         this(var2);
      }
   }

   class BoundTickingBlockEntity<T extends BlockEntity> implements TickingBlockEntity {
      private final T blockEntity;
      private final BlockEntityTicker<T> ticker;
      private boolean loggedInvalidBlockState;

      private BoundTickingBlockEntity(T var2, BlockEntityTicker<T> var3) {
         super();
         this.blockEntity = var2;
         this.ticker = var3;
      }

      public void tick() {
         if (!this.blockEntity.isRemoved() && this.blockEntity.hasLevel()) {
            BlockPos var1 = this.blockEntity.getBlockPos();
            if (LevelChunk.this.isTicking(var1)) {
               try {
                  ProfilerFiller var2 = LevelChunk.this.level.getProfiler();
                  var2.push(this::getType);
                  BlockState var6 = LevelChunk.this.getBlockState(var1);
                  if (this.blockEntity.getType().isValid(var6)) {
                     this.ticker.tick(LevelChunk.this.level, this.blockEntity.getBlockPos(), var6, this.blockEntity);
                     this.loggedInvalidBlockState = false;
                  } else if (!this.loggedInvalidBlockState) {
                     this.loggedInvalidBlockState = true;
                     LevelChunk.LOGGER.warn("Block entity {} @ {} state {} invalid for ticking:", new org.apache.logging.log4j.util.Supplier[]{this::getType, this::getPos, () -> {
                        return var6;
                     }});
                  }

                  var2.pop();
               } catch (Throwable var5) {
                  CrashReport var3 = CrashReport.forThrowable(var5, "Ticking block entity");
                  CrashReportCategory var4 = var3.addCategory("Block entity being ticked");
                  this.blockEntity.fillCrashReportCategory(var4);
                  throw new ReportedException(var3);
               }
            }
         }

      }

      public boolean isRemoved() {
         return this.blockEntity.isRemoved();
      }

      public BlockPos getPos() {
         return this.blockEntity.getBlockPos();
      }

      public String getType() {
         return BlockEntityType.getKey(this.blockEntity.getType()).toString();
      }

      public String toString() {
         return "Level ticker for " + this.getType() + "@" + this.getPos();
      }

      // $FF: synthetic method
      BoundTickingBlockEntity(BlockEntity var2, BlockEntityTicker var3, Object var4) {
         this(var2, var3);
      }
   }

   public static enum EntityCreationType {
      IMMEDIATE,
      QUEUED,
      CHECK;

      private EntityCreationType() {
      }
   }
}
