package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;

public abstract class RenderLayer<S extends EntityRenderState, M extends EntityModel<? super S>> {
   private final RenderLayerParent<S, M> renderer;

   public RenderLayer(RenderLayerParent<S, M> var1) {
      super();
      this.renderer = var1;
   }

   protected static <S extends LivingEntityRenderState> void coloredCutoutModelCopyLayerRender(Model<? super S> var0, Identifier var1, PoseStack var2, SubmitNodeCollector var3, int var4, S var5, int var6, int var7) {
      if (!var5.isInvisible) {
         renderColoredCutoutModel(var0, var1, var2, var3, var4, var5, var6, var7);
      }

   }

   protected static <S extends LivingEntityRenderState> void renderColoredCutoutModel(Model<? super S> var0, Identifier var1, PoseStack var2, SubmitNodeCollector var3, int var4, S var5, int var6, int var7) {
      var3.order(var7).submitModel(var0, var5, var2, RenderTypes.entityCutoutNoCull(var1), var4, LivingEntityRenderer.getOverlayCoords(var5, 0.0F), var6, (TextureAtlasSprite)null, var5.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   public M getParentModel() {
      return this.renderer.getModel();
   }

   public abstract void submit(PoseStack var1, SubmitNodeCollector var2, int var3, S var4, float var5, float var6);
}
