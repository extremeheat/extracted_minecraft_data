package net.minecraft.client.renderer.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;

public class RenderPlayer extends RendererLivingEntity<AbstractClientPlayer> {
   private boolean field_177140_a;

   public RenderPlayer(RenderManager var1) {
      this(var1, false);
   }

   public RenderPlayer(RenderManager var1, boolean var2) {
      super(var1, new ModelPlayer(0.0F, var2), 0.5F);
      this.field_177140_a = var2;
      this.func_177094_a(new LayerBipedArmor(this));
      this.func_177094_a(new LayerHeldItem(this));
      this.func_177094_a(new LayerArrow(this));
      this.func_177094_a(new LayerDeadmau5Head(this));
      this.func_177094_a(new LayerCape(this));
      this.func_177094_a(new LayerCustomHead(this.func_177087_b().field_78116_c));
   }

   public ModelPlayer func_177087_b() {
      return (ModelPlayer)super.func_177087_b();
   }

   public void func_76986_a(AbstractClientPlayer var1, double var2, double var4, double var6, float var8, float var9) {
      if (!var1.func_175144_cb() || this.field_76990_c.field_78734_h == var1) {
         double var10 = var4;
         if (var1.func_70093_af() && !(var1 instanceof EntityPlayerSP)) {
            var10 = var4 - 0.125D;
         }

         this.func_177137_d(var1);
         super.func_76986_a((EntityLivingBase)var1, var2, var10, var6, var8, var9);
      }
   }

   private void func_177137_d(AbstractClientPlayer var1) {
      ModelPlayer var2 = this.func_177087_b();
      if (var1.func_175149_v()) {
         var2.func_178719_a(false);
         var2.field_78116_c.field_78806_j = true;
         var2.field_178720_f.field_78806_j = true;
      } else {
         ItemStack var3 = var1.field_71071_by.func_70448_g();
         var2.func_178719_a(true);
         var2.field_178720_f.field_78806_j = var1.func_175148_a(EnumPlayerModelParts.HAT);
         var2.field_178730_v.field_78806_j = var1.func_175148_a(EnumPlayerModelParts.JACKET);
         var2.field_178733_c.field_78806_j = var1.func_175148_a(EnumPlayerModelParts.LEFT_PANTS_LEG);
         var2.field_178731_d.field_78806_j = var1.func_175148_a(EnumPlayerModelParts.RIGHT_PANTS_LEG);
         var2.field_178734_a.field_78806_j = var1.func_175148_a(EnumPlayerModelParts.LEFT_SLEEVE);
         var2.field_178732_b.field_78806_j = var1.func_175148_a(EnumPlayerModelParts.RIGHT_SLEEVE);
         var2.field_78119_l = 0;
         var2.field_78118_o = false;
         var2.field_78117_n = var1.func_70093_af();
         if (var3 == null) {
            var2.field_78120_m = 0;
         } else {
            var2.field_78120_m = 1;
            if (var1.func_71052_bv() > 0) {
               EnumAction var4 = var3.func_77975_n();
               if (var4 == EnumAction.BLOCK) {
                  var2.field_78120_m = 3;
               } else if (var4 == EnumAction.BOW) {
                  var2.field_78118_o = true;
               }
            }
         }
      }

   }

   protected ResourceLocation func_110775_a(AbstractClientPlayer var1) {
      return var1.func_110306_p();
   }

   public void func_82422_c() {
      GlStateManager.func_179109_b(0.0F, 0.1875F, 0.0F);
   }

   protected void func_77041_b(AbstractClientPlayer var1, float var2) {
      float var3 = 0.9375F;
      GlStateManager.func_179152_a(var3, var3, var3);
   }

   protected void func_177069_a(AbstractClientPlayer var1, double var2, double var4, double var6, String var8, float var9, double var10) {
      if (var10 < 100.0D) {
         Scoreboard var12 = var1.func_96123_co();
         ScoreObjective var13 = var12.func_96539_a(2);
         if (var13 != null) {
            Score var14 = var12.func_96529_a(var1.func_70005_c_(), var13);
            this.func_147906_a(var1, var14.func_96652_c() + " " + var13.func_96678_d(), var2, var4, var6, 64);
            var4 += (double)((float)this.func_76983_a().field_78288_b * 1.15F * var9);
         }
      }

      super.func_177069_a(var1, var2, var4, var6, var8, var9, var10);
   }

   public void func_177138_b(AbstractClientPlayer var1) {
      float var2 = 1.0F;
      GlStateManager.func_179124_c(var2, var2, var2);
      ModelPlayer var3 = this.func_177087_b();
      this.func_177137_d(var1);
      var3.field_78095_p = 0.0F;
      var3.field_78117_n = false;
      var3.func_78087_a(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, var1);
      var3.func_178725_a();
   }

   public void func_177139_c(AbstractClientPlayer var1) {
      float var2 = 1.0F;
      GlStateManager.func_179124_c(var2, var2, var2);
      ModelPlayer var3 = this.func_177087_b();
      this.func_177137_d(var1);
      var3.field_78117_n = false;
      var3.field_78095_p = 0.0F;
      var3.func_78087_a(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, var1);
      var3.func_178726_b();
   }

   protected void func_77039_a(AbstractClientPlayer var1, double var2, double var4, double var6) {
      if (var1.func_70089_S() && var1.func_70608_bn()) {
         super.func_77039_a(var1, var2 + (double)var1.field_71079_bU, var4 + (double)var1.field_71082_cx, var6 + (double)var1.field_71089_bV);
      } else {
         super.func_77039_a(var1, var2, var4, var6);
      }

   }

   protected void func_77043_a(AbstractClientPlayer var1, float var2, float var3, float var4) {
      if (var1.func_70089_S() && var1.func_70608_bn()) {
         GlStateManager.func_179114_b(var1.func_71051_bG(), 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(this.func_77037_a(var1), 0.0F, 0.0F, 1.0F);
         GlStateManager.func_179114_b(270.0F, 0.0F, 1.0F, 0.0F);
      } else {
         super.func_77043_a(var1, var2, var3, var4);
      }

   }

   // $FF: synthetic method
   public ModelBase func_177087_b() {
      return this.func_177087_b();
   }
}
