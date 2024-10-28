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
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ElytraLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
   private static final ResourceLocation WINGS_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/elytra.png");
   private final ElytraModel<T> elytraModel;

   public ElytraLayer(RenderLayerParent<T, M> var1, EntityModelSet var2) {
      super(var1);
      this.elytraModel = new ElytraModel(var2.bakeLayer(ModelLayers.ELYTRA));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      ItemStack var11 = var4.getItemBySlot(EquipmentSlot.CHEST);
      if (var11.is(Items.ELYTRA)) {
         ResourceLocation var12;
         if (var4 instanceof AbstractClientPlayer) {
            AbstractClientPlayer var13 = (AbstractClientPlayer)var4;
            PlayerSkin var14 = var13.getSkin();
            if (var14.elytraTexture() != null) {
               var12 = var14.elytraTexture();
            } else if (var14.capeTexture() != null && var13.isModelPartShown(PlayerModelPart.CAPE)) {
               var12 = var14.capeTexture();
            } else {
               var12 = WINGS_LOCATION;
            }
         } else {
            var12 = WINGS_LOCATION;
         }

         var1.pushPose();
         var1.translate(0.0F, 0.0F, 0.125F);
         this.getParentModel().copyPropertiesTo(this.elytraModel);
         this.elytraModel.setupAnim(var4, var5, var6, var8, var9, var10);
         VertexConsumer var15 = ItemRenderer.getArmorFoilBuffer(var2, RenderType.armorCutoutNoCull(var12), var11.hasFoil());
         this.elytraModel.renderToBuffer(var1, var15, var3, OverlayTexture.NO_OVERLAY);
         var1.popPose();
      }
   }
}
