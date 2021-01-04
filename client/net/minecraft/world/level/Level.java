package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagManager;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public abstract class Level implements BlockAndBiomeGetter, LevelAccessor, AutoCloseable {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final Direction[] DIRECTIONS = Direction.values();
   public final List<BlockEntity> blockEntityList = Lists.newArrayList();
   public final List<BlockEntity> tickableBlockEntities = Lists.newArrayList();
   protected final List<BlockEntity> pendingBlockEntities = Lists.newArrayList();
   protected final List<BlockEntity> blockEntitiesToUnload = Lists.newArrayList();
   private final long cloudColor = 16777215L;
   private final Thread thread;
   private int skyDarken;
   protected int randValue = (new Random()).nextInt();
   protected final int addend = 1013904223;
   protected float oRainLevel;
   protected float rainLevel;
   protected float oThunderLevel;
   protected float thunderLevel;
   private int skyFlashTime;
   public final Random random = new Random();
   public final Dimension dimension;
   protected final ChunkSource chunkSource;
   protected final LevelData levelData;
   private final ProfilerFiller profiler;
   public final boolean isClientSide;
   protected boolean updatingBlockEntities;
   private final WorldBorder worldBorder;

   protected Level(LevelData var1, DimensionType var2, BiFunction<Level, Dimension, ChunkSource> var3, ProfilerFiller var4, boolean var5) {
      super();
      this.profiler = var4;
      this.levelData = var1;
      this.dimension = var2.create(this);
      this.chunkSource = (ChunkSource)var3.apply(this, this.dimension);
      this.isClientSide = var5;
      this.worldBorder = this.dimension.createWorldBorder();
      this.thread = Thread.currentThread();
   }

   public Biome getBiome(BlockPos var1) {
      ChunkSource var2 = this.getChunkSource();
      LevelChunk var3 = var2.getChunk(var1.getX() >> 4, var1.getZ() >> 4, false);
      if (var3 != null) {
         return var3.getBiome(var1);
      } else {
         ChunkGenerator var4 = this.getChunkSource().getGenerator();
         return var4 == null ? Biomes.PLAINS : var4.getBiomeSource().getBiome(var1);
      }
   }

   public boolean isClientSide() {
      return this.isClientSide;
   }

   @Nullable
   public MinecraftServer getServer() {
      return null;
   }

   public void validateSpawn() {
      this.setSpawnPos(new BlockPos(8, 64, 8));
   }

   public BlockState getTopBlockState(BlockPos var1) {
      BlockPos var2;
      for(var2 = new BlockPos(var1.getX(), this.getSeaLevel(), var1.getZ()); !this.isEmptyBlock(var2.above()); var2 = var2.above()) {
      }

      return this.getBlockState(var2);
   }

   public static boolean isInWorldBounds(BlockPos var0) {
      return !isOutsideBuildHeight(var0) && var0.getX() >= -30000000 && var0.getZ() >= -30000000 && var0.getX() < 30000000 && var0.getZ() < 30000000;
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
      ChunkAccess var5 = this.chunkSource.getChunk(var1, var2, var3, var4);
      if (var5 == null && var4) {
         throw new IllegalStateException("Should always be able to create a chunk!");
      } else {
         return var5;
      }
   }

   public boolean setBlock(BlockPos var1, BlockState var2, int var3) {
      if (isOutsideBuildHeight(var1)) {
         return false;
      } else if (!this.isClientSide && this.levelData.getGeneratorType() == LevelType.DEBUG_ALL_BLOCK_STATES) {
         return false;
      } else {
         LevelChunk var4 = this.getChunkAt(var1);
         Block var5 = var2.getBlock();
         BlockState var6 = var4.setBlockState(var1, var2, (var3 & 64) != 0);
         if (var6 == null) {
            return false;
         } else {
            BlockState var7 = this.getBlockState(var1);
            if (var7 != var6 && (var7.getLightBlock(this, var1) != var6.getLightBlock(this, var1) || var7.getLightEmission() != var6.getLightEmission() || var7.useShapeForLightOcclusion() || var6.useShapeForLightOcclusion())) {
               this.profiler.push("queueCheckLight");
               this.getChunkSource().getLightEngine().checkBlock(var1);
               this.profiler.pop();
            }

            if (var7 == var2) {
               if (var6 != var7) {
                  this.setBlocksDirty(var1, var6, var7);
               }

               if ((var3 & 2) != 0 && (!this.isClientSide || (var3 & 4) == 0) && (this.isClientSide || var4.getFullStatus() != null && var4.getFullStatus().isOrAfter(ChunkHolder.FullChunkStatus.TICKING))) {
                  this.sendBlockUpdated(var1, var6, var2, var3);
               }

               if (!this.isClientSide && (var3 & 1) != 0) {
                  this.blockUpdated(var1, var6.getBlock());
                  if (var2.hasAnalogOutputSignal()) {
                     this.updateNeighbourForOutputSignal(var1, var5);
                  }
               }

               if ((var3 & 16) == 0) {
                  int var8 = var3 & -2;
                  var6.updateIndirectNeighbourShapes(this, var1, var8);
                  var2.updateNeighbourShapes(this, var1, var8);
                  var2.updateIndirectNeighbourShapes(this, var1, var8);
               }

               this.onBlockStateChange(var1, var6, var7);
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

   public boolean destroyBlock(BlockPos var1, boolean var2) {
      BlockState var3 = this.getBlockState(var1);
      if (var3.isAir()) {
         return false;
      } else {
         FluidState var4 = this.getFluidState(var1);
         this.levelEvent(2001, var1, Block.getId(var3));
         if (var2) {
            BlockEntity var5 = var3.getBlock().isEntityBlock() ? this.getBlockEntity(var1) : null;
            Block.dropResources(var3, this, var1, var5);
         }

         return this.setBlock(var1, var4.createLegacyBlock(), 3);
      }
   }

   public boolean setBlockAndUpdate(BlockPos var1, BlockState var2) {
      return this.setBlock(var1, var2, 3);
   }

   public abstract void sendBlockUpdated(BlockPos var1, BlockState var2, BlockState var3, int var4);

   public void blockUpdated(BlockPos var1, Block var2) {
      if (this.levelData.getGeneratorType() != LevelType.DEBUG_ALL_BLOCK_STATES) {
         this.updateNeighborsAt(var1, var2);
      }

   }

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

   public int getRawBrightness(BlockPos var1, int var2) {
      if (var1.getX() >= -30000000 && var1.getZ() >= -30000000 && var1.getX() < 30000000 && var1.getZ() < 30000000) {
         if (var1.getY() < 0) {
            return 0;
         } else {
            if (var1.getY() >= 256) {
               var1 = new BlockPos(var1.getX(), 255, var1.getZ());
            }

            return this.getChunkAt(var1).getRawBrightness(var1, var2);
         }
      } else {
         return 15;
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

   public int getBrightness(LightLayer var1, BlockPos var2) {
      return this.getChunkSource().getLightEngine().getLayerListener(var1).getLightValue(var2);
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
      return this.skyDarken < 4;
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

   public float getSkyDarken(float var1) {
      float var2 = this.getTimeOfDay(var1);
      float var3 = 1.0F - (Mth.cos(var2 * 6.2831855F) * 2.0F + 0.2F);
      var3 = Mth.clamp(var3, 0.0F, 1.0F);
      var3 = 1.0F - var3;
      var3 = (float)((double)var3 * (1.0D - (double)(this.getRainLevel(var1) * 5.0F) / 16.0D));
      var3 = (float)((double)var3 * (1.0D - (double)(this.getThunderLevel(var1) * 5.0F) / 16.0D));
      return var3 * 0.8F + 0.2F;
   }

   public Vec3 getSkyColor(BlockPos var1, float var2) {
      float var3 = this.getTimeOfDay(var2);
      float var4 = Mth.cos(var3 * 6.2831855F) * 2.0F + 0.5F;
      var4 = Mth.clamp(var4, 0.0F, 1.0F);
      Biome var5 = this.getBiome(var1);
      float var6 = var5.getTemperature(var1);
      int var7 = var5.getSkyColor(var6);
      float var8 = (float)(var7 >> 16 & 255) / 255.0F;
      float var9 = (float)(var7 >> 8 & 255) / 255.0F;
      float var10 = (float)(var7 & 255) / 255.0F;
      var8 *= var4;
      var9 *= var4;
      var10 *= var4;
      float var11 = this.getRainLevel(var2);
      float var12;
      float var13;
      if (var11 > 0.0F) {
         var12 = (var8 * 0.3F + var9 * 0.59F + var10 * 0.11F) * 0.6F;
         var13 = 1.0F - var11 * 0.75F;
         var8 = var8 * var13 + var12 * (1.0F - var13);
         var9 = var9 * var13 + var12 * (1.0F - var13);
         var10 = var10 * var13 + var12 * (1.0F - var13);
      }

      var12 = this.getThunderLevel(var2);
      if (var12 > 0.0F) {
         var13 = (var8 * 0.3F + var9 * 0.59F + var10 * 0.11F) * 0.2F;
         float var14 = 1.0F - var12 * 0.75F;
         var8 = var8 * var14 + var13 * (1.0F - var14);
         var9 = var9 * var14 + var13 * (1.0F - var14);
         var10 = var10 * var14 + var13 * (1.0F - var14);
      }

      if (this.skyFlashTime > 0) {
         var13 = (float)this.skyFlashTime - var2;
         if (var13 > 1.0F) {
            var13 = 1.0F;
         }

         var13 *= 0.45F;
         var8 = var8 * (1.0F - var13) + 0.8F * var13;
         var9 = var9 * (1.0F - var13) + 0.8F * var13;
         var10 = var10 * (1.0F - var13) + 1.0F * var13;
      }

      return new Vec3((double)var8, (double)var9, (double)var10);
   }

   public float getSunAngle(float var1) {
      float var2 = this.getTimeOfDay(var1);
      return var2 * 6.2831855F;
   }

   public Vec3 getCloudColor(float var1) {
      float var2 = this.getTimeOfDay(var1);
      float var3 = Mth.cos(var2 * 6.2831855F) * 2.0F + 0.5F;
      var3 = Mth.clamp(var3, 0.0F, 1.0F);
      float var4 = 1.0F;
      float var5 = 1.0F;
      float var6 = 1.0F;
      float var7 = this.getRainLevel(var1);
      float var8;
      float var9;
      if (var7 > 0.0F) {
         var8 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.6F;
         var9 = 1.0F - var7 * 0.95F;
         var4 = var4 * var9 + var8 * (1.0F - var9);
         var5 = var5 * var9 + var8 * (1.0F - var9);
         var6 = var6 * var9 + var8 * (1.0F - var9);
      }

      var4 *= var3 * 0.9F + 0.1F;
      var5 *= var3 * 0.9F + 0.1F;
      var6 *= var3 * 0.85F + 0.15F;
      var8 = this.getThunderLevel(var1);
      if (var8 > 0.0F) {
         var9 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.2F;
         float var10 = 1.0F - var8 * 0.95F;
         var4 = var4 * var10 + var9 * (1.0F - var10);
         var5 = var5 * var10 + var9 * (1.0F - var10);
         var6 = var6 * var10 + var9 * (1.0F - var10);
      }

      return new Vec3((double)var4, (double)var5, (double)var6);
   }

   public Vec3 getFogColor(float var1) {
      float var2 = this.getTimeOfDay(var1);
      return this.dimension.getFogColor(var2, var1);
   }

   public float getStarBrightness(float var1) {
      float var2 = this.getTimeOfDay(var1);
      float var3 = 1.0F - (Mth.cos(var2 * 6.2831855F) * 2.0F + 0.25F);
      var3 = Mth.clamp(var3, 0.0F, 1.0F);
      return var3 * var3 * 0.5F;
   }

   public boolean addBlockEntity(BlockEntity var1) {
      if (this.updatingBlockEntities) {
         LOGGER.error("Adding block entity while ticking: {} @ {}", new Supplier[]{() -> {
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
            if (this.chunkSource.isTickingChunk(var4) && this.getWorldBorder().isWithinBounds(var4)) {
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

   public boolean containsAnyBlocks(AABB var1) {
      int var2 = Mth.floor(var1.minX);
      int var3 = Mth.ceil(var1.maxX);
      int var4 = Mth.floor(var1.minY);
      int var5 = Mth.ceil(var1.maxY);
      int var6 = Mth.floor(var1.minZ);
      int var7 = Mth.ceil(var1.maxZ);
      BlockPos.PooledMutableBlockPos var8 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var9 = null;

      try {
         for(int var10 = var2; var10 < var3; ++var10) {
            for(int var11 = var4; var11 < var5; ++var11) {
               for(int var12 = var6; var12 < var7; ++var12) {
                  BlockState var13 = this.getBlockState(var8.set(var10, var11, var12));
                  if (!var13.isAir()) {
                     boolean var14 = true;
                     return var14;
                  }
               }
            }
         }
      } catch (Throwable var24) {
         var9 = var24;
         throw var24;
      } finally {
         if (var8 != null) {
            if (var9 != null) {
               try {
                  var8.close();
               } catch (Throwable var23) {
                  var9.addSuppressed(var23);
               }
            } else {
               var8.close();
            }
         }

      }

      return false;
   }

   public boolean containsFireBlock(AABB var1) {
      int var2 = Mth.floor(var1.minX);
      int var3 = Mth.ceil(var1.maxX);
      int var4 = Mth.floor(var1.minY);
      int var5 = Mth.ceil(var1.maxY);
      int var6 = Mth.floor(var1.minZ);
      int var7 = Mth.ceil(var1.maxZ);
      if (this.hasChunksAt(var2, var4, var6, var3, var5, var7)) {
         BlockPos.PooledMutableBlockPos var8 = BlockPos.PooledMutableBlockPos.acquire();
         Throwable var9 = null;

         try {
            for(int var10 = var2; var10 < var3; ++var10) {
               for(int var11 = var4; var11 < var5; ++var11) {
                  for(int var12 = var6; var12 < var7; ++var12) {
                     Block var13 = this.getBlockState(var8.set(var10, var11, var12)).getBlock();
                     if (var13 == Blocks.FIRE || var13 == Blocks.LAVA) {
                        boolean var14 = true;
                        return var14;
                     }
                  }
               }
            }
         } catch (Throwable var24) {
            var9 = var24;
            throw var24;
         } finally {
            if (var8 != null) {
               if (var9 != null) {
                  try {
                     var8.close();
                  } catch (Throwable var23) {
                     var9.addSuppressed(var23);
                  }
               } else {
                  var8.close();
               }
            }

         }
      }

      return false;
   }

   @Nullable
   public BlockState containsBlock(AABB var1, Block var2) {
      int var3 = Mth.floor(var1.minX);
      int var4 = Mth.ceil(var1.maxX);
      int var5 = Mth.floor(var1.minY);
      int var6 = Mth.ceil(var1.maxY);
      int var7 = Mth.floor(var1.minZ);
      int var8 = Mth.ceil(var1.maxZ);
      if (this.hasChunksAt(var3, var5, var7, var4, var6, var8)) {
         BlockPos.PooledMutableBlockPos var9 = BlockPos.PooledMutableBlockPos.acquire();
         Throwable var10 = null;

         try {
            for(int var11 = var3; var11 < var4; ++var11) {
               for(int var12 = var5; var12 < var6; ++var12) {
                  for(int var13 = var7; var13 < var8; ++var13) {
                     BlockState var14 = this.getBlockState(var9.set(var11, var12, var13));
                     if (var14.getBlock() == var2) {
                        BlockState var15 = var14;
                        return var15;
                     }
                  }
               }
            }
         } catch (Throwable var25) {
            var10 = var25;
            throw var25;
         } finally {
            if (var9 != null) {
               if (var10 != null) {
                  try {
                     var9.close();
                  } catch (Throwable var24) {
                     var10.addSuppressed(var24);
                  }
               } else {
                  var9.close();
               }
            }

         }
      }

      return null;
   }

   public boolean containsMaterial(AABB var1, Material var2) {
      int var3 = Mth.floor(var1.minX);
      int var4 = Mth.ceil(var1.maxX);
      int var5 = Mth.floor(var1.minY);
      int var6 = Mth.ceil(var1.maxY);
      int var7 = Mth.floor(var1.minZ);
      int var8 = Mth.ceil(var1.maxZ);
      BlockMaterialPredicate var9 = BlockMaterialPredicate.forMaterial(var2);
      return BlockPos.betweenClosedStream(var3, var5, var7, var4 - 1, var6 - 1, var8 - 1).anyMatch((var2x) -> {
         return var9.test(this.getBlockState(var2x));
      });
   }

   public Explosion explode(@Nullable Entity var1, double var2, double var4, double var6, float var8, Explosion.BlockInteraction var9) {
      return this.explode(var1, (DamageSource)null, var2, var4, var6, var8, false, var9);
   }

   public Explosion explode(@Nullable Entity var1, double var2, double var4, double var6, float var8, boolean var9, Explosion.BlockInteraction var10) {
      return this.explode(var1, (DamageSource)null, var2, var4, var6, var8, var9, var10);
   }

   public Explosion explode(@Nullable Entity var1, @Nullable DamageSource var2, double var3, double var5, double var7, float var9, boolean var10, Explosion.BlockInteraction var11) {
      Explosion var12 = new Explosion(this, var1, var3, var5, var7, var9, var10, var11);
      if (var2 != null) {
         var12.setDamageSource(var2);
      }

      var12.explode();
      var12.finalizeExplosion(true);
      return var12;
   }

   public boolean extinguishFire(@Nullable Player var1, BlockPos var2, Direction var3) {
      var2 = var2.relative(var3);
      if (this.getBlockState(var2).getBlock() == Blocks.FIRE) {
         this.levelEvent(var1, 1009, var2, 0);
         this.removeBlock(var2, false);
         return true;
      } else {
         return false;
      }
   }

   public String gatherChunkSourceStats() {
      return this.chunkSource.gatherStats();
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
               var2.setPosition(var1);
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
      return isOutsideBuildHeight(var1) ? false : this.chunkSource.hasChunk(var1.getX() >> 4, var1.getZ() >> 4);
   }

   public boolean loadedAndEntityCanStandOn(BlockPos var1, Entity var2) {
      if (isOutsideBuildHeight(var1)) {
         return false;
      } else {
         ChunkAccess var3 = this.getChunk(var1.getX() >> 4, var1.getZ() >> 4, ChunkStatus.FULL, false);
         return var3 == null ? false : var3.getBlockState(var1).entityCanStandOn(this, var1, var2);
      }
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
      this.chunkSource.close();
   }

   public ChunkStatus statusForCollisions() {
      return ChunkStatus.FULL;
   }

   public List<Entity> getEntities(@Nullable Entity var1, AABB var2, @Nullable Predicate<? super Entity> var3) {
      ArrayList var4 = Lists.newArrayList();
      int var5 = Mth.floor((var2.minX - 2.0D) / 16.0D);
      int var6 = Mth.floor((var2.maxX + 2.0D) / 16.0D);
      int var7 = Mth.floor((var2.minZ - 2.0D) / 16.0D);
      int var8 = Mth.floor((var2.maxZ + 2.0D) / 16.0D);

      for(int var9 = var5; var9 <= var6; ++var9) {
         for(int var10 = var7; var10 <= var8; ++var10) {
            LevelChunk var11 = this.getChunkSource().getChunk(var9, var10, false);
            if (var11 != null) {
               var11.getEntities((Entity)var1, var2, var4, var3);
            }
         }
      }

      return var4;
   }

   public List<Entity> getEntities(@Nullable EntityType<?> var1, AABB var2, Predicate<? super Entity> var3) {
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

   public Level getLevel() {
      return this;
   }

   public LevelType getGeneratorType() {
      return this.levelData.getGeneratorType();
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
      return var3.isRedstoneConductor(this, var1) ? this.getDirectSignalTo(var1) : var3.getSignal(this, var1, var2);
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

   public void setGameTime(long var1) {
      this.levelData.setGameTime(var1);
   }

   public long getSeed() {
      return this.levelData.getSeed();
   }

   public long getGameTime() {
      return this.levelData.getGameTime();
   }

   public long getDayTime() {
      return this.levelData.getDayTime();
   }

   public void setDayTime(long var1) {
      this.levelData.setDayTime(var1);
   }

   protected void tickTime() {
      this.setGameTime(this.levelData.getGameTime() + 1L);
      if (this.levelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
         this.setDayTime(this.levelData.getDayTime() + 1L);
      }

   }

   public BlockPos getSharedSpawnPos() {
      BlockPos var1 = new BlockPos(this.levelData.getXSpawn(), this.levelData.getYSpawn(), this.levelData.getZSpawn());
      if (!this.getWorldBorder().isWithinBounds(var1)) {
         var1 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
      }

      return var1;
   }

   public void setSpawnPos(BlockPos var1) {
      this.levelData.setSpawn(var1);
   }

   public boolean mayInteract(Player var1, BlockPos var2) {
      return true;
   }

   public void broadcastEntityEvent(Entity var1, byte var2) {
   }

   public ChunkSource getChunkSource() {
      return this.chunkSource;
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
      if (this.dimension.isHasSkyLight() && !this.dimension.isHasCeiling()) {
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
         return this.getBiome(var1).getPrecipitation() == Biome.Precipitation.RAIN;
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

   public int getHeight() {
      return this.dimension.isHasCeiling() ? 128 : 256;
   }

   public double getHorizonHeight() {
      return this.levelData.getGeneratorType() == LevelType.FLAT ? 0.0D : 63.0D;
   }

   public CrashReportCategory fillReportDetails(CrashReport var1) {
      CrashReportCategory var2 = var1.addCategory("Affected level", 1);
      var2.setDetail("All players", () -> {
         return this.players().size() + " total; " + this.players();
      });
      ChunkSource var10002 = this.chunkSource;
      var2.setDetail("Chunk stats", var10002::gatherStats);
      var2.setDetail("Level dimension", () -> {
         return this.dimension.getType().toString();
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
            if (var6.getBlock() == Blocks.COMPARATOR) {
               var6.neighborChanged(this, var5, var2, var1, false);
            } else if (var6.isRedstoneConductor(this, var5)) {
               var5 = var5.relative(var4);
               var6 = this.getBlockState(var5);
               if (var6.getBlock() == Blocks.COMPARATOR) {
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

   public int getSkyFlashTime() {
      return this.skyFlashTime;
   }

   public void setSkyFlashTime(int var1) {
      this.skyFlashTime = var1;
   }

   public WorldBorder getWorldBorder() {
      return this.worldBorder;
   }

   public void sendPacketToServer(Packet<?> var1) {
      throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
   }

   @Nullable
   public BlockPos findNearestMapFeature(String var1, BlockPos var2, int var3, boolean var4) {
      return null;
   }

   public Dimension getDimension() {
      return this.dimension;
   }

   public Random getRandom() {
      return this.random;
   }

   public boolean isStateAtPosition(BlockPos var1, Predicate<BlockState> var2) {
      return var2.test(this.getBlockState(var1));
   }

   public abstract RecipeManager getRecipeManager();

   public abstract TagManager getTagManager();

   public BlockPos getBlockRandomPos(int var1, int var2, int var3, int var4) {
      this.randValue = this.randValue * 3 + 1013904223;
      int var5 = this.randValue >> 2;
      return new BlockPos(var1 + (var5 & 15), var2 + (var5 >> 16 & var4), var3 + (var5 >> 8 & 15));
   }

   public boolean noSave() {
      return false;
   }

   public ProfilerFiller getProfiler() {
      return this.profiler;
   }

   public BlockPos getHeightmapPos(Heightmap.Types var1, BlockPos var2) {
      return new BlockPos(var2.getX(), this.getHeight(var1, var2.getX(), var2.getZ()), var2.getZ());
   }

   // $FF: synthetic method
   public ChunkAccess getChunk(int var1, int var2) {
      return this.getChunk(var1, var2);
   }
}
