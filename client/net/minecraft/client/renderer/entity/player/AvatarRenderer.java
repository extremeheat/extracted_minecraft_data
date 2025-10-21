package net.minecraft.client.renderer.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Objects;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.entity.ClientAvatarState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5EarsLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwingAnimationType;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class AvatarRenderer<AvatarlikeEntity extends Avatar & ClientAvatarEntity> extends LivingEntityRenderer<AvatarlikeEntity, AvatarRenderState, PlayerModel> {
   public AvatarRenderer(EntityRendererProvider.Context var1, boolean var2) {
      super(var1, new PlayerModel(var1.bakeLayer(var2 ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), var2), 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, ArmorModelSet.bake(var2 ? ModelLayers.PLAYER_SLIM_ARMOR : ModelLayers.PLAYER_ARMOR, var1.getModelSet(), (var1x) -> new PlayerModel(var1x, var2)), var1.getEquipmentRenderer()));
      this.addLayer(new PlayerItemInHandLayer(this));
      this.addLayer(new ArrowLayer(this, var1));
      this.addLayer(new Deadmau5EarsLayer(this, var1.getModelSet()));
      this.addLayer(new CapeLayer(this, var1.getModelSet(), var1.getEquipmentAssets()));
      this.addLayer(new CustomHeadLayer(this, var1.getModelSet(), var1.getPlayerSkinRenderCache()));
      this.addLayer(new WingsLayer(this, var1.getModelSet(), var1.getEquipmentRenderer()));
      this.addLayer(new ParrotOnShoulderLayer(this, var1.getModelSet()));
      this.addLayer(new SpinAttackEffectLayer(this, var1.getModelSet()));
      this.addLayer(new BeeStingerLayer(this, var1));
   }

   protected boolean shouldRenderLayers(AvatarRenderState var1) {
      return !var1.isSpectator;
   }

   public Vec3 getRenderOffset(AvatarRenderState var1) {
      Vec3 var2 = super.getRenderOffset(var1);
      return var1.isCrouching ? var2.add(0.0, (double)(var1.scale * -2.0F) / 16.0, 0.0) : var2;
   }

   private static HumanoidModel.ArmPose getArmPose(Avatar var0, HumanoidArm var1) {
      ItemStack var2 = var0.getItemInHand(InteractionHand.MAIN_HAND);
      ItemStack var3 = var0.getItemInHand(InteractionHand.OFF_HAND);
      HumanoidModel.ArmPose var4 = getArmPose(var0, var2, InteractionHand.MAIN_HAND);
      HumanoidModel.ArmPose var5 = getArmPose(var0, var3, InteractionHand.OFF_HAND);
      if (var4.isTwoHanded()) {
         var5 = var3.isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
      }

      return var0.getMainArm() == var1 ? var4 : var5;
   }

   private static HumanoidModel.ArmPose getArmPose(Avatar var0, ItemStack var1, InteractionHand var2) {
      if (var1.isEmpty()) {
         return HumanoidModel.ArmPose.EMPTY;
      } else if (!var0.swinging && var1.is(Items.CROSSBOW) && CrossbowItem.isCharged(var1)) {
         return HumanoidModel.ArmPose.CROSSBOW_HOLD;
      } else {
         if (var0.getUsedItemHand() == var2 && var0.getUseItemRemainingTicks() > 0) {
            ItemUseAnimation var3 = var1.getUseAnimation();
            if (var3 == ItemUseAnimation.BLOCK) {
               return HumanoidModel.ArmPose.BLOCK;
            }

            if (var3 == ItemUseAnimation.BOW) {
               return HumanoidModel.ArmPose.BOW_AND_ARROW;
            }

            if (var3 == ItemUseAnimation.TRIDENT) {
               return HumanoidModel.ArmPose.THROW_TRIDENT;
            }

            if (var3 == ItemUseAnimation.CROSSBOW) {
               return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
            }

            if (var3 == ItemUseAnimation.SPYGLASS) {
               return HumanoidModel.ArmPose.SPYGLASS;
            }

            if (var3 == ItemUseAnimation.TOOT_HORN) {
               return HumanoidModel.ArmPose.TOOT_HORN;
            }

            if (var3 == ItemUseAnimation.BRUSH) {
               return HumanoidModel.ArmPose.BRUSH;
            }

            if (var3 == ItemUseAnimation.SPEAR) {
               return HumanoidModel.ArmPose.SPEAR;
            }
         }

         SwingAnimation var4 = (SwingAnimation)var1.get(DataComponents.SWING_ANIMATION);
         if (var4 != null && var4.type() == SwingAnimationType.STAB && var0.swinging) {
            return HumanoidModel.ArmPose.SPEAR;
         } else {
            return var1.is(ItemTags.SPEARS) ? HumanoidModel.ArmPose.SPEAR : HumanoidModel.ArmPose.ITEM;
         }
      }
   }

   public ResourceLocation getTextureLocation(AvatarRenderState var1) {
      return var1.skin.body().texturePath();
   }

   protected void scale(AvatarRenderState var1, PoseStack var2) {
      float var3 = 0.9375F;
      var2.scale(0.9375F, 0.9375F, 0.9375F);
   }

   protected void submitNameTag(AvatarRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      var2.pushPose();
      int var5 = var1.showExtraEars ? -10 : 0;
      if (var1.scoreText != null) {
         var3.submitNameTag(var2, var1.nameTagAttachment, var5, var1.scoreText, !var1.isDiscrete, var1.lightCoords, var1.distanceToCameraSq, var4);
         Objects.requireNonNull(this.getFont());
         var2.translate(0.0F, 9.0F * 1.15F * 0.025F, 0.0F);
      }

      if (var1.nameTag != null) {
         var3.submitNameTag(var2, var1.nameTagAttachment, var5, var1.nameTag, !var1.isDiscrete, var1.lightCoords, var1.distanceToCameraSq, var4);
      }

      var2.popPose();
   }

   public AvatarRenderState createRenderState() {
      return new AvatarRenderState();
   }

   public void extractRenderState(AvatarlikeEntity var1, AvatarRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      HumanoidMobRenderer.extractHumanoidRenderState(var1, var2, var3, this.itemModelResolver);
      var2.leftArmPose = getArmPose(var1, HumanoidArm.LEFT);
      var2.rightArmPose = getArmPose(var1, HumanoidArm.RIGHT);
      var2.skin = ((ClientAvatarEntity)var1).getSkin();
      var2.arrowCount = var1.getArrowCount();
      var2.stingerCount = var1.getStingerCount();
      var2.isSpectator = var1.isSpectator();
      var2.showHat = var1.isModelPartShown(PlayerModelPart.HAT);
      var2.showJacket = var1.isModelPartShown(PlayerModelPart.JACKET);
      var2.showLeftPants = var1.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
      var2.showRightPants = var1.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
      var2.showLeftSleeve = var1.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
      var2.showRightSleeve = var1.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
      var2.showCape = var1.isModelPartShown(PlayerModelPart.CAPE);
      this.extractFlightData(var1, var2, var3);
      this.extractCapeState(var1, var2, var3);
      if (var2.distanceToCameraSq < 100.0) {
         var2.scoreText = ((ClientAvatarEntity)var1).belowNameDisplay();
      } else {
         var2.scoreText = null;
      }

      var2.parrotOnLeftShoulder = ((ClientAvatarEntity)var1).getParrotVariantOnShoulder(true);
      var2.parrotOnRightShoulder = ((ClientAvatarEntity)var1).getParrotVariantOnShoulder(false);
      var2.id = var1.getId();
      var2.showExtraEars = ((ClientAvatarEntity)var1).showExtraEars();
      var2.heldOnHead.clear();
      if (var2.isUsingItem) {
         ItemStack var4 = var1.getItemInHand(var2.useItemHand);
         if (var4.is(Items.SPYGLASS)) {
            this.itemModelResolver.updateForLiving(var2.heldOnHead, var4, ItemDisplayContext.HEAD, var1);
         }
      }

   }

   protected boolean shouldShowName(AvatarlikeEntity var1, double var2) {
      return super.shouldShowName(var1, var2) && (var1.shouldShowName() || var1.hasCustomName() && var1 == this.entityRenderDispatcher.crosshairPickEntity);
   }

   private void extractFlightData(AvatarlikeEntity var1, AvatarRenderState var2, float var3) {
      var2.fallFlyingTimeInTicks = (float)var1.getFallFlyingTicks() + var3;
      Vec3 var4 = var1.getViewVector(var3);
      Vec3 var5 = ((ClientAvatarEntity)var1).avatarState().deltaMovementOnPreviousTick().lerp(var1.getDeltaMovement(), (double)var3);
      if (var5.horizontalDistanceSqr() > 9.999999747378752E-6 && var4.horizontalDistanceSqr() > 9.999999747378752E-6) {
         var2.shouldApplyFlyingYRot = true;
         double var6 = var5.horizontal().normalize().dot(var4.horizontal().normalize());
         double var8 = var5.x * var4.z - var5.z * var4.x;
         var2.flyingYRot = (float)(Math.signum(var8) * Math.acos(Math.min(1.0, Math.abs(var6))));
      } else {
         var2.shouldApplyFlyingYRot = false;
         var2.flyingYRot = 0.0F;
      }

   }

   private void extractCapeState(AvatarlikeEntity var1, AvatarRenderState var2, float var3) {
      ClientAvatarState var4 = ((ClientAvatarEntity)var1).avatarState();
      double var5 = var4.getInterpolatedCloakX(var3) - Mth.lerp((double)var3, var1.xo, var1.getX());
      double var7 = var4.getInterpolatedCloakY(var3) - Mth.lerp((double)var3, var1.yo, var1.getY());
      double var9 = var4.getInterpolatedCloakZ(var3) - Mth.lerp((double)var3, var1.zo, var1.getZ());
      float var11 = Mth.rotLerp(var3, var1.yBodyRotO, var1.yBodyRot);
      double var12 = (double)Mth.sin(var11 * 0.017453292F);
      double var14 = (double)(-Mth.cos(var11 * 0.017453292F));
      var2.capeFlap = (float)var7 * 10.0F;
      var2.capeFlap = Mth.clamp(var2.capeFlap, -6.0F, 32.0F);
      var2.capeLean = (float)(var5 * var12 + var9 * var14) * 100.0F;
      var2.capeLean *= 1.0F - var2.fallFlyingScale();
      var2.capeLean = Mth.clamp(var2.capeLean, 0.0F, 150.0F);
      var2.capeLean2 = (float)(var5 * var14 - var9 * var12) * 100.0F;
      var2.capeLean2 = Mth.clamp(var2.capeLean2, -20.0F, 20.0F);
      float var16 = var4.getInterpolatedBob(var3);
      float var17 = var4.getInterpolatedWalkDistance(var3);
      var2.capeFlap += Mth.sin(var17 * 6.0F) * 32.0F * var16;
   }

   public void renderRightHand(PoseStack var1, SubmitNodeCollector var2, int var3, ResourceLocation var4, boolean var5) {
      this.renderHand(var1, var2, var3, var4, (this.model).rightArm, var5);
   }

   public void renderLeftHand(PoseStack var1, SubmitNodeCollector var2, int var3, ResourceLocation var4, boolean var5) {
      this.renderHand(var1, var2, var3, var4, (this.model).leftArm, var5);
   }

   private void renderHand(PoseStack var1, SubmitNodeCollector var2, int var3, ResourceLocation var4, ModelPart var5, boolean var6) {
      PlayerModel var7 = (PlayerModel)this.getModel();
      var5.resetPose();
      var5.visible = true;
      var7.leftSleeve.visible = var6;
      var7.rightSleeve.visible = var6;
      var7.leftArm.zRot = -0.1F;
      var7.rightArm.zRot = 0.1F;
      var2.submitModelPart(var5, var1, RenderTypes.entityTranslucent(var4), var3, OverlayTexture.NO_OVERLAY, (TextureAtlasSprite)null);
   }

   protected void setupRotations(AvatarRenderState var1, PoseStack var2, float var3, float var4) {
      float var5 = var1.swimAmount;
      float var6 = var1.xRot;
      if (var1.isFallFlying) {
         super.setupRotations(var1, var2, var3, var4);
         float var7 = var1.fallFlyingScale();
         if (!var1.isAutoSpinAttack) {
            var2.mulPose((Quaternionfc)Axis.XP.rotationDegrees(var7 * (-90.0F - var6)));
         }

         if (var1.shouldApplyFlyingYRot) {
            var2.mulPose((Quaternionfc)Axis.YP.rotation(var1.flyingYRot));
         }
      } else if (var5 > 0.0F) {
         super.setupRotations(var1, var2, var3, var4);
         float var9 = var1.isInWater ? -90.0F - var6 : -90.0F;
         float var8 = Mth.lerp(var5, 0.0F, var9);
         var2.mulPose((Quaternionfc)Axis.XP.rotationDegrees(var8));
         if (var1.isVisuallySwimming) {
            var2.translate(0.0F, -1.0F, 0.3F);
         }
      } else {
         super.setupRotations(var1, var2, var3, var4);
      }

   }

   public boolean isEntityUpsideDown(AvatarlikeEntity var1) {
      if (var1.isModelPartShown(PlayerModelPart.CAPE)) {
         if (var1 instanceof Player) {
            Player var2 = (Player)var1;
            return isPlayerUpsideDown(var2);
         } else {
            return super.isEntityUpsideDown(var1);
         }
      } else {
         return false;
      }
   }

   public static boolean isPlayerUpsideDown(Player var0) {
      return isUpsideDownName(var0.getGameProfile().name());
   }

   // $FF: synthetic method
   public boolean isEntityUpsideDown(final LivingEntity var1) {
      return this.isEntityUpsideDown((Avatar)var1);
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((AvatarRenderState)var1);
   }

   // $FF: synthetic method
   protected boolean shouldRenderLayers(final LivingEntityRenderState var1) {
      return this.shouldRenderLayers((AvatarRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   // $FF: synthetic method
   protected void submitNameTag(final EntityRenderState var1, final PoseStack var2, final SubmitNodeCollector var3, final CameraRenderState var4) {
      this.submitNameTag((AvatarRenderState)var1, var2, var3, var4);
   }

   // $FF: synthetic method
   public Vec3 getRenderOffset(final EntityRenderState var1) {
      return this.getRenderOffset((AvatarRenderState)var1);
   }
}
