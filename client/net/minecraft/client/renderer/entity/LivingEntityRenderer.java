package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

public abstract class LivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
   extends EntityRenderer<T, S>
   implements RenderLayerParent<S, M> {
   private static final float EYE_BED_OFFSET = 0.1F;
   protected M model;
   protected final ItemRenderer itemRenderer;
   protected final List<RenderLayer<S, M>> layers = Lists.newArrayList();

   public LivingEntityRenderer(EntityRendererProvider.Context var1, M var2, float var3) {
      super(var1);
      this.itemRenderer = var1.getItemRenderer();
      this.model = (M)var2;
      this.shadowRadius = var3;
   }

   protected final boolean addLayer(RenderLayer<S, M> var1) {
      return this.layers.add(var1);
   }

   @Override
   public M getModel() {
      return this.model;
   }

   protected AABB getBoundingBoxForCulling(T var1) {
      AABB var2 = super.getBoundingBoxForCulling((T)var1);
      if (var1.getItemBySlot(EquipmentSlot.HEAD).is(Items.DRAGON_HEAD)) {
         float var3 = 0.5F;
         return var2.inflate(0.5, 0.5, 0.5);
      } else {
         return var2;
      }
   }

   public void render(S var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      if (var1.hasPose(Pose.SLEEPING)) {
         Direction var5 = var1.bedOrientation;
         if (var5 != null) {
            float var6 = var1.eyeHeight - 0.1F;
            var2.translate((float)(-var5.getStepX()) * var6, 0.0F, (float)(-var5.getStepZ()) * var6);
         }
      }

      float var13 = var1.scale;
      var2.scale(var13, var13, var13);
      this.setupRotations((S)var1, var2, var1.bodyRot, var13);
      var2.scale(-1.0F, -1.0F, 1.0F);
      this.scale((S)var1, var2);
      var2.translate(0.0F, -1.501F, 0.0F);
      this.model.setupAnim((S)var1);
      boolean var14 = this.isBodyVisible((S)var1);
      boolean var7 = !var14 && !var1.isInvisibleToPlayer;
      RenderType var8 = this.getRenderType((S)var1, var14, var7, var1.appearsGlowing);
      if (var8 != null) {
         VertexConsumer var9 = var3.getBuffer(var8);
         int var10 = getOverlayCoords(var1, this.getWhiteOverlayProgress((S)var1));
         int var11 = var7 ? 654311423 : -1;
         int var12 = ARGB.multiply(var11, this.getModelTint((S)var1));
         this.model.renderToBuffer(var2, var9, var4, var10, var12);
      }

      if (this.shouldRenderLayers((S)var1)) {
         for (RenderLayer var16 : this.layers) {
            var16.render(var2, var3, var4, var1, var1.yRot, var1.xRot);
         }
      }

      var2.popPose();
      super.render((S)var1, var2, var3, var4);
   }

   protected boolean shouldRenderLayers(S var1) {
      return true;
   }

   protected int getModelTint(S var1) {
      return -1;
   }

   public abstract ResourceLocation getTextureLocation(S var1);

   @Nullable
   protected RenderType getRenderType(S var1, boolean var2, boolean var3, boolean var4) {
      ResourceLocation var5 = this.getTextureLocation((S)var1);
      if (var3) {
         return RenderType.itemEntityTranslucentCull(var5);
      } else if (var2) {
         return this.model.renderType(var5);
      } else {
         return var4 ? RenderType.outline(var5) : null;
      }
   }

   public static int getOverlayCoords(LivingEntityRenderState var0, float var1) {
      return OverlayTexture.pack(OverlayTexture.u(var1), OverlayTexture.v(var0.hasRedOverlay));
   }

   protected boolean isBodyVisible(S var1) {
      return !var1.isInvisible;
   }

   private static float sleepDirectionToRotation(Direction var0) {
      switch (var0) {
         case SOUTH:
            return 90.0F;
         case WEST:
            return 0.0F;
         case NORTH:
            return 270.0F;
         case EAST:
            return 180.0F;
         default:
            return 0.0F;
      }
   }

   protected boolean isShaking(S var1) {
      return var1.isFullyFrozen;
   }

   protected void setupRotations(S var1, PoseStack var2, float var3, float var4) {
      if (this.isShaking((S)var1)) {
         var3 += (float)(Math.cos((double)((float)Mth.floor(var1.ageInTicks) * 3.25F)) * 3.141592653589793 * 0.4000000059604645);
      }

      if (!var1.hasPose(Pose.SLEEPING)) {
         var2.mulPose(Axis.YP.rotationDegrees(180.0F - var3));
      }

      if (var1.deathTime > 0.0F) {
         float var5 = (var1.deathTime - 1.0F) / 20.0F * 1.6F;
         var5 = Mth.sqrt(var5);
         if (var5 > 1.0F) {
            var5 = 1.0F;
         }

         var2.mulPose(Axis.ZP.rotationDegrees(var5 * this.getFlipDegrees()));
      } else if (var1.isAutoSpinAttack) {
         var2.mulPose(Axis.XP.rotationDegrees(-90.0F - var1.xRot));
         var2.mulPose(Axis.YP.rotationDegrees(var1.ageInTicks * -75.0F));
      } else if (var1.hasPose(Pose.SLEEPING)) {
         Direction var8 = var1.bedOrientation;
         float var6 = var8 != null ? sleepDirectionToRotation(var8) : var3;
         var2.mulPose(Axis.YP.rotationDegrees(var6));
         var2.mulPose(Axis.ZP.rotationDegrees(this.getFlipDegrees()));
         var2.mulPose(Axis.YP.rotationDegrees(270.0F));
      } else if (var1.isUpsideDown) {
         var2.translate(0.0F, (var1.boundingBoxHeight + 0.1F) / var4, 0.0F);
         var2.mulPose(Axis.ZP.rotationDegrees(180.0F));
      }
   }

   protected float getFlipDegrees() {
      return 90.0F;
   }

   protected float getWhiteOverlayProgress(S var1) {
      return 0.0F;
   }

   protected void scale(S var1, PoseStack var2) {
   }

   protected boolean shouldShowName(T var1, double var2) {
      if (var1.isDiscrete()) {
         float var4 = 32.0F;
         if (var2 >= 1024.0) {
            return false;
         }
      }

      Minecraft var10 = Minecraft.getInstance();
      LocalPlayer var5 = var10.player;
      boolean var6 = !var1.isInvisibleTo(var5);
      if (var1 != var5) {
         PlayerTeam var7 = var1.getTeam();
         PlayerTeam var8 = var5.getTeam();
         if (var7 != null) {
            Team.Visibility var9 = var7.getNameTagVisibility();
            switch (var9) {
               case ALWAYS:
                  return var6;
               case NEVER:
                  return false;
               case HIDE_FOR_OTHER_TEAMS:
                  return var8 == null ? var6 : var7.isAlliedTo(var8) && (var7.canSeeFriendlyInvisibles() || var6);
               case HIDE_FOR_OWN_TEAM:
                  return var8 == null ? var6 : !var7.isAlliedTo(var8) && var6;
               default:
                  return true;
            }
         }
      }

      return Minecraft.renderNames() && var1 != var10.getCameraEntity() && var6 && !var1.isVehicle();
   }

   public static boolean isEntityUpsideDown(LivingEntity var0) {
      if (var0 instanceof Player || var0.hasCustomName()) {
         String var1 = ChatFormatting.stripFormatting(var0.getName().getString());
         if ("Dinnerbone".equals(var1) || "Grumm".equals(var1)) {
            return !(var0 instanceof Player) || ((Player)var0).isModelPartShown(PlayerModelPart.CAPE);
         }
      }

      return false;
   }

   protected float getShadowRadius(S var1) {
      return super.getShadowRadius((S)var1) * var1.scale;
   }

   public void extractRenderState(T var1, S var2, float var3) {
      super.extractRenderState((T)var1, (S)var2, var3);
      float var4 = Mth.rotLerp(var3, var1.yHeadRotO, var1.yHeadRot);
      var2.bodyRot = solveBodyRot(var1, var4, var3);
      var2.yRot = Mth.wrapDegrees(var4 - var2.bodyRot);
      var2.xRot = var1.getXRot(var3);
      var2.customName = var1.getCustomName();
      var2.isUpsideDown = isEntityUpsideDown(var1);
      if (var2.isUpsideDown) {
         var2.xRot *= -1.0F;
         var2.yRot *= -1.0F;
      }

      var2.walkAnimationPos = var1.walkAnimation.position(var3);
      var2.walkAnimationSpeed = var1.walkAnimation.speed(var3);
      if (var1.getVehicle() instanceof LivingEntity var5) {
         var2.wornHeadAnimationPos = var5.walkAnimation.position(var3);
      } else {
         var2.wornHeadAnimationPos = var2.walkAnimationPos;
      }

      var2.scale = var1.getScale();
      var2.ageScale = var1.getAgeScale();
      var2.pose = var1.getPose();
      var2.bedOrientation = var1.getBedOrientation();
      if (var2.bedOrientation != null) {
         var2.eyeHeight = var1.getEyeHeight(Pose.STANDING);
      }

      var2.isFullyFrozen = var1.isFullyFrozen();
      var2.isBaby = var1.isBaby();
      var2.isInWater = var1.isInWater();
      var2.isAutoSpinAttack = var1.isAutoSpinAttack();
      var2.hasRedOverlay = var1.hurtTime > 0 || var1.deathTime > 0;
      ItemStack var9 = var1.getItemBySlot(EquipmentSlot.HEAD);
      var2.headItem = var9.copy();
      var2.headItemModel = this.itemRenderer.resolveItemModel(var9, var1, ItemDisplayContext.HEAD);
      var2.mainArm = var1.getMainArm();
      ItemStack var10 = var1.getItemHeldByArm(HumanoidArm.RIGHT);
      ItemStack var7 = var1.getItemHeldByArm(HumanoidArm.LEFT);
      var2.rightHandItem = var10.copy();
      var2.leftHandItem = var7.copy();
      var2.rightHandItemModel = this.itemRenderer.resolveItemModel(var10, var1, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
      var2.leftHandItemModel = this.itemRenderer.resolveItemModel(var7, var1, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
      var2.deathTime = var1.deathTime > 0 ? (float)var1.deathTime + var3 : 0.0F;
      Minecraft var8 = Minecraft.getInstance();
      var2.isInvisibleToPlayer = var2.isInvisible && var1.isInvisibleTo(var8.player);
      var2.appearsGlowing = var8.shouldEntityAppearGlowing(var1);
   }

   private static float solveBodyRot(LivingEntity var0, float var1, float var2) {
      if (var0.getVehicle() instanceof LivingEntity var3) {
         float var7 = Mth.rotLerp(var2, var3.yBodyRotO, var3.yBodyRot);
         float var5 = 85.0F;
         float var6 = Mth.clamp(Mth.wrapDegrees(var1 - var7), -85.0F, 85.0F);
         var7 = var1 - var6;
         if (Math.abs(var6) > 50.0F) {
            var7 += var6 * 0.2F;
         }

         return var7;
      } else {
         return Mth.rotLerp(var2, var0.yBodyRotO, var0.yBodyRot);
      }
   }
}
