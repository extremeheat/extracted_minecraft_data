package net.minecraft.client.renderer.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Scoreboard;

public class PlayerRenderer extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> {
   public PlayerRenderer(EntityRendererProvider.Context var1, boolean var2) {
      super(var1, new PlayerModel(var1.bakeLayer(var2 ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), var2), 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, new HumanoidArmorModel(var1.bakeLayer(var2 ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel(var1.bakeLayer(var2 ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)), var1.getEquipmentRenderer()));
      this.addLayer(new PlayerItemInHandLayer(this, var1.getItemRenderer()));
      this.addLayer(new ArrowLayer(this, var1));
      this.addLayer(new Deadmau5EarsLayer(this, var1.getModelSet()));
      this.addLayer(new CapeLayer(this, var1.getModelSet(), var1.getEquipmentModels()));
      this.addLayer(new CustomHeadLayer(this, var1.getModelSet(), var1.getItemRenderer()));
      this.addLayer(new WingsLayer(this, var1.getModelSet(), var1.getEquipmentRenderer()));
      this.addLayer(new ParrotOnShoulderLayer(this, var1.getModelSet()));
      this.addLayer(new SpinAttackEffectLayer(this, var1.getModelSet()));
      this.addLayer(new BeeStingerLayer(this, var1));
   }

   protected boolean shouldRenderLayers(PlayerRenderState var1) {
      return !var1.isSpectator;
   }

   public Vec3 getRenderOffset(PlayerRenderState var1) {
      Vec3 var2 = super.getRenderOffset(var1);
      return var1.isCrouching ? var2.add(0.0, (double)(var1.scale * -2.0F) / 16.0, 0.0) : var2;
   }

   public static HumanoidModel.ArmPose getArmPose(PlayerRenderState var0, HumanoidArm var1) {
      HumanoidModel.ArmPose var2 = getArmPose(var0, var0.mainHandState, InteractionHand.MAIN_HAND);
      HumanoidModel.ArmPose var3 = getArmPose(var0, var0.offhandState, InteractionHand.OFF_HAND);
      if (var2.isTwoHanded()) {
         var3 = var0.offhandState.isEmpty ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
      }

      return var0.mainArm == var1 ? var2 : var3;
   }

   private static HumanoidModel.ArmPose getArmPose(PlayerRenderState var0, PlayerRenderState.HandState var1, InteractionHand var2) {
      if (var1.isEmpty) {
         return HumanoidModel.ArmPose.EMPTY;
      } else {
         if (var0.useItemHand == var2 && var0.useItemRemainingTicks > 0) {
            ItemUseAnimation var3 = var1.useAnimation;
            if (var3 == ItemUseAnimation.BLOCK) {
               return HumanoidModel.ArmPose.BLOCK;
            }

            if (var3 == ItemUseAnimation.BOW) {
               return HumanoidModel.ArmPose.BOW_AND_ARROW;
            }

            if (var3 == ItemUseAnimation.SPEAR) {
               return HumanoidModel.ArmPose.THROW_SPEAR;
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
         } else if (!var0.swinging && var1.holdsChargedCrossbow) {
            return HumanoidModel.ArmPose.CROSSBOW_HOLD;
         }

         return HumanoidModel.ArmPose.ITEM;
      }
   }

   public ResourceLocation getTextureLocation(PlayerRenderState var1) {
      return var1.skin.texture();
   }

   protected void scale(PlayerRenderState var1, PoseStack var2) {
      float var3 = 0.9375F;
      var2.scale(0.9375F, 0.9375F, 0.9375F);
   }

   protected void renderNameTag(PlayerRenderState var1, Component var2, PoseStack var3, MultiBufferSource var4, int var5) {
      var3.pushPose();
      if (var1.scoreText != null) {
         super.renderNameTag(var1, var1.scoreText, var3, var4, var5);
         Objects.requireNonNull(this.getFont());
         var3.translate(0.0F, 9.0F * 1.15F * 0.025F, 0.0F);
      }

      super.renderNameTag(var1, var2, var3, var4, var5);
      var3.popPose();
   }

   public PlayerRenderState createRenderState() {
      return new PlayerRenderState();
   }

   public void extractRenderState(AbstractClientPlayer var1, PlayerRenderState var2, float var3) {
      super.extractRenderState((LivingEntity)var1, (LivingEntityRenderState)var2, var3);
      HumanoidMobRenderer.extractHumanoidRenderState(var1, var2, var3);
      var2.skin = var1.getSkin();
      var2.arrowCount = var1.getArrowCount();
      var2.stingerCount = var1.getStingerCount();
      var2.useItemRemainingTicks = var1.getUseItemRemainingTicks();
      var2.swinging = var1.swinging;
      var2.isSpectator = var1.isSpectator();
      var2.showHat = var1.isModelPartShown(PlayerModelPart.HAT);
      var2.showJacket = var1.isModelPartShown(PlayerModelPart.JACKET);
      var2.showLeftPants = var1.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
      var2.showRightPants = var1.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
      var2.showLeftSleeve = var1.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
      var2.showRightSleeve = var1.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
      var2.showCape = var1.isModelPartShown(PlayerModelPart.CAPE);
      extractFlightData(var1, var2, var3);
      this.extractHandState(var1, var2.mainHandState, InteractionHand.MAIN_HAND);
      this.extractHandState(var1, var2.offhandState, InteractionHand.OFF_HAND);
      extractCapeState(var1, var2, var3);
      if (var2.distanceToCameraSq < 100.0) {
         Scoreboard var4 = var1.getScoreboard();
         Objective var5 = var4.getDisplayObjective(DisplaySlot.BELOW_NAME);
         if (var5 != null) {
            ReadOnlyScoreInfo var6 = var4.getPlayerScoreInfo(var1, var5);
            MutableComponent var7 = ReadOnlyScoreInfo.safeFormatValue(var6, var5.numberFormatOrDefault(StyledFormat.NO_STYLE));
            var2.scoreText = Component.empty().append((Component)var7).append(CommonComponents.SPACE).append(var5.getDisplayName());
         } else {
            var2.scoreText = null;
         }
      } else {
         var2.scoreText = null;
      }

      var2.parrotOnLeftShoulder = getParrotOnShoulder(var1, true);
      var2.parrotOnRightShoulder = getParrotOnShoulder(var1, false);
      var2.id = var1.getId();
      var2.name = var1.getGameProfile().getName();
   }

   private static void extractFlightData(AbstractClientPlayer var0, PlayerRenderState var1, float var2) {
      var1.fallFlyingTimeInTicks = (float)var0.getFallFlyingTicks() + var2;
      Vec3 var3 = var0.getViewVector(var2);
      Vec3 var4 = var0.getDeltaMovementLerped(var2);
      double var5 = var4.horizontalDistanceSqr();
      double var7 = var3.horizontalDistanceSqr();
      if (var5 > 0.0 && var7 > 0.0) {
         var1.shouldApplyFlyingYRot = true;
         double var9 = Math.min(1.0, (var4.x * var3.x + var4.z * var3.z) / Math.sqrt(var5 * var7));
         double var11 = var4.x * var3.z - var4.z * var3.x;
         var1.flyingYRot = (float)(Math.signum(var11) * Math.acos(var9));
      } else {
         var1.shouldApplyFlyingYRot = false;
         var1.flyingYRot = 0.0F;
      }

   }

   private void extractHandState(AbstractClientPlayer var1, PlayerRenderState.HandState var2, InteractionHand var3) {
      ItemStack var4 = var1.getItemInHand(var3);
      var2.isEmpty = var4.isEmpty();
      var2.useAnimation = !var4.isEmpty() ? var4.getUseAnimation() : null;
      var2.holdsChargedCrossbow = var4.is(Items.CROSSBOW) && CrossbowItem.isCharged(var4);
   }

   private static void extractCapeState(AbstractClientPlayer var0, PlayerRenderState var1, float var2) {
      double var3 = Mth.lerp((double)var2, var0.xCloakO, var0.xCloak) - Mth.lerp((double)var2, var0.xo, var0.getX());
      double var5 = Mth.lerp((double)var2, var0.yCloakO, var0.yCloak) - Mth.lerp((double)var2, var0.yo, var0.getY());
      double var7 = Mth.lerp((double)var2, var0.zCloakO, var0.zCloak) - Mth.lerp((double)var2, var0.zo, var0.getZ());
      float var9 = Mth.rotLerp(var2, var0.yBodyRotO, var0.yBodyRot);
      double var10 = (double)Mth.sin(var9 * 0.017453292F);
      double var12 = (double)(-Mth.cos(var9 * 0.017453292F));
      var1.capeFlap = (float)var5 * 10.0F;
      var1.capeFlap = Mth.clamp(var1.capeFlap, -6.0F, 32.0F);
      var1.capeLean = (float)(var3 * var10 + var7 * var12) * 100.0F;
      var1.capeLean *= 1.0F - var1.fallFlyingScale();
      var1.capeLean = Mth.clamp(var1.capeLean, 0.0F, 150.0F);
      var1.capeLean2 = (float)(var3 * var12 - var7 * var10) * 100.0F;
      var1.capeLean2 = Mth.clamp(var1.capeLean2, -20.0F, 20.0F);
      float var14 = Mth.lerp(var2, var0.oBob, var0.bob);
      float var15 = Mth.lerp(var2, var0.walkDistO, var0.walkDist);
      var1.capeFlap += Mth.sin(var15 * 6.0F) * 32.0F * var14;
   }

   @Nullable
   private static Parrot.Variant getParrotOnShoulder(AbstractClientPlayer var0, boolean var1) {
      CompoundTag var2 = var1 ? var0.getShoulderEntityLeft() : var0.getShoulderEntityRight();
      return EntityType.byString(var2.getString("id")).filter((var0x) -> {
         return var0x == EntityType.PARROT;
      }).isPresent() ? Parrot.Variant.byId(var2.getInt("Variant")) : null;
   }

   public void renderRightHand(PoseStack var1, MultiBufferSource var2, int var3, ResourceLocation var4, boolean var5) {
      this.renderHand(var1, var2, var3, var4, ((PlayerModel)this.model).rightArm, var5);
   }

   public void renderLeftHand(PoseStack var1, MultiBufferSource var2, int var3, ResourceLocation var4, boolean var5) {
      this.renderHand(var1, var2, var3, var4, ((PlayerModel)this.model).leftArm, var5);
   }

   private void renderHand(PoseStack var1, MultiBufferSource var2, int var3, ResourceLocation var4, ModelPart var5, boolean var6) {
      PlayerModel var7 = (PlayerModel)this.getModel();
      var5.resetPose();
      var5.visible = true;
      var7.leftSleeve.visible = var6;
      var7.rightSleeve.visible = var6;
      var7.leftArm.zRot = -0.1F;
      var7.rightArm.zRot = 0.1F;
      var5.render(var1, var2.getBuffer(RenderType.entityTranslucent(var4)), var3, OverlayTexture.NO_OVERLAY);
   }

   protected void setupRotations(PlayerRenderState var1, PoseStack var2, float var3, float var4) {
      float var5 = var1.swimAmount;
      float var6 = var1.xRot;
      float var7;
      if (var1.isFallFlying) {
         super.setupRotations(var1, var2, var3, var4);
         var7 = var1.fallFlyingScale();
         if (!var1.isAutoSpinAttack) {
            var2.mulPose(Axis.XP.rotationDegrees(var7 * (-90.0F - var6)));
         }

         if (var1.shouldApplyFlyingYRot) {
            var2.mulPose(Axis.YP.rotation(var1.flyingYRot));
         }
      } else if (var5 > 0.0F) {
         super.setupRotations(var1, var2, var3, var4);
         var7 = var1.isInWater ? -90.0F - var6 : -90.0F;
         float var8 = Mth.lerp(var5, 0.0F, var7);
         var2.mulPose(Axis.XP.rotationDegrees(var8));
         if (var1.isVisuallySwimming) {
            var2.translate(0.0F, -1.0F, 0.3F);
         }
      } else {
         super.setupRotations(var1, var2, var3, var4);
      }

   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((PlayerRenderState)var1);
   }

   // $FF: synthetic method
   protected boolean shouldRenderLayers(final LivingEntityRenderState var1) {
      return this.shouldRenderLayers((PlayerRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   // $FF: synthetic method
   public Vec3 getRenderOffset(final EntityRenderState var1) {
      return this.getRenderOffset((PlayerRenderState)var1);
   }
}
