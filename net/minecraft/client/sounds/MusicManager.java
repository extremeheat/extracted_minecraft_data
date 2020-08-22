package net.minecraft.client.sounds;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public class MusicManager {
   private final Random random = new Random();
   private final Minecraft minecraft;
   private SoundInstance currentMusic;
   private int nextSongDelay = 100;

   public MusicManager(Minecraft var1) {
      this.minecraft = var1;
   }

   public void tick() {
      MusicManager.Music var1 = this.minecraft.getSituationalMusic();
      if (this.currentMusic != null) {
         if (!var1.getEvent().getLocation().equals(this.currentMusic.getLocation())) {
            this.minecraft.getSoundManager().stop(this.currentMusic);
            this.nextSongDelay = Mth.nextInt(this.random, 0, var1.getMinDelay() / 2);
         }

         if (!this.minecraft.getSoundManager().isActive(this.currentMusic)) {
            this.currentMusic = null;
            this.nextSongDelay = Math.min(Mth.nextInt(this.random, var1.getMinDelay(), var1.getMaxDelay()), this.nextSongDelay);
         }
      }

      this.nextSongDelay = Math.min(this.nextSongDelay, var1.getMaxDelay());
      if (this.currentMusic == null && this.nextSongDelay-- <= 0) {
         this.startPlaying(var1);
      }

   }

   public void startPlaying(MusicManager.Music var1) {
      this.currentMusic = SimpleSoundInstance.forMusic(var1.getEvent());
      this.minecraft.getSoundManager().play(this.currentMusic);
      this.nextSongDelay = Integer.MAX_VALUE;
   }

   public void stopPlaying() {
      if (this.currentMusic != null) {
         this.minecraft.getSoundManager().stop(this.currentMusic);
         this.currentMusic = null;
         this.nextSongDelay = 0;
      }

   }

   public boolean isPlayingMusic(MusicManager.Music var1) {
      return this.currentMusic == null ? false : var1.getEvent().getLocation().equals(this.currentMusic.getLocation());
   }

   public static enum Music {
      MENU(SoundEvents.MUSIC_MENU, 20, 600),
      GAME(SoundEvents.MUSIC_GAME, 12000, 24000),
      CREATIVE(SoundEvents.MUSIC_CREATIVE, 1200, 3600),
      CREDITS(SoundEvents.MUSIC_CREDITS, 0, 0),
      NETHER(SoundEvents.MUSIC_NETHER, 1200, 3600),
      END_BOSS(SoundEvents.MUSIC_DRAGON, 0, 0),
      END(SoundEvents.MUSIC_END, 6000, 24000),
      UNDER_WATER(SoundEvents.MUSIC_UNDER_WATER, 12000, 24000);

      private final SoundEvent event;
      private final int minDelay;
      private final int maxDelay;

      private Music(SoundEvent var3, int var4, int var5) {
         this.event = var3;
         this.minDelay = var4;
         this.maxDelay = var5;
      }

      public SoundEvent getEvent() {
         return this.event;
      }

      public int getMinDelay() {
         return this.minDelay;
      }

      public int getMaxDelay() {
         return this.maxDelay;
      }
   }
}
