package net.minecraft.client.renderer.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Objects;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5EarsLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Scoreboard;

public class PlayerRenderer extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
   public PlayerRenderer(EntityRendererProvider.Context var1, boolean var2) {
      super(var1, new PlayerModel(var1.bakeLayer(var2 ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), var2), 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, new HumanoidArmorModel(var1.bakeLayer(var2 ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel(var1.bakeLayer(var2 ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)), var1.getModelManager()));
      this.addLayer(new PlayerItemInHandLayer(this, var1.getItemInHandRenderer()));
      this.addLayer(new ArrowLayer(var1, this));
      this.addLayer(new Deadmau5EarsLayer(this));
      this.addLayer(new CapeLayer(this));
      this.addLayer(new CustomHeadLayer(this, var1.getModelSet(), var1.getItemInHandRenderer()));
      this.addLayer(new ElytraLayer(this, var1.getModelSet()));
      this.addLayer(new ParrotOnShoulderLayer(this, var1.getModelSet()));
      this.addLayer(new SpinAttackEffectLayer(this, var1.getModelSet()));
      this.addLayer(new BeeStingerLayer(this));
   }

   public void render(AbstractClientPlayer var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      this.setModelProperties(var1);
      super.render((LivingEntity)var1, var2, var3, var4, var5, var6);
   }

   public Vec3 getRenderOffset(AbstractClientPlayer var1, float var2) {
      return var1.isCrouching() ? new Vec3(0.0, (double)(var1.getScale() * -2.0F) / 16.0, 0.0) : super.getRenderOffset(var1, var2);
   }

   private void setModelProperties(AbstractClientPlayer var1) {
      PlayerModel var2 = (PlayerModel)this.getModel();
      if (var1.isSpectator()) {
         var2.setAllVisible(false);
         var2.head.visible = true;
         var2.hat.visible = true;
      } else {
         var2.setAllVisible(true);
         var2.hat.visible = var1.isModelPartShown(PlayerModelPart.HAT);
         var2.jacket.visible = var1.isModelPartShown(PlayerModelPart.JACKET);
         var2.leftPants.visible = var1.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
         var2.rightPants.visible = var1.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
         var2.leftSleeve.visible = var1.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
         var2.rightSleeve.visible = var1.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
         var2.crouching = var1.isCrouching();
         HumanoidModel.ArmPose var3 = getArmPose(var1, InteractionHand.MAIN_HAND);
         HumanoidModel.ArmPose var4 = getArmPose(var1, InteractionHand.OFF_HAND);
         if (var3.isTwoHanded()) {
            var4 = var1.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
         }

         if (var1.getMainArm() == HumanoidArm.RIGHT) {
            var2.rightArmPose = var3;
            var2.leftArmPose = var4;
         } else {
            var2.rightArmPose = var4;
            var2.leftArmPose = var3;
         }
      }

   }

   private static HumanoidModel.ArmPose getArmPose(AbstractClientPlayer var0, InteractionHand var1) {
      ItemStack var2 = var0.getItemInHand(var1);
      if (var2.isEmpty()) {
         return HumanoidModel.ArmPose.EMPTY;
      } else {
         if (var0.getUsedItemHand() == var1 && var0.getUseItemRemainingTicks() > 0) {
            UseAnim var3 = var2.getUseAnimation();
            if (var3 == UseAnim.BLOCK) {
               return HumanoidModel.ArmPose.BLOCK;
            }

            if (var3 == UseAnim.BOW) {
               return HumanoidModel.ArmPose.BOW_AND_ARROW;
            }

            if (var3 == UseAnim.SPEAR) {
               return HumanoidModel.ArmPose.THROW_SPEAR;
            }

            if (var3 == UseAnim.CROSSBOW && var1 == var0.getUsedItemHand()) {
               return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
            }

            if (var3 == UseAnim.SPYGLASS) {
               return HumanoidModel.ArmPose.SPYGLASS;
            }

            if (var3 == UseAnim.TOOT_HORN) {
               return HumanoidModel.ArmPose.TOOT_HORN;
            }

            if (var3 == UseAnim.BRUSH) {
               return HumanoidModel.ArmPose.BRUSH;
            }
         } else if (!var0.swinging && var2.is(Items.CROSSBOW) && CrossbowItem.isCharged(var2)) {
            return HumanoidModel.ArmPose.CROSSBOW_HOLD;
         }

         return HumanoidModel.ArmPose.ITEM;
      }
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer var1) {
      return var1.getSkin().texture();
   }

   protected void scale(AbstractClientPlayer var1, PoseStack var2, float var3) {
      float var4 = 0.9375F;
      var2.scale(0.9375F, 0.9375F, 0.9375F);
   }

   protected void renderNameTag(AbstractClientPlayer var1, Component var2, PoseStack var3, MultiBufferSource var4, int var5, float var6) {
      double var7 = this.entityRenderDispatcher.distanceToSqr(var1);
      var3.pushPose();
      if (var7 < 100.0) {
         Scoreboard var9 = var1.getScoreboard();
         Objective var10 = var9.getDisplayObjective(DisplaySlot.BELOW_NAME);
         if (var10 != null) {
            ReadOnlyScoreInfo var11 = var9.getPlayerScoreInfo(var1, var10);
            MutableComponent var12 = ReadOnlyScoreInfo.safeFormatValue(var11, var10.numberFormatOrDefault(StyledFormat.NO_STYLE));
            super.renderNameTag(var1, Component.empty().append((Component)var12).append(CommonComponents.SPACE).append(var10.getDisplayName()), var3, var4, var5, var6);
            Objects.requireNonNull(this.getFont());
            var3.translate(0.0F, 9.0F * 1.15F * 0.025F, 0.0F);
         }
      }

      super.renderNameTag(var1, var2, var3, var4, var5, var6);
      var3.popPose();
   }

   public void renderRightHand(PoseStack var1, MultiBufferSource var2, int var3, AbstractClientPlayer var4) {
      this.renderHand(var1, var2, var3, var4, ((PlayerModel)this.model).rightArm, ((PlayerModel)this.model).rightSleeve);
   }

   public void renderLeftHand(PoseStack var1, MultiBufferSource var2, int var3, AbstractClientPlayer var4) {
      this.renderHand(var1, var2, var3, var4, ((PlayerModel)this.model).leftArm, ((PlayerModel)this.model).leftSleeve);
   }

   private void renderHand(PoseStack var1, MultiBufferSource var2, int var3, AbstractClientPlayer var4, ModelPart var5, ModelPart var6) {
      PlayerModel var7 = (PlayerModel)this.getModel();
      this.setModelProperties(var4);
      var7.attackTime = 0.0F;
      var7.crouching = false;
      var7.swimAmount = 0.0F;
      var7.setupAnim((LivingEntity)var4, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      var5.xRot = 0.0F;
      ResourceLocation var8 = var4.getSkin().texture();
      var5.render(var1, var2.getBuffer(RenderType.entitySolid(var8)), var3, OverlayTexture.NO_OVERLAY);
      var6.xRot = 0.0F;
      var6.render(var1, var2.getBuffer(RenderType.entityTranslucent(var8)), var3, OverlayTexture.NO_OVERLAY);
   }

   protected void setupRotations(AbstractClientPlayer var1, PoseStack var2, float var3, float var4, float var5, float var6) {
      float var7 = var1.getSwimAmount(var5);
      float var8 = var1.getViewXRot(var5);
      float var9;
      float var10;
      if (var1.isFallFlying()) {
         super.setupRotations(var1, var2, var3, var4, var5, var6);
         var9 = (float)var1.getFallFlyingTicks() + var5;
         var10 = Mth.clamp(var9 * var9 / 100.0F, 0.0F, 1.0F);
         if (!var1.isAutoSpinAttack()) {
            var2.mulPose(Axis.XP.rotationDegrees(var10 * (-90.0F - var8)));
         }

         Vec3 var11 = var1.getViewVector(var5);
         Vec3 var12 = var1.getDeltaMovementLerped(var5);
         double var13 = var12.horizontalDistanceSqr();
         double var15 = var11.horizontalDistanceSqr();
         if (var13 > 0.0 && var15 > 0.0) {
            double var17 = (var12.x * var11.x + var12.z * var11.z) / Math.sqrt(var13 * var15);
            double var19 = var12.x * var11.z - var12.z * var11.x;
            var2.mulPose(Axis.YP.rotation((float)(Math.signum(var19) * Math.acos(var17))));
         }
      } else if (var7 > 0.0F) {
         super.setupRotations(var1, var2, var3, var4, var5, var6);
         var9 = var1.isInWater() ? -90.0F - var8 : -90.0F;
         var10 = Mth.lerp(var7, 0.0F, var9);
         var2.mulPose(Axis.XP.rotationDegrees(var10));
         if (var1.isVisuallySwimming()) {
            var2.translate(0.0F, -1.0F, 0.3F);
         }
      } else {
         super.setupRotations(var1, var2, var3, var4, var5, var6);
      }

   }
}
