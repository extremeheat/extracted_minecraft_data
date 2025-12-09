package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerCapeModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class CapeLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
   private final HumanoidModel<AvatarRenderState> model;
   private final EquipmentAssetManager equipmentAssets;

   public CapeLayer(RenderLayerParent<AvatarRenderState, PlayerModel> var1, EntityModelSet var2, EquipmentAssetManager var3) {
      super(var1);
      this.model = new PlayerCapeModel(var2.bakeLayer(ModelLayers.PLAYER_CAPE));
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

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, AvatarRenderState var4, float var5, float var6) {
      if (!var4.isInvisible && var4.showCape) {
         PlayerSkin var7 = var4.skin;
         if (var7.cape() != null) {
            if (!this.hasLayer(var4.chestEquipment, EquipmentClientInfo.LayerType.WINGS)) {
               var1.pushPose();
               if (this.hasLayer(var4.chestEquipment, EquipmentClientInfo.LayerType.HUMANOID)) {
                  var1.translate(0.0F, -0.053125F, 0.06875F);
               }

               var2.submitModel(this.model, var4, var1, RenderTypes.entitySolid(var7.cape().texturePath()), var3, OverlayTexture.NO_OVERLAY, var4.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
               var1.popPose();
            }
         }
      }
   }
}
