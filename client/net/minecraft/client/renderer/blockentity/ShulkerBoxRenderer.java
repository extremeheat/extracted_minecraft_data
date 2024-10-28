package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ShulkerBoxRenderer implements BlockEntityRenderer<ShulkerBoxBlockEntity> {
   private final ShulkerModel<?> model;

   public ShulkerBoxRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.model = new ShulkerModel(var1.bakeLayer(ModelLayers.SHULKER));
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
      Material var13;
      if (var9 == null) {
         var13 = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
      } else {
         var13 = (Material)Sheets.SHULKER_TEXTURE_LOCATION.get(var9.getId());
      }

      var3.pushPose();
      var3.translate(0.5F, 0.5F, 0.5F);
      float var10 = 0.9995F;
      var3.scale(0.9995F, 0.9995F, 0.9995F);
      var3.mulPose(var7.getRotation());
      var3.scale(1.0F, -1.0F, -1.0F);
      var3.translate(0.0F, -1.0F, 0.0F);
      ModelPart var11 = this.model.getLid();
      var11.setPos(0.0F, 24.0F - var1.getProgress(var2) * 0.5F * 16.0F, 0.0F);
      var11.yRot = 270.0F * var1.getProgress(var2) * 0.017453292F;
      VertexConsumer var12 = var13.buffer(var4, RenderType::entityCutoutNoCull);
      this.model.renderToBuffer(var3, var12, var5, var6);
      var3.popPose();
   }
}
