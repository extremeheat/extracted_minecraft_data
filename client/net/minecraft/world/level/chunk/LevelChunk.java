package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.EuclideanGameEventListenerRegistry;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.GameEventListenerRegistry;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.TickContainerAccess;
import org.slf4j.Logger;

public class LevelChunk extends ChunkAccess {
   static final Logger LOGGER = LogUtils.getLogger();
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
   private final Map<BlockPos, RebindableTickingBlockEntityWrapper> tickersInLevel;
   private boolean loaded;
   final Level level;
   @Nullable
   private Supplier<FullChunkStatus> fullStatus;
   @Nullable
   private PostLoadProcessor postLoad;
   private final Int2ObjectMap<GameEventListenerRegistry> gameEventListenerRegistrySections;
   private final LevelChunkTicks<Block> blockTicks;
   private final LevelChunkTicks<Fluid> fluidTicks;

   public LevelChunk(Level var1, ChunkPos var2) {
      this(var1, var2, UpgradeData.EMPTY, new LevelChunkTicks(), new LevelChunkTicks(), 0L, (LevelChunkSection[])null, (PostLoadProcessor)null, (BlendingData)null);
   }

   public LevelChunk(Level var1, ChunkPos var2, UpgradeData var3, LevelChunkTicks<Block> var4, LevelChunkTicks<Fluid> var5, long var6, @Nullable LevelChunkSection[] var8, @Nullable PostLoadProcessor var9, @Nullable BlendingData var10) {
      super(var2, var3, var1, var1.registryAccess().registryOrThrow(Registries.BIOME), var6, var8, var10);
      this.tickersInLevel = Maps.newHashMap();
      this.level = var1;
      this.gameEventListenerRegistrySections = new Int2ObjectOpenHashMap();
      Heightmap.Types[] var11 = Heightmap.Types.values();
      int var12 = var11.length;

      for(int var13 = 0; var13 < var12; ++var13) {
         Heightmap.Types var14 = var11[var13];
         if (ChunkStatus.FULL.heightmapsAfter().contains(var14)) {
            this.heightmaps.put(var14, new Heightmap(this, var14));
         }
      }

      this.postLoad = var9;
      this.blockTicks = var4;
      this.fluidTicks = var5;
   }

   public LevelChunk(ServerLevel var1, ProtoChunk var2, @Nullable PostLoadProcessor var3) {
      this(var1, var2.getPos(), var2.getUpgradeData(), var2.unpackBlockTicks(), var2.unpackFluidTicks(), var2.getInhabitedTime(), var2.getSections(), var3, var2.getBlendingData());
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
         Map.Entry var7 = (Map.Entry)var4.next();
         if (ChunkStatus.FULL.heightmapsAfter().contains(var7.getKey())) {
            this.setHeightmap((Heightmap.Types)var7.getKey(), ((Heightmap)var7.getValue()).getRawData());
         }
      }

      this.skyLightSources = var2.skyLightSources;
      this.setLightCorrect(var2.isLightCorrect());
      this.unsaved = true;
   }

   public TickContainerAccess<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public TickContainerAccess<Fluid> getFluidTicks() {
      return this.fluidTicks;
   }

   public ChunkAccess.TicksToSave getTicksForSerialization() {
      return new ChunkAccess.TicksToSave(this.blockTicks, this.fluidTicks);
   }

   public GameEventListenerRegistry getListenerRegistry(int var1) {
      Level var3 = this.level;
      if (var3 instanceof ServerLevel var2) {
         return (GameEventListenerRegistry)this.gameEventListenerRegistrySections.computeIfAbsent(var1, (var3x) -> {
            return new EuclideanGameEventListenerRegistry(var2, var1, this::removeGameEventListenerRegistry);
         });
      } else {
         return super.getListenerRegistry(var1);
      }
   }

   public BlockState getBlockState(BlockPos var1) {
      int var2 = var1.getX();
      int var3 = var1.getY();
      int var4 = var1.getZ();
      if (this.level.isDebug()) {
         BlockState var10 = null;
         if (var3 == 60) {
            var10 = Blocks.BARRIER.defaultBlockState();
         }

         if (var3 == 70) {
            var10 = DebugLevelSource.getBlockStateFor(var2, var4);
         }

         return var10 == null ? Blocks.AIR.defaultBlockState() : var10;
      } else {
         CrashReport var6;
         CrashReportCategory var7;
         try {
            int var5 = this.getSectionIndex(var3);
            if (var5 >= 0 && var5 < this.sections.length) {
               LevelChunkSection var11 = this.sections[var5];
               if (!var11.hasOnlyAir()) {
                  return var11.getBlockState(var2 & 15, var3 & 15, var4 & 15);
               }
            }
         } catch (Throwable var9) {
            var6 = CrashReport.forThrowable(var9, "Getting block state");
            var7 = var6.addCategory("Block being got");
            var7.setDetail("Location", () -> {
               return CrashReportCategory.formatLocation(this, var2, var3, var4);
            });
            throw new ReportedException(var6);
         }

         try {
            return Blocks.AIR.defaultBlockState();
         } catch (Throwable var8) {
            var6 = CrashReport.forThrowable(var8, "Getting block state");
            var7 = var6.addCategory("Block being got");
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
      CrashReport var5;
      CrashReportCategory var6;
      try {
         int var4 = this.getSectionIndex(var2);
         if (var4 >= 0 && var4 < this.sections.length) {
            LevelChunkSection var9 = this.sections[var4];
            if (!var9.hasOnlyAir()) {
               return var9.getFluidState(var1 & 15, var2 & 15, var3 & 15);
            }
         }
      } catch (Throwable var8) {
         var5 = CrashReport.forThrowable(var8, "Getting fluid state");
         var6 = var5.addCategory("Block being got");
         var6.setDetail("Location", () -> {
            return CrashReportCategory.formatLocation(this, var1, var2, var3);
         });
         throw new ReportedException(var5);
      }

      try {
         return Fluids.EMPTY.defaultFluidState();
      } catch (Throwable var7) {
         var5 = CrashReport.forThrowable(var7, "Getting fluid state");
         var6 = var5.addCategory("Block being got");
         var6.setDetail("Location", () -> {
            return CrashReportCategory.formatLocation(this, var1, var2, var3);
         });
         throw new ReportedException(var5);
      }
   }

   @Nullable
   public BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3) {
      int var4 = var1.getY();
      LevelChunkSection var5 = this.getSection(this.getSectionIndex(var4));
      boolean var6 = var5.hasOnlyAir();
      if (var6 && var2.isAir()) {
         return null;
      } else {
         int var7 = var1.getX() & 15;
         int var8 = var4 & 15;
         int var9 = var1.getZ() & 15;
         BlockState var10 = var5.setBlockState(var7, var8, var9, var2);
         if (var10 == var2) {
            return null;
         } else {
            Block var11 = var2.getBlock();
            ((Heightmap)this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING)).update(var7, var4, var9, var2);
            ((Heightmap)this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES)).update(var7, var4, var9, var2);
            ((Heightmap)this.heightmaps.get(Heightmap.Types.OCEAN_FLOOR)).update(var7, var4, var9, var2);
            ((Heightmap)this.heightmaps.get(Heightmap.Types.WORLD_SURFACE)).update(var7, var4, var9, var2);
            boolean var12 = var5.hasOnlyAir();
            if (var6 != var12) {
               this.level.getChunkSource().getLightEngine().updateSectionStatus(var1, var12);
            }

            if (LightEngine.hasDifferentLightProperties(this, var1, var10, var2)) {
               ProfilerFiller var13 = this.level.getProfiler();
               var13.push("updateSkyLightSources");
               this.skyLightSources.update(this, var7, var4, var9);
               var13.popPush("queueCheckLight");
               this.level.getChunkSource().getLightEngine().checkBlock(var1);
               var13.pop();
            }

            boolean var15 = var10.hasBlockEntity();
            if (!this.level.isClientSide) {
               var10.onRemove(this.level, var1, var2, var3);
            } else if (!var10.is(var11) && var15) {
               this.removeBlockEntity(var1);
            }

            if (!var5.getBlockState(var7, var8, var9).is(var11)) {
               return null;
            } else {
               if (!this.level.isClientSide) {
                  var2.onPlace(this.level, var1, var10, var3);
               }

               if (var2.hasBlockEntity()) {
                  BlockEntity var14 = this.getBlockEntity(var1, LevelChunk.EntityCreationType.CHECK);
                  if (var14 != null && !var14.isValidBlockState(var2)) {
                     this.removeBlockEntity(var1);
                     var14 = null;
                  }

                  if (var14 == null) {
                     var14 = ((EntityBlock)var11).newBlockEntity(var1, var2);
                     if (var14 != null) {
                        this.addAndRegisterBlockEntity(var14);
                     }
                  } else {
                     var14.setBlockState(var2);
                     this.updateBlockEntityTicker(var14);
                  }
               }

               this.unsaved = true;
               return var10;
            }
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public void addEntity(Entity var1) {
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
   public BlockEntity getBlockEntity(BlockPos var1, EntityCreationType var2) {
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
         Level var3 = this.level;
         if (var3 instanceof ServerLevel) {
            ServerLevel var2 = (ServerLevel)var3;
            this.addGameEventListener(var1, var2);
         }

         this.updateBlockEntityTicker(var1);
      }

   }

   private boolean isInLevel() {
      return this.loaded || this.level.isClientSide();
   }

   boolean isTicking(BlockPos var1) {
      if (!this.level.getWorldBorder().isWithinBounds(var1)) {
         return false;
      } else {
         Level var3 = this.level;
         if (!(var3 instanceof ServerLevel)) {
            return true;
         } else {
            ServerLevel var2 = (ServerLevel)var3;
            return this.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING) && var2.areEntitiesLoaded(ChunkPos.asLong(var1));
         }
      }
   }

   public void setBlockEntity(BlockEntity var1) {
      BlockPos var2 = var1.getBlockPos();
      BlockState var3 = this.getBlockState(var2);
      if (!var3.hasBlockEntity()) {
         LOGGER.warn("Trying to set block entity {} at position {}, but state {} does not allow it", new Object[]{var1, var2, var3});
      } else {
         BlockState var4 = var1.getBlockState();
         if (var3 != var4) {
            if (!var1.getType().isValid(var3)) {
               LOGGER.warn("Trying to set block entity {} at position {}, but state {} does not allow it", new Object[]{var1, var2, var3});
               return;
            }

            if (var3.getBlock() != var4.getBlock()) {
               LOGGER.warn("Block state mismatch on block entity {} in position {}, {} != {}, updating", new Object[]{var1, var2, var3, var4});
            }

            var1.setBlockState(var3);
         }

         var1.setLevel(this.level);
         var1.clearRemoved();
         BlockEntity var5 = (BlockEntity)this.blockEntities.put(var2.immutable(), var1);
         if (var5 != null && var5 != var1) {
            var5.setRemoved();
         }

      }
   }

   @Nullable
   public CompoundTag getBlockEntityNbtForSaving(BlockPos var1, HolderLookup.Provider var2) {
      BlockEntity var3 = this.getBlockEntity(var1);
      CompoundTag var4;
      if (var3 != null && !var3.isRemoved()) {
         var4 = var3.saveWithFullMetadata(this.level.registryAccess());
         var4.putBoolean("keepPacked", false);
         return var4;
      } else {
         var4 = (CompoundTag)this.pendingBlockEntities.get(var1);
         if (var4 != null) {
            var4 = var4.copy();
            var4.putBoolean("keepPacked", true);
         }

         return var4;
      }
   }

   public void removeBlockEntity(BlockPos var1) {
      if (this.isInLevel()) {
         BlockEntity var2 = (BlockEntity)this.blockEntities.remove(var1);
         if (var2 != null) {
            Level var4 = this.level;
            if (var4 instanceof ServerLevel) {
               ServerLevel var3 = (ServerLevel)var4;
               this.removeGameEventListener(var2, var3);
            }

            var2.setRemoved();
         }
      }

      this.removeBlockEntityTicker(var1);
   }

   private <T extends BlockEntity> void removeGameEventListener(T var1, ServerLevel var2) {
      Block var3 = var1.getBlockState().getBlock();
      if (var3 instanceof EntityBlock) {
         GameEventListener var4 = ((EntityBlock)var3).getListener(var2, var1);
         if (var4 != null) {
            int var5 = SectionPos.blockToSectionCoord(var1.getBlockPos().getY());
            GameEventListenerRegistry var6 = this.getListenerRegistry(var5);
            var6.unregister(var4);
         }
      }

   }

   private void removeGameEventListenerRegistry(int var1) {
      this.gameEventListenerRegistrySections.remove(var1);
   }

   private void removeBlockEntityTicker(BlockPos var1) {
      RebindableTickingBlockEntityWrapper var2 = (RebindableTickingBlockEntityWrapper)this.tickersInLevel.remove(var1);
      if (var2 != null) {
         var2.rebind(NULL_TICKER);
      }

   }

   public void runPostLoad() {
      if (this.postLoad != null) {
         this.postLoad.run(this);
         this.postLoad = null;
      }

   }

   public boolean isEmpty() {
      return false;
   }

   public void replaceWithPacketData(FriendlyByteBuf var1, CompoundTag var2, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> var3) {
      this.clearAllBlockEntities();
      LevelChunkSection[] var4 = this.sections;
      int var5 = var4.length;

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         LevelChunkSection var7 = var4[var6];
         var7.read(var1);
      }

      Heightmap.Types[] var9 = Heightmap.Types.values();
      var5 = var9.length;

      for(var6 = 0; var6 < var5; ++var6) {
         Heightmap.Types var10 = var9[var6];
         String var8 = var10.getSerializationKey();
         if (var2.contains(var8, 12)) {
            this.setHeightmap(var10, var2.getLongArray(var8));
         }
      }

      this.initializeLightSources();
      var3.accept((var1x, var2x, var3x) -> {
         BlockEntity var4 = this.getBlockEntity(var1x, LevelChunk.EntityCreationType.IMMEDIATE);
         if (var4 != null && var3x != null && var4.getType() == var2x) {
            var4.loadWithComponents(var3x, this.level.registryAccess());
         }

      });
   }

   public void replaceBiomes(FriendlyByteBuf var1) {
      LevelChunkSection[] var2 = this.sections;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         LevelChunkSection var5 = var2[var4];
         var5.readBiomes(var1);
      }

   }

   public void setLoaded(boolean var1) {
      this.loaded = var1;
   }

   public Level getLevel() {
      return this.level;
   }

   public Map<BlockPos, BlockEntity> getBlockEntities() {
      return this.blockEntities;
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
               FluidState var7 = var6.getFluidState();
               if (!var7.isEmpty()) {
                  var7.tick(this.level, var5);
               }

               if (!(var6.getBlock() instanceof LiquidBlock)) {
                  BlockState var8 = Block.updateFromNeighbourShapes(var6, this.level, var5);
                  this.level.setBlock(var5, var8, 20);
               }
            }

            this.postProcessing[var2].clear();
         }
      }

      UnmodifiableIterator var9 = ImmutableList.copyOf(this.pendingBlockEntities.keySet()).iterator();

      while(var9.hasNext()) {
         BlockPos var10 = (BlockPos)var9.next();
         this.getBlockEntity(var10);
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
         var3 = BlockEntity.loadStatic(var1, var4, var2, this.level.registryAccess());
      }

      if (var3 != null) {
         var3.setLevel(this.level);
         this.addAndRegisterBlockEntity(var3);
      } else {
         LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", var4, var1);
      }

      return var3;
   }

   public void unpackTicks(long var1) {
      this.blockTicks.unpack(var1);
      this.fluidTicks.unpack(var1);
   }

   public void registerTickContainerInLevel(ServerLevel var1) {
      var1.getBlockTicks().addContainer(this.chunkPos, this.blockTicks);
      var1.getFluidTicks().addContainer(this.chunkPos, this.fluidTicks);
   }

   public void unregisterTickContainerFromLevel(ServerLevel var1) {
      var1.getBlockTicks().removeContainer(this.chunkPos);
      var1.getFluidTicks().removeContainer(this.chunkPos);
   }

   public ChunkStatus getPersistedStatus() {
      return ChunkStatus.FULL;
   }

   public FullChunkStatus getFullStatus() {
      return this.fullStatus == null ? FullChunkStatus.FULL : (FullChunkStatus)this.fullStatus.get();
   }

   public void setFullStatus(Supplier<FullChunkStatus> var1) {
      this.fullStatus = var1;
   }

   public void clearAllBlockEntities() {
      this.blockEntities.values().forEach(BlockEntity::setRemoved);
      this.blockEntities.clear();
      this.tickersInLevel.values().forEach((var0) -> {
         var0.rebind(NULL_TICKER);
      });
      this.tickersInLevel.clear();
   }

   public void registerAllBlockEntitiesAfterLevelLoad() {
      this.blockEntities.values().forEach((var1) -> {
         Level var3 = this.level;
         if (var3 instanceof ServerLevel var2) {
            this.addGameEventListener(var1, var2);
         }

         this.updateBlockEntityTicker(var1);
      });
   }

   private <T extends BlockEntity> void addGameEventListener(T var1, ServerLevel var2) {
      Block var3 = var1.getBlockState().getBlock();
      if (var3 instanceof EntityBlock) {
         GameEventListener var4 = ((EntityBlock)var3).getListener(var2, var1);
         if (var4 != null) {
            this.getListenerRegistry(SectionPos.blockToSectionCoord(var1.getBlockPos().getY())).register(var4);
         }
      }

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
               RebindableTickingBlockEntityWrapper var6 = new RebindableTickingBlockEntityWrapper(this, var5);
               this.level.addBlockEntityTicker(var6);
               return var6;
            } else {
               return null;
            }
         });
      }

   }

   private <T extends BlockEntity> TickingBlockEntity createTicker(T var1, BlockEntityTicker<T> var2) {
      return new BoundTickingBlockEntity(var1, var2);
   }

   @FunctionalInterface
   public interface PostLoadProcessor {
      void run(LevelChunk var1);
   }

   public static enum EntityCreationType {
      IMMEDIATE,
      QUEUED,
      CHECK;

      private EntityCreationType() {
      }

      // $FF: synthetic method
      private static EntityCreationType[] $values() {
         return new EntityCreationType[]{IMMEDIATE, QUEUED, CHECK};
      }
   }

   class RebindableTickingBlockEntityWrapper implements TickingBlockEntity {
      private TickingBlockEntity ticker;

      RebindableTickingBlockEntityWrapper(final LevelChunk var1, final TickingBlockEntity var2) {
         super();
         this.ticker = var2;
      }

      void rebind(TickingBlockEntity var1) {
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
         return String.valueOf(this.ticker) + " <wrapped>";
      }
   }

   class BoundTickingBlockEntity<T extends BlockEntity> implements TickingBlockEntity {
      private final T blockEntity;
      private final BlockEntityTicker<T> ticker;
      private boolean loggedInvalidBlockState;

      BoundTickingBlockEntity(final T var2, final BlockEntityTicker<T> var3) {
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
                     LevelChunk.LOGGER.warn("Block entity {} @ {} state {} invalid for ticking:", new Object[]{LogUtils.defer(this::getType), LogUtils.defer(this::getPos), var6});
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
         String var10000 = this.getType();
         return "Level ticker for " + var10000 + "@" + String.valueOf(this.getPos());
      }
   }
}
