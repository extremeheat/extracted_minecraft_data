package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.slime.SlimeModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class SlimeOuterLayer extends RenderLayer<SlimeRenderState, SlimeModel> {
   private final SlimeModel model;

   public SlimeOuterLayer(RenderLayerParent<SlimeRenderState, SlimeModel> var1, EntityModelSet var2) {
      super(var1);
      this.model = new SlimeModel(var2.bakeLayer(ModelLayers.SLIME_OUTER));
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, SlimeRenderState var4, float var5, float var6) {
      boolean var7 = var4.appearsGlowing() && var4.isInvisible;
      if (!var4.isInvisible || var7) {
         int var8 = LivingEntityRenderer.getOverlayCoords(var4, 0.0F);
         if (var7) {
            var2.order(1).submitModel(this.model, var4, var1, RenderTypes.outline(SlimeRenderer.SLIME_LOCATION), var3, var8, -1, (TextureAtlasSprite)null, var4.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
         } else {
            var2.order(1).submitModel(this.model, var4, var1, RenderTypes.entityTranslucent(SlimeRenderer.SLIME_LOCATION), var3, var8, -1, (TextureAtlasSprite)null, var4.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
         }

      }
   }
}
