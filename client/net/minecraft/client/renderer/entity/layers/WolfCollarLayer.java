package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class WolfCollarLayer extends RenderLayer<WolfRenderState, WolfModel> {
   private static final ResourceLocation WOLF_COLLAR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_collar.png");

   public WolfCollarLayer(RenderLayerParent<WolfRenderState, WolfModel> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, WolfRenderState var4, float var5, float var6) {
      DyeColor var7 = var4.collarColor;
      if (var7 != null && !var4.isInvisible) {
         int var8 = var7.getTextureDiffuseColor();
         VertexConsumer var9 = var2.getBuffer(RenderType.entityCutoutNoCull(WOLF_COLLAR_LOCATION));
         this.getParentModel().renderToBuffer(var1, var9, var3, OverlayTexture.NO_OVERLAY, var8);
      }
   }
}
