package net.minecraft.server.level;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.TickList;
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
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenRegion implements WorldGenLevel {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<ChunkAccess> cache;
   private final int x;
   private final int z;
   private final int size;
   private final ServerLevel level;
   private final long seed;
   private final LevelData levelData;
   private final Random random;
   private final DimensionType dimensionType;
   private final TickList<Block> blockTicks = new WorldGenTickList((var1x) -> {
      return this.getChunk(var1x).getBlockTicks();
   });
   private final TickList<Fluid> liquidTicks = new WorldGenTickList((var1x) -> {
      return this.getChunk(var1x).getLiquidTicks();
   });
   private final BiomeManager biomeManager;
   private final ChunkPos firstPos;
   private final ChunkPos lastPos;
   private final StructureFeatureManager structureFeatureManager;

   public WorldGenRegion(ServerLevel var1, List<ChunkAccess> var2) {
      super();
      int var3 = Mth.floor(Math.sqrt((double)var2.size()));
      if (var3 * var3 != var2.size()) {
         throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Cache size is not a square."));
      } else {
         ChunkPos var4 = ((ChunkAccess)var2.get(var2.size() / 2)).getPos();
         this.cache = var2;
         this.x = var4.x;
         this.z = var4.z;
         this.size = var3;
         this.level = var1;
         this.seed = var1.getSeed();
         this.levelData = var1.getLevelData();
         this.random = var1.getRandom();
         this.dimensionType = var1.dimensionType();
         this.biomeManager = new BiomeManager(this, BiomeManager.obfuscateSeed(this.seed), var1.dimensionType().getBiomeZoomer());
         this.firstPos = ((ChunkAccess)var2.get(0)).getPos();
         this.lastPos = ((ChunkAccess)var2.get(var2.size() - 1)).getPos();
         this.structureFeatureManager = var1.structureFeatureManager().forWorldGenRegion(this);
      }
   }

   public int getCenterX() {
      return this.x;
   }

   public int getCenterZ() {
      return this.z;
   }

   public ChunkAccess getChunk(int var1, int var2) {
      return this.getChunk(var1, var2, ChunkStatus.EMPTY);
   }

   @Nullable
   public ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4) {
      ChunkAccess var5;
      if (this.hasChunk(var1, var2)) {
         int var6 = var1 - this.firstPos.x;
         int var7 = var2 - this.firstPos.z;
         var5 = (ChunkAccess)this.cache.get(var6 + var7 * this.size);
         if (var5.getStatus().isOrAfter(var3)) {
            return var5;
         }
      } else {
         var5 = null;
      }

      if (!var4) {
         return null;
      } else {
         LOGGER.error("Requested chunk : {} {}", var1, var2);
         LOGGER.error("Region bounds : {} {} | {} {}", this.firstPos.x, this.firstPos.z, this.lastPos.x, this.lastPos.z);
         if (var5 != null) {
            throw (RuntimeException)Util.pauseInIde(new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", var3, var5.getStatus(), var1, var2)));
         } else {
            throw (RuntimeException)Util.pauseInIde(new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", var1, var2)));
         }
      }
   }

   public boolean hasChunk(int var1, int var2) {
      return var1 >= this.firstPos.x && var1 <= this.lastPos.x && var2 >= this.firstPos.z && var2 <= this.lastPos.z;
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

   public Biome getUncachedNoiseBiome(int var1, int var2, int var3) {
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
               var3 = BlockEntity.loadStatic(var1, var5, var4);
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

   public boolean setBlock(BlockPos var1, BlockState var2, int var3, int var4) {
      ChunkAccess var5 = this.getChunk(var1);
      BlockState var6 = var5.setBlockState(var1, var2, false);
      if (var6 != null) {
         this.level.onBlockStateChange(var1, var6, var2);
      }

      if (var2.hasBlockEntity()) {
         if (var5.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
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

   @Deprecated
   public ServerLevel getLevel() {
      return this.level;
   }

   public RegistryAccess registryAccess() {
      return this.level.registryAccess();
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

   public ChunkSource getChunkSource() {
      return this.level.getChunkSource();
   }

   public long getSeed() {
      return this.seed;
   }

   public TickList<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public TickList<Fluid> getLiquidTicks() {
      return this.liquidTicks;
   }

   public int getSeaLevel() {
      return this.level.getSeaLevel();
   }

   public Random getRandom() {
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

   public DimensionType dimensionType() {
      return this.dimensionType;
   }

   public boolean isStateAtPosition(BlockPos var1, Predicate<BlockState> var2) {
      return var2.test(this.getBlockState(var1));
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

   public Stream<? extends StructureStart<?>> startsForFeature(SectionPos var1, StructureFeature<?> var2) {
      return this.structureFeatureManager.startsForFeature(var1, var2);
   }

   public int getSectionsCount() {
      return this.level.getSectionsCount();
   }

   public int getMinSection() {
      return this.level.getMinSection();
   }
}
