package net.minecraft.sounds;

@FunctionalInterface
public interface SoundSequenceBuilder {
   void waitThenPlay(int var1, SoundEvent var2, SoundSource var3, float var4, float var5);
}
