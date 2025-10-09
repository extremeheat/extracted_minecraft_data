package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.joml.Quaternionfc;

public abstract class LivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> implements RenderLayerParent<S, M> {
   private static final float EYE_BED_OFFSET = 0.1F;
   protected M model;
   protected final ItemModelResolver itemModelResolver;
   protected final List<RenderLayer<S, M>> layers = Lists.newArrayList();

   public LivingEntityRenderer(EntityRendererProvider.Context var1, M var2, float var3) {
      super(var1);
      this.itemModelResolver = var1.getItemModelResolver();
      this.model = var2;
      this.shadowRadius = var3;
   }

   protected final boolean addLayer(RenderLayer<S, M> var1) {
      return this.layers.add(var1);
   }

   public M getModel() {
      return this.model;
   }

   protected AABB getBoundingBoxForCulling(T var1) {
      AABB var2 = super.getBoundingBoxForCulling(var1);
      if (var1.getItemBySlot(EquipmentSlot.HEAD).is(Items.DRAGON_HEAD)) {
         float var3 = 0.5F;
         return var2.inflate(0.5, 0.5, 0.5);
      } else {
         return var2;
      }
   }

   public void submit(S var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      var2.pushPose();
      if (var1.hasPose(Pose.SLEEPING)) {
         Direction var5 = var1.bedOrientation;
         if (var5 != null) {
            float var6 = var1.eyeHeight - 0.1F;
            var2.translate((float)(-var5.getStepX()) * var6, 0.0F, (float)(-var5.getStepZ()) * var6);
         }
      }

      float var12 = var1.scale;
      var2.scale(var12, var12, var12);
      this.setupRotations(var1, var2, var1.bodyRot, var12);
      var2.scale(-1.0F, -1.0F, 1.0F);
      this.scale(var1, var2);
      var2.translate(0.0F, -1.501F, 0.0F);
      boolean var13 = this.isBodyVisible(var1);
      boolean var7 = !var13 && !var1.isInvisibleToPlayer;
      RenderType var8 = this.getRenderType(var1, var13, var7, var1.appearsGlowing());
      if (var8 != null) {
         int var9 = getOverlayCoords(var1, this.getWhiteOverlayProgress(var1));
         int var10 = var7 ? 654311423 : -1;
         int var11 = ARGB.multiply(var10, this.getModelTint(var1));
         var3.submitModel(this.model, var1, var2, var8, var1.lightCoords, var9, var11, (TextureAtlasSprite)null, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      }

      if (this.shouldRenderLayers(var1) && !this.layers.isEmpty()) {
         this.model.setupAnim(var1);

         for(RenderLayer var15 : this.layers) {
            var15.submit(var2, var3, var1.lightCoords, var1, var1.yRot, var1.xRot);
         }
      }

      var2.popPose();
      super.submit(var1, var2, var3, var4);
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
      ResourceLocation var5 = this.getTextureLocation(var1);
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
         case SOUTH -> {
            return 90.0F;
         }
         case WEST -> {
            return 0.0F;
         }
         case NORTH -> {
            return 270.0F;
         }
         case EAST -> {
            return 180.0F;
         }
         default -> {
            return 0.0F;
         }
      }
   }

   protected boolean isShaking(S var1) {
      return var1.isFullyFrozen;
   }

   protected void setupRotations(S var1, PoseStack var2, float var3, float var4) {
      if (this.isShaking(var1)) {
         var3 += (float)(Math.cos((double)((float)Mth.floor(var1.ageInTicks) * 3.25F)) * 3.141592653589793 * 0.4000000059604645);
      }

      if (!var1.hasPose(Pose.SLEEPING)) {
         var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F - var3));
      }

      if (var1.deathTime > 0.0F) {
         float var5 = (var1.deathTime - 1.0F) / 20.0F * 1.6F;
         var5 = Mth.sqrt(var5);
         if (var5 > 1.0F) {
            var5 = 1.0F;
         }

         var2.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(var5 * this.getFlipDegrees()));
      } else if (var1.isAutoSpinAttack) {
         var2.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-90.0F - var1.xRot));
         var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var1.ageInTicks * -75.0F));
      } else if (var1.hasPose(Pose.SLEEPING)) {
         Direction var8 = var1.bedOrientation;
         float var6 = var8 != null ? sleepDirectionToRotation(var8) : var3;
         var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var6));
         var2.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(this.getFlipDegrees()));
         var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(270.0F));
      } else if (var1.isUpsideDown) {
         var2.translate(0.0F, (var1.boundingBoxHeight + 0.1F) / var4, 0.0F);
         var2.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(180.0F));
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
            Team.Visibility var9 = ((Team)var7).getNameTagVisibility();
            switch (var9) {
               case ALWAYS -> {
                  return var6;
               }
               case NEVER -> {
                  return false;
               }
               case HIDE_FOR_OTHER_TEAMS -> {
                  return var8 == null ? var6 : ((Team)var7).isAlliedTo(var8) && (((Team)var7).canSeeFriendlyInvisibles() || var6);
               }
               case HIDE_FOR_OWN_TEAM -> {
                  return var8 == null ? var6 : !((Team)var7).isAlliedTo(var8) && var6;
               }
               default -> {
                  return true;
               }
            }
         }
      }

      return Minecraft.renderNames() && var1 != var10.getCameraEntity() && var6 && !var1.isVehicle();
   }

   public boolean isEntityUpsideDown(T var1) {
      Component var2 = var1.getCustomName();
      return var2 != null && isUpsideDownName(var2.getString());
   }

   protected static boolean isUpsideDownName(String var0) {
      return "Dinnerbone".equals(var0) || "Grumm".equals(var0);
   }

   protected float getShadowRadius(S var1) {
      return super.getShadowRadius(var1) * var1.scale;
   }

   public void extractRenderState(T var1, S var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      float var4 = Mth.rotLerp(var3, var1.yHeadRotO, var1.yHeadRot);
      var2.bodyRot = solveBodyRot(var1, var4, var3);
      var2.yRot = Mth.wrapDegrees(var4 - var2.bodyRot);
      var2.xRot = var1.getXRot(var3);
      var2.isUpsideDown = this.isEntityUpsideDown(var1);
      if (var2.isUpsideDown) {
         var2.xRot *= -1.0F;
         var2.yRot *= -1.0F;
      }

      if (!var1.isPassenger() && var1.isAlive()) {
         var2.walkAnimationPos = var1.walkAnimation.position(var3);
         var2.walkAnimationSpeed = var1.walkAnimation.speed(var3);
      } else {
         var2.walkAnimationPos = 0.0F;
         var2.walkAnimationSpeed = 0.0F;
      }

      Entity var6 = var1.getVehicle();
      if (var6 instanceof LivingEntity var5) {
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

      label48: {
         var2.isFullyFrozen = var1.isFullyFrozen();
         var2.isBaby = var1.isBaby();
         var2.isInWater = var1.isInWater();
         var2.isAutoSpinAttack = var1.isAutoSpinAttack();
         var2.hasRedOverlay = var1.hurtTime > 0 || var1.deathTime > 0;
         ItemStack var9 = var1.getItemBySlot(EquipmentSlot.HEAD);
         Item var8 = var9.getItem();
         if (var8 instanceof BlockItem var10) {
            Block var12 = var10.getBlock();
            if (var12 instanceof AbstractSkullBlock var7) {
               var2.wornHeadType = var7.getType();
               var2.wornHeadProfile = (ResolvableProfile)var9.get(DataComponents.PROFILE);
               var2.headItem.clear();
               break label48;
            }
         }

         var2.wornHeadType = null;
         var2.wornHeadProfile = null;
         if (!HumanoidArmorLayer.shouldRender(var9, EquipmentSlot.HEAD)) {
            this.itemModelResolver.updateForLiving(var2.headItem, var9, ItemDisplayContext.HEAD, var1);
         } else {
            var2.headItem.clear();
         }
      }

      var2.deathTime = var1.deathTime > 0 ? (float)var1.deathTime + var3 : 0.0F;
      Minecraft var11 = Minecraft.getInstance();
      var2.isInvisibleToPlayer = var2.isInvisible && var1.isInvisibleTo(var11.player);
   }

   private static float solveBodyRot(LivingEntity var0, float var1, float var2) {
      Entity var4 = var0.getVehicle();
      if (var4 instanceof LivingEntity var3) {
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

   // $FF: synthetic method
   protected float getShadowRadius(final EntityRenderState var1) {
      return this.getShadowRadius((LivingEntityRenderState)var1);
   }
}
