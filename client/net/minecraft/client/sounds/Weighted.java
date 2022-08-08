package net.minecraft.client.sounds;

import net.minecraft.util.RandomSource;

public interface Weighted<T> {
   int getWeight();

   T getSound(RandomSource var1);

   void preloadIfRequired(SoundEngine var1);
}
