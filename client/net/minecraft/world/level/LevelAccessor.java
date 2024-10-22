package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;

public interface LevelAccessor extends CommonLevelAccessor, LevelTimeAccess, ScheduledTickAccess {
   @Override
   default long dayTime() {
      return this.getLevelData().getDayTime();
   }

   long nextSubTickCount();

   @Override
   default <T> ScheduledTick<T> createTick(BlockPos var1, T var2, int var3, TickPriority var4) {
      return new ScheduledTick<>((T)var2, var1, this.getLevelData().getGameTime() + (long)var3, var4, this.nextSubTickCount());
   }

   @Override
   default <T> ScheduledTick<T> createTick(BlockPos var1, T var2, int var3) {
      return new ScheduledTick<>((T)var2, var1, this.getLevelData().getGameTime() + (long)var3, this.nextSubTickCount());
   }

   LevelData getLevelData();

   DifficultyInstance getCurrentDifficultyAt(BlockPos var1);

   @Nullable
   MinecraftServer getServer();

   default Difficulty getDifficulty() {
      return this.getLevelData().getDifficulty();
   }

   ChunkSource getChunkSource();

   @Override
   default boolean hasChunk(int var1, int var2) {
      return this.getChunkSource().hasChunk(var1, var2);
   }

   RandomSource getRandom();

   default void blockUpdated(BlockPos var1, Block var2) {
   }

   default void neighborShapeChanged(Direction var1, BlockPos var2, BlockPos var3, BlockState var4, int var5, int var6) {
      NeighborUpdater.executeShapeUpdate(this, var1, var2, var3, var4, var5, var6 - 1);
   }

   default void playSound(@Nullable Player var1, BlockPos var2, SoundEvent var3, SoundSource var4) {
      this.playSound(var1, var2, var3, var4, 1.0F, 1.0F);
   }

   void playSound(@Nullable Player var1, BlockPos var2, SoundEvent var3, SoundSource var4, float var5, float var6);

   void addParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12);

   void levelEvent(@Nullable Player var1, int var2, BlockPos var3, int var4);

   default void levelEvent(int var1, BlockPos var2, int var3) {
      this.levelEvent(null, var1, var2, var3);
   }

   void gameEvent(Holder<GameEvent> var1, Vec3 var2, GameEvent.Context var3);

   default void gameEvent(@Nullable Entity var1, Holder<GameEvent> var2, Vec3 var3) {
      this.gameEvent(var2, var3, new GameEvent.Context(var1, null));
   }

   default void gameEvent(@Nullable Entity var1, Holder<GameEvent> var2, BlockPos var3) {
      this.gameEvent(var2, var3, new GameEvent.Context(var1, null));
   }

   default void gameEvent(Holder<GameEvent> var1, BlockPos var2, GameEvent.Context var3) {
      this.gameEvent(var1, Vec3.atCenterOf(var2), var3);
   }

   default void gameEvent(ResourceKey<GameEvent> var1, BlockPos var2, GameEvent.Context var3) {
      this.gameEvent(this.registryAccess().lookupOrThrow(Registries.GAME_EVENT).getOrThrow(var1), var2, var3);
   }
}
