package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class LayerCape implements LayerRenderer<AbstractClientPlayer> {
   private final RenderPlayer field_177167_a;

   public LayerCape(RenderPlayer var1) {
      super();
      this.field_177167_a = var1;
   }

   public void func_177141_a(AbstractClientPlayer var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.func_152122_n() && !var1.func_82150_aj() && var1.func_175148_a(EnumPlayerModelParts.CAPE) && var1.func_110303_q() != null) {
         ItemStack var9 = var1.func_184582_a(EntityEquipmentSlot.CHEST);
         if (var9.func_77973_b() != Items.field_185160_cR) {
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_177167_a.func_110776_a(var1.func_110303_q());
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b(0.0F, 0.0F, 0.125F);
            double var10 = var1.field_71091_bM + (var1.field_71094_bP - var1.field_71091_bM) * (double)var4 - (var1.field_70169_q + (var1.field_70165_t - var1.field_70169_q) * (double)var4);
            double var12 = var1.field_71096_bN + (var1.field_71095_bQ - var1.field_71096_bN) * (double)var4 - (var1.field_70167_r + (var1.field_70163_u - var1.field_70167_r) * (double)var4);
            double var14 = var1.field_71097_bO + (var1.field_71085_bR - var1.field_71097_bO) * (double)var4 - (var1.field_70166_s + (var1.field_70161_v - var1.field_70166_s) * (double)var4);
            float var16 = var1.field_70760_ar + (var1.field_70761_aq - var1.field_70760_ar);
            double var17 = (double)MathHelper.func_76126_a(var16 * 0.017453292F);
            double var19 = (double)(-MathHelper.func_76134_b(var16 * 0.017453292F));
            float var21 = (float)var12 * 10.0F;
            var21 = MathHelper.func_76131_a(var21, -6.0F, 32.0F);
            float var22 = (float)(var10 * var17 + var14 * var19) * 100.0F;
            var22 = MathHelper.func_76131_a(var22, 0.0F, 150.0F);
            float var23 = (float)(var10 * var19 - var14 * var17) * 100.0F;
            var23 = MathHelper.func_76131_a(var23, -20.0F, 20.0F);
            if (var22 < 0.0F) {
               var22 = 0.0F;
            }

            float var24 = var1.field_71107_bF + (var1.field_71109_bG - var1.field_71107_bF) * var4;
            var21 += MathHelper.func_76126_a((var1.field_70141_P + (var1.field_70140_Q - var1.field_70141_P) * var4) * 6.0F) * 32.0F * var24;
            if (var1.func_70093_af()) {
               var21 += 25.0F;
            }

            GlStateManager.func_179114_b(6.0F + var22 / 2.0F + var21, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179114_b(var23 / 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.func_179114_b(-var23 / 2.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
            this.field_177167_a.func_177087_b().func_178728_c(0.0625F);
            GlStateManager.func_179121_F();
         }
      }
   }

   public boolean func_177142_b() {
      return false;
   }
}
