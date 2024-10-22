package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
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
   private final ShulkerBoxRenderer.ShulkerBoxModel model;

   public ShulkerBoxRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.model = new ShulkerBoxRenderer.ShulkerBoxModel(var1.bakeLayer(ModelLayers.SHULKER_BOX));
   }

   public void render(ShulkerBoxBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      Direction var7 = Direction.UP;
      if (var1.hasLevel()) {
         BlockState var8 = var1.getLevel().getBlockState(var1.getBlockPos());
         if (var8.getBlock() instanceof ShulkerBoxBlock) {
            var7 = var8.getValue(ShulkerBoxBlock.FACING);
         }
      }

      DyeColor var9 = var1.getColor();
      Material var12;
      if (var9 == null) {
         var12 = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
      } else {
         var12 = Sheets.SHULKER_TEXTURE_LOCATION.get(var9.getId());
      }

      var3.pushPose();
      var3.translate(0.5F, 0.5F, 0.5F);
      float var10 = 0.9995F;
      var3.scale(0.9995F, 0.9995F, 0.9995F);
      var3.mulPose(var7.getRotation());
      var3.scale(1.0F, -1.0F, -1.0F);
      var3.translate(0.0F, -1.0F, 0.0F);
      this.model.animate(var1, var2);
      VertexConsumer var11 = var12.buffer(var4, this.model::renderType);
      this.model.renderToBuffer(var3, var11, var5, var6);
      var3.popPose();
   }

   static class ShulkerBoxModel extends Model {
      private final ModelPart lid;

      public ShulkerBoxModel(ModelPart var1) {
         super(var1, RenderType::entityCutoutNoCull);
         this.lid = var1.getChild("lid");
      }

      public void animate(ShulkerBoxBlockEntity var1, float var2) {
         this.lid.setPos(0.0F, 24.0F - var1.getProgress(var2) * 0.5F * 16.0F, 0.0F);
         this.lid.yRot = 270.0F * var1.getProgress(var2) * 0.017453292F;
      }
   }
}
