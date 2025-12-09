package net.minecraft.client.sounds;

import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.Nullable;

public class MusicManager {
   private static final int STARTING_DELAY = 100;
   private final RandomSource random = RandomSource.create();
   private final Minecraft minecraft;
   private @Nullable SoundInstance currentMusic;
   private MusicFrequency gameMusicFrequency;
   private float currentGain = 1.0F;
   private int nextSongDelay = 100;
   private boolean toastShown = false;

   public MusicManager(Minecraft var1) {
      super();
      this.minecraft = var1;
      this.gameMusicFrequency = (MusicFrequency)var1.options.musicFrequency().get();
   }

   public void tick() {
      float var1 = this.minecraft.getMusicVolume();
      if (this.currentMusic != null && this.currentGain != var1) {
         boolean var2 = this.fadePlaying(var1);
         if (!var2) {
            return;
         }
      }

      Music var3 = this.minecraft.getSituationalMusic();
      if (var3 == null) {
         this.nextSongDelay = Math.max(this.nextSongDelay, 100);
      } else {
         if (this.currentMusic != null) {
            if (canReplace(var3, this.currentMusic)) {
               this.minecraft.getSoundManager().stop(this.currentMusic);
               this.nextSongDelay = Mth.nextInt(this.random, 0, var3.minDelay() / 2);
            }

            if (!this.minecraft.getSoundManager().isActive(this.currentMusic)) {
               this.currentMusic = null;
               this.nextSongDelay = Math.min(this.nextSongDelay, this.gameMusicFrequency.getNextSongDelay(var3, this.random));
            }
         }

         this.nextSongDelay = Math.min(this.nextSongDelay, this.gameMusicFrequency.getNextSongDelay(var3, this.random));
         if (this.currentMusic == null && this.nextSongDelay-- <= 0) {
            this.startPlaying(var3);
         }

      }
   }

   private static boolean canReplace(Music var0, SoundInstance var1) {
      return var0.replaceCurrentMusic() && !((SoundEvent)var0.sound().value()).location().equals(var1.getIdentifier());
   }

   public void startPlaying(Music var1) {
      SoundEvent var2 = (SoundEvent)var1.sound().value();
      this.currentMusic = SimpleSoundInstance.forMusic(var2);
      switch (this.minecraft.getSoundManager().play(this.currentMusic)) {
         case STARTED:
            this.minecraft.getToastManager().showNowPlayingToast();
            this.toastShown = true;
            break;
         case STARTED_SILENTLY:
            this.toastShown = false;
      }

      this.nextSongDelay = 2147483647;
   }

   public void showNowPlayingToastIfNeeded() {
      if (!this.toastShown) {
         this.minecraft.getToastManager().showNowPlayingToast();
         this.toastShown = true;
      }

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
         this.minecraft.getToastManager().hideNowPlayingToast();
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
            this.minecraft.getSoundManager().updateCategoryVolume(SoundSource.MUSIC, this.currentGain);
            return true;
         }
      }
   }

   public boolean isPlayingMusic(Music var1) {
      return this.currentMusic == null ? false : ((SoundEvent)var1.sound().value()).location().equals(this.currentMusic.getIdentifier());
   }

   public @Nullable String getCurrentMusicTranslationKey() {
      if (this.currentMusic != null) {
         Sound var1 = this.currentMusic.getSound();
         if (var1 != null) {
            return var1.getLocation().toShortLanguageKey();
         }
      }

      return null;
   }

   public void setMinutesBetweenSongs(MusicFrequency var1) {
      this.gameMusicFrequency = var1;
      this.nextSongDelay = this.gameMusicFrequency.getNextSongDelay(this.minecraft.getSituationalMusic(), this.random);
   }

   public static enum MusicFrequency implements StringRepresentable {
      DEFAULT("DEFAULT", "options.music_frequency.default", 20),
      FREQUENT("FREQUENT", "options.music_frequency.frequent", 10),
      CONSTANT("CONSTANT", "options.music_frequency.constant", 0);

      public static final Codec<MusicFrequency> CODEC = StringRepresentable.<MusicFrequency>fromEnum(MusicFrequency::values);
      private final String name;
      private final int maxFrequency;
      private final Component caption;

      private MusicFrequency(final String var3, final String var4, final int var5) {
         this.name = var3;
         this.maxFrequency = var5 * 1200;
         this.caption = Component.translatable(var4);
      }

      int getNextSongDelay(@Nullable Music var1, RandomSource var2) {
         if (var1 == null) {
            return this.maxFrequency;
         } else if (this == CONSTANT) {
            return 100;
         } else {
            int var3 = Math.min(var1.minDelay(), this.maxFrequency);
            int var4 = Math.min(var1.maxDelay(), this.maxFrequency);
            return Mth.nextInt(var2, var3, var4);
         }
      }

      public Component caption() {
         return this.caption;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static MusicFrequency[] $values() {
         return new MusicFrequency[]{DEFAULT, FREQUENT, CONSTANT};
      }
   }
}
