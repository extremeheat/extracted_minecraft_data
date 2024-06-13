package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class JukeboxSongPlayer {
   public static final int PLAY_EVENT_INTERVAL_TICKS = 20;
   private long ticksSinceSongStarted;
   @Nullable
   private Holder<JukeboxSong> song;
   private final BlockPos blockPos;
   private final JukeboxSongPlayer.OnSongChanged onSongChanged;

   public JukeboxSongPlayer(JukeboxSongPlayer.OnSongChanged var1, BlockPos var2) {
      super();
      this.onSongChanged = var1;
      this.blockPos = var2;
   }

   public boolean isPlaying() {
      return this.song != null;
   }

   @Nullable
   public JukeboxSong getSong() {
      return this.song == null ? null : this.song.value();
   }

   public long getTicksSinceSongStarted() {
      return this.ticksSinceSongStarted;
   }

   public void setSongWithoutPlaying(Holder<JukeboxSong> var1, long var2) {
      if (!((JukeboxSong)var1.value()).hasFinished(var2)) {
         this.song = var1;
         this.ticksSinceSongStarted = var2;
      }
   }

   public int getComparatorOutput() {
      return this.song != null ? this.song.value().comparatorOutput() : 0;
   }

   public void play(LevelAccessor var1, Holder<JukeboxSong> var2) {
      this.song = var2;
      this.ticksSinceSongStarted = 0L;
      int var3 = var1.registryAccess().registryOrThrow(Registries.JUKEBOX_SONG).getId(this.song.value());
      var1.levelEvent(null, 1010, this.blockPos, var3);
      this.onSongChanged.notifyChange();
   }

   public void stop(LevelAccessor var1, @Nullable BlockState var2) {
      if (this.song != null) {
         this.song = null;
         this.ticksSinceSongStarted = 0L;
         var1.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.blockPos, GameEvent.Context.of(var2));
         var1.levelEvent(1011, this.blockPos, 0);
         this.onSongChanged.notifyChange();
      }
   }

   public void tick(LevelAccessor var1, @Nullable BlockState var2) {
      if (this.song != null) {
         if (this.song.value().hasFinished(this.ticksSinceSongStarted)) {
            this.stop(var1, var2);
         } else {
            if (this.shouldEmitJukeboxPlayingEvent()) {
               var1.gameEvent(GameEvent.JUKEBOX_PLAY, this.blockPos, GameEvent.Context.of(var2));
               spawnMusicParticles(var1, this.blockPos);
            }

            this.ticksSinceSongStarted++;
         }
      }
   }

   private boolean shouldEmitJukeboxPlayingEvent() {
      return this.ticksSinceSongStarted % 20L == 0L;
   }

   private static void spawnMusicParticles(LevelAccessor var0, BlockPos var1) {
      if (var0 instanceof ServerLevel var2) {
         Vec3 var3 = Vec3.atBottomCenterOf(var1).add(0.0, 1.2000000476837158, 0.0);
         float var4 = (float)var0.getRandom().nextInt(4) / 24.0F;
         var2.sendParticles(ParticleTypes.NOTE, var3.x(), var3.y(), var3.z(), 0, (double)var4, 0.0, 0.0, 1.0);
      }
   }

   @FunctionalInterface
   public interface OnSongChanged {
      void notifyChange();
   }
}
