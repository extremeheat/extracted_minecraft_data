package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HitboxRenderState;
import net.minecraft.client.renderer.entity.state.HitboxesRenderState;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartBehavior;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class EntityRenderer<T extends Entity, S extends EntityRenderState> {
   private static final float SHADOW_POWER_FALLOFF_Y = 0.5F;
   private static final float MAX_SHADOW_RADIUS = 32.0F;
   public static final float NAMETAG_SCALE = 0.025F;
   protected final EntityRenderDispatcher entityRenderDispatcher;
   private final Font font;
   protected float shadowRadius;
   protected float shadowStrength = 1.0F;

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

   public void submit(S var1, PoseStack var2, SubmitNodeCollector var3) {
      if (var1.leashStates != null) {
         for(EntityRenderState.LeashState var5 : var1.leashStates) {
            var3.submitLeash(var2, var5);
         }
      }

      this.submitNameTag(var1, var2, var3);
   }

   protected boolean shouldShowName(T var1, double var2) {
      return var1.shouldShowName() || var1.hasCustomName() && var1 == this.entityRenderDispatcher.crosshairPickEntity;
   }

   public Font getFont() {
      return this.font;
   }

   protected void submitNameTag(S var1, PoseStack var2, SubmitNodeCollector var3) {
      if (var1.nameTag != null) {
         var3.submitNameTag(var2, var1.nameTagAttachment, var1.nameTag, !var1.isDiscrete, var1.lightCoords, var1.distanceToCameraSq);
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
      EntityRenderState var3 = this.createRenderState();
      this.extractRenderState(var1, var3, var2);
      return (S)var3;
   }

   public void extractRenderState(T var1, S var2, float var3) {
      label95: {
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
                     break label95;
                  }
               }
            }
         }

         var2.passengerOffset = null;
      }

      var2.distanceToCameraSq = this.entityRenderDispatcher.distanceToSqr(var1);
      boolean var24 = var2.distanceToCameraSq < 4096.0 && this.shouldShowName(var1, var2.distanceToCameraSq);
      if (var24) {
         var2.nameTag = this.getNameTag(var1);
         var2.nameTagAttachment = var1.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, var1.getYRot(var3));
      } else {
         var2.nameTag = null;
      }

      Level var25;
      label82: {
         var2.isDiscrete = var1.isDiscrete();
         var25 = var1.level();
         if (var1 instanceof Leashable var28) {
            Entity var31 = var28.getLeashHolder();
            if (var31 instanceof Entity) {
               float var32 = var1.getPreciseBodyRotation(var3) * 0.017453292F;
               Vec3 var9 = var28.getLeashOffset(var3);
               BlockPos var33 = BlockPos.containing(var1.getEyePosition(var3));
               BlockPos var11 = BlockPos.containing(var31.getEyePosition(var3));
               int var12 = this.getBlockLightLevel(var1, var33);
               int var13 = this.entityRenderDispatcher.getRenderer(var31).getBlockLightLevel(var31, var11);
               int var14 = var25.getBrightness(LightLayer.SKY, var33);
               int var15 = var25.getBrightness(LightLayer.SKY, var11);
               boolean var16 = var31.supportQuadLeashAsHolder() && var28.supportQuadLeash();
               int var17 = var16 ? 4 : 1;
               if (var2.leashStates == null || var2.leashStates.size() != var17) {
                  var2.leashStates = new ArrayList(var17);

                  for(int var18 = 0; var18 < var17; ++var18) {
                     var2.leashStates.add(new EntityRenderState.LeashState());
                  }
               }

               if (var16) {
                  float var34 = var31.getPreciseBodyRotation(var3) * 0.017453292F;
                  Vec3 var19 = var31.getPosition(var3);
                  Vec3[] var20 = var28.getQuadLeashOffsets();
                  Vec3[] var21 = var31.getQuadLeashHolderOffsets();
                  int var22 = 0;

                  while(true) {
                     if (var22 >= var17) {
                        break label82;
                     }

                     EntityRenderState.LeashState var23 = (EntityRenderState.LeashState)var2.leashStates.get(var22);
                     var23.offset = var20[var22].yRot(-var32);
                     var23.start = var1.getPosition(var3).add(var23.offset);
                     var23.end = var19.add(var21[var22].yRot(-var34));
                     var23.startBlockLight = var12;
                     var23.endBlockLight = var13;
                     var23.startSkyLight = var14;
                     var23.endSkyLight = var15;
                     var23.slack = false;
                     ++var22;
                  }
               } else {
                  Vec3 var35 = var9.yRot(-var32);
                  EntityRenderState.LeashState var36 = (EntityRenderState.LeashState)var2.leashStates.getFirst();
                  var36.offset = var35;
                  var36.start = var1.getPosition(var3).add(var35);
                  var36.end = var31.getRopeHoldPosition(var3);
                  var36.startBlockLight = var12;
                  var36.endBlockLight = var13;
                  var36.startSkyLight = var14;
                  var36.endSkyLight = var15;
                  break label82;
               }
            }
         }

         var2.leashStates = null;
      }

      var2.displayFireAnimation = var1.displayFireAnimation();
      Minecraft var29 = Minecraft.getInstance();
      boolean var30 = var29.shouldEntityAppearGlowing(var1);
      var2.outlineColor = var30 ? ARGB.opaque(var1.getTeamColor()) : 0;
      if (var29.debugEntries.isCurrentlyEnabled(DebugScreenEntries.ENTITY_HITBOXES) && !var2.isInvisible && !var29.showOnlyReducedInfo()) {
         this.extractHitboxes(var1, var2, var3);
      } else {
         var2.hitboxesRenderState = null;
         var2.serverHitboxesRenderState = null;
      }

      var2.lightCoords = this.getPackedLightCoords(var1, var3);
      this.extractShadow(var2, var29, var25);
   }

   private void extractShadow(S var1, Minecraft var2, Level var3) {
      var1.shadowPieces.clear();
      if ((Boolean)var2.options.entityShadows().get() && !var1.isInvisible) {
         float var4 = Math.min(this.getShadowRadius(var1), 32.0F);
         var1.shadowRadius = var4;
         if (var4 > 0.0F) {
            double var5 = var1.distanceToCameraSq;
            float var7 = (float)((1.0 - var5 / 256.0) * (double)this.getShadowStrength(var1));
            if (var7 > 0.0F) {
               int var8 = Mth.floor(var1.x - (double)var4);
               int var9 = Mth.floor(var1.x + (double)var4);
               int var10 = Mth.floor(var1.z - (double)var4);
               int var11 = Mth.floor(var1.z + (double)var4);
               float var12 = Math.min(var7 / 0.5F - 1.0F, var4);
               int var13 = Mth.floor(var1.y - (double)var12);
               int var14 = Mth.floor(var1.y);
               BlockPos.MutableBlockPos var15 = new BlockPos.MutableBlockPos();

               for(int var16 = var10; var16 <= var11; ++var16) {
                  for(int var17 = var8; var17 <= var9; ++var17) {
                     var15.set(var17, 0, var16);
                     ChunkAccess var18 = var3.getChunk(var15);

                     for(int var19 = var13; var19 <= var14; ++var19) {
                        var15.setY(var19);
                        this.extractShadowPiece(var1, var3, var7, var15, var18);
                     }
                  }
               }
            }
         }
      } else {
         var1.shadowRadius = 0.0F;
      }

   }

   private void extractShadowPiece(S var1, Level var2, float var3, BlockPos.MutableBlockPos var4, ChunkAccess var5) {
      float var6 = var3 - (float)(var1.y - (double)var4.getY()) * 0.5F;
      BlockPos var7 = var4.below();
      BlockState var8 = var5.getBlockState(var7);
      if (var8.getRenderShape() != RenderShape.INVISIBLE) {
         int var9 = var2.getMaxLocalRawBrightness(var4);
         if (var9 > 3) {
            if (var8.isCollisionShapeFullBlock(var5, var7)) {
               VoxelShape var10 = var8.getShape(var5, var7);
               if (!var10.isEmpty()) {
                  float var11 = Mth.clamp(var6 * 0.5F * LightTexture.getBrightness(var2.dimensionType(), var9), 0.0F, 1.0F);
                  float var12 = (float)((double)var4.getX() - var1.x);
                  float var13 = (float)((double)var4.getY() - var1.y);
                  float var14 = (float)((double)var4.getZ() - var1.z);
                  var1.shadowPieces.add(new EntityRenderState.ShadowPiece(var12, var13, var14, var10, var11));
               }
            }
         }
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
