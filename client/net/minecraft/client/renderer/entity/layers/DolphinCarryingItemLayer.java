package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.DolphinModel;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.block.Block;

public class DolphinCarryingItemLayer extends RenderLayer<Dolphin, DolphinModel<Dolphin>> {
   private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

   public DolphinCarryingItemLayer(RenderLayerParent<Dolphin, DolphinModel<Dolphin>> var1) {
      super(var1);
   }

   public void render(Dolphin var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      boolean var9 = var1.getMainArm() == HumanoidArm.RIGHT;
      ItemStack var10 = var9 ? var1.getOffhandItem() : var1.getMainHandItem();
      ItemStack var11 = var9 ? var1.getMainHandItem() : var1.getOffhandItem();
      if (!var10.isEmpty() || !var11.isEmpty()) {
         this.renderItemOnNose(var1, var11);
      }
   }

   private void renderItemOnNose(LivingEntity var1, ItemStack var2) {
      if (!var2.isEmpty()) {
         Item var3 = var2.getItem();
         Block var4 = Block.byItem(var3);
         GlStateManager.pushMatrix();
         boolean var5 = this.itemRenderer.isGui3d(var2) && var4.getRenderLayer() == BlockLayer.TRANSLUCENT;
         if (var5) {
            GlStateManager.depthMask(false);
         }

         float var6 = 1.0F;
         float var7 = -1.0F;
         float var8 = Mth.abs(var1.xRot) / 60.0F;
         if (var1.xRot < 0.0F) {
            GlStateManager.translatef(0.0F, 1.0F - var8 * 0.5F, -1.0F + var8 * 0.5F);
         } else {
            GlStateManager.translatef(0.0F, 1.0F + var8 * 0.8F, -1.0F + var8 * 0.2F);
         }

         this.itemRenderer.renderWithMobState(var2, var1, ItemTransforms.TransformType.GROUND, false);
         if (var5) {
            GlStateManager.depthMask(true);
         }

         GlStateManager.popMatrix();
      }
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
