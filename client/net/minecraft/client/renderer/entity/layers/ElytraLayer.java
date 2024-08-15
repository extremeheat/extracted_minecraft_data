package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class ElytraLayer<S extends HumanoidRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
   private static final ResourceLocation WINGS_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/elytra.png");
   private final ElytraModel elytraModel;
   private final ElytraModel elytraBabyModel;

   public ElytraLayer(RenderLayerParent<S, M> var1, EntityModelSet var2) {
      super(var1);
      this.elytraModel = new ElytraModel(var2.bakeLayer(ModelLayers.ELYTRA));
      this.elytraBabyModel = new ElytraModel(var2.bakeLayer(ModelLayers.ELYTRA_BABY));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6) {
      if (var4.chestItem.is(Items.ELYTRA)) {
         ResourceLocation var7;
         if (var4 instanceof PlayerRenderState var8) {
            PlayerSkin var9 = var8.skin;
            if (var9.elytraTexture() != null) {
               var7 = var9.elytraTexture();
            } else if (var9.capeTexture() != null && var8.showCape) {
               var7 = var9.capeTexture();
            } else {
               var7 = WINGS_LOCATION;
            }
         } else {
            var7 = WINGS_LOCATION;
         }

         ElytraModel var10 = var4.isBaby ? this.elytraBabyModel : this.elytraModel;
         var1.pushPose();
         var1.translate(0.0F, 0.0F, 0.125F);
         var10.setupAnim(var4);
         VertexConsumer var11 = ItemRenderer.getArmorFoilBuffer(var2, RenderType.armorCutoutNoCull(var7), var4.chestItem.hasFoil());
         var10.renderToBuffer(var1, var11, var3, OverlayTexture.NO_OVERLAY);
         var1.popPose();
      }
   }
}
