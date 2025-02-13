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
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class BrushableBlockRenderer implements BlockEntityRenderer<BrushableBlockEntity> {
   private final ItemRenderer itemRenderer;

   public BrushableBlockRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.itemRenderer = var1.getItemRenderer();
   }

   public void render(BrushableBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      if (var1.getLevel() != null) {
         int var8 = (Integer)var1.getBlockState().getValue(BlockStateProperties.DUSTED);
         if (var8 > 0) {
            Direction var9 = var1.getHitDirection();
            if (var9 != null) {
               ItemStack var10 = var1.getItem();
               if (!var10.isEmpty()) {
                  var3.pushPose();
                  var3.translate(0.0F, 0.5F, 0.0F);
                  float[] var11 = this.translations(var9, var8);
                  var3.translate(var11[0], var11[1], var11[2]);
                  var3.mulPose((Quaternionfc)Axis.YP.rotationDegrees(75.0F));
                  boolean var12 = var9 == Direction.EAST || var9 == Direction.WEST;
                  var3.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)((var12 ? 90 : 0) + 11)));
                  var3.scale(0.5F, 0.5F, 0.5F);
                  int var13 = LevelRenderer.getLightColor(var1.getLevel(), var1.getBlockState(), var1.getBlockPos().relative(var9));
                  this.itemRenderer.renderStatic(var10, ItemDisplayContext.FIXED, var13, OverlayTexture.NO_OVERLAY, var3, var4, var1.getLevel(), 0);
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
         case EAST -> var3[0] = 0.73F + var4;
         case WEST -> var3[0] = 0.25F - var4;
         case UP -> var3[1] = 0.25F + var4;
         case DOWN -> var3[1] = -0.23F - var4;
         case NORTH -> var3[2] = 0.25F - var4;
         case SOUTH -> var3[2] = 0.73F + var4;
      }

      return var3;
   }
}
