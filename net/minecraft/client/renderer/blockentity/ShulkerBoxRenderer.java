package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ShulkerBoxRenderer extends BlockEntityRenderer {
   private final ShulkerModel model;

   public ShulkerBoxRenderer(ShulkerModel var1, BlockEntityRenderDispatcher var2) {
      super(var2);
      this.model = var1;
   }

   public void render(ShulkerBoxBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      Direction var7 = Direction.UP;
      if (var1.hasLevel()) {
         BlockState var8 = var1.getLevel().getBlockState(var1.getBlockPos());
         if (var8.getBlock() instanceof ShulkerBoxBlock) {
            var7 = (Direction)var8.getValue(ShulkerBoxBlock.FACING);
         }
      }

      DyeColor var9 = var1.getColor();
      Material var12;
      if (var9 == null) {
         var12 = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
      } else {
         var12 = (Material)Sheets.SHULKER_TEXTURE_LOCATION.get(var9.getId());
      }

      var3.pushPose();
      var3.translate(0.5D, 0.5D, 0.5D);
      float var10 = 0.9995F;
      var3.scale(0.9995F, 0.9995F, 0.9995F);
      var3.mulPose(var7.getRotation());
      var3.scale(1.0F, -1.0F, -1.0F);
      var3.translate(0.0D, -1.0D, 0.0D);
      VertexConsumer var11 = var12.buffer(var4, RenderType::entityCutoutNoCull);
      this.model.getBase().render(var3, var11, var5, var6);
      var3.translate(0.0D, (double)(-var1.getProgress(var2) * 0.5F), 0.0D);
      var3.mulPose(Vector3f.YP.rotationDegrees(270.0F * var1.getProgress(var2)));
      this.model.getLid().render(var3, var11, var5, var6);
      var3.popPose();
   }
}
