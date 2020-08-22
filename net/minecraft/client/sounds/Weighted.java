package net.minecraft.client.sounds;

public interface Weighted {
   int getWeight();

   Object getSound();

   void preloadIfRequired(SoundEngine var1);
}
