package net.minecraft.server.level;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenRegion implements LevelAccessor {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<ChunkAccess> cache;
   private final int x;
   private final int z;
   private final int size;
   private final ServerLevel level;
   private final long seed;
   private final int seaLevel;
   private final LevelData levelData;
   private final Random random;
   private final Dimension dimension;
   private final ChunkGeneratorSettings settings;
   private final TickList<Block> blockTicks = new WorldGenTickList((var1x) -> {
      return this.getChunk(var1x).getBlockTicks();
   });
   private final TickList<Fluid> liquidTicks = new WorldGenTickList((var1x) -> {
      return this.getChunk(var1x).getLiquidTicks();
   });

   public WorldGenRegion(ServerLevel var1, List<ChunkAccess> var2) {
      super();
      int var3 = Mth.floor(Math.sqrt((double)var2.size()));
      if (var3 * var3 != var2.size()) {
         throw new IllegalStateException("Cache size is not a square.");
      } else {
         ChunkPos var4 = ((ChunkAccess)var2.get(var2.size() / 2)).getPos();
         this.cache = var2;
         this.x = var4.x;
         this.z = var4.z;
         this.size = var3;
         this.level = var1;
         this.seed = var1.getSeed();
         this.settings = var1.getChunkSource().getGenerator().getSettings();
         this.seaLevel = var1.getSeaLevel();
         this.levelData = var1.getLevelData();
         this.random = var1.getRandom();
         this.dimension = var1.getDimension();
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
         ChunkPos var6 = ((ChunkAccess)this.cache.get(0)).getPos();
         int var7 = var1 - var6.x;
         int var8 = var2 - var6.z;
         var5 = (ChunkAccess)this.cache.get(var7 + var8 * this.size);
         if (var5.getStatus().isOrAfter(var3)) {
            return var5;
         }
      } else {
         var5 = null;
      }

      if (!var4) {
         return null;
      } else {
         ChunkAccess var9 = (ChunkAccess)this.cache.get(0);
         ChunkAccess var10 = (ChunkAccess)this.cache.get(this.cache.size() - 1);
         LOGGER.error("Requested chunk : {} {}", var1, var2);
         LOGGER.error("Region bounds : {} {} | {} {}", var9.getPos().x, var9.getPos().z, var10.getPos().x, var10.getPos().z);
         if (var5 != null) {
            throw new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", var3, var5.getStatus(), var1, var2));
         } else {
            throw new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", var1, var2));
         }
      }
   }

   public boolean hasChunk(int var1, int var2) {
      ChunkAccess var3 = (ChunkAccess)this.cache.get(0);
      ChunkAccess var4 = (ChunkAccess)this.cache.get(this.cache.size() - 1);
      return var1 >= var3.getPos().x && var1 <= var4.getPos().x && var2 >= var3.getPos().z && var2 <= var4.getPos().z;
   }

   public BlockState getBlockState(BlockPos var1) {
      return this.getChunk(var1.getX() >> 4, var1.getZ() >> 4).getBlockState(var1);
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

   public Biome getBiome(BlockPos var1) {
      Biome var2 = this.getChunk(var1).getBiomes()[var1.getX() & 15 | (var1.getZ() & 15) << 4];
      if (var2 == null) {
         throw new RuntimeException(String.format("Biome is null @ %s", var1));
      } else {
         return var2;
      }
   }

   public int getBrightness(LightLayer var1, BlockPos var2) {
      return this.getChunkSource().getLightEngine().getLayerListener(var1).getLightValue(var2);
   }

   public int getRawBrightness(BlockPos var1, int var2) {
      return this.getChunk(var1).getRawBrightness(var1, var2, this.getDimension().isHasSkyLight());
   }

   public boolean destroyBlock(BlockPos var1, boolean var2) {
      BlockState var3 = this.getBlockState(var1);
      if (var3.isAir()) {
         return false;
      } else {
         if (var2) {
            BlockEntity var4 = var3.getBlock().isEntityBlock() ? this.getBlockEntity(var1) : null;
            Block.dropResources(var3, this.level, var1, var4);
         }

         return this.setBlock(var1, Blocks.AIR.defaultBlockState(), 3);
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
         if (var4 != null) {
            if ("DUMMY".equals(var4.getString("id"))) {
               Block var5 = this.getBlockState(var1).getBlock();
               if (!(var5 instanceof EntityBlock)) {
                  return null;
               }

               var3 = ((EntityBlock)var5).newBlockEntity(this.level);
            } else {
               var3 = BlockEntity.loadStatic(var4);
            }

            if (var3 != null) {
               var2.setBlockEntity(var1, var3);
               return var3;
            }
         }

         if (var2.getBlockState(var1).getBlock() instanceof EntityBlock) {
            LOGGER.warn("Tried to access a block entity before it was created. {}", var1);
         }

         return null;
      }
   }

   public boolean setBlock(BlockPos var1, BlockState var2, int var3) {
      ChunkAccess var4 = this.getChunk(var1);
      BlockState var5 = var4.setBlockState(var1, var2, false);
      if (var5 != null) {
         this.level.onBlockStateChange(var1, var5, var2);
      }

      Block var6 = var2.getBlock();
      if (var6.isEntityBlock()) {
         if (var4.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
            var4.setBlockEntity(var1, ((EntityBlock)var6).newBlockEntity(this));
         } else {
            CompoundTag var7 = new CompoundTag();
            var7.putInt("x", var1.getX());
            var7.putInt("y", var1.getY());
            var7.putInt("z", var1.getZ());
            var7.putString("id", "DUMMY");
            var4.setBlockEntityNbt(var7);
         }
      } else if (var5 != null && var5.getBlock().isEntityBlock()) {
         var4.removeBlockEntity(var1);
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
      int var2 = Mth.floor(var1.x / 16.0D);
      int var3 = Mth.floor(var1.z / 16.0D);
      this.getChunk(var2, var3).addEntity(var1);
      return true;
   }

   public boolean removeBlock(BlockPos var1, boolean var2) {
      return this.setBlock(var1, Blocks.AIR.defaultBlockState(), 3);
   }

   public WorldBorder getWorldBorder() {
      return this.level.getWorldBorder();
   }

   public boolean isUnobstructed(@Nullable Entity var1, VoxelShape var2) {
      return true;
   }

   public boolean isClientSide() {
      return false;
   }

   @Deprecated
   public ServerLevel getLevel() {
      return this.level;
   }

   public LevelData getLevelData() {
      return this.levelData;
   }

   public DifficultyInstance getCurrentDifficultyAt(BlockPos var1) {
      if (!this.hasChunk(var1.getX() >> 4, var1.getZ() >> 4)) {
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
      return this.seaLevel;
   }

   public Random getRandom() {
      return this.random;
   }

   public void blockUpdated(BlockPos var1, Block var2) {
   }

   public int getHeight(Heightmap.Types var1, int var2, int var3) {
      return this.getChunk(var2 >> 4, var3 >> 4).getHeight(var1, var2 & 15, var3 & 15) + 1;
   }

   public void playSound(@Nullable Player var1, BlockPos var2, SoundEvent var3, SoundSource var4, float var5, float var6) {
   }

   public void addParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12) {
   }

   public void levelEvent(@Nullable Player var1, int var2, BlockPos var3, int var4) {
   }

   public BlockPos getSharedSpawnPos() {
      return this.level.getSharedSpawnPos();
   }

   public Dimension getDimension() {
      return this.dimension;
   }

   public boolean isStateAtPosition(BlockPos var1, Predicate<BlockState> var2) {
      return var2.test(this.getBlockState(var1));
   }

   public <T extends Entity> List<T> getEntitiesOfClass(Class<? extends T> var1, AABB var2, @Nullable Predicate<? super T> var3) {
      return Collections.emptyList();
   }

   public List<Entity> getEntities(@Nullable Entity var1, AABB var2, @Nullable Predicate<? super Entity> var3) {
      return Collections.emptyList();
   }

   public List<Player> players() {
      return Collections.emptyList();
   }

   public BlockPos getHeightmapPos(Heightmap.Types var1, BlockPos var2) {
      return new BlockPos(var2.getX(), this.getHeight(var1, var2.getX(), var2.getZ()), var2.getZ());
   }

   // $FF: synthetic method
   @Deprecated
   public Level getLevel() {
      return this.getLevel();
   }
}
