package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.animal.wolf.WolfModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;

public class WolfCollarLayer extends RenderLayer<WolfRenderState, WolfModel> {
   private static final Identifier WOLF_COLLAR_LOCATION = Identifier.withDefaultNamespace("textures/entity/wolf/wolf_collar.png");

   public WolfCollarLayer(RenderLayerParent<WolfRenderState, WolfModel> var1) {
      super(var1);
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, WolfRenderState var4, float var5, float var6) {
      DyeColor var7 = var4.collarColor;
      if (var7 != null && !var4.isInvisible) {
         int var8 = var7.getTextureDiffuseColor();
         var2.order(1).submitModel(this.getParentModel(), var4, var1, RenderTypes.entityCutoutNoCull(WOLF_COLLAR_LOCATION), var3, OverlayTexture.NO_OVERLAY, var8, (TextureAtlasSprite)null, var4.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      }
   }
}
