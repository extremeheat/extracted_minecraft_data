package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Objects;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
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

public class ShulkerBoxRenderer implements BlockEntityRenderer<ShulkerBoxBlockEntity> {
   private final ShulkerBoxModel model;

   public ShulkerBoxRenderer(BlockEntityRendererProvider.Context var1) {
      this(var1.getModelSet());
   }

   public ShulkerBoxRenderer(EntityModelSet var1) {
      super();
      this.model = new ShulkerBoxModel(var1.bakeLayer(ModelLayers.SHULKER_BOX));
   }

   public void render(ShulkerBoxBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      Direction var7 = (Direction)var1.getBlockState().getValueOrElse(ShulkerBoxBlock.FACING, Direction.UP);
      DyeColor var8 = var1.getColor();
      Material var9;
      if (var8 == null) {
         var9 = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
      } else {
         var9 = Sheets.getShulkerBoxMaterial(var8);
      }

      float var10 = var1.getProgress(var2);
      this.render(var3, var4, var5, var6, var7, var10, var9);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, int var4, Direction var5, float var6, Material var7) {
      var1.pushPose();
      var1.translate(0.5F, 0.5F, 0.5F);
      float var8 = 0.9995F;
      var1.scale(0.9995F, 0.9995F, 0.9995F);
      var1.mulPose(var5.getRotation());
      var1.scale(1.0F, -1.0F, -1.0F);
      var1.translate(0.0F, -1.0F, 0.0F);
      this.model.animate(var6);
      ShulkerBoxModel var10002 = this.model;
      Objects.requireNonNull(var10002);
      VertexConsumer var9 = var7.buffer(var2, var10002::renderType);
      this.model.renderToBuffer(var1, var9, var3, var4);
      var1.popPose();
   }

   static class ShulkerBoxModel extends Model {
      private final ModelPart lid;

      public ShulkerBoxModel(ModelPart var1) {
         super(var1, RenderType::entityCutoutNoCull);
         this.lid = var1.getChild("lid");
      }

      public void animate(float var1) {
         this.lid.setPos(0.0F, 24.0F - var1 * 0.5F * 16.0F, 0.0F);
         this.lid.yRot = 270.0F * var1 * 0.017453292F;
      }
   }
}
