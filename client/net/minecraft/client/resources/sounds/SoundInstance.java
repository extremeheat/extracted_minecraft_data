package net.minecraft.client.resources.sounds;

import javax.annotation.Nullable;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public interface SoundInstance {
   ResourceLocation getLocation();

   @Nullable
   WeighedSoundEvents resolve(SoundManager var1);

   Sound getSound();

   SoundSource getSource();

   boolean isLooping();

   boolean isRelative();

   int getDelay();

   float getVolume();

   float getPitch();

   double getX();

   double getY();

   double getZ();

   SoundInstance.Attenuation getAttenuation();

   default boolean canStartSilent() {
      return false;
   }

   default boolean canPlaySound() {
      return true;
   }

   public static enum Attenuation {
      NONE,
      LINEAR;

      private Attenuation() {
      }

      // $FF: synthetic method
      private static SoundInstance.Attenuation[] $values() {
         return new SoundInstance.Attenuation[]{NONE, LINEAR};
      }
   }
}
