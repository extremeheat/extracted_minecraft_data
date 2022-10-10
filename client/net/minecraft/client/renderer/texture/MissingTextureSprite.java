package net.minecraft.client.renderer.texture;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public final class MissingTextureSprite extends TextureAtlasSprite {
   private static final ResourceLocation field_195678_l = new ResourceLocation("missingno");
   @Nullable
   private static DynamicTexture field_195679_m;
   private static final NativeImage field_195680_n = new NativeImage(16, 16, false);
   private static final MissingTextureSprite field_195681_o = (MissingTextureSprite)Util.func_199748_a(() -> {
      MissingTextureSprite var0 = new MissingTextureSprite();
      int var1 = -16777216;
      int var2 = -524040;

      for(int var3 = 0; var3 < 16; ++var3) {
         for(int var4 = 0; var4 < 16; ++var4) {
            if (var3 < 8 ^ var4 < 8) {
               field_195680_n.func_195700_a(var4, var3, -524040);
            } else {
               field_195680_n.func_195700_a(var4, var3, -16777216);
            }
         }
      }

      field_195680_n.func_195711_f();
      return var0;
   });

   private MissingTextureSprite() {
      super(field_195678_l, 16, 16);
      this.field_195670_c = new NativeImage[1];
      this.field_195670_c[0] = field_195680_n;
   }

   public static MissingTextureSprite func_195677_a() {
      return field_195681_o;
   }

   public static ResourceLocation func_195675_b() {
      return field_195678_l;
   }

   public void func_130103_l() {
      for(int var1 = 1; var1 < this.field_195670_c.length; ++var1) {
         this.field_195670_c[var1].close();
      }

      this.field_195670_c = new NativeImage[1];
      this.field_195670_c[0] = field_195680_n;
   }

   public static DynamicTexture func_195676_d() {
      if (field_195679_m == null) {
         field_195679_m = new DynamicTexture(field_195680_n);
         Minecraft.func_71410_x().func_110434_K().func_110579_a(field_195678_l, field_195679_m);
      }

      return field_195679_m;
   }
}
