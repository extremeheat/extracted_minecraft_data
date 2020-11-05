package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
      if (var4.isCapeLoaded() && !var4.isInvisible() && var4.isModelPartShown(PlayerModelPart.CAPE) && var4.getCloakTextureLocation() != null) {
         ItemStack var11 = var4.getItemBySlot(EquipmentSlot.CHEST);
         if (!var11.is(Items.ELYTRA)) {
            var1.pushPose();
            var1.translate(0.0D, 0.0D, 0.125D);
            double var12 = Mth.lerp((double)var7, var4.xCloakO, var4.xCloak) - Mth.lerp((double)var7, var4.xo, var4.getX());
            double var14 = Mth.lerp((double)var7, var4.yCloakO, var4.yCloak) - Mth.lerp((double)var7, var4.yo, var4.getY());
            double var16 = Mth.lerp((double)var7, var4.zCloakO, var4.zCloak) - Mth.lerp((double)var7, var4.zo, var4.getZ());
            float var18 = var4.yBodyRotO + (var4.yBodyRot - var4.yBodyRotO);
            double var19 = (double)Mth.sin(var18 * 0.017453292F);
            double var21 = (double)(-Mth.cos(var18 * 0.017453292F));
            float var23 = (float)var14 * 10.0F;
            var23 = Mth.clamp(var23, -6.0F, 32.0F);
            float var24 = (float)(var12 * var19 + var16 * var21) * 100.0F;
            var24 = Mth.clamp(var24, 0.0F, 150.0F);
            float var25 = (float)(var12 * var21 - var16 * var19) * 100.0F;
            var25 = Mth.clamp(var25, -20.0F, 20.0F);
            if (var24 < 0.0F) {
               var24 = 0.0F;
            }

            float var26 = Mth.lerp(var7, var4.oBob, var4.bob);
            var23 += Mth.sin(Mth.lerp(var7, var4.walkDistO, var4.walkDist) * 6.0F) * 32.0F * var26;
            if (var4.isCrouching()) {
               var23 += 25.0F;
            }

            var1.mulPose(Vector3f.XP.rotationDegrees(6.0F + var24 / 2.0F + var23));
            var1.mulPose(Vector3f.ZP.rotationDegrees(var25 / 2.0F));
            var1.mulPose(Vector3f.YP.rotationDegrees(180.0F - var25 / 2.0F));
            VertexConsumer var27 = var2.getBuffer(RenderType.entitySolid(var4.getCloakTextureLocation()));
            ((PlayerModel)this.getParentModel()).renderCloak(var1, var27, var3, OverlayTexture.NO_OVERLAY);
            var1.popPose();
         }
      }
   }
}
