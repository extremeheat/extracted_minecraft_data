package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;

public class CampfireRenderer extends BlockEntityRenderer<CampfireBlockEntity> {
   public CampfireRenderer() {
      super();
   }

   public void render(CampfireBlockEntity var1, double var2, double var4, double var6, float var8, int var9) {
      Direction var10 = (Direction)var1.getBlockState().getValue(CampfireBlock.FACING);
      NonNullList var11 = var1.getItems();

      for(int var12 = 0; var12 < var11.size(); ++var12) {
         ItemStack var13 = (ItemStack)var11.get(var12);
         if (var13 != ItemStack.EMPTY) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.44921875F, (float)var6 + 0.5F);
            Direction var14 = Direction.from2DDataValue((var12 + var10.get2DDataValue()) % 4);
            GlStateManager.rotatef(-var14.toYRot(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(-0.3125F, -0.3125F, 0.0F);
            GlStateManager.scalef(0.375F, 0.375F, 0.375F);
            Minecraft.getInstance().getItemRenderer().renderStatic(var13, ItemTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
         }
      }

   }
}
