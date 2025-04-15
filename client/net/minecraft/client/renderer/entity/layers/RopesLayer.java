package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HappyGhastModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HappyGhastRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;

public class RopesLayer<M extends HappyGhastModel> extends RenderLayer<HappyGhastRenderState, M> {
   private final RenderType ropes;
   private final HappyGhastModel adultModel;
   private final HappyGhastModel babyModel;

   public RopesLayer(RenderLayerParent<HappyGhastRenderState, M> var1, EntityModelSet var2, ResourceLocation var3) {
      super(var1);
      this.ropes = RenderType.entityCutoutNoCull(var3);
      this.adultModel = new HappyGhastModel(var2.bakeLayer(ModelLayers.HAPPY_GHAST_ROPES));
      this.babyModel = new HappyGhastModel(var2.bakeLayer(ModelLayers.HAPPY_GHAST_BABY_ROPES));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, HappyGhastRenderState var4, float var5, float var6) {
      if (var4.isLeashHolder && var4.bodyItem.is(ItemTags.HARNESSES)) {
         HappyGhastModel var7 = var4.isBaby ? this.babyModel : this.adultModel;
         var7.setupAnim(var4);
         var7.renderToBuffer(var1, var2.getBuffer(this.ropes), var3, OverlayTexture.NO_OVERLAY);
      }
   }
}
