package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;

public class ShulkerHeadLayer extends RenderLayer {
   public ShulkerHeadLayer(RenderLayerParent var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Shulker var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      var1.pushPose();
      var1.translate(0.0D, 1.0D, 0.0D);
      var1.scale(-1.0F, -1.0F, 1.0F);
      Quaternion var11 = var4.getAttachFace().getOpposite().getRotation();
      var11.conj();
      var1.mulPose(var11);
      var1.scale(-1.0F, -1.0F, 1.0F);
      var1.translate(0.0D, -1.0D, 0.0D);
      ModelPart var12 = ((ShulkerModel)this.getParentModel()).getHead();
      var12.yRot = var9 * 0.017453292F;
      var12.xRot = var10 * 0.017453292F;
      DyeColor var13 = var4.getColor();
      ResourceLocation var14;
      if (var13 == null) {
         var14 = ShulkerRenderer.DEFAULT_TEXTURE_LOCATION;
      } else {
         var14 = ShulkerRenderer.TEXTURE_LOCATION[var13.getId()];
      }

      VertexConsumer var15 = var2.getBuffer(RenderType.entitySolid(var14));
      var12.render(var1, var15, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F));
      var1.popPose();
   }
}
