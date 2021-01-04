package net.minecraft.world.level;

import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface LevelAccessor extends EntityGetter, LevelReader, LevelSimulatedRW {
   long getSeed();

   default float getMoonBrightness() {
      return Dimension.MOON_BRIGHTNESS_PER_PHASE[this.getDimension().getMoonPhase(this.getLevelData().getDayTime())];
   }

   default float getTimeOfDay(float var1) {
      return this.getDimension().getTimeOfDay(this.getLevelData().getDayTime(), var1);
   }

   default int getMoonPhase() {
      return this.getDimension().getMoonPhase(this.getLevelData().getDayTime());
   }

   TickList<Block> getBlockTicks();

   TickList<Fluid> getLiquidTicks();

   Level getLevel();

   LevelData getLevelData();

   DifficultyInstance getCurrentDifficultyAt(BlockPos var1);

   default Difficulty getDifficulty() {
      return this.getLevelData().getDifficulty();
   }

   ChunkSource getChunkSource();

   default boolean hasChunk(int var1, int var2) {
      return this.getChunkSource().hasChunk(var1, var2);
   }

   Random getRandom();

   void blockUpdated(BlockPos var1, Block var2);

   BlockPos getSharedSpawnPos();

   void playSound(@Nullable Player var1, BlockPos var2, SoundEvent var3, SoundSource var4, float var5, float var6);

   void addParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12);

   void levelEvent(@Nullable Player var1, int var2, BlockPos var3, int var4);

   default void levelEvent(int var1, BlockPos var2, int var3) {
      this.levelEvent((Player)null, var1, var2, var3);
   }

   default Stream<VoxelShape> getEntityCollisions(@Nullable Entity var1, AABB var2, Set<Entity> var3) {
      return EntityGetter.super.getEntityCollisions(var1, var2, var3);
   }

   default boolean isUnobstructed(@Nullable Entity var1, VoxelShape var2) {
      return EntityGetter.super.isUnobstructed(var1, var2);
   }
}
