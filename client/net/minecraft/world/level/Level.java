package net.minecraft.world.level;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagContainer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Scoreboard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Level implements LevelAccessor, AutoCloseable {
   protected static final Logger LOGGER = LogManager.getLogger();
   public static final Codec<ResourceKey<Level>> RESOURCE_KEY_CODEC;
   public static final ResourceKey<Level> OVERWORLD;
   public static final ResourceKey<Level> NETHER;
   public static final ResourceKey<Level> END;
   private static final Direction[] DIRECTIONS;
   public final List<BlockEntity> blockEntityList = Lists.newArrayList();
   public final List<BlockEntity> tickableBlockEntities = Lists.newArrayList();
   protected final List<BlockEntity> pendingBlockEntities = Lists.newArrayList();
   protected final List<BlockEntity> blockEntitiesToUnload = Lists.newArrayList();
   private final Thread thread;
   private final boolean isDebug;
   private int skyDarken;
   protected int randValue = (new Random()).nextInt();
   protected final int addend = 1013904223;
   protected float oRainLevel;
   protected float rainLevel;
   protected float oThunderLevel;
   protected float thunderLevel;
   public final Random random = new Random();
   private final DimensionType dimensionType;
   protected final WritableLevelData levelData;
   private final Supplier<ProfilerFiller> profiler;
   public final boolean isClientSide;
   protected boolean updatingBlockEntities;
   private final WorldBorder worldBorder;
   private final BiomeManager biomeManager;
   private final ResourceKey<Level> dimension;

   protected Level(WritableLevelData var1, ResourceKey<Level> var2, final DimensionType var3, Supplier<ProfilerFiller> var4, boolean var5, boolean var6, long var7) {
      super();
      this.profiler = var4;
      this.levelData = var1;
      this.dimensionType = var3;
      this.dimension = var2;
      this.isClientSide = var5;
      if (var3.coordinateScale() != 1.0D) {
         this.worldBorder = new WorldBorder() {
            public double getCenterX() {
               return super.getCenterX() / var3.coordinateScale();
            }

            public double getCenterZ() {
               return super.getCenterZ() / var3.coordinateScale();
            }
         };
      } else {
         this.worldBorder = new WorldBorder();
      }

      this.thread = Thread.currentThread();
      this.biomeManager = new BiomeManager(this, var7, var3.getBiomeZoomer());
      this.isDebug = var6;
   }

   public boolean isClientSide() {
      return this.isClientSide;
   }

   @Nullable
   public MinecraftServer getServer() {
      return null;
   }

   public static boolean isInWorldBounds(BlockPos var0) {
      return !isOutsideBuildHeight(var0) && isInWorldBoundsHorizontal(var0);
   }

   public static boolean isInSpawnableBounds(BlockPos var0) {
      return !isOutsideSpawnableHeight(var0.getY()) && isInWorldBoundsHorizontal(var0);
   }

   private static boolean isInWorldBoundsHorizontal(BlockPos var0) {
      return var0.getX() >= -30000000 && var0.getZ() >= -30000000 && var0.getX() < 30000000 && var0.getZ() < 30000000;
   }

   private static boolean isOutsideSpawnableHeight(int var0) {
      return var0 < -20000000 || var0 >= 20000000;
   }

   public static boolean isOutsideBuildHeight(BlockPos var0) {
      return isOutsideBuildHeight(var0.getY());
   }

   public static boolean isOutsideBuildHeight(int var0) {
      return var0 < 0 || var0 >= 256;
   }

   public LevelChunk getChunkAt(BlockPos var1) {
      return this.getChunk(var1.getX() >> 4, var1.getZ() >> 4);
   }

   public LevelChunk getChunk(int var1, int var2) {
      return (LevelChunk)this.getChunk(var1, var2, ChunkStatus.FULL);
   }

   public ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4) {
      ChunkAccess var5 = this.getChunkSource().getChunk(var1, var2, var3, var4);
      if (var5 == null && var4) {
         throw new IllegalStateException("Should always be able to create a chunk!");
      } else {
         return var5;
      }
   }

   public boolean setBlock(BlockPos var1, BlockState var2, int var3) {
      return this.setBlock(var1, var2, var3, 512);
   }

   public boolean setBlock(BlockPos var1, BlockState var2, int var3, int var4) {
      if (isOutsideBuildHeight(var1)) {
         return false;
      } else if (!this.isClientSide && this.isDebug()) {
         return false;
      } else {
         LevelChunk var5 = this.getChunkAt(var1);
         Block var6 = var2.getBlock();
         BlockState var7 = var5.setBlockState(var1, var2, (var3 & 64) != 0);
         if (var7 == null) {
            return false;
         } else {
            BlockState var8 = this.getBlockState(var1);
            if ((var3 & 128) == 0 && var8 != var7 && (var8.getLightBlock(this, var1) != var7.getLightBlock(this, var1) || var8.getLightEmission() != var7.getLightEmission() || var8.useShapeForLightOcclusion() || var7.useShapeForLightOcclusion())) {
               this.getProfiler().push("queueCheckLight");
               this.getChunkSource().getLightEngine().checkBlock(var1);
               this.getProfiler().pop();
            }

            if (var8 == var2) {
               if (var7 != var8) {
                  this.setBlocksDirty(var1, var7, var8);
               }

               if ((var3 & 2) != 0 && (!this.isClientSide || (var3 & 4) == 0) && (this.isClientSide || var5.getFullStatus() != null && var5.getFullStatus().isOrAfter(ChunkHolder.FullChunkStatus.TICKING))) {
                  this.sendBlockUpdated(var1, var7, var2, var3);
               }

               if ((var3 & 1) != 0) {
                  this.blockUpdated(var1, var7.getBlock());
                  if (!this.isClientSide && var2.hasAnalogOutputSignal()) {
                     this.updateNeighbourForOutputSignal(var1, var6);
                  }
               }

               if ((var3 & 16) == 0 && var4 > 0) {
                  int var9 = var3 & -34;
                  var7.updateIndirectNeighbourShapes(this, var1, var9, var4 - 1);
                  var2.updateNeighbourShapes(this, var1, var9, var4 - 1);
                  var2.updateIndirectNeighbourShapes(this, var1, var9, var4 - 1);
               }

               this.onBlockStateChange(var1, var7, var8);
            }

            return true;
         }
      }
   }

   public void onBlockStateChange(BlockPos var1, BlockState var2, BlockState var3) {
   }

   public boolean removeBlock(BlockPos var1, boolean var2) {
      FluidState var3 = this.getFluidState(var1);
      return this.setBlock(var1, var3.createLegacyBlock(), 3 | (var2 ? 64 : 0));
   }

   public boolean destroyBlock(BlockPos var1, boolean var2, @Nullable Entity var3, int var4) {
      BlockState var5 = this.getBlockState(var1);
      if (var5.isAir()) {
         return false;
      } else {
         FluidState var6 = this.getFluidState(var1);
         if (!(var5.getBlock() instanceof BaseFireBlock)) {
            this.levelEvent(2001, var1, Block.getId(var5));
         }

         if (var2) {
            BlockEntity var7 = var5.getBlock().isEntityBlock() ? this.getBlockEntity(var1) : null;
            Block.dropResources(var5, this, var1, var7, var3, ItemStack.EMPTY);
         }

         return this.setBlock(var1, var6.createLegacyBlock(), 3, var4);
      }
   }

   public boolean setBlockAndUpdate(BlockPos var1, BlockState var2) {
      return this.setBlock(var1, var2, 3);
   }

   public abstract void sendBlockUpdated(BlockPos var1, BlockState var2, BlockState var3, int var4);

   public void setBlocksDirty(BlockPos var1, BlockState var2, BlockState var3) {
   }

   public void updateNeighborsAt(BlockPos var1, Block var2) {
      this.neighborChanged(var1.west(), var2, var1);
      this.neighborChanged(var1.east(), var2, var1);
      this.neighborChanged(var1.below(), var2, var1);
      this.neighborChanged(var1.above(), var2, var1);
      this.neighborChanged(var1.north(), var2, var1);
      this.neighborChanged(var1.south(), var2, var1);
   }

   public void updateNeighborsAtExceptFromFacing(BlockPos var1, Block var2, Direction var3) {
      if (var3 != Direction.WEST) {
         this.neighborChanged(var1.west(), var2, var1);
      }

      if (var3 != Direction.EAST) {
         this.neighborChanged(var1.east(), var2, var1);
      }

      if (var3 != Direction.DOWN) {
         this.neighborChanged(var1.below(), var2, var1);
      }

      if (var3 != Direction.UP) {
         this.neighborChanged(var1.above(), var2, var1);
      }

      if (var3 != Direction.NORTH) {
         this.neighborChanged(var1.north(), var2, var1);
      }

      if (var3 != Direction.SOUTH) {
         this.neighborChanged(var1.south(), var2, var1);
      }

   }

   public void neighborChanged(BlockPos var1, Block var2, BlockPos var3) {
      if (!this.isClientSide) {
         BlockState var4 = this.getBlockState(var1);

         try {
            var4.neighborChanged(this, var1, var2, var3, false);
         } catch (Throwable var8) {
            CrashReport var6 = CrashReport.forThrowable(var8, "Exception while updating neighbours");
            CrashReportCategory var7 = var6.addCategory("Block being updated");
            var7.setDetail("Source block type", () -> {
               try {
                  return String.format("ID #%s (%s // %s)", Registry.BLOCK.getKey(var2), var2.getDescriptionId(), var2.getClass().getCanonicalName());
               } catch (Throwable var2x) {
                  return "ID #" + Registry.BLOCK.getKey(var2);
               }
            });
            CrashReportCategory.populateBlockDetails(var7, var1, var4);
            throw new ReportedException(var6);
         }
      }
   }

   public int getHeight(Heightmap.Types var1, int var2, int var3) {
      int var4;
      if (var2 >= -30000000 && var3 >= -30000000 && var2 < 30000000 && var3 < 30000000) {
         if (this.hasChunk(var2 >> 4, var3 >> 4)) {
            var4 = this.getChunk(var2 >> 4, var3 >> 4).getHeight(var1, var2 & 15, var3 & 15) + 1;
         } else {
            var4 = 0;
         }
      } else {
         var4 = this.getSeaLevel() + 1;
      }

      return var4;
   }

   public LevelLightEngine getLightEngine() {
      return this.getChunkSource().getLightEngine();
   }

   public BlockState getBlockState(BlockPos var1) {
      if (isOutsideBuildHeight(var1)) {
         return Blocks.VOID_AIR.defaultBlockState();
      } else {
         LevelChunk var2 = this.getChunk(var1.getX() >> 4, var1.getZ() >> 4);
         return var2.getBlockState(var1);
      }
   }

   public FluidState getFluidState(BlockPos var1) {
      if (isOutsideBuildHeight(var1)) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         LevelChunk var2 = this.getChunkAt(var1);
         return var2.getFluidState(var1);
      }
   }

   public boolean isDay() {
      return !this.dimensionType().hasFixedTime() && this.skyDarken < 4;
   }

   public boolean isNight() {
      return !this.dimensionType().hasFixedTime() && !this.isDay();
   }

   public void playSound(@Nullable Player var1, BlockPos var2, SoundEvent var3, SoundSource var4, float var5, float var6) {
      this.playSound(var1, (double)var2.getX() + 0.5D, (double)var2.getY() + 0.5D, (double)var2.getZ() + 0.5D, var3, var4, var5, var6);
   }

   public abstract void playSound(@Nullable Player var1, double var2, double var4, double var6, SoundEvent var8, SoundSource var9, float var10, float var11);

   public abstract void playSound(@Nullable Player var1, Entity var2, SoundEvent var3, SoundSource var4, float var5, float var6);

   public void playLocalSound(double var1, double var3, double var5, SoundEvent var7, SoundSource var8, float var9, float var10, boolean var11) {
   }

   public void addParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12) {
   }

   public void addParticle(ParticleOptions var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
   }

   public void addAlwaysVisibleParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12) {
   }

   public void addAlwaysVisibleParticle(ParticleOptions var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
   }

   public float getSunAngle(float var1) {
      float var2 = this.getTimeOfDay(var1);
      return var2 * 6.2831855F;
   }

   public boolean addBlockEntity(BlockEntity var1) {
      if (this.updatingBlockEntities) {
         LOGGER.error("Adding block entity while ticking: {} @ {}", new org.apache.logging.log4j.util.Supplier[]{() -> {
            return Registry.BLOCK_ENTITY_TYPE.getKey(var1.getType());
         }, var1::getBlockPos});
      }

      boolean var2 = this.blockEntityList.add(var1);
      if (var2 && var1 instanceof TickableBlockEntity) {
         this.tickableBlockEntities.add(var1);
      }

      if (this.isClientSide) {
         BlockPos var3 = var1.getBlockPos();
         BlockState var4 = this.getBlockState(var3);
         this.sendBlockUpdated(var3, var4, var4, 2);
      }

      return var2;
   }

   public void addAllPendingBlockEntities(Collection<BlockEntity> var1) {
      if (this.updatingBlockEntities) {
         this.pendingBlockEntities.addAll(var1);
      } else {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            BlockEntity var3 = (BlockEntity)var2.next();
            this.addBlockEntity(var3);
         }
      }

   }

   public void tickBlockEntities() {
      ProfilerFiller var1 = this.getProfiler();
      var1.push("blockEntities");
      if (!this.blockEntitiesToUnload.isEmpty()) {
         this.tickableBlockEntities.removeAll(this.blockEntitiesToUnload);
         this.blockEntityList.removeAll(this.blockEntitiesToUnload);
         this.blockEntitiesToUnload.clear();
      }

      this.updatingBlockEntities = true;
      Iterator var2 = this.tickableBlockEntities.iterator();

      while(var2.hasNext()) {
         BlockEntity var3 = (BlockEntity)var2.next();
         if (!var3.isRemoved() && var3.hasLevel()) {
            BlockPos var4 = var3.getBlockPos();
            if (this.getChunkSource().isTickingChunk(var4) && this.getWorldBorder().isWithinBounds(var4)) {
               try {
                  var1.push(() -> {
                     return String.valueOf(BlockEntityType.getKey(var3.getType()));
                  });
                  if (var3.getType().isValid(this.getBlockState(var4).getBlock())) {
                     ((TickableBlockEntity)var3).tick();
                  } else {
                     var3.logInvalidState();
                  }

                  var1.pop();
               } catch (Throwable var8) {
                  CrashReport var6 = CrashReport.forThrowable(var8, "Ticking block entity");
                  CrashReportCategory var7 = var6.addCategory("Block entity being ticked");
                  var3.fillCrashReportCategory(var7);
                  throw new ReportedException(var6);
               }
            }
         }

         if (var3.isRemoved()) {
            var2.remove();
            this.blockEntityList.remove(var3);
            if (this.hasChunkAt(var3.getBlockPos())) {
               this.getChunkAt(var3.getBlockPos()).removeBlockEntity(var3.getBlockPos());
            }
         }
      }

      this.updatingBlockEntities = false;
      var1.popPush("pendingBlockEntities");
      if (!this.pendingBlockEntities.isEmpty()) {
         for(int var9 = 0; var9 < this.pendingBlockEntities.size(); ++var9) {
            BlockEntity var10 = (BlockEntity)this.pendingBlockEntities.get(var9);
            if (!var10.isRemoved()) {
               if (!this.blockEntityList.contains(var10)) {
                  this.addBlockEntity(var10);
               }

               if (this.hasChunkAt(var10.getBlockPos())) {
                  LevelChunk var5 = this.getChunkAt(var10.getBlockPos());
                  BlockState var11 = var5.getBlockState(var10.getBlockPos());
                  var5.setBlockEntity(var10.getBlockPos(), var10);
                  this.sendBlockUpdated(var10.getBlockPos(), var11, var11, 3);
               }
            }
         }

         this.pendingBlockEntities.clear();
      }

      var1.pop();
   }

   public void guardEntityTick(Consumer<Entity> var1, Entity var2) {
      try {
         var1.accept(var2);
      } catch (Throwable var6) {
         CrashReport var4 = CrashReport.forThrowable(var6, "Ticking entity");
         CrashReportCategory var5 = var4.addCategory("Entity being ticked");
         var2.fillCrashReportCategory(var5);
         throw new ReportedException(var4);
      }
   }

   public Explosion explode(@Nullable Entity var1, double var2, double var4, double var6, float var8, Explosion.BlockInteraction var9) {
      return this.explode(var1, (DamageSource)null, (ExplosionDamageCalculator)null, var2, var4, var6, var8, false, var9);
   }

   public Explosion explode(@Nullable Entity var1, double var2, double var4, double var6, float var8, boolean var9, Explosion.BlockInteraction var10) {
      return this.explode(var1, (DamageSource)null, (ExplosionDamageCalculator)null, var2, var4, var6, var8, var9, var10);
   }

   public Explosion explode(@Nullable Entity var1, @Nullable DamageSource var2, @Nullable ExplosionDamageCalculator var3, double var4, double var6, double var8, float var10, boolean var11, Explosion.BlockInteraction var12) {
      Explosion var13 = new Explosion(this, var1, var2, var3, var4, var6, var8, var10, var11, var12);
      var13.explode();
      var13.finalizeExplosion(true);
      return var13;
   }

   public String gatherChunkSourceStats() {
      return this.getChunkSource().gatherStats();
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1) {
      if (isOutsideBuildHeight(var1)) {
         return null;
      } else if (!this.isClientSide && Thread.currentThread() != this.thread) {
         return null;
      } else {
         BlockEntity var2 = null;
         if (this.updatingBlockEntities) {
            var2 = this.getPendingBlockEntityAt(var1);
         }

         if (var2 == null) {
            var2 = this.getChunkAt(var1).getBlockEntity(var1, LevelChunk.EntityCreationType.IMMEDIATE);
         }

         if (var2 == null) {
            var2 = this.getPendingBlockEntityAt(var1);
         }

         return var2;
      }
   }

   @Nullable
   private BlockEntity getPendingBlockEntityAt(BlockPos var1) {
      for(int var2 = 0; var2 < this.pendingBlockEntities.size(); ++var2) {
         BlockEntity var3 = (BlockEntity)this.pendingBlockEntities.get(var2);
         if (!var3.isRemoved() && var3.getBlockPos().equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public void setBlockEntity(BlockPos var1, @Nullable BlockEntity var2) {
      if (!isOutsideBuildHeight(var1)) {
         if (var2 != null && !var2.isRemoved()) {
            if (this.updatingBlockEntities) {
               var2.setLevelAndPosition(this, var1);
               Iterator var3 = this.pendingBlockEntities.iterator();

               while(var3.hasNext()) {
                  BlockEntity var4 = (BlockEntity)var3.next();
                  if (var4.getBlockPos().equals(var1)) {
                     var4.setRemoved();
                     var3.remove();
                  }
               }

               this.pendingBlockEntities.add(var2);
            } else {
               this.getChunkAt(var1).setBlockEntity(var1, var2);
               this.addBlockEntity(var2);
            }
         }

      }
   }

   public void removeBlockEntity(BlockPos var1) {
      BlockEntity var2 = this.getBlockEntity(var1);
      if (var2 != null && this.updatingBlockEntities) {
         var2.setRemoved();
         this.pendingBlockEntities.remove(var2);
      } else {
         if (var2 != null) {
            this.pendingBlockEntities.remove(var2);
            this.blockEntityList.remove(var2);
            this.tickableBlockEntities.remove(var2);
         }

         this.getChunkAt(var1).removeBlockEntity(var1);
      }

   }

   public boolean isLoaded(BlockPos var1) {
      return isOutsideBuildHeight(var1) ? false : this.getChunkSource().hasChunk(var1.getX() >> 4, var1.getZ() >> 4);
   }

   public boolean loadedAndEntityCanStandOnFace(BlockPos var1, Entity var2, Direction var3) {
      if (isOutsideBuildHeight(var1)) {
         return false;
      } else {
         ChunkAccess var4 = this.getChunk(var1.getX() >> 4, var1.getZ() >> 4, ChunkStatus.FULL, false);
         return var4 == null ? false : var4.getBlockState(var1).entityCanStandOnFace(this, var1, var2, var3);
      }
   }

   public boolean loadedAndEntityCanStandOn(BlockPos var1, Entity var2) {
      return this.loadedAndEntityCanStandOnFace(var1, var2, Direction.UP);
   }

   public void updateSkyBrightness() {
      double var1 = 1.0D - (double)(this.getRainLevel(1.0F) * 5.0F) / 16.0D;
      double var3 = 1.0D - (double)(this.getThunderLevel(1.0F) * 5.0F) / 16.0D;
      double var5 = 0.5D + 2.0D * Mth.clamp((double)Mth.cos(this.getTimeOfDay(1.0F) * 6.2831855F), -0.25D, 0.25D);
      this.skyDarken = (int)((1.0D - var5 * var1 * var3) * 11.0D);
   }

   public void setSpawnSettings(boolean var1, boolean var2) {
      this.getChunkSource().setSpawnSettings(var1, var2);
   }

   protected void prepareWeather() {
      if (this.levelData.isRaining()) {
         this.rainLevel = 1.0F;
         if (this.levelData.isThundering()) {
            this.thunderLevel = 1.0F;
         }
      }

   }

   public void close() throws IOException {
      this.getChunkSource().close();
   }

   @Nullable
   public BlockGetter getChunkForCollisions(int var1, int var2) {
      return this.getChunk(var1, var2, ChunkStatus.FULL, false);
   }

   public List<Entity> getEntities(@Nullable Entity var1, AABB var2, @Nullable Predicate<? super Entity> var3) {
      this.getProfiler().incrementCounter("getEntities");
      ArrayList var4 = Lists.newArrayList();
      int var5 = Mth.floor((var2.minX - 2.0D) / 16.0D);
      int var6 = Mth.floor((var2.maxX + 2.0D) / 16.0D);
      int var7 = Mth.floor((var2.minZ - 2.0D) / 16.0D);
      int var8 = Mth.floor((var2.maxZ + 2.0D) / 16.0D);
      ChunkSource var9 = this.getChunkSource();

      for(int var10 = var5; var10 <= var6; ++var10) {
         for(int var11 = var7; var11 <= var8; ++var11) {
            LevelChunk var12 = var9.getChunk(var10, var11, false);
            if (var12 != null) {
               var12.getEntities((Entity)var1, var2, var4, var3);
            }
         }
      }

      return var4;
   }

   public <T extends Entity> List<T> getEntities(@Nullable EntityType<T> var1, AABB var2, Predicate<? super T> var3) {
      this.getProfiler().incrementCounter("getEntities");
      int var4 = Mth.floor((var2.minX - 2.0D) / 16.0D);
      int var5 = Mth.ceil((var2.maxX + 2.0D) / 16.0D);
      int var6 = Mth.floor((var2.minZ - 2.0D) / 16.0D);
      int var7 = Mth.ceil((var2.maxZ + 2.0D) / 16.0D);
      ArrayList var8 = Lists.newArrayList();

      for(int var9 = var4; var9 < var5; ++var9) {
         for(int var10 = var6; var10 < var7; ++var10) {
            LevelChunk var11 = this.getChunkSource().getChunk(var9, var10, false);
            if (var11 != null) {
               var11.getEntities((EntityType)var1, var2, var8, var3);
            }
         }
      }

      return var8;
   }

   public <T extends Entity> List<T> getEntitiesOfClass(Class<? extends T> var1, AABB var2, @Nullable Predicate<? super T> var3) {
      this.getProfiler().incrementCounter("getEntities");
      int var4 = Mth.floor((var2.minX - 2.0D) / 16.0D);
      int var5 = Mth.ceil((var2.maxX + 2.0D) / 16.0D);
      int var6 = Mth.floor((var2.minZ - 2.0D) / 16.0D);
      int var7 = Mth.ceil((var2.maxZ + 2.0D) / 16.0D);
      ArrayList var8 = Lists.newArrayList();
      ChunkSource var9 = this.getChunkSource();

      for(int var10 = var4; var10 < var5; ++var10) {
         for(int var11 = var6; var11 < var7; ++var11) {
            LevelChunk var12 = var9.getChunk(var10, var11, false);
            if (var12 != null) {
               var12.getEntitiesOfClass(var1, var2, var8, var3);
            }
         }
      }

      return var8;
   }

   public <T extends Entity> List<T> getLoadedEntitiesOfClass(Class<? extends T> var1, AABB var2, @Nullable Predicate<? super T> var3) {
      this.getProfiler().incrementCounter("getLoadedEntities");
      int var4 = Mth.floor((var2.minX - 2.0D) / 16.0D);
      int var5 = Mth.ceil((var2.maxX + 2.0D) / 16.0D);
      int var6 = Mth.floor((var2.minZ - 2.0D) / 16.0D);
      int var7 = Mth.ceil((var2.maxZ + 2.0D) / 16.0D);
      ArrayList var8 = Lists.newArrayList();
      ChunkSource var9 = this.getChunkSource();

      for(int var10 = var4; var10 < var5; ++var10) {
         for(int var11 = var6; var11 < var7; ++var11) {
            LevelChunk var12 = var9.getChunkNow(var10, var11);
            if (var12 != null) {
               var12.getEntitiesOfClass(var1, var2, var8, var3);
            }
         }
      }

      return var8;
   }

   @Nullable
   public abstract Entity getEntity(int var1);

   public void blockEntityChanged(BlockPos var1, BlockEntity var2) {
      if (this.hasChunkAt(var1)) {
         this.getChunkAt(var1).markUnsaved();
      }

   }

   public int getSeaLevel() {
      return 63;
   }

   public int getDirectSignalTo(BlockPos var1) {
      byte var2 = 0;
      int var3 = Math.max(var2, this.getDirectSignal(var1.below(), Direction.DOWN));
      if (var3 >= 15) {
         return var3;
      } else {
         var3 = Math.max(var3, this.getDirectSignal(var1.above(), Direction.UP));
         if (var3 >= 15) {
            return var3;
         } else {
            var3 = Math.max(var3, this.getDirectSignal(var1.north(), Direction.NORTH));
            if (var3 >= 15) {
               return var3;
            } else {
               var3 = Math.max(var3, this.getDirectSignal(var1.south(), Direction.SOUTH));
               if (var3 >= 15) {
                  return var3;
               } else {
                  var3 = Math.max(var3, this.getDirectSignal(var1.west(), Direction.WEST));
                  if (var3 >= 15) {
                     return var3;
                  } else {
                     var3 = Math.max(var3, this.getDirectSignal(var1.east(), Direction.EAST));
                     return var3 >= 15 ? var3 : var3;
                  }
               }
            }
         }
      }
   }

   public boolean hasSignal(BlockPos var1, Direction var2) {
      return this.getSignal(var1, var2) > 0;
   }

   public int getSignal(BlockPos var1, Direction var2) {
      BlockState var3 = this.getBlockState(var1);
      int var4 = var3.getSignal(this, var1, var2);
      return var3.isRedstoneConductor(this, var1) ? Math.max(var4, this.getDirectSignalTo(var1)) : var4;
   }

   public boolean hasNeighborSignal(BlockPos var1) {
      if (this.getSignal(var1.below(), Direction.DOWN) > 0) {
         return true;
      } else if (this.getSignal(var1.above(), Direction.UP) > 0) {
         return true;
      } else if (this.getSignal(var1.north(), Direction.NORTH) > 0) {
         return true;
      } else if (this.getSignal(var1.south(), Direction.SOUTH) > 0) {
         return true;
      } else if (this.getSignal(var1.west(), Direction.WEST) > 0) {
         return true;
      } else {
         return this.getSignal(var1.east(), Direction.EAST) > 0;
      }
   }

   public int getBestNeighborSignal(BlockPos var1) {
      int var2 = 0;
      Direction[] var3 = DIRECTIONS;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Direction var6 = var3[var5];
         int var7 = this.getSignal(var1.relative(var6), var6);
         if (var7 >= 15) {
            return 15;
         }

         if (var7 > var2) {
            var2 = var7;
         }
      }

      return var2;
   }

   public void disconnect() {
   }

   public long getGameTime() {
      return this.levelData.getGameTime();
   }

   public long getDayTime() {
      return this.levelData.getDayTime();
   }

   public boolean mayInteract(Player var1, BlockPos var2) {
      return true;
   }

   public void broadcastEntityEvent(Entity var1, byte var2) {
   }

   public void blockEvent(BlockPos var1, Block var2, int var3, int var4) {
      this.getBlockState(var1).triggerEvent(this, var1, var3, var4);
   }

   public LevelData getLevelData() {
      return this.levelData;
   }

   public GameRules getGameRules() {
      return this.levelData.getGameRules();
   }

   public float getThunderLevel(float var1) {
      return Mth.lerp(var1, this.oThunderLevel, this.thunderLevel) * this.getRainLevel(var1);
   }

   public void setThunderLevel(float var1) {
      this.oThunderLevel = var1;
      this.thunderLevel = var1;
   }

   public float getRainLevel(float var1) {
      return Mth.lerp(var1, this.oRainLevel, this.rainLevel);
   }

   public void setRainLevel(float var1) {
      this.oRainLevel = var1;
      this.rainLevel = var1;
   }

   public boolean isThundering() {
      if (this.dimensionType().hasSkyLight() && !this.dimensionType().hasCeiling()) {
         return (double)this.getThunderLevel(1.0F) > 0.9D;
      } else {
         return false;
      }
   }

   public boolean isRaining() {
      return (double)this.getRainLevel(1.0F) > 0.2D;
   }

   public boolean isRainingAt(BlockPos var1) {
      if (!this.isRaining()) {
         return false;
      } else if (!this.canSeeSky(var1)) {
         return false;
      } else if (this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var1).getY() > var1.getY()) {
         return false;
      } else {
         Biome var2 = this.getBiome(var1);
         return var2.getPrecipitation() == Biome.Precipitation.RAIN && var2.getTemperature(var1) >= 0.15F;
      }
   }

   public boolean isHumidAt(BlockPos var1) {
      Biome var2 = this.getBiome(var1);
      return var2.isHumid();
   }

   @Nullable
   public abstract MapItemSavedData getMapData(String var1);

   public abstract void setMapData(MapItemSavedData var1);

   public abstract int getFreeMapId();

   public void globalLevelEvent(int var1, BlockPos var2, int var3) {
   }

   public CrashReportCategory fillReportDetails(CrashReport var1) {
      CrashReportCategory var2 = var1.addCategory("Affected level", 1);
      var2.setDetail("All players", () -> {
         return this.players().size() + " total; " + this.players();
      });
      ChunkSource var10002 = this.getChunkSource();
      var2.setDetail("Chunk stats", var10002::gatherStats);
      var2.setDetail("Level dimension", () -> {
         return this.dimension().location().toString();
      });

      try {
         this.levelData.fillCrashReportCategory(var2);
      } catch (Throwable var4) {
         var2.setDetailError("Level Data Unobtainable", var4);
      }

      return var2;
   }

   public abstract void destroyBlockProgress(int var1, BlockPos var2, int var3);

   public void createFireworks(double var1, double var3, double var5, double var7, double var9, double var11, @Nullable CompoundTag var13) {
   }

   public abstract Scoreboard getScoreboard();

   public void updateNeighbourForOutputSignal(BlockPos var1, Block var2) {
      Iterator var3 = Direction.Plane.HORIZONTAL.iterator();

      while(var3.hasNext()) {
         Direction var4 = (Direction)var3.next();
         BlockPos var5 = var1.relative(var4);
         if (this.hasChunkAt(var5)) {
            BlockState var6 = this.getBlockState(var5);
            if (var6.is(Blocks.COMPARATOR)) {
               var6.neighborChanged(this, var5, var2, var1, false);
            } else if (var6.isRedstoneConductor(this, var5)) {
               var5 = var5.relative(var4);
               var6 = this.getBlockState(var5);
               if (var6.is(Blocks.COMPARATOR)) {
                  var6.neighborChanged(this, var5, var2, var1, false);
               }
            }
         }
      }

   }

   public DifficultyInstance getCurrentDifficultyAt(BlockPos var1) {
      long var2 = 0L;
      float var4 = 0.0F;
      if (this.hasChunkAt(var1)) {
         var4 = this.getMoonBrightness();
         var2 = this.getChunkAt(var1).getInhabitedTime();
      }

      return new DifficultyInstance(this.getDifficulty(), this.getDayTime(), var2, var4);
   }

   public int getSkyDarken() {
      return this.skyDarken;
   }

   public void setSkyFlashTime(int var1) {
   }

   public WorldBorder getWorldBorder() {
      return this.worldBorder;
   }

   public void sendPacketToServer(Packet<?> var1) {
      throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
   }

   public DimensionType dimensionType() {
      return this.dimensionType;
   }

   public ResourceKey<Level> dimension() {
      return this.dimension;
   }

   public Random getRandom() {
      return this.random;
   }

   public boolean isStateAtPosition(BlockPos var1, Predicate<BlockState> var2) {
      return var2.test(this.getBlockState(var1));
   }

   public abstract RecipeManager getRecipeManager();

   public abstract TagContainer getTagManager();

   public BlockPos getBlockRandomPos(int var1, int var2, int var3, int var4) {
      this.randValue = this.randValue * 3 + 1013904223;
      int var5 = this.randValue >> 2;
      return new BlockPos(var1 + (var5 & 15), var2 + (var5 >> 16 & var4), var3 + (var5 >> 8 & 15));
   }

   public boolean noSave() {
      return false;
   }

   public ProfilerFiller getProfiler() {
      return (ProfilerFiller)this.profiler.get();
   }

   public Supplier<ProfilerFiller> getProfilerSupplier() {
      return this.profiler;
   }

   public BiomeManager getBiomeManager() {
      return this.biomeManager;
   }

   public final boolean isDebug() {
      return this.isDebug;
   }

   // $FF: synthetic method
   public ChunkAccess getChunk(int var1, int var2) {
      return this.getChunk(var1, var2);
   }

   static {
      RESOURCE_KEY_CODEC = ResourceLocation.CODEC.xmap(ResourceKey.elementKey(Registry.DIMENSION_REGISTRY), ResourceKey::location);
      OVERWORLD = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("overworld"));
      NETHER = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("the_nether"));
      END = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("the_end"));
      DIRECTIONS = Direction.values();
   }
}
