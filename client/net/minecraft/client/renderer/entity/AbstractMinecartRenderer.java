package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Objects;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartBehavior;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import net.minecraft.world.entity.vehicle.OldMinecartBehavior;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public abstract class AbstractMinecartRenderer<T extends AbstractMinecart, S extends MinecartRenderState> extends EntityRenderer<T, S> {
   private static final Identifier MINECART_LOCATION = Identifier.withDefaultNamespace("textures/entity/minecart.png");
   private static final float DISPLAY_BLOCK_SCALE = 0.75F;
   protected final MinecartModel model;

   public AbstractMinecartRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2) {
      super(var1);
      this.shadowRadius = 0.7F;
      this.model = new MinecartModel(var1.bakeLayer(var2));
   }

   public void submit(S var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      super.submit(var1, var2, var3, var4);
      var2.pushPose();
      long var5 = var1.offsetSeed;
      float var7 = (((float)(var5 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float var8 = (((float)(var5 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float var9 = (((float)(var5 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      var2.translate(var7, var8, var9);
      if (var1.isNewRender) {
         newRender(var1, var2);
      } else {
         oldRender(var1, var2);
      }

      float var10 = var1.hurtTime;
      if (var10 > 0.0F) {
         var2.mulPose((Quaternionfc)Axis.XP.rotationDegrees(Mth.sin((double)var10) * var10 * var1.damageTime / 10.0F * (float)var1.hurtDir));
      }

      BlockState var11 = var1.displayBlockState;
      if (var11.getRenderShape() != RenderShape.INVISIBLE) {
         var2.pushPose();
         var2.scale(0.75F, 0.75F, 0.75F);
         var2.translate(-0.5F, (float)(var1.displayOffset - 8) / 16.0F, 0.5F);
         var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(90.0F));
         this.submitMinecartContents(var1, var11, var2, var3, var1.lightCoords);
         var2.popPose();
      }

      var2.scale(-1.0F, -1.0F, 1.0F);
      var3.submitModel(this.model, var1, var2, this.model.renderType(MINECART_LOCATION), var1.lightCoords, OverlayTexture.NO_OVERLAY, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      var2.popPose();
   }

   private static <S extends MinecartRenderState> void newRender(S var0, PoseStack var1) {
      var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var0.yRot));
      var1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(-var0.xRot));
      var1.translate(0.0F, 0.375F, 0.0F);
   }

   private static <S extends MinecartRenderState> void oldRender(S var0, PoseStack var1) {
      double var2 = var0.x;
      double var4 = var0.y;
      double var6 = var0.z;
      float var8 = var0.xRot;
      float var9 = var0.yRot;
      if (var0.posOnRail != null && var0.frontPos != null && var0.backPos != null) {
         Vec3 var10 = var0.frontPos;
         Vec3 var11 = var0.backPos;
         var1.translate(var0.posOnRail.x - var2, (var10.y + var11.y) / 2.0 - var4, var0.posOnRail.z - var6);
         Vec3 var12 = var11.add(-var10.x, -var10.y, -var10.z);
         if (var12.length() != 0.0) {
            var12 = var12.normalize();
            var9 = (float)(Math.atan2(var12.z, var12.x) * 180.0 / 3.141592653589793);
            var8 = (float)(Math.atan(var12.y) * 73.0);
         }
      }

      var1.translate(0.0F, 0.375F, 0.0F);
      var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F - var9));
      var1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(-var8));
   }

   public void extractRenderState(T var1, S var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      MinecartBehavior var6 = var1.getBehavior();
      if (var6 instanceof NewMinecartBehavior var4) {
         newExtractState(var1, var4, var2, var3);
         var2.isNewRender = true;
      } else {
         var6 = var1.getBehavior();
         if (var6 instanceof OldMinecartBehavior var5) {
            oldExtractState(var1, var5, var2, var3);
            var2.isNewRender = false;
         }
      }

      long var7 = (long)var1.getId() * 493286711L;
      var2.offsetSeed = var7 * var7 * 4392167121L + var7 * 98761L;
      var2.hurtTime = (float)var1.getHurtTime() - var3;
      var2.hurtDir = var1.getHurtDir();
      var2.damageTime = Math.max(var1.getDamage() - var3, 0.0F);
      var2.displayOffset = var1.getDisplayOffset();
      var2.displayBlockState = var1.getDisplayBlockState();
   }

   private static <T extends AbstractMinecart, S extends MinecartRenderState> void newExtractState(T var0, NewMinecartBehavior var1, S var2, float var3) {
      if (var1.cartHasPosRotLerp()) {
         var2.renderPos = var1.getCartLerpPosition(var3);
         var2.xRot = var1.getCartLerpXRot(var3);
         var2.yRot = var1.getCartLerpYRot(var3);
      } else {
         var2.renderPos = null;
         var2.xRot = var0.getXRot();
         var2.yRot = var0.getYRot();
      }

   }

   private static <T extends AbstractMinecart, S extends MinecartRenderState> void oldExtractState(T var0, OldMinecartBehavior var1, S var2, float var3) {
      float var4 = 0.3F;
      var2.xRot = var0.getXRot(var3);
      var2.yRot = var0.getYRot(var3);
      double var5 = var2.x;
      double var7 = var2.y;
      double var9 = var2.z;
      Vec3 var11 = var1.getPos(var5, var7, var9);
      if (var11 != null) {
         var2.posOnRail = var11;
         Vec3 var12 = var1.getPosOffs(var5, var7, var9, 0.30000001192092896);
         Vec3 var13 = var1.getPosOffs(var5, var7, var9, -0.30000001192092896);
         var2.frontPos = (Vec3)Objects.requireNonNullElse(var12, var11);
         var2.backPos = (Vec3)Objects.requireNonNullElse(var13, var11);
      } else {
         var2.posOnRail = null;
         var2.frontPos = null;
         var2.backPos = null;
      }

   }

   protected void submitMinecartContents(S var1, BlockState var2, PoseStack var3, SubmitNodeCollector var4, int var5) {
      var4.submitBlock(var3, var2, var5, OverlayTexture.NO_OVERLAY, var1.outlineColor);
   }

   protected AABB getBoundingBoxForCulling(T var1) {
      AABB var2 = super.getBoundingBoxForCulling(var1);
      return !var1.getDisplayBlockState().isAir() ? var2.expandTowards(0.0, (double)((float)var1.getDisplayOffset() * 0.75F / 16.0F), 0.0) : var2;
   }

   public Vec3 getRenderOffset(S var1) {
      Vec3 var2 = super.getRenderOffset(var1);
      return var1.isNewRender && var1.renderPos != null ? var2.add(var1.renderPos.x - var1.x, var1.renderPos.y - var1.y, var1.renderPos.z - var1.z) : var2;
   }
}
