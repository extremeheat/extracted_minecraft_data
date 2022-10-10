package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

public class LightTexture implements AutoCloseable {
   private final DynamicTexture field_205110_a;
   private final NativeImage field_205111_b;
   private final ResourceLocation field_205112_c;
   private boolean field_205113_d;
   private float field_205114_e;
   private float field_205115_f;
   private final GameRenderer field_205116_g;
   private final Minecraft field_205117_h;

   public LightTexture(GameRenderer var1) {
      super();
      this.field_205116_g = var1;
      this.field_205117_h = var1.func_205000_l();
      this.field_205110_a = new DynamicTexture(16, 16, false);
      this.field_205112_c = this.field_205117_h.func_110434_K().func_110578_a("light_map", this.field_205110_a);
      this.field_205111_b = this.field_205110_a.func_195414_e();
   }

   public void close() {
      this.field_205110_a.close();
   }

   public void func_205107_a() {
      this.field_205115_f = (float)((double)this.field_205115_f + (Math.random() - Math.random()) * Math.random() * Math.random());
      this.field_205115_f = (float)((double)this.field_205115_f * 0.9D);
      this.field_205114_e += this.field_205115_f - this.field_205114_e;
      this.field_205113_d = true;
   }

   public void func_205108_b() {
      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      GlStateManager.func_179090_x();
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
   }

   public void func_205109_c() {
      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      GlStateManager.func_179128_n(5890);
      GlStateManager.func_179096_D();
      float var1 = 0.00390625F;
      GlStateManager.func_179152_a(0.00390625F, 0.00390625F, 0.00390625F);
      GlStateManager.func_179109_b(8.0F, 8.0F, 8.0F);
      GlStateManager.func_179128_n(5888);
      this.field_205117_h.func_110434_K().func_110577_a(this.field_205112_c);
      GlStateManager.func_187421_b(3553, 10241, 9729);
      GlStateManager.func_187421_b(3553, 10240, 9729);
      GlStateManager.func_187421_b(3553, 10242, 10496);
      GlStateManager.func_187421_b(3553, 10243, 10496);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179098_w();
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
   }

   public void func_205106_a(float var1) {
      if (this.field_205113_d) {
         this.field_205117_h.field_71424_I.func_76320_a("lightTex");
         WorldClient var2 = this.field_205117_h.field_71441_e;
         if (var2 != null) {
            float var3 = var2.func_72971_b(1.0F);
            float var4 = var3 * 0.95F + 0.05F;
            float var6 = this.field_205117_h.field_71439_g.func_203719_J();
            float var5;
            if (this.field_205117_h.field_71439_g.func_70644_a(MobEffects.field_76439_r)) {
               var5 = this.field_205116_g.func_180438_a(this.field_205117_h.field_71439_g, var1);
            } else if (var6 > 0.0F && this.field_205117_h.field_71439_g.func_70644_a(MobEffects.field_205136_C)) {
               var5 = var6;
            } else {
               var5 = 0.0F;
            }

            for(int var7 = 0; var7 < 16; ++var7) {
               for(int var8 = 0; var8 < 16; ++var8) {
                  float var9 = var2.field_73011_w.func_177497_p()[var7] * var4;
                  float var10 = var2.field_73011_w.func_177497_p()[var8] * (this.field_205114_e * 0.1F + 1.5F);
                  if (var2.func_175658_ac() > 0) {
                     var9 = var2.field_73011_w.func_177497_p()[var7];
                  }

                  float var11 = var9 * (var3 * 0.65F + 0.35F);
                  float var12 = var9 * (var3 * 0.65F + 0.35F);
                  float var15 = var10 * ((var10 * 0.6F + 0.4F) * 0.6F + 0.4F);
                  float var16 = var10 * (var10 * var10 * 0.6F + 0.4F);
                  float var17 = var11 + var10;
                  float var18 = var12 + var15;
                  float var19 = var9 + var16;
                  var17 = var17 * 0.96F + 0.03F;
                  var18 = var18 * 0.96F + 0.03F;
                  var19 = var19 * 0.96F + 0.03F;
                  float var20;
                  if (this.field_205116_g.func_205002_d(var1) > 0.0F) {
                     var20 = this.field_205116_g.func_205002_d(var1);
                     var17 = var17 * (1.0F - var20) + var17 * 0.7F * var20;
                     var18 = var18 * (1.0F - var20) + var18 * 0.6F * var20;
                     var19 = var19 * (1.0F - var20) + var19 * 0.6F * var20;
                  }

                  if (var2.field_73011_w.func_186058_p() == DimensionType.THE_END) {
                     var17 = 0.22F + var10 * 0.75F;
                     var18 = 0.28F + var15 * 0.75F;
                     var19 = 0.25F + var16 * 0.75F;
                  }

                  if (var5 > 0.0F) {
                     var20 = 1.0F / var17;
                     if (var20 > 1.0F / var18) {
                        var20 = 1.0F / var18;
                     }

                     if (var20 > 1.0F / var19) {
                        var20 = 1.0F / var19;
                     }

                     var17 = var17 * (1.0F - var5) + var17 * var20 * var5;
                     var18 = var18 * (1.0F - var5) + var18 * var20 * var5;
                     var19 = var19 * (1.0F - var5) + var19 * var20 * var5;
                  }

                  if (var17 > 1.0F) {
                     var17 = 1.0F;
                  }

                  if (var18 > 1.0F) {
                     var18 = 1.0F;
                  }

                  if (var19 > 1.0F) {
                     var19 = 1.0F;
                  }

                  var20 = (float)this.field_205117_h.field_71474_y.field_74333_Y;
                  float var21 = 1.0F - var17;
                  float var22 = 1.0F - var18;
                  float var23 = 1.0F - var19;
                  var21 = 1.0F - var21 * var21 * var21 * var21;
                  var22 = 1.0F - var22 * var22 * var22 * var22;
                  var23 = 1.0F - var23 * var23 * var23 * var23;
                  var17 = var17 * (1.0F - var20) + var21 * var20;
                  var18 = var18 * (1.0F - var20) + var22 * var20;
                  var19 = var19 * (1.0F - var20) + var23 * var20;
                  var17 = var17 * 0.96F + 0.03F;
                  var18 = var18 * 0.96F + 0.03F;
                  var19 = var19 * 0.96F + 0.03F;
                  if (var17 > 1.0F) {
                     var17 = 1.0F;
                  }

                  if (var18 > 1.0F) {
                     var18 = 1.0F;
                  }

                  if (var19 > 1.0F) {
                     var19 = 1.0F;
                  }

                  if (var17 < 0.0F) {
                     var17 = 0.0F;
                  }

                  if (var18 < 0.0F) {
                     var18 = 0.0F;
                  }

                  if (var19 < 0.0F) {
                     var19 = 0.0F;
                  }

                  boolean var24 = true;
                  int var25 = (int)(var17 * 255.0F);
                  int var26 = (int)(var18 * 255.0F);
                  int var27 = (int)(var19 * 255.0F);
                  this.field_205111_b.func_195700_a(var8, var7, -16777216 | var27 << 16 | var26 << 8 | var25);
               }
            }

            this.field_205110_a.func_110564_a();
            this.field_205113_d = false;
            this.field_205117_h.field_71424_I.func_76319_b();
         }
      }
   }
}
