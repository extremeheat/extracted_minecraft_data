package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HitboxRenderState;
import net.minecraft.client.renderer.entity.state.HitboxesRenderState;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.server.level.ServerLevel;
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
import org.joml.Quaternionfc;

public abstract class EntityRenderer<T extends Entity, S extends EntityRenderState> {
   protected static final float NAMETAG_SCALE = 0.025F;
   public static final int LEASH_RENDER_STEPS = 24;
   public static final float LEASH_WIDTH = 0.05F;
   protected final EntityRenderDispatcher entityRenderDispatcher;
   private final Font font;
   protected float shadowRadius;
   protected float shadowStrength = 1.0F;
   private final S reusedState = (S)this.createRenderState();

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
                  AABB var12 = this.entityRenderDispatcher.getRenderer(var11).getBoundingBoxForCulling(var11);
                  return var2.isVisible(var12) || var2.isVisible(var9.minmax(var12));
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
      if (var1.leashStates != null) {
         for(EntityRenderState.LeashState var6 : var1.leashStates) {
            renderLeash(var2, var3, var6);
         }
      }

      if (var1.nameTag != null) {
         this.renderNameTag(var1, var1.nameTag, var2, var3, var4);
      }

   }

   private static void renderLeash(PoseStack var0, MultiBufferSource var1, EntityRenderState.LeashState var2) {
      float var3 = (float)(var2.end.x - var2.start.x);
      float var4 = (float)(var2.end.y - var2.start.y);
      float var5 = (float)(var2.end.z - var2.start.z);
      float var6 = Mth.invSqrt(var3 * var3 + var5 * var5) * 0.05F / 2.0F;
      float var7 = var5 * var6;
      float var8 = var3 * var6;
      var0.pushPose();
      var0.translate(var2.offset);
      VertexConsumer var9 = var1.getBuffer(RenderType.leash());
      Matrix4f var10 = var0.last().pose();

      for(int var11 = 0; var11 <= 24; ++var11) {
         addVertexPair(var9, var10, var3, var4, var5, 0.05F, 0.05F, var7, var8, var11, false, var2);
      }

      for(int var12 = 24; var12 >= 0; --var12) {
         addVertexPair(var9, var10, var3, var4, var5, 0.05F, 0.0F, var7, var8, var12, true, var2);
      }

      var0.popPose();
   }

   private static void addVertexPair(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, boolean var10, EntityRenderState.LeashState var11) {
      float var12 = (float)var9 / 24.0F;
      int var13 = (int)Mth.lerp(var12, (float)var11.startBlockLight, (float)var11.endBlockLight);
      int var14 = (int)Mth.lerp(var12, (float)var11.startSkyLight, (float)var11.endSkyLight);
      int var15 = LightTexture.pack(var13, var14);
      float var16 = var9 % 2 == (var10 ? 1 : 0) ? 0.7F : 1.0F;
      float var17 = 0.5F * var16;
      float var18 = 0.4F * var16;
      float var19 = 0.3F * var16;
      float var20 = var2 * var12;
      float var21;
      if (var11.slack) {
         var21 = var3 > 0.0F ? var3 * var12 * var12 : var3 - var3 * (1.0F - var12) * (1.0F - var12);
      } else {
         var21 = var3 * var12;
      }

      float var22 = var4 * var12;
      var0.addVertex(var1, var20 - var7, var21 + var6, var22 + var8).setColor(var17, var18, var19, 1.0F).setLight(var15);
      var0.addVertex(var1, var20 + var7, var21 + var5 - var6, var22 - var8).setColor(var17, var18, var19, 1.0F).setLight(var15);
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
         var3.mulPose((Quaternionfc)this.entityRenderDispatcher.cameraOrientation());
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
      return (S)var3;
   }

   public void extractRenderState(T var1, S var2, float var3) {
      label90: {
         var2.entityType = var1.getType();
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
               MinecartBehavior var26 = var4.getBehavior();
               if (var26 instanceof NewMinecartBehavior) {
                  NewMinecartBehavior var5 = (NewMinecartBehavior)var26;
                  if (var5.cartHasPosRotLerp()) {
                     double var27 = Mth.lerp((double)var3, var4.xOld, var4.getX());
                     double var8 = Mth.lerp((double)var3, var4.yOld, var4.getY());
                     double var10 = Mth.lerp((double)var3, var4.zOld, var4.getZ());
                     var2.passengerOffset = var5.getCartLerpPosition(var3).subtract(new Vec3(var27, var8, var10));
                     break label90;
                  }
               }
            }
         }

         var2.passengerOffset = null;
      }

      var2.distanceToCameraSq = this.entityRenderDispatcher.distanceToSqr(var1);
      boolean var23 = var2.distanceToCameraSq < 4096.0 && this.shouldShowName(var1, var2.distanceToCameraSq);
      if (var23) {
         var2.nameTag = this.getNameTag(var1);
         var2.nameTagAttachment = var1.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, var1.getYRot(var3));
      } else {
         var2.nameTag = null;
      }

      label77: {
         var2.isDiscrete = var1.isDiscrete();
         if (var1 instanceof Leashable var24) {
            Entity var7 = var24.getLeashHolder();
            if (var7 instanceof Entity) {
               float var29 = var1.getPreciseBodyRotation(var3) * 0.017453292F;
               Vec3 var30 = var24.getLeashOffset(var3);
               BlockPos var9 = BlockPos.containing(var1.getEyePosition(var3));
               BlockPos var31 = BlockPos.containing(var7.getEyePosition(var3));
               int var11 = this.getBlockLightLevel(var1, var9);
               int var12 = this.entityRenderDispatcher.getRenderer(var7).getBlockLightLevel(var7, var31);
               int var13 = var1.level().getBrightness(LightLayer.SKY, var9);
               int var14 = var1.level().getBrightness(LightLayer.SKY, var31);
               boolean var15 = var7.supportQuadLeashAsHolder() && var24.supportQuadLeash();
               int var16 = var15 ? 4 : 1;
               if (var2.leashStates == null || var2.leashStates.size() != var16) {
                  var2.leashStates = new ArrayList(var16);

                  for(int var17 = 0; var17 < var16; ++var17) {
                     var2.leashStates.add(new EntityRenderState.LeashState());
                  }
               }

               if (var15) {
                  float var32 = var7.getPreciseBodyRotation(var3) * 0.017453292F;
                  Vec3 var18 = var7.getPosition(var3);
                  Vec3[] var19 = var24.getQuadLeashOffsets();
                  Vec3[] var20 = var7.getQuadLeashHolderOffsets();
                  int var21 = 0;

                  while(true) {
                     if (var21 >= var16) {
                        break label77;
                     }

                     EntityRenderState.LeashState var22 = (EntityRenderState.LeashState)var2.leashStates.get(var21);
                     var22.offset = var19[var21].yRot(-var29);
                     var22.start = var1.getPosition(var3).add(var22.offset);
                     var22.end = var18.add(var20[var21].yRot(-var32));
                     var22.startBlockLight = var11;
                     var22.endBlockLight = var12;
                     var22.startSkyLight = var13;
                     var22.endSkyLight = var14;
                     var22.slack = false;
                     ++var21;
                  }
               } else {
                  Vec3 var33 = var30.yRot(-var29);
                  EntityRenderState.LeashState var34 = (EntityRenderState.LeashState)var2.leashStates.getFirst();
                  var34.offset = var33;
                  var34.start = var1.getPosition(var3).add(var33);
                  var34.end = var7.getRopeHoldPosition(var3);
                  var34.startBlockLight = var11;
                  var34.endBlockLight = var12;
                  var34.startSkyLight = var13;
                  var34.endSkyLight = var14;
                  break label77;
               }
            }
         }

         var2.leashStates = null;
      }

      var2.displayFireAnimation = var1.displayFireAnimation();
      Minecraft var25 = Minecraft.getInstance();
      if (var25.getEntityRenderDispatcher().shouldRenderHitBoxes() && !var2.isInvisible && !var25.showOnlyReducedInfo()) {
         this.extractHitboxes(var1, var2, var3);
      } else {
         var2.hitboxesRenderState = null;
         var2.serverHitboxesRenderState = null;
      }

   }

   private void extractHitboxes(T var1, S var2, float var3) {
      var2.hitboxesRenderState = this.extractHitboxes(var1, var3, false);
      var2.serverHitboxesRenderState = null;
   }

   private HitboxesRenderState extractHitboxes(T var1, float var2, boolean var3) {
      ImmutableList.Builder var4 = new ImmutableList.Builder();
      AABB var5 = var1.getBoundingBox();
      HitboxRenderState var6;
      if (var3) {
         var6 = new HitboxRenderState(var5.minX - var1.getX(), var5.minY - var1.getY(), var5.minZ - var1.getZ(), var5.maxX - var1.getX(), var5.maxY - var1.getY(), var5.maxZ - var1.getZ(), 0.0F, 1.0F, 0.0F);
      } else {
         var6 = new HitboxRenderState(var5.minX - var1.getX(), var5.minY - var1.getY(), var5.minZ - var1.getZ(), var5.maxX - var1.getX(), var5.maxY - var1.getY(), var5.maxZ - var1.getZ(), 1.0F, 1.0F, 1.0F);
      }

      var4.add(var6);
      Entity var7 = var1.getVehicle();
      if (var7 != null) {
         float var8 = Math.min(var7.getBbWidth(), var1.getBbWidth()) / 2.0F;
         float var9 = 0.0625F;
         Vec3 var10 = var7.getPassengerRidingPosition(var1).subtract(var1.position());
         HitboxRenderState var11 = new HitboxRenderState(var10.x - (double)var8, var10.y, var10.z - (double)var8, var10.x + (double)var8, var10.y + 0.0625, var10.z + (double)var8, 1.0F, 1.0F, 0.0F);
         var4.add(var11);
      }

      this.extractAdditionalHitboxes(var1, var4, var2);
      Vec3 var12 = var1.getViewVector(var2);
      return new HitboxesRenderState(var12.x, var12.y, var12.z, var4.build());
   }

   protected void extractAdditionalHitboxes(T var1, ImmutableList.Builder<HitboxRenderState> var2, float var3) {
   }

   @Nullable
   private static Entity getServerSideEntity(Entity var0) {
      IntegratedServer var1 = Minecraft.getInstance().getSingleplayerServer();
      if (var1 != null) {
         ServerLevel var2 = var1.getLevel(var0.level().dimension());
         if (var2 != null) {
            return var2.getEntity(var0.getId());
         }
      }

      return null;
   }
}
