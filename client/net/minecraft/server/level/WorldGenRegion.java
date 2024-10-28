package net.minecraft.server.level;

import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.status.ChunkDependencies;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.ChunkType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.WorldGenTickAccess;
import org.slf4j.Logger;

public class WorldGenRegion implements WorldGenLevel {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final StaticCache2D<GenerationChunkHolder> cache;
   private final ChunkAccess center;
   private final ServerLevel level;
   private final long seed;
   private final LevelData levelData;
   private final RandomSource random;
   private final DimensionType dimensionType;
   private final WorldGenTickAccess<Block> blockTicks = new WorldGenTickAccess((var1x) -> {
      return this.getChunk(var1x).getBlockTicks();
   });
   private final WorldGenTickAccess<Fluid> fluidTicks = new WorldGenTickAccess((var1x) -> {
      return this.getChunk(var1x).getFluidTicks();
   });
   private final BiomeManager biomeManager;
   private final ChunkStep generatingStep;
   @Nullable
   private Supplier<String> currentlyGenerating;
   private final AtomicLong subTickCount = new AtomicLong();
   private static final ResourceLocation WORLDGEN_REGION_RANDOM = ResourceLocation.withDefaultNamespace("worldgen_region_random");

   public WorldGenRegion(ServerLevel var1, StaticCache2D<GenerationChunkHolder> var2, ChunkStep var3, ChunkAccess var4) {
      super();
      this.generatingStep = var3;
      this.cache = var2;
      this.center = var4;
      this.level = var1;
      this.seed = var1.getSeed();
      this.levelData = var1.getLevelData();
      this.random = var1.getChunkSource().randomState().getOrCreateRandomFactory(WORLDGEN_REGION_RANDOM).at(this.center.getPos().getWorldPosition());
      this.dimensionType = var1.dimensionType();
      this.biomeManager = new BiomeManager(this, BiomeManager.obfuscateSeed(this.seed));
   }

   public boolean isOldChunkAround(ChunkPos var1, int var2) {
      return this.level.getChunkSource().chunkMap.isOldChunkAround(var1, var2);
   }

   public ChunkPos getCenter() {
      return this.center.getPos();
   }

   public void setCurrentlyGenerating(@Nullable Supplier<String> var1) {
      this.currentlyGenerating = var1;
   }

   public ChunkAccess getChunk(int var1, int var2) {
      return this.getChunk(var1, var2, ChunkStatus.EMPTY);
   }

   @Nullable
   public ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4) {
      int var5 = this.center.getPos().getChessboardDistance(var1, var2);
      ChunkStatus var6 = var5 >= this.generatingStep.directDependencies().size() ? null : this.generatingStep.directDependencies().get(var5);
      GenerationChunkHolder var7;
      if (var6 != null) {
         var7 = (GenerationChunkHolder)this.cache.get(var1, var2);
         if (var3.isOrBefore(var6)) {
            ChunkAccess var8 = var7.getChunkIfPresentUnchecked(var6);
            if (var8 != null) {
               return var8;
            }
         }
      } else {
         var7 = null;
      }

      CrashReport var11 = CrashReport.forThrowable(new IllegalStateException("Requested chunk unavailable during world generation"), "Exception generating new chunk");
      CrashReportCategory var9 = var11.addCategory("Chunk request details");
      var9.setDetail("Requested chunk", (Object)String.format(Locale.ROOT, "%d, %d", var1, var2));
      var9.setDetail("Generating status", () -> {
         return this.generatingStep.targetStatus().getName();
      });
      Objects.requireNonNull(var3);
      var9.setDetail("Requested status", var3::getName);
      var9.setDetail("Actual status", () -> {
         return var7 == null ? "[out of cache bounds]" : var7.getPersistedStatus().getName();
      });
      var9.setDetail("Maximum allowed status", () -> {
         return var6 == null ? "null" : var6.getName();
      });
      ChunkDependencies var10002 = this.generatingStep.directDependencies();
      Objects.requireNonNull(var10002);
      var9.setDetail("Dependencies", var10002::toString);
      var9.setDetail("Requested distance", (Object)var5);
      ChunkPos var10 = this.center.getPos();
      Objects.requireNonNull(var10);
      var9.setDetail("Generating chunk", var10::toString);
      throw new ReportedException(var11);
   }

   public boolean hasChunk(int var1, int var2) {
      int var3 = this.center.getPos().getChessboardDistance(var1, var2);
      return var3 < this.generatingStep.directDependencies().size();
   }

   public BlockState getBlockState(BlockPos var1) {
      return this.getChunk(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getZ())).getBlockState(var1);
   }

   public FluidState getFluidState(BlockPos var1) {
      return this.getChunk(var1).getFluidState(var1);
   }

   @Nullable
   public Player getNearestPlayer(double var1, double var3, double var5, double var7, Predicate<Entity> var9) {
      return null;
   }

   public int getSkyDarken() {
      return 0;
   }

   public BiomeManager getBiomeManager() {
      return this.biomeManager;
   }

   public Holder<Biome> getUncachedNoiseBiome(int var1, int var2, int var3) {
      return this.level.getUncachedNoiseBiome(var1, var2, var3);
   }

   public float getShade(Direction var1, boolean var2) {
      return 1.0F;
   }

   public LevelLightEngine getLightEngine() {
      return this.level.getLightEngine();
   }

   public boolean destroyBlock(BlockPos var1, boolean var2, @Nullable Entity var3, int var4) {
      BlockState var5 = this.getBlockState(var1);
      if (var5.isAir()) {
         return false;
      } else {
         if (var2) {
            BlockEntity var6 = var5.hasBlockEntity() ? this.getBlockEntity(var1) : null;
            Block.dropResources(var5, this.level, var1, var6, var3, ItemStack.EMPTY);
         }

         return this.setBlock(var1, Blocks.AIR.defaultBlockState(), 3, var4);
      }
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1) {
      ChunkAccess var2 = this.getChunk(var1);
      BlockEntity var3 = var2.getBlockEntity(var1);
      if (var3 != null) {
         return var3;
      } else {
         CompoundTag var4 = var2.getBlockEntityNbt(var1);
         BlockState var5 = var2.getBlockState(var1);
         if (var4 != null) {
            if ("DUMMY".equals(var4.getString("id"))) {
               if (!var5.hasBlockEntity()) {
                  return null;
               }

               var3 = ((EntityBlock)var5.getBlock()).newBlockEntity(var1, var5);
            } else {
               var3 = BlockEntity.loadStatic(var1, var5, var4, this.level.registryAccess());
            }

            if (var3 != null) {
               var2.setBlockEntity(var3);
               return var3;
            }
         }

         if (var5.hasBlockEntity()) {
            LOGGER.warn("Tried to access a block entity before it was created. {}", var1);
         }

         return null;
      }
   }

   public boolean ensureCanWrite(BlockPos var1) {
      int var2 = SectionPos.blockToSectionCoord(var1.getX());
      int var3 = SectionPos.blockToSectionCoord(var1.getZ());
      ChunkPos var4 = this.getCenter();
      int var5 = Math.abs(var4.x - var2);
      int var6 = Math.abs(var4.z - var3);
      if (var5 <= this.generatingStep.blockStateWriteRadius() && var6 <= this.generatingStep.blockStateWriteRadius()) {
         if (this.center.isUpgrading()) {
            LevelHeightAccessor var7 = this.center.getHeightAccessorForGeneration();
            if (var1.getY() < var7.getMinBuildHeight() || var1.getY() >= var7.getMaxBuildHeight()) {
               return false;
            }
         }

         return true;
      } else {
         Util.logAndPauseIfInIde("Detected setBlock in a far chunk [" + var2 + ", " + var3 + "], pos: " + String.valueOf(var1) + ", status: " + String.valueOf(this.generatingStep.targetStatus()) + (this.currentlyGenerating == null ? "" : ", currently generating: " + (String)this.currentlyGenerating.get()));
         return false;
      }
   }

   public boolean setBlock(BlockPos var1, BlockState var2, int var3, int var4) {
      if (!this.ensureCanWrite(var1)) {
         return false;
      } else {
         ChunkAccess var5 = this.getChunk(var1);
         BlockState var6 = var5.setBlockState(var1, var2, false);
         if (var6 != null) {
            this.level.onBlockStateChange(var1, var6, var2);
         }

         if (var2.hasBlockEntity()) {
            if (var5.getPersistedStatus().getChunkType() == ChunkType.LEVELCHUNK) {
               BlockEntity var7 = ((EntityBlock)var2.getBlock()).newBlockEntity(var1, var2);
               if (var7 != null) {
                  var5.setBlockEntity(var7);
               } else {
                  var5.removeBlockEntity(var1);
               }
            } else {
               CompoundTag var8 = new CompoundTag();
               var8.putInt("x", var1.getX());
               var8.putInt("y", var1.getY());
               var8.putInt("z", var1.getZ());
               var8.putString("id", "DUMMY");
               var5.setBlockEntityNbt(var8);
            }
         } else if (var6 != null && var6.hasBlockEntity()) {
            var5.removeBlockEntity(var1);
         }

         if (var2.hasPostProcess(this, var1)) {
            this.markPosForPostprocessing(var1);
         }

         return true;
      }
   }

   private void markPosForPostprocessing(BlockPos var1) {
      this.getChunk(var1).markPosForPostprocessing(var1);
   }

   public boolean addFreshEntity(Entity var1) {
      int var2 = SectionPos.blockToSectionCoord(var1.getBlockX());
      int var3 = SectionPos.blockToSectionCoord(var1.getBlockZ());
      this.getChunk(var2, var3).addEntity(var1);
      return true;
   }

   public boolean removeBlock(BlockPos var1, boolean var2) {
      return this.setBlock(var1, Blocks.AIR.defaultBlockState(), 3);
   }

   public WorldBorder getWorldBorder() {
      return this.level.getWorldBorder();
   }

   public boolean isClientSide() {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public ServerLevel getLevel() {
      return this.level;
   }

   public RegistryAccess registryAccess() {
      return this.level.registryAccess();
   }

   public FeatureFlagSet enabledFeatures() {
      return this.level.enabledFeatures();
   }

   public LevelData getLevelData() {
      return this.levelData;
   }

   public DifficultyInstance getCurrentDifficultyAt(BlockPos var1) {
      if (!this.hasChunk(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getZ()))) {
         throw new RuntimeException("We are asking a region for a chunk out of bound");
      } else {
         return new DifficultyInstance(this.level.getDifficulty(), this.level.getDayTime(), 0L, this.level.getMoonBrightness());
      }
   }

   @Nullable
   public MinecraftServer getServer() {
      return this.level.getServer();
   }

   public ChunkSource getChunkSource() {
      return this.level.getChunkSource();
   }

   public long getSeed() {
      return this.seed;
   }

   public LevelTickAccess<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public LevelTickAccess<Fluid> getFluidTicks() {
      return this.fluidTicks;
   }

   public int getSeaLevel() {
      return this.level.getSeaLevel();
   }

   public RandomSource getRandom() {
      return this.random;
   }

   public int getHeight(Heightmap.Types var1, int var2, int var3) {
      return this.getChunk(SectionPos.blockToSectionCoord(var2), SectionPos.blockToSectionCoord(var3)).getHeight(var1, var2 & 15, var3 & 15) + 1;
   }

   public void playSound(@Nullable Player var1, BlockPos var2, SoundEvent var3, SoundSource var4, float var5, float var6) {
   }

   public void addParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12) {
   }

   public void levelEvent(@Nullable Player var1, int var2, BlockPos var3, int var4) {
   }

   public void gameEvent(Holder<GameEvent> var1, Vec3 var2, GameEvent.Context var3) {
   }

   public DimensionType dimensionType() {
      return this.dimensionType;
   }

   public boolean isStateAtPosition(BlockPos var1, Predicate<BlockState> var2) {
      return var2.test(this.getBlockState(var1));
   }

   public boolean isFluidAtPosition(BlockPos var1, Predicate<FluidState> var2) {
      return var2.test(this.getFluidState(var1));
   }

   public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> var1, AABB var2, Predicate<? super T> var3) {
      return Collections.emptyList();
   }

   public List<Entity> getEntities(@Nullable Entity var1, AABB var2, @Nullable Predicate<? super Entity> var3) {
      return Collections.emptyList();
   }

   public List<Player> players() {
      return Collections.emptyList();
   }

   public int getMinBuildHeight() {
      return this.level.getMinBuildHeight();
   }

   public int getHeight() {
      return this.level.getHeight();
   }

   public long nextSubTickCount() {
      return this.subTickCount.getAndIncrement();
   }
}
