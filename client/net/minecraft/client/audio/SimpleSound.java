package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class SimpleSound extends AbstractSound {
   public SimpleSound(SoundEvent var1, SoundCategory var2, float var3, float var4, BlockPos var5) {
      this(var1, var2, var3, var4, (float)var5.func_177958_n() + 0.5F, (float)var5.func_177956_o() + 0.5F, (float)var5.func_177952_p() + 0.5F);
   }

   public static SimpleSound func_184371_a(SoundEvent var0, float var1) {
      return func_194007_a(var0, var1, 0.25F);
   }

   public static SimpleSound func_194007_a(SoundEvent var0, float var1, float var2) {
      return new SimpleSound(var0, SoundCategory.MASTER, var2, var1, false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
   }

   public static SimpleSound func_184370_a(SoundEvent var0) {
      return new SimpleSound(var0, SoundCategory.MUSIC, 1.0F, 1.0F, false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
   }

   public static SimpleSound func_184372_a(SoundEvent var0, float var1, float var2, float var3) {
      return new SimpleSound(var0, SoundCategory.RECORDS, 4.0F, 1.0F, false, 0, ISound.AttenuationType.LINEAR, var1, var2, var3);
   }

   public SimpleSound(SoundEvent var1, SoundCategory var2, float var3, float var4, float var5, float var6, float var7) {
      this((SoundEvent)var1, var2, var3, var4, false, 0, ISound.AttenuationType.LINEAR, var5, var6, var7);
   }

   private SimpleSound(SoundEvent var1, SoundCategory var2, float var3, float var4, boolean var5, int var6, ISound.AttenuationType var7, float var8, float var9, float var10) {
      this(var1.func_187503_a(), var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public SimpleSound(ResourceLocation var1, SoundCategory var2, float var3, float var4, boolean var5, int var6, ISound.AttenuationType var7, float var8, float var9, float var10) {
      super(var1, var2);
      this.field_147662_b = var3;
      this.field_147663_c = var4;
      this.field_147660_d = var8;
      this.field_147661_e = var9;
      this.field_147658_f = var10;
      this.field_147659_g = var5;
      this.field_147665_h = var6;
      this.field_147666_i = var7;
   }
}
