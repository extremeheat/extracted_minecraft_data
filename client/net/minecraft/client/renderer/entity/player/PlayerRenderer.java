package net.minecraft.client.renderer.entity.player;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5EarsLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class PlayerRenderer extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
   public PlayerRenderer(EntityRenderDispatcher var1) {
      this(var1, false);
   }

   public PlayerRenderer(EntityRenderDispatcher var1, boolean var2) {
      super(var1, new PlayerModel(0.0F, var2), 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(0.5F), new HumanoidModel(1.0F)));
      this.addLayer(new ItemInHandLayer(this));
      this.addLayer(new ArrowLayer(this));
      this.addLayer(new Deadmau5EarsLayer(this));
      this.addLayer(new CapeLayer(this));
      this.addLayer(new CustomHeadLayer(this));
      this.addLayer(new ElytraLayer(this));
      this.addLayer(new ParrotOnShoulderLayer(this));
      this.addLayer(new SpinAttackEffectLayer(this));
   }

   public void render(AbstractClientPlayer var1, double var2, double var4, double var6, float var8, float var9) {
      if (!var1.isLocalPlayer() || this.entityRenderDispatcher.camera.getEntity() == var1) {
         double var10 = var4;
         if (var1.isVisuallySneaking()) {
            var10 = var4 - 0.125D;
         }

         this.setModelProperties(var1);
         GlStateManager.setProfile(GlStateManager.Profile.PLAYER_SKIN);
         super.render((LivingEntity)var1, var2, var10, var6, var8, var9);
         GlStateManager.unsetProfile(GlStateManager.Profile.PLAYER_SKIN);
      }
   }

   private void setModelProperties(AbstractClientPlayer var1) {
      PlayerModel var2 = (PlayerModel)this.getModel();
      if (var1.isSpectator()) {
         var2.setAllVisible(false);
         var2.head.visible = true;
         var2.hat.visible = true;
      } else {
         ItemStack var3 = var1.getMainHandItem();
         ItemStack var4 = var1.getOffhandItem();
         var2.setAllVisible(true);
         var2.hat.visible = var1.isModelPartShown(PlayerModelPart.HAT);
         var2.jacket.visible = var1.isModelPartShown(PlayerModelPart.JACKET);
         var2.leftPants.visible = var1.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
         var2.rightPants.visible = var1.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
         var2.leftSleeve.visible = var1.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
         var2.rightSleeve.visible = var1.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
         var2.sneaking = var1.isVisuallySneaking();
         HumanoidModel.ArmPose var5 = this.getArmPose(var1, var3, var4, InteractionHand.MAIN_HAND);
         HumanoidModel.ArmPose var6 = this.getArmPose(var1, var3, var4, InteractionHand.OFF_HAND);
         if (var1.getMainArm() == HumanoidArm.RIGHT) {
            var2.rightArmPose = var5;
            var2.leftArmPose = var6;
         } else {
            var2.rightArmPose = var6;
            var2.leftArmPose = var5;
         }
      }

   }

   private HumanoidModel.ArmPose getArmPose(AbstractClientPlayer var1, ItemStack var2, ItemStack var3, InteractionHand var4) {
      HumanoidModel.ArmPose var5 = HumanoidModel.ArmPose.EMPTY;
      ItemStack var6 = var4 == InteractionHand.MAIN_HAND ? var2 : var3;
      if (!var6.isEmpty()) {
         var5 = HumanoidModel.ArmPose.ITEM;
         if (var1.getUseItemRemainingTicks() > 0) {
            UseAnim var7 = var6.getUseAnimation();
            if (var7 == UseAnim.BLOCK) {
               var5 = HumanoidModel.ArmPose.BLOCK;
            } else if (var7 == UseAnim.BOW) {
               var5 = HumanoidModel.ArmPose.BOW_AND_ARROW;
            } else if (var7 == UseAnim.SPEAR) {
               var5 = HumanoidModel.ArmPose.THROW_SPEAR;
            } else if (var7 == UseAnim.CROSSBOW && var4 == var1.getUsedItemHand()) {
               var5 = HumanoidModel.ArmPose.CROSSBOW_CHARGE;
            }
         } else {
            boolean var11 = var2.getItem() == Items.CROSSBOW;
            boolean var8 = CrossbowItem.isCharged(var2);
            boolean var9 = var3.getItem() == Items.CROSSBOW;
            boolean var10 = CrossbowItem.isCharged(var3);
            if (var11 && var8) {
               var5 = HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            if (var9 && var10 && var2.getItem().getUseAnimation(var2) == UseAnim.NONE) {
               var5 = HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }
         }
      }

      return var5;
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer var1) {
      return var1.getSkinTextureLocation();
   }

   protected void scale(AbstractClientPlayer var1, float var2) {
      float var3 = 0.9375F;
      GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
   }

   protected void renderNameTags(AbstractClientPlayer var1, double var2, double var4, double var6, String var8, double var9) {
      if (var9 < 100.0D) {
         Scoreboard var11 = var1.getScoreboard();
         Objective var12 = var11.getDisplayObjective(2);
         if (var12 != null) {
            Score var13 = var11.getOrCreatePlayerScore(var1.getScoreboardName(), var12);
            this.renderNameTag(var1, var13.getScore() + " " + var12.getDisplayName().getColoredString(), var2, var4, var6, 64);
            this.getFont().getClass();
            var4 += (double)(9.0F * 1.15F * 0.025F);
         }
      }

      super.renderNameTags(var1, var2, var4, var6, var8, var9);
   }

   public void renderRightHand(AbstractClientPlayer var1) {
      float var2 = 1.0F;
      GlStateManager.color3f(1.0F, 1.0F, 1.0F);
      float var3 = 0.0625F;
      PlayerModel var4 = (PlayerModel)this.getModel();
      this.setModelProperties(var1);
      GlStateManager.enableBlend();
      var4.attackTime = 0.0F;
      var4.sneaking = false;
      var4.swimAmount = 0.0F;
      var4.setupAnim((LivingEntity)var1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      var4.rightArm.xRot = 0.0F;
      var4.rightArm.render(0.0625F);
      var4.rightSleeve.xRot = 0.0F;
      var4.rightSleeve.render(0.0625F);
      GlStateManager.disableBlend();
   }

   public void renderLeftHand(AbstractClientPlayer var1) {
      float var2 = 1.0F;
      GlStateManager.color3f(1.0F, 1.0F, 1.0F);
      float var3 = 0.0625F;
      PlayerModel var4 = (PlayerModel)this.getModel();
      this.setModelProperties(var1);
      GlStateManager.enableBlend();
      var4.sneaking = false;
      var4.attackTime = 0.0F;
      var4.swimAmount = 0.0F;
      var4.setupAnim((LivingEntity)var1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      var4.leftArm.xRot = 0.0F;
      var4.leftArm.render(0.0625F);
      var4.leftSleeve.xRot = 0.0F;
      var4.leftSleeve.render(0.0625F);
      GlStateManager.disableBlend();
   }

   protected void setupRotations(AbstractClientPlayer var1, float var2, float var3, float var4) {
      float var5 = var1.getSwimAmount(var4);
      float var6;
      float var7;
      if (var1.isFallFlying()) {
         super.setupRotations(var1, var2, var3, var4);
         var6 = (float)var1.getFallFlyingTicks() + var4;
         var7 = Mth.clamp(var6 * var6 / 100.0F, 0.0F, 1.0F);
         if (!var1.isAutoSpinAttack()) {
            GlStateManager.rotatef(var7 * (-90.0F - var1.xRot), 1.0F, 0.0F, 0.0F);
         }

         Vec3 var8 = var1.getViewVector(var4);
         Vec3 var9 = var1.getDeltaMovement();
         double var10 = Entity.getHorizontalDistanceSqr(var9);
         double var12 = Entity.getHorizontalDistanceSqr(var8);
         if (var10 > 0.0D && var12 > 0.0D) {
            double var14 = (var9.x * var8.x + var9.z * var8.z) / (Math.sqrt(var10) * Math.sqrt(var12));
            double var16 = var9.x * var8.z - var9.z * var8.x;
            GlStateManager.rotatef((float)(Math.signum(var16) * Math.acos(var14)) * 180.0F / 3.1415927F, 0.0F, 1.0F, 0.0F);
         }
      } else if (var5 > 0.0F) {
         super.setupRotations(var1, var2, var3, var4);
         var6 = var1.isInWater() ? -90.0F - var1.xRot : -90.0F;
         var7 = Mth.lerp(var5, 0.0F, var6);
         GlStateManager.rotatef(var7, 1.0F, 0.0F, 0.0F);
         if (var1.isVisuallySwimming()) {
            GlStateManager.translatef(0.0F, -1.0F, 0.3F);
         }
      } else {
         super.setupRotations(var1, var2, var3, var4);
      }

   }
}
