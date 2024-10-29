package net.minecraft.world.level;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.CollectingNeighborUpdater;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;

public abstract class Level implements LevelAccessor, AutoCloseable {
   public static final Codec<ResourceKey<Level>> RESOURCE_KEY_CODEC;
   public static final ResourceKey<Level> OVERWORLD;
   public static final ResourceKey<Level> NETHER;
   public static final ResourceKey<Level> END;
   public static final int MAX_LEVEL_SIZE = 30000000;
   public static final int LONG_PARTICLE_CLIP_RANGE = 512;
   public static final int SHORT_PARTICLE_CLIP_RANGE = 32;
   public static final int MAX_BRIGHTNESS = 15;
   public static final int TICKS_PER_DAY = 24000;
   public static final int MAX_ENTITY_SPAWN_Y = 20000000;
   public static final int MIN_ENTITY_SPAWN_Y = -20000000;
   protected final List<TickingBlockEntity> blockEntityTickers = Lists.newArrayList();
   protected final NeighborUpdater neighborUpdater;
   private final List<TickingBlockEntity> pendingBlockEntityTickers = Lists.newArrayList();
   private boolean tickingBlockEntities;
   private final Thread thread;
   private final boolean isDebug;
   private int skyDarken;
   protected int randValue = RandomSource.create().nextInt();
   protected final int addend = 1013904223;
   protected float oRainLevel;
   protected float rainLevel;
   protected float oThunderLevel;
   protected float thunderLevel;
   public final RandomSource random = RandomSource.create();
   /** @deprecated */
   @Deprecated
   private final RandomSource threadSafeRandom = RandomSource.createThreadSafe();
   private final Holder<DimensionType> dimensionTypeRegistration;
   protected final WritableLevelData levelData;
   public final boolean isClientSide;
   private final WorldBorder worldBorder;
   private final BiomeManager biomeManager;
   private final ResourceKey<Level> dimension;
   private final RegistryAccess registryAccess;
   private final DamageSources damageSources;
   private long subTickCount;

   protected Level(WritableLevelData var1, ResourceKey<Level> var2, RegistryAccess var3, Holder<DimensionType> var4, boolean var5, boolean var6, long var7, int var9) {
      super();
      this.levelData = var1;
      this.dimensionTypeRegistration = var4;
      final DimensionType var10 = (DimensionType)var4.value();
      this.dimension = var2;
      this.isClientSide = var5;
      if (var10.coordinateScale() != 1.0) {
         this.worldBorder = new WorldBorder(this) {
            public double getCenterX() {
               return super.getCenterX() / var10.coordinateScale();
            }

            public double getCenterZ() {
               return super.getCenterZ() / var10.coordinateScale();
            }
         };
      } else {
         this.worldBorder = new WorldBorder();
      }

      this.thread = Thread.currentThread();
      this.biomeManager = new BiomeManager(this, var7);
      this.isDebug = var6;
      this.neighborUpdater = new CollectingNeighborUpdater(this, var9);
      this.registryAccess = var3;
      this.damageSources = new DamageSources(var3);
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
            if (var8 == var2) {
               if (var7 != var8) {
                  this.setBlocksDirty(var1, var7, var8);
               }

               if ((var3 & 2) != 0 && (!this.isClientSide || (var3 & 4) == 0) && (this.isClientSide || var5.getFullStatus() != null && var5.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING))) {
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
            this.gameEvent(GameEvent.BLOCK_DESTROY, var1, GameEvent.Context.of(var3, var5));
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
   }

   public void updateNeighborsAt(BlockPos var1, Block var2, @Nullable Orientation var3) {
   }

   public void updateNeighborsAtExceptFromFacing(BlockPos var1, Block var2, Direction var3, @Nullable Orientation var4) {
   }

   public void neighborChanged(BlockPos var1, Block var2, @Nullable Orientation var3) {
   }

   public void neighborChanged(BlockState var1, BlockPos var2, Block var3, @Nullable Orientation var4, boolean var5) {
   }

   public void neighborShapeChanged(Direction var1, BlockPos var2, BlockPos var3, BlockState var4, int var5, int var6) {
      this.neighborUpdater.shapeUpdate(var1, var4, var2, var3, var5, var6);
   }

   public int getHeight(Heightmap.Types var1, int var2, int var3) {
      int var4;
      if (var2 >= -30000000 && var3 >= -30000000 && var2 < 30000000 && var3 < 30000000) {
         if (this.hasChunk(SectionPos.blockToSectionCoord(var2), SectionPos.blockToSectionCoord(var3))) {
            var4 = this.getChunk(SectionPos.blockToSectionCoord(var2), SectionPos.blockToSectionCoord(var3)).getHeight(var1, var2 & 15, var3 & 15) + 1;
         } else {
            var4 = this.getMinY();
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

   public void playSound(@Nullable Entity var1, BlockPos var2, SoundEvent var3, SoundSource var4, float var5, float var6) {
      Player var10001;
      if (var1 instanceof Player var7) {
         var10001 = var7;
      } else {
         var10001 = null;
      }

      this.playSound(var10001, var2, var3, var4, var5, var6);
   }

   public void playSound(@Nullable Player var1, BlockPos var2, SoundEvent var3, SoundSource var4, float var5, float var6) {
      this.playSound(var1, (double)var2.getX() + 0.5, (double)var2.getY() + 0.5, (double)var2.getZ() + 0.5, var3, var4, var5, var6);
   }

   public abstract void playSeededSound(@Nullable Player var1, double var2, double var4, double var6, Holder<SoundEvent> var8, SoundSource var9, float var10, float var11, long var12);

   public void playSeededSound(@Nullable Player var1, double var2, double var4, double var6, SoundEvent var8, SoundSource var9, float var10, float var11, long var12) {
      this.playSeededSound(var1, var2, var4, var6, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(var8), var9, var10, var11, var12);
   }

   public abstract void playSeededSound(@Nullable Player var1, Entity var2, Holder<SoundEvent> var3, SoundSource var4, float var5, float var6, long var7);

   public void playSound(@Nullable Player var1, double var2, double var4, double var6, SoundEvent var8, SoundSource var9) {
      this.playSound(var1, var2, var4, var6, var8, var9, 1.0F, 1.0F);
   }

   public void playSound(@Nullable Player var1, double var2, double var4, double var6, SoundEvent var8, SoundSource var9, float var10, float var11) {
      this.playSeededSound(var1, var2, var4, var6, var8, var9, var10, var11, this.threadSafeRandom.nextLong());
   }

   public void playSound(@Nullable Player var1, double var2, double var4, double var6, Holder<SoundEvent> var8, SoundSource var9, float var10, float var11) {
      this.playSeededSound(var1, var2, var4, var6, var8, var9, var10, var11, this.threadSafeRandom.nextLong());
   }

   public void playSound(@Nullable Player var1, Entity var2, SoundEvent var3, SoundSource var4, float var5, float var6) {
      this.playSeededSound(var1, var2, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(var3), var4, var5, var6, this.threadSafeRandom.nextLong());
   }

   public void playLocalSound(BlockPos var1, SoundEvent var2, SoundSource var3, float var4, float var5, boolean var6) {
      this.playLocalSound((double)var1.getX() + 0.5, (double)var1.getY() + 0.5, (double)var1.getZ() + 0.5, var2, var3, var4, var5, var6);
   }

   public void playLocalSound(Entity var1, SoundEvent var2, SoundSource var3, float var4, float var5) {
   }

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
      ProfilerFiller var1 = Profiler.get();
      var1.push("blockEntities");
      this.tickingBlockEntities = true;
      if (!this.pendingBlockEntityTickers.isEmpty()) {
         this.blockEntityTickers.addAll(this.pendingBlockEntityTickers);
         this.pendingBlockEntityTickers.clear();
      }

      Iterator var2 = this.blockEntityTickers.iterator();
      boolean var3 = this.tickRateManager().runsNormally();

      while(var2.hasNext()) {
         TickingBlockEntity var4 = (TickingBlockEntity)var2.next();
         if (var4.isRemoved()) {
            var2.remove();
         } else if (var3 && this.shouldTickBlocksAt(var4.getPos())) {
            var4.tick();
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

   public boolean shouldTickBlocksAt(BlockPos var1) {
      return this.shouldTickBlocksAt(ChunkPos.asLong(var1));
   }

   public void explode(@Nullable Entity var1, double var2, double var4, double var6, float var8, ExplosionInteraction var9) {
      this.explode(var1, Explosion.getDefaultDamageSource(this, var1), (ExplosionDamageCalculator)null, var2, var4, var6, var8, false, var9, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);
   }

   public void explode(@Nullable Entity var1, double var2, double var4, double var6, float var8, boolean var9, ExplosionInteraction var10) {
      this.explode(var1, Explosion.getDefaultDamageSource(this, var1), (ExplosionDamageCalculator)null, var2, var4, var6, var8, var9, var10, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);
   }

   public void explode(@Nullable Entity var1, @Nullable DamageSource var2, @Nullable ExplosionDamageCalculator var3, Vec3 var4, float var5, boolean var6, ExplosionInteraction var7) {
      this.explode(var1, var2, var3, var4.x(), var4.y(), var4.z(), var5, var6, var7, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);
   }

   public void explode(@Nullable Entity var1, @Nullable DamageSource var2, @Nullable ExplosionDamageCalculator var3, double var4, double var6, double var8, float var10, boolean var11, ExplosionInteraction var12) {
      this.explode(var1, var2, var3, var4, var6, var8, var10, var11, var12, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);
   }

   public abstract void explode(@Nullable Entity var1, @Nullable DamageSource var2, @Nullable ExplosionDamageCalculator var3, double var4, double var6, double var8, float var10, boolean var11, ExplosionInteraction var12, ParticleOptions var13, ParticleOptions var14, Holder<SoundEvent> var15);

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
      return this.loadedAndEntityCanStandOnFace(var1, var2, Direction.UP);
   }

   public void updateSkyBrightness() {
      double var1 = 1.0 - (double)(this.getRainLevel(1.0F) * 5.0F) / 16.0;
      double var3 = 1.0 - (double)(this.getThunderLevel(1.0F) * 5.0F) / 16.0;
      double var5 = 0.5 + 2.0 * Mth.clamp((double)Mth.cos(this.getTimeOfDay(1.0F) * 6.2831855F), -0.25, 0.25);
      this.skyDarken = (int)((1.0 - var5 * var1 * var3) * 11.0);
   }

   public void setSpawnSettings(boolean var1) {
      this.getChunkSource().setSpawnSettings(var1);
   }

   public BlockPos getSharedSpawnPos() {
      BlockPos var1 = this.levelData.getSpawnPos();
      if (!this.getWorldBorder().isWithinBounds(var1)) {
         var1 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, BlockPos.containing(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
      }

      return var1;
   }

   public float getSharedSpawnAngle() {
      return this.levelData.getSpawnAngle();
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
      Profiler.get().incrementCounter("getEntities");
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
      ArrayList var4 = Lists.newArrayList();
      this.getEntities(var1, var2, var3, var4);
      return var4;
   }

   public <T extends Entity> void getEntities(EntityTypeTest<Entity, T> var1, AABB var2, Predicate<? super T> var3, List<? super T> var4) {
      this.getEntities(var1, var2, var3, var4, 2147483647);
   }

   public <T extends Entity> void getEntities(EntityTypeTest<Entity, T> var1, AABB var2, Predicate<? super T> var3, List<? super T> var4, int var5) {
      Profiler.get().incrementCounter("getEntities");
      this.getEntities().get(var1, var2, (var4x) -> {
         if (var3.test(var4x)) {
            var4.add(var4x);
            if (var4.size() >= var5) {
               return AbortableIterationConsumer.Continuation.ABORT;
            }
         }

         if (var4x instanceof EnderDragon var5x) {
            EnderDragonPart[] var6 = var5x.getSubEntities();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               EnderDragonPart var9 = var6[var8];
               Entity var10 = (Entity)var1.tryCast(var9);
               if (var10 != null && var3.test(var10)) {
                  var4.add(var10);
                  if (var4.size() >= var5) {
                     return AbortableIterationConsumer.Continuation.ABORT;
                  }
               }
            }
         }

         return AbortableIterationConsumer.Continuation.CONTINUE;
      });
   }

   @Nullable
   public abstract Entity getEntity(int var1);

   public void blockEntityChanged(BlockPos var1) {
      if (this.hasChunkAt(var1)) {
         this.getChunkAt(var1).markUnsaved();
      }

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

   public void broadcastDamageEvent(Entity var1, DamageSource var2) {
   }

   public void blockEvent(BlockPos var1, Block var2, int var3, int var4) {
      this.getBlockState(var1).triggerEvent(this, var1, var3, var4);
   }

   public LevelData getLevelData() {
      return this.levelData;
   }

   public abstract TickRateManager tickRateManager();

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

   private boolean canHaveWeather() {
      return this.dimensionType().hasSkyLight() && !this.dimensionType().hasCeiling();
   }

   public boolean isThundering() {
      return this.canHaveWeather() && (double)this.getThunderLevel(1.0F) > 0.9;
   }

   public boolean isRaining() {
      return this.canHaveWeather() && (double)this.getRainLevel(1.0F) > 0.2;
   }

   public boolean isRainingAt(BlockPos var1) {
      if (!this.isRaining()) {
         return false;
      } else if (!this.canSeeSky(var1)) {
         return false;
      } else if (this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var1).getY() > var1.getY()) {
         return false;
      } else {
         Biome var2 = (Biome)this.getBiome(var1).value();
         return var2.getPrecipitationAt(var1, this.getSeaLevel()) == Biome.Precipitation.RAIN;
      }
   }

   @Nullable
   public abstract MapItemSavedData getMapData(MapId var1);

   public abstract void setMapData(MapId var1, MapItemSavedData var2);

   public abstract MapId getFreeMapId();

   public void globalLevelEvent(int var1, BlockPos var2, int var3) {
   }

   public CrashReportCategory fillReportDetails(CrashReport var1) {
      CrashReportCategory var2 = var1.addCategory("Affected level", 1);
      var2.setDetail("All players", () -> {
         int var10000 = this.players().size();
         return "" + var10000 + " total; " + String.valueOf(this.players());
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

   public void createFireworks(double var1, double var3, double var5, double var7, double var9, double var11, List<FireworkExplosion> var13) {
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
               this.neighborChanged(var6, var5, var2, (Orientation)null, false);
            } else if (var6.isRedstoneConductor(this, var5)) {
               var5 = var5.relative(var4);
               var6 = this.getBlockState(var5);
               if (var6.is(Blocks.COMPARATOR)) {
                  this.neighborChanged(var6, var5, var2, (Orientation)null, false);
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
      return (DimensionType)this.dimensionTypeRegistration.value();
   }

   public Holder<DimensionType> dimensionTypeRegistration() {
      return this.dimensionTypeRegistration;
   }

   public ResourceKey<Level> dimension() {
      return this.dimension;
   }

   public RandomSource getRandom() {
      return this.random;
   }

   public boolean isStateAtPosition(BlockPos var1, Predicate<BlockState> var2) {
      return var2.test(this.getBlockState(var1));
   }

   public boolean isFluidAtPosition(BlockPos var1, Predicate<FluidState> var2) {
      return var2.test(this.getFluidState(var1));
   }

   public abstract RecipeAccess recipeAccess();

   public BlockPos getBlockRandomPos(int var1, int var2, int var3, int var4) {
      this.randValue = this.randValue * 3 + 1013904223;
      int var5 = this.randValue >> 2;
      return new BlockPos(var1 + (var5 & 15), var2 + (var5 >> 16 & var4), var3 + (var5 >> 8 & 15));
   }

   public boolean noSave() {
      return false;
   }

   public BiomeManager getBiomeManager() {
      return this.biomeManager;
   }

   public final boolean isDebug() {
      return this.isDebug;
   }

   protected abstract LevelEntityGetter<Entity> getEntities();

   public long nextSubTickCount() {
      return (long)(this.subTickCount++);
   }

   public RegistryAccess registryAccess() {
      return this.registryAccess;
   }

   public DamageSources damageSources() {
      return this.damageSources;
   }

   public abstract PotionBrewing potionBrewing();

   public abstract FuelValues fuelValues();

   // $FF: synthetic method
   public ChunkAccess getChunk(final int var1, final int var2) {
      return this.getChunk(var1, var2);
   }

   static {
      RESOURCE_KEY_CODEC = ResourceKey.codec(Registries.DIMENSION);
      OVERWORLD = ResourceKey.create(Registries.DIMENSION, ResourceLocation.withDefaultNamespace("overworld"));
      NETHER = ResourceKey.create(Registries.DIMENSION, ResourceLocation.withDefaultNamespace("the_nether"));
      END = ResourceKey.create(Registries.DIMENSION, ResourceLocation.withDefaultNamespace("the_end"));
   }

   public static enum ExplosionInteraction implements StringRepresentable {
      NONE("none"),
      BLOCK("block"),
      MOB("mob"),
      TNT("tnt"),
      TRIGGER("trigger");

      public static final Codec<ExplosionInteraction> CODEC = StringRepresentable.fromEnum(ExplosionInteraction::values);
      private final String id;

      private ExplosionInteraction(final String var3) {
         this.id = var3;
      }

      public String getSerializedName() {
         return this.id;
      }

      // $FF: synthetic method
      private static ExplosionInteraction[] $values() {
         return new ExplosionInteraction[]{NONE, BLOCK, MOB, TNT, TRIGGER};
      }
   }
}
