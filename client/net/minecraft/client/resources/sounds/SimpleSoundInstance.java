package net.minecraft.client.resources.sounds;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SimpleSoundInstance extends AbstractSoundInstance {
   public SimpleSoundInstance(SoundEvent var1, SoundSource var2, float var3, float var4, BlockPos var5) {
      this(var1, var2, var3, var4, (double)var5.getX() + 0.5D, (double)var5.getY() + 0.5D, (double)var5.getZ() + 0.5D);
   }

   public static SimpleSoundInstance forUI(SoundEvent var0, float var1) {
      return forUI(var0, var1, 0.25F);
   }

   public static SimpleSoundInstance forUI(SoundEvent var0, float var1, float var2) {
      return new SimpleSoundInstance(var0.getLocation(), SoundSource.MASTER, var2, var1, false, 0, SoundInstance.Attenuation.NONE, 0.0D, 0.0D, 0.0D, true);
   }

   public static SimpleSoundInstance forMusic(SoundEvent var0) {
      return new SimpleSoundInstance(var0.getLocation(), SoundSource.MUSIC, 1.0F, 1.0F, false, 0, SoundInstance.Attenuation.NONE, 0.0D, 0.0D, 0.0D, true);
   }

   public static SimpleSoundInstance forRecord(SoundEvent var0, double var1, double var3, double var5) {
      return new SimpleSoundInstance(var0, SoundSource.RECORDS, 4.0F, 1.0F, false, 0, SoundInstance.Attenuation.LINEAR, var1, var3, var5);
   }

   public static SimpleSoundInstance forLocalAmbience(SoundEvent var0, float var1, float var2) {
      return new SimpleSoundInstance(var0.getLocation(), SoundSource.AMBIENT, var2, var1, false, 0, SoundInstance.Attenuation.NONE, 0.0D, 0.0D, 0.0D, true);
   }

   public static SimpleSoundInstance forAmbientAddition(SoundEvent var0) {
      return forLocalAmbience(var0, 1.0F, 1.0F);
   }

   public static SimpleSoundInstance forAmbientMood(SoundEvent var0, double var1, double var3, double var5) {
      return new SimpleSoundInstance(var0, SoundSource.AMBIENT, 1.0F, 1.0F, false, 0, SoundInstance.Attenuation.LINEAR, var1, var3, var5);
   }

   public SimpleSoundInstance(SoundEvent var1, SoundSource var2, float var3, float var4, double var5, double var7, double var9) {
      this(var1, var2, var3, var4, false, 0, SoundInstance.Attenuation.LINEAR, var5, var7, var9);
   }

   private SimpleSoundInstance(SoundEvent var1, SoundSource var2, float var3, float var4, boolean var5, int var6, SoundInstance.Attenuation var7, double var8, double var10, double var12) {
      this(var1.getLocation(), var2, var3, var4, var5, var6, var7, var8, var10, var12, false);
   }

   public SimpleSoundInstance(ResourceLocation var1, SoundSource var2, float var3, float var4, boolean var5, int var6, SoundInstance.Attenuation var7, double var8, double var10, double var12, boolean var14) {
      super(var1, var2);
      this.volume = var3;
      this.pitch = var4;
      this.x = var8;
      this.y = var10;
      this.z = var12;
      this.looping = var5;
      this.delay = var6;
      this.attenuation = var7;
      this.relative = var14;
   }
}
