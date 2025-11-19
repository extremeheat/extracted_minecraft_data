package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.effects.SpinAttackEffectModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class SpinAttackEffectLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
   public static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/trident_riptide.png");
   private final SpinAttackEffectModel model;

   public SpinAttackEffectLayer(RenderLayerParent<AvatarRenderState, PlayerModel> var1, EntityModelSet var2) {
      super(var1);
      this.model = new SpinAttackEffectModel(var2.bakeLayer(ModelLayers.PLAYER_SPIN_ATTACK));
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, AvatarRenderState var4, float var5, float var6) {
      if (var4.isAutoSpinAttack) {
         var2.submitModel(this.model, var4, var1, this.model.renderType(TEXTURE), var3, OverlayTexture.NO_OVERLAY, var4.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      }
   }
}
