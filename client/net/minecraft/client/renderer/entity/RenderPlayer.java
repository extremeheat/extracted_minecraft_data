package net.minecraft.client.renderer.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerEntityOnShoulder;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerSpinAttackEffect;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.client.renderer.entity.model.ModelPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RenderPlayer extends RenderLivingBase<AbstractClientPlayer> {
   private float field_205127_a;

   public RenderPlayer(RenderManager var1) {
      this(var1, false);
   }

   public RenderPlayer(RenderManager var1, boolean var2) {
      super(var1, new ModelPlayer(0.0F, var2), 0.5F);
      this.func_177094_a(new LayerBipedArmor(this));
      this.func_177094_a(new LayerHeldItem(this));
      this.func_177094_a(new LayerArrow(this));
      this.func_177094_a(new LayerDeadmau5Head(this));
      this.func_177094_a(new LayerCape(this));
      this.func_177094_a(new LayerCustomHead(this.func_177087_b().field_78116_c));
      this.func_177094_a(new LayerElytra(this));
      this.func_177094_a(new LayerEntityOnShoulder(var1));
      this.func_177094_a(new LayerSpinAttackEffect(this));
   }

   public ModelPlayer func_177087_b() {
      return (ModelPlayer)super.func_177087_b();
   }

   public void func_76986_a(AbstractClientPlayer var1, double var2, double var4, double var6, float var8, float var9) {
      if (!var1.func_175144_cb() || this.field_76990_c.field_78734_h == var1) {
         double var10 = var4;
         if (var1.func_70093_af()) {
            var10 = var4 - 0.125D;
         }

         this.func_177137_d(var1);
         GlStateManager.func_187408_a(GlStateManager.Profile.PLAYER_SKIN);
         super.func_76986_a((EntityLivingBase)var1, var2, var10, var6, var8, var9);
         GlStateManager.func_187440_b(GlStateManager.Profile.PLAYER_SKIN);
      }
   }

   private void func_177137_d(AbstractClientPlayer var1) {
      ModelPlayer var2 = this.func_177087_b();
      if (var1.func_175149_v()) {
         var2.func_178719_a(false);
         var2.field_78116_c.field_78806_j = true;
         var2.field_178720_f.field_78806_j = true;
      } else {
         ItemStack var3 = var1.func_184614_ca();
         ItemStack var4 = var1.func_184592_cb();
         var2.func_178719_a(true);
         var2.field_178720_f.field_78806_j = var1.func_175148_a(EnumPlayerModelParts.HAT);
         var2.field_178730_v.field_78806_j = var1.func_175148_a(EnumPlayerModelParts.JACKET);
         var2.field_178733_c.field_78806_j = var1.func_175148_a(EnumPlayerModelParts.LEFT_PANTS_LEG);
         var2.field_178731_d.field_78806_j = var1.func_175148_a(EnumPlayerModelParts.RIGHT_PANTS_LEG);
         var2.field_178734_a.field_78806_j = var1.func_175148_a(EnumPlayerModelParts.LEFT_SLEEVE);
         var2.field_178732_b.field_78806_j = var1.func_175148_a(EnumPlayerModelParts.RIGHT_SLEEVE);
         var2.field_78117_n = var1.func_70093_af();
         ModelBiped.ArmPose var5 = this.func_212499_a(var1, var3);
         ModelBiped.ArmPose var6 = this.func_212499_a(var1, var4);
         if (var1.func_184591_cq() == EnumHandSide.RIGHT) {
            var2.field_187076_m = var5;
            var2.field_187075_l = var6;
         } else {
            var2.field_187076_m = var6;
            var2.field_187075_l = var5;
         }
      }

   }

   public ResourceLocation func_110775_a(AbstractClientPlayer var1) {
      return var1.func_110306_p();
   }

   protected void func_77041_b(AbstractClientPlayer var1, float var2) {
      float var3 = 0.9375F;
      GlStateManager.func_179152_a(0.9375F, 0.9375F, 0.9375F);
   }

   protected void func_188296_a(AbstractClientPlayer var1, double var2, double var4, double var6, String var8, double var9) {
      if (var9 < 100.0D) {
         Scoreboard var11 = var1.func_96123_co();
         ScoreObjective var12 = var11.func_96539_a(2);
         if (var12 != null) {
            Score var13 = var11.func_96529_a(var1.func_195047_I_(), var12);
            this.func_147906_a(var1, var13.func_96652_c() + " " + var12.func_96678_d().func_150254_d(), var2, var4, var6, 64);
            var4 += (double)((float)this.func_76983_a().field_78288_b * 1.15F * 0.025F);
         }
      }

      super.func_188296_a(var1, var2, var4, var6, var8, var9);
   }

   public void func_177138_b(AbstractClientPlayer var1) {
      float var2 = 1.0F;
      GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
      float var3 = 0.0625F;
      ModelPlayer var4 = this.func_177087_b();
      this.func_177137_d(var1);
      GlStateManager.func_179147_l();
      var4.field_78095_p = 0.0F;
      var4.field_78117_n = false;
      var4.field_205061_a = 0.0F;
      var4.func_78087_a(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, var1);
      var4.field_178723_h.field_78795_f = 0.0F;
      var4.field_178723_h.func_78785_a(0.0625F);
      var4.field_178732_b.field_78795_f = 0.0F;
      var4.field_178732_b.func_78785_a(0.0625F);
      GlStateManager.func_179084_k();
   }

   public void func_177139_c(AbstractClientPlayer var1) {
      float var2 = 1.0F;
      GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
      float var3 = 0.0625F;
      ModelPlayer var4 = this.func_177087_b();
      this.func_177137_d(var1);
      GlStateManager.func_179147_l();
      var4.field_78117_n = false;
      var4.field_78095_p = 0.0F;
      var4.field_205061_a = 0.0F;
      var4.func_78087_a(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, var1);
      var4.field_178724_i.field_78795_f = 0.0F;
      var4.field_178724_i.func_78785_a(0.0625F);
      var4.field_178734_a.field_78795_f = 0.0F;
      var4.field_178734_a.func_78785_a(0.0625F);
      GlStateManager.func_179084_k();
   }

   protected void func_77039_a(AbstractClientPlayer var1, double var2, double var4, double var6) {
      if (var1.func_70089_S() && var1.func_70608_bn()) {
         super.func_77039_a(var1, var2 + (double)var1.field_71079_bU, var4 + (double)var1.field_71082_cx, var6 + (double)var1.field_71089_bV);
      } else {
         super.func_77039_a(var1, var2, var4, var6);
      }

   }

   protected void func_77043_a(AbstractClientPlayer var1, float var2, float var3, float var4) {
      float var5 = var1.func_205015_b(var4);
      if (var1.func_70089_S() && var1.func_70608_bn()) {
         GlStateManager.func_179114_b(var1.func_71051_bG(), 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(this.func_77037_a(var1), 0.0F, 0.0F, 1.0F);
         GlStateManager.func_179114_b(270.0F, 0.0F, 1.0F, 0.0F);
      } else {
         float var6;
         if (var1.func_184613_cA()) {
            super.func_77043_a(var1, var2, var3, var4);
            var6 = (float)var1.func_184599_cB() + var4;
            float var7 = MathHelper.func_76131_a(var6 * var6 / 100.0F, 0.0F, 1.0F);
            if (!var1.func_204805_cN()) {
               GlStateManager.func_179114_b(var7 * (-90.0F - var1.field_70125_A), 1.0F, 0.0F, 0.0F);
            }

            Vec3d var8 = var1.func_70676_i(var4);
            double var9 = var1.field_70159_w * var1.field_70159_w + var1.field_70179_y * var1.field_70179_y;
            double var11 = var8.field_72450_a * var8.field_72450_a + var8.field_72449_c * var8.field_72449_c;
            if (var9 > 0.0D && var11 > 0.0D) {
               double var13 = (var1.field_70159_w * var8.field_72450_a + var1.field_70179_y * var8.field_72449_c) / (Math.sqrt(var9) * Math.sqrt(var11));
               double var15 = var1.field_70159_w * var8.field_72449_c - var1.field_70179_y * var8.field_72450_a;
               GlStateManager.func_179114_b((float)(Math.signum(var15) * Math.acos(var13)) * 180.0F / 3.1415927F, 0.0F, 1.0F, 0.0F);
            }
         } else if (var5 > 0.0F) {
            super.func_77043_a(var1, var2, var3, var4);
            var6 = this.func_205126_b(var1.field_70125_A, -90.0F - var1.field_70125_A, var5);
            if (!var1.func_203007_ba()) {
               var6 = this.func_77034_a(this.field_205127_a, 0.0F, 1.0F - var5);
            }

            GlStateManager.func_179114_b(var6, 1.0F, 0.0F, 0.0F);
            if (var1.func_203007_ba()) {
               this.field_205127_a = var6;
               GlStateManager.func_179109_b(0.0F, -1.0F, 0.3F);
            }
         } else {
            super.func_77043_a(var1, var2, var3, var4);
         }
      }

   }

   private float func_205126_b(float var1, float var2, float var3) {
      return var1 + (var2 - var1) * var3;
   }

   private ModelBiped.ArmPose func_212499_a(AbstractClientPlayer var1, ItemStack var2) {
      if (var2.func_190926_b()) {
         return ModelBiped.ArmPose.EMPTY;
      } else {
         if (var1.func_184605_cv() > 0) {
            EnumAction var3 = var2.func_77975_n();
            if (var3 == EnumAction.BLOCK) {
               return ModelBiped.ArmPose.BLOCK;
            }

            if (var3 == EnumAction.BOW) {
               return ModelBiped.ArmPose.BOW_AND_ARROW;
            }

            if (var3 == EnumAction.SPEAR) {
               return ModelBiped.ArmPose.THROW_SPEAR;
            }
         }

         return ModelBiped.ArmPose.ITEM;
      }
   }

   // $FF: synthetic method
   public ModelBase func_177087_b() {
      return this.func_177087_b();
   }
}
