package net.minecraft.server.level;

import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StaticCache2D;
import net.minecraft.util.Util;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.attribute.EnvironmentAttributeReader;
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
import org.jspecify.annotations.Nullable;
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
   private final WorldGenTickAccess<Block> blockTicks = new WorldGenTickAccess<Block>((pos) -> this.getChunk(pos).getBlockTicks());
   private final WorldGenTickAccess<Fluid> fluidTicks = new WorldGenTickAccess<Fluid>((pos) -> this.getChunk(pos).getFluidTicks());
   private final BiomeManager biomeManager;
   private final ChunkStep generatingStep;
   private @Nullable Supplier<String> currentlyGenerating;
   private final AtomicLong subTickCount = new AtomicLong();
   private static final Identifier WORLDGEN_REGION_RANDOM = Identifier.withDefaultNamespace("worldgen_region_random");

   public WorldGenRegion(final ServerLevel level, final StaticCache2D<GenerationChunkHolder> cache, final ChunkStep generatingStep, final ChunkAccess center) {
      super();
      this.generatingStep = generatingStep;
      this.cache = cache;
      this.center = center;
      this.level = level;
      this.seed = level.getSeed();
      this.levelData = level.getLevelData();
      this.random = level.getChunkSource().randomState().getOrCreateRandomFactory(WORLDGEN_REGION_RANDOM).at(this.center.getPos().getWorldPosition());
      this.dimensionType = level.dimensionType();
      this.biomeManager = new BiomeManager(this, BiomeManager.obfuscateSeed(this.seed));
   }

   public boolean isOldChunkAround(final ChunkPos pos, final int range) {
      return this.level.getChunkSource().chunkMap.isOldChunkAround(pos, range);
   }

   public ChunkPos getCenter() {
      return this.center.getPos();
   }

   public void setCurrentlyGenerating(final @Nullable Supplier<String> currentlyGenerating) {
      this.currentlyGenerating = currentlyGenerating;
   }

   public ChunkAccess getChunk(final int chunkX, final int chunkZ) {
      return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY);
   }

   public @Nullable ChunkAccess getChunk(final int chunkX, final int chunkZ, final ChunkStatus targetStatus, final boolean loadOrGenerate) {
      int distance = this.center.getPos().getChessboardDistance(chunkX, chunkZ);
      ChunkStatus maxAllowedStatus = distance >= this.generatingStep.directDependencies().size() ? null : this.generatingStep.directDependencies().get(distance);
      GenerationChunkHolder chunkHolder;
      if (maxAllowedStatus != null) {
         chunkHolder = this.cache.get(chunkX, chunkZ);
         if (targetStatus.isOrBefore(maxAllowedStatus)) {
            ChunkAccess chunk = chunkHolder.getChunkIfPresentUnchecked(maxAllowedStatus);
            if (chunk != null) {
               return chunk;
            }
         }
      } else {
         chunkHolder = null;
      }

      CrashReport report = CrashReport.forThrowable(new IllegalStateException("Requested chunk unavailable during world generation"), "Exception generating new chunk");
      CrashReportCategory category = report.addCategory("Chunk request details");
      category.setDetail("Requested chunk", String.format(Locale.ROOT, "%d, %d", chunkX, chunkZ));
      category.setDetail("Generating status", (CrashReportDetail)(() -> this.generatingStep.targetStatus().getName()));
      Objects.requireNonNull(targetStatus);
      category.setDetail("Requested status", targetStatus::getName);
      category.setDetail("Actual status", (CrashReportDetail)(() -> chunkHolder == null ? "[out of cache bounds]" : chunkHolder.getPersistedStatus().getName()));
      category.setDetail("Maximum allowed status", (CrashReportDetail)(() -> maxAllowedStatus == null ? "null" : maxAllowedStatus.getName()));
      ChunkDependencies var10002 = this.generatingStep.directDependencies();
      Objects.requireNonNull(var10002);
      category.setDetail("Dependencies", var10002::toString);
      category.setDetail("Requested distance", distance);
      ChunkPos var11 = this.center.getPos();
      Objects.requireNonNull(var11);
      category.setDetail("Generating chunk", var11::toString);
      throw new ReportedException(report);
   }

   public boolean hasChunk(final int chunkX, final int chunkZ) {
      int distance = this.center.getPos().getChessboardDistance(chunkX, chunkZ);
      return distance < this.generatingStep.directDependencies().size();
   }

   public BlockState getBlockState(final BlockPos pos) {
      return this.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())).getBlockState(pos);
   }

   public FluidState getFluidState(final BlockPos pos) {
      return this.getChunk(pos).getFluidState(pos);
   }

   public @Nullable Player getNearestPlayer(final double x, final double y, final double z, final double maxDist, final @Nullable Predicate<Entity> predicate) {
      return null;
   }

   public int getSkyDarken() {
      return 0;
   }

   public BiomeManager getBiomeManager() {
      return this.biomeManager;
   }

   public Holder<Biome> getUncachedNoiseBiome(final int quartX, final int quartY, final int quartZ) {
      return this.level.getUncachedNoiseBiome(quartX, quartY, quartZ);
   }

   public float getShade(final Direction direction, final boolean shade) {
      return 1.0F;
   }

   public LevelLightEngine getLightEngine() {
      return this.level.getLightEngine();
   }

   public boolean destroyBlock(final BlockPos pos, final boolean dropResources, final @Nullable Entity breaker, final int updateLimit) {
      BlockState blockState = this.getBlockState(pos);
      if (blockState.isAir()) {
         return false;
      } else {
         if (dropResources) {
            BlockEntity blockEntity = blockState.hasBlockEntity() ? this.getBlockEntity(pos) : null;
            Block.dropResources(blockState, this.level, pos, blockEntity, breaker, ItemStack.EMPTY);
         }

         return this.setBlock(pos, Blocks.AIR.defaultBlockState(), 3, updateLimit);
      }
   }

   public @Nullable BlockEntity getBlockEntity(final BlockPos pos) {
      ChunkAccess chunk = this.getChunk(pos);
      BlockEntity blockEntity = chunk.getBlockEntity(pos);
      if (blockEntity != null) {
         return blockEntity;
      } else {
         CompoundTag tag = chunk.getBlockEntityNbt(pos);
         BlockState state = chunk.getBlockState(pos);
         if (tag != null) {
            if ("DUMMY".equals(tag.getStringOr("id", ""))) {
               if (!state.hasBlockEntity()) {
                  return null;
               }

               blockEntity = ((EntityBlock)state.getBlock()).newBlockEntity(pos, state);
            } else {
               blockEntity = BlockEntity.loadStatic(pos, state, tag, this.level.registryAccess());
            }

            if (blockEntity != null) {
               chunk.setBlockEntity(blockEntity);
               return blockEntity;
            }
         }

         if (state.hasBlockEntity()) {
            LOGGER.warn("Tried to access a block entity before it was created. {}", pos);
         }

         return null;
      }
   }

   public boolean ensureCanWrite(final BlockPos pos) {
      int chunkX = SectionPos.blockToSectionCoord(pos.getX());
      int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
      ChunkPos centerPos = this.getCenter();
      int distanceX = Math.abs(centerPos.x() - chunkX);
      int distanceZ = Math.abs(centerPos.z() - chunkZ);
      if (distanceX <= this.generatingStep.blockStateWriteRadius() && distanceZ <= this.generatingStep.blockStateWriteRadius()) {
         if (this.center.isUpgrading()) {
            LevelHeightAccessor levelHeightAccessor = this.center.getHeightAccessorForGeneration();
            if (levelHeightAccessor.isOutsideBuildHeight(pos.getY())) {
               return false;
            }
         }

         return true;
      } else {
         Util.logAndPauseIfInIde("Detected setBlock in a far chunk [" + chunkX + ", " + chunkZ + "], pos: " + String.valueOf(pos) + ", status: " + String.valueOf(this.generatingStep.targetStatus()) + (this.currentlyGenerating == null ? "" : ", currently generating: " + (String)this.currentlyGenerating.get()));
         return false;
      }
   }

   public boolean setBlock(final BlockPos pos, final BlockState blockState, final @Block.UpdateFlags int updateFlags, final int updateLimit) {
      if (!this.ensureCanWrite(pos)) {
         return false;
      } else {
         ChunkAccess chunk = this.getChunk(pos);
         BlockState oldState = chunk.setBlockState(pos, blockState, updateFlags);
         if (oldState != null) {
            this.level.updatePOIOnBlockStateChange(pos, oldState, blockState);
         }

         if (blockState.hasBlockEntity()) {
            if (chunk.getPersistedStatus().getChunkType() == ChunkType.LEVELCHUNK) {
               BlockEntity blockEntity = ((EntityBlock)blockState.getBlock()).newBlockEntity(pos, blockState);
               if (blockEntity != null) {
                  chunk.setBlockEntity(blockEntity);
               } else {
                  chunk.removeBlockEntity(pos);
               }
            } else {
               CompoundTag tag = new CompoundTag();
               tag.putInt("x", pos.getX());
               tag.putInt("y", pos.getY());
               tag.putInt("z", pos.getZ());
               tag.putString("id", "DUMMY");
               chunk.setBlockEntityNbt(tag);
            }
         } else if (oldState != null && oldState.hasBlockEntity()) {
            chunk.removeBlockEntity(pos);
         }

         if (blockState.hasPostProcess(this, pos) && (updateFlags & 16) == 0) {
            this.markPosForPostprocessing(pos);
         }

         return true;
      }
   }

   private void markPosForPostprocessing(final BlockPos blockPos) {
      this.getChunk(blockPos).markPosForPostprocessing(blockPos);
   }

   public boolean addFreshEntity(final Entity entity) {
      int xc = SectionPos.blockToSectionCoord(entity.getBlockX());
      int zc = SectionPos.blockToSectionCoord(entity.getBlockZ());
      this.getChunk(xc, zc).addEntity(entity);
      return true;
   }

   public boolean removeBlock(final BlockPos pos, final boolean movedByPiston) {
      return this.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
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

   public DifficultyInstance getCurrentDifficultyAt(final BlockPos pos) {
      if (!this.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))) {
         throw new RuntimeException("We are asking a region for a chunk out of bound");
      } else {
         return new DifficultyInstance(this.level.getDifficulty(), this.level.getOverworldClockTime(), 0L, this.level.getMoonBrightness(pos));
      }
   }

   public @Nullable MinecraftServer getServer() {
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

   public int getHeight(final Heightmap.Types type, final int x, final int z) {
      return this.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z)).getHeight(type, x & 15, z & 15) + 1;
   }

   public void playSound(final @Nullable Entity except, final BlockPos pos, final SoundEvent sound, final SoundSource source, final float volume, final float pitch) {
   }

   public void addParticle(final ParticleOptions particle, final double x, final double y, final double z, final double xd, final double yd, final double zd) {
   }

   public void levelEvent(final @Nullable Entity source, final int type, final BlockPos pos, final int data) {
   }

   public void gameEvent(final Holder<GameEvent> gameEvent, final Vec3 position, final GameEvent.Context context) {
   }

   public DimensionType dimensionType() {
      return this.dimensionType;
   }

   public boolean isStateAtPosition(final BlockPos pos, final Predicate<BlockState> predicate) {
      return predicate.test(this.getBlockState(pos));
   }

   public boolean isFluidAtPosition(final BlockPos pos, final Predicate<FluidState> predicate) {
      return predicate.test(this.getFluidState(pos));
   }

   public <T extends Entity> List<T> getEntities(final EntityTypeTest<Entity, T> type, final AABB bb, final Predicate<? super T> selector) {
      return Collections.emptyList();
   }

   public List<Entity> getEntities(final @Nullable Entity except, final AABB bb, final @Nullable Predicate<? super Entity> selector) {
      return Collections.emptyList();
   }

   public List<Player> players() {
      return Collections.emptyList();
   }

   public int getMinY() {
      return this.level.getMinY();
   }

   public int getHeight() {
      return this.level.getHeight();
   }

   public long nextSubTickCount() {
      return this.subTickCount.getAndIncrement();
   }

   public EnvironmentAttributeReader environmentAttributes() {
      return EnvironmentAttributeReader.EMPTY;
   }
}
