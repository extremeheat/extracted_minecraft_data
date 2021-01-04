package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ShulkerBoxRenderer extends BlockEntityRenderer<ShulkerBoxBlockEntity> {
   private final ShulkerModel<?> model;

   public ShulkerBoxRenderer(ShulkerModel<?> var1) {
      super();
      this.model = var1;
   }

   public void render(ShulkerBoxBlockEntity var1, double var2, double var4, double var6, float var8, int var9) {
      Direction var10 = Direction.UP;
      if (var1.hasLevel()) {
         BlockState var11 = this.getLevel().getBlockState(var1.getBlockPos());
         if (var11.getBlock() instanceof ShulkerBoxBlock) {
            var10 = (Direction)var11.getValue(ShulkerBoxBlock.FACING);
         }
      }

      GlStateManager.enableDepthTest();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      GlStateManager.disableCull();
      if (var9 >= 0) {
         this.bindTexture(BREAKING_LOCATIONS[var9]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 4.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         DyeColor var12 = var1.getColor();
         if (var12 == null) {
            this.bindTexture(ShulkerRenderer.DEFAULT_TEXTURE_LOCATION);
         } else {
            this.bindTexture(ShulkerRenderer.TEXTURE_LOCATION[var12.getId()]);
         }
      }

      GlStateManager.pushMatrix();
      GlStateManager.enableRescaleNormal();
      if (var9 < 0) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 1.5F, (float)var6 + 0.5F);
      GlStateManager.scalef(1.0F, -1.0F, -1.0F);
      GlStateManager.translatef(0.0F, 1.0F, 0.0F);
      float var13 = 0.9995F;
      GlStateManager.scalef(0.9995F, 0.9995F, 0.9995F);
      GlStateManager.translatef(0.0F, -1.0F, 0.0F);
      switch(var10) {
      case DOWN:
         GlStateManager.translatef(0.0F, 2.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
      case UP:
      default:
         break;
      case NORTH:
         GlStateManager.translatef(0.0F, 1.0F, 1.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         break;
      case SOUTH:
         GlStateManager.translatef(0.0F, 1.0F, -1.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         break;
      case WEST:
         GlStateManager.translatef(-1.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
         break;
      case EAST:
         GlStateManager.translatef(1.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
      }

      this.model.getBase().render(0.0625F);
      GlStateManager.translatef(0.0F, -var1.getProgress(var8) * 0.5F, 0.0F);
      GlStateManager.rotatef(270.0F * var1.getProgress(var8), 0.0F, 1.0F, 0.0F);
      this.model.getLid().render(0.0625F);
      GlStateManager.enableCull();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (var9 >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

   }
}
