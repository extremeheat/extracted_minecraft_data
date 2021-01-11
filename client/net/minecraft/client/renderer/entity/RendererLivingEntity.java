package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public abstract class RendererLivingEntity<T extends EntityLivingBase> extends Render<T> {
   private static final Logger field_147923_a = LogManager.getLogger();
   private static final DynamicTexture field_177096_e = new DynamicTexture(16, 16);
   protected ModelBase field_77045_g;
   protected FloatBuffer field_177095_g = GLAllocation.func_74529_h(4);
   protected List<LayerRenderer<T>> field_177097_h = Lists.newArrayList();
   protected boolean field_177098_i = false;

   public RendererLivingEntity(RenderManager var1, ModelBase var2, float var3) {
      super(var1);
      this.field_77045_g = var2;
      this.field_76989_e = var3;
   }

   protected <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean func_177094_a(U var1) {
      return this.field_177097_h.add(var1);
   }

   protected <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean func_177089_b(U var1) {
      return this.field_177097_h.remove(var1);
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

   public void func_82422_c() {
   }

   public void func_76986_a(T var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179129_p();
      this.field_77045_g.field_78095_p = this.func_77040_d(var1, var9);
      this.field_77045_g.field_78093_q = var1.func_70115_ae();
      this.field_77045_g.field_78091_s = var1.func_70631_g_();

      try {
         float var10 = this.func_77034_a(var1.field_70760_ar, var1.field_70761_aq, var9);
         float var11 = this.func_77034_a(var1.field_70758_at, var1.field_70759_as, var9);
         float var12 = var11 - var10;
         float var14;
         if (var1.func_70115_ae() && var1.field_70154_o instanceof EntityLivingBase) {
            EntityLivingBase var13 = (EntityLivingBase)var1.field_70154_o;
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
         }

         float var20 = var1.field_70127_C + (var1.field_70125_A - var1.field_70127_C) * var9;
         this.func_77039_a(var1, var2, var4, var6);
         var14 = this.func_77044_a(var1, var9);
         this.func_77043_a(var1, var14, var10, var9);
         GlStateManager.func_179091_B();
         GlStateManager.func_179152_a(-1.0F, -1.0F, 1.0F);
         this.func_77041_b(var1, var9);
         float var15 = 0.0625F;
         GlStateManager.func_179109_b(0.0F, -1.5078125F, 0.0F);
         float var16 = var1.field_70722_aY + (var1.field_70721_aZ - var1.field_70722_aY) * var9;
         float var17 = var1.field_70754_ba - var1.field_70721_aZ * (1.0F - var9);
         if (var1.func_70631_g_()) {
            var17 *= 3.0F;
         }

         if (var16 > 1.0F) {
            var16 = 1.0F;
         }

         GlStateManager.func_179141_d();
         this.field_77045_g.func_78086_a(var1, var17, var16, var9);
         this.field_77045_g.func_78087_a(var17, var16, var14, var12, var20, 0.0625F, var1);
         boolean var18;
         if (this.field_177098_i) {
            var18 = this.func_177088_c(var1);
            this.func_77036_a(var1, var17, var16, var14, var12, var20, 0.0625F);
            if (var18) {
               this.func_180565_e();
            }
         } else {
            var18 = this.func_177090_c(var1, var9);
            this.func_77036_a(var1, var17, var16, var14, var12, var20, 0.0625F);
            if (var18) {
               this.func_177091_f();
            }

            GlStateManager.func_179132_a(true);
            if (!(var1 instanceof EntityPlayer) || !((EntityPlayer)var1).func_175149_v()) {
               this.func_177093_a(var1, var17, var16, var9, var14, var12, var20, 0.0625F);
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
      if (!this.field_177098_i) {
         super.func_76986_a(var1, var2, var4, var6, var8, var9);
      }

   }

   protected boolean func_177088_c(T var1) {
      int var2 = 16777215;
      if (var1 instanceof EntityPlayer) {
         ScorePlayerTeam var3 = (ScorePlayerTeam)var1.func_96124_cp();
         if (var3 != null) {
            String var4 = FontRenderer.func_78282_e(var3.func_96668_e());
            if (var4.length() >= 2) {
               var2 = this.func_76983_a().func_175064_b(var4.charAt(1));
            }
         }
      }

      float var6 = (float)(var2 >> 16 & 255) / 255.0F;
      float var7 = (float)(var2 >> 8 & 255) / 255.0F;
      float var5 = (float)(var2 & 255) / 255.0F;
      GlStateManager.func_179140_f();
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
      GlStateManager.func_179131_c(var6, var7, var5, 1.0F);
      GlStateManager.func_179090_x();
      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      GlStateManager.func_179090_x();
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
      return true;
   }

   protected void func_180565_e() {
      GlStateManager.func_179145_e();
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
      GlStateManager.func_179098_w();
      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      GlStateManager.func_179098_w();
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
   }

   protected void func_77036_a(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      boolean var8 = !var1.func_82150_aj();
      boolean var9 = !var8 && !var1.func_98034_c(Minecraft.func_71410_x().field_71439_g);
      if (var8 || var9) {
         if (!this.func_180548_c(var1)) {
            return;
         }

         if (var9) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 0.15F);
            GlStateManager.func_179132_a(false);
            GlStateManager.func_179147_l();
            GlStateManager.func_179112_b(770, 771);
            GlStateManager.func_179092_a(516, 0.003921569F);
         }

         this.field_77045_g.func_78088_a(var1, var2, var3, var4, var5, var6, var7);
         if (var9) {
            GlStateManager.func_179084_k();
            GlStateManager.func_179092_a(516, 0.1F);
            GlStateManager.func_179121_F();
            GlStateManager.func_179132_a(true);
         }
      }

   }

   protected boolean func_177090_c(T var1, float var2) {
      return this.func_177092_a(var1, var2, true);
   }

   protected boolean func_177092_a(T var1, float var2, boolean var3) {
      float var4 = var1.func_70013_c(var2);
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
         GL11.glTexEnvi(8960, 8704, OpenGlHelper.field_176095_s);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176099_x, 8448);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176098_y, OpenGlHelper.field_77478_a);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_176093_u);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176081_B, 768);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176082_C, 768);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176077_E, 7681);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176078_F, OpenGlHelper.field_77478_a);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176085_I, 770);
         GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
         GlStateManager.func_179098_w();
         GL11.glTexEnvi(8960, 8704, OpenGlHelper.field_176095_s);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176099_x, OpenGlHelper.field_176094_t);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176098_y, OpenGlHelper.field_176092_v);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_176091_w);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176080_A, OpenGlHelper.field_176092_v);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176081_B, 768);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176082_C, 768);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176076_D, 770);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176077_E, 7681);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176078_F, OpenGlHelper.field_176091_w);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176085_I, 770);
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
         GL11.glTexEnv(8960, 8705, this.field_177095_g);
         GlStateManager.func_179138_g(OpenGlHelper.field_176096_r);
         GlStateManager.func_179098_w();
         GlStateManager.func_179144_i(field_177096_e.func_110552_b());
         GL11.glTexEnvi(8960, 8704, OpenGlHelper.field_176095_s);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176099_x, 8448);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176098_y, OpenGlHelper.field_176091_w);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_77476_b);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176081_B, 768);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176082_C, 768);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176077_E, 7681);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176078_F, OpenGlHelper.field_176091_w);
         GL11.glTexEnvi(8960, OpenGlHelper.field_176085_I, 770);
         GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
         return true;
      }
   }

   protected void func_177091_f() {
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
      GlStateManager.func_179098_w();
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.field_176095_s);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176099_x, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176098_y, OpenGlHelper.field_77478_a);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_176093_u);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176081_B, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176082_C, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176077_E, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176078_F, OpenGlHelper.field_77478_a);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176079_G, OpenGlHelper.field_176093_u);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176085_I, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176086_J, 770);
      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.field_176095_s);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176099_x, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176081_B, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176082_C, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176098_y, 5890);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_176091_w);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176077_E, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176085_I, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176078_F, 5890);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179138_g(OpenGlHelper.field_176096_r);
      GlStateManager.func_179090_x();
      GlStateManager.func_179144_i(0);
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.field_176095_s);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176099_x, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176081_B, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176082_C, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176098_y, 5890);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_176091_w);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176077_E, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176085_I, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176078_F, 5890);
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
      } else {
         String var6 = EnumChatFormatting.func_110646_a(var1.func_70005_c_());
         if (var6 != null && (var6.equals("Dinnerbone") || var6.equals("Grumm")) && (!(var1 instanceof EntityPlayer) || ((EntityPlayer)var1).func_175148_a(EnumPlayerModelParts.CAPE))) {
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
            float var12 = 0.02666667F;
            GlStateManager.func_179092_a(516, 0.1F);
            if (var1.func_70093_af()) {
               FontRenderer var13 = this.func_76983_a();
               GlStateManager.func_179094_E();
               GlStateManager.func_179109_b((float)var2, (float)var4 + var1.field_70131_O + 0.5F - (var1.func_70631_g_() ? var1.field_70131_O / 2.0F : 0.0F), (float)var6);
               GL11.glNormal3f(0.0F, 1.0F, 0.0F);
               GlStateManager.func_179114_b(-this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
               GlStateManager.func_179114_b(this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
               GlStateManager.func_179152_a(-0.02666667F, -0.02666667F, 0.02666667F);
               GlStateManager.func_179109_b(0.0F, 9.374999F, 0.0F);
               GlStateManager.func_179140_f();
               GlStateManager.func_179132_a(false);
               GlStateManager.func_179147_l();
               GlStateManager.func_179090_x();
               GlStateManager.func_179120_a(770, 771, 1, 0);
               int var14 = var13.func_78256_a(var11) / 2;
               Tessellator var15 = Tessellator.func_178181_a();
               WorldRenderer var16 = var15.func_178180_c();
               var16.func_181668_a(7, DefaultVertexFormats.field_181706_f);
               var16.func_181662_b((double)(-var14 - 1), -1.0D, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
               var16.func_181662_b((double)(-var14 - 1), 8.0D, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
               var16.func_181662_b((double)(var14 + 1), 8.0D, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
               var16.func_181662_b((double)(var14 + 1), -1.0D, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
               var15.func_78381_a();
               GlStateManager.func_179098_w();
               GlStateManager.func_179132_a(true);
               var13.func_78276_b(var11, -var13.func_78256_a(var11) / 2, 0, 553648127);
               GlStateManager.func_179145_e();
               GlStateManager.func_179084_k();
               GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.func_179121_F();
            } else {
               this.func_177069_a(var1, var2, var4 - (var1.func_70631_g_() ? (double)(var1.field_70131_O / 2.0F) : 0.0D), var6, var11, 0.02666667F, var8);
            }

         }
      }
   }

   protected boolean func_177070_b(T var1) {
      EntityPlayerSP var2 = Minecraft.func_71410_x().field_71439_g;
      if (var1 instanceof EntityPlayer && var1 != var2) {
         Team var3 = var1.func_96124_cp();
         Team var4 = var2.func_96124_cp();
         if (var3 != null) {
            Team.EnumVisible var5 = var3.func_178770_i();
            switch(var5) {
            case ALWAYS:
               return true;
            case NEVER:
               return false;
            case HIDE_FOR_OTHER_TEAMS:
               return var4 == null || var3.func_142054_a(var4);
            case HIDE_FOR_OWN_TEAM:
               return var4 == null || !var3.func_142054_a(var4);
            default:
               return true;
            }
         }
      }

      return Minecraft.func_71382_s() && var1 != this.field_76990_c.field_78734_h && !var1.func_98034_c(var2) && var1.field_70153_n == null;
   }

   public void func_177086_a(boolean var1) {
      this.field_177098_i = var1;
   }

   // $FF: synthetic method
   protected boolean func_177070_b(Entity var1) {
      return this.func_177070_b((EntityLivingBase)var1);
   }

   // $FF: synthetic method
   public void func_177067_a(Entity var1, double var2, double var4, double var6) {
      this.func_177067_a((EntityLivingBase)var1, var2, var4, var6);
   }

   static {
      int[] var0 = field_177096_e.func_110565_c();

      for(int var1 = 0; var1 < 256; ++var1) {
         var0[var1] = -1;
      }

      field_177096_e.func_110564_a();
   }
}
