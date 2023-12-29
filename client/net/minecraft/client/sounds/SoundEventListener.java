package net.minecraft.client.sounds;

import net.minecraft.client.resources.sounds.SoundInstance;

public interface SoundEventListener {
   void onPlaySound(SoundInstance var1, WeighedSoundEvents var2, float var3);
}
