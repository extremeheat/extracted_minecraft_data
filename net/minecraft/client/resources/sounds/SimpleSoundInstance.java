package net.minecraft.client.resources.sounds;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SimpleSoundInstance extends AbstractSoundInstance {
   public SimpleSoundInstance(SoundEvent var1, SoundSource var2, float var3, float var4, BlockPos var5) {
      this(var1, var2, var3, var4, (float)var5.getX() + 0.5F, (float)var5.getY() + 0.5F, (float)var5.getZ() + 0.5F);
   }

   public static SimpleSoundInstance forUI(SoundEvent var0, float var1) {
      return forUI(var0, var1, 0.25F);
   }

   public static SimpleSoundInstance forUI(SoundEvent var0, float var1, float var2) {
      return new SimpleSoundInstance(var0.getLocation(), SoundSource.MASTER, var2, var1, false, 0, SoundInstance.Attenuation.NONE, 0.0F, 0.0F, 0.0F, true);
   }

   public static SimpleSoundInstance forMusic(SoundEvent var0) {
      return new SimpleSoundInstance(var0.getLocation(), SoundSource.MUSIC, 1.0F, 1.0F, false, 0, SoundInstance.Attenuation.NONE, 0.0F, 0.0F, 0.0F, true);
   }

   public static SimpleSoundInstance forRecord(SoundEvent var0, float var1, float var2, float var3) {
      return new SimpleSoundInstance(var0, SoundSource.RECORDS, 4.0F, 1.0F, false, 0, SoundInstance.Attenuation.LINEAR, var1, var2, var3);
   }

   public SimpleSoundInstance(SoundEvent var1, SoundSource var2, float var3, float var4, float var5, float var6, float var7) {
      this(var1, var2, var3, var4, false, 0, SoundInstance.Attenuation.LINEAR, var5, var6, var7);
   }

   private SimpleSoundInstance(SoundEvent var1, SoundSource var2, float var3, float var4, boolean var5, int var6, SoundInstance.Attenuation var7, float var8, float var9, float var10) {
      this(var1.getLocation(), var2, var3, var4, var5, var6, var7, var8, var9, var10, false);
   }

   public SimpleSoundInstance(ResourceLocation var1, SoundSource var2, float var3, float var4, boolean var5, int var6, SoundInstance.Attenuation var7, float var8, float var9, float var10, boolean var11) {
      super(var1, var2);
      this.volume = var3;
      this.pitch = var4;
      this.x = var8;
      this.y = var9;
      this.z = var10;
      this.looping = var5;
      this.delay = var6;
      this.attenuation = var7;
      this.relative = var11;
   }
}
