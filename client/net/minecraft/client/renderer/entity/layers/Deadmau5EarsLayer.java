package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerEarsModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;

public class Deadmau5EarsLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
   private final HumanoidModel<PlayerRenderState> model;

   public Deadmau5EarsLayer(RenderLayerParent<PlayerRenderState, PlayerModel> var1, EntityModelSet var2) {
      super(var1);
      this.model = new PlayerEarsModel(var2.bakeLayer(ModelLayers.PLAYER_EARS));
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, PlayerRenderState var4, float var5, float var6) {
      if ("deadmau5".equals(var4.name) && !var4.isInvisible) {
         int var7 = LivingEntityRenderer.getOverlayCoords(var4, 0.0F);
         var2.submitModel(this.model, var4, var1, RenderType.entitySolid(var4.skin.texture()), var3, var7, var4.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      }
   }
}
