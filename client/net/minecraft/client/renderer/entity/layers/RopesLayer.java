package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.animal.ghast.HappyGhastModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HappyGhastRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;

public class RopesLayer<M extends HappyGhastModel> extends RenderLayer<HappyGhastRenderState, M> {
   private final RenderType ropes;
   private final HappyGhastModel adultModel;
   private final HappyGhastModel babyModel;

   public RopesLayer(RenderLayerParent<HappyGhastRenderState, M> var1, EntityModelSet var2, Identifier var3) {
      super(var1);
      this.ropes = RenderTypes.entityCutoutNoCull(var3);
      this.adultModel = new HappyGhastModel(var2.bakeLayer(ModelLayers.HAPPY_GHAST_ROPES));
      this.babyModel = new HappyGhastModel(var2.bakeLayer(ModelLayers.HAPPY_GHAST_BABY_ROPES));
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, HappyGhastRenderState var4, float var5, float var6) {
      if (var4.isLeashHolder && var4.bodyItem.is(ItemTags.HARNESSES)) {
         HappyGhastModel var7 = var4.isBaby ? this.babyModel : this.adultModel;
         var2.submitModel(var7, var4, var1, this.ropes, var3, OverlayTexture.NO_OVERLAY, var4.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      }
   }
}
