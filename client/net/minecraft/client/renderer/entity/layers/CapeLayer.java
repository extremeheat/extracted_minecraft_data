package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CapeLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
   public CapeLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, AbstractClientPlayer var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (!var4.isInvisible() && var4.isModelPartShown(PlayerModelPart.CAPE)) {
         PlayerSkin var11 = var4.getSkin();
         if (var11.capeTexture() != null) {
            ItemStack var12 = var4.getItemBySlot(EquipmentSlot.CHEST);
            if (!var12.is(Items.ELYTRA)) {
               var1.pushPose();
               var1.translate(0.0F, 0.0F, 0.125F);
               double var13 = Mth.lerp((double)var7, var4.xCloakO, var4.xCloak) - Mth.lerp((double)var7, var4.xo, var4.getX());
               double var15 = Mth.lerp((double)var7, var4.yCloakO, var4.yCloak) - Mth.lerp((double)var7, var4.yo, var4.getY());
               double var17 = Mth.lerp((double)var7, var4.zCloakO, var4.zCloak) - Mth.lerp((double)var7, var4.zo, var4.getZ());
               float var19 = Mth.rotLerp(var7, var4.yBodyRotO, var4.yBodyRot);
               double var20 = (double)Mth.sin(var19 * 0.017453292F);
               double var22 = (double)(-Mth.cos(var19 * 0.017453292F));
               float var24 = (float)var15 * 10.0F;
               var24 = Mth.clamp(var24, -6.0F, 32.0F);
               float var25 = (float)(var13 * var20 + var17 * var22) * 100.0F;
               var25 = Mth.clamp(var25, 0.0F, 150.0F);
               float var26 = (float)(var13 * var22 - var17 * var20) * 100.0F;
               var26 = Mth.clamp(var26, -20.0F, 20.0F);
               if (var25 < 0.0F) {
                  var25 = 0.0F;
               }

               float var27 = Mth.lerp(var7, var4.oBob, var4.bob);
               var24 += Mth.sin(Mth.lerp(var7, var4.walkDistO, var4.walkDist) * 6.0F) * 32.0F * var27;
               if (var4.isCrouching()) {
                  var24 += 25.0F;
               }

               var1.mulPose(Axis.XP.rotationDegrees(6.0F + var25 / 2.0F + var24));
               var1.mulPose(Axis.ZP.rotationDegrees(var26 / 2.0F));
               var1.mulPose(Axis.YP.rotationDegrees(180.0F - var26 / 2.0F));
               VertexConsumer var28 = var2.getBuffer(RenderType.entitySolid(var11.capeTexture()));
               ((PlayerModel)this.getParentModel()).renderCloak(var1, var28, var3, OverlayTexture.NO_OVERLAY);
               var1.popPose();
            }
         }
      }
   }
}
