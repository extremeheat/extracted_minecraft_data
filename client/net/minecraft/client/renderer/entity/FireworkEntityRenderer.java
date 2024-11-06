package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.FireworkRocketRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemDisplayContext;

public class FireworkEntityRenderer extends EntityRenderer<FireworkRocketEntity, FireworkRocketRenderState> {
   private final ItemModelResolver itemModelResolver;

   public FireworkEntityRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.itemModelResolver = var1.getItemModelResolver();
   }

   public void render(FireworkRocketRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      var2.mulPose(this.entityRenderDispatcher.cameraOrientation());
      if (var1.isShotAtAngle) {
         var2.mulPose(Axis.ZP.rotationDegrees(180.0F));
         var2.mulPose(Axis.YP.rotationDegrees(180.0F));
         var2.mulPose(Axis.XP.rotationDegrees(90.0F));
      }

      var1.item.render(var2, var3, var4, OverlayTexture.NO_OVERLAY);
      var2.popPose();
      super.render(var1, var2, var3, var4);
   }

   public FireworkRocketRenderState createRenderState() {
      return new FireworkRocketRenderState();
   }

   public void extractRenderState(FireworkRocketEntity var1, FireworkRocketRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isShotAtAngle = var1.isShotAtAngle();
      this.itemModelResolver.updateForNonLiving(var2.item, var1.getItem(), ItemDisplayContext.GROUND, var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
