package net.minecraft.client.resources.sounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class SimpleSoundInstance extends AbstractSoundInstance {
   public SimpleSoundInstance(SoundEvent var1, SoundSource var2, float var3, float var4, RandomSource var5, BlockPos var6) {
      this(var1, var2, var3, var4, var5, (double)var6.getX() + 0.5, (double)var6.getY() + 0.5, (double)var6.getZ() + 0.5);
   }

   public static SimpleSoundInstance forUI(SoundEvent var0, float var1) {
      return forUI(var0, var1, 0.25F);
   }

   public static SimpleSoundInstance forUI(Holder<SoundEvent> var0, float var1) {
      return forUI((SoundEvent)var0.value(), var1);
   }

   public static SimpleSoundInstance forUI(SoundEvent var0, float var1, float var2) {
      return new SimpleSoundInstance(var0.getLocation(), SoundSource.MASTER, var2, var1, SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true);
   }

   public static SimpleSoundInstance forMusic(SoundEvent var0) {
      return new SimpleSoundInstance(var0.getLocation(), SoundSource.MUSIC, 1.0F, 1.0F, SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true);
   }

   public static SimpleSoundInstance forRecord(SoundEvent var0, Vec3 var1) {
      return new SimpleSoundInstance(var0, SoundSource.RECORDS, 4.0F, 1.0F, SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.LINEAR, var1.x, var1.y, var1.z);
   }

   public static SimpleSoundInstance forLocalAmbience(SoundEvent var0, float var1, float var2) {
      return new SimpleSoundInstance(var0.getLocation(), SoundSource.AMBIENT, var2, var1, SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true);
   }

   public static SimpleSoundInstance forAmbientAddition(SoundEvent var0) {
      return forLocalAmbience(var0, 1.0F, 1.0F);
   }

   public static SimpleSoundInstance forAmbientMood(SoundEvent var0, RandomSource var1, double var2, double var4, double var6) {
      return new SimpleSoundInstance(var0, SoundSource.AMBIENT, 1.0F, 1.0F, var1, false, 0, SoundInstance.Attenuation.LINEAR, var2, var4, var6);
   }

   public SimpleSoundInstance(SoundEvent var1, SoundSource var2, float var3, float var4, RandomSource var5, double var6, double var8, double var10) {
      this(var1, var2, var3, var4, var5, false, 0, SoundInstance.Attenuation.LINEAR, var6, var8, var10);
   }

   private SimpleSoundInstance(SoundEvent var1, SoundSource var2, float var3, float var4, RandomSource var5, boolean var6, int var7, SoundInstance.Attenuation var8, double var9, double var11, double var13) {
      this(var1.getLocation(), var2, var3, var4, var5, var6, var7, var8, var9, var11, var13, false);
   }

   public SimpleSoundInstance(ResourceLocation var1, SoundSource var2, float var3, float var4, RandomSource var5, boolean var6, int var7, SoundInstance.Attenuation var8, double var9, double var11, double var13, boolean var15) {
      super(var1, var2, var5);
      this.volume = var3;
      this.pitch = var4;
      this.x = var9;
      this.y = var11;
      this.z = var13;
      this.looping = var6;
      this.delay = var7;
      this.attenuation = var8;
      this.relative = var15;
   }
}
