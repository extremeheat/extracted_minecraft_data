package net.minecraft.world.level;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
import net.minecraft.core.SectionPos;
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
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
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
   public static final int MAX_LEVEL_SIZE = 30000000;
   public static final int LONG_PARTICLE_CLIP_RANGE = 512;
   public static final int SHORT_PARTICLE_CLIP_RANGE = 32;
   private static final Direction[] DIRECTIONS;
   public static final int MAX_BRIGHTNESS = 15;
   public static final int TICKS_PER_DAY = 24000;
   public static final int MAX_ENTITY_SPAWN_Y = 20000000;
   public static final int MIN_ENTITY_SPAWN_Y = -20000000;
   protected final List<TickingBlockEntity> blockEntityTickers = Lists.newArrayList();
   private final List<TickingBlockEntity> pendingBlockEntityTickers = Lists.newArrayList();
   private boolean tickingBlockEntities;
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
   private final WorldBorder worldBorder;
   private final BiomeManager biomeManager;
   private final ResourceKey<Level> dimension;
   private long subTickCount;

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
      this.biomeManager = new BiomeManager(this, var7);
      this.isDebug = var6;
   }

   public boolean isClientSide() {
      return this.isClientSide;
   }

   @Nullable
   public MinecraftServer getServer() {
      return null;
   }

   public boolean isInWorldBounds(BlockPos var1) {
      return !this.isOutsideBuildHeight(var1) && isInWorldBoundsHorizontal(var1);
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

   public LevelChunk getChunkAt(BlockPos var1) {
      return this.getChunk(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getZ()));
   }

   public LevelChunk getChunk(int var1, int var2) {
      return (LevelChunk)this.getChunk(var1, var2, ChunkStatus.FULL);
   }

   @Nullable
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
      if (this.isOutsideBuildHeight(var1)) {
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
            BlockEntity var7 = var5.hasBlockEntity() ? this.getBlockEntity(var1) : null;
            Block.dropResources(var5, this, var1, var7, var3, ItemStack.EMPTY);
         }

         boolean var8 = this.setBlock(var1, var6.createLegacyBlock(), 3, var4);
         if (var8) {
            this.gameEvent(var3, GameEvent.BLOCK_DESTROY, var1);
         }

         return var8;
      }
   }

   public void addDestroyBlockEffect(BlockPos var1, BlockState var2) {
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

      if (var3 != Direction.field_526) {
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
            CrashReportCategory.populateBlockDetails(var7, this, var1, var4);
            throw new ReportedException(var6);
         }
      }
   }

   public int getHeight(Heightmap.Types var1, int var2, int var3) {
      int var4;
      if (var2 >= -30000000 && var3 >= -30000000 && var2 < 30000000 && var3 < 30000000) {
         if (this.hasChunk(SectionPos.blockToSectionCoord(var2), SectionPos.blockToSectionCoord(var3))) {
            var4 = this.getChunk(SectionPos.blockToSectionCoord(var2), SectionPos.blockToSectionCoord(var3)).getHeight(var1, var2 & 15, var3 & 15) + 1;
         } else {
            var4 = this.getMinBuildHeight();
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
      if (this.isOutsideBuildHeight(var1)) {
         return Blocks.VOID_AIR.defaultBlockState();
      } else {
         LevelChunk var2 = this.getChunk(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getZ()));
         return var2.getBlockState(var1);
      }
   }

   public FluidState getFluidState(BlockPos var1) {
      if (this.isOutsideBuildHeight(var1)) {
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

   public void addBlockEntityTicker(TickingBlockEntity var1) {
      (this.tickingBlockEntities ? this.pendingBlockEntityTickers : this.blockEntityTickers).add(var1);
   }

   protected void tickBlockEntities() {
      ProfilerFiller var1 = this.getProfiler();
      var1.push("blockEntities");
      this.tickingBlockEntities = true;
      if (!this.pendingBlockEntityTickers.isEmpty()) {
         this.blockEntityTickers.addAll(this.pendingBlockEntityTickers);
         this.pendingBlockEntityTickers.clear();
      }

      Iterator var2 = this.blockEntityTickers.iterator();

      while(var2.hasNext()) {
         TickingBlockEntity var3 = (TickingBlockEntity)var2.next();
         if (var3.isRemoved()) {
            var2.remove();
         } else if (this.shouldTickBlocksAt(ChunkPos.asLong(var3.getPos()))) {
            var3.tick();
         }
      }

      this.tickingBlockEntities = false;
      var1.pop();
   }

   public <T extends Entity> void guardEntityTick(Consumer<T> var1, T var2) {
      try {
         var1.accept(var2);
      } catch (Throwable var6) {
         CrashReport var4 = CrashReport.forThrowable(var6, "Ticking entity");
         CrashReportCategory var5 = var4.addCategory("Entity being ticked");
         var2.fillCrashReportCategory(var5);
         throw new ReportedException(var4);
      }
   }

   public boolean shouldTickDeath(Entity var1) {
      return true;
   }

   public boolean shouldTickBlocksAt(long var1) {
      return true;
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

   public abstract String gatherChunkSourceStats();

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1) {
      if (this.isOutsideBuildHeight(var1)) {
         return null;
      } else {
         return !this.isClientSide && Thread.currentThread() != this.thread ? null : this.getChunkAt(var1).getBlockEntity(var1, LevelChunk.EntityCreationType.IMMEDIATE);
      }
   }

   public void setBlockEntity(BlockEntity var1) {
      BlockPos var2 = var1.getBlockPos();
      if (!this.isOutsideBuildHeight(var2)) {
         this.getChunkAt(var2).addAndRegisterBlockEntity(var1);
      }
   }

   public void removeBlockEntity(BlockPos var1) {
      if (!this.isOutsideBuildHeight(var1)) {
         this.getChunkAt(var1).removeBlockEntity(var1);
      }
   }

   public boolean isLoaded(BlockPos var1) {
      return this.isOutsideBuildHeight(var1) ? false : this.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getZ()));
   }

   public boolean loadedAndEntityCanStandOnFace(BlockPos var1, Entity var2, Direction var3) {
      if (this.isOutsideBuildHeight(var1)) {
         return false;
      } else {
         ChunkAccess var4 = this.getChunk(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getZ()), ChunkStatus.FULL, false);
         return var4 == null ? false : var4.getBlockState(var1).entityCanStandOnFace(this, var1, var2, var3);
      }
   }

   public boolean loadedAndEntityCanStandOn(BlockPos var1, Entity var2) {
      return this.loadedAndEntityCanStandOnFace(var1, var2, Direction.field_526);
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

   public List<Entity> getEntities(@Nullable Entity var1, AABB var2, Predicate<? super Entity> var3) {
      this.getProfiler().incrementCounter("getEntities");
      ArrayList var4 = Lists.newArrayList();
      this.getEntities().get(var2, (var3x) -> {
         if (var3x != var1 && var3.test(var3x)) {
            var4.add(var3x);
         }

         if (var3x instanceof EnderDragon) {
            EnderDragonPart[] var4x = ((EnderDragon)var3x).getSubEntities();
            int var5 = var4x.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               EnderDragonPart var7 = var4x[var6];
               if (var3x != var1 && var3.test(var7)) {
                  var4.add(var7);
               }
            }
         }

      });
      return var4;
   }

   public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> var1, AABB var2, Predicate<? super T> var3) {
      this.getProfiler().incrementCounter("getEntities");
      ArrayList var4 = Lists.newArrayList();
      this.getEntities().get(var1, var2, (var3x) -> {
         if (var3.test(var3x)) {
            var4.add(var3x);
         }

         if (var3x instanceof EnderDragon) {
            EnderDragon var4x = (EnderDragon)var3x;
            EnderDragonPart[] var5 = var4x.getSubEntities();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               EnderDragonPart var8 = var5[var7];
               Entity var9 = (Entity)var1.tryCast(var8);
               if (var9 != null && var3.test(var9)) {
                  var4.add(var9);
               }
            }
         }

      });
      return var4;
   }

   @Nullable
   public abstract Entity getEntity(int var1);

   public void blockEntityChanged(BlockPos var1) {
      if (this.hasChunkAt(var1)) {
         this.getChunkAt(var1).setUnsaved(true);
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
         var3 = Math.max(var3, this.getDirectSignal(var1.above(), Direction.field_526));
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
      } else if (this.getSignal(var1.above(), Direction.field_526) > 0) {
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
      float var2 = Mth.clamp(var1, 0.0F, 1.0F);
      this.oThunderLevel = var2;
      this.thunderLevel = var2;
   }

   public float getRainLevel(float var1) {
      return Mth.lerp(var1, this.oRainLevel, this.rainLevel);
   }

   public void setRainLevel(float var1) {
      float var2 = Mth.clamp(var1, 0.0F, 1.0F);
      this.oRainLevel = var2;
      this.rainLevel = var2;
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
         return var2.getPrecipitation() == Biome.Precipitation.RAIN && var2.warmEnoughToRain(var1);
      }
   }

   public boolean isHumidAt(BlockPos var1) {
      Biome var2 = this.getBiome(var1);
      return var2.isHumid();
   }

   @Nullable
   public abstract MapItemSavedData getMapData(String var1);

   public abstract void setMapData(String var1, MapItemSavedData var2);

   public abstract int getFreeMapId();

   public void globalLevelEvent(int var1, BlockPos var2, int var3) {
   }

   public CrashReportCategory fillReportDetails(CrashReport var1) {
      CrashReportCategory var2 = var1.addCategory("Affected level", 1);
      var2.setDetail("All players", () -> {
         int var10000 = this.players().size();
         return var10000 + " total; " + this.players();
      });
      ChunkSource var10002 = this.getChunkSource();
      Objects.requireNonNull(var10002);
      var2.setDetail("Chunk stats", var10002::gatherStats);
      var2.setDetail("Level dimension", () -> {
         return this.dimension().location().toString();
      });

      try {
         this.levelData.fillCrashReportCategory(var2, this);
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

   public boolean isFluidAtPosition(BlockPos var1, Predicate<FluidState> var2) {
      return var2.test(this.getFluidState(var1));
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

   protected abstract LevelEntityGetter<Entity> getEntities();

   protected void postGameEventInRadius(@Nullable Entity var1, GameEvent var2, BlockPos var3, int var4) {
      int var5 = SectionPos.blockToSectionCoord(var3.getX() - var4);
      int var6 = SectionPos.blockToSectionCoord(var3.getZ() - var4);
      int var7 = SectionPos.blockToSectionCoord(var3.getX() + var4);
      int var8 = SectionPos.blockToSectionCoord(var3.getZ() + var4);
      int var9 = SectionPos.blockToSectionCoord(var3.getY() - var4);
      int var10 = SectionPos.blockToSectionCoord(var3.getY() + var4);

      for(int var11 = var5; var11 <= var7; ++var11) {
         for(int var12 = var6; var12 <= var8; ++var12) {
            LevelChunk var13 = this.getChunkSource().getChunkNow(var11, var12);
            if (var13 != null) {
               for(int var14 = var9; var14 <= var10; ++var14) {
                  var13.getEventDispatcher(var14).post(var2, var1, var3);
               }
            }
         }
      }

   }

   public long nextSubTickCount() {
      return (long)(this.subTickCount++);
   }

   public boolean shouldDelayFallingBlockEntityRemoval(Entity.RemovalReason var1) {
      return false;
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
