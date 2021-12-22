package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ElytraLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
   private static final ResourceLocation WINGS_LOCATION = new ResourceLocation("textures/entity/elytra.png");
   private final ElytraModel<T> elytraModel;

   public ElytraLayer(RenderLayerParent<T, M> var1, EntityModelSet var2) {
      super(var1);
      this.elytraModel = new ElytraModel(var2.bakeLayer(ModelLayers.ELYTRA));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      ItemStack var11 = var4.getItemBySlot(EquipmentSlot.CHEST);
      if (var11.method_87(Items.ELYTRA)) {
         ResourceLocation var12;
         if (var4 instanceof AbstractClientPlayer) {
            AbstractClientPlayer var13 = (AbstractClientPlayer)var4;
            if (var13.isElytraLoaded() && var13.getElytraTextureLocation() != null) {
               var12 = var13.getElytraTextureLocation();
            } else if (var13.isCapeLoaded() && var13.getCloakTextureLocation() != null && var13.isModelPartShown(PlayerModelPart.CAPE)) {
               var12 = var13.getCloakTextureLocation();
            } else {
               var12 = WINGS_LOCATION;
            }
         } else {
            var12 = WINGS_LOCATION;
         }

         var1.pushPose();
         var1.translate(0.0D, 0.0D, 0.125D);
         this.getParentModel().copyPropertiesTo(this.elytraModel);
         this.elytraModel.setupAnim(var4, var5, var6, var8, var9, var10);
         VertexConsumer var14 = ItemRenderer.getArmorFoilBuffer(var2, RenderType.armorCutoutNoCull(var12), false, var11.hasFoil());
         this.elytraModel.renderToBuffer(var1, var14, var3, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
         var1.popPose();
      }
   }
}
