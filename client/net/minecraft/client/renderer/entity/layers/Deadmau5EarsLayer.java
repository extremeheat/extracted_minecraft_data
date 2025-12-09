package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerEarsModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class Deadmau5EarsLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
   private final HumanoidModel<AvatarRenderState> model;

   public Deadmau5EarsLayer(RenderLayerParent<AvatarRenderState, PlayerModel> var1, EntityModelSet var2) {
      super(var1);
      this.model = new PlayerEarsModel(var2.bakeLayer(ModelLayers.PLAYER_EARS));
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, AvatarRenderState var4, float var5, float var6) {
      if (var4.showExtraEars && !var4.isInvisible) {
         int var7 = LivingEntityRenderer.getOverlayCoords(var4, 0.0F);
         var2.submitModel(this.model, var4, var1, RenderTypes.entitySolid(var4.skin.body().texturePath()), var3, var7, var4.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      }
   }
}
