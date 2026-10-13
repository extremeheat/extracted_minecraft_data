package net.minecraft.world.item;

import java.util.function.BooleanSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class JukeboxSongPlayer {
   public static final int PLAY_EVENT_INTERVAL_TICKS = 20;
   private long ticksSinceSongStarted;
   private @Nullable Holder<JukeboxSong> song;
   private final BlockPos blockPos;
   private final OnSongChanged onSongChanged;
   private final BooleanSupplier shouldEmitParticlesHigher;

   public JukeboxSongPlayer(final OnSongChanged onSongChanged, final BlockPos blockPos, final BooleanSupplier shouldEmitParticlesHigher) {
      super();
      this.onSongChanged = onSongChanged;
      this.blockPos = blockPos;
      this.shouldEmitParticlesHigher = shouldEmitParticlesHigher;
   }

   public boolean isPlaying() {
      return this.song != null;
   }

   public @Nullable JukeboxSong getSong() {
      return this.song == null ? null : (JukeboxSong)this.song.value();
   }

   public long getTicksSinceSongStarted() {
      return this.ticksSinceSongStarted;
   }

   public void setSongWithoutPlaying(final Holder<JukeboxSong> song, final long ticksSinceSongStarted) {
      if (!((JukeboxSong)song.value()).hasFinished(ticksSinceSongStarted)) {
         this.song = song;
         this.ticksSinceSongStarted = ticksSinceSongStarted;
      }
   }

   public void play(final LevelAccessor level, final Holder<JukeboxSong> song) {
      this.song = song;
      this.ticksSinceSongStarted = 0L;
      int songId = level.registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG).getId(this.song.value());
      level.levelEvent((Entity)null, 1010, this.blockPos, songId);
      this.onSongChanged.notifyChange();
   }

   public void stop(final LevelAccessor level, final @Nullable BlockState blockState) {
      if (this.song != null) {
         this.song = null;
         this.ticksSinceSongStarted = 0L;
         level.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, (BlockPos)this.blockPos, (GameEvent.Context)GameEvent.Context.of(blockState));
         level.levelEvent(1011, this.blockPos, 0);
         this.onSongChanged.notifyChange();
      }
   }

   public void tick(final LevelAccessor level, final @Nullable BlockState blockState) {
      if (this.song != null) {
         if (((JukeboxSong)this.song.value()).hasFinished(this.ticksSinceSongStarted)) {
            this.stop(level, blockState);
         } else {
            if (this.shouldEmitJukeboxPlayingEvent()) {
               level.gameEvent(GameEvent.JUKEBOX_PLAY, (BlockPos)this.blockPos, (GameEvent.Context)GameEvent.Context.of(blockState));
               this.spawnMusicParticles(level);
            }

            ++this.ticksSinceSongStarted;
         }
      }
   }

   private boolean shouldEmitJukeboxPlayingEvent() {
      return this.ticksSinceSongStarted % 20L == 0L;
   }

   private void spawnMusicParticles(final LevelAccessor level) {
      if (level instanceof ServerLevel serverLevel) {
         float y = 1.2F + (this.shouldEmitParticlesHigher.getAsBoolean() ? 1.0F : 0.0F);
         Vec3 pos = Vec3.atBottomCenterOf(this.blockPos).add(0.0, (double)y, 0.0);
         float randomColor = (float)level.getRandom().nextInt(4) / 24.0F;
         serverLevel.sendParticles(ParticleTypes.NOTE, pos.x(), pos.y(), pos.z(), 0, (double)randomColor, 0.0, 0.0, 1.0);
      }

   }

   @FunctionalInterface
   public interface OnSongChanged {
      void notifyChange();
   }
}
