package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerCapeModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.item.Items;

public class CapeLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
   private final HumanoidModel<PlayerRenderState> model;

   public CapeLayer(RenderLayerParent<PlayerRenderState, PlayerModel> var1, EntityModelSet var2) {
      super(var1);
      this.model = new PlayerCapeModel<>(var2.bakeLayer(ModelLayers.PLAYER_CAPE));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, PlayerRenderState var4, float var5, float var6) {
      if (!var4.isInvisible && var4.showCape) {
         PlayerSkin var7 = var4.skin;
         if (var7.capeTexture() != null) {
            if (!var4.chestItem.is(Items.ELYTRA)) {
               VertexConsumer var8 = var2.getBuffer(RenderType.entitySolid(var7.capeTexture()));
               this.getParentModel().copyPropertiesTo(this.model);
               this.model.setupAnim(var4);
               this.model.renderToBuffer(var1, var8, var3, OverlayTexture.NO_OVERLAY);
            }
         }
      }
   }
}
