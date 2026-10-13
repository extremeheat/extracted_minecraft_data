package net.minecraft.client.sounds;

import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public interface Weighted<T> {
   int getWeight();

   @Nullable T getSound(RandomSource random);

   void preloadIfRequired(SoundEngine soundEngine);
}
