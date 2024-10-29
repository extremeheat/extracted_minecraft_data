package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SaddleableRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class SaddleLayer<S extends LivingEntityRenderState & SaddleableRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
   private final ResourceLocation textureLocation;
   private final M adultModel;
   private final M babyModel;

   public SaddleLayer(RenderLayerParent<S, M> var1, M var2, M var3, ResourceLocation var4) {
      super(var1);
      this.adultModel = var2;
      this.babyModel = var3;
      this.textureLocation = var4;
   }

   public SaddleLayer(RenderLayerParent<S, M> var1, M var2, ResourceLocation var3) {
      this(var1, var2, var2, var3);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6) {
      if (((SaddleableRenderState)var4).isSaddled()) {
         EntityModel var7 = var4.isBaby ? this.babyModel : this.adultModel;
         var7.setupAnim(var4);
         VertexConsumer var8 = var2.getBuffer(RenderType.entityCutoutNoCull(this.textureLocation));
         var7.renderToBuffer(var1, var8, var3, OverlayTexture.NO_OVERLAY);
      }
   }
}
