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
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class CapeLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
   private final HumanoidModel<PlayerRenderState> model;
   private final EquipmentAssetManager equipmentAssets;

   public CapeLayer(RenderLayerParent<PlayerRenderState, PlayerModel> var1, EntityModelSet var2, EquipmentAssetManager var3) {
      super(var1);
      this.model = new PlayerCapeModel<PlayerRenderState>(var2.bakeLayer(ModelLayers.PLAYER_CAPE));
      this.equipmentAssets = var3;
   }

   private boolean hasLayer(ItemStack var1, EquipmentClientInfo.LayerType var2) {
      Equippable var3 = (Equippable)var1.get(DataComponents.EQUIPPABLE);
      if (var3 != null && !var3.assetId().isEmpty()) {
         EquipmentClientInfo var4 = this.equipmentAssets.get((ResourceKey)var3.assetId().get());
         return !var4.getLayers(var2).isEmpty();
      } else {
         return false;
      }
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, PlayerRenderState var4, float var5, float var6) {
      if (!var4.isInvisible && var4.showCape) {
         PlayerSkin var7 = var4.skin;
         if (var7.capeTexture() != null) {
            if (!this.hasLayer(var4.chestEquipment, EquipmentClientInfo.LayerType.WINGS)) {
               var1.pushPose();
               if (this.hasLayer(var4.chestEquipment, EquipmentClientInfo.LayerType.HUMANOID)) {
                  var1.translate(0.0F, -0.053125F, 0.06875F);
               }

               VertexConsumer var8 = var2.getBuffer(RenderType.entitySolid(var7.capeTexture()));
               ((PlayerModel)this.getParentModel()).copyPropertiesTo(this.model);
               this.model.setupAnim(var4);
               this.model.renderToBuffer(var1, var8, var3, OverlayTexture.NO_OVERLAY);
               var1.popPose();
            }
         }
      }
   }
}
