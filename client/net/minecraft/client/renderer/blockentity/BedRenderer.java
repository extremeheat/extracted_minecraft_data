package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.client.model.BedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class BedRenderer extends BlockEntityRenderer<BedBlockEntity> {
   private static final ResourceLocation[] TEXTURES = (ResourceLocation[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map((var0) -> {
      return new ResourceLocation("textures/entity/bed/" + var0.getName() + ".png");
   }).toArray((var0) -> {
      return new ResourceLocation[var0];
   });
   private final BedModel bedModel = new BedModel();

   public BedRenderer() {
      super();
   }

   public void render(BedBlockEntity var1, double var2, double var4, double var6, float var8, int var9) {
      if (var9 >= 0) {
         this.bindTexture(BREAKING_LOCATIONS[var9]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 4.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         ResourceLocation var10 = TEXTURES[var1.getColor().getId()];
         if (var10 != null) {
            this.bindTexture(var10);
         }
      }

      if (var1.hasLevel()) {
         BlockState var11 = var1.getBlockState();
         this.renderPiece(var11.getValue(BedBlock.PART) == BedPart.HEAD, var2, var4, var6, (Direction)var11.getValue(BedBlock.FACING));
      } else {
         this.renderPiece(true, var2, var4, var6, Direction.SOUTH);
         this.renderPiece(false, var2, var4, var6 - 1.0D, Direction.SOUTH);
      }

      if (var9 >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

   }

   private void renderPiece(boolean var1, double var2, double var4, double var6, Direction var8) {
      this.bedModel.preparePiece(var1);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var2, (float)var4 + 0.5625F, (float)var6);
      GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translatef(0.5F, 0.5F, 0.5F);
      GlStateManager.rotatef(180.0F + var8.toYRot(), 0.0F, 0.0F, 1.0F);
      GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
      GlStateManager.enableRescaleNormal();
      this.bedModel.render();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }
}
