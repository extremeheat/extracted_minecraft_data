package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartBehavior;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public abstract class EntityRenderer<T extends Entity, S extends EntityRenderState> {
   protected static final float NAMETAG_SCALE = 0.025F;
   public static final int LEASH_RENDER_STEPS = 24;
   protected final EntityRenderDispatcher entityRenderDispatcher;
   private final Font font;
   protected float shadowRadius;
   protected float shadowStrength = 1.0F;
   private final S reusedState = this.createRenderState();

   protected EntityRenderer(EntityRendererProvider.Context var1) {
      super();
      this.entityRenderDispatcher = var1.getEntityRenderDispatcher();
      this.font = var1.getFont();
   }

   public final int getPackedLightCoords(T var1, float var2) {
      BlockPos var3 = BlockPos.containing(var1.getLightProbePosition(var2));
      return LightTexture.pack(this.getBlockLightLevel(var1, var3), this.getSkyLightLevel(var1, var3));
   }

   protected int getSkyLightLevel(T var1, BlockPos var2) {
      return var1.level().getBrightness(LightLayer.SKY, var2);
   }

   protected int getBlockLightLevel(T var1, BlockPos var2) {
      return var1.isOnFire() ? 15 : var1.level().getBrightness(LightLayer.BLOCK, var2);
   }

   public boolean shouldRender(T var1, Frustum var2, double var3, double var5, double var7) {
      if (!var1.shouldRender(var3, var5, var7)) {
         return false;
      } else if (!this.affectedByCulling(var1)) {
         return true;
      } else {
         AABB var9 = this.getBoundingBoxForCulling(var1).inflate(0.5);
         if (var9.hasNaN() || var9.getSize() == 0.0) {
            var9 = new AABB(var1.getX() - 2.0, var1.getY() - 2.0, var1.getZ() - 2.0, var1.getX() + 2.0, var1.getY() + 2.0, var1.getZ() + 2.0);
         }

         if (var2.isVisible(var9)) {
            return true;
         } else {
            if (var1 instanceof Leashable) {
               Leashable var10 = (Leashable)var1;
               Entity var11 = var10.getLeashHolder();
               if (var11 != null) {
                  return var2.isVisible(this.entityRenderDispatcher.getRenderer(var11).getBoundingBoxForCulling(var11));
               }
            }

            return false;
         }
      }
   }

   protected AABB getBoundingBoxForCulling(T var1) {
      return var1.getBoundingBox();
   }

   protected boolean affectedByCulling(T var1) {
      return true;
   }

   public Vec3 getRenderOffset(S var1) {
      return var1.passengerOffset != null ? var1.passengerOffset : Vec3.ZERO;
   }

   public void render(S var1, PoseStack var2, MultiBufferSource var3, int var4) {
      EntityRenderState.LeashState var5 = var1.leashState;
      if (var5 != null) {
         renderLeash(var2, var3, var5);
      }

      if (var1.nameTag != null) {
         this.renderNameTag(var1, var1.nameTag, var2, var3, var4);
      }

   }

   private static void renderLeash(PoseStack var0, MultiBufferSource var1, EntityRenderState.LeashState var2) {
      float var3 = 0.025F;
      float var4 = (float)(var2.end.x - var2.start.x);
      float var5 = (float)(var2.end.y - var2.start.y);
      float var6 = (float)(var2.end.z - var2.start.z);
      float var7 = Mth.invSqrt(var4 * var4 + var6 * var6) * 0.025F / 2.0F;
      float var8 = var6 * var7;
      float var9 = var4 * var7;
      var0.pushPose();
      var0.translate(var2.offset);
      VertexConsumer var10 = var1.getBuffer(RenderType.leash());
      Matrix4f var11 = var0.last().pose();

      int var12;
      for(var12 = 0; var12 <= 24; ++var12) {
         addVertexPair(var10, var11, var4, var5, var6, var2.startBlockLight, var2.endBlockLight, var2.startSkyLight, var2.endSkyLight, 0.025F, 0.025F, var8, var9, var12, false);
      }

      for(var12 = 24; var12 >= 0; --var12) {
         addVertexPair(var10, var11, var4, var5, var6, var2.startBlockLight, var2.endBlockLight, var2.startSkyLight, var2.endSkyLight, 0.025F, 0.0F, var8, var9, var12, true);
      }

      var0.popPose();
   }

   private static void addVertexPair(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, int var5, int var6, int var7, int var8, float var9, float var10, float var11, float var12, int var13, boolean var14) {
      float var15 = (float)var13 / 24.0F;
      int var16 = (int)Mth.lerp(var15, (float)var5, (float)var6);
      int var17 = (int)Mth.lerp(var15, (float)var7, (float)var8);
      int var18 = LightTexture.pack(var16, var17);
      float var19 = var13 % 2 == (var14 ? 1 : 0) ? 0.7F : 1.0F;
      float var20 = 0.5F * var19;
      float var21 = 0.4F * var19;
      float var22 = 0.3F * var19;
      float var23 = var2 * var15;
      float var24 = var3 > 0.0F ? var3 * var15 * var15 : var3 - var3 * (1.0F - var15) * (1.0F - var15);
      float var25 = var4 * var15;
      var0.addVertex(var1, var23 - var11, var24 + var10, var25 + var12).setColor(var20, var21, var22, 1.0F).setLight(var18);
      var0.addVertex(var1, var23 + var11, var24 + var9 - var10, var25 - var12).setColor(var20, var21, var22, 1.0F).setLight(var18);
   }

   protected boolean shouldShowName(T var1, double var2) {
      return var1.shouldShowName() || var1.hasCustomName() && var1 == this.entityRenderDispatcher.crosshairPickEntity;
   }

   public Font getFont() {
      return this.font;
   }

   protected void renderNameTag(S var1, Component var2, PoseStack var3, MultiBufferSource var4, int var5) {
      Vec3 var6 = var1.nameTagAttachment;
      if (var6 != null) {
         boolean var7 = !var1.isDiscrete;
         int var8 = "deadmau5".equals(var2.getString()) ? -10 : 0;
         var3.pushPose();
         var3.translate(var6.x, var6.y + 0.5, var6.z);
         var3.mulPose(this.entityRenderDispatcher.cameraOrientation());
         var3.scale(0.025F, -0.025F, 0.025F);
         Matrix4f var9 = var3.last().pose();
         Font var10 = this.getFont();
         float var11 = (float)(-var10.width((FormattedText)var2)) / 2.0F;
         int var12 = (int)(Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
         var10.drawInBatch(var2, var11, (float)var8, -2130706433, false, var9, var4, var7 ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, var12, var5);
         if (var7) {
            var10.drawInBatch((Component)var2, var11, (float)var8, -1, false, var9, var4, Font.DisplayMode.NORMAL, 0, LightTexture.lightCoordsWithEmission(var5, 2));
         }

         var3.popPose();
      }
   }

   @Nullable
   protected Component getNameTag(T var1) {
      return var1.getDisplayName();
   }

   protected float getShadowRadius(S var1) {
      return this.shadowRadius;
   }

   protected float getShadowStrength(S var1) {
      return this.shadowStrength;
   }

   public abstract S createRenderState();

   public final S createRenderState(T var1, float var2) {
      EntityRenderState var3 = this.reusedState;
      this.extractRenderState(var1, var3, var2);
      return var3;
   }

   public void extractRenderState(T var1, S var2, float var3) {
      label44: {
         var2.x = Mth.lerp((double)var3, var1.xOld, var1.getX());
         var2.y = Mth.lerp((double)var3, var1.yOld, var1.getY());
         var2.z = Mth.lerp((double)var3, var1.zOld, var1.getZ());
         var2.isInvisible = var1.isInvisible();
         var2.ageInTicks = (float)var1.tickCount + var3;
         var2.boundingBoxWidth = var1.getBbWidth();
         var2.boundingBoxHeight = var1.getBbHeight();
         var2.eyeHeight = var1.getEyeHeight();
         if (var1.isPassenger()) {
            Entity var6 = var1.getVehicle();
            if (var6 instanceof AbstractMinecart) {
               AbstractMinecart var4 = (AbstractMinecart)var6;
               MinecartBehavior var14 = var4.getBehavior();
               if (var14 instanceof NewMinecartBehavior) {
                  NewMinecartBehavior var5 = (NewMinecartBehavior)var14;
                  if (var5.cartHasPosRotLerp()) {
                     double var15 = Mth.lerp((double)var3, var4.xOld, var4.getX());
                     double var8 = Mth.lerp((double)var3, var4.yOld, var4.getY());
                     double var10 = Mth.lerp((double)var3, var4.zOld, var4.getZ());
                     var2.passengerOffset = var5.getCartLerpPosition(var3).subtract(new Vec3(var15, var8, var10));
                     break label44;
                  }
               }
            }
         }

         var2.passengerOffset = null;
      }

      var2.distanceToCameraSq = this.entityRenderDispatcher.distanceToSqr(var1);
      boolean var12 = var2.distanceToCameraSq < 4096.0 && this.shouldShowName(var1, var2.distanceToCameraSq);
      if (var12) {
         var2.nameTag = this.getNameTag(var1);
         var2.nameTagAttachment = var1.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, var1.getYRot(var3));
      } else {
         var2.nameTag = null;
      }

      var2.isDiscrete = var1.isDiscrete();
      Entity var10000;
      if (var1 instanceof Leashable var16) {
         var10000 = var16.getLeashHolder();
      } else {
         var10000 = null;
      }

      Entity var13 = var10000;
      if (var13 != null) {
         float var17 = var1.getPreciseBodyRotation(var3) * 0.017453292F;
         Vec3 var7 = var1.getLeashOffset(var3).yRot(-var17);
         BlockPos var18 = BlockPos.containing(var1.getEyePosition(var3));
         BlockPos var9 = BlockPos.containing(var13.getEyePosition(var3));
         if (var2.leashState == null) {
            var2.leashState = new EntityRenderState.LeashState();
         }

         EntityRenderState.LeashState var19 = var2.leashState;
         var19.offset = var7;
         var19.start = var1.getPosition(var3).add(var7);
         var19.end = var13.getRopeHoldPosition(var3);
         var19.startBlockLight = this.getBlockLightLevel(var1, var18);
         var19.endBlockLight = this.entityRenderDispatcher.getRenderer(var13).getBlockLightLevel(var13, var9);
         var19.startSkyLight = var1.level().getBrightness(LightLayer.SKY, var18);
         var19.endSkyLight = var1.level().getBrightness(LightLayer.SKY, var9);
      } else {
         var2.leashState = null;
      }

      var2.displayFireAnimation = var1.displayFireAnimation();
   }
}
