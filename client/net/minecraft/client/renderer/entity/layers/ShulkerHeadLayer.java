package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;

public class ShulkerHeadLayer extends RenderLayer<Shulker, ShulkerModel<Shulker>> {
   public ShulkerHeadLayer(RenderLayerParent<Shulker, ShulkerModel<Shulker>> var1) {
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
      DyeColor var12 = var4.getColor();
      ResourceLocation var13 = var12 == null ? ShulkerRenderer.DEFAULT_TEXTURE_LOCATION : ShulkerRenderer.TEXTURE_LOCATION[var12.getId()];
      VertexConsumer var14 = var2.getBuffer(RenderType.entitySolid(var13));
      ((ShulkerModel)this.getParentModel()).getHead().render(var1, var14, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F));
      var1.popPose();
   }
}
