package net.minecraft.client.renderer.entity.layers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class LayerDolphinCarriedItem implements LayerRenderer<EntityLivingBase> {
   protected final RenderLivingBase<?> field_205130_a;
   private final ItemRenderer field_205131_b;

   public LayerDolphinCarriedItem(RenderLivingBase<?> var1) {
      super();
      this.field_205130_a = var1;
      this.field_205131_b = Minecraft.func_71410_x().func_175599_af();
   }

   public void func_177141_a(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      boolean var9 = var1.func_184591_cq() == EnumHandSide.RIGHT;
      ItemStack var10 = var9 ? var1.func_184592_cb() : var1.func_184614_ca();
      ItemStack var11 = var9 ? var1.func_184614_ca() : var1.func_184592_cb();
      if (!var10.func_190926_b() || !var11.func_190926_b()) {
         this.func_205129_a(var1, var11);
      }
   }

   private void func_205129_a(EntityLivingBase var1, ItemStack var2) {
      if (!var2.func_190926_b()) {
         if (!var2.func_190926_b()) {
            Item var3 = var2.func_77973_b();
            Block var4 = Block.func_149634_a(var3);
            GlStateManager.func_179094_E();
            boolean var5 = this.field_205131_b.func_175050_a(var2) && var4.func_180664_k() == BlockRenderLayer.TRANSLUCENT;
            if (var5) {
               GlStateManager.func_179132_a(false);
            }

            float var6 = 1.0F;
            float var7 = -1.0F;
            float var8 = MathHelper.func_76135_e(var1.field_70125_A) / 60.0F;
            if (var1.field_70125_A < 0.0F) {
               GlStateManager.func_179109_b(0.0F, 1.0F - var8 * 0.5F, -1.0F + var8 * 0.5F);
            } else {
               GlStateManager.func_179109_b(0.0F, 1.0F + var8 * 0.8F, -1.0F + var8 * 0.2F);
            }

            this.field_205131_b.func_184392_a(var2, var1, ItemCameraTransforms.TransformType.GROUND, false);
            if (var5) {
               GlStateManager.func_179132_a(true);
            }

            GlStateManager.func_179121_F();
         }
      }
   }

   public boolean func_177142_b() {
      return false;
   }
}
