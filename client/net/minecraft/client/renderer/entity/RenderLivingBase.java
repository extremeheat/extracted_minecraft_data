package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RenderLivingBase<T extends EntityLivingBase> extends Render<T> {
   private static final Logger field_147923_a = LogManager.getLogger();
   private static final DynamicTexture field_177096_e = (DynamicTexture)Util.func_200696_a(new DynamicTexture(16, 16, false), (var0) -> {
      var0.func_195414_e().func_195711_f();

      for(int var1 = 0; var1 < 16; ++var1) {
         for(int var2 = 0; var2 < 16; ++var2) {
            var0.func_195414_e().func_195700_a(var2, var1, -1);
         }
      }

      var0.func_110564_a();
   });
   protected ModelBase field_77045_g;
   protected FloatBuffer field_177095_g = GLAllocation.func_74529_h(4);
   protected List<LayerRenderer<T>> field_177097_h = Lists.newArrayList();
   protected boolean field_188323_j;

   public RenderLivingBase(RenderManager var1, ModelBase var2, float var3) {
      super(var1);
      this.field_77045_g = var2;
      this.field_76989_e = var3;
   }

   protected <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean func_177094_a(U var1) {
      return this.field_177097_h.add(var1);
   }

   public ModelBase func_177087_b() {
      return this.field_77045_g;
   }

   protected float func_77034_a(float var1, float var2, float var3) {
      float var4;
      for(var4 = var2 - var1; var4 < -180.0F; var4 += 360.0F) {
      }

      while(var4 >= 180.0F) {
         var4 -= 360.0F;
      }

      return var1 + var3 * var4;
   }

   public void func_76986_a(T var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179129_p();
      this.field_77045_g.field_78095_p = this.func_77040_d(var1, var9);
      this.field_77045_g.field_78093_q = var1.func_184218_aH();
      this.field_77045_g.field_78091_s = var1.func_70631_g_();

      try {
         float var10 = this.func_77034_a(var1.field_70760_ar, var1.field_70761_aq, var9);
         float var11 = this.func_77034_a(var1.field_70758_at, var1.field_70759_as, var9);
         float var12 = var11 - var10;
         float var14;
         if (var1.func_184218_aH() && var1.func_184187_bx() instanceof EntityLivingBase) {
            EntityLivingBase var13 = (EntityLivingBase)var1.func_184187_bx();
            var10 = this.func_77034_a(var13.field_70760_ar, var13.field_70761_aq, var9);
            var12 = var11 - var10;
            var14 = MathHelper.func_76142_g(var12);
            if (var14 < -85.0F) {
               var14 = -85.0F;
            }

            if (var14 >= 85.0F) {
               var14 = 85.0F;
            }

            var10 = var11 - var14;
            if (var14 * var14 > 2500.0F) {
               var10 += var14 * 0.2F;
            }

            var12 = var11 - var10;
         }

         float var20 = var1.field_70127_C + (var1.field_70125_A - var1.field_70127_C) * var9;
         this.func_77039_a(var1, var2, var4, var6);
         var14 = this.func_77044_a(var1, var9);
         this.func_77043_a(var1, var14, var10, var9);
         float var15 = this.func_188322_c(var1, var9);
         float var16 = 0.0F;
         float var17 = 0.0F;
         if (!var1.func_184218_aH()) {
            var16 = var1.field_184618_aE + (var1.field_70721_aZ - var1.field_184618_aE) * var9;
            var17 = var1.field_184619_aG - var1.field_70721_aZ * (1.0F - var9);
            if (var1.func_70631_g_()) {
               var17 *= 3.0F;
            }

            if (var16 > 1.0F) {
               var16 = 1.0F;
            }
         }

         GlStateManager.func_179141_d();
         this.field_77045_g.func_78086_a(var1, var17, var16, var9);
         this.field_77045_g.func_78087_a(var17, var16, var14, var12, var20, var15, var1);
         boolean var18;
         if (this.field_188301_f) {
            var18 = this.func_177088_c(var1);
            GlStateManager.func_179142_g();
            GlStateManager.func_187431_e(this.func_188298_c(var1));
            if (!this.field_188323_j) {
               this.func_77036_a(var1, var17, var16, var14, var12, var20, var15);
            }

            if (!(var1 instanceof EntityPlayer) || !((EntityPlayer)var1).func_175149_v()) {
               this.func_177093_a(var1, var17, var16, var9, var14, var12, var20, var15);
            }

            GlStateManager.func_187417_n();
            GlStateManager.func_179119_h();
            if (var18) {
               this.func_180565_e();
            }
         } else {
            var18 = this.func_177090_c(var1, var9);
            this.func_77036_a(var1, var17, var16, var14, var12, var20, var15);
            if (var18) {
               this.func_177091_f();
            }

            GlStateManager.func_179132_a(true);
            if (!(var1 instanceof EntityPlayer) || !((EntityPlayer)var1).func_175149_v()) {
               this.func_177093_a(var1, var17, var16, var9, var14, var12, var20, var15);
            }
         }

         GlStateManager.func_179101_C();
      } catch (Exception var19) {
         field_147923_a.error("Couldn't render entity", var19);
      }

      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      GlStateManager.func_179098_w();
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
      GlStateManager.func_179089_o();
      GlStateManager.func_179121_F();
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   public float func_188322_c(T var1, float var2) {
      GlStateManager.func_179091_B();
      GlStateManager.func_179152_a(-1.0F, -1.0F, 1.0F);
      this.func_77041_b(var1, var2);
      float var3 = 0.0625F;
      GlStateManager.func_179109_b(0.0F, -1.501F, 0.0F);
      return 0.0625F;
   }

   protected boolean func_177088_c(T var1) {
      GlStateManager.func_179140_f();
      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      GlStateManager.func_179090_x();
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
      return true;
   }

   protected void func_180565_e() {
      GlStateManager.func_179145_e();
      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      GlStateManager.func_179098_w();
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
   }

   protected void func_77036_a(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      boolean var8 = this.func_193115_c(var1);
      boolean var9 = !var8 && !var1.func_98034_c(Minecraft.func_71410_x().field_71439_g);
      if (var8 || var9) {
         if (!this.func_180548_c(var1)) {
            return;
         }

         if (var9) {
            GlStateManager.func_187408_a(GlStateManager.Profile.TRANSPARENT_MODEL);
         }

         this.field_77045_g.func_78088_a(var1, var2, var3, var4, var5, var6, var7);
         if (var9) {
            GlStateManager.func_187440_b(GlStateManager.Profile.TRANSPARENT_MODEL);
         }
      }

   }

   protected boolean func_193115_c(T var1) {
      return !var1.func_82150_aj() || this.field_188301_f;
   }

   protected boolean func_177090_c(T var1, float var2) {
      return this.func_177092_a(var1, var2, true);
   }

   protected boolean func_177092_a(T var1, float var2, boolean var3) {
      float var4 = var1.func_70013_c();
      int var5 = this.func_77030_a(var1, var4, var2);
      boolean var6 = (var5 >> 24 & 255) > 0;
      boolean var7 = var1.field_70737_aN > 0 || var1.field_70725_aQ > 0;
      if (!var6 && !var7) {
         return false;
      } else if (!var6 && !var3) {
         return false;
      } else {
         GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
         GlStateManager.func_179098_w();
         GlStateManager.func_187399_a(8960, 8704, OpenGlHelper.field_176095_s);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176099_x, 8448);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176098_y, OpenGlHelper.field_77478_a);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_176093_u);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176081_B, 768);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176082_C, 768);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176077_E, 7681);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176078_F, OpenGlHelper.field_77478_a);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176085_I, 770);
         GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
         GlStateManager.func_179098_w();
         GlStateManager.func_187399_a(8960, 8704, OpenGlHelper.field_176095_s);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176099_x, OpenGlHelper.field_176094_t);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176098_y, OpenGlHelper.field_176092_v);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_176091_w);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176080_A, OpenGlHelper.field_176092_v);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176081_B, 768);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176082_C, 768);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176076_D, 770);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176077_E, 7681);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176078_F, OpenGlHelper.field_176091_w);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176085_I, 770);
         this.field_177095_g.position(0);
         if (var7) {
            this.field_177095_g.put(1.0F);
            this.field_177095_g.put(0.0F);
            this.field_177095_g.put(0.0F);
            this.field_177095_g.put(0.3F);
         } else {
            float var8 = (float)(var5 >> 24 & 255) / 255.0F;
            float var9 = (float)(var5 >> 16 & 255) / 255.0F;
            float var10 = (float)(var5 >> 8 & 255) / 255.0F;
            float var11 = (float)(var5 & 255) / 255.0F;
            this.field_177095_g.put(var9);
            this.field_177095_g.put(var10);
            this.field_177095_g.put(var11);
            this.field_177095_g.put(1.0F - var8);
         }

         this.field_177095_g.flip();
         GlStateManager.func_187448_b(8960, 8705, this.field_177095_g);
         GlStateManager.func_179138_g(OpenGlHelper.field_176096_r);
         GlStateManager.func_179098_w();
         GlStateManager.func_179144_i(field_177096_e.func_110552_b());
         GlStateManager.func_187399_a(8960, 8704, OpenGlHelper.field_176095_s);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176099_x, 8448);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176098_y, OpenGlHelper.field_176091_w);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_77476_b);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176081_B, 768);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176082_C, 768);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176077_E, 7681);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176078_F, OpenGlHelper.field_176091_w);
         GlStateManager.func_187399_a(8960, OpenGlHelper.field_176085_I, 770);
         GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
         return true;
      }
   }

   protected void func_177091_f() {
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
      GlStateManager.func_179098_w();
      GlStateManager.func_187399_a(8960, 8704, OpenGlHelper.field_176095_s);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176099_x, 8448);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176098_y, OpenGlHelper.field_77478_a);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_176093_u);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176081_B, 768);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176082_C, 768);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176077_E, 8448);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176078_F, OpenGlHelper.field_77478_a);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176079_G, OpenGlHelper.field_176093_u);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176085_I, 770);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176086_J, 770);
      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      GlStateManager.func_187399_a(8960, 8704, OpenGlHelper.field_176095_s);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176099_x, 8448);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176081_B, 768);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176082_C, 768);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176098_y, 5890);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_176091_w);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176077_E, 8448);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176085_I, 770);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176078_F, 5890);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179138_g(OpenGlHelper.field_176096_r);
      GlStateManager.func_179090_x();
      GlStateManager.func_179144_i(0);
      GlStateManager.func_187399_a(8960, 8704, OpenGlHelper.field_176095_s);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176099_x, 8448);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176081_B, 768);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176082_C, 768);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176098_y, 5890);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_176091_w);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176077_E, 8448);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176085_I, 770);
      GlStateManager.func_187399_a(8960, OpenGlHelper.field_176078_F, 5890);
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
   }

   protected void func_77039_a(T var1, double var2, double var4, double var6) {
      GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
   }

   protected void func_77043_a(T var1, float var2, float var3, float var4) {
      GlStateManager.func_179114_b(180.0F - var3, 0.0F, 1.0F, 0.0F);
      if (var1.field_70725_aQ > 0) {
         float var5 = ((float)var1.field_70725_aQ + var4 - 1.0F) / 20.0F * 1.6F;
         var5 = MathHelper.func_76129_c(var5);
         if (var5 > 1.0F) {
            var5 = 1.0F;
         }

         GlStateManager.func_179114_b(var5 * this.func_77037_a(var1), 0.0F, 0.0F, 1.0F);
      } else if (var1.func_204805_cN()) {
         GlStateManager.func_179114_b(-90.0F - var1.field_70125_A, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(((float)var1.field_70173_aa + var4) * -75.0F, 0.0F, 1.0F, 0.0F);
      } else if (var1.func_145818_k_() || var1 instanceof EntityPlayer) {
         String var6 = TextFormatting.func_110646_a(var1.func_200200_C_().getString());
         if (var6 != null && ("Dinnerbone".equals(var6) || "Grumm".equals(var6)) && (!(var1 instanceof EntityPlayer) || ((EntityPlayer)var1).func_175148_a(EnumPlayerModelParts.CAPE))) {
            GlStateManager.func_179109_b(0.0F, var1.field_70131_O + 0.1F, 0.0F);
            GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
         }
      }

   }

   protected float func_77040_d(T var1, float var2) {
      return var1.func_70678_g(var2);
   }

   protected float func_77044_a(T var1, float var2) {
      return (float)var1.field_70173_aa + var2;
   }

   protected void func_177093_a(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      Iterator var9 = this.field_177097_h.iterator();

      while(var9.hasNext()) {
         LayerRenderer var10 = (LayerRenderer)var9.next();
         boolean var11 = this.func_177092_a(var1, var4, var10.func_177142_b());
         var10.func_177141_a(var1, var2, var3, var4, var5, var6, var7, var8);
         if (var11) {
            this.func_177091_f();
         }
      }

   }

   protected float func_77037_a(T var1) {
      return 90.0F;
   }

   protected int func_77030_a(T var1, float var2, float var3) {
      return 0;
   }

   protected void func_77041_b(T var1, float var2) {
   }

   public void func_177067_a(T var1, double var2, double var4, double var6) {
      if (this.func_177070_b(var1)) {
         double var8 = var1.func_70068_e(this.field_76990_c.field_78734_h);
         float var10 = var1.func_70093_af() ? 32.0F : 64.0F;
         if (var8 < (double)(var10 * var10)) {
            String var11 = var1.func_145748_c_().func_150254_d();
            GlStateManager.func_179092_a(516, 0.1F);
            this.func_188296_a(var1, var2, var4, var6, var11, var8);
         }
      }
   }

   protected boolean func_177070_b(T var1) {
      EntityPlayerSP var2 = Minecraft.func_71410_x().field_71439_g;
      boolean var3 = !var1.func_98034_c(var2);
      if (var1 != var2) {
         Team var4 = var1.func_96124_cp();
         Team var5 = var2.func_96124_cp();
         if (var4 != null) {
            Team.EnumVisible var6 = var4.func_178770_i();
            switch(var6) {
            case ALWAYS:
               return var3;
            case NEVER:
               return false;
            case HIDE_FOR_OTHER_TEAMS:
               return var5 == null ? var3 : var4.func_142054_a(var5) && (var4.func_98297_h() || var3);
            case HIDE_FOR_OWN_TEAM:
               return var5 == null ? var3 : !var4.func_142054_a(var5) && var3;
            default:
               return true;
            }
         }
      }

      return Minecraft.func_71382_s() && var1 != this.field_76990_c.field_78734_h && var3 && !var1.func_184207_aI();
   }

   // $FF: synthetic method
   protected boolean func_177070_b(Entity var1) {
      return this.func_177070_b((EntityLivingBase)var1);
   }

   // $FF: synthetic method
   public void func_177067_a(Entity var1, double var2, double var4, double var6) {
      this.func_177067_a((EntityLivingBase)var1, var2, var4, var6);
   }
}
