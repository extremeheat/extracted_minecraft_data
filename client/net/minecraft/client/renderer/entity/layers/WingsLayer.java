package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.minecraft.world.item.equipment.Equippable;

public class WingsLayer<S extends HumanoidRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
   private final ElytraModel elytraModel;
   private final ElytraModel elytraBabyModel;
   private final EquipmentLayerRenderer equipmentRenderer;

   public WingsLayer(RenderLayerParent<S, M> var1, EntityModelSet var2, EquipmentLayerRenderer var3) {
      super(var1);
      this.elytraModel = new ElytraModel(var2.bakeLayer(ModelLayers.ELYTRA));
      this.elytraBabyModel = new ElytraModel(var2.bakeLayer(ModelLayers.ELYTRA_BABY));
      this.equipmentRenderer = var3;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6) {
      ItemStack var7 = var4.chestItem;
      Equippable var8 = (Equippable)var7.get(DataComponents.EQUIPPABLE);
      if (var8 != null && !var8.model().isEmpty()) {
         ResourceLocation var9 = getPlayerElytraTexture(var4);
         ElytraModel var10 = var4.isBaby ? this.elytraBabyModel : this.elytraModel;
         ResourceLocation var11 = (ResourceLocation)var8.model().get();
         var1.pushPose();
         var1.translate(0.0F, 0.0F, 0.125F);
         var10.setupAnim(var4);
         this.equipmentRenderer.renderLayers(EquipmentModel.LayerType.WINGS, var11, var10, var7, var1, var2, var3, var9);
         var1.popPose();
      }
   }

   @Nullable
   private static ResourceLocation getPlayerElytraTexture(HumanoidRenderState var0) {
      if (var0 instanceof PlayerRenderState var1) {
         PlayerSkin var2 = var1.skin;
         if (var2.elytraTexture() != null) {
            return var2.elytraTexture();
         }

         if (var2.capeTexture() != null && var1.showCape) {
            return var2.capeTexture();
         }
      }

      return null;
   }
}
