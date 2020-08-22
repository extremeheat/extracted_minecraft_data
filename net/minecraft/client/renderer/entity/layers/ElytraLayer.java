package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ElytraLayer extends RenderLayer {
   private static final ResourceLocation WINGS_LOCATION = new ResourceLocation("textures/entity/elytra.png");
   private final ElytraModel elytraModel = new ElytraModel();

   public ElytraLayer(RenderLayerParent var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, LivingEntity var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      ItemStack var11 = var4.getItemBySlot(EquipmentSlot.CHEST);
      if (var11.getItem() == Items.ELYTRA) {
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
         VertexConsumer var14 = ItemRenderer.getFoilBuffer(var2, this.elytraModel.renderType(var12), false, var11.hasFoil());
         this.elytraModel.renderToBuffer(var1, var14, var3, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
         var1.popPose();
      }
   }
}
