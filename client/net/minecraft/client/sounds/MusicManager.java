package net.minecraft.client.sounds;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class MusicManager {
   private static final int STARTING_DELAY = 100;
   private final RandomSource random = RandomSource.create();
   private final Minecraft minecraft;
   @Nullable
   private SoundInstance currentMusic;
   private float currentGain = 1.0F;
   private int nextSongDelay = 100;

   public MusicManager(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void tick() {
      MusicInfo var1 = this.minecraft.getSituationalMusic();
      float var2 = var1.volume();
      if (this.currentMusic != null && this.currentGain != var2) {
         boolean var3 = this.fadePlaying(var2);
         if (!var3) {
            return;
         }
      }

      Music var4 = var1.music();
      if (var4 == null) {
         this.nextSongDelay = Math.max(this.nextSongDelay, 100);
      } else {
         if (this.currentMusic != null) {
            if (var1.canReplace(this.currentMusic)) {
               this.minecraft.getSoundManager().stop(this.currentMusic);
               this.nextSongDelay = Mth.nextInt(this.random, 0, var4.getMinDelay() / 2);
            }

            if (!this.minecraft.getSoundManager().isActive(this.currentMusic)) {
               this.currentMusic = null;
               this.nextSongDelay = Math.min(this.nextSongDelay, Mth.nextInt(this.random, var4.getMinDelay(), var4.getMaxDelay()));
            }
         }

         this.nextSongDelay = Math.min(this.nextSongDelay, var4.getMaxDelay());
         if (this.currentMusic == null && this.nextSongDelay-- <= 0) {
            this.startPlaying(var1);
         }

      }
   }

   public void startPlaying(MusicInfo var1) {
      this.currentMusic = SimpleSoundInstance.forMusic((SoundEvent)var1.music().getEvent().value());
      if (this.currentMusic.getSound() != SoundManager.EMPTY_SOUND) {
         this.minecraft.getSoundManager().play(this.currentMusic);
         this.minecraft.getSoundManager().setVolume(this.currentMusic, var1.volume());
      }

      this.nextSongDelay = 2147483647;
      this.currentGain = var1.volume();
   }

   public void stopPlaying(Music var1) {
      if (this.isPlayingMusic(var1)) {
         this.stopPlaying();
      }

   }

   public void stopPlaying() {
      if (this.currentMusic != null) {
         this.minecraft.getSoundManager().stop(this.currentMusic);
         this.currentMusic = null;
      }

      this.nextSongDelay += 100;
   }

   private boolean fadePlaying(float var1) {
      if (this.currentMusic == null) {
         return false;
      } else if (this.currentGain == var1) {
         return true;
      } else {
         if (this.currentGain < var1) {
            this.currentGain += Mth.clamp(this.currentGain, 5.0E-4F, 0.005F);
            if (this.currentGain > var1) {
               this.currentGain = var1;
            }
         } else {
            this.currentGain = 0.03F * var1 + 0.97F * this.currentGain;
            if (Math.abs(this.currentGain - var1) < 1.0E-4F || this.currentGain < var1) {
               this.currentGain = var1;
            }
         }

         this.currentGain = Mth.clamp(this.currentGain, 0.0F, 1.0F);
         if (this.currentGain <= 1.0E-4F) {
            this.stopPlaying();
            return false;
         } else {
            this.minecraft.getSoundManager().setVolume(this.currentMusic, this.currentGain);
            return true;
         }
      }
   }

   public boolean isPlayingMusic(Music var1) {
      return this.currentMusic == null ? false : ((SoundEvent)var1.getEvent().value()).location().equals(this.currentMusic.getLocation());
   }
}
