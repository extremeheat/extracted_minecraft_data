package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.BreezeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BreezeRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.breeze.Breeze;

public class BreezeEyesLayer extends RenderLayer<Breeze, BreezeModel<Breeze>> {
   private static final RenderType BREEZE_EYES = RenderType.breezeEyes(ResourceLocation.withDefaultNamespace("textures/entity/breeze/breeze_eyes.png"));

   public BreezeEyesLayer(RenderLayerParent<Breeze, BreezeModel<Breeze>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Breeze var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      VertexConsumer var11 = var2.getBuffer(BREEZE_EYES);
      BreezeModel var12 = (BreezeModel)this.getParentModel();
      BreezeRenderer.enable(var12, var12.head(), var12.eyes()).renderToBuffer(var1, var11, var3, OverlayTexture.NO_OVERLAY);
   }
}
