package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;

public class WolfCollarLayer extends RenderLayer<Wolf, WolfModel<Wolf>> {
   private static final ResourceLocation WOLF_COLLAR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_collar.png");

   public WolfCollarLayer(RenderLayerParent<Wolf, WolfModel<Wolf>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Wolf var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (var4.isTame() && !var4.isInvisible()) {
         int var11 = var4.getCollarColor().getTextureDiffuseColor();
         VertexConsumer var12 = var2.getBuffer(RenderType.entityCutoutNoCull(WOLF_COLLAR_LOCATION));
         ((WolfModel)this.getParentModel()).renderToBuffer(var1, var12, var3, OverlayTexture.NO_OVERLAY, var11);
      }
   }
}
