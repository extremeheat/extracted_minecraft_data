package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerEarsModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;

public class Deadmau5EarsLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
   private final HumanoidModel<PlayerRenderState> model;

   public Deadmau5EarsLayer(RenderLayerParent<PlayerRenderState, PlayerModel> var1, EntityModelSet var2) {
      super(var1);
      this.model = new PlayerEarsModel(var2.bakeLayer(ModelLayers.PLAYER_EARS));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, PlayerRenderState var4, float var5, float var6) {
      if ("deadmau5".equals(var4.name) && !var4.isInvisible) {
         VertexConsumer var7 = var2.getBuffer(RenderType.entitySolid(var4.skin.texture()));
         int var8 = LivingEntityRenderer.getOverlayCoords(var4, 0.0F);
         this.getParentModel().copyPropertiesTo(this.model);
         this.model.setupAnim(var4);
         this.model.renderToBuffer(var1, var7, var3, var8);
      }
   }
}
