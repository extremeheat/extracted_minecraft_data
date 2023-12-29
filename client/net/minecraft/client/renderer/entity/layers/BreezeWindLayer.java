package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.BreezeModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.breeze.Breeze;

public class BreezeWindLayer extends RenderLayer<Breeze, BreezeModel<Breeze>> {
   private static final float TOP_PART_ALPHA = 1.0F;
   private static final float MIDDLE_PART_ALPHA = 1.0F;
   private static final float BOTTOM_PART_ALPHA = 1.0F;
   private final ResourceLocation textureLoc;
   private final BreezeModel<Breeze> model;

   public BreezeWindLayer(RenderLayerParent<Breeze, BreezeModel<Breeze>> var1, EntityModelSet var2, ResourceLocation var3) {
      super(var1);
      this.model = new BreezeModel<>(var2.bakeLayer(ModelLayers.BREEZE_WIND));
      this.textureLoc = var3;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Breeze var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      float var11 = (float)var4.tickCount + var7;
      this.model.prepareMobModel(var4, var5, var6, var7);
      this.getParentModel().copyPropertiesTo(this.model);
      VertexConsumer var12 = var2.getBuffer(RenderType.breezeWind(this.getTextureLocation(var4), this.xOffset(var11) % 1.0F, 0.0F));
      this.model.setupAnim(var4, var5, var6, var8, var9, var10);
      this.model.windTop().skipDraw = true;
      this.model.windMiddle().skipDraw = true;
      this.model.windBottom().skipDraw = false;
      this.model.root().render(var1, var12, var3, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      this.model.windTop().skipDraw = true;
      this.model.windMiddle().skipDraw = false;
      this.model.windBottom().skipDraw = true;
      this.model.root().render(var1, var12, var3, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      this.model.windTop().skipDraw = false;
      this.model.windMiddle().skipDraw = true;
      this.model.windBottom().skipDraw = true;
      this.model.root().render(var1, var12, var3, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   private float xOffset(float var1) {
      return var1 * 0.02F;
   }

   protected ResourceLocation getTextureLocation(Breeze var1) {
      return this.textureLoc;
   }
}
