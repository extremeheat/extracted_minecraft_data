package net.minecraft.world.level;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;

public interface LevelAccessor extends CommonLevelAccessor, LevelTimeAccess {
   default long dayTime() {
      return this.getLevelData().getDayTime();
   }

   long nextSubTickCount();

   LevelTickAccess<Block> getBlockTicks();

   private default <T> ScheduledTick<T> createTick(BlockPos var1, T var2, int var3, TickPriority var4) {
      return new ScheduledTick(var2, var1, this.getLevelData().getGameTime() + (long)var3, var4, this.nextSubTickCount());
   }

   private default <T> ScheduledTick<T> createTick(BlockPos var1, T var2, int var3) {
      return new ScheduledTick(var2, var1, this.getLevelData().getGameTime() + (long)var3, this.nextSubTickCount());
   }

   default void scheduleTick(BlockPos var1, Block var2, int var3, TickPriority var4) {
      this.getBlockTicks().schedule(this.createTick(var1, var2, var3, var4));
   }

   default void scheduleTick(BlockPos var1, Block var2, int var3) {
      this.getBlockTicks().schedule(this.createTick(var1, var2, var3));
   }

   LevelTickAccess<Fluid> getFluidTicks();

   default void scheduleTick(BlockPos var1, Fluid var2, int var3, TickPriority var4) {
      this.getFluidTicks().schedule(this.createTick(var1, var2, var3, var4));
   }

   default void scheduleTick(BlockPos var1, Fluid var2, int var3) {
      this.getFluidTicks().schedule(this.createTick(var1, var2, var3));
   }

   LevelData getLevelData();

   DifficultyInstance getCurrentDifficultyAt(BlockPos var1);

   @Nullable
   MinecraftServer getServer();

   default Difficulty getDifficulty() {
      return this.getLevelData().getDifficulty();
   }

   ChunkSource getChunkSource();

   default boolean hasChunk(int var1, int var2) {
      return this.getChunkSource().hasChunk(var1, var2);
   }

   Random getRandom();

   default void blockUpdated(BlockPos var1, Block var2) {
   }

   void playSound(@Nullable Player var1, BlockPos var2, SoundEvent var3, SoundSource var4, float var5, float var6);

   void addParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12);

   void levelEvent(@Nullable Player var1, int var2, BlockPos var3, int var4);

   default void levelEvent(int var1, BlockPos var2, int var3) {
      this.levelEvent((Player)null, var1, var2, var3);
   }

   void gameEvent(@Nullable Entity var1, GameEvent var2, BlockPos var3);

   default void gameEvent(GameEvent var1, BlockPos var2) {
      this.gameEvent((Entity)null, var1, (BlockPos)var2);
   }

   default void gameEvent(GameEvent var1, Entity var2) {
      this.gameEvent((Entity)null, var1, (BlockPos)var2.blockPosition());
   }

   default void gameEvent(@Nullable Entity var1, GameEvent var2, Entity var3) {
      this.gameEvent(var1, var2, var3.blockPosition());
   }
}
