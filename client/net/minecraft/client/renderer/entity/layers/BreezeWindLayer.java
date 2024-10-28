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

public class BreezeWindLayer extends RenderLayer<Breeze, BreezeModel<Breeze>> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/breeze/breeze_wind.png");
   private static final BreezeModel<Breeze> MODEL = new BreezeModel(BreezeModel.createBodyLayer(128, 128).bakeRoot());

   public BreezeWindLayer(RenderLayerParent<Breeze, BreezeModel<Breeze>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Breeze var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      float var11 = (float)var4.tickCount + var7;
      VertexConsumer var12 = var2.getBuffer(RenderType.breezeWind(TEXTURE_LOCATION, this.xOffset(var11) % 1.0F, 0.0F));
      MODEL.setupAnim(var4, var5, var6, var8, var9, var10);
      BreezeRenderer.enable(MODEL, MODEL.wind()).renderToBuffer(var1, var12, var3, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   private float xOffset(float var1) {
      return var1 * 0.02F;
   }
}
