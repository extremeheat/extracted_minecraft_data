package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BrushableBlockRenderer implements BlockEntityRenderer<BrushableBlockEntity> {
   private final ItemRenderer itemRenderer;

   public BrushableBlockRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.itemRenderer = var1.getItemRenderer();
   }

   public void render(BrushableBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      if (var1.getLevel() != null) {
         int var7 = var1.getBlockState().getValue(BlockStateProperties.DUSTED);
         if (var7 > 0) {
            Direction var8 = var1.getHitDirection();
            if (var8 != null) {
               ItemStack var9 = var1.getItem();
               if (!var9.isEmpty()) {
                  var3.pushPose();
                  var3.translate(0.0F, 0.5F, 0.0F);
                  float[] var10 = this.translations(var8, var7);
                  var3.translate(var10[0], var10[1], var10[2]);
                  var3.mulPose(Axis.YP.rotationDegrees(75.0F));
                  boolean var11 = var8 == Direction.EAST || var8 == Direction.WEST;
                  var3.mulPose(Axis.YP.rotationDegrees((float)((var11 ? 90 : 0) + 11)));
                  var3.scale(0.5F, 0.5F, 0.5F);
                  int var12 = LevelRenderer.getLightColor(var1.getLevel(), var1.getBlockState(), var1.getBlockPos().relative(var8));
                  this.itemRenderer.renderStatic(var9, ItemDisplayContext.FIXED, var12, OverlayTexture.NO_OVERLAY, var3, var4, var1.getLevel(), 0);
                  var3.popPose();
               }
            }
         }
      }
   }

   private float[] translations(Direction var1, int var2) {
      float[] var3 = new float[]{0.5F, 0.0F, 0.5F};
      float var4 = (float)var2 / 10.0F * 0.75F;
      switch (var1) {
         case EAST:
            var3[0] = 0.73F + var4;
            break;
         case WEST:
            var3[0] = 0.25F - var4;
            break;
         case UP:
            var3[1] = 0.25F + var4;
            break;
         case DOWN:
            var3[1] = -0.23F - var4;
            break;
         case NORTH:
            var3[2] = 0.25F - var4;
            break;
         case SOUTH:
            var3[2] = 0.73F + var4;
      }

      return var3;
   }
}
