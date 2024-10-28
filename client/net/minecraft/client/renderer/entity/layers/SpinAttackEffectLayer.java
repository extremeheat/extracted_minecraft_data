package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.SpinAttackEffectModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class SpinAttackEffectLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
   public static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/trident_riptide.png");
   private final SpinAttackEffectModel model;

   public SpinAttackEffectLayer(RenderLayerParent<PlayerRenderState, PlayerModel> var1, EntityModelSet var2) {
      super(var1);
      this.model = new SpinAttackEffectModel(var2.bakeLayer(ModelLayers.PLAYER_SPIN_ATTACK));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, PlayerRenderState var4, float var5, float var6) {
      if (var4.isAutoSpinAttack) {
         VertexConsumer var7 = var2.getBuffer(this.model.renderType(TEXTURE));
         this.model.setupAnim(var4);
         this.model.renderToBuffer(var1, var7, var3, OverlayTexture.NO_OVERLAY);
      }
   }
}
