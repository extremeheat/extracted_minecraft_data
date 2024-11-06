package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.FishingHookRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.phys.Vec3;

public class FishingHookRenderer extends EntityRenderer<FishingHook, FishingHookRenderState> {
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/fishing_hook.png");
   private static final RenderType RENDER_TYPE;
   private static final double VIEW_BOBBING_SCALE = 960.0;

   public FishingHookRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   public boolean shouldRender(FishingHook var1, Frustum var2, double var3, double var5, double var7) {
      return super.shouldRender(var1, var2, var3, var5, var7) && var1.getPlayerOwner() != null;
   }

   public void render(FishingHookRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      var2.pushPose();
      var2.scale(0.5F, 0.5F, 0.5F);
      var2.mulPose(this.entityRenderDispatcher.cameraOrientation());
      PoseStack.Pose var5 = var2.last();
      VertexConsumer var6 = var3.getBuffer(RENDER_TYPE);
      vertex(var6, var5, var4, 0.0F, 0, 0, 1);
      vertex(var6, var5, var4, 1.0F, 0, 1, 1);
      vertex(var6, var5, var4, 1.0F, 1, 1, 0);
      vertex(var6, var5, var4, 0.0F, 1, 0, 0);
      var2.popPose();
      float var7 = (float)var1.lineOriginOffset.x;
      float var8 = (float)var1.lineOriginOffset.y;
      float var9 = (float)var1.lineOriginOffset.z;
      VertexConsumer var10 = var3.getBuffer(RenderType.lineStrip());
      PoseStack.Pose var11 = var2.last();
      boolean var12 = true;

      for(int var13 = 0; var13 <= 16; ++var13) {
         stringVertex(var7, var8, var9, var10, var11, fraction(var13, 16), fraction(var13 + 1, 16));
      }

      var2.popPose();
      super.render(var1, var2, var3, var4);
   }

   public static HumanoidArm getHoldingArm(Player var0) {
      return var0.getMainHandItem().getItem() instanceof FishingRodItem ? var0.getMainArm() : var0.getMainArm().getOpposite();
   }

   private Vec3 getPlayerHandPos(Player var1, float var2, float var3) {
      int var4 = getHoldingArm(var1) == HumanoidArm.RIGHT ? 1 : -1;
      if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && var1 == Minecraft.getInstance().player) {
         double var16 = 960.0 / (double)(Integer)this.entityRenderDispatcher.options.fov().get();
         Vec3 var7 = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float)var4 * 0.525F, -0.1F).scale(var16).yRot(var2 * 0.5F).xRot(-var2 * 0.7F);
         return var1.getEyePosition(var3).add(var7);
      } else {
         float var5 = Mth.lerp(var3, var1.yBodyRotO, var1.yBodyRot) * 0.017453292F;
         double var6 = (double)Mth.sin(var5);
         double var8 = (double)Mth.cos(var5);
         float var10 = var1.getScale();
         double var11 = (double)var4 * 0.35 * (double)var10;
         double var13 = 0.8 * (double)var10;
         float var15 = var1.isCrouching() ? -0.1875F : 0.0F;
         return var1.getEyePosition(var3).add(-var8 * var11 - var6 * var13, (double)var15 - 0.45 * (double)var10, -var6 * var11 + var8 * var13);
      }
   }

   private static float fraction(int var0, int var1) {
      return (float)var0 / (float)var1;
   }

   private static void vertex(VertexConsumer var0, PoseStack.Pose var1, int var2, float var3, int var4, int var5, int var6) {
      var0.addVertex(var1, var3 - 0.5F, (float)var4 - 0.5F, 0.0F).setColor(-1).setUv((float)var5, (float)var6).setOverlay(OverlayTexture.NO_OVERLAY).setLight(var2).setNormal(var1, 0.0F, 1.0F, 0.0F);
   }

   private static void stringVertex(float var0, float var1, float var2, VertexConsumer var3, PoseStack.Pose var4, float var5, float var6) {
      float var7 = var0 * var5;
      float var8 = var1 * (var5 * var5 + var5) * 0.5F + 0.25F;
      float var9 = var2 * var5;
      float var10 = var0 * var6 - var7;
      float var11 = var1 * (var6 * var6 + var6) * 0.5F + 0.25F - var8;
      float var12 = var2 * var6 - var9;
      float var13 = Mth.sqrt(var10 * var10 + var11 * var11 + var12 * var12);
      var10 /= var13;
      var11 /= var13;
      var12 /= var13;
      var3.addVertex(var4, var7, var8, var9).setColor(-16777216).setNormal(var4, var10, var11, var12);
   }

   public FishingHookRenderState createRenderState() {
      return new FishingHookRenderState();
   }

   public void extractRenderState(FishingHook var1, FishingHookRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      Player var4 = var1.getPlayerOwner();
      if (var4 == null) {
         var2.lineOriginOffset = Vec3.ZERO;
      } else {
         float var5 = var4.getAttackAnim(var3);
         float var6 = Mth.sin(Mth.sqrt(var5) * 3.1415927F);
         Vec3 var7 = this.getPlayerHandPos(var4, var6, var3);
         Vec3 var8 = var1.getPosition(var3).add(0.0, 0.25, 0.0);
         var2.lineOriginOffset = var7.subtract(var8);
      }
   }

   protected boolean affectedByCulling(FishingHook var1) {
      return false;
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   // $FF: synthetic method
   protected boolean affectedByCulling(final Entity var1) {
      return this.affectedByCulling((FishingHook)var1);
   }

   static {
      RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);
   }
}
